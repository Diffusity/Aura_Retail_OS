package aura.interfaces;

import java.util.Map;

// PATTERN: Strategy (Behavioral) — interchangeable pricing algorithms
public interface IPricingStrategy {
    double computePrice(IInventoryItem item, Map<String, Object> context);
}
