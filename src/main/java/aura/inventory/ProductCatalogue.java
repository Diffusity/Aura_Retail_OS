package aura.inventory;

import java.util.HashMap;
import java.util.Map;

// PATTERN: Flyweight (Structural) — shared intrinsic product data; extrinsic data (stockCount) stays in Product
public class ProductCatalogue {
    private static final Map<String, ProductMetadata> sharedMetadata = new HashMap<>();

    // PATTERN: Flyweight — store intrinsic state once regardless of how many kiosks use it
    public static ProductMetadata getOrCreate(String id, String name, String category, double basePrice) {
        return sharedMetadata.computeIfAbsent(id, k -> new ProductMetadata(id, name, category, basePrice));
    }

    public static ProductMetadata getMetadata(String id) { return sharedMetadata.get(id); }

    // Intrinsic state: immutable, shared across all kiosk instances
    public static class ProductMetadata {
        public final String id, name, category;
        public final double basePrice;

        ProductMetadata(String id, String name, String category, double price) {
            this.id = id; this.name = name; this.category = category; this.basePrice = price;
        }

        @Override public String toString() {
            return "ProductMetadata{id='" + id + "', name='" + name + "', category='" + category + "', price=" + basePrice + "}";
        }
    }
}
