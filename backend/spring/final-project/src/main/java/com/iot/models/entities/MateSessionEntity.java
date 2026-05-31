package com.iot.models.entities;

import jakarta.persistence.Column;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;




import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;


import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;


/**
 * JPA entity mapped to the {@code session} table.
 *
 * <p>Fields: {@code id} (generated), {@code total_pours},
 * {@code createdAt} (set on insert via {@link CreationTimestamp}).
 *
 * <p>Never return this type from REST controllers — use {@link com.iot.models.dto.EventResponseDto}.
 */
@Entity
@Table(name = "sessions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MateSessionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Total number of pours (button presses) detected during this session.
    @Column(name = "total_pours", nullable = false)
    private Integer totalPours;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant timestamp;
}

