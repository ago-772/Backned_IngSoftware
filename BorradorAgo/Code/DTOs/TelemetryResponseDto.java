package com.iot.models.dto;

import java.time.Instant;

import Entity.TelemetryEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TelemetryResponseDto {

    private Integer id;

    private Double temperature;

    private Double targetTemperature;

    private Instant createdAt;

    public static TelemetryResponseDto fromEntity(TelemetryEntity entity) {

        return TelemetryResponseDto.builder()
                .id(entity.getId())
                .temperature(entity.getTemperature())
                .targetTemperature(entity.getTargetTemperature())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}