package aura.hardware.dispenser;

import aura.interfaces.IDispenserImpl;

// PATTERN: Bridge (ConcreteImplementor, Structural) — spiral mechanism dispenses via rotating coils
public class SpiralDispenserImpl implements IDispenserImpl {
    @Override
    public boolean performDispense(String productId, int quantity) {
        System.out.println("[SpiralDispenser] Dispensing " + quantity + "x " + productId + " via spiral mechanism.");
        return true;
    }

    @Override
    public void calibrate() {
        System.out.println("[SpiralDispenser] Calibrating spiral coils...");
    }

    @Override
    public String getHardwareType() { return "SpiralDispenser"; }
}
