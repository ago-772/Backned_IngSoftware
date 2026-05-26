package com.iot.models.dto;

import com.iot.models.entities.MetricEntity;
import com.iot.models.enums.MetricType;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricResponseDto {

    private Integer id;
    private MetricType type;
    private Double value;
    private String unit;
    private Instant createdAt; // Mantenemos este nombre en el DTO si lo prefieres para el frontend

    public static MetricResponseDto fromEntity(MetricEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return MetricResponseDto.builder()
                .id(entity.getId())
                .type(entity.getType())
                .value(entity.getValue())
                .unit(entity.getUnit())
                .createdAt(entity.getTimestamp()) 
                .build();
    }
}