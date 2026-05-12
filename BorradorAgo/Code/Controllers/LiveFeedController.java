package com.iot.controllers;

import com.iot.models.dto.TelemetryResponseDto;
import com.iot.observer.LiveFeedObserver;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for live telemetry feed consumed by the frontend.
 * Returns the latest in-memory reading held by LiveFeedObserver.
 * Intended to be polled periodically by the frontend for real-time display.
 */
@RestController
@RequestMapping("/live")
public class LiveFeedController {

    private final LiveFeedObserver liveFeedObserver;

    public LiveFeedController(LiveFeedObserver liveFeedObserver) {
        this.liveFeedObserver = liveFeedObserver;
    }

    /** Returns the latest telemetry reading held in memory for live display. */
    @GetMapping("/telemetry")
    public ResponseEntity<TelemetryResponseDto> getLatest() {
        TelemetryResponseDto latest = liveFeedObserver.getLatestReading();
        if (latest == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(latest);
    }
}