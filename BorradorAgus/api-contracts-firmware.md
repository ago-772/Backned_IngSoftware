    Contracts .json for ESP32


-----%First State%-----
First contract for first state.
First state is that when the water began to boil

{
"temperature": 30,
"targetTemperature": 80,
"levelWater" : 70,
"typeEvent": "Starting to boil"
}

Contract that I return to ESP32 when it's starting to boil
{
"idMateSession": 105
}

-----%Second State%-----
Second state is that the water is boiling (telemetry)
{
"idMateSession": 105,
"temperature": 30,
"levelWater" : 70,
"typeEvent": "Boiling or Telemetry"
} 

Just I return 200 Ok "Keep working"

-----%Three State%-----
Three state is that the process is done or completed
{
"idMateSession": 105,
"temperature": 80,
"levelWater" : 70,
"typeEvent": "Completed"
}
