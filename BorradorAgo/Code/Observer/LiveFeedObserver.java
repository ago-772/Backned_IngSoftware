package com.iot.observer;

import com.iot.models.dto.TelemetryResponseDto;
import org.springframework.stereotype.Component;

/**
 * Observer that propagates live telemetry updates to connected frontend clients.
 * Intended for Server-Sent Events (SSE) or polling endpoint support.
 * Stores the latest reading in memory for immediate frontend consumption.
 */
@Component
public class LiveFeedObserver implements TelemetryObserver {

    private TelemetryResponseDto latestReading;

    @Override
    public void onTelemetryReceived(TelemetryResponseDto telemetry) {
        this.latestReading = telemetry;
    }

    /**
     * Returns the most recent telemetry reading held in memory.
     * Called by the live feed controller endpoint.
     */
    public TelemetryResponseDto getLatestReading() {
        return latestReading;
    }
}