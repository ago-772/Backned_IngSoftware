package com.iot.services;

import com.iot.models.dto.MetricResponseDto;
import com.iot.models.dto.TelemetryResponseDto;
import com.iot.models.entities.MetricEntity;
import com.iot.models.enums.EventType;
import com.iot.models.enums.MetricType;
import com.iot.repositories.MetricRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProcessingMetricService {

    private final MetricRepository metricRepository;
    private final TelemetryService telemetryService;
    private final EventService eventService;

    // Algoritmo 1: curva de enfriamiento
    // Calcula la tasa de descenso de temperatura promedio en el último período
    public MetricResponseDto computeCoolingCurve(String deviceId) {
        Instant from = Instant.now().minus(30, ChronoUnit.MINUTES);
        Instant to = Instant.now();
        List<TelemetryResponseDto> records = telemetryService.getHistory(deviceId, from, to);

        if (records.size() < 2) {
            throw new RuntimeException("Not enough data to compute cooling curve");
        }

        double first = records.get(0).getTemperature();
        double last = records.get(records.size() - 1).getTemperature();
        double coolingRate = (first - last) / records.size();

        return save(MetricType.COOLING_CURVE, coolingRate);
    }

    // Algoritmo 2: frecuencia de cebados
    // Cuenta cuántos eventos de cebado ocurrieron
    public MetricResponseDto computeCebadoFrequency(String deviceId) {
        long count = eventService.countByType(deviceId, EventType.CEBADO);
        return save(MetricType.CEBADO_FREQUENCY, (double) count);
    }

    // Algoritmo 3: alerta de temperatura
    // Compara temperatura actual vs objetivo y calcula la diferencia
    public MetricResponseDto computeTemperatureAlert(String deviceId) {
        TelemetryResponseDto latest = telemetryService.getLatest(deviceId);
        double diff = latest.getTargetTemperature() - latest.getTemperature();
        return save(MetricType.TEMPERATURE_ALERT, diff);
    }

    public List<MetricResponseDto> getByType(MetricType type) {
        return metricRepository
                .findByTypeOrderByCreatedAtDesc(type)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private MetricResponseDto save(MetricType type, Double value) {
        MetricEntity entity = MetricEntity.builder()
                .type(type)
                .value(value)
                .build();
        return toDto(metricRepository.save(entity));
    }

    private MetricResponseDto toDto(MetricEntity entity) {
        return MetricResponseDto.builder()
                .id(entity.getId())
                .type(entity.getType())
                .value(entity.getValue())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}