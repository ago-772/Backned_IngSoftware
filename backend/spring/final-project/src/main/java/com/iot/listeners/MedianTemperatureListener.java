package com.iot.listeners;

import com.iot.models.entities.MetricEntity;
import com.iot.models.entities.TelemetryEntity;
import com.iot.models.enums.MetricType;
import com.iot.observer.SessionClosedEvent;
import com.iot.observer.SessionObserver;
import com.iot.repositories.MetricRepository;
import com.iot.repositories.TelemetryRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Observer that reacts to a closed session and computes the median temperature from all telemetry
 * readings recorded during that session.
 */
@Component
@RequiredArgsConstructor
public class MedianTemperatureListener implements SessionObserver {

  private final TelemetryRepository telemetryRepository;
  private final MetricRepository metricRepository;

  @Override
  public void onSessionClosed(SessionClosedEvent event) {
    List<TelemetryEntity> readings = telemetryRepository.findBySessionId(event.getSessionId());

    if (readings.isEmpty()) return;

    double median = computeMedian(readings);

    MetricEntity metric =
        MetricEntity.builder()
            .sessionId(event.getSessionId())
            .type(MetricType.MEDIAN_TEMPERATURE)
            .value(median)
            .build();

    metricRepository.save(metric);
  }

  /**
   * Computes the median temperature from a list of telemetry readings. Sorts values and picks the
   * middle element, or averages the two middle elements if the list has an even number of readings.
   */
  private double computeMedian(List<TelemetryEntity> readings) {
    List<Double> temps = new ArrayList<>();

    for (TelemetryEntity reading : readings) {
      temps.add(reading.getTemperature());
    }

    Collections.sort(temps);

    int size = temps.size();
    int middle = size / 2;

    if (size % 2 == 0) {
      return (temps.get(middle - 1) + temps.get(middle)) / 2.0;
    } else {
      return temps.get(middle);
    }
  }
}
