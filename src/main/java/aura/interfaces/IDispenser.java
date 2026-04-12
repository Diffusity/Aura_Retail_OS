package aura.interfaces;

// PATTERN: Abstraction (Structural) — hides hardware implementation from kiosk logic
public interface IDispenser {
    boolean dispense(String productId, int quantity);
    boolean isOperational();
    void setImpl(IDispenserImpl impl); // supports Bridge swap
}
