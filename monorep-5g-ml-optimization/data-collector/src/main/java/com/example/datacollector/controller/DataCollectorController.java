package com.example.datacollector.controller;

import com.example.datacollector.entity.NetworkMetrics;
import com.example.datacollector.service.NetworkMetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DataCollectorController {

    private final NetworkMetricsService networkMetricsService;

    @Autowired
    public DataCollectorController(NetworkMetricsService networkMetricsService) {
        this.networkMetricsService = networkMetricsService;
    }

    @GetMapping("/metrics")
    public List<NetworkMetrics> getAllMetrics() {
        return networkMetricsService.getAllMetrics();
    }

    @GetMapping("/metrics/latest")
    public List<NetworkMetrics> getLatestMetricsForEachSession() {return networkMetricsService.getLatestMetricsForEachSession();}
}