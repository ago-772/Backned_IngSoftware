package com.iot.models.entities;

import com.iot.models.enums.AlertSeverity;
import com.iot.models.enums.AlertType;
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

/**
 * JPA entity mapped to the {@code system_alerts} table.
 *
 * <p>Represents alerts generated automatically by the backend.
 *
 * <p>Examples:
 * WATER_TOO_COLD,
 * TARGET_TEMPERATURE_REACHED,
 * RAPID_COOLING_DETECTED.
 */
@Entity
@Table(name = "system_alerts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemAlertEntity {

    //Unique identifier
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Type of alert generated
    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type", nullable = false, length = 64)
    private AlertType alertType;

    //Human-readable alert description.
    @Column(nullable = false, length = 256)
    private String message;

    //Severity level of the alert (INFO, WARNING, CRITICAL, etc.)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private AlertSeverity severity;

    //Timestamp when the alert was generated.
    @CreationTimestamp
    @Column(name = "triggered_at", nullable = false, updatable = false)
    private Instant triggeredAt;

    //Indicates whether the alert has been acknowledged by the system or user.
    @Column(nullable = false)
    private Boolean acknowledged;
}