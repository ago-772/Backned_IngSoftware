package com.iot.controllers;

import com.iot.models.dto.MateSessionRequestDto;
import com.iot.models.dto.MateSessionResponseDto;
import com.iot.services.MateSessionService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** REST controller for mate session lifecycle events sent by the ESP32. */
@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
public class MateSessionController {

  private final MateSessionService mateSessionService;

  /**
   * Receives a session event (SYSTEM_STARTED or SYSTEM_STOPPED) from the ESP32.
   *
   * @param request session payload validated before processing
   * @return 201 with the persisted session, 400 if payload is invalid
   */
  @PostMapping
  public ResponseEntity<MateSessionResponseDto> create(
      @Valid @RequestBody MateSessionRequestDto request) {

    MateSessionResponseDto response = mateSessionService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  /*
  Voy a dar por finalizada la session, y guardo en la base de datos ese evento
   */
  @PostMapping("/finish")
  public ResponseEntity<Void> finish(@RequestBody MateSessionRequestDto dto) {
    mateSessionService.finishSession(dto);

    return ResponseEntity.ok().build();
  }

  @GetMapping
  public ResponseEntity<List<MateSessionResponseDto>> findAll() {
    return ResponseEntity.ok(mateSessionService.findAll());
  }

  /** Returns the most recent session. */
  @GetMapping("/latest")
  public ResponseEntity<MateSessionResponseDto> findLatest() {
    return mateSessionService
        .findLatest()
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }
}
