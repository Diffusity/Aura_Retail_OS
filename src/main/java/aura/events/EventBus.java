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

    // Functional interface — allows lambdas to subscribe without implementing full IEventSubscriber
    @FunctionalInterface
    public interface EventListener {
        void onEvent(String eventType, Map<String, Object> eventData);
    }

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

    // Subscribe a full IEventSubscriber (observer classes)
    public void subscribe(String eventType, IEventSubscriber subscriber) {
        subscribers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(subscriber);
    }

    // Subscribe a lambda (wraps EventListener into anonymous IEventSubscriber)
    public void subscribe(String eventType, EventListener listener) {
        subscribe(eventType, new IEventSubscriber() {
            @Override public void onEvent(String type, Map<String, Object> data) { listener.onEvent(type, data); }
            @Override public List<String> getSubscribedEvents() { return List.of(eventType); }
        });
    }

    public void unsubscribe(String eventType, IEventSubscriber subscriber) {
        if (subscribers.containsKey(eventType)) subscribers.get(eventType).remove(subscriber);
    }

    @Override
    public void publish(String eventType, Map<String, Object> eventData) {
        System.out.println("[EventBus] Publishing: " + eventType + " → " + eventData);
        List<IEventSubscriber> subs = subscribers.getOrDefault(eventType, Collections.emptyList());
        for (IEventSubscriber sub : new ArrayList<>(subs)) {
            sub.onEvent(eventType, eventData);
        }
    }
}
