package aura.hardware.dispenser;

import aura.interfaces.IDispenserImpl;

// PATTERN: Bridge (ConcreteImplementor, Structural) — robotic arm retrieves items with precision positioning
public class RoboticArmDispenserImpl implements IDispenserImpl {
    @Override
    public boolean performDispense(String productId, int quantity) {
        System.out.println("[RoboticArmDispenser] Robotic arm retrieving " + quantity + "x " + productId + ".");
        return true;
    }

    @Override
    public void calibrate() {
        System.out.println("[RoboticArmDispenser] Calibrating arm joints and gripper...");
    }

    @Override
    public String getHardwareType() { return "RoboticArmDispenser"; }
}
