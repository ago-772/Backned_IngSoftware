# Backend
## structure
```bash

src/main/java/com/iot/
├── models/
│   ├── entities/
│   │   ├── Telemetry.java       
│   │   ├── Event.java                  
│   │   └── Metric.java                 
│   │             
│   │            
│   │     
│   ├── enums/
│   │   ├── EventType.java              
│   │   └── MetricType.java             
│   │          
│   │              
│   │            
│   └── dto/
│       ├── TelemetryRequestDto.java    
│       ├── TelemetryResponseDto.java   
│       ├── EventRequestDto.java        
│       ├── EventResponseDto.java       
│       └── MetricResponseDto.java      
│       
│
├── services/
│   ├── TelemetryService.java           
│   │     create(), findAll(), findLatest()
│   │
│   ├── EventService.java               
│   │     create(), findAll(), findByType(), findLatestByType(), countByType()
│   │
│   └── MetricService.java              
│         save(), findAll(), findByType(), findLatestByType()
│
├── repositories/
│   ├── TelemetryRepository.java        
│   │     findByTimestampBetweenOrderByTimestampAsc(), findTopByOrderByTimestampDesc()
│   │
│   ├── EventRepository.java            
│   │     findByTypeOrderByTimestampDesc(), findTopByTypeOrderByTimestampDesc()
│   │     findByTimestampBetweenOrderByTimestampAsc(), countByType()
│   │
│   └── MetricRepository.java           
│         findByTypeOrderByCreatedAtDesc(), findTopByTypeOrderByCreatedAtDesc()
│         findByTypeAndCreatedAtBetweenOrderByCreatedAtAsc()
│   
│
├── controllers/
│   ├── TelemetryController.java        
│   │     POST /telemetry, GET /telemetry, GET /telemetry/latest
│   │
│   ├── EventController.java            
│   │     POST /events, GET /events, GET /events/type/{type}, GET /events/count/{type}
│   │
│   └── MetricController.java           
│         GET /metrics, GET /metrics/type/{type}, GET /metrics/latest/{type}
│
└── docs/
      └── openapi.yml  

```