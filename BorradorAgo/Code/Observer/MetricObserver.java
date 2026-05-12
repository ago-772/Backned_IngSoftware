package com.iot.observer;

import com.iot.models.dto.TelemetryResponseDto;
import com.iot.models.enums.MetricType;
import com.iot.services.MetricService;
import org.springframework.stereotype.Component;

/**
 * Observer that computes and persists metrics on every new telemetry reading.
 * Reacts to: moving average, cooling rate, temp drop prediction.
 */
@Component
public class MetricObserver implements TelemetryObserver {

    private final MetricService metricService;

    public MetricObserver(MetricService metricService) {
        this.metricService = metricService;
    }

    @Override
    public void onTelemetryReceived(TelemetryResponseDto telemetry) {
        // Placeholder — algorithms will be implemented in ProcessingService
        // and results forwarded here, or computed directly in future iterations.
    }
}