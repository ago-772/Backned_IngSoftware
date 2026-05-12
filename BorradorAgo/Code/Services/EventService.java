package com.iot.services;

import com.iot.models.dto.EventRequestDto;
import com.iot.models.dto.EventResponseDto;
import com.iot.models.entities.EventEntity;
import com.iot.models.enums.EventType;
import com.iot.repositories.EventRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    //Persists a new event triggered by the device.
    @Transactional
    public EventResponseDto create(EventRequestDto dto) {
        EventEntity entity = EventEntity.builder()
                .type(dto.getType())
                .deviceId(dto.getDeviceId())
                .build();

        EventEntity saved = eventRepository.save(entity);
        return EventResponseDto.fromEntity(saved);
    }

    //Returns all events ordered by most recent first.
    @Transactional(readOnly = true)
    public List<EventResponseDto> findAll() {
        return eventRepository.findAll(Sort.by(Sort.Direction.DESC, "timestamp"))
                .stream()
                .map(EventResponseDto::fromEntity)
                .toList();
    }  

    /**
     * Returns all events of a given type ordered by most recent first.
     * Used to list brew history or heating state changes in the frontend.
     */
    @Transactional(readOnly = true)
    public List<EventResponseDto> findByType(EventType type) {
        return eventRepository.findByTypeOrderByTimestampDesc(type)
                .stream()
                .map(EventResponseDto::fromEntity)
                .toList();
    }

    /**
     * Returns the most recent event of a given type.
     * Used to infer current system state (e.g. is heating active?).
     */
    @Transactional(readOnly = true)
    public Optional<EventResponseDto> findLatestByType(EventType type) {
        return eventRepository.findTopByTypeOrderByTimestampDesc(type)
                .map(EventResponseDto::fromEntity);
    }

    /**
     * Returns the total count of events of a given type.
     * Used for brew count display in the frontend.
     */
    @Transactional(readOnly = true)
    public long countByType(EventType type) {
        return eventRepository.countByType(type);
    }


/* 
    public EventResponseDto save(EventRequestDto dto) {
        EventEntity entity = EventEntity.builder()
                .deviceId(dto.getDeviceId())
                .type(dto.getType())
                .build();
        return toDto(eventRepository.save(entity));
    }

    public List<EventResponseDto> getHistory(String deviceId, Instant from, Instant to) {
        return eventRepository
                .findByDeviceIdAndCreatedAtBetweenOrderByCreatedAtAsc(deviceId, from, to)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<EventResponseDto> getAllByDevice(String deviceId) {
        return eventRepository
                .findByDeviceIdOrderByCreatedAtDesc(deviceId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public long countByType(String deviceId, EventType type) {
        return eventRepository.countByDeviceIdAndType(deviceId, type);
    }

    private EventResponseDto toDto(EventEntity entity) {
        return EventResponseDto.builder()
                .id(entity.getId())
                .deviceId(entity.getDeviceId())
                .type(entity.getType())
                .createdAt(entity.getCreatedAt())
                .build();
    }
*/
}