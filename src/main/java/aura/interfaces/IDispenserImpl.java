package aura.interfaces;

// PATTERN: Bridge (Implementor, Structural) — separates what to dispense from how
public interface IDispenserImpl {
    boolean performDispense(String productId, int quantity);
    void calibrate();
    String getHardwareType();
}
