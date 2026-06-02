package com.iot.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

import com.iot.models.enums.MetricType;

/**
 * JPA entity mapped to the {@code metrics} table.
 *
 * <p>Fields: {@code id} (generated), {@code type}, {@code value},
 * {@code createdAt} (set on insert via {@link CreationTimestamp}).
 *
 * <p>Never return this type from REST controllers — use {@link com.iot.models.dto.MetricResponseDto}.
 */
@Entity
@Table(name = "metrics")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricEntity {

    //Unique identifier
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // FK to the session this metric belongs to
    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    //Type of metric calculated by the backend algorithms.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 64)
    private MetricType type;

    //The numeric result of the calculation or algorithm.
    @Column(nullable = false)
    private Double value;

    //Unit of measurement (e.g., "Celsius/min", "count", "percentage").
    @Column(nullable = false, length = 20)
    private String unit;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant timestamp;
}