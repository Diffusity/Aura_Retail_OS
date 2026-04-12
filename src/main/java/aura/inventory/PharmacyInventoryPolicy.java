package aura.inventory;

import aura.interfaces.IInventoryPolicy;

public class PharmacyInventoryPolicy implements IInventoryPolicy {
    @Override public int getMaxPurchaseQuantity(String productId, boolean emergencyMode) { return 1; }
    @Override public boolean canRestock(String productId) { return true; }
}
