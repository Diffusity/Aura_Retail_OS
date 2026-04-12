package aura.simulation;

import aura.inventory.Product;
import aura.inventory.ProductBundle;

// PATTERN: Composite propagation demo — Scenario C
public class ScenarioC_NestedBundles {
    public static void main(String[] args) {
        System.out.println("=== PATH B SCENARIO C: Nested Product Bundle Inventory ===\n");

        // PATTERN: Composite — leaf products
        Product bandages    = new Product("P001", "Bandages",     50, 2.00);
        Product antiseptic  = new Product("P002", "Antiseptic",   30, 4.00);
        Product paracetamol = new Product("P003", "Paracetamol",  20, 1.50);
        Product waterBottle = new Product("P004", "Water Bottle", 100, 1.00);
        Product energyBar   = new Product("P005", "Energy Bar",   80, 2.50);

        // PATTERN: Composite — mid-level bundle
        ProductBundle medKit = new ProductBundle("B001", "Med Kit");
        medKit.add(bandages); medKit.add(antiseptic); medKit.add(paracetamol);
        medKit.setBundleDiscount(0.10);

        // PATTERN: Composite — top-level bundle wraps another bundle
        ProductBundle emergencyKit = new ProductBundle("B002", "Emergency Kit");
        emergencyKit.add(medKit); emergencyKit.add(waterBottle); emergencyKit.add(energyBar);
        emergencyKit.setBundleDiscount(0.15);

        System.out.println("[COMPOSITE] 3-level hierarchy: EmergencyKit → MedKit → Products");
        System.out.println("[COMPOSITE] Med Kit available stock:       " + medKit.getAvailableStock());
        System.out.println("[COMPOSITE] Emergency Kit available stock: " + emergencyKit.getAvailableStock());
        System.out.printf("[COMPOSITE] Emergency Kit base price:      $%.2f%n", emergencyKit.getBasePrice());

        // Deplete paracetamol — all ancestors should become unavailable
        paracetamol.commitReservation(20);
        System.out.println("\n[COMPOSITE] Depleted Paracetamol (stock=0).");
        System.out.println("[COMPOSITE] Paracetamol available:   " + paracetamol.isAvailable());  // false
        System.out.println("[COMPOSITE] Med Kit available:       " + medKit.isAvailable());       // false
        System.out.println("[COMPOSITE] Emergency Kit available: " + emergencyKit.isAvailable()); // false
        System.out.println("[COMPOSITE] Propagation works recursively through bundle tree.");
        System.out.println("=== SCENARIO C COMPLETE ===");
    }
}
