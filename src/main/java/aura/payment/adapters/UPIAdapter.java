package aura.payment.adapters;

import aura.interfaces.IPaymentProvider;
import aura.payment.thirdparty.UPIGateway;

// PATTERN: Adapter (Structural) — translates UPIGateway into IPaymentProvider
public class UPIAdapter implements IPaymentProvider {
    private final UPIGateway gateway;

    public UPIAdapter(UPIGateway gateway) { this.gateway = gateway; }

    @Override
    public boolean processPayment(String transactionId, double amount) {
        return gateway.sendPaymentRequest("user@upi", amount, transactionId);
    }

    @Override public boolean refund(String transactionId) { return gateway.initiateRefund(transactionId, 0); }
    @Override public String getProviderName() { return "UPI"; }
}
