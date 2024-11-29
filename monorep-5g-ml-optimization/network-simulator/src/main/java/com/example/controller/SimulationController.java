package com.example.controller;

import com.example.service.SessionManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.service.NetworkSimulatorService;
import com.example.model.NetworkMetrics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/simulation")
public class SimulationController {

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private NetworkSimulatorService simulatorService;

    @GetMapping("/sessions/")
    public ResponseEntity<List<SessionManager.UserSession>> getAllSessions() {
        try {
            List<SessionManager.UserSession> sessions = sessionManager.getAllSessions();
            return ResponseEntity.ok(sessions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/simulateMetricsOverTime/")
    public ResponseEntity<Map<String, List<NetworkMetrics>>> simulateMetricsOverTime(
            @RequestParam(defaultValue = "10") int iterations,
            @RequestParam(defaultValue = "3") long intervalMs) {
        try {
            Map<String, SessionManager.UserSession> sessions = sessionManager.getUserSessions();
            Map<String, List<NetworkMetrics>> metricsOverTime = new HashMap<>();

            for (SessionManager.UserSession userSession : sessions.values()) {
                List<NetworkMetrics> metricsList = simulatorService.generateMetricsOverTime(userSession, iterations, intervalMs);
                metricsOverTime.put(userSession.getSessionId(), metricsList);
            }

            return ResponseEntity.ok(metricsOverTime);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/metrics/")
    public ResponseEntity<List<NetworkMetrics>> getAllMetrics() {
        try {
            List<NetworkMetrics> metrics = sessionManager.getAllMetrics();
            return ResponseEntity.ok(metrics);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/metrics/{sessionId}")
    public ResponseEntity<NetworkMetrics> getSessionMetrics(@PathVariable String sessionId) {
        try {
            NetworkMetrics metrics = sessionManager.getSessionMetrics(sessionId);
            return ResponseEntity.ok(metrics);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    @PostMapping("/update/{sessionId}")
    public ResponseEntity<String> updateSessionResources(@PathVariable String sessionId, @RequestBody ResourceAllocationRequest request) {
        try {
            sessionManager.updateSessionResources(sessionId, request.getAllocatedBandwidth(), request.getAllocatedPRBs());
            return ResponseEntity.ok("Resources updated for session: " + sessionId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Session ID not found.");
        }
    }

    @DeleteMapping("/stop/{sessionId}")
    public ResponseEntity<String> stopSession(@PathVariable String sessionId) {
        sessionManager.stopSession(sessionId);
        return ResponseEntity.ok("Session stopped: " + sessionId);
    }

    @GetMapping("/availableBandwidth/")
    public ResponseEntity<Double> getAvailableBw() {
        try {
            double availableBandwidth = sessionManager.getAvailableBandwidth();
            return ResponseEntity.ok(availableBandwidth);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/availablePrbs/")
    public ResponseEntity<Double> getAvailablePrbs() {
        try {
            double availablePrbs = sessionManager.getAvailablePRBs();
            return ResponseEntity.ok(availablePrbs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    public static class ResourceAllocationRequest {
        private double allocatedBandwidth;
        private double allocatedPRBs;

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
}