package com.iot.models.dto;

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
    private Instant createdAt;
}