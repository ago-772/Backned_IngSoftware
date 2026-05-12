TelemetryReading — telemetry_readings Muestra periódica enviada por la ESP32 cada 2-5 segundos.
id Long
temperature Double — validado [0, 100] °C
targetTemperature Double — validado [50, 90] °C
timestamp LocalDateTime — seteado por el backend

Event — events Acción discreta enviada por la ESP32 en el momento que ocurre.
idEvent Long
type EventType — POUR_MATE, HEATING_STARTED, HEATING_STOPPED, TARGET_TEMPERATURE_CHANGED, SYSTEM_STARTED, SYSTEM_STOPPED
timestamp Instant — seteado por el backend
deviceId String — identificador de la ESP32

Metric — metrics Resultado calculado por los algoritmos del backend.
id Long
type MetricType — COOLING_RATE, TEMP_DROP_PREDICTION, BREW_INTERVAL, SESSION_BREW_COUNT, SESSION_AVG_TEMPERATURE
value Double
unit String — ej. °C/min, min, brews
createdAt Instant — seteado por el backend

MateSession — mate_sessions Periodo de uso activo, deducido por el backend a partir de eventos.
id Long
startTime Instant — seteado por el backend al abrir
endTime Instant — nullable, seteado al cerrar
totalPours Integer — se incrementa con cada POUR_MATE
averageTemperature Double — nullable, calculado al cerrar
status SessionStatus — ACTIVE, CLOSED

SystemAlert — system_alerts Alerta generada por el backend cuando se detecta una condición anormal.
id Long
alertType AlertType — 11 tipos agrupados en Temperatura, Sensor, Sistema, Uso
message String — descripción legible
severity AlertSeverity — INFO, WARNING, CRITICAL
triggeredAt Instant — seteado por el backend
acknowledged Boolean — para el frontend

WaterLevelReading.java 
id Long
level Double — validado [0, 100] %
timestamp Instant
