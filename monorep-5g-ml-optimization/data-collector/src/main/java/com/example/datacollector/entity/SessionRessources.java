package com.example.datacollector.entity;

import jakarta.persistence.*;
import java.util.List;
import jakarta.persistence.CascadeType;

import java.util.List;

@Entity
public class SessionRessources {
    @Id
    private String sessionId;
    private double allocatedBandwidth;
    private double allocatedPrbs;

    @OneToMany(mappedBy = "sessionRessources", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NetworkMetrics> metrics;


    public List<NetworkMetrics> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<NetworkMetrics> metrics) {
        this.metrics = metrics;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
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
}
