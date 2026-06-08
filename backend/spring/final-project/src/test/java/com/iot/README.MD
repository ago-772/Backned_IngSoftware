## Tests Unitarios – TelemetryService

Se implementaron pruebas unitarias utilizando **JUnit 5**, **Mockito** y **AssertJ** para validar el comportamiento de `TelemetryService`, `EventService`simulando el repositorio para aislar la lógica de negocio.

### Casos probados

* **create_persistsEntityAndReturnsDto**

  * Verifica que una telemetría se guarde correctamente y que se devuelva el DTO correspondiente.

* **findAll_returnsAllReadingsMappedToDtos**

  * Verifica que todas las telemetrías recuperadas del repositorio sean devueltas correctamente como DTOs.

* **findAll_returnsEmptyListWhenNoReadingsExist**

  * Verifica que se devuelva una lista vacía cuando no existen registros.

* **findLatest_returnsDtoWhenReadingExists**

  * Verifica que se obtenga correctamente la última telemetría registrada.

* **findLatest_returnsEmptyOptionalWhenNoReadingsExist**

  * Verifica que se devuelva un `Optional` vacío cuando no existen telemetrías.

## Tests Unitarios – TelemetryService

### Casos probados

* **create_persistsEntityAndReturnsDto**
  * Verifica que un evento se persista correctamente y que el DTO devuelto contenga la información esperada.

* **findAll_returnsAllEventsMappedToDtos**
  * Verifica que todos los eventos recuperados del repositorio sean transformados correctamente a DTOs.

* **findAll_returnsEmptyListWhenNoEventsExist**
  * Verifica que se devuelva una lista vacía cuando no existen eventos almacenados.

* **findLatest_returnsDtoWhenEventExists**
  * Verifica que se obtenga correctamente el evento más reciente cuando existen registros.

* **findLatest_throwsRuntimeExceptionWhenNoEventsExist**
  * Verifica que se lance una excepción con el mensaje correspondiente cuando no existen eventos registrados.

