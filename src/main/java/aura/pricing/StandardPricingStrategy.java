package aura.pricing;

import aura.interfaces.IInventoryItem;
import aura.interfaces.IPricingStrategy;

import java.util.Map;

// PATTERN: Strategy (ConcreteStrategy, Behavioral)
public class StandardPricingStrategy implements IPricingStrategy {
    @Override
    public double computePrice(IInventoryItem item, Map<String, Object> context) {
        return item.getBasePrice();
    }
}
