# Controladores (controllers/)

## TelemetryController 
Encargado de manejar los datos de telemetría (temperatura). 
- **POST /api/telemetry** — recibe datos desde ESP32
- **GET /api/telemetry**  — Devuelve el historial de telemetría. 
- **GET /api/telemetry/latest**  — Devuelve la lectura más reciente 

## EventController
Encargado de manejar los eventos del dispositivo 
- **POST /api/events** — recibe eventos desde ESP32
- **GET /api/events** — devuelve el historial de eventos
- **GET /api/events/latest** — devuelve el evento más reciente

