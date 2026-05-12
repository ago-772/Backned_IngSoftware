package com.iot.controllers;

import com.iot.models.dto.TelemetryRequestDto;
import com.iot.models.dto.TelemetryResponseDto;
import com.iot.services.TelemetryService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for telemetry ingestion and historical consultation.
 *
 * <p>POST endpoints are consumed by the ESP32.
 * GET endpoints are consumed by the frontend.
 */
@RestController
@RequestMapping("/telemetry")
public class TelemetryController {

    private final TelemetryService telemetryService;

    public TelemetryController(TelemetryService telemetryService) {
        this.telemetryService = telemetryService;
    }

    /** Ingests a new telemetry reading from the ESP32. */
    @PostMapping
    public ResponseEntity<TelemetryResponseDto> create(@Valid @RequestBody TelemetryRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(telemetryService.create(dto));
    }

    /** Returns all telemetry readings ordered by most recent first. */
    @GetMapping
    public ResponseEntity<List<TelemetryResponseDto>> findAll() {
        return ResponseEntity.ok(telemetryService.findAll());
    }

    /** Returns the most recent telemetry reading. */
    @GetMapping("/latest")
    public ResponseEntity<TelemetryResponseDto> findLatest() {
        return telemetryService.findLatest()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}