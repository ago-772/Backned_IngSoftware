package com.iot.models.dto;

import com.iot.models.entities.MateSessionEntity;
import com.iot.models.enums.SessionType;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response shape for GET/POST /sessions (camelCase JSON).
 *
 * <p>Use {@link #fromEntity(MateSessionEntity)} to map from persistence — do not expose entities on
 * the wire.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MateSessionResponseDto {

  private Long id;
  private SessionType sessionType;
  private Integer totalPours;
  private Double averageTemperature;
  private Instant timestamp;

  public static MateSessionResponseDto fromEntity(MateSessionEntity entity) {
    return MateSessionResponseDto.builder()
        .id(entity.getId())
        .sessionType(entity.getSessionType())
        .totalPours(entity.getTotalPours())
        .timestamp(entity.getTimestamp())
        .build();
  }
}
