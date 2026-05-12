package com.iot.models.dto;

import com.iot.models.entities.WaterLevelReading;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response shape for GET/POST /water-level (camelCase JSON).
 *
 * <p>Use {@link #fromEntity(WaterLevelReading)} to map from persistence — do not expose entities on the wire.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaterLevelResponseDto {

    private Long id;
    private Double level;
    private Instant timestamp;

    public static WaterLevelResponseDto fromEntity(WaterLevelReading entity) {
        return WaterLevelResponseDto.builder()
                .id(entity.getId())
                .level(entity.getLevel())
                .timestamp(entity.getTimestamp())
                .build();
    }
}