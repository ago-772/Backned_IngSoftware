# Listeners — IoT Backend

Paquete: `com.iot.listeners`

Los listeners implementan el patrón **Observer**. Cada uno escucha el evento `SessionClosedEvent` (publicado por `MateSessionService` al cerrar una sesión), calcula una métrica sobre las telemetrías de esa sesión y la persiste en la tabla `metrics`.

Todos comparten la misma estructura:
- `@Component` + `@RequiredArgsConstructor` (Lombok)
- Implementan `SessionObserver` → método `onSessionClosed(SessionClosedEvent)`
- Inyectan `TelemetryRepository` y `MetricRepository`
- Si no hay lecturas para la sesión, retornan sin hacer nada

---

## AnomalyDetectionListener

**Métrica generada:** `MetricType.ANOMALY_COUNT`

Cuenta cuántas lecturas de temperatura superaron la temperatura objetivo por más del umbral fijo.

```
anomalyCount = lecturas donde (temperature > targetTemperature + 5.0)
```

| Constante | Valor | Descripción |
|---|---|---|
| `THRESHOLD` | `5.0` | Diferencia máxima permitida entre temperatura actual y objetivo |

**Flujo:**
1. Busca todas las `TelemetryEntity` de la sesión cerrada.
2. Itera y cuenta las que superen `targetTemperature + THRESHOLD`.
3. Persiste `MetricEntity` con `type = ANOMALY_COUNT` y `value = anomalyCount`.

---

## AverageTemperatureListener

**Métrica generada:** `MetricType.AVERAGE_TEMPERATURE`

Calcula el promedio aritmético de todas las lecturas de temperatura de la sesión.

```
average = Σ(temperature) / totalReadings
```

**Flujo:**
1. Busca todas las `TelemetryEntity` de la sesión cerrada.
2. Suma todas las temperaturas y divide por la cantidad de lecturas.
3. Persiste `MetricEntity` con `type = AVERAGE_TEMPERATURE` y `value = average`.

---

## MedianTemperatureListener

**Métrica generada:** `MetricType.MEDIAN_TEMPERATURE`

Calcula la mediana de temperaturas de la sesión. Soporta cantidad par e impar de lecturas.

```
// impar → elemento del medio
// par   → promedio de los dos elementos centrales
```

**Método privado:** `computeMedian(List<TelemetryEntity>)`
1. Extrae los valores de temperatura a una `List<Double>`.
2. Ordena la lista con `Collections.sort()`.
3. Retorna el elemento central (impar) o el promedio de los dos centrales (par).

**Flujo del listener:**
1. Busca todas las `TelemetryEntity` de la sesión cerrada.
2. Llama a `computeMedian()`.
3. Persiste `MetricEntity` con `type = MEDIAN_TEMPERATURE` y `value = median`.

---

## Resumen

| Listener | Métrica | Algoritmo |
|---|---|---|
| `AnomalyDetectionListener` | `ANOMALY_COUNT` | Conteo de lecturas con desvío > 5°C sobre objetivo |
| `AverageTemperatureListener` | `AVERAGE_TEMPERATURE` | Promedio aritmético |
| `MedianTemperatureListener` | `MEDIAN_TEMPERATURE` | Mediana (con sort) |

---

## Dependencias

- `SessionObserver` / `SessionClosedEvent` → `com.iot.observer`
- `TelemetryRepository` → `com.iot.repositories`
- `MetricRepository` → `com.iot.repositories`
- `MetricType` → `com.iot.models.enums`