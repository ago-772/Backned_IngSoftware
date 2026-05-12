package com.iot.observer;

import com.iot.models.dto.TelemetryResponseDto;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Subject that holds and notifies all registered TelemetryObserver instances.
 *
 * <p>Spring auto-injects every @Component that implements TelemetryObserver.
 * ProcessingService calls notifyAll() after each telemetry ingestion.
 */
@Component
public class TelemetrySubject {

    private final List<TelemetryObserver> observers;

    public TelemetrySubject(List<TelemetryObserver> observers) {
        this.observers = observers;
    }

    /**
     * Notifies all registered observers with the latest telemetry reading.
     */
    public void notifyAll(TelemetryResponseDto telemetry) {
        observers.forEach(observer -> observer.onTelemetryReceived(telemetry));
    }
}