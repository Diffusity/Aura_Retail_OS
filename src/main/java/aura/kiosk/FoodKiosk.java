package aura.kiosk;

import aura.interfaces.IInventoryItem;
import aura.inventory.SecureInventoryProxy;

// PATTERN: Inheritance — food kiosk can operate offline, enforces purchase quantity limits
public class FoodKiosk extends BaseKiosk {
    public FoodKiosk(String id) { super(id); }

    @Override
    protected boolean checkHardwareHealth() { return getDispenser() != null && getDispenser().isOperational(); }

    @Override
    protected boolean checkNetworkAvailability() { return true; } // food kiosk can operate offline

    @Override
    public IInventoryItem getInventoryItem(String productId) {
        return SecureInventoryProxy.getInstance().getItem(productId);
    }

    @Override
    public boolean checkInventory(String productId, int qty) {
        IInventoryItem item = getInventoryItem(productId);
        boolean withinLimit = getInventoryPolicy() == null ||
                qty <= getInventoryPolicy().getMaxPurchaseQuantity(productId, false);
        return item != null && item.getAvailableStock() >= qty && withinLimit;
    }
}
