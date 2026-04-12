package aura.monitoring;

import aura.interfaces.IEventSubscriber;

import java.util.List;
import java.util.Map;

// PATTERN: Observer (ConcreteSubscriber) — reacts to low stock and completed transactions
public class SupplyChainSystem implements IEventSubscriber {
    @Override
    public void onEvent(String eventType, Map<String, Object> eventData) {
        if ("LowStockEvent".equals(eventType))
            System.out.println("[SupplyChain] Low stock alert — scheduling reorder: " + eventData);
        if ("TransactionCompletedEvent".equals(eventType))
            System.out.println("[SupplyChain] Transaction completed — updating demand forecast: " + eventData);
    }

    @Override
    public List<String> getSubscribedEvents() {
        return List.of("LowStockEvent", "TransactionCompletedEvent");
    }
}
