package com.iot.services;

import com.iot.observer.SessionObserver;
import com.iot.observer.SessionClosedEvent;
import com.iot.models.dto.MateSessionRequestDto;
import com.iot.models.dto.MateSessionResponseDto;
import com.iot.models.entities.MateSessionEntity;
import com.iot.models.enums.SessionType;
import com.iot.repositories.MateSessionRepository;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class MateSessionService {

    private final MateSessionRepository repository;
    private final List<SessionObserver> observers = new ArrayList<>();

    public MateSessionService(MateSessionRepository repository) {
        this.repository = repository;
    }
 
    // ── SessionSubject ──────────────────────────────────────────

    // Registers a new observer.
    public void subscribe(SessionObserver observer) {
        observers.add(observer);
    }

    // Removes an existing observer.
    public void unsubscribe(SessionObserver observer) {
        observers.remove(observer);
    }

    // Notifies all registered observers when an event occurs.
    public void notifyObservers(SessionClosedEvent event) {
        for (SessionObserver observer : observers) {
            observer.onSessionClosed(event);
        }
    }

    // ── lógica de negocio ───────────────────────────────────────

    public MateSessionResponseDto create(MateSessionRequestDto request) {
        MateSessionEntity entity = MateSessionEntity.builder()
                .sessionType(request.getSessionType())
                .totalPours(request.getTotalPours())
                .build();

        // Persist the entity (postgress)
        MateSessionEntity saved = repository.save(entity);

        // Notify observers when the system is stopped
        if (SessionType.SYSTEM_STOPPED.equals(saved.getSessionType())) {
            notifyObservers(new SessionClosedEvent(saved.getId()));
        }

        // Convert entity to response DTO
        return MateSessionResponseDto.fromEntity(saved);
    }
}