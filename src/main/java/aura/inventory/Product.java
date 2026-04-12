package aura.inventory;

import aura.interfaces.IInventoryItem;

import java.util.Collections;
import java.util.List;

// PATTERN: Composite (Leaf, Structural) — individual inventory item with derived stock
// PATTERN: Encapsulation — all mutation methods are controlled; stock is never set directly
public class Product implements IInventoryItem {
    private final String id;
    private final String name;
    private int stockCount;
    private int reservedCount = 0;       // items in active transactions — not committed yet
    private int hardwareFaultCount = 0;  // items blocked due to hardware failure
    private final double basePrice;
    private boolean requiresRefrigeration = false;

    public Product(String id, String name, int stock, double price) {
        this.id = id;
        this.name = name;
        this.stockCount = stock;
        this.basePrice = price;
    }

    // PATTERN: Derived attribute — NEVER stored; always computed from sub-values
    @Override
    public int getAvailableStock() {
        return Math.max(0, stockCount - reservedCount - hardwareFaultCount);
    }

    @Override public boolean isAvailable() { return getAvailableStock() > 0; }

    // Reservation lifecycle — supports atomic transaction pattern
    public void reserve(int qty)             { reservedCount += qty; }
    public void releaseReservation(int qty)  { reservedCount = Math.max(0, reservedCount - qty); }
    public void commitReservation(int qty) {
        stockCount -= qty;
        reservedCount = Math.max(0, reservedCount - qty);
    }

    public void markHardwareUnavailable(int qty) { hardwareFaultCount += qty; }
    public void clearHardwareFault()             { hardwareFaultCount = 0; }

    // Composite leaf: add/remove/getChildren are no-ops
    @Override public void add(IInventoryItem item)      { throw new UnsupportedOperationException("Product is a leaf"); }
    @Override public void remove(IInventoryItem item)   { throw new UnsupportedOperationException("Product is a leaf"); }
    @Override public List<IInventoryItem> getChildren() { return Collections.emptyList(); }

    @Override public String getId()        { return id; }
    @Override public String getName()      { return name; }
    @Override public double getBasePrice() { return basePrice; }

    public void setRequiresRefrigeration(boolean r) { this.requiresRefrigeration = r; }
    public boolean requiresRefrigeration()          { return requiresRefrigeration; }

    @Override
    public String toString() {
        return name + " (stock=" + getAvailableStock() + ", available=" + isAvailable() + ")";
    }
}
