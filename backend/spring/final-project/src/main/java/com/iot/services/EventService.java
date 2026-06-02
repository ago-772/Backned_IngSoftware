package com.iot.services;

import com.iot.models.dto.EventRequestDto;
import com.iot.models.dto.EventResponseDto;
import com.iot.models.entities.EventEntity;
import com.iot.models.enums.EventType;
import com.iot.repositories.EventRepository;
import org.springframework.data.domain.Sort;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service 
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    //Persists a new event from the ESP32
    @Transactional
    public EventResponseDto create(EventRequestDto dto) {
        EventEntity entity = EventEntity.builder()
                .type(dto.getType())
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

    /** Returns the latest event. */
    @Transactional(readOnly = true)
    public EventResponseDto findLatest() {

        Optional<EventEntity> entityOptional = eventRepository.findTopByOrderByTimestampDesc();

        if (entityOptional.isPresent()) {
            EventEntity entity = entityOptional.get();
            return EventResponseDto.fromEntity(entity);
        }

    throw new RuntimeException("No events found");
}
}