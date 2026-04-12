package aura.hardware.dispenser;

import aura.interfaces.IDispenserImpl;

// PATTERN: Bridge (ConcreteImplementor, Structural) — conveyor belt moves items from storage to pickup
public class ConveyorDispenserImpl implements IDispenserImpl {
    @Override
    public boolean performDispense(String productId, int quantity) {
        System.out.println("[ConveyorDispenser] Conveyor belt delivering " + quantity + "x " + productId + ".");
        return true;
    }

    @Override
    public void calibrate() {
        System.out.println("[ConveyorDispenser] Calibrating conveyor belt speed and sensors...");
    }

    @Override
    public String getHardwareType() { return "ConveyorDispenser"; }
}
