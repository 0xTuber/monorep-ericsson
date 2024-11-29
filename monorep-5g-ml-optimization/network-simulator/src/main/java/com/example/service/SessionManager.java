package com.example.service;

import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.util.concurrent.*;
import java.util.*;
import com.example.model.NetworkMetrics;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class SessionManager {

    private static final int MAX_SESSIONS = 10;
    private static final int INITIAL_SESSIONS = 5;
    private static final double TOTAL_AVAILABLE_BANDWIDTH = 20.0;
    private static final double TOTAL_AVAILABLE_PRBs = 100.0;
    private ExecutorService executorService = Executors.newFixedThreadPool(MAX_SESSIONS);
    private double totalAllocatedBandwidth = 0.0;
    private double totalAllocatedPRBs = 0.0;
    private Map<String, Future<?>> sessionMap = new ConcurrentHashMap<>();
    private Map<String, UserSession> userSessions = new ConcurrentHashMap<>();
    private Map<String, NetworkMetrics> latestMetrics = new ConcurrentHashMap<>();

    @Autowired
    private NetworkSimulatorService simulatorService;

    @PostConstruct
    public void initializeSessions() {
        for (int i = 0; i < INITIAL_SESSIONS; i++) {
            ResourceAllocation allocation = generateRandomResourceAllocation();
            if (allocation != null) {
                startSession(allocation);
            } else {
                break;
            }
        }
        scheduleNewUserAddition();
    }

    private void scheduleNewUserAddition() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            if (sessionMap.size() < MAX_SESSIONS) {
                ResourceAllocation allocation = generateRandomResourceAllocation();
                if (allocation != null) {
                    startSession(allocation);
                } else {
                    scheduler.shutdown();
                }
            } else {
                scheduler.shutdown();
            }
        }, 10, 10, TimeUnit.SECONDS);
    }

    private ResourceAllocation generateRandomResourceAllocation() {
        Random random = new Random();

        double remainingBandwidth = TOTAL_AVAILABLE_BANDWIDTH - totalAllocatedBandwidth;
        double remainingPRBs = TOTAL_AVAILABLE_PRBs - totalAllocatedPRBs;

        if (remainingBandwidth <= 0 || remainingPRBs <= 0) {
            return null;
        }

        double maxBandwidthPerUser = 5.0;
        double maxPRBsPerUser = 25.0;

        double allocatedBandwidth = Math.min(1 + random.nextDouble() * (maxBandwidthPerUser - 1), remainingBandwidth);
        double allocatedPRBs = Math.min(5 + random.nextInt((int)(maxPRBsPerUser - 5 + 1)), remainingPRBs);

        return new ResourceAllocation(allocatedBandwidth, allocatedPRBs);
    }


    public String startSession(ResourceAllocation allocation) {
        if (sessionMap.size() >= MAX_SESSIONS) {
            throw new RuntimeException("Maximum number of sessions reached.");
        }

        if (allocation == null) {
            throw new RuntimeException("No resources available to allocate to new session.");
        }

        synchronized (this) {
            if (totalAllocatedBandwidth + allocation.getAllocatedBandwidth() > TOTAL_AVAILABLE_BANDWIDTH ||
                    totalAllocatedPRBs + allocation.getAllocatedPRBs() > TOTAL_AVAILABLE_PRBs) {
                throw new RuntimeException("Insufficient resources to start a new session.");
            }

            totalAllocatedBandwidth += allocation.getAllocatedBandwidth();
            totalAllocatedPRBs += allocation.getAllocatedPRBs();
        }

        String sessionId = UUID.randomUUID().toString();
        UserSession userSession = new UserSession(sessionId, allocation.getAllocatedBandwidth(), allocation.getAllocatedPRBs());
        userSessions.put(sessionId, userSession);

        Future<?> future = executorService.submit(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    NetworkMetrics metrics = simulatorService.generateMetrics(userSession);
                    latestMetrics.put(sessionId, metrics);
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                synchronized (this) {
                    totalAllocatedBandwidth -= userSession.getAllocatedBandwidth();
                    totalAllocatedPRBs -= userSession.getAllocatedPRBs();
                }
            }
        });

        sessionMap.put(sessionId, future);
        return sessionId;
    }


    public List<UserSession> getAllSessions() {
        return new ArrayList<>(userSessions.values());
    }

    public List<NetworkMetrics> getAllMetrics() {
        return new ArrayList<>(latestMetrics.values());
    }

    public NetworkMetrics getSessionMetrics(String sessionId) {
        NetworkMetrics metrics = latestMetrics.get(sessionId);
        if (metrics != null) {
            return metrics;
        } else {
            throw new RuntimeException("Session ID not found or no metrics available yet");
        }
    }

    public void updateSessionResources(String sessionId, double allocatedBandwidth, double allocatedPRBs) {
        UserSession userSession = userSessions.get(sessionId);
        if (userSession != null) {
            synchronized (this) {
                double bandwidthDifference = allocatedBandwidth - userSession.getAllocatedBandwidth();
                double prbDifference = allocatedPRBs - userSession.getAllocatedPRBs();

                if (totalAllocatedBandwidth + bandwidthDifference > TOTAL_AVAILABLE_BANDWIDTH ||
                        totalAllocatedPRBs + prbDifference > TOTAL_AVAILABLE_PRBs) {
                    throw new RuntimeException("Insufficient resources to update the session");
                }

                totalAllocatedBandwidth += bandwidthDifference;
                totalAllocatedPRBs += prbDifference;

                userSession.setAllocatedBandwidth(allocatedBandwidth);
                userSession.setAllocatedPRBs(allocatedPRBs);
            }
        } else {
            throw new RuntimeException("Session ID not found");
        }
    }


    public void stopSession(String sessionId) {
        Future<?> future = sessionMap.remove(sessionId);
        UserSession userSession = userSessions.remove(sessionId);
        latestMetrics.remove(sessionId);
        if (future != null) {
            future.cancel(true);
        }
        if (userSession != null) {
            synchronized (this) {
                totalAllocatedBandwidth -= userSession.getAllocatedBandwidth();
                totalAllocatedPRBs -= userSession.getAllocatedPRBs();
            }
        }
    }

    public Map<String, UserSession> getUserSessions() {
        return new HashMap<>(userSessions);
    }

    public double getTotalAllocatedBandwidth() {
        return totalAllocatedBandwidth;
    }

    public double getTotalAllocatedPRBs() {
        return totalAllocatedPRBs;
    }

    public double getAvailableBandwidth() {
        return TOTAL_AVAILABLE_BANDWIDTH - totalAllocatedBandwidth;
    }

    public double getAvailablePRBs() {
        return TOTAL_AVAILABLE_PRBs - totalAllocatedPRBs;
    }

    public static class UserSession {
        private String sessionId;
        private double allocatedBandwidth;
        private double allocatedPRBs;

        public UserSession(String sessionId, double allocatedBandwidth, double allocatedPRBs) {
            this.sessionId = sessionId;
            this.allocatedBandwidth = allocatedBandwidth;
            this.allocatedPRBs = allocatedPRBs;
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

        public double getAllocatedPRBs() {
            return allocatedPRBs;
        }

        public void setAllocatedPRBs(double allocatedPRBs) {
            this.allocatedPRBs = allocatedPRBs;
        }
    }

    public static class ResourceAllocation {
        private double allocatedBandwidth;
        private double allocatedPrbs;

        public ResourceAllocation(double allocatedBandwidth, double allocatedPRBs) {
            this.allocatedBandwidth = allocatedBandwidth;
            this.allocatedPrbs = allocatedPRBs;
        }

        public double getAllocatedBandwidth() {
            return allocatedBandwidth;
        }

        public double getAllocatedPRBs() {
            return allocatedPrbs;
        }
    }
}
