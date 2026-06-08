package com.iot.models.dto;

import com.iot.models.entities.EventEntity;
import com.iot.models.enums.EventType;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventResponseDto {

  private Long id;
  private EventType type;
  private Instant timestamp;
  private String deviceId;

  public static EventResponseDto fromEntity(EventEntity entity) {
    return EventResponseDto.builder()
        .id(entity.getId())
        .type(entity.getType())
        .timestamp(entity.getTimestamp())
        .build();
  }
}
