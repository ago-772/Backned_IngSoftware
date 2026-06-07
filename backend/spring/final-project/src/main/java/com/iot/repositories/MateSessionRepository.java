package com.iot.repositories;

import com.iot.models.entities.MateSessionEntity;
import com.iot.models.enums.SessionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MateSessionRepository
        extends JpaRepository<MateSessionEntity, Long> {
        //Search the last session active (SYSTEM_STARTED)
        Optional<MateSessionEntity> findFirstBySessionType(SessionType sessionType);
}