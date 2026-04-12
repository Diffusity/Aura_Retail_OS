package aura.verification;

import aura.interfaces.IVerificationModule;

public class AgeVerifier implements IVerificationModule {
    @Override
    public boolean verify(String userId, String productId) {
        System.out.println("[AgeVerifier] Age verification for user=" + userId);
        return true;
    }
}
