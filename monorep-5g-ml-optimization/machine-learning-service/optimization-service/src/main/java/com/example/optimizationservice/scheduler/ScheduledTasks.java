package com.example.optimizationservice.scheduler;

import com.example.optimizationservice.service.OptimizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {
    @Autowired
    private OptimizationService optimizationService;

    @Scheduled(fixedRate = 10000)
    public void scheduleOptimization() {
        System.out.println("Scheduled optimization task started" + java.time.LocalDateTime.now());
        optimizationService.optimizeNetwork();
    }
}
