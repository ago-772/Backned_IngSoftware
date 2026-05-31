# Épicas e Historias de Usuario

**DemandadeNintendo** — Ingeniería de Software · FCEFyN · UNC  
**Proyecto:** Sistema de Monitoreo de Depósito de Farmacia  
**Fecha:** 2026-05-27  
**Versión:** 1.0

---

## Épicas

| Épica | Color | Descripción | Historias |
|---|---|---|---|
| **Control de Acceso — Ingreso** | 🔷 | Métodos de autenticación para ingresar al depósito (PIN y RFID) | HU-001, HU-002, HU-003, HU-00x |
| **Seguridad** | 🔶 | Sensores perimetrales, detección de intrusiones y sistema de alarmas | HU-00x |
| **Interfaz de Operador** | 🟢 | Dashboard con plano esquemático en vivo, historiales y gestión del sistema | HU-00x |

> **Nota sobre numeración:** Cada historia reinicia su numeración de sub-tareas desde ST-001. Para dependencias entre historias se usa la notación `HU-NNN / ST-NNN`.

---

## HU-001: Validación Básica de PIN en el Portón

**ID:** HU-001  
**Épica:** Control de Acceso — Ingreso  
**RF base:** RF-01.1, RF-01.2, RF-01.6, RF-08.1, RF-08.2  
**Story Points:** 26  
**Depende de:** Ninguna (historia inicial)

### Descripción

Como **operador del depósito**, quiero **ingresar un PIN de 4 dígitos en el teclado matricial del portón de carga/descarga** para **acceder al depósito cuando estoy autorizado**.

### Criterios de Aceptación

| ID | Criterio | RF |
|---|---|---|
| AC-01 | El teclado matricial captura 4 dígitos y los envía al backend para validación | RF-01.1 |
| AC-02 | Si el PIN es correcto, el backend responde OK, el LED pasa a **verde fijo** y el actuador desbloquea el portón | RF-01.1 |
| AC-03 | Si el PIN es incorrecto, el LED titila en **rojo 3 segundos** y el portón permanece cerrado | RF-01.1 |
| AC-04 | El sensor magnético del portón reporta si está **abierto** o **cerrado** en cada ciclo de lectura | RF-01.2 |
| AC-05 | Una vez abierto el portón, el sistema permanece desbloqueado hasta que el sensor detecte el cierre | RF-01.6 |
| AC-06 | Todo intento de ingreso (exitoso o fallido) se persiste en PostgreSQL con timestamp | RF-08.1, RF-08.2 |

### Sub-tareas

#### 🔙 Backend (5)

| # | Sub-tarea | Depende de | SP | Descripción |
|---|---|---|---|---|
| ST-001 | Entity `PinCredential` | — | 3 | Crear entidad JPA con campos: hash, salt, activo, fechaCreacion, ultimaModificacion |
| ST-002 | Repository `PinCredentialRepository` | ST-001 | 2 | Spring Data JPA con findByActivo, save, delete |
| ST-003 | `PinValidationService` | ST-001, ST-002 | 3 | Validar PIN contra hash BCrypt + verificar estado activo del PIN |
| ST-004 | Endpoint `POST /api/pin/validate` | ST-003 | 3 | Recibe PIN del firmware, delega en PinValidationService, devuelve resultado + HTTP status |
| ST-005 | Entity + Repo `AccessEvent` | — | 3 | Persistir eventos de acceso con timestamp, método (PIN/RFID), éxito/fallo, identidad |

#### 📟 Firmware (5)

| # | Sub-tarea | Depende de | SP | Descripción |
|---|---|---|---|---|
| ST-006 | Driver teclado matricial 4×4 | — | 3 | Lectura de 4 dígitos individuales + tecla de confirmación (#) + tecla de borrado (*) |
| ST-007 | HTTP POST a `POST /api/pin/validate` | ST-004, ST-006 | 3 | Enviar PIN al backend, parsear respuesta JSON, manejar errores de conexión |
| ST-008 | Control LED RGB básico | ST-007 | 2 | Encender verde (PIN OK) / rojo 3s (PIN fallido) |
| ST-009 | Lectura sensor magnético | — | 2 | Leer estado del sensor del portón: HIGH = cerrado, LOW = abierto |
| ST-010 | Actuador servo/rele portón | ST-007, ST-009 | 2 | Activar servo para abrir/cerrar según respuesta del backend + estado del sensor |

---

## HU-002: Bloqueo por Fuerza Bruta y Ventana de Tiempo

**ID:** HU-002  
**Épica:** Control de Acceso — Ingreso  
**RF base:** RF-01.3, RF-01.4, RF-01.5, RF-05.1  
**Story Points:** 20  
**Depende de:** HU-001

### Descripción

Como **operador del depósito**, quiero **que el sistema se proteja contra intentos repetidos y tenga una ventana de tiempo limitada tras un PIN correcto** para **evitar accesos no autorizados por fuerza bruta y garantizar que solo personal autorizado ingrese dentro del plazo establecido**.

### Criterios de Aceptación

| ID | Criterio | RF |
|---|---|---|
| AC-01 | Si ocurren **≥5 intentos fallidos en una ventana de 45s**, el PIN se bloquea automáticamente | RF-01.3 |
| AC-02 | Mientras el PIN esté bloqueado, cualquier intento (incluso el correcto) es rechazado con código `BLOCKED` y mensaje descriptivo | RF-01.3 |
| AC-03 | Al dispararse el bloqueo por fuerza bruta, se dispara el evento de alarma correspondiente | RF-05.1 |
| AC-04 | Tras ingresar el PIN correcto, se abre una **ventana de 30 segundos** para abrir el portón | RF-01.4 |
| AC-05 | Si la ventana de 30s expira sin que el sensor detecte apertura, el PIN se bloquea automáticamente | RF-01.5 |
| AC-06 | El LED del panel refleja: **rojo fijo** (bloqueado), **verde fijo** (ventana abierta), **rojo titilante** (alarma por fuerza bruta) | — |

### Sub-tareas

#### 🔙 Backend (4)

| # | Sub-tarea | Depende de | SP | Descripción |
|---|---|---|---|---|
| ST-001 | `BruteForceGuard` | `HU-001 / ST-005` | 3 | Contador de intentos con timestamps, ventana deslizante de 45s, umbral de 5, bloqueo automático |
| ST-002 | `TimeWindowService` | `HU-001 / ST-005` | 2 | Control de ventana de 30s post-PIN correcto, timeout, bloqueo al expirar |
| ST-003 | Actualizar `POST /api/pin/validate` | ST-001, ST-002 | 3 | Integrar BruteForceGuard + TimeWindowService en el flujo de validación, extender respuesta con códigos de estado |
| ST-004 | Tests backend (JUnit 5) | ST-001, ST-002, ST-003 | 5 | Tests unitarios para BruteForceGuard, TimeWindowService, y test de integración del flujo completo |

#### 📟 Firmware (3)

| # | Sub-tarea | Depende de | SP | Descripción |
|---|---|---|---|---|
| ST-005 | Timer 30s en firmware | `HU-001 / ST-010`, ST-003 | 3 | Iniciar conteo regresivo al recibir `PIN_OK_VALID_WINDOW`, bloquear portón al expirar sin detectar apertura |
| ST-006 | Bloqueo local de teclado | ST-005 | 2 | No aceptar entrada del teclado ni enviar peticiones mientras el PIN esté bloqueado localmente |
| ST-007 | LED extendido con alarma | ST-003 | 2 | Rojo fijo (bloqueado), verde fijo (ventana abierta), rojo titilante 1Hz (alarma fuerza bruta) |

---

## HU-003: Gestión de PIN y Visualización en Dashboard

**ID:** HU-003  
**Épica:** Control de Acceso — Ingreso  
**RF base:** RF-01.7, RF-06.1, RF-07.2, RF-07.3, NF-09  
**Story Points:** 19  
**Depende de:** HU-001, HU-002

### Descripción

Como **administrador del depósito**, quiero **poder cambiar el PIN del portón y consultar el historial de accesos desde el dashboard** para **gestionar la seguridad del sistema sin necesidad de intervención técnica y auditar quién ingresó y cuándo**.

### Criterios de Aceptación

| ID | Criterio | RF |
|---|---|---|
| AC-01 | El administrador puede cambiar el PIN desde el frontend indicando: PIN antiguo + PIN nuevo + confirmación | RF-01.7 |
| AC-02 | El backend valida que el PIN nuevo cumpla con el formato (4 dígitos numéricos) y que el PIN antiguo sea correcto antes de persistir | RF-01.7 |
| AC-03 | El frontend muestra el estado del portón en tiempo real: **abierto** (verde), **cerrado** (gris), **bloqueado** (rojo) | RF-06.1 |
| AC-04 | El dashboard lista los **últimos ingresos exitosos** con timestamp, método (PIN/RFID) y estado | RF-07.2 |
| AC-05 | El dashboard lista los **últimos intentos fallidos** con timestamp, método y causa (PIN inválido / bloqueado / timeout) | RF-07.3 |
| AC-06 | El dashboard se actualiza automáticamente cada N segundos mediante polling, sin necesidad de refresh manual del navegador | NF-09 |

### Sub-tareas

#### 🔙 Backend (3)

| # | Sub-tarea | Depende de | SP | Descripción |
|---|---|---|---|---|
| ST-001 | Endpoint `PUT /api/pin/change` | `HU-001 / ST-003` | 3 | Validar PIN antiguo contra hash, actualizar hash con nuevo PIN, persistir cambio con timestamp, devolver confirmación |
| ST-002 | Endpoint `GET /api/access-events` | `HU-001 / ST-005` | 3 | Listar eventos de acceso con paginación, filtro por tipo (éxito/fallo), orden cronológico descendente |
| ST-003 | Endpoint `GET /api/pin/status` | `HU-002 / ST-003` | 2 | Devolver estado actual del PIN (activo/bloqueado), modo (ventana abierta/cerrada), estado del portón (abierto/cerrado) |

#### 🖥️ Frontend (4)

| # | Sub-tarea | Depende de | SP | Descripción |
|---|---|---|---|---|
| ST-004 | Formulario cambio de PIN | ST-001 | 3 | Componente React con 3 campos (antiguo, nuevo, confirmación), validación local de formato, envío asíncrono a `PUT /api/pin/change`, feedback visual de éxito/error |
| ST-005 | Indicador visual estado portón | ST-003 | 2 | Componente con 3 estados visuales: abierto (verde + icono puerta), cerrado (gris), bloqueado (rojo + icono candado) |
| ST-006 | Vista historial de accesos | ST-002 | 3 | Tabla combinada con ingresos exitosos e intentos fallidos, columnas: timestamp, tipo (éxito/fallo), método (PIN/RFID), causa. Con paginado y orden cronológico |
| ST-007 | Polling dashboard | ST-004, ST-005, ST-006 | 3 | Hook/efecto que consume `GET /api/pin/status` y `GET /api/access-events` cada N segundos, actualiza todos los componentes sin refresh manual |

---

## Resumen por Historia

| Historia | Story Points | Sub-tareas | Backend | Firmware | Frontend | Depende de |
|---|---|---|---|---|---|---|
| **HU-001** · Validación Básica PIN | 26 | 10 (ST-001 a ST-010) | 5 | 5 | 0 | — |
| **HU-002** · Fuerza Bruta + Ventana | 20 | 7 (ST-001 a ST-007) | 4 | 3 | 0 | HU-001 |
| **HU-003** · Gestión + Dashboard | 19 | 7 (ST-001 a ST-007) | 3 | 0 | 4 | HU-001, HU-002 |
| **Total** | **65** | **24** | **12** | **8** | **4** | |

---

<div align="center">
  <img src="./logoDemandaNintendo.png" width="180" alt="DemandadeNintendo" />
  <br />
  <strong>DemandadeNintendo</strong>
  <br />
  Ingeniería de Software · FCEFyN · UNC
</div>