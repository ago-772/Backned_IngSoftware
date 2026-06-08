# Entities — IoT Backend

Todas las entidades están en el paquete `com.iot.models.entities` y usan **JPA + Lombok**. Ninguna debe exponerse directamente desde los controllers REST; siempre se mapean a sus DTOs correspondientes.

---

## MateSessionEntity

**Tabla:** `sessions`

Representa una sesión de uso del sistema. Es la entidad central: `TelemetryEntity` y `MetricEntity` se asocian a ella.

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | `Long` (PK, auto) | Identificador único |
| `sessionType` | `SessionType` (enum) | Estado/tipo de la sesión |
| `totalPours` | `Integer` | Total de vertidos (pulsaciones del botón) detectados |
| `timestamp` | `Instant` | Fecha de creación (auto, inmutable) |

- `sessionType` se persiste como `String` (`EnumType.STRING`).
- No retornar desde controllers — usar `EventResponseDto`.

---

## TelemetryEntity

**Tabla:** `telemetry`

Almacena lecturas periódicas de sensores asociadas a una sesión activa.

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | `Long` (PK, auto) | Identificador único |
| `sessionId` | `MateSessionEntity` (FK) | Sesión a la que pertenece (`@ManyToOne`, lazy) |
| `temperature` | `Double` | Temperatura actual del agua (°C) |
| `targetTemperature` | `Double` | Temperatura objetivo configurada |
| `waterLevel` | `Double` | Nivel de agua en porcentaje `[0, 100]` |
| `createdAt` | `Instant` | Timestamp de inserción (auto, inmutable) |

- Relación `@ManyToOne(fetch = LAZY)` con `MateSessionEntity` vía columna `session_id`.
- No retornar desde controllers — usar `TelemetryResponseDto`.

---

## MetricEntity

**Tabla:** `metrics`

Guarda métricas calculadas por los algoritmos del backend para una sesión.

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | `Integer` (PK, auto) | Identificador único |
| `sessionId` | `Long` (FK lógica) | ID de la sesión relacionada |
| `type` | `MetricType` (enum) | Tipo de métrica calculada |
| `value` | `Double` | Resultado numérico del cálculo |
| `unit` | `String` (max 20) | Unidad de medida (ej: `"Celsius/min"`, `"count"`, `"percentage"`) |
| `createdAt` | `Instant` | Timestamp de inserción (auto, inmutable) |

- `sessionId` es una FK lógica (`Long`), no una relación JPA.
- `type` se persiste como `String` (`EnumType.STRING`).
- No retornar desde controllers — usar `MetricResponseDto`.

---

## EventEntity

**Tabla:** `events`

Registra eventos discretos reportados por el dispositivo ESP32.

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | `Long` (PK, auto) | Identificador único |
| `type` | `EventType` (enum) | Tipo de evento reportado por el ESP32 |
| `timestamp` | `Instant` | Timestamp de inserción (auto, inmutable) |

- `type` se persiste como `String` (`EnumType.STRING`).
- No retornar desde controllers — usar `EventResponseDto`.

---

## Relaciones

```
MateSessionEntity (1)
    ├──< TelemetryEntity  [@ManyToOne lazy, FK: session_id]
    └──< MetricEntity     [FK lógica: session_id]

EventEntity  →  independiente (sin FK a sesión)
```

---

## Convenciones comunes

- Todas las entidades usan `@Builder`, `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor` de Lombok.
- Los campos `createdAt` / `timestamp` se generan automáticamente con `@CreationTimestamp` y son inmutables (`updatable = false`).
- Los enums se mapean siempre como `EnumType.STRING`.
- Enums relacionados: `SessionType`, `MetricType`, `EventType` — definidos en `com.iot.models.enums`.