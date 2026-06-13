package com.iot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.iot.models.dto.EventRequestDto;
import com.iot.models.dto.EventResponseDto;
import com.iot.models.entities.EventEntity;
import com.iot.models.enums.EventType;
import com.iot.repositories.EventRepository;
import com.iot.services.EventService;
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
class EventServiceTest {

  @Mock private EventRepository eventRepository;

  @InjectMocks private EventService eventService;

  private EventEntity sampleEntity;

  @BeforeEach
  void setUp() {
    sampleEntity =
        EventEntity.builder().id(1L).type(EventType.POUR_MATE).timestamp(Instant.now()).build();
  }

  // ── create ──────────────────────────────────────────────────

  @Test
  void create_persistsEntityAndReturnsDto() {
    EventRequestDto request = EventRequestDto.builder().type(EventType.POUR_MATE).build();

    when(eventRepository.save(any(EventEntity.class))).thenReturn(sampleEntity);

    EventResponseDto result = eventService.create(request);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1L);
    assertThat(result.getType()).isEqualTo(EventType.POUR_MATE);
    verify(eventRepository, times(1)).save(any(EventEntity.class));
  }

  @Test
  void create_mapsEventTypeCorrectly() {
    EventRequestDto request = EventRequestDto.builder().type(EventType.HEATING_STARTED).build();

    EventEntity heatingEntity =
        EventEntity.builder()
            .id(2L)
            .type(EventType.HEATING_STARTED)
            .timestamp(Instant.now())
            .build();

    when(eventRepository.save(any(EventEntity.class))).thenReturn(heatingEntity);

    EventResponseDto result = eventService.create(request);

    assertThat(result.getType()).isEqualTo(EventType.HEATING_STARTED);
  }

  // ── findAll ─────────────────────────────────────────────────

  @Test
  void findAll_returnsAllEventsMappedToDtos() {
    EventEntity second =
        EventEntity.builder()
            .id(2L)
            .type(EventType.HEATING_STOPPED)
            .timestamp(Instant.now())
            .build();

    when(eventRepository.findAll(any(Sort.class))).thenReturn(List.of(sampleEntity, second));

    List<EventResponseDto> result = eventService.findAll();

    assertThat(result).hasSize(2);
    assertThat(result.get(0).getType()).isEqualTo(EventType.POUR_MATE);
    assertThat(result.get(1).getType()).isEqualTo(EventType.HEATING_STOPPED);
  }

  @Test
  void findAll_returnsEmptyListWhenNoEventsExist() {
    when(eventRepository.findAll(any(Sort.class))).thenReturn(List.of());

    List<EventResponseDto> result = eventService.findAll();

    assertThat(result).isEmpty();
  }

  // ── findLatest ──────────────────────────────────────────────

  @Test
  void findLatest_returnsDtoWhenEventExists() {
    when(eventRepository.findTopByOrderByTimestampDesc()).thenReturn(Optional.of(sampleEntity));

    EventResponseDto result = eventService.findLatest();

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1L);
    assertThat(result.getType()).isEqualTo(EventType.POUR_MATE);
  }

  @Test
  void findLatest_throwsRuntimeExceptionWhenNoEventsExist() {
    when(eventRepository.findTopByOrderByTimestampDesc()).thenReturn(Optional.empty());

    assertThatThrownBy(() -> eventService.findLatest())
        .isInstanceOf(RuntimeException.class)
        .hasMessage("No events found");
  }
}
