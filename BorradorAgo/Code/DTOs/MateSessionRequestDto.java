package com.iot.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payload used internally by the backend to open a new mate session.
 * Not sent by the ESP32 — sessions are inferred server-side from incoming events.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MateSessionRequestDto {

    // Intentionally empty — sessions are opened automatically by the backend
    // when a POUR_MATE or HEATING_STARTED event is received.
    // This class exists to keep the layer consistent and allow future fields if needed.
}