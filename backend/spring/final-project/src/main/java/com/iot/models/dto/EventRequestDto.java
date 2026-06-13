package com.iot.models.dto;

import com.iot.models.enums.EventType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for incoming events from ESP32. Never expose {@link com.iot.models.entities.EventEntity}
 * directly.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestDto {

  @NotNull private EventType type;
}
