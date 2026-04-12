package aura.inventory;

import aura.interfaces.IInventoryPolicy;

public class StandardInventoryPolicy implements IInventoryPolicy {
    @Override public int getMaxPurchaseQuantity(String productId, boolean emergencyMode) { return emergencyMode ? 2 : 10; }
    @Override public boolean canRestock(String productId) { return true; }
}
