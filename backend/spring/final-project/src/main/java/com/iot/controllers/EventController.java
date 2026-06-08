package com.iot.controllers;

import com.iot.models.dto.EventRequestDto;
import com.iot.models.dto.EventResponseDto;
import com.iot.services.EventService;
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
 * REST controller for discrete event ingestion and consultation.
 *
 * <p>POST endpoints are consumed by the ESP32. GET endpoints are consumed by the frontend.
 */
@RestController
@RequestMapping("/events")
public class EventController {

  private final EventService eventService;

  public EventController(EventService eventService) {
    this.eventService = eventService;
  }

  /** Ingests a new discrete event from the ESP32. */
  @PostMapping
  public ResponseEntity<EventResponseDto> create(@Valid @RequestBody EventRequestDto dto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(eventService.create(dto));
  }

  /** Returns the latest event. */
  @GetMapping("/latest")
  public ResponseEntity<EventResponseDto> findLatest() {
    return ResponseEntity.ok(eventService.findLatest());
  }

  /** Returns all events ordered by most recent first. */
  @GetMapping
  public ResponseEntity<List<EventResponseDto>> findAll() {
    return ResponseEntity.ok(eventService.findAll());
  }
}
