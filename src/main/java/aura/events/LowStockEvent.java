package aura.events;

/** Fired when a product's stock falls below the reorder threshold. */
public class LowStockEvent {
    public static final String EVENT_TYPE = "LowStockEvent";
    private final String productId;
    private final int currentStock;
    private final String kioskId;

    public LowStockEvent(String productId, int currentStock, String kioskId) {
        this.productId = productId;
        this.currentStock = currentStock;
        this.kioskId = kioskId;
    }

    public java.util.Map<String, Object> toMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("productId", productId);
        map.put("currentStock", currentStock);
        map.put("kioskId", kioskId);
        return map;
    }
}
