package Entity;

/**
 * Severity levels for {@link com.iot.models.entities.SystemAlert}.
 */
public enum AlertSeverity {

    /** Informational — no action required. */
    INFO,

    /** Something worth attention but not critical. */
    WARNING,

    /** Requires immediate user attention. */
    CRITICAL
}