package com.iot.observer;

import com.iot.models.dto.TelemetryResponseDto;

/**
 * Observer interface for telemetry events.
 * Any component that needs to react to new telemetry data must implement this interface.
 */
public interface TelemetryObserver {
    void onTelemetryReceived(TelemetryResponseDto telemetry);
}