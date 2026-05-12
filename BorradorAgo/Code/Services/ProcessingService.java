package com.iot.services;

import com.iot.models.dto.TelemetryResponseDto;
import com.iot.models.entities.TelemetryReading;
import com.iot.models.enums.AlertSeverity;
import com.iot.models.enums.AlertType;
import com.iot.models.enums.MetricType;
import com.iot.observer.TelemetryObserver;
import com.iot.repositories.TelemetryRepository;
import com.iot.strategy.SessionCloseStrategy;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Central processing service for all telemetry-driven analysis.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Receive new telemetry and trigger algorithm pipeline</li>
 *   <li>Run the three required algorithms: moving average, cooling rate, anomaly detection</li>
 *   <li>Generate metrics and persist them via {@link MetricService}</li>
 *   <li>Generate alerts via {@link SystemAlertService} when thresholds are exceeded</li>
 *   <li>Coordinate the Strategy pattern for session close logic</li>
 *   <li>Notify registered Observer instances after processing</li>
 * </ul>
 */
@Service
public class ProcessingService {

    // --- Constants ---
    private static final int MOVING_AVERAGE_WINDOW = 10;
    private static final double RAPID_COOLING_THRESHOLD = 2.0;  // °C/min
    private static final double ANOMALY_DEVIATION_THRESHOLD = 5.0; // °C from moving average

    // --- Dependencies ---
    private final TelemetryRepository telemetryRepository;
    private final MetricService metricService;
    private final SystemAlertService systemAlertService;
    private final MateSessionService mateSessionService;
    private final List<TelemetryObserver> observers;
    private final SessionCloseStrategy sessionCloseStrategy;

    public ProcessingService(TelemetryRepository telemetryRepository,
                             MetricService metricService,
                             SystemAlertService systemAlertService,
                             MateSessionService mateSessionService,
                             List<TelemetryObserver> observers,
                             SessionCloseStrategy sessionCloseStrategy) {
        this.telemetryRepository = telemetryRepository;
        this.metricService = metricService;
        this.systemAlertService = systemAlertService;
        this.mateSessionService = mateSessionService;
        this.observers = observers;
        this.sessionCloseStrategy = sessionCloseStrategy;
    }

    // -------------------------------------------------------------------------
    // 1. ENTRY POINT — called by TelemetryService after each ingestion
    // -------------------------------------------------------------------------

    /**
     * Central pipeline triggered on every new telemetry reading.
     * Runs all algorithms, generates metrics, checks alerts, and notifies observers.
     */
    @Transactional
    public void process(TelemetryReading reading) {
        List<TelemetryReading> recentReadings = fetchRecentReadings();

        double movingAvg = computeMovingAverage(recentReadings);
        double coolingRate = computeCoolingRate(recentReadings);

        persistMetrics(reading, movingAvg, coolingRate);
        checkAlerts(reading, movingAvg, coolingRate);
        checkSessionClose(reading);
        notifyObservers(TelemetryResponseDto.fromEntity(reading));
    }

    // -------------------------------------------------------------------------
    // 2. ALGORITHMS
    // -------------------------------------------------------------------------

    /**
     * Algorithm 1 — Moving average of temperature over the last N readings.
     * Smooths out noise from the sensor for stable frontend display.
     *
     * @param readings last N telemetry readings ordered ascending
     * @return average temperature across the window
     */
    private double computeMovingAverage(List<TelemetryReading> readings) {
        if (readings.isEmpty()) return 0.0;
        return readings.stream()
                .mapToDouble(TelemetryReading::getTemperature)
                .average()
                .orElse(0.0);
    }

    /**
     * Algorithm 2 — Cooling rate in °C/min between the oldest and newest reading in the window.
     * Positive value means heating, negative means cooling.
     *
     * @param readings last N telemetry readings ordered ascending
     * @return rate of temperature change in °C per minute
     */
    private double computeCoolingRate(List<TelemetryReading> readings) {
        if (readings.size() < 2) return 0.0;

        TelemetryReading oldest = readings.get(0);
        TelemetryReading newest = readings.get(readings.size() - 1);

        double tempDelta = newest.getTemperature() - oldest.getTemperature();
        double minutesDelta = ChronoUnit.SECONDS.between(oldest.getTimestamp(), newest.getTimestamp()) / 60.0;

        if (minutesDelta == 0) return 0.0;
        return tempDelta / minutesDelta;
    }

    /**
     * Algorithm 3 — Anomaly detection based on deviation from moving average.
     * A reading is anomalous if it deviates more than the configured threshold.
     *
     * @param reading current telemetry reading
     * @param movingAverage computed moving average for the current window
     * @return true if the reading is considered anomalous
     */
    private boolean isAnomaly(TelemetryReading reading, double movingAverage) {
        return Math.abs(reading.getTemperature() - movingAverage) > ANOMALY_DEVIATION_THRESHOLD;
    }

    // -------------------------------------------------------------------------
    // 3. METRIC GENERATION
    // -------------------------------------------------------------------------

    /**
     * Persists computed algorithm outputs as metrics for historical visualization.
     */
    private void persistMetrics(TelemetryReading reading, double movingAvg, double coolingRate) {
        metricService.save(MetricType.SESSION_AVG_TEMPERATURE, movingAvg, "°C");
        metricService.save(MetricType.COOLING_RATE, coolingRate, "°C/min");

        double prediction = estimateMinutesToTarget(reading, coolingRate);
        metricService.save(MetricType.TEMP_DROP_PREDICTION, prediction, "min");
    }

    /**
     * Estimates how many minutes until temperature drops below targetTemperature.
     * Returns 0 if already below target or cooling rate is non-negative.
     */
    private double estimateMinutesToTarget(TelemetryReading reading, double coolingRate) {
        if (coolingRate >= 0) return 0.0;
        double delta = reading.getTemperature() - reading.getTargetTemperature();
        if (delta <= 0) return 0.0;
        return delta / Math.abs(coolingRate);
    }

    // -------------------------------------------------------------------------
    // 4. ALERT GENERATION
    // -------------------------------------------------------------------------

    /**
     * Checks all alert conditions against the current reading and computed metrics.
     * Avoids duplicate alerts by checking the most recent alert of each type.
     */
    private void checkAlerts(TelemetryReading reading, double movingAvg, double coolingRate) {
        checkTemperatureTooLow(reading);
        checkOverheating(reading);
        checkRapidCooling(coolingRate);
        checkAnomaly(reading, movingAvg);
    }

    private void checkTemperatureTooLow(TelemetryReading reading) {
        if (reading.getTemperature() < reading.getTargetTemperature()) {
            systemAlertService.create(
                    AlertType.WATER_TOO_COLD,
                    "Water temperature dropped below target: " + reading.getTemperature() + "°C",
                    AlertSeverity.WARNING
            );
        }
    }

    private void checkOverheating(TelemetryReading reading) {
        if (reading.getTemperature() >= 95.0) {
            systemAlertService.create(
                    AlertType.WATER_OVERHEATED,
                    "Water temperature is dangerously high: " + reading.getTemperature() + "°C",
                    AlertSeverity.CRITICAL
            );
        }
    }

    private void checkRapidCooling(double coolingRate) {
        if (coolingRate < -RAPID_COOLING_THRESHOLD) {
            systemAlertService.create(
                    AlertType.RAPID_COOLING_DETECTED,
                    "Rapid cooling detected: " + coolingRate + "°C/min",
                    AlertSeverity.WARNING
            );
        }
    }

    private void checkAnomaly(TelemetryReading reading, double movingAvg) {
        if (isAnomaly(reading, movingAvg)) {
            systemAlertService.create(
                    AlertType.INVALID_SENSOR_VALUE,
                    "Anomalous reading detected: " + reading.getTemperature() + "°C (avg: " + movingAvg + "°C)",
                    AlertSeverity.WARNING
            );
        }
    }

    // -------------------------------------------------------------------------
    // 5. SESSION MANAGEMENT — Strategy pattern
    // -------------------------------------------------------------------------

    /**
     * Delegates session close decision to the injected SessionCloseStrategy.
     * The strategy determines whether the active session should be closed
     * based on the current reading (e.g. inactivity timeout, temperature drop).
     */
    private void checkSessionClose(TelemetryReading reading) {
        mateSessionService.findActive().ifPresent(session -> {
            if (sessionCloseStrategy.shouldClose(session, reading)) {
                mateSessionService.closeActiveSession();
            }
        });
    }

    // -------------------------------------------------------------------------
    // 6. OBSERVER — notify all registered observers
    // -------------------------------------------------------------------------

    /**
     * Notifies all registered TelemetryObserver instances with the latest reading.
     * Used to propagate live updates to the frontend and other subsystems.
     */
    private void notifyObservers(TelemetryResponseDto dto) {
        observers.forEach(observer -> observer.onTelemetryReceived(dto));
    }

    // -------------------------------------------------------------------------
    // HELPERS
    // -------------------------------------------------------------------------

    /**
     * Fetches the last N telemetry readings ordered ascending for algorithm input.
     */
    private List<TelemetryReading> fetchRecentReadings() {
        Instant from = Instant.now().minus(10, ChronoUnit.MINUTES);
        return telemetryRepository.findByTimestampBetweenOrderByTimestampAsc(from, Instant.now());
    }
}