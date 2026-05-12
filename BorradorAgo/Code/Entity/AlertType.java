package Entity;
/**
 * All possible alert types grouped by category.
 */
public enum AlertType {

    // --- Temperature ---

    /** Water reached the target temperature set by the potentiometer. */
    TARGET_TEMPERATURE_REACHED,

    /** Water temperature dropped below the minimum acceptable threshold. */
    WATER_TOO_COLD,

    /** Water temperature exceeded the maximum safe threshold. */
    WATER_OVERHEATED,

    /** Temperature is dropping faster than the expected cooling rate. */
    RAPID_COOLING_DETECTED,

    // --- Sensor ---

    /** Temperature sensor reported an error or is unresponsive. */
    TEMPERATURE_SENSOR_ERROR,

    /** Sensor returned a value outside the valid range [0, 100] °C. */
    INVALID_SENSOR_VALUE,

    /** No telemetry readings received within the expected interval. */
    NO_TELEMETRY_RECEIVED,

    // --- System ---

    /** A new mate session was opened by the backend. */
    SESSION_STARTED,

    /** An active mate session was closed by the backend. */
    SESSION_FINISHED,

    // --- Usage ---

    /** Unusually high number of pours detected in a short period. */
    HIGH_MATE_ACTIVITY,

    /** Water level or temperature suggests the kettle may be running low. */
    LOW_WATER_WARNING
}