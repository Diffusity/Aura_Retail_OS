package aura.hardware;

import aura.interfaces.IDispenser;

// PATTERN: Proxy (Structural) — adds health checking before delegating to real Dispenser
// STUB — full implementation in Phase 3 (Subtask 2)
public class HardwareProxy implements IDispenser {
    private final IDispenser realDispenser;

    public HardwareProxy(IDispenser realDispenser) {
        this.realDispenser = realDispenser;
    }

    @Override
    public boolean dispense(String productId, int quantity) {
        if (!isOperational()) {
            System.out.println("[HardwareProxy] Health check failed — dispense blocked.");
            return false;
        }
        System.out.println("[HardwareProxy] Health check passed — delegating dispense.");
        return realDispenser.dispense(productId, quantity);
    }

    @Override
    public boolean isOperational() {
        // TODO (Phase 3): integrate with hardware health monitoring
        return realDispenser.isOperational();
    }

    @Override
    public void setImpl(aura.interfaces.IDispenserImpl impl) {
        realDispenser.setImpl(impl);
    }
}
