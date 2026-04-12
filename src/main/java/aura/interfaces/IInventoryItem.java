package aura.interfaces;

import java.util.List;

// PATTERN: Composite (Component, Structural) — uniform interface for products and bundles
public interface IInventoryItem {
    String getId();
    String getName();
    int getAvailableStock();
    double getBasePrice();
    boolean isAvailable();
    void add(IInventoryItem item);       // only meaningful in ProductBundle
    void remove(IInventoryItem item);    // only meaningful in ProductBundle
    List<IInventoryItem> getChildren(); // only meaningful in ProductBundle
}
