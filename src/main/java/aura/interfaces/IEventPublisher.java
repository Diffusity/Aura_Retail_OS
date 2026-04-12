package aura.interfaces;

import java.util.Map;

// PATTERN: Observer (Subject contract)
public interface IEventPublisher {
    void publish(String eventType, Map<String, Object> eventData);
}
