package com.iot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.iot.listeners.AverageTemperatureListener;
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
class AverageTemperatureListenerTest {

  // Mock del repositorio de telemetría: simula la BD devolviendo lecturas controladas
  @Mock private TelemetryRepository telemetryRepository;

  // Mock del repositorio de métricas: capturamos qué se intenta guardar
  @Mock private MetricRepository metricRepository;

  // Subject under test
  @InjectMocks private AverageTemperatureListener listener;

  // ── onSessionClosed ──────────────────────────────────────────

  /**
   * Verifies that the listener computes the correct average temperature from multiple readings and
   * persists a MetricEntity with the right type, value, and session ID.
   */
  @Test
  void onSessionClosed_Average() {

    // Define a fake session ID and create the event that the listener will receive.
    Long sessionId = 10L;
    SessionClosedEvent event = new SessionClosedEvent(sessionId);

    // Simulate three telemetry readings for the session with temperatures 60, 80, and 100.
    List<TelemetryEntity> readings =
        List.of(
            buildReading(60.0, 80.0, sessionId),
            buildReading(80.0, 80.0, sessionId),
            buildReading(70.0, 80.0, sessionId),
            buildReading(90.0, 80.0, sessionId),
            buildReading(50.0, 80.0, sessionId));

    // Stub the repository to return the prepared readings for the given session.
    when(telemetryRepository.findBySessionId(sessionId)).thenReturn(readings);

    // Stub save() to return the same entity it receives, simulating real JPA behavior.
    when(metricRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    // Act
    listener.onSessionClosed(event);

    // Capture the MetricEntity passed to save() and verify it was called exactly once.
    ArgumentCaptor<MetricEntity> captor = ArgumentCaptor.forClass(MetricEntity.class);
    verify(metricRepository, times(1)).save(captor.capture());

    // Retrieve the captured entity and assert it has the correct type, value, and session ID.
    MetricEntity saved = captor.getValue();
    assertThat(saved.getType()).isEqualTo(MetricType.AVERAGE_TEMPERATURE);
    assertThat(saved.getValue()).isEqualTo(70.0);
    assertThat(saved.getSessionId()).isEqualTo(sessionId);
  }

  /**
   * Verifies that the listener handles a single reading correctly, where the average equals the
   * reading's own temperature value.
   */
  @Test
  void onSessionClosed_computesAverageForSingleReading() {

    // Define a fake session ID and create the event that the listener will receive.
    Long sessionId = 11L;
    SessionClosedEvent event = new SessionClosedEvent(sessionId);

    // Stub the repository to return a single reading with temperature 50.0.
    when(telemetryRepository.findBySessionId(sessionId))
        .thenReturn(List.of(buildReading(50.0, 70.0, sessionId)));

    // Stub save() to return the same entity it receives, simulating real JPA behavior.
    when(metricRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    listener.onSessionClosed(event);

    // Capture the saved entity and verify the average equals the single reading value.
    ArgumentCaptor<MetricEntity> captor = ArgumentCaptor.forClass(MetricEntity.class);
    verify(metricRepository).save(captor.capture());
    assertThat(captor.getValue().getValue()).isEqualTo(50.0);
  }

  /**
   * Verifies that the listener does not persist any metric when no telemetry readings exist for the
   * session.
   */
  @Test
  void onSessionClosed_doesNothingWhenNoReadingsExist() {

    // Define a fake session ID and create the event that the listener will receive.
    Long sessionId = 12L;
    SessionClosedEvent event = new SessionClosedEvent(sessionId);

    // Stub the repository to return an empty list — no readings exist for this session.
    when(telemetryRepository.findBySessionId(sessionId)).thenReturn(List.of());

    listener.onSessionClosed(event);

    // Verify that no metric was saved when there are no readings.
    verify(metricRepository, never()).save(any());
  }

  // ── helpers ──────────────────────────────────────────────────

  private TelemetryEntity buildReading(double temperature, double targetTemp, Long sessionId) {

    MateSessionEntity session = MateSessionEntity.builder().id(sessionId).build();

    return TelemetryEntity.builder()
        .temperature(temperature)
        .targetTemperature(targetTemp)
        .waterLevel(50.0)
        .sessionId(session)
        .build();
  }
}
