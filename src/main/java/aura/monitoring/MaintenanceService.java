package aura.monitoring;

import aura.failure.RecalibrationHandler;
import aura.failure.RetryHandler;
import aura.failure.TechnicianAlertHandler;
import aura.interfaces.IEventSubscriber;
import aura.interfaces.IFailureHandler;

import java.util.List;
import java.util.Map;

// PATTERN: Observer (ConcreteSubscriber) — triggers failure chain on hardware events
public class MaintenanceService implements IEventSubscriber {
    private final IFailureHandler failureChain;

    public MaintenanceService() {
        // Wire chain of responsibility: Retry → Recalibration → TechnicianAlert
        IFailureHandler retry = new RetryHandler();
        IFailureHandler recal = new RecalibrationHandler();
        IFailureHandler alert = new TechnicianAlertHandler();
        retry.setNext(recal);
        recal.setNext(alert);
        this.failureChain = retry;
    }

    @Override
    public void onEvent(String eventType, Map<String, Object> eventData) {
        if ("HardwareFailureEvent".equals(eventType)) {
            String failureType = (String) eventData.getOrDefault("component", "UNKNOWN");
            String context = "kioskId=" + eventData.getOrDefault("kioskId", "?");
            failureChain.handle(failureType, context);
        }
    }

    @Override
    public List<String> getSubscribedEvents() { return List.of("HardwareFailureEvent"); }
}
