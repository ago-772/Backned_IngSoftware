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
 * JPA entity mapped to the {@code telemetry_readings} table.
 *
 * <p>Represents a periodic sample sent by the ESP32, containing the current water
 * temperature and the target temperature set via the potentiometer.
 *
 * <p>Fields:
 * <ul>
 *   <li>{@code id} — auto-generated primary key</li>
 *   <li>{@code temperature} — current water temperature, must be within [0, 100] °C</li>
 *   <li>{@code targetTemperature} — desired temperature from potentiometer, within [50, 90] °C</li>
 *   <li>{@code timestamp} — moment the reading was recorded by the backend</li>
 * </ul>
 *
 * <p>Never return this type from REST controllers — use a TelemetryResponseDto instead.
 */
@Entity
@Table(name = "telemetry_readings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TelemetryReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double temperature;

    @Column(name = "target_temperature", nullable = false)
    private Double targetTemperature;

    @CreationTimestamp
    @Column(name = "timestamp", nullable = false, updatable = false)
    private Instant timestamp;
}