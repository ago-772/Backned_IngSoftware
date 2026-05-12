package Entity;

/**
 * Lifecycle states of a {@link com.iot.models.entities.MateSession}.
 */
public enum SessionStatus {

    /** Session is currently in progress; end time and averages are not yet computed. */
    ACTIVE,

    /** Session has ended; all fields are finalized. */
    CLOSED
}