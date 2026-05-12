package com.iot.services;

import com.iot.models.dto.SystemAlertRequestDto;
import com.iot.models.dto.SystemAlertResponseDto;
import com.iot.models.entities.SystemAlert;
import com.iot.models.enums.AlertSeverity;
import com.iot.models.enums.AlertType;
import com.iot.repositories.SystemAlertRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Business logic for generating and managing system alerts.
 *
 * <p>Alerts are created internally by {@link ProcessingService} when thresholds or
 * anomalies are detected. Returns {@link SystemAlertResponseDto} only — never JPA entities.
 */
@Service
public class SystemAlertService {

    private final SystemAlertRepository systemAlertRepository;

    public SystemAlertService(SystemAlertRepository systemAlertRepository) {
        this.systemAlertRepository = systemAlertRepository;
    }

    /**
     * Persists a new alert generated internally by the backend.
     * Called by ProcessingService — not exposed directly to external clients.
     */
    @Transactional
    public SystemAlertResponseDto create(AlertType type, String message, AlertSeverity severity) {
        SystemAlert entity = SystemAlert.builder()
                .alertType(type)
                .message(message)
                .severity(severity)
                .acknowledged(false)
                .build();

        SystemAlert saved = systemAlertRepository.save(entity);
        return SystemAlertResponseDto.fromEntity(saved);
    }

    /**
     * Marks an alert as acknowledged by the frontend user.
     * Returns empty if the alert does not exist.
     */
    @Transactional
    public Optional<SystemAlertResponseDto> acknowledge(Long id) {
        return systemAlertRepository.findById(id)
                .map(alert -> {
                    alert.setAcknowledged(true);
                    return SystemAlertResponseDto.fromEntity(systemAlertRepository.save(alert));
                });
    }

    /**
     * Returns all unacknowledged alerts ordered by most recent first.
     * Used for active notification display in the frontend.
     */
    @Transactional(readOnly = true)
    public List<SystemAlertResponseDto> findUnacknowledged() {
        return systemAlertRepository.findByAcknowledgedFalseOrderByTriggeredAtDesc()
                .stream()
                .map(SystemAlertResponseDto::fromEntity)
                .toList();
    }

    /**
     * Returns all alerts ordered by most recent first.
     * Used for full alert history in the frontend.
     */
    @Transactional(readOnly = true)
    public List<SystemAlertResponseDto> findAll() {
        return systemAlertRepository.findAll(Sort.by(Sort.Direction.DESC, "triggeredAt"))
                .stream()
                .map(SystemAlertResponseDto::fromEntity)
                .toList();
    }

    /**
     * Returns all alerts of a given severity ordered by most recent first.
     * Used to filter critical alerts in the frontend.
     */
    @Transactional(readOnly = true)
    public List<SystemAlertResponseDto> findBySeverity(AlertSeverity severity) {
        return systemAlertRepository.findBySeverityOrderByTriggeredAtDesc(severity)
                .stream()
                .map(SystemAlertResponseDto::fromEntity)
                .toList();
    }

    /**
     * Returns the most recent alert of a given type.
     * Used by ProcessingService to avoid generating duplicate alerts in quick succession.
     */
    @Transactional(readOnly = true)
    public Optional<SystemAlertResponseDto> findLatestByType(AlertType type) {
        return systemAlertRepository.findTopByAlertTypeOrderByTriggeredAtDesc(type)
                .map(SystemAlertResponseDto::fromEntity);
    }
}