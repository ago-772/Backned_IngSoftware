package com.iot.models.dto;

import com.iot.models.entities.MateSession;
import com.iot.models.enums.SessionStatus;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response shape for GET /sessions and GET /sessions/{id} (camelCase JSON).
 *
 * <p>Use {@link #fromEntity(MateSession)} to map from persistence — do not expose entities on the wire.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MateSessionResponseDto {

    private Long id;
    private Instant startTime;
    private Instant endTime;
    private Integer totalPours;
    private Double averageTemperature;
    private SessionStatus status;

    public static MateSessionResponseDto fromEntity(MateSession entity) {
        return MateSessionResponseDto.builder()
                .id(entity.getId())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .totalPours(entity.getTotalPours())
                .averageTemperature(entity.getAverageTemperature())
                .status(entity.getStatus())
                .build();
    }
}