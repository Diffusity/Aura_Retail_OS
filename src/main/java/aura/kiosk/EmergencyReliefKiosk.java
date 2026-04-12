package aura.kiosk;

import aura.interfaces.IInventoryItem;
import aura.inventory.SecureInventoryProxy;
import aura.registry.CentralRegistry;

// PATTERN: Inheritance — enforces emergency purchase limits via policy
public class EmergencyReliefKiosk extends BaseKiosk {
    public EmergencyReliefKiosk(String id) { super(id); }

    @Override
    protected boolean checkHardwareHealth() { return getDispenser() != null && getDispenser().isOperational(); }

    @Override
    protected boolean checkNetworkAvailability() { return true; }

    @Override
    public IInventoryItem getInventoryItem(String productId) {
        return SecureInventoryProxy.getInstance().getItem(productId);
    }

    @Override
    public boolean checkInventory(String productId, int qty) {
        boolean emergency = "EMERGENCY".equals(CentralRegistry.getInstance().getSystemMode());
        int maxQty = getInventoryPolicy() != null
                ? getInventoryPolicy().getMaxPurchaseQuantity(productId, emergency)
                : 5;
        IInventoryItem item = getInventoryItem(productId);
        if (qty > maxQty) {
            System.out.println("[EmergencyReliefKiosk] Purchase limit exceeded. Max=" + maxQty);
            return false;
        }
        return item != null && item.getAvailableStock() >= qty;
    }
}
