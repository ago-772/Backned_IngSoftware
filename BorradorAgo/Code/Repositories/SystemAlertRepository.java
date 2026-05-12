package com.iot.repositories;

import com.iot.models.entities.SystemAlert;
import com.iot.models.enums.AlertSeverity;
import com.iot.models.enums.AlertType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA access for {@link SystemAlert}.
 *
 * <p>Inherited {@code save}, {@code findAll}, {@code findById}, etc. are available out of the box.
 * Custom queries below support alert management and frontend notification display.
 */
public interface SystemAlertRepository extends JpaRepository<SystemAlert, Long> {

    /**
     * Returns all unacknowledged alerts ordered by most recent first.
     * Used to display active notifications in the frontend.
     */
    List<SystemAlert> findByAcknowledgedFalseOrderByTriggeredAtDesc();

    /**
     * Returns all alerts of a given severity ordered by most recent first.
     * Used to filter critical alerts in the frontend.
     */
    List<SystemAlert> findBySeverityOrderByTriggeredAtDesc(AlertSeverity severity);

    /**
     * Returns all alerts of a given type ordered by most recent first.
     * Used to check recurrence of a specific alert before triggering a new one.
     */
    List<SystemAlert> findByAlertTypeOrderByTriggeredAtDesc(AlertType alertType);

    /**
     * Returns the most recent alert of a given type.
     * Used to avoid duplicate alerts being generated in quick succession.
     */
    java.util.Optional<SystemAlert> findTopByAlertTypeOrderByTriggeredAtDesc(AlertType alertType);
}