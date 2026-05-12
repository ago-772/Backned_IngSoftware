package com.iot.repositories;

import com.iot.models.entities.MateSession;
import com.iot.models.enums.SessionStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA access for {@link MateSession}.
 *
 * <p>Inherited {@code save}, {@code findAll}, {@code findById}, etc. are available out of the box.
 * Custom queries below support session lifecycle management and historical consultation.
 */
public interface MateSessionRepository extends JpaRepository<MateSession, Long> {

    /**
     * Returns the current active session if one exists.
     * Used to determine whether to open a new session or update the existing one.
     */
    Optional<MateSession> findTopByStatusOrderByStartTimeDesc(SessionStatus status);

    /**
     * Returns all sessions with a given status.
     * Used to list active or closed sessions in the frontend.
     */
    List<MateSession> findByStatusOrderByStartTimeDesc(SessionStatus status);

    /**
     * Returns all sessions that started between two timestamps, ordered ascending.
     * Used for historical consultation.
     */
    List<MateSession> findByStartTimeBetweenOrderByStartTimeAsc(Instant from, Instant to);

    /**
     * Returns the most recent closed session.
     * Used to display last session summary in the frontend.
     */
    Optional<MateSession> findTopByStatusOrderByEndTimeDesc(SessionStatus status);
}