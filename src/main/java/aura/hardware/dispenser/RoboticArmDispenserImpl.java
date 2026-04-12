package aura.hardware.dispenser;

import aura.interfaces.IDispenserImpl;

// PATTERN: Bridge (ConcreteImplementor, Structural)
public class RoboticArmDispenserImpl implements IDispenserImpl {
    @Override
    public boolean performDispense(String productId, int qty) {
        System.out.println("[RoboticArm] Extending arm to retrieve: " + productId + " qty=" + qty);
        return true;
    }
    @Override public void calibrate() { System.out.println("[RoboticArm] Running arm calibration sequence..."); }
    @Override public String getHardwareType() { return "ROBOTIC_ARM"; }
}
