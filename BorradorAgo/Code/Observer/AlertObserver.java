package com.iot.observer;

import com.iot.models.dto.TelemetryResponseDto;
import com.iot.models.enums.AlertSeverity;
import com.iot.models.enums.AlertType;
import com.iot.services.SystemAlertService;
import org.springframework.stereotype.Component;

/**
 * Observer that evaluates alert conditions on every new telemetry reading.
 * Reacts to: temperature too low, overheating, rapid cooling.
 */
@Component
public class AlertObserver implements TelemetryObserver {

    private final SystemAlertService systemAlertService;

    public AlertObserver(SystemAlertService systemAlertService) {
        this.systemAlertService = systemAlertService;
    }

    @Override
    public void onTelemetryReceived(TelemetryResponseDto telemetry) {
        checkTemperatureTooLow(telemetry);
        checkOverheating(telemetry);
    }

    private void checkTemperatureTooLow(TelemetryResponseDto telemetry) {
        if (telemetry.getTemperature() < telemetry.getTargetTemperature()) {
            systemAlertService.create(
                    AlertType.WATER_TOO_COLD,
                    "Water temperature dropped below target: " + telemetry.getTemperature() + "°C",
                    AlertSeverity.WARNING
            );
        }
    }

    private void checkOverheating(TelemetryResponseDto telemetry) {
        if (telemetry.getTemperature() >= 95.0) {
            systemAlertService.create(
                    AlertType.WATER_OVERHEATED,
                    "Water temperature is dangerously high: " + telemetry.getTemperature() + "°C",
                    AlertSeverity.CRITICAL
            );
        }
    }
}