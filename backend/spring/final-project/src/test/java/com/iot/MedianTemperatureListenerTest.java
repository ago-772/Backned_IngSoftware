package com.iot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.iot.listeners.MedianTemperatureListener;
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
class MedianTemperatureListenerTest {

  // Mocks
  @Mock private TelemetryRepository telemetryRepository;
  @Mock private MetricRepository metricRepository;

  // Subject under test
  @InjectMocks private MedianTemperatureListener listener;

  // ── onSessionClosed ──────────────────────────────────────────

  /**
   * Verifies that the listener computes the correct median temperature from an odd number of
   * readings and persists a MetricEntity with the right type, value, and session ID.
   *
   * <p>With readings [10.0, 30.0, 20.0], sorted → [10.0, 20.0, 30.0], median = 20.0.
   */
  @Test
  void onSessionClosed_computesCorrectMedianForOddNumberOfReadings() {

    // Define a fake session ID and create the event that the listener will receive.
    Long sessionId = 10L;
    SessionClosedEvent event = new SessionClosedEvent(sessionId);

    // Simulate three telemetry readings — sorted they are [10, 20, 30], median is the middle value.
    List<TelemetryEntity> readings =
        List.of(
            buildReading(10.0, sessionId),
            buildReading(30.0, sessionId),
            buildReading(20.0, sessionId));

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
    assertThat(saved.getType()).isEqualTo(MetricType.MEDIAN_TEMPERATURE);
    assertThat(saved.getValue()).isEqualTo(20.0);
    assertThat(saved.getSessionId()).isEqualTo(sessionId);
  }

  /**
   * Verifies that the listener computes the correct median temperature from an even number of
   * readings by averaging the two middle values.
   *
   * <p>With readings [10.0, 40.0, 20.0, 30.0], sorted → [10.0, 20.0, 30.0, 40.0], median = (20.0 +
   * 30.0) / 2 = 25.0.
   */
  @Test
  void onSessionClosed_computesCorrectMedianForEvenNumberOfReadings() {

    // Define a fake session ID and create the event that the listener will receive.
    Long sessionId = 11L;
    SessionClosedEvent event = new SessionClosedEvent(sessionId);

    // Simulate four telemetry readings — median is the average of the two middle values.
    List<TelemetryEntity> readings =
        List.of(
            buildReading(10.0, sessionId),
            buildReading(40.0, sessionId),
            buildReading(20.0, sessionId),
            buildReading(30.0, sessionId));

    // Stub the repository to return the prepared readings for the given session.
    when(telemetryRepository.findBySessionId(sessionId)).thenReturn(readings);

    // Stub save() to return the same entity it receives, simulating real JPA behavior.
    when(metricRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    // Trigger the listener with the event — this is the action being tested.
    listener.onSessionClosed(event);

    // Capture the MetricEntity passed to save() and verify it was called exactly once.
    ArgumentCaptor<MetricEntity> captor = ArgumentCaptor.forClass(MetricEntity.class);
    verify(metricRepository, times(1)).save(captor.capture());

    // Retrieve the captured entity and assert the median equals the average of the two middle
    // values.
    MetricEntity saved = captor.getValue();
    assertThat(saved.getType()).isEqualTo(MetricType.MEDIAN_TEMPERATURE);
    assertThat(saved.getValue()).isCloseTo(25.0, offset(0.0001));
    assertThat(saved.getSessionId()).isEqualTo(sessionId);
  }

  /**
   * Verifies that the listener handles a single reading correctly, where the median equals the
   * reading's own temperature value.
   */
  @Test
  void onSessionClosed_computesMedianForSingleReading() {

    // Define a fake session ID and create the event that the listener will receive.
    Long sessionId = 12L;
    SessionClosedEvent event = new SessionClosedEvent(sessionId);

    // Stub the repository to return a single reading with temperature 55.0.
    when(telemetryRepository.findBySessionId(sessionId))
        .thenReturn(List.of(buildReading(55.0, sessionId)));

    // Stub save() to return the same entity it receives, simulating real JPA behavior.
    when(metricRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    // Trigger the listener with the event — this is the action being tested.
    listener.onSessionClosed(event);

    // Capture the saved entity and verify the median equals the single reading value.
    ArgumentCaptor<MetricEntity> captor = ArgumentCaptor.forClass(MetricEntity.class);
    verify(metricRepository).save(captor.capture());
    assertThat(captor.getValue().getValue()).isEqualTo(55.0);
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

  private TelemetryEntity buildReading(double temperature, Long sessionId) {
    MateSessionEntity session = MateSessionEntity.builder().id(sessionId).build();
    return TelemetryEntity.builder()
        .temperature(temperature)
        .waterLevel(50.0)
        .sessionId(session)
        .build();
  }
}
