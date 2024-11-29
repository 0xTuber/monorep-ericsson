package com.example.model;

import java.time.LocalDateTime;

public class NetworkMetrics {
    private String sessionId;
    private double allocatedBandwidth;
    private double allocatedPrbs;
    private double latency;
    private double throughput;
    private double packetLoss;
    private double signalStrength;
    private LocalDateTime timestamp;
    private double snr;
    private double rsrp;
    private double rsrq;
    private int cqi;
    private int mcs;
    private double bler;
    private double prbUtilization;
    private double ueSpeed;
    private String userActivity;
    private double userDistance;
    private String mobilityPattern;
    private double interferenceLevel;
    private boolean handoverEvent;


    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public double getAllocatedBandwidth() {
        return allocatedBandwidth;
    }

    public void setAllocatedBandwidth(double allocatedBandwidth) {
        this.allocatedBandwidth = allocatedBandwidth;
    }

    public double getAllocatedPrbs() {
        return allocatedPrbs;
    }

    public void setAllocatedPrbs(double allocatedPRBs) {
        this.allocatedPrbs = allocatedPRBs;
    }

    public String getUserActivity() {
        return userActivity;
    }

    public void setUserActivity(String userActivity) {
        this.userActivity = userActivity;
    }

    public double getUserDistance() {
        return userDistance;
    }

    public void setUserDistance(double userDistance) {
        this.userDistance = userDistance;
    }

    public String getMobilityPattern() {
        return mobilityPattern;
    }

    public void setMobilityPattern(String mobilityPattern) {
        this.mobilityPattern = mobilityPattern;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public double getSignalStrength() {
        return signalStrength;
    }

    public void setSignalStrength(double signalStrength) {
        this.signalStrength = signalStrength;
    }

    public double getPacketLoss() {
        return packetLoss;
    }

    public void setPacketLoss(double packetLoss) {
        this.packetLoss = packetLoss;
    }

    public double getThroughput() {
        return throughput;
    }

    public void setThroughput(double throughput) {
        this.throughput = throughput;
    }

    public double getLatency() {
        return latency;
    }

    public void setLatency(double latency) {
        this.latency = latency;
    }

    public double getSnr() {
        return snr;
    }

    public void setSnr(double snr) {
        this.snr = snr;
    }

    public double getRsrp() {
        return rsrp;
    }

    public void setRsrp(double rsrp) {
        this.rsrp = rsrp;
    }

    public double getRsrq() {
        return rsrq;
    }

    public void setRsrq(double rsrq) {
        this.rsrq = rsrq;
    }

    public int getCqi() {
        return cqi;
    }

    public void setCqi(int cqi) {
        this.cqi = cqi;
    }

    public int getMcs() {
        return mcs;
    }

    public void setMcs(int mcs) {
        this.mcs = mcs;
    }

    public double getBler() {
        return bler;
    }

    public void setBler(double bler) {
        this.bler = bler;
    }

    public double getPrbUtilization() {
        return prbUtilization;
    }

    public void setPrbUtilization(double prbUtilization) {
        this.prbUtilization = prbUtilization;
    }

    public double getUeSpeed() {
        return ueSpeed;
    }

    public void setUeSpeed(double ueSpeed) {
        this.ueSpeed = ueSpeed;
    }

    public double getInterferenceLevel() {
        return interferenceLevel;
    }

    public void setInterferenceLevel(double interferenceLevel) {
        this.interferenceLevel = interferenceLevel;
    }

    public boolean isHandoverEvent() {
        return handoverEvent;
    }

    public void setHandoverEvent(boolean handoverEvent) {
        this.handoverEvent = handoverEvent;
    }
}