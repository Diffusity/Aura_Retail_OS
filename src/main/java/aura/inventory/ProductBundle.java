package aura.inventory;

import aura.interfaces.IInventoryItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// PATTERN: Composite (Composite Node, Structural) — contains bundles and/or products
// Availability and stock propagate recursively from all children
public class ProductBundle implements IInventoryItem {
    private final String id;
    private final String name;
    private final List<IInventoryItem> children = new ArrayList<>();
    private double bundleDiscount = 0.0;

    public ProductBundle(String id, String name) { this.id = id; this.name = name; }

    @Override public void add(IInventoryItem item)    { children.add(item); }
    @Override public void remove(IInventoryItem item) { children.remove(item); }
    @Override public List<IInventoryItem> getChildren() { return Collections.unmodifiableList(children); }

    // PATTERN: Composite — stock is min of all children's available stock
    @Override
    public int getAvailableStock() {
        return children.stream().mapToInt(IInventoryItem::getAvailableStock).min().orElse(0);
    }

    // PATTERN: Composite — bundle is available only if ALL children are available
    @Override
    public boolean isAvailable() {
        return !children.isEmpty() && children.stream().allMatch(IInventoryItem::isAvailable);
    }

    // PATTERN: Composite — price is sum of all children, minus bundle discount
    @Override
    public double getBasePrice() {
        double total = children.stream().mapToDouble(IInventoryItem::getBasePrice).sum();
        return total * (1.0 - bundleDiscount);
    }

    @Override public String getId()   { return id; }
    @Override public String getName() { return name; }

    public void setBundleDiscount(double d) { this.bundleDiscount = d; }

    @Override
    public String toString() {
        return name + " (bundle, available=" + isAvailable() + ", children=" + children.size() + ")";
    }
}
