package aura.hardware.dispenser;

import aura.interfaces.IDispenserImpl;

// PATTERN: Bridge (ConcreteImplementor, Structural)
public class ConveyorDispenserImpl implements IDispenserImpl {
    @Override
    public boolean performDispense(String productId, int qty) {
        System.out.println("[ConveyorDispenser] Activating belt for: " + productId + " qty=" + qty);
        return true;
    }
    @Override public void calibrate() { System.out.println("[ConveyorDispenser] Belt calibration..."); }
    @Override public String getHardwareType() { return "CONVEYOR"; }
}
