package aura.failure;

import aura.interfaces.IFailureHandler;

// PATTERN: Chain of Responsibility (ConcreteHandler, Behavioral) — attempts retry before passing up
public class RetryHandler implements IFailureHandler {
    private IFailureHandler next;

    @Override
    public void setNext(IFailureHandler next) {
        this.next = next;
    }

    @Override
    public boolean handle(String failureType, String context) {
        System.out.println("[RetryHandler] Attempting retry for failure: " + failureType + " | " + context);
        // Simulate: retry succeeds for transient failures
        if ("TRANSIENT".equals(failureType)) {
            System.out.println("[RetryHandler] Retry succeeded.");
            return true;
        }
        System.out.println("[RetryHandler] Cannot handle. Passing to next handler.");
        if (next != null) return next.handle(failureType, context);
        return false;
    }
}
