package com.iot.repositories;

import com.iot.models.entities.TelemetryEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TelemetryRepository extends JpaRepository<TelemetryEntity, Long> {

  Optional<TelemetryEntity> findTopByOrderByCreatedAtDesc();

  @Query("SELECT t FROM TelemetryEntity t WHERE t.sessionId.id = :sessionId")
  List<TelemetryEntity> findBySessionId(@Param("sessionId") Long sessionId);
}
