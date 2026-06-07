package com.iot.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

import com.iot.models.entities.TelemetryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TelemetryRepository extends JpaRepository<TelemetryEntity, Long> {

    Optional<TelemetryEntity> findTopByOrderByCreatedAtDesc();
    @Query("SELECT t FROM TelemetryEntity t WHERE t.sessionId.id = :sessionId")
    List<TelemetryEntity> findBySessionId(@Param("sessionId") Long sessionId);
}
 