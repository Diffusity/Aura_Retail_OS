package aura.pricing;

import aura.interfaces.IInventoryItem;
import aura.interfaces.IPricingStrategy;

import java.util.Map;

// PATTERN: Strategy (ConcreteStrategy, Behavioral)
public class DiscountedPricingStrategy implements IPricingStrategy {
    private final double discountRate;

    public DiscountedPricingStrategy(double rate) { this.discountRate = rate; }

    @Override
    public double computePrice(IInventoryItem item, Map<String, Object> context) {
        return item.getBasePrice() * (1.0 - discountRate);
    }
}
