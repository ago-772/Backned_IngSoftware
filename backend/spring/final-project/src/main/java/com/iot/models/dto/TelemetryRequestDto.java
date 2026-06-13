package com.iot.models.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for incoming telemetry data from ESP32. Never expose {@link
 * com.iot.models.entities.TelemetryEntity} directly.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TelemetryRequestDto {

  // Current water temperature from the sensor.
  @NotNull
  @DecimalMin("0.0")
  @DecimalMax("100.0")
  private Double temperature;

  // Target temperature set via the potentiometer.
  @NotNull
  @DecimalMin("10.0")
  @DecimalMax("90.0")
  private Double targetTemperature;

  @NotNull
  @DecimalMin("0.0")
  @DecimalMax("100.0")
  private Double waterLevel;
}
