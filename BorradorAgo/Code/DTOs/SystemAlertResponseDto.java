package com.iot.models.dto;

import com.iot.models.entities.SystemAlert;
import com.iot.models.enums.AlertSeverity;
import com.iot.models.enums.AlertType;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response shape for GET/PATCH /alerts (camelCase JSON).
 *
 * <p>Use {@link #fromEntity(SystemAlert)} to map from persistence — do not expose entities on the wire.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemAlertResponseDto {

    private Long id;
    private AlertType alertType;
    private String message;
    private AlertSeverity severity;
    private Instant triggeredAt;
    private Boolean acknowledged;

    public static SystemAlertResponseDto fromEntity(SystemAlert entity) {
        return SystemAlertResponseDto.builder()
                .id(entity.getId())
                .alertType(entity.getAlertType())
                .message(entity.getMessage())
                .severity(entity.getSeverity())
                .triggeredAt(entity.getTriggeredAt())
                .acknowledged(entity.getAcknowledged())
                .build();
    }
}