package aura.inventory;

import aura.events.EventBus;
import aura.interfaces.IInventoryItem;

import java.util.HashMap;
import java.util.Map;

public class InventoryManager {
    private final Map<String, IInventoryItem> items = new HashMap<>();

    public void addItem(IInventoryItem item) { items.put(item.getId(), item); }
    public IInventoryItem getItem(String id) { return items.get(id); }

    public boolean reserve(String productId, int qty) {
        IInventoryItem item = items.get(productId);
        if (item == null || item.getAvailableStock() < qty) return false;
        if (item instanceof Product) ((Product) item).reserve(qty);
        return true;
    }

    public boolean commitUpdate(String productId, int qty) {
        IInventoryItem item = items.get(productId);
        if (!(item instanceof Product)) return false;
        ((Product) item).commitReservation(qty);
        // Check low stock threshold and publish event
        if (item.getAvailableStock() < 5) {
            EventBus.getInstance().publish("LowStockEvent",
                Map.of("productId", productId, "remainingStock", item.getAvailableStock()));
        }
        return true;
    }

    public void release(String productId, int qty) {
        IInventoryItem item = items.get(productId);
        if (item instanceof Product) ((Product) item).releaseReservation(qty);
    }
}
