package com.example.datacollector.repository;

import com.example.datacollector.entity.NetworkMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NetworkMetricsRepository extends JpaRepository<NetworkMetrics, Long> {
    NetworkMetrics findTopByOrderByTimestampDesc();
    List<NetworkMetrics> findAllByOrderByTimestampAsc();
    @Query("SELECT m FROM NetworkMetrics m WHERE m.timestamp = (SELECT MAX(n.timestamp) FROM NetworkMetrics n WHERE n.sessionRessources.sessionId = m.sessionRessources.sessionId)")
    List<NetworkMetrics> findLatestMetricsForEachSession();
}