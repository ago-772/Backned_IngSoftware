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


@RestController
@RequestMapping("/telemetry")
public class TelemetryController {

    private final TelemetryService telemetryService;

    public TelemetryController(TelemetryService telemetryService){
        this.telemetryService = telemetryService;
    }

    // ESP32 → backend: saves a new telemetry reading
    @PostMapping
    public ResponseEntity<TelemetryResponseDto> create(@RequestBody @Valid TelemetryRequestDto dto) {
        TelemetryResponseDto response = telemetryService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Frontend → backend: returns all telemetry history
    @GetMapping
    public ResponseEntity<List<TelemetryResponseDto>> getAll() {
        List<TelemetryResponseDto> response = telemetryService.findAll();
        return ResponseEntity.ok(response);
    }

    // Frontend → backend: returns the most recent telemetry reading
    @GetMapping("/latest")
    public ResponseEntity<TelemetryResponseDto> getLatest() {
        return telemetryService.findLatest()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}
}