package com.iot.services;

import com.iot.models.dto.TelemetryResponseDto;
import com.iot.models.entities.TelemetryReading;
import com.iot.models.enums.MetricType;
import com.iot.observer.TelemetrySubject;
import com.iot.repositories.TelemetryRepository;
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
 *   <li>Run the three required algorithms: moving average, cooling rate, anomaly detection</li>
 *   <li>Persist computed results as metrics via {@link MetricService}</li>
 *   <li>Delegate alert generation and live feed updates to observers via {@link TelemetrySubject}</li>
 * </ul>
 *
 * <p>This service does NOT handle alerts or live feed directly —
 * those concerns belong to the registered observers.
 */
@Service
public class ProcessingService {

    private static final int    MOVING_AVERAGE_WINDOW      = 10;
    private static final double RAPID_COOLING_THRESHOLD    = 2.0;  // °C/min
    private static final double ANOMALY_DEVIATION_THRESHOLD = 5.0; // °C from moving average

    private final TelemetryRepository telemetryRepository;
    private final MetricService metricService;
    private final TelemetrySubject telemetrySubject;
    private final EventRepository eventRepository;

    public ProcessingService(TelemetryRepository telemetryRepository,
                         MetricService metricService,
                         TelemetrySubject telemetrySubject,
                         EventRepository eventRepository) {
    this.telemetryRepository = telemetryRepository;
    this.metricService = metricService;
    this.telemetrySubject = telemetrySubject;
    this.eventRepository = eventRepository;
    }
    
    // -------------------------------------------------------------------------
    // ENTRY POINT — called by TelemetryService after each ingestion
    // -------------------------------------------------------------------------

    /**
     * Central pipeline triggered on every new telemetry reading.
     * Runs algorithms, persists metrics, then notifies all observers.
     */
    @Transactional
    public void process(TelemetryReading reading) {
        List<TelemetryReading> recentReadings = fetchRecentReadings();

        double movingAvg  = computeMovingAverage(recentReadings);
        double coolingRate = computeCoolingRate(recentReadings);
        double prediction  = estimateMinutesToTarget(reading, coolingRate);

        persistMetrics(movingAvg, coolingRate, prediction);
        computeBrewFrequency();
        // Delegate alerts + live feed to observers
        telemetrySubject.notifyAll(TelemetryResponseDto.fromEntity(reading));
    }

    // -------------------------------------------------------------------------
    // ALGORITHM 1 — Moving average
    // -------------------------------------------------------------------------

    /**
     * Computes the moving average of temperature over the last N readings.
     * Smooths sensor noise for stable frontend display.
     */
    private double computeMovingAverage(List<TelemetryReading> readings) {
        if (readings.isEmpty()) return 0.0;
        return readings.stream()
                .mapToDouble(TelemetryReading::getTemperature)
                .average()
                .orElse(0.0);
    }

    // -------------------------------------------------------------------------
    // ALGORITHM 2 — Cooling rate
    // -------------------------------------------------------------------------

    /**
     * Computes the rate of temperature change in °C/min between oldest and newest reading.
     * Negative value means cooling, positive means heating.
     */
    private double computeCoolingRate(List<TelemetryReading> readings) {
        if (readings.size() < 2) return 0.0;

        TelemetryReading oldest = readings.get(0);
        TelemetryReading newest = readings.get(readings.size() - 1);

        //cuánto cambió la temperatura
        double tempDelta    = 
            newest.getTemperature() - oldest.getTemperature();
        
        //la diferencia entre dos tiempos, convierte segundos → minutos
        double minutesDelta = 
            ChronoUnit.SECONDS.between(
            oldest.getTimestamp(), 
            newest.getTimestamp()
        ) / 60.0;

        if (minutesDelta == 0) return 0.0;
        return tempDelta / minutesDelta;
    }

    // ------------------------------------------------
    //  -------------------------
    // ALGORITHM 3 — Temperature drop prediction
    // -------------------------------------------------------------------------

    /**
     * Estimates how many minutes until temperature drops below targetTemperature.
     * Returns 0 if already below target or water is heating up.
     */
    private double estimateMinutesToTarget(TelemetryReading reading, double coolingRate) {
        if (coolingRate >= 0) return 0.0;
        double delta = reading.getTemperature() - reading.getTargetTemperature();
        if (delta <= 0) return 0.0;
        return delta / Math.abs(coolingRate);
    }

    // -------------------------------------------------------------------------
    // ALGORITHM 4 — Brew frequency
    // -------------------------------------------------------------------------

    /**
     * Counts the total number of POUR_MATE events recorded in the system.
     * Used to track overall brew activity and generate SESSION_BREW_COUNT metrics.
     */
    private void computeBrewFrequency() {
    long brewCount = eventRepository.countByType(EventType.POUR_MATE);
    metricService.save(MetricType.SESSION_BREW_COUNT, (double) brewCount, "brews");
    }

    // -------------------------------------------------------------------------
    // METRIC PERSISTENCE
    // -------------------------------------------------------------------------

    /**
     * Persists all computed algorithm outputs as metrics.
     */
    private void persistMetrics(double movingAvg, double coolingRate, double prediction) {
        metricService.save(MetricType.SESSION_AVG_TEMPERATURE, movingAvg,      "°C");
        metricService.save(MetricType.COOLING_RATE,            coolingRate,     "°C/min");
        metricService.save(MetricType.TEMP_DROP_PREDICTION,    prediction,      "min");
    }

    // -------------------------------------------------------------------------
    // HELPERS
    // -------------------------------------------------------------------------

    /**
     * Fetches telemetry readings from the last 10 minutes as algorithm input window.
     */
    private List<TelemetryReading> fetchRecentReadings() {
        Instant from = Instant.now().minus(10, ChronoUnit.MINUTES);
        return telemetryRepository.findByTimestampBetweenOrderByTimestampAsc(from, Instant.now());
    }
}