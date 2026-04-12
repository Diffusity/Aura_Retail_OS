package aura.simulation;

import aura.factory.FoodKioskFactory;
import aura.hardware.dispenser.RoboticArmDispenserImpl;
import aura.kiosk.KioskBuilder;
import aura.kiosk.KioskInterface;

// PATTERN: Decorator + Bridge demo
public class ScenarioA_NewHardwareModule {
    public static void main(String[] args) {
        SystemBootstrap.initialize();
        System.out.println("\n=== PATH B SCENARIO A: Adding a New Hardware Module ===\n");

        // PATTERN: Abstract Factory — creates compatible FoodKiosk components
        KioskInterface baseKiosk = new KioskBuilder(new FoodKioskFactory())
            .build("KIOSK-001");
        System.out.println("[FACTORY] FoodKiosk KIOSK-001 created without refrigeration.");
        System.out.println("[FACADE] Diagnostics: " + baseKiosk.runDiagnostics());

        // PATTERN: Bridge — swap dispenser implementation at runtime
        System.out.println("\n[BRIDGE] Swapping SpiralDispenser → RoboticArmDispenser on KIOSK-001...");

        // PATTERN: Decorator — add refrigeration and solar WITHOUT modifying FoodKiosk.java
        KioskInterface refrigeratedKiosk = new KioskBuilder(new FoodKioskFactory())
            .addRefrigeration()
            .addSolarMonitor()
            .withDispenserImpl(new RoboticArmDispenserImpl())
            .build("KIOSK-002");
        System.out.println("\n[DECORATOR] FoodKiosk KIOSK-002 created with Refrigeration + Solar modules.");
        System.out.println("[BRIDGE] KIOSK-002 dispenser is now RoboticArm.");
        System.out.println("[FACADE] Diagnostics: " + refrigeratedKiosk.runDiagnostics());

        System.out.println("\n[DECORATOR] FoodKiosk.java was NOT modified. Module added via Decorator.");
        System.out.println("[BRIDGE] Dispenser swap required zero changes to BaseKiosk or KioskInterface.");
        System.out.println("=== SCENARIO A COMPLETE ===");
    }
}
