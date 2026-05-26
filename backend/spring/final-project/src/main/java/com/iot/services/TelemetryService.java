package com.iot.services;

import com.iot.models.dto.TelemetryRequestDto;
import com.iot.models.dto.TelemetryResponseDto;
import com.iot.models.entities.TelemetryEntity;
import com.iot.repositories.TelemetryRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
/**
 * Business logic for ingesting and listing sensor readings.
 * 
 * <p>Persists via {@link SensorsRepository} and returns {@link SensorResponseDto} only — never JPA entities.
 */
@Service
public class TelemetryService {

    private final TelemetryRepository telemetryRepository;

    public TelemetryService(TelemetryRepository telemetryRepository) {
        this.telemetryRepository = telemetryRepository;
    }

    // Persists a new telemetry reading from the ESP32.
	@Transactional
    public TelemetryResponseDto create(TelemetryRequestDto dto) {
        TelemetryEntity entity = TelemetryEntity.builder()
                .temperature(dto.getTemperature())
                .targetTemperature(dto.getTargetTemperature())
                .waterLevel(dto.getWaterLevel())
                .build();

        TelemetryEntity saved = telemetryRepository.save(entity);
        return TelemetryResponseDto.fromEntity(saved);
    }

    // Returns all telemetry readings
    @Transactional(readOnly = true)
    public List<TelemetryResponseDto> findAll() {
        return telemetryRepository.findAll(Sort.by(Sort.Direction.DESC, "timestamp"))
                .stream()
                .map(TelemetryResponseDto::fromEntity)
                .toList();
    }

    //Returns the most recent telemetry reading
     @Transactional(readOnly = true)
    public Optional<TelemetryResponseDto> findLatest() {
        return telemetryRepository
                .findTopByOrderByTimestampDesc()
                .map(TelemetryResponseDto::fromEntity);
    }

}