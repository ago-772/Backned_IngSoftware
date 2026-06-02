package com.iot.services;

import com.iot.models.dto.TelemetryRequestDto;
import com.iot.models.dto.TelemetryResponseDto;
import com.iot.models.entities.TelemetryEntity;
import com.iot.repositories.TelemetryRepository;

import java.util.ArrayList;
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
        List<TelemetryEntity> entities = telemetryRepository.findAll(Sort.by(Sort.Direction.DESC, "timestamp"));
        
        List<TelemetryResponseDto> resultado = new ArrayList<>();

        for (TelemetryEntity entity : entities) {
            TelemetryResponseDto dto = TelemetryResponseDto.fromEntity(entity);
            resultado.add(dto);
        }

    return resultado;
    } 

    //Returns the most recent telemetry reading
    @Transactional(readOnly = true)
        public Optional<TelemetryResponseDto> findLatest() {

        Optional<TelemetryEntity> entityOptional = telemetryRepository.findTopByOrderByTimestampDesc();

        if (entityOptional.isPresent()) {
            TelemetryEntity entity = entityOptional.get();
            TelemetryResponseDto dto = TelemetryResponseDto.fromEntity(entity);
            return Optional.of(dto);
        }

        return Optional.empty();
}

}