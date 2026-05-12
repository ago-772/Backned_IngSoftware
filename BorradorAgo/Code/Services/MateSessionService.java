package com.iot.services;

import com.iot.models.dto.MateSessionResponseDto;
import com.iot.models.entities.Event;
import com.iot.models.entities.MateSession;
import com.iot.models.enums.EventType;
import com.iot.models.enums.SessionStatus;
import com.iot.repositories.MateSessionRepository;
import com.iot.repositories.TelemetryRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Business logic for inferring and managing mate sessions.
 *
 * <p>Sessions are never created by external clients — they are opened and closed
 * automatically based on incoming events. Returns {@link MateSessionResponseDto} only — never JPA entities.
 */
@Service
public class MateSessionService {

    private final MateSessionRepository mateSessionRepository;
    private final TelemetryRepository telemetryRepository;

    public MateSessionService(MateSessionRepository mateSessionRepository,
                               TelemetryRepository telemetryRepository) {
        this.mateSessionRepository = mateSessionRepository;
        this.telemetryRepository = telemetryRepository;
    }

    /**
     * Reacts to an incoming event and manages the session lifecycle.
     * POUR_MATE / HEATING_STARTED — opens a new session if none is active, or increments pour count.
     * SYSTEM_STOPPED — closes the active session if one exists.
     */
    @Transactional
    public void handleEvent(Event event) {
        if (event.getType() == EventType.POUR_MATE || event.getType() == EventType.HEATING_STARTED) {
            handleBrewOrHeatEvent();
        } else if (event.getType() == EventType.SYSTEM_STOPPED) {
            closeActiveSession();
        }
    }

    /**
     * Opens a new session if none is active, or increments totalPours on the existing one.
     */
    private void handleBrewOrHeatEvent() {
        Optional<MateSession> active = mateSessionRepository
                .findTopByStatusOrderByStartTimeDesc(SessionStatus.ACTIVE);

        if (active.isPresent()) {
            MateSession session = active.get();
            session.setTotalPours(session.getTotalPours() + 1);
            mateSessionRepository.save(session);
        } else {
            MateSession newSession = MateSession.builder()
                    .totalPours(1)
                    .status(SessionStatus.ACTIVE)
                    .build();
            mateSessionRepository.save(newSession);
        }
    }

    /**
     * Closes the active session, computing endTime and averageTemperature.
     * averageTemperature is calculated from all telemetry readings since session start.
     */
    private void closeActiveSession() {
        mateSessionRepository.findTopByStatusOrderByStartTimeDesc(SessionStatus.ACTIVE)
                .ifPresent(session -> {
                    double avgTemp = telemetryRepository
                            .findByTimestampBetweenOrderByTimestampAsc(session.getStartTime(), Instant.now())
                            .stream()
                            .mapToDouble(t -> t.getTemperature())
                            .average()
                            .orElse(0.0);

                    session.setEndTime(Instant.now());
                    session.setAverageTemperature(avgTemp);
                    session.setStatus(SessionStatus.CLOSED);
                    mateSessionRepository.save(session);
                });
    }

    /**
     * Returns all sessions ordered by most recent first.
     * Used for historical session list in the frontend.
     */
    @Transactional(readOnly = true)
    public List<MateSessionResponseDto> findAll() {
        return mateSessionRepository.findAll(Sort.by(Sort.Direction.DESC, "startTime"))
                .stream()
                .map(MateSessionResponseDto::fromEntity)
                .toList();
    }

    /**
     * Returns the currently active session if one exists.
     * Used for live session display in the frontend.
     */
    @Transactional(readOnly = true)
    public Optional<MateSessionResponseDto> findActive() {
        return mateSessionRepository.findTopByStatusOrderByStartTimeDesc(SessionStatus.ACTIVE)
                .map(MateSessionResponseDto::fromEntity);
    }

    /**
     * Returns a session by id.
     */
    @Transactional(readOnly = true)
    public Optional<MateSessionResponseDto> findById(Long id) {
        return mateSessionRepository.findById(id)
                .map(MateSessionResponseDto::fromEntity);
    }
}