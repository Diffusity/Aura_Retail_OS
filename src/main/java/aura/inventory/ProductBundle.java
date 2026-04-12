package aura.inventory;

import aura.interfaces.IInventoryItem;

import java.util.ArrayList;
import java.util.List;

// PATTERN: Composite (Composite Node, Structural) — bundle of products/sub-bundles
// isAvailable() propagates: bundle is available only if ALL children are available
// STUB — full implementation in Phase 4 (Subtask 2)
public class ProductBundle implements IInventoryItem {
    private final String id;
    private final String name;
    private final double basePrice;
    private final List<IInventoryItem> children = new ArrayList<>();

    public ProductBundle(String id, String name, double basePrice) {
        this.id = id;
        this.name = name;
        this.basePrice = basePrice;
    }

    @Override public String getId()   { return id; }
    @Override public String getName() { return name; }
    @Override public double getBasePrice() { return basePrice; }

    @Override
    public int getAvailableStock() {
        // Bundle stock = minimum stock across all children
        return children.stream()
                .mapToInt(IInventoryItem::getAvailableStock)
                .min()
                .orElse(0);
    }

    // PATTERN: Composite — recursive availability check propagates from leaf to root
    @Override
    public boolean isAvailable() {
        return !children.isEmpty() && children.stream().allMatch(IInventoryItem::isAvailable);
    }

    @Override public void add(IInventoryItem item)    { children.add(item); }
    @Override public void remove(IInventoryItem item) { children.remove(item); }
    @Override public List<IInventoryItem> getChildren() { return children; }

    @Override
    public String toString() {
        return "ProductBundle{id='" + id + "', name='" + name + "', available=" + isAvailable() + ", children=" + children.size() + "}";
    }
}
