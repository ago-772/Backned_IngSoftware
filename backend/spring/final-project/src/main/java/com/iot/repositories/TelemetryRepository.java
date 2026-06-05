package com.iot.repositories;

import java.util.List;

import com.iot.models.entities.TelemetryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TelemetryRepository extends JpaRepository<TelemetryEntity, Long> {

    Optional<TelemetryEntity> findTopByOrderByCreatedAtDesc();
    List<TelemetryEntity> findBySessionId(Long sessionId);
}
 