// SessionObserver.java
package com.iot.observer;

/**
 * Observer interface for session lifecycle events. Implementations react when a session is closed.
 */
public interface SessionObserver {
  void onSessionClosed(SessionClosedEvent event);
}
