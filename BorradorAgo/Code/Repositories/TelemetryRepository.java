package com.iot.repositories;

import com.iot.models.entities.TelemetryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface TelemetryRepository extends JpaRepository<TelemetryEntity, Long> {

    /**
     * Returns telemetry data between two timestamps ordered ascending.
     */
    List<TelemetryEntity> findByTimestampBetweenOrderByTimestampAsc(
            Instant from,
            Instant to
    );

    /**
     * Returns all historical telemetry ordered ascending.
     */
    List<TelemetryEntity> findAllByOrderByTimestampAsc();

    /**
     * Returns the latest recorded telemetry.
     */
    Optional<TelemetryEntity> findTopByOrderByTimestampDesc();
}