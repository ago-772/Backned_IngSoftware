package com.iot.models.dto;

import com.iot.models.entities.TelemetryEntity;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TelemetryResponseDto {

    private Long id;
    private Double temperature;
    private Double targetTemperature;
    private Double waterLevel;
    private Instant createdAt;

    public static TelemetryResponseDto fromEntity(TelemetryEntity entity) {

        return TelemetryResponseDto.builder()
                .id(entity.getId())
                .temperature(entity.getTemperature())
                .targetTemperature(entity.getTargetTemperature())
                .waterLevel(entity.getWaterLevel())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}