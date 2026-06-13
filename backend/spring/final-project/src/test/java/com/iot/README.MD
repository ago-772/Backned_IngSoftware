# Unit Tests — Listener de Métricas

Tests unitarios para los tres listeners del patrón Observer del proyecto **Calentador de Mate**.
Cada listener se activa cuando se cierra una sesión (`SessionClosedEvent`) y persiste una métrica calculada.

---

## Stack

- **JUnit 5** — framework de testing
- **Mockito** — mocks e inyección de dependencias
- **AssertJ** — assertions expresivas

---

## Dependencias mockeadas

Todos los tests comparten la misma estructura de mocks:

| Mock | Rol |
|------|-----|
| `TelemetryRepository` | Devuelve lecturas controladas sin tocar la BD real |
| `MetricRepository` | Permite verificar qué métrica se intentó guardar |

---

## Patrón común de cada test

```
1. Crear sessionId y SessionClosedEvent
2. Stubear telemetryRepository.findBySessionId() con lecturas preparadas
3. Stubear metricRepository.save() para simular comportamiento JPA real
4. Disparar listener.onSessionClosed(event)
5. Capturar con ArgumentCaptor lo que se pasó a save()
6. Verificar type, value y sessionId de la MetricEntity guardada
```

---

## AverageTemperatureListenerTest

Calcula el **promedio** de todas las temperaturas de la sesión.

| Test | Descripción | Resultado esperado |
|------|-------------|-------------------|
| `onSessionClosed_Average` | lecturas: 60, 80, 70, 90, 50 | `value = 70.0`, `type = AVERAGE_TEMPERATURE` |
| `onSessionClosed_computesAverageForSingleReading` | Una sola lectura: 50 | `value = 50.0` |
| `onSessionClosed_doesNothingWhenNoReadingsExist` | Lista vacía | `save()` nunca se llama |

**Caso borde cubierto:** lista vacía → no se guarda nada (evita división por cero).

---

## AnomalyDetectionListenerTest

Cuenta cuántas lecturas superan `targetTemperature + 5.0` (threshold fijo).

| Test | Descripción | Resultado esperado |
|------|-------------|-------------------|
| `onSessionClosed_countsOnlyReadingsThatExceedThreshold` | 90 > 85 ✓, 84 = 85 ✗, 70 < 85 ✗, 75 = 75 ✗, 75.1 > 75 ✓| `value = 1.0`, `type = ANOMALY_COUNT` |
| `onSessionClosed_savesZeroAnomalies_whenNoReadingsExceedThreshold` | Todas dentro del umbral | `value = 0.0` |
| `onSessionClosed_countsAllReadings_whenAllExceedThreshold` | 100, 95, 90 — todas superan | `value = 3.0` |
| `onSessionClosed_doesNothingWhenNoReadingsExist` | Lista vacía | `save()` nunca se llama |

**Caso borde cubierto:** límite exacto (`temperatura == target + 5.0`) **no** cuenta como anomalía — solo `>`, no `>=`.

---

## MedianTemperatureListenerTest

Calcula la **mediana** de las temperaturas. Si hay cantidad par de lecturas, promedia los dos valores del medio.

| Test | Descripción | Resultado esperado |
|------|-------------|-------------------|
| `onSessionClosed_computesCorrectMedianForOddNumberOfReadings` | [10, 30, 20] → sorted [10, 20, 30] | `value = 20.0`, `type = MEDIAN_TEMPERATURE` |
| `onSessionClosed_computesCorrectMedianForEvenNumberOfReadings` | [10, 40, 20, 30] → sorted [10, 20, 30, 40] | `value = 25.0` (promedio de 20 y 30) |
| `onSessionClosed_computesMedianForSingleReading` | Una sola lectura: 55 | `value = 55.0` |
| `onSessionClosed_doesNothingWhenNoReadingsExist` | Lista vacía | `save()` nunca se llama |

**Nota:** el test de cantidad par usa `isCloseTo(25.0, offset(0.0001))` en lugar de `isEqualTo` para evitar errores de precisión de punto flotante.

---

## Decisiones de diseño

**`ArgumentCaptor` vs `argThat`**

Se usa `ArgumentCaptor` para capturar la `MetricEntity` guardada y hacer múltiples asserts separados sobre ella (`type`, `value`, `sessionId`). Es más legible que un único lambda con varias condiciones encadenadas.

**`sessionId` variable por test**

Cada test usa un `sessionId` distinto (10L, 11L, 12L...) para que el stub de `findBySessionId` sea preciso y no haya colisiones entre tests. Por eso el evento se crea localmente en cada test en lugar de en un `@BeforeEach`.

**`buildReading()` helper**

Cada clase de test tiene su propio helper para construir `TelemetryEntity`. Los parámetros expuestos varían según lo que cada listener necesita testear:
- `AverageTemperatureListener` → recibe `temperature`, `targetTemp`, `sessionId`
- `MedianTemperatureListener` → recibe solo `temperature` y `sessionId` (targetTemp no es relevante)
- `AnomalyDetectionListener` → recibe `temperature`, `targetTemperature`, `sessionId` (ambos son clave para el threshold)