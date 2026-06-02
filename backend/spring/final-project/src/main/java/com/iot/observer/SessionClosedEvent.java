package com.iot.observer;
// SessionClosedEvent.java

/**
 * Data object passed to all observers when a session is closed.
 */
public class SessionClosedEvent {
    private final Long sessionId;

    public SessionClosedEvent(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getSessionId() {
        return sessionId;
    }
}