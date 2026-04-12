package aura.failure;

import aura.interfaces.IFailureHandler;

// PATTERN: Chain of Responsibility (ConcreteHandler, Behavioral) — last resort: escalate to human technician
public class TechnicianAlertHandler implements IFailureHandler {
    private IFailureHandler next;

    @Override
    public void setNext(IFailureHandler next) {
        this.next = next;
    }

    @Override
    public boolean handle(String failureType, String context) {
        System.out.println("[TechnicianAlertHandler] CRITICAL: Alerting technician for failure: "
                + failureType + " | " + context);
        System.out.println("[TechnicianAlertHandler] Technician dispatch request sent via City Monitoring Center.");
        // Always returns true — this is the terminal handler
        return true;
    }
}
