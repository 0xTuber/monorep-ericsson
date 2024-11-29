package com.example.datacollector.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
public class NetworkMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    private SessionRessources sessionRessources;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SessionRessources getSessionRessources() {
        return sessionRessources;
    }

    public void setSessionRessources(SessionRessources sessionRessources) {
        this.sessionRessources = sessionRessources;
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

    public void setAllocatedPrbs(double allocatedPrbs) {
        this.allocatedPrbs = allocatedPrbs;
    }

    public double getLatency() {
        return latency;
    }

    public void setLatency(double latency) {
        this.latency = latency;
    }

    public double getThroughput() {
        return throughput;
    }

    public void setThroughput(double throughput) {
        this.throughput = throughput;
    }

    public double getPacketLoss() {
        return packetLoss;
    }

    public void setPacketLoss(double packetLoss) {
        this.packetLoss = packetLoss;
    }

    public double getSignalStrength() {
        return signalStrength;
    }

    public void setSignalStrength(double signalStrength) {
        this.signalStrength = signalStrength;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
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

    @Override
    public String toString() {
        return "NetworkMetrics{" +
                "id=" + id +
                ", latency=" + latency +
                ", throughput=" + throughput +
                ", packetLoss=" + packetLoss +
                ", signalStrength=" + signalStrength +
                ", timestamp=" + timestamp +
                ", snr=" + snr +
                ", rsrp=" + rsrp +
                ", rsrq=" + rsrq +
                ", cqi=" + cqi +
                ", mcs=" + mcs +
                ", bler=" + bler +
                ", prbUtilization=" + prbUtilization +
                ", ueSpeed=" + ueSpeed +
                ", userActivity='" + userActivity + '\'' +
                ", userDistance=" + userDistance +
                ", mobilityPattern='" + mobilityPattern + '\'' +
                ", interferenceLevel=" + interferenceLevel +
                ", handoverEvent=" + handoverEvent +
                '}';
    }
}
