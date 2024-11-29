package com.example.model;

public class UserSession {
    private String SessionID;
    private double UsedRessouces;
    NetworkMetrics networkMetrics;
    private boolean IsActive;


    public double getUsedRessouces() {
        return UsedRessouces;
    }

    public NetworkMetrics getNetworkMetrics() {
        return networkMetrics;
    }

    public void setUsedRessouces(double usedRessouces) {
        UsedRessouces = usedRessouces;
    }

    public void setNetworkMetrics(NetworkMetrics networkMetrics) {
        this.networkMetrics = networkMetrics;
    }

    public boolean getStatus() {
        return IsActive;
    }

    public void setStatus(boolean isActive) {
        IsActive = isActive;
    }
}
