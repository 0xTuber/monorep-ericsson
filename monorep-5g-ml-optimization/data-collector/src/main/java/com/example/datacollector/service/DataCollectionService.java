package com.example.datacollector.service;

import com.example.datacollector.entity.NetworkMetrics;
import com.example.datacollector.entity.SessionRessources;
import com.example.datacollector.repository.NetworkMetricsRepository;
import com.example.datacollector.repository.SessionRessourcesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class DataCollectionService {

    @Autowired
    private NetworkMetricsRepository networkMetricsRepository;

    @Autowired
    private SessionRessourcesRepository sessionRessourcesRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${NETWORK_SIMULATOR_URL}")
    private String networkSimulatorUrl;

    public void collectData() {
        try {
            String urlWithParams = networkSimulatorUrl + "/simulateMetricsOverTime/?iterations=10&intervalMs=3";

            ParameterizedTypeReference<Map<String, List<NetworkMetrics>>> responseType =
                    new ParameterizedTypeReference<Map<String, List<NetworkMetrics>>>() {};
            ResponseEntity<Map<String, List<NetworkMetrics>>> responseEntity =
                    restTemplate.exchange(urlWithParams, HttpMethod.GET, null, responseType);
            Map<String, List<NetworkMetrics>> metricsMap = responseEntity.getBody();

            if (metricsMap != null) {
                for (Map.Entry<String, List<NetworkMetrics>> entry : metricsMap.entrySet()) {
                    String sessionId = entry.getKey();
                    List<NetworkMetrics> metricsList = entry.getValue();

                    SessionRessources sessionRessources = sessionRessourcesRepository.findById(sessionId).orElse(null);
                    if (sessionRessources == null) {
                        sessionRessources = new SessionRessources();
                        sessionRessources.setSessionId(sessionId);
                    }

                    for (NetworkMetrics metrics : metricsList) {
                        sessionRessources.setAllocatedBandwidth(metrics.getAllocatedBandwidth());
                        sessionRessources.setAllocatedPrbs(metrics.getAllocatedPrbs());
                        sessionRessourcesRepository.save(sessionRessources);

                        metrics.setSessionRessources(sessionRessources);

                        if (metrics.getTimestamp() == null) {
                            metrics.setTimestamp(LocalDateTime.now());
                        }

                        networkMetricsRepository.save(metrics);
                        System.out.println("Successfully collected data for session " + sessionId + ": " + metrics);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to collect data from network-simulator: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
