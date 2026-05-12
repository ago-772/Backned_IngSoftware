package com.iot.services;

import com.iot.models.dto.TelemetryRequestDto;
import com.iot.models.dto.TelemetryResponseDto;
import com.iot.models.entities.TelemetryEntity;
import com.iot.repositories.TelemetryRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TelemetryService {

    private final TelemetryRepository telemetryRepository;

    public TelemetryService(TelemetryRepository telemetryRepository) {
        this.telemetryRepository = telemetryRepository;
    }

    //-> Mapping Request DTO to JPA Entity using Builder pattern
    @Transactional
    public TelemetryResponseDto create(TelemetryRequestDto dto) {

        TelemetryEntity entity = TelemetryEntity.builder()
                .temperature(dto.getTemperature())
                .targetTemperature(dto.getTargetTemperature())
                .build();
    // Save to database
        TelemetryEntity saved = telemetryRepository.save(entity);

    // Trigger algorithm pipeline after persisting
    processingService.process(saved);
    
    // Return the mapped Response DTO
        return toDto(saved);
    }
 
    //-> Gets the latest readings for the "Live View" dashboard.
    @Transactional(readOnly = true)
    public Optional<TelemetryResponseDto> findLatest() {

        return telemetryRepository.findTopByOrderByTimestampDesc()
                .map(TelemetryResponseDto::fromEntity);
    }


    //-> Returns all telemetry readings ordered by most recent first.

    @Transactional(readOnly = true)
    public List<TelemetryResponseDto> findAll() {
        return telemetryRepository.findAll(Sort.by(Sort.Direction.DESC, "timestamp"))
                .stream()
                .map(TelemetryResponseDto::fromEntity)
                .toList();
    }

/* 
    @Transactional(readOnly = true)
    public TelemetryResponseDto getLatest() {

        TelemetryEntity entity = telemetryRepository
                .findTopByOrderByCreatedAtDesc()
                .orElseThrow(() -> new RuntimeException("No telemetry found"));

        return toDto(entity);
    }
*/
}