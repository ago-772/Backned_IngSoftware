package com.iot;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.models.dto.TelemetryRequestDto;
import com.iot.models.dto.TelemetryResponseDto;
import com.iot.services.TelemetryService;
import com.iot.controllers.TelemetryController;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TelemetryController.class)
class TelemetryControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private TelemetryService telemetryService;

  // ── POST /telemetry ───────────────────────────────────────────────────────

  @Test
  void create_validPayload_returns201() throws Exception {
    TelemetryRequestDto request =
        TelemetryRequestDto.builder()
            .temperature(75.0)
            .targetTemperature(80.0)
            .waterLevel(60.0)
            .build();

    TelemetryResponseDto response =
        TelemetryResponseDto.builder()
            .id(1L)
            .temperature(75.0)
            .targetTemperature(80.0)
            .waterLevel(60.0)
            .createdAt(Instant.now())
            .sessionId(10L)
            .build();

    when(telemetryService.create(any(TelemetryRequestDto.class))).thenReturn(response);

    mockMvc
        .perform(
            post("/telemetry")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.temperature").value(75.0))
        .andExpect(jsonPath("$.waterLevel").value(60.0))
        .andExpect(jsonPath("$.sessionId").value(10));
  }

  @Test
  void create_missingTemperature_returns400() throws Exception {
    // temperature is @NotNull
    String payload = """
        {"targetTemperature": 80.0, "waterLevel": 50.0}
        """;

    mockMvc
        .perform(post("/telemetry").contentType(MediaType.APPLICATION_JSON).content(payload))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_temperatureAboveMax_returns400() throws Exception {
    // temperature @DecimalMax(100.0)
    String payload = """
        {"temperature": 101.0, "targetTemperature": 80.0, "waterLevel": 50.0}
        """;

    mockMvc
        .perform(post("/telemetry").contentType(MediaType.APPLICATION_JSON).content(payload))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_temperatureBelowMin_returns400() throws Exception {
    // temperature @DecimalMin(0.0)
    String payload = """
        {"temperature": -1.0, "targetTemperature": 80.0, "waterLevel": 50.0}
        """;

    mockMvc
        .perform(post("/telemetry").contentType(MediaType.APPLICATION_JSON).content(payload))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_targetTemperatureOutOfRange_returns400() throws Exception {
    // targetTemperature @DecimalMin(10.0) @DecimalMax(90.0)
    String payload = """
        {"temperature": 50.0, "targetTemperature": 5.0, "waterLevel": 50.0}
        """;

    mockMvc
        .perform(post("/telemetry").contentType(MediaType.APPLICATION_JSON).content(payload))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_missingWaterLevel_returns400() throws Exception {
    String payload = """
        {"temperature": 50.0, "targetTemperature": 70.0}
        """;

    mockMvc
        .perform(post("/telemetry").contentType(MediaType.APPLICATION_JSON).content(payload))
        .andExpect(status().isBadRequest());
  }

  // ── GET /telemetry ────────────────────────────────────────────────────────

  @Test
  void getAll_returnsList() throws Exception {
    List<TelemetryResponseDto> list =
        List.of(
            TelemetryResponseDto.builder()
                .id(2L)
                .temperature(80.0)
                .targetTemperature(82.0)
                .waterLevel(55.0)
                .createdAt(Instant.now())
                .sessionId(1L)
                .build(),
            TelemetryResponseDto.builder()
                .id(1L)
                .temperature(70.0)
                .targetTemperature(80.0)
                .waterLevel(70.0)
                .createdAt(Instant.now())
                .sessionId(1L)
                .build());

    when(telemetryService.findAll()).thenReturn(list);

    mockMvc
        .perform(get("/telemetry"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].id").value(2))
        .andExpect(jsonPath("$[1].id").value(1));
  }

  @Test
  void getAll_whenEmpty_returnsEmptyArray() throws Exception {
    when(telemetryService.findAll()).thenReturn(List.of());

    mockMvc
        .perform(get("/telemetry"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));
  }

  // ── GET /telemetry/latest ─────────────────────────────────────────────────

  @Test
  void getLatest_whenExists_returns200() throws Exception {
    TelemetryResponseDto response =
        TelemetryResponseDto.builder()
            .id(3L)
            .temperature(78.5)
            .targetTemperature(80.0)
            .waterLevel(45.0)
            .createdAt(Instant.now())
            .sessionId(2L)
            .build();

    when(telemetryService.findLatest()).thenReturn(Optional.of(response));

    mockMvc
        .perform(get("/telemetry/latest"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(3))
        .andExpect(jsonPath("$.temperature").value(78.5));
  }

  @Test
  void getLatest_whenNone_returns404() throws Exception {
    when(telemetryService.findLatest()).thenReturn(Optional.empty());

    mockMvc.perform(get("/telemetry/latest")).andExpect(status().isNotFound());
  }
}