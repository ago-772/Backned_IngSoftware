package com.iot.services;

import com.iot.models.dto.MateSessionRequestDto;
import com.iot.models.dto.MateSessionResponseDto;
import com.iot.models.entities.MateSessionEntity;
import com.iot.models.enums.SessionType;
import com.iot.observer.SessionClosedEvent;
import com.iot.observer.SessionObserver;
import com.iot.repositories.MateSessionRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MateSessionService {

  private final MateSessionRepository mateSessionRepository;
  private final List<SessionObserver> observers = new ArrayList<>();

  public MateSessionService(MateSessionRepository repository, List<SessionObserver> observers) {
    this.mateSessionRepository = repository;
    this.observers.addAll(observers);
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
  @Transactional
  public MateSessionResponseDto create(MateSessionRequestDto request) {
    Optional<MateSessionEntity> entityOptional =
        mateSessionRepository.findFirstBySessionType(SessionType.SYSTEM_STARTED);

    if (entityOptional.isEmpty()) {
      MateSessionEntity entity =
          MateSessionEntity.builder()
              .sessionType(request.getSessionType())
              .totalPours(request.getTotalPours())
              .build();

      // Persist the entity (postgress)
      MateSessionEntity saved = mateSessionRepository.save(entity);

      // Convert entity to response DTO
      return MateSessionResponseDto.fromEntity(saved);
    } else {
      throw new RuntimeException("Session already exists");
    }
  }

  @Transactional
  public void finishSession(@NonNull MateSessionRequestDto dto) {
    Optional<MateSessionEntity> entityOptional =
        mateSessionRepository.findFirstBySessionType(SessionType.SYSTEM_STARTED);

    if (entityOptional.isPresent()) {
      MateSessionEntity session = entityOptional.get();
      session.setTotalPours(dto.getTotalPours());
      session.setSessionType(SessionType.SYSTEM_STOPPED);

      MateSessionEntity saved = mateSessionRepository.save(session);

      notifyObservers(new SessionClosedEvent(saved.getId()));

    } else {
      throw new IllegalStateException("No active mate session found.");
    }
  }
}
