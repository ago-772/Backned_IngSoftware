package com.iot.repositories;

import com.iot.models.entities.MateSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MateSessionRepository
        extends JpaRepository<MateSessionEntity, Long> {
}