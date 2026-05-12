package com.iot.models.dto;

import com.iot.models.enums.AlertSeverity;
import com.iot.models.enums.AlertType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payload used internally by the backend to create a system alert.
 * Not sent by the ESP32 — alerts are generated server-side.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemAlertRequestDto {

    @NotNull
    private AlertType alertType;

    @NotBlank
    @Size(max = 256)
    private String message;

    @NotNull
    private AlertSeverity severity;
}