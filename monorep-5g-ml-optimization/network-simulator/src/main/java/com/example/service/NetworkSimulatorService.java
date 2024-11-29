package com.example.service;

import org.springframework.stereotype.Service;
import com.example.model.NetworkMetrics;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class NetworkSimulatorService {

    private Random random = new Random();
    private double previousPrbUtilization = 0.5;
    private static final double CELL_RADIUS = 1.0;
    private static final double TOTAL_AVAILABLE_BANDWIDTH = 20;
    private static final double TOTAL_AVAILABLE_PRBs = 100;

    public NetworkMetrics generateMetrics(SessionManager.UserSession userSession) {
        NetworkMetrics metrics = new NetworkMetrics();

        metrics.setSessionId(userSession.getSessionId());

        double allocatedBandwidth = userSession.getAllocatedBandwidth();
        double allocatedPrbs = userSession.getAllocatedPRBs();
        metrics.setAllocatedBandwidth(allocatedBandwidth);
        metrics.setAllocatedPrbs(allocatedPrbs);

        int hourOfDay = LocalDateTime.now().getHour();
        double networkLoadFactor = (hourOfDay >= 18 && hourOfDay <= 22) ? 0.8 : 0.3;

        double userDistance = 0.1 + random.nextDouble() * 0.9;
        double ueSpeed;
        double randomValue = random.nextDouble();

        if (randomValue < 0.4) {
            ueSpeed = random.nextDouble();
        } else if (randomValue < 0.7) {
            ueSpeed = 1 + random.nextDouble() * 4;
        } else {
            ueSpeed = 5 + random.nextDouble() * 115;
        }

        String mobilityPattern;
        if (ueSpeed < 1) {
            mobilityPattern = "Stationary";
        } else if (ueSpeed < 5) {
            mobilityPattern = "Walking";
        } else {
            mobilityPattern = "Driving";
        }

        String[] userActivities = {"Streaming", "WebBrowsing", "Gaming", "VoIP"};
        String userActivity = userActivities[random.nextInt(userActivities.length)];

        double p_tx = 43;
        double pathLoss = 128.1 + 37.6 * Math.log10(userDistance);
        double weatherImpact = random.nextDouble() * 3;
        double shadowing = random.nextGaussian() * 8;
        double rsrp = p_tx - pathLoss - weatherImpact + shadowing;

        double interferenceLevel_dBm = -100 + random.nextDouble() * 10;
        double noiseFigure = 5;
        double bandwidthHz = allocatedBandwidth * 1e6;
        double thermalNoise_dBm = -174 + 10 * Math.log10(bandwidthHz) + noiseFigure;

        double rsrp_mW = Math.pow(10, rsrp / 10.0);
        double interferencePower_mW = Math.pow(10, interferenceLevel_dBm / 10.0);
        double thermalNoise_mW = Math.pow(10, thermalNoise_dBm / 10.0);

        double totalNoise_mW = interferencePower_mW + thermalNoise_mW;

        double snr_linear = rsrp_mW / totalNoise_mW;

        double snr = 10 * Math.log10(snr_linear);

        snr = Math.max(-10, Math.min(30, snr));
        rsrp = Math.max(-120, Math.min(-60, rsrp));

        int cqi = calculateCqi(snr);

        MCSInfo mcsInfo = getMCSInfo(cqi);
        int mcs = mcsInfo.modulationOrder;

        previousPrbUtilization += random.nextGaussian() * 0.05;
        double prbUtilization = Math.max(0, Math.min(1, previousPrbUtilization));
        previousPrbUtilization = prbUtilization;

        boolean handoverEvent;
        if (mobilityPattern.equals("Driving")) {
            handoverEvent = true;
        } else if (mobilityPattern.equals("Walking")) {
            handoverEvent = random.nextDouble() < 0.1;
        } else {
            handoverEvent = false;
        }

        allocatedBandwidth = Math.max(0, Math.min(allocatedBandwidth, TOTAL_AVAILABLE_BANDWIDTH));
        allocatedPrbs = Math.max(0, Math.min(allocatedPrbs, TOTAL_AVAILABLE_PRBs));

        double bler = calculateBler(snr, prbUtilization, mobilityPattern, userActivity);

        double throughput = calculateThroughput(mcsInfo, prbUtilization, snr, bler, userActivity, allocatedBandwidth, allocatedPrbs);
        double latency = calculateLatency(prbUtilization, bler, handoverEvent, mobilityPattern, userActivity, allocatedPrbs);

        throughput = Math.max(0, throughput);
        latency = Math.min(latency, 1000);

        double N = TOTAL_AVAILABLE_PRBs;
        double RSSI_mW = rsrp_mW + interferencePower_mW + thermalNoise_mW;
        double RSSI_dBm = 10 * Math.log10(RSSI_mW);
        double rsrq = rsrp - RSSI_dBm + 10 * Math.log10(N);
        rsrq = Math.max(-20, Math.min(-3, rsrq));

        metrics.setUeSpeed(ueSpeed);
        metrics.setSnr(snr);
        metrics.setRsrp(rsrp);
        metrics.setRsrq(rsrq);
        metrics.setCqi(cqi);
        metrics.setMcs(mcs);
        metrics.setInterferenceLevel(interferenceLevel_dBm);
        metrics.setPrbUtilization(prbUtilization);
        metrics.setBler(bler);
        metrics.setHandoverEvent(handoverEvent);
        metrics.setLatency(latency);
        metrics.setThroughput(throughput);
        metrics.setPacketLoss(bler);
        metrics.setSignalStrength(rsrp);
        metrics.setTimestamp(LocalDateTime.now());
        metrics.setUserDistance(userDistance);
        metrics.setMobilityPattern(mobilityPattern);
        metrics.setUserActivity(userActivity);

        return metrics;
    }

    private double calculateBler(double snr, double prbUtilization, String mobilityPattern, String userActivity) {
        double baseBler = 0.5 * Math.exp(-snr / 5.0);

        double mobilityFactor = getMobilityFactor(mobilityPattern);

        double activityFactor = getActivityBlerFactor(userActivity);

        double bler = baseBler * mobilityFactor * activityFactor
                + (prbUtilization * 0.1)
                + random.nextGaussian() * 0.005;

        return Math.min(Math.max(0, bler), 1);
    }

    private double calculateLatency(double prbUtilization, double bler, boolean handoverEvent,
                                    String mobilityPattern, String userActivity, double allocatedPRBs) {
        double baseLatency = 10.0;
        double mobilityLatencyFactor = getMobilityLatencyFactor(mobilityPattern);
        double activityLatencyFactor = getActivityLatencyFactor(userActivity);
        double resourceFactor = TOTAL_AVAILABLE_PRBs / allocatedPRBs;
        double latency = baseLatency * mobilityLatencyFactor * activityLatencyFactor * resourceFactor
                + (prbUtilization * 50)
                + (bler * 200);

        if (handoverEvent) {
            latency += 50;
        }
        latency += random.nextGaussian() * 5;
        return Math.max(0, latency);
    }

    private double calculateThroughput(MCSInfo mcsInfo, double prbUtilization, double snr, double bler,
                                       String userActivity, double allocatedBandwidth, double allocatedPRBs) {
        double spectralEfficiency = mcsInfo.modulationOrder * mcsInfo.codeRate;
        double snrFactor = Math.log1p(Math.max(0, snr)) / Math.log1p(30);
        double activityThroughputFactor = getActivityThroughputFactor(userActivity);
        double prbFactor = allocatedPRBs / TOTAL_AVAILABLE_PRBs;

        double throughput = allocatedBandwidth
                * spectralEfficiency
                * prbFactor
                * snrFactor
                * (1 - bler)
                / activityThroughputFactor
                * (random.nextDouble() * 0.1 + 0.9);
        if (bler > 0.9 || prbUtilization > 0.95) {
            throughput = 0;
        }
        return throughput;
    }

    //To be reviewed!!
    private int calculateCqi(double snr) {
        if (snr < -6.7) return 0;
        else if (snr < -4.7) return 1;
        else if (snr < -2.3) return 2;
        else if (snr < 0.2) return 3;
        else if (snr < 2.4) return 4;
        else if (snr < 4.3) return 5;
        else if (snr < 5.9) return 6;
        else if (snr < 8.1) return 7;
        else if (snr < 10.3) return 8;
        else if (snr < 11.7) return 9;
        else if (snr < 14.1) return 10;
        else if (snr < 16.3) return 11;
        else if (snr < 18.7) return 12;
        else if (snr < 21) return 13;
        else if (snr < 22.7) return 14;
        else return 15;
    }

    //to be reviewed
    private MCSInfo getMCSInfo(int cqi) {
        MCSInfo[] mcsTable = new MCSInfo[16];
        mcsTable[0] = new MCSInfo(2, 0.1);
        mcsTable[1] = new MCSInfo(2, 0.15);
        mcsTable[2] = new MCSInfo(2, 0.23);
        mcsTable[3] = new MCSInfo(2, 0.38);
        mcsTable[4] = new MCSInfo(2, 0.6);
        mcsTable[5] = new MCSInfo(4, 0.45);
        mcsTable[6] = new MCSInfo(4, 0.55);
        mcsTable[7] = new MCSInfo(4, 0.65);
        mcsTable[8] = new MCSInfo(6, 0.6);
        mcsTable[9] = new MCSInfo(6, 0.7);
        mcsTable[10] = new MCSInfo(6, 0.75);
        mcsTable[11] = new MCSInfo(6, 0.8);
        mcsTable[12] = new MCSInfo(6, 0.85);
        mcsTable[13] = new MCSInfo(6, 0.9);
        mcsTable[14] = new MCSInfo(6, 0.95);
        mcsTable[15] = new MCSInfo(6, 1.0);
        return mcsTable[Math.min(cqi, 15)];
    }

    private double getMobilityFactor(String mobilityPattern) {
        switch (mobilityPattern) {
            case "Stationary":
                return 0.8;
            case "Walking":
                return 1.0;
            case "Driving":
                return 1.2;
            default:
                return 1.0;
        }
    }

    private double getActivityBlerFactor(String userActivity) {
        switch (userActivity) {
            case "VoIP":
                return 0.8;
            case "Gaming":
                return 1.2;
            case "Streaming":
                return 1.1;
            case "WebBrowsing":
                return 1.0;
            default:
                return 1.0;
        }
    }

    private double getMobilityLatencyFactor(String mobilityPattern) {
        switch (mobilityPattern) {
            case "Stationary":
                return 1.0;
            case "Walking":
                return 1.2;
            case "Driving":
                return 1.5;
            default:
                return 1.0;
        }
    }

    private double getActivityLatencyFactor(String userActivity) {
        switch (userActivity) {
            case "VoIP":
                return 0.8;
            case "Gaming":
                return 1.5;
            case "Streaming":
                return 1.1;
            case "WebBrowsing":
                return 1.2;
            default:
                return 1.0;
        }
    }

    private double getActivityThroughputFactor(String userActivity) {
        switch (userActivity) {
            case "Gaming":
                return 1.2;
            case "Streaming":
                return 1.1;
            case "VoIP":
                return 0.8;
            case "WebBrowsing":
                return 0.9;
            default:
                return 1.0;
        }
    }

    private class MCSInfo {
        int modulationOrder;
        double codeRate;

        MCSInfo(int modulationOrder, double codeRate) {
            this.modulationOrder = modulationOrder;
            this.codeRate = codeRate;
        }
    }

    public List<NetworkMetrics> generateMetricsOverTime(SessionManager.UserSession userSession, int iterations, long intervalMs) {
        List<NetworkMetrics> metricsList = new ArrayList<>();

        for (int i = 0; i < iterations; i++) {
            NetworkMetrics metrics = generateMetrics(userSession);
            metricsList.add(metrics);
            try {
                Thread.sleep(intervalMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return metricsList;
    }
}
