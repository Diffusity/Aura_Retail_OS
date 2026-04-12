package aura.interfaces;

// PATTERN: Adapter (Target, Structural) — uniform interface for all payment systems
public interface IPaymentProvider {
    boolean processPayment(String transactionId, double amount);
    boolean refund(String transactionId);
    String getProviderName();
}
