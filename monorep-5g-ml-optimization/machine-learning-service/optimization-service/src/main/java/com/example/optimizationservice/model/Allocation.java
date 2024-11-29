package com.example.optimizationservice.model;

public class Allocation {
    private String sessionId;
    private double allocatedBandwidth;
    private double allocatedPRBs;

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

    public double getAllocatedPRBs() {
        return allocatedPRBs;
    }

    public void setAllocatedPRBs(double allocatedPRBs) {
        this.allocatedPRBs = allocatedPRBs;
    }
}
