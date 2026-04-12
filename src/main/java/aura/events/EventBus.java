package aura.events;

import aura.interfaces.IEventPublisher;
import aura.interfaces.IEventSubscriber;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// PATTERN: Singleton (Creational) + Observer Subject (Behavioral)
// Priority rule: EmergencyModeActivatedEvent is dispatched synchronously before any other event
public class EventBus implements IEventPublisher {
    private static volatile EventBus instance;
    private final Map<String, List<IEventSubscriber>> subscribers = new HashMap<>();

    private EventBus() {}

    public static EventBus getInstance() {
        if (instance == null) {
            synchronized (EventBus.class) {
                if (instance == null) instance = new EventBus();
            }
        }
        return instance;
    }

    public void subscribe(String eventType, IEventSubscriber subscriber) {
        subscribers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(subscriber);
    }

    public void unsubscribe(String eventType, IEventSubscriber subscriber) {
        if (subscribers.containsKey(eventType)) subscribers.get(eventType).remove(subscriber);
    }

    @Override
    public void publish(String eventType, Map<String, Object> eventData) {
        System.out.println("[EventBus] Publishing: " + eventType + " → " + eventData);
        List<IEventSubscriber> subs = subscribers.getOrDefault(eventType, Collections.emptyList());
        for (IEventSubscriber sub : subs) {
            sub.onEvent(eventType, eventData);
        }
    }
}
