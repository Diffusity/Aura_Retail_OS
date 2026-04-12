package aura.monitoring;

import aura.interfaces.IEventSubscriber;

import java.util.List;
import java.util.Map;

// PATTERN: Observer (ConcreteSubscriber, Behavioral) — receives and logs system-wide events
public class CityMonitoringCenter implements IEventSubscriber {
    @Override
    public void onEvent(String eventType, Map<String, Object> eventData) {
        switch (eventType) {
            case "HardwareFailureEvent":
                System.out.println("[CityMonitor] ALERT: Hardware failure — " + eventData); break;
            case "TransactionFailedEvent":
                System.out.println("[CityMonitor] Transaction failure — " + eventData); break;
            case "EmergencyModeActivatedEvent":
                System.out.println("[CityMonitor] EMERGENCY MODE ACTIVATED — " + eventData); break;
        }
    }

    @Override
    public List<String> getSubscribedEvents() {
        return List.of("HardwareFailureEvent", "TransactionFailedEvent", "EmergencyModeActivatedEvent");
    }
}
