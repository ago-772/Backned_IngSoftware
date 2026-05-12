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
 * JPA entity mapped to the {@code water_level_readings} table.
 *
 * <p>Represents a periodic water level sample sent by the ESP32.
 *
 * <p>Fields:
 * <ul>
 *   <li>{@code id} — auto-generated primary key</li>
 *   <li>{@code level} — current water level as a percentage, must be within [0, 100] %</li>
 *   <li>{@code timestamp} — moment the reading was recorded by the backend</li>
 * </ul>
 *
 * <p>Never return this type from REST controllers — use a WaterLevelResponseDto instead.
 */
@Entity
@Table(name = "water_level_readings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaterLevelReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double level;

    @CreationTimestamp
    @Column(name = "timestamp", nullable = false, updatable = false)
    private Instant timestamp;
}