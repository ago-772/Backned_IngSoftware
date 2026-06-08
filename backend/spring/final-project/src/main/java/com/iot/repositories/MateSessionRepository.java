package com.iot.repositories;
 
import com.iot.models.entities.MateSessionEntity;
import com.iot.models.enums.SessionType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
 
public interface MateSessionRepository extends JpaRepository<MateSessionEntity, Long> {
 
  /** Returns the first session matching the given type (used to find the active SYSTEM_STARTED session). */
  Optional<MateSessionEntity> findFirstBySessionType(SessionType sessionType);
 
  /** Returns the most recent session regardless of type. */
  Optional<MateSessionEntity> findTopByOrderByTimestampDesc();
}
 