package com.iot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.iot.listeners.AnomalyDetectionListener;
import com.iot.models.entities.MateSessionEntity;
import com.iot.models.entities.MetricEntity;
import com.iot.models.entities.TelemetryEntity;
import com.iot.models.enums.MetricType;
import com.iot.observer.SessionClosedEvent;
import com.iot.repositories.MetricRepository;
import com.iot.repositories.TelemetryRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AnomalyDetectionListenerTest {

  // Mocks
  @Mock private TelemetryRepository telemetryRepository;
  @Mock private MetricRepository metricRepository;

  // Subject under test
  @InjectMocks private AnomalyDetectionListener listener;

  // ── onSessionClosed ──────────────────────────────────────────

  /**
   * Verifies that the listener counts only the readings where temperature exceeds targetTemperature
   * by more than 5.0 (the configured threshold).
   *
   * <p>Reading 1: 90.0 > 80.0 + 5.0 = 85.0 → anomaly Reading 2: 84.0 > 80.0 + 5.0 = 85.0 → NOT
   * anomaly (equal is not enough) Reading 3: 70.0 > 80.0 + 5.0 = 85.0 → NOT anomaly Expected
   * anomaly count: 1
   */
  @Test
  void onSessionClosed_countsOnlyReadingsThatExceedThreshold() {

    // Define a fake session ID and create the event that the listener will receive.
    Long sessionId = 10L;
    SessionClosedEvent event = new SessionClosedEvent(sessionId);

    // Simulate three readings: one anomaly, one on the boundary, one normal.
    List<TelemetryEntity> readings =
        List.of(
        buildReading(90.0, 80.0, sessionId),  // anomalía
        buildReading(84.0, 80.0, sessionId),  // límite exacto → NO anomalía
        buildReading(70.0, 80.0, sessionId),  // normal
        buildReading(75.0, 70.0, sessionId), // límite exacto con target distinto → NO anomalía
        buildReading(75.1, 70.0, sessionId)); // anomalía


    // Stub the repository to return the prepared readings for the given session.
    when(telemetryRepository.findBySessionId(sessionId)).thenReturn(readings);

    // Stub save() to return the same entity it receives, simulating real JPA behavior.
    when(metricRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    // Trigger the listener with the event — this is the action being tested.
    listener.onSessionClosed(event);

    // Capture the MetricEntity passed to save() and verify it was called exactly once.
    ArgumentCaptor<MetricEntity> captor = ArgumentCaptor.forClass(MetricEntity.class);
    verify(metricRepository, times(1)).save(captor.capture());

    // Retrieve the captured entity and assert it has the correct type, value, and session ID.
    MetricEntity saved = captor.getValue();
    assertThat(saved.getType()).isEqualTo(MetricType.ANOMALY_COUNT);
    assertThat(saved.getValue()).isEqualTo(2.0);
    assertThat(saved.getSessionId()).isEqualTo(sessionId);
  }

  /**
   * Verifies that the listener persists zero anomalies when all readings are within the allowed
   * threshold.
   */
  @Test
  void onSessionClosed_savesZeroAnomalies_whenNoReadingsExceedThreshold() {

    // Define a fake session ID and create the event that the listener will receive.
    Long sessionId = 11L;
    SessionClosedEvent event = new SessionClosedEvent(sessionId);

    // Simulate readings where no temperature exceeds targetTemperature + 5.0.
    List<TelemetryEntity> readings =
        List.of(
            buildReading(80.0, 80.0, sessionId),
            buildReading(75.0, 80.0, sessionId),
            buildReading(60.0, 80.0, sessionId));

    // Stub the repository to return the prepared readings for the given session.
    when(telemetryRepository.findBySessionId(sessionId)).thenReturn(readings);

    // Stub save() to return the same entity it receives, simulating real JPA behavior.
    when(metricRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    // Trigger the listener with the event — this is the action being tested.
    listener.onSessionClosed(event);

    // Capture the saved entity and verify the anomaly count is zero.
    ArgumentCaptor<MetricEntity> captor = ArgumentCaptor.forClass(MetricEntity.class);
    verify(metricRepository).save(captor.capture());
    assertThat(captor.getValue().getValue()).isEqualTo(0.0);
  }

  /**
   * Verifies that the listener counts all readings as anomalies when every reading exceeds the
   * threshold.
   */
  @Test
  void onSessionClosed_countsAllReadings_whenAllExceedThreshold() {

    // Define a fake session ID and create the event that the listener will receive.
    Long sessionId = 12L;
    SessionClosedEvent event = new SessionClosedEvent(sessionId);

    // Simulate readings where every temperature exceeds targetTemperature + 5.0.
    List<TelemetryEntity> readings =
        List.of(
            buildReading(100.0, 80.0, sessionId),
            buildReading(95.0, 80.0, sessionId),
            buildReading(90.0, 80.0, sessionId));

    // Stub the repository to return the prepared readings for the given session.
    when(telemetryRepository.findBySessionId(sessionId)).thenReturn(readings);

    // Stub save() to return the same entity it receives, simulating real JPA behavior.
    when(metricRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    // Trigger the listener with the event — this is the action being tested.
    listener.onSessionClosed(event);

    // Capture the saved entity and verify all readings were counted as anomalies.
    ArgumentCaptor<MetricEntity> captor = ArgumentCaptor.forClass(MetricEntity.class);
    verify(metricRepository).save(captor.capture());
    assertThat(captor.getValue().getValue()).isEqualTo(3.0);
  }

  /**
   * Verifies that the listener does not persist any metric when no telemetry readings exist for the
   * session.
   */
  @Test
  void onSessionClosed_doesNothingWhenNoReadingsExist() {

    // Define a fake session ID and create the event that the listener will receive.
    Long sessionId = 13L;
    SessionClosedEvent event = new SessionClosedEvent(sessionId);

    // Stub the repository to return an empty list — no readings exist for this session.
    when(telemetryRepository.findBySessionId(sessionId)).thenReturn(List.of());

    // Trigger the listener with the event — this is the action being tested.
    listener.onSessionClosed(event);

    // Verify that no metric was saved when there are no readings.
    verify(metricRepository, never()).save(any());
  }

  // ── helpers ──────────────────────────────────────────────────

  private TelemetryEntity buildReading(
      double temperature, double targetTemperature, Long sessionId) {
    MateSessionEntity session = MateSessionEntity.builder().id(sessionId).build();
    return TelemetryEntity.builder()
        .temperature(temperature)
        .targetTemperature(targetTemperature)
        .waterLevel(50.0)
        .sessionId(session)
        .build();
  }
}
