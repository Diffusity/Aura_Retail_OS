package aura.hardware.dispenser;

import aura.interfaces.IDispenser;
import aura.interfaces.IDispenserImpl;

// PATTERN: Bridge (Abstraction, Structural) — decouples dispenser logic from its hardware implementation
// The kiosk holds a reference to IDispenser, never to the concrete *DispenserImpl
public class Dispenser implements IDispenser {
    private IDispenserImpl impl;

    public Dispenser(IDispenserImpl impl) {
        this.impl = impl;
    }

    @Override
    public boolean dispense(String productId, int quantity) {
        System.out.println("[Dispenser] Delegating dispense to impl: " + impl.getHardwareType());
        return impl.performDispense(productId, quantity);
    }

    @Override
    public boolean isOperational() {
        // For now, always true; in prod: check impl health via HardwareProxy
        return true;
    }

    @Override
    public void setImpl(IDispenserImpl impl) {
        System.out.println("[Dispenser][BRIDGE] Swapping implementation to: " + impl.getHardwareType());
        this.impl = impl;
    }

    public IDispenserImpl getImpl() { return impl; }
}
