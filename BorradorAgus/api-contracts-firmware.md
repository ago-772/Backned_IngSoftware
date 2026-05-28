    Contracts .json except for ESP32

First contract for first stage. 
First state is that the water began to boil

{
    "idMateSession": "mate-session-01",
    "temperature": "30",
    "targetTemperature": "80",
    "levelWater" : "70%",
    "typeEvent": "Starting to boil",
    "timestamp": "2026-05-27 12:26:00"
}

Second state is that the water is boiling (telemetry)
 {
    "idMateSession": "mate-session-01",
    "temperature": "30",
    "levelWater" : "70%",
    "typeEvent": "Boiling",
    "timestamp": "2026-05-27 12:26:00"
 }
 
Three state is that the process is done or completed
{
    "idMateSession": "mate-session-01",
    "temperature": "80",
    "levelWater" : "70%",
    "typeEvent": "Completed",
    "timestamp": "2026-05-27 12:26:00"
}