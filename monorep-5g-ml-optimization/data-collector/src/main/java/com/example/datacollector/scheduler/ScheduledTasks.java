package com.example.datacollector.scheduler;

import com.example.datacollector.service.DataCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    @Autowired
    private DataCollectionService dataCollectionService;

    @Scheduled(fixedRate = 3000)
    public void scheduleDataCollection() {
        dataCollectionService.collectData();
    }
}
