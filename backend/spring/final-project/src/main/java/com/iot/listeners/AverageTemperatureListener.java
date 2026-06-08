package com.iot.listeners;

import com.iot.models.entities.MetricEntity;
import com.iot.models.entities.TelemetryEntity;
import com.iot.models.enums.MetricType;
import com.iot.observer.SessionClosedEvent;
import com.iot.observer.SessionObserver;
import com.iot.repositories.MetricRepository;
import com.iot.repositories.TelemetryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AverageTemperatureListener implements SessionObserver {

  private final TelemetryRepository telemetryRepository;
  private final MetricRepository metricRepository;

  /**
   * Called by MateSessionService when a session closes. Computes average temperature from all
   * telemetry of that session and persists it as a Metric.
   */
  @Override
  public void onSessionClosed(SessionClosedEvent event) {

    // usa el sessionId del evento para buscar las telemetrías de ESA sesión
    List<TelemetryEntity> readings = telemetryRepository.findBySessionId(event.getSessionId());

    // si no hay lecturas no hace nada
    if (readings.isEmpty()) return;

    double sum = 0.0;

    for (TelemetryEntity reading : readings) {
      sum += reading.getTemperature();
    }

    double average = sum / readings.size();

    MetricEntity metric =
        MetricEntity.builder()
            .sessionId(event.getSessionId())
            .type(MetricType.AVERAGE_TEMPERATURE)
            .value(average)
            .build();

    metricRepository.save(metric);
  }
}
