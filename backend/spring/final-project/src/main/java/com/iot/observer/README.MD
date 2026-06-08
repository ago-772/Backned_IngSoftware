## Patrón Observer

Se implementó el patrón de diseño **Observer** para desacoplar los procesos que deben ejecutarse cuando una sesión de mate finaliza.

Al cerrarse una sesión de mate, el sistema genera un evento que contiene el identificador de la sesión finalizada (`sessionId`) y notifica automáticamente a todos los observadores registrados.

Cada observador recibe este evento y ejecuta el cálculo de las métricas que le corresponden utilizando las telemetrías asociadas a la sesión, reaccionando de forma independiente con su propia lógica:

`AverageTemperatureListener` busca todas las telemetrías asociadas a la sesión y calcula la temperatura promedio.

`AnomalyDetectionListener` recorre las mismas telemetrías y cuenta cuántas lecturas superaron la temperatura objetivo más un umbral definido

---
### Componentes

🔹  **SessionObserver** (Observer)

Interfaz que define el contrato que deben implementar todas las clases que deseen reaccionar al cierre de una sesión.

🔹 **SessionClosedEvent** (Event)

Clase utilizada para transportar la información del evento. Actualmente contiene el identificador (`sessionId`) de la sesión que fue cerrada.

Su responsabilidad es únicamente transportar datos entre el Subject y los Observers.

🔹 **MateSessionService** (Concrete Subject)

Actúa como el **Subject** del patrón.

Responsabilidades:

- Mantener la lista de observadores registrados.
- Permitir la suscripción y desuscripción de observadores.
- Notificar a todos los observadores cuando una sesión se cierra.

🔹 **AverageTemperatureListener** (Concrete Observer)

Implementa la interfaz `SessionObserver`.

Cuando recibe la notificación:

1. Obtiene el `sessionId` desde el evento.
2. Recupera las telemetrías asociadas a la sesión desde la base de datos.
3. Calcula la temperatura promedio de las lecturas.
4. Persiste el resultado como una métrica de tipo `AVERAGE_TEMPERATURE`.

### Diagrama de clases simplificado

```text
ESP32 → POST /sessions { sessionType: SYSTEM_STOPPED }
  ▼
MateSessionService.create()
  ├── guarda la sesión en PostgreSQL
  └── notifyObservers(new SessionClosedEvent(sessionId))
        ▼
        AverageTemperatureListener.onSessionClosed(event)
          ├── busca telemetrías por sessionId
          ├── calcula promedio
          └── guarda MetricEntity en PostgreSQL


«interface»
SessionObserver
└── onSessionClosed(event)
        ▲
        │ implements
        │
AverageTemperatureListener          
└── onSessionClosed(event)         
      1. busca telemetrías           
      2. calcula promedio       
      3. guarda Metric               
                                                                     

 MateSessionService (Subject)
├── observers: List<SessionObserver> 
├── subscribe()
├── subscribe()
├── unsubscribe()
└── notifyObservers()
      └── llama a onSessionClosed()
                en cada observer
```