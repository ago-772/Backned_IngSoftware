package com.iot.models.entities;

import com.iot.models.enums.SessionStatus;
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
 * JPA entity mapped to the {@code mate_sessions} table.
 *
 * <p>Represents an active or completed mate usage session.
 *
 * <p>A session is deduced by backend activity,
 * such as pour events over a time period.
 */
@Entity
@Table(name = "mate_sessions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MateSessionEntity {

    //Unique identifier
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "start_time", nullable = false, updatable = false)
    private Instant startTime;

    @Column(name = "end_time")
    private Instant endTime;

    //Total number of pours (button presses) detected during this session.
    @Column(name = "total_pours", nullable = false)
    private Integer totalPours = 0;

    //Average temperature calculated over the duration of the session.
    @Column(name = "average_temperature", nullable = false)
    private Double averageTemperature;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private SessionStatus status;

}