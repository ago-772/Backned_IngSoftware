package com.iot.models.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payload received from the ESP32 for a water level reading.
 * level must be within [0, 100] %.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaterLevelRequestDto {

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private Double level;
}