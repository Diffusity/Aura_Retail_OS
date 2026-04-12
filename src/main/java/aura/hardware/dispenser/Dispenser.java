package aura.hardware.dispenser;

import aura.events.EventBus;
import aura.interfaces.IDispenser;
import aura.interfaces.IDispenserImpl;

import java.util.Map;

// PATTERN: Bridge (Abstraction, Structural) — holds a reference to IDispenserImpl; delegates work to it
// Swapping the implementation requires zero changes to this class or any kiosk class
public class Dispenser implements IDispenser {
    private IDispenserImpl impl;

    public Dispenser(IDispenserImpl impl) { this.impl = impl; }

    // PATTERN: Bridge — runtime swap without affecting callers
    @Override
    public void setImpl(IDispenserImpl newImpl) {
        System.out.println("[Dispenser][BRIDGE] Swapping impl: " + (impl != null ? impl.getHardwareType() : "null")
            + " → " + newImpl.getHardwareType());
        this.impl = newImpl;
    }

    @Override
    public boolean dispense(String productId, int quantity) {
        if (!isOperational()) {
            EventBus.getInstance().publish("HardwareFailureEvent",
                Map.of("component", "dispenser", "productId", productId));
            return false;
        }
        return impl.performDispense(productId, quantity);
    }

    @Override public boolean isOperational() { return impl != null; }

    public IDispenserImpl getImpl() { return impl; }
}
