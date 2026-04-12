package aura.kiosk;

import aura.interfaces.IInventoryItem;
import aura.inventory.SecureInventoryProxy;

// PATTERN: Inheritance — overrides verifyUser to require prescription; overrides hardware/network checks
public class PharmacyKiosk extends BaseKiosk {
    public PharmacyKiosk(String id) { super(id); }

    @Override
    protected boolean verifyUser(String userId, String productId) {
        boolean hasPrescription = super.verifyUser(userId, productId);
        if (!hasPrescription) System.out.println("[PharmacyKiosk] Denied: no valid prescription.");
        return hasPrescription;
    }

    @Override
    protected boolean checkHardwareHealth() { return getDispenser() != null && getDispenser().isOperational(); }

    @Override
    protected boolean checkNetworkAvailability() {
        // Pharmacy must be online to validate prescriptions
        return true; // simplified for demo; in prod: NetworkConnectivityDecorator.isNetworkAvailable()
    }

    @Override
    public IInventoryItem getInventoryItem(String productId) {
        return SecureInventoryProxy.getInstance().getItem(productId);
    }

    @Override
    public boolean checkInventory(String productId, int qty) {
        IInventoryItem item = getInventoryItem(productId);
        return item != null && item.getAvailableStock() >= qty;
    }
}
