package com.iot.listeners;

import com.iot.models.entities.MetricEntity;
import com.iot.models.enums.MetricType;
import com.iot.models.entities.TelemetryEntity;
import com.iot.observer.SessionClosedEvent;
import com.iot.observer.SessionObserver;
import com.iot.repositories.MetricRepository;
import com.iot.repositories.TelemetryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Observer that reacts to a closed session and counts
 * how many telemetry readings exceeded the target temperature
 * by more than the configured threshold.
 */
@Component
@RequiredArgsConstructor
public class AnomalyDetectionListener implements SessionObserver {

    private static final double THRESHOLD = 5.0;

    private final TelemetryRepository telemetryRepository;
    private final MetricRepository metricRepository;

    @Override
    public void onSessionClosed(SessionClosedEvent event) {
        List<TelemetryEntity> readings =
            telemetryRepository.findBySessionId(event.getSessionId());

        if (readings.isEmpty()) return;

    long anomalyCount = 0;

    for (TelemetryEntity reading : readings) {
        if (reading.getTemperature() > reading.getTargetTemperature() + THRESHOLD) {
            anomalyCount++;
        }
    }   

        MetricEntity metric = MetricEntity.builder()
            .sessionId(event.getSessionId())
            .type(MetricType.ANOMALY_COUNT)
            .value((double) anomalyCount)
            .build();

        metricRepository.save(metric);
    }
}