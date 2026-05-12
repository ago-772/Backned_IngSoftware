package com.iot.controllers;

import com.iot.models.dto.MateSessionResponseDto;
import com.iot.services.MateSessionService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for mate session consultation.
 * All endpoints are consumed by the frontend — sessions are never created via REST.
 */
@RestController
@RequestMapping("/sessions")
public class MateSessionController {

    private final MateSessionService mateSessionService;

    public MateSessionController(MateSessionService mateSessionService) {
        this.mateSessionService = mateSessionService;
    }

    /** Returns all mate sessions ordered by most recent first. */
    @GetMapping
    public ResponseEntity<List<MateSessionResponseDto>> findAll() {
        return ResponseEntity.ok(mateSessionService.findAll());
    }

    /** Returns the currently active session if one exists. */
    @GetMapping("/active")
    public ResponseEntity<MateSessionResponseDto> findActive() {
        return mateSessionService.findActive()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Returns a session by id. */
    @GetMapping("/{id}")
    public ResponseEntity<MateSessionResponseDto> findById(@PathVariable Long id) {
        return mateSessionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}