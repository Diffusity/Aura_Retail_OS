package aura.verification;

import aura.interfaces.IVerificationModule;

public class PrescriptionVerifier implements IVerificationModule {
    @Override
    public boolean verify(String userId, String productId) {
        System.out.println("[PrescriptionVerifier] Checking prescription for user=" + userId
                + " product=" + productId);
        return true; // Simulated: always valid in demo
    }
}
