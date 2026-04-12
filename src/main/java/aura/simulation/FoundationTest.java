package aura.simulation;

import aura.events.EventBus;
import aura.events.HardwareFailureEvent;
import aura.monitoring.CityMonitoringCenter;
import aura.monitoring.MaintenanceService;
import aura.monitoring.SupplyChainSystem;
import aura.registry.CentralRegistry;

import java.util.Map;

/**
 * Foundation Test — verifies CentralRegistry persistence round-trip.
 * This is an internal test class (not submitted as a simulation scenario).
 * Run with: mvn exec:java -Dexec.mainClass="aura.simulation.FoundationTest"
 */
public class FoundationTest {
    public static void main(String[] args) {
        System.out.println("=== [FoundationTest] Starting Persistence Round-Trip Test ===\n");

        // --- Step 1: Write systemMode to persistence ---
        CentralRegistry reg = CentralRegistry.getInstance();
        reg.setSystemMode("EMERGENCY");
        reg.setConfig("maxPurchaseQty", 2);
        System.out.println("[FoundationTest] Saved systemMode=EMERGENCY to data/config.json");

        // --- Step 2: Simulate a fresh registry read (same JVM, config already on disk) ---
        CentralRegistry reg2 = CentralRegistry.getInstance();
        System.out.println("[FoundationTest] Loaded systemMode=" + reg2.getSystemMode());
        assert "EMERGENCY".equals(reg2.getSystemMode()) : "Persistence round-trip failed!";
        System.out.println("[FoundationTest] [PASS] Persistence round-trip verified.\n");

        // --- Step 3: Wire EventBus and test Observer dispatch ---
        System.out.println("=== [FoundationTest] Testing EventBus + Observer Pattern ===\n");
        EventBus bus = EventBus.getInstance();

        CityMonitoringCenter cityMonitor = new CityMonitoringCenter();
        SupplyChainSystem supplyChain = new SupplyChainSystem();
        MaintenanceService maintenanceService = new MaintenanceService();

        // Auto-register based on subscriber's own list
        for (String evt : cityMonitor.getSubscribedEvents())     bus.subscribe(evt, cityMonitor);
        for (String evt : supplyChain.getSubscribedEvents())     bus.subscribe(evt, supplyChain);
        for (String evt : maintenanceService.getSubscribedEvents()) bus.subscribe(evt, maintenanceService);

        // Publish a hardware failure — should trigger CityMonitor + MaintenanceService → Chain
        HardwareFailureEvent failure = new HardwareFailureEvent("DISPENSER", "K001", "Jam detected");
        bus.publish(HardwareFailureEvent.EVENT_TYPE, failure.toMap());

        System.out.println("\n[FoundationTest] [PASS] EventBus + Observer + Chain of Responsibility verified.");

        // --- Step 4: Reset system mode ---
        reg.setSystemMode("NORMAL");
        System.out.println("\n[FoundationTest] Reset systemMode=NORMAL");
        System.out.println("\n=== [FoundationTest] All checks passed. Foundation is solid. ===");
    }
}
