package com.iot.models.dto;

import com.iot.models.enums.SessionType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder; 
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payload received from the ESP32 when the system starts or stops.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MateSessionRequestDto {

    @NotNull
    private SessionType sessionType;

    private Integer totalPours;
} 