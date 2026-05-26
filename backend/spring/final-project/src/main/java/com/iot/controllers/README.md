# Controladores (controllers/)

## TelemetryController 
Encargado de manejar los datos de telemetría (temperatura). 
- **POST /api/telemetry** — recibe datos desde ESP32
- **GET /api/telemetry**  — Devuelve el historial de telemetría. 
- **GET /api/telemetry/live**  — Devuelve el último dato registrado (tiempo real) 

## EventController
Encargado de manejar los eventos del dispositivo 
- **POST /api/events** — recibe eventos desde ESP32
- **GET /api/events**  — historial de eventos para frontend

## MetricsController
Encargado de exponer métricas del sistema. 
- **GET /api/metrics/cooling-curve**  — curva de enfriamiento
- **GET /api/metrics/frequency**  — frecuencia de cebados
