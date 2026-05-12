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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "temperature", nullable = false)
    private Double temperature;

    @Column(name = "target_temperature", nullable = false)
    private Double targetTemperature;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}