package com.iot.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

/**
 * JPA entity mapped to the {@code telemetry} table.
 *
 * <p>Fields: {@code id} (generated), {@code deviceId}, {@code temperature},
 * {@code targetTemperature}, {@code createdAt} (set on insert via {@link CreationTimestamp}).
 *
 * <p>Never return this type from REST controllers — use {@link com.iot.models.dto.TelemetryResponseDto}.
 */
@Entity
@Table(name = "telemetry")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TelemetryEntity {

    // Unique identifier generated automatically by the database
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Identifier of the active session
    @Column(name = "session_id")
    private Long sessionId;

    //Current water temperature measured by the sensor
    @Column(name = "temperature", nullable = false)
    private Double temperature;

    //Target temperature configured for the system
    @Column(name = "target_temperature", nullable = false)
    private Double targetTemperature;

    //Current water level percentage detected by the sensor
    // %[0, 100]
    @Column(name = "water_level", nullable = false)
    private Double waterLevel;

    //Timestamp automatically generated when the reading is stored
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}