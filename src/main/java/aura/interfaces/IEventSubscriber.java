package aura.interfaces;

import java.util.List;
import java.util.Map;

// PATTERN: Observer (Behavioral) — subscriber contract
public interface IEventSubscriber {
    void onEvent(String eventType, Map<String, Object> eventData);
    List<String> getSubscribedEvents();
}
