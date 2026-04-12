package aura.payment.adapters;

import aura.interfaces.IPaymentProvider;
import aura.payment.thirdparty.LegacyCreditCardAPI;

import java.util.HashMap;
import java.util.Map;

// PATTERN: Adapter (Structural) — translates LegacyCreditCardAPI into IPaymentProvider
public class CreditCardAdapter implements IPaymentProvider {
    private final LegacyCreditCardAPI legacyApi;
    private final Map<String, Integer> chargeRefs = new HashMap<>(); // txId → chargeRefId

    public CreditCardAdapter(LegacyCreditCardAPI api) { this.legacyApi = api; }

    @Override
    public boolean processPayment(String transactionId, double amount) {
        int ref = legacyApi.initiateCharge(amount, transactionId);
        if (ref != -1) { chargeRefs.put(transactionId, ref); return true; }
        return false;
    }

    @Override
    public boolean refund(String transactionId) {
        Integer ref = chargeRefs.get(transactionId);
        return ref != null && legacyApi.reverseCharge(ref);
    }

    @Override public String getProviderName() { return "CreditCard"; }
}
