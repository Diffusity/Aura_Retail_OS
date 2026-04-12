package aura.inventory;

import aura.interfaces.IInventoryItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// PATTERN: Composite (Leaf, Structural) — single product; leaf node with no children
// STUB — full implementation in Phase 4 (Subtask 2)
public class Product implements IInventoryItem {
    private final String id;
    private final String name;
    private int availableStock;
    private final double basePrice;

    public Product(String id, String name, int availableStock, double basePrice) {
        this.id = id;
        this.name = name;
        this.availableStock = availableStock;
        this.basePrice = basePrice;
    }

    @Override public String getId()             { return id; }
    @Override public String getName()           { return name; }
    @Override public int getAvailableStock()    { return availableStock; }
    @Override public double getBasePrice()      { return basePrice; }
    @Override public boolean isAvailable()      { return availableStock > 0; }

    // Leaf nodes do not support child operations
    @Override public void add(IInventoryItem item)    { throw new UnsupportedOperationException("Product is a leaf node."); }
    @Override public void remove(IInventoryItem item) { throw new UnsupportedOperationException("Product is a leaf node."); }
    @Override public List<IInventoryItem> getChildren() { return Collections.emptyList(); }

    public void setAvailableStock(int stock) { this.availableStock = stock; }

    @Override
    public String toString() {
        return "Product{id='" + id + "', name='" + name + "', stock=" + availableStock + ", available=" + isAvailable() + "}";
    }
}
