package aura.failure;

import aura.interfaces.IFailureHandler;

// PATTERN: Chain of Responsibility (ConcreteHandler, Behavioral) — attempts hardware recalibration
public class RecalibrationHandler implements IFailureHandler {
    private IFailureHandler next;

    @Override
    public void setNext(IFailureHandler next) {
        this.next = next;
    }

    @Override
    public boolean handle(String failureType, String context) {
        System.out.println("[RecalibrationHandler] Attempting recalibration for: " + failureType + " | " + context);
        if ("DISPENSER".equals(failureType) || "CALIBRATION_NEEDED".equals(failureType)) {
            System.out.println("[RecalibrationHandler] Recalibration completed successfully.");
            return true;
        }
        System.out.println("[RecalibrationHandler] Cannot handle. Passing to next handler.");
        if (next != null) return next.handle(failureType, context);
        return false;
    }
}
