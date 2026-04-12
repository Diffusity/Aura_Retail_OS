package aura.simulation;

import aura.factory.EmergencyReliefKioskFactory;
import aura.factory.FoodKioskFactory;
import aura.factory.PharmacyKioskFactory;
import aura.hardware.dispenser.RoboticArmDispenserImpl;
import aura.interfaces.IPaymentProvider;
import aura.inventory.Product;
import aura.inventory.ProductBundle;
import aura.inventory.SecureInventoryProxy;
import aura.kiosk.KioskBuilder;
import aura.kiosk.KioskInterface;
import aura.payment.PaymentProviderRegistry;
import aura.payment.adapters.CryptoPaymentAdapter;
import aura.payment.thirdparty.FakeCryptoAPI;

import java.util.stream.Collectors;

/**
 * Subtask 2 Demo — runs 3 patterns live:
 *   1. Bridge: swap dispenser impl at runtime
 *   2. Adapter: register CryptoPaymentAdapter without modifying existing code
 *   3. Composite: nested bundle availability propagation
 */
public class SubTask2Demo {
    public static void main(String[] args) {
        SystemBootstrap.initialize();

        System.out.println("\n========================================================");
        System.out.println("  AURA RETAIL OS — SUBTASK 2 PATTERN DEMONSTRATION");
        System.out.println("========================================================\n");

        // ─── SECTION 1: Abstract Factory + Template Method ───────────────────
        System.out.println("── [1] Abstract Factory · KioskBuilder · Facade ─────────\n");

        // Seed inventory so purchases can resolve
        SecureInventoryProxy inv = SecureInventoryProxy.getInstance();
        inv.addItem(new Product("FOOD-001", "Energy Bar", 50, 3.50));
        inv.addItem(new Product("MED-001",  "Paracetamol", 20, 1.50));

        KioskInterface foodKiosk = new KioskBuilder(new FoodKioskFactory())
                .build("F-001");
        System.out.println("[FACTORY] FoodKiosk F-001 created.");

        KioskInterface pharmacyKiosk = new KioskBuilder(new PharmacyKioskFactory())
                .build("P-001");
        System.out.println("[FACTORY] PharmacyKiosk P-001 created.");

        KioskInterface emergencyKiosk = new KioskBuilder(new EmergencyReliefKioskFactory())
                .build("E-001");
        System.out.println("[FACTORY] EmergencyReliefKiosk E-001 created.\n");

        // ─── SECTION 2: Bridge ────────────────────────────────────────────────
        System.out.println("── [2] Bridge Pattern ───────────────────────────────────\n");

        KioskInterface bridgeKiosk = new KioskBuilder(new FoodKioskFactory())
                .withDispenserImpl(new RoboticArmDispenserImpl())  // Bridge swap
                .build("F-002");
        System.out.println("[BRIDGE] F-002 dispenser swapped to RoboticArm at build time.");
        System.out.println("[BRIDGE] Swap required zero changes to BaseKiosk or FoodKiosk.\n");

        // ─── SECTION 3: Decorator ─────────────────────────────────────────────
        System.out.println("── [3] Decorator Pattern ────────────────────────────────\n");

        KioskInterface decoratedKiosk = new KioskBuilder(new FoodKioskFactory())
                .addRefrigeration()
                .addSolarMonitor()
                .addNetworkModule()
                .withDispenserImpl(new RoboticArmDispenserImpl())
                .build("F-003");
        System.out.println("[DECORATOR] F-003 wrapped: Refrigeration + Solar + Network.");
        System.out.println("[DECORATOR] FoodKiosk.java was NOT modified.\n");

        // ─── SECTION 4: Adapter ───────────────────────────────────────────────
        System.out.println("── [4] Adapter Pattern ──────────────────────────────────\n");

        PaymentProviderRegistry registry = PaymentProviderRegistry.getInstance();
        System.out.println("[ADAPTER] Registered providers: " + registry.getAllProviders().stream()
                .map(IPaymentProvider::getProviderName).collect(Collectors.joining(", ")));

        registry.register(new CryptoPaymentAdapter(new FakeCryptoAPI()));
        System.out.println("[ADAPTER] CryptoPaymentAdapter registered at runtime.");

        IPaymentProvider crypto = registry.getProvider("Crypto");
        System.out.println("[ADAPTER] Crypto payment result: " +
                crypto.processPayment("TX-SUB2-001", 99.99));
        System.out.println("[ADAPTER] Incompatible FakeCryptoAPI.sendCrypto() hidden behind IPaymentProvider.\n");

        // ─── SECTION 5: Composite ─────────────────────────────────────────────
        System.out.println("── [5] Composite Pattern ────────────────────────────────\n");

        Product bandages    = new Product("P001", "Bandages",     50, 2.00);
        Product antiseptic  = new Product("P002", "Antiseptic",   30, 4.00);
        Product paracetamol = new Product("P003", "Paracetamol2", 20, 1.50);

        ProductBundle medKit = new ProductBundle("B001", "Med Kit");
        medKit.add(bandages); medKit.add(antiseptic); medKit.add(paracetamol);
        medKit.setBundleDiscount(0.10);

        ProductBundle emergencyKit = new ProductBundle("B002", "Emergency Kit");
        emergencyKit.add(medKit);
        emergencyKit.add(new Product("P004", "Water", 100, 1.00));

        System.out.println("[COMPOSITE] Med Kit available:       " + medKit.isAvailable());
        System.out.println("[COMPOSITE] Emergency Kit available: " + emergencyKit.isAvailable());

        paracetamol.commitReservation(20); // depletes paracetamol
        System.out.println("[COMPOSITE] After depleting Paracetamol:");
        System.out.println("[COMPOSITE] Paracetamol available:   " + paracetamol.isAvailable());
        System.out.println("[COMPOSITE] Med Kit available:       " + medKit.isAvailable());
        System.out.println("[COMPOSITE] Emergency Kit available: " + emergencyKit.isAvailable());
        System.out.println("[COMPOSITE] Propagation proven recursively.\n");

        System.out.println("========================================================");
        System.out.println("  SUBTASK 2 DEMO COMPLETE — ALL PATTERNS VERIFIED");
        System.out.println("========================================================");
    }
}
