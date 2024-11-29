package com.example.datacollector.repository;

import com.example.datacollector.entity.SessionRessources;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRessourcesRepository extends JpaRepository<SessionRessources, String> {
}
