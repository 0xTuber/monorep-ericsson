package com.example.optimizationservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NetworkMetrics {

    @JsonProperty("latency")
    private double latency;

    @JsonProperty("throughput")
    private double throughput;

    @JsonProperty("packet_loss")
    private double packetLoss;

    public NetworkMetrics() {
    }

    public NetworkMetrics(double latency, double throughput, double packetLoss) {
        this.latency = latency;
        this.throughput = throughput;
        this.packetLoss = packetLoss;
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

    @Override
    public String toString() {
        return "NetworkMetrics{" +
                "latency=" + latency +
                ", throughput=" + throughput +
                ", packetLoss=" + packetLoss +
                '}';
    }
}