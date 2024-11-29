package com.example.datacollector.service;

import com.example.datacollector.entity.NetworkMetrics;
import com.example.datacollector.repository.NetworkMetricsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class NetworkMetricsService {

    private final NetworkMetricsRepository networkMetricsRepository;

    @Autowired
    public NetworkMetricsService(NetworkMetricsRepository networkMetricsRepository) {
        this.networkMetricsRepository = networkMetricsRepository;
    }

    public List<NetworkMetrics> getAllMetrics() {
        return networkMetricsRepository.findAllByOrderByTimestampAsc();
    }

    public NetworkMetrics getLatestMetrics() {
        return networkMetricsRepository.findTopByOrderByTimestampDesc();
    }

    public List<NetworkMetrics> getLatestMetricsForEachSession() {
        return networkMetricsRepository.findLatestMetricsForEachSession();
    }

    public NetworkMetrics saveMetrics(NetworkMetrics networkMetrics) {
        networkMetrics.setTimestamp(LocalDateTime.now());
        return networkMetricsRepository.save(networkMetrics);
    }
}