package com.iot.controllers;

import com.iot.models.dto.MetricResponseDto;
import com.iot.models.enums.MetricType;
import com.iot.services.MetricService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for retrieving algorithm-computed metrics.
 * All endpoints are consumed by the frontend — metrics are never created via REST.
 */
@RestController
@RequestMapping("/metrics")
public class MetricController {

    private final MetricService metricService;

    public MetricController(MetricService metricService) {
        this.metricService = metricService;
    }

    /** Returns all metrics ordered by most recent first. */
    @GetMapping
    public ResponseEntity<List<MetricResponseDto>> findAll() {
        return ResponseEntity.ok(metricService.findAll());
    }

    /** Returns all metrics of a given type ordered by most recent first. */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<MetricResponseDto>> findByType(@PathVariable MetricType type) {
        return ResponseEntity.ok(metricService.findByType(type));
    }

    /** Returns the most recent metric of a given type. */
    @GetMapping("/latest/{type}")
    public ResponseEntity<MetricResponseDto> findLatestByType(@PathVariable MetricType type) {
        return metricService.findLatestByType(type)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}