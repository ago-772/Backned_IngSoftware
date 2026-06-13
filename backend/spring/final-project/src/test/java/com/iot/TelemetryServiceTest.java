package com.iot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.iot.models.dto.TelemetryRequestDto;
import com.iot.models.dto.TelemetryResponseDto;
import com.iot.models.entities.MateSessionEntity;
import com.iot.models.entities.TelemetryEntity;
import com.iot.models.enums.SessionType;
import com.iot.repositories.MateSessionRepository;
import com.iot.repositories.TelemetryRepository;
import com.iot.services.TelemetryService;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class TelemetryServiceTest {

  @Mock private TelemetryRepository telemetryRepository;
  @Mock private MateSessionRepository mateSessionRepository;

  @InjectMocks private TelemetryService telemetryService;

  private TelemetryEntity entity;
  private TelemetryRequestDto request;

  // ── fixtures ──────────────────────────────────────────────────────────

  @BeforeEach
  void setUp() {
    MateSessionEntity session = MateSessionEntity.builder().id(1L).build();

    entity =
        TelemetryEntity.builder()
            .id(1L)
            .temperature(75.0)
            .targetTemperature(80.0)
            .waterLevel(60.0)
            .createdAt(Instant.now())
            .sessionId(session)
            .build();

    request =
        TelemetryRequestDto.builder()
            .temperature(75.0)
            .targetTemperature(80.0)
            .waterLevel(60.0)
            .build();
  }

  // ── create() ──────────────────────────────────────────────────

  // Verifies that all telemetry readings are retrieved and mapped to DTOs.
  @Test
  void create_persistsEntityAndReturnsDto() {

    MateSessionEntity session = MateSessionEntity.builder().id(1L).build();
    when(mateSessionRepository.findFirstBySessionType(SessionType.SYSTEM_STARTED))
        .thenReturn(Optional.of(session));

    when(telemetryRepository.save(any(TelemetryEntity.class))).thenReturn(entity);

    TelemetryResponseDto result = telemetryService.create(request);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1L);
    assertThat(result.getTemperature()).isEqualTo(75.0);
    assertThat(result.getTargetTemperature()).isEqualTo(80.0);
    assertThat(result.getWaterLevel()).isEqualTo(60.0);
    verify(telemetryRepository, times(1)).save(any(TelemetryEntity.class));
  }

  // ── findAll() ─────────────────────────────────────────────────

  // Verifies that the latest telemetry reading is returned when data exists.
  @Test
  void findAll_returnsAllReadingsMappedToDtos() {
    TelemetryEntity second =
        TelemetryEntity.builder()
            .id(2L)
            .temperature(60.0)
            .targetTemperature(80.0)
            .waterLevel(50.0)
            .createdAt(Instant.now())
            .sessionId(MateSessionEntity.builder().id(1L).build())
            .build();

    when(telemetryRepository.findAll(any(Sort.class))).thenReturn(List.of(entity, second));

    List<TelemetryResponseDto> result = telemetryService.findAll();

    assertThat(result).hasSize(2);
    assertThat(result.get(0).getId()).isEqualTo(1L);
    assertThat(result.get(1).getId()).isEqualTo(2L);
  }

  // Verifies that an empty list is returned when no telemetry readings exist.
  @Test
  void findAll_returnsEmptyListWhenNoReadingsExist() {
    when(telemetryRepository.findAll(any(Sort.class))).thenReturn(List.of());

    List<TelemetryResponseDto> result = telemetryService.findAll();

    assertThat(result).isEmpty();
  }

  // ── findLatest() ──────────────────────────────────────────────

  // Verifies that the latest telemetry reading is returned when data exists.
  @Test
  void findLatest_returnsDtoWhenReadingExists() {
    when(telemetryRepository.findTopByOrderByCreatedAtDesc()).thenReturn(Optional.of(entity));

    Optional<TelemetryResponseDto> result = telemetryService.findLatest();

    assertThat(result).isPresent();
    assertThat(result.get().getId()).isEqualTo(1L);
  }

  // Verifies that an empty Optional is returned when no telemetry readings exist.
  @Test
  void findLatest_returnsEmptyOptionalWhenNoReadingsExist() {
    when(telemetryRepository.findTopByOrderByCreatedAtDesc()).thenReturn(Optional.empty());

    Optional<TelemetryResponseDto> result = telemetryService.findLatest();

    assertThat(result).isEmpty();
  }
}
