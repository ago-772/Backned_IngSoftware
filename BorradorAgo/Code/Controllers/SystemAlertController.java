package com.iot.controllers;

import com.iot.models.dto.SystemAlertResponseDto;
import com.iot.models.enums.AlertSeverity;
import com.iot.services.SystemAlertService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for system alert consultation and acknowledgement.
 * All endpoints are consumed by the frontend — alerts are never created via REST.
 */
@RestController
@RequestMapping("/alerts")
public class SystemAlertController {

    private final SystemAlertService systemAlertService;

    public SystemAlertController(SystemAlertService systemAlertService) {
        this.systemAlertService = systemAlertService;
    }

    /** Returns all alerts ordered by most recent first. */
    @GetMapping
    public ResponseEntity<List<SystemAlertResponseDto>> findAll() {
        return ResponseEntity.ok(systemAlertService.findAll());
    }

    /** Returns all unacknowledged alerts ordered by most recent first. */
    @GetMapping("/unacknowledged")
    public ResponseEntity<List<SystemAlertResponseDto>> findUnacknowledged() {
        return ResponseEntity.ok(systemAlertService.findUnacknowledged());
    }

    /** Returns all alerts of a given severity ordered by most recent first. */
    @GetMapping("/severity/{severity}")
    public ResponseEntity<List<SystemAlertResponseDto>> findBySeverity(@PathVariable AlertSeverity severity) {
        return ResponseEntity.ok(systemAlertService.findBySeverity(severity));
    }

    /** Marks an alert as acknowledged. */
    @PatchMapping("/{id}/acknowledge")
    public ResponseEntity<SystemAlertResponseDto> acknowledge(@PathVariable Long id) {
        return systemAlertService.acknowledge(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}