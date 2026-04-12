package aura.interfaces;

public interface IInventoryPolicy {
    int getMaxPurchaseQuantity(String productId, boolean emergencyMode);
    boolean canRestock(String productId);
}
