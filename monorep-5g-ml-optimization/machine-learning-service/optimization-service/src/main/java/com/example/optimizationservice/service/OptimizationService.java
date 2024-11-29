package com.example.optimizationservice.service;

import com.example.optimizationservice.model.Allocation;
import com.example.optimizationservice.model.AllocationResponse;
import com.example.optimizationservice.model.NetworkMetrics;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class OptimizationService {

    private static final Logger logger = LoggerFactory.getLogger(OptimizationService.class);

    private final RestTemplate restTemplate;

    public OptimizationService() {
        this.restTemplate = new RestTemplate();
    }

    public void optimizeNetwork() {
        String dataCollectorUrl = "http://app:8081/metrics/all";
        String mlServiceUrl = "http://server:8000/optimize";

        try {
            ResponseEntity<NetworkMetrics[]> response = restTemplate.getForEntity(dataCollectorUrl, NetworkMetrics[].class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                NetworkMetrics[] metricsArray = response.getBody();
                logger.info("Fetched Metrics for all sessions.");

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<NetworkMetrics[]> request = new HttpEntity<>(metricsArray, headers);

                ResponseEntity<String> mlResponse = restTemplate.postForEntity(mlServiceUrl, request, String.class);
                if (mlResponse.getStatusCode() == HttpStatus.OK) {
                    logger.info("Received optimized parameters from ML service.");

                    ObjectMapper objectMapper = new ObjectMapper();
                    AllocationResponse allocationResponse = objectMapper.readValue(mlResponse.getBody(), AllocationResponse.class);

                    for (Allocation allocation : allocationResponse.getAllocations()) {
                        String updateUrl = "http://app:8081/update/" + allocation.getSessionId();
                        HttpEntity<Allocation> updateRequest = new HttpEntity<>(allocation, headers);
                        ResponseEntity<String> updateResponse = restTemplate.postForEntity(updateUrl, updateRequest, String.class);
                        if (updateResponse.getStatusCode() == HttpStatus.OK) {
                            logger.info("Updated session {} with new allocations.", allocation.getSessionId());
                        } else {
                            logger.error("Failed to update session {}: {}", allocation.getSessionId(), updateResponse.getStatusCode());
                        }
                    }
                } else {
                    logger.error("ML Service responded with status: {}", mlResponse.getStatusCode());
                }
            } else {
                logger.error("Failed to fetch metrics. Status Code: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("Error during optimization process: {}", e.getMessage());
        }
    }

}