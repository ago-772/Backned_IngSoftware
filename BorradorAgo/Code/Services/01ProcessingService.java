package com.iot.services;

import com.iot.models.entities.EventEntity;
import com.iot.models.entities.MetricEntity;
import com.iot.models.entities.SystemAlertEntity;
import com.iot.models.entities.TelemetryEntity;
import com.iot.models.enums.AlertSeverity;
import com.iot.models.enums.AlertType;
import com.iot.models.enums.MetricType;
import com.iot.repositories.TelemetryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProcessingService {

    private final TelemetryRepository telemetryRepository;
    private final MetricService metricService;
    private final AlertService alertService;
    private final SessionService sessionService;

    public ProcessingService(
            TelemetryRepository telemetryRepository,
            MetricService metricService,
            AlertService alertService,
            SessionService sessionService
    ) {
        this.telemetryRepository = telemetryRepository;
        this.metricService = metricService;
        this.alertService = alertService;
        this.sessionService = sessionService;
    }

    /**
     * Central processing flow executed when new telemetry arrives.
     */
    @Transactional
    public void processTelemetry(TelemetryEntity telemetry) {

        generateMetrics(telemetry);

        detectLowTemperatureAlert(telemetry);

        updateSessionState();
    }

    /**
     * Generates calculated metrics from telemetry.
     */
    private void generateMetrics(TelemetryEntity telemetry) {

        List<MetricEntity> metrics = new ArrayList<>();

        MetricEntity coolingMetric = calculateCoolingRate(telemetry);

        MetricEntity averageMetric = calculateMovingAverage();

        if (coolingMetric != null) {
            metrics.add(coolingMetric);
        }

        if (averageMetric != null) {
            metrics.add(averageMetric);
        }

        metricService.saveAll(metrics);
    }

    /**
     * Calculates cooling rate using latest telemetry values.
     */
    private MetricEntity calculateCoolingRate(TelemetryEntity currentTelemetry) {

        List<TelemetryEntity> latestTelemetry =
                telemetryRepository.findTop10ByOrderByTimestampDesc();

        if (latestTelemetry.size() < 2) {
            return null;
        } 

        TelemetryEntity previous = latestTelemetry.get(1);

        double deltaTemperature =
                previous.getTemperature() - currentTelemetry.getTemperature();

        MetricEntity metric = MetricEntity.builder()
                .type(MetricType.COOLING_RATE)
                .value(deltaTemperature)
                .timestamp(Instant.now())
                .build();

        return metric;
    }

    /**
     * Calculates moving average temperature.
     */
    private MetricEntity calculateMovingAverage() {

        List<TelemetryEntity> latestTelemetry =
                telemetryRepository.findTop10ByOrderByTimestampDesc();

        if (latestTelemetry.isEmpty()) {
            return null;
        }

        double average = latestTelemetry.stream()
                .mapToDouble(TelemetryEntity::getTemperature)
                .average()
                .orElse(0);

        MetricEntity metric = MetricEntity.builder()
                .type(MetricType.MOVING_AVERAGE)
                .value(average)
                .timestamp(Instant.now())
                .build();

        return metric;
    }

    /**
     * Detects low temperature conditions.
     */
    private void detectLowTemperatureAlert(TelemetryEntity telemetry) {

        if (telemetry.getTemperature() < 60) {

            SystemAlertEntity alert = SystemAlertEntity.builder()
                    .alertType(AlertType.LOW_TEMPERATURE)
                    .message("Water temperature is below recommended value")
                    .severity(AlertSeverity.WARNING)
                    .triggeredAt(Instant.now())
                    .acknowledged(false)
                    .build();

            alertService.save(alert);
        }
    }

    /**
     * Updates mate session activity state.
     */
    private void updateSessionState() {

        sessionService.updateSession();
    }

    /**
     * Processes incoming events.
     */
    @Transactional
    public void processEvent(EventEntity event) {

        sessionService.registerEvent(event);
    }
}