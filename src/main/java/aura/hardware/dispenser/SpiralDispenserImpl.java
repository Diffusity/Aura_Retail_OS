package aura.hardware.dispenser;

import aura.interfaces.IDispenserImpl;

// PATTERN: Bridge (ConcreteImplementor, Structural)
public class SpiralDispenserImpl implements IDispenserImpl {
    @Override
    public boolean performDispense(String productId, int qty) {
        System.out.println("[SpiralDispenser] Rotating spiral motor for: " + productId + " qty=" + qty);
        return true;
    }
    @Override public void calibrate() { System.out.println("[SpiralDispenser] Calibrating spiral motor..."); }
    @Override public String getHardwareType() { return "SPIRAL"; }
}
