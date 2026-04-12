package aura.inventory;

import aura.interfaces.IInventoryPolicy;

// System constraint enforcement: max 2 per user during emergency mode
public class EmergencyInventoryPolicy implements IInventoryPolicy {
    @Override
    public int getMaxPurchaseQuantity(String productId, boolean emergencyMode) {
        return emergencyMode ? 2 : 5; // cap enforced here — not in kiosk or command
    }
    @Override public boolean canRestock(String productId) { return true; }
}
