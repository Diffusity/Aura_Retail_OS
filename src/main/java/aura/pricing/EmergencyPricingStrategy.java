package aura.pricing;

import aura.interfaces.IInventoryItem;
import aura.interfaces.IPricingStrategy;

import java.util.Map;

// PATTERN: Strategy (ConcreteStrategy, Behavioral) — free distribution in disaster zones
public class EmergencyPricingStrategy implements IPricingStrategy {
    @Override
    public double computePrice(IInventoryItem item, Map<String, Object> context) {
        return 0.0; // Essential items distributed free in emergency mode
    }
}
