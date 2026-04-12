package aura.verification;

import aura.interfaces.IVerificationModule;

public class EmergencyAccessVerifier implements IVerificationModule {
    @Override
    public boolean verify(String userId, String productId) {
        System.out.println("[EmergencyAccessVerifier] Emergency access granted for user=" + userId);
        return true;
    }
}
