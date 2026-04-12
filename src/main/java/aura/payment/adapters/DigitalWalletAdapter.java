package aura.payment.adapters;

import aura.interfaces.IPaymentProvider;
import aura.payment.thirdparty.WalletSDK;

import java.util.HashMap;
import java.util.Map;

// PATTERN: Adapter (Structural) — translates WalletSDK into IPaymentProvider
public class DigitalWalletAdapter implements IPaymentProvider {
    private final WalletSDK sdk;
    private final Map<String, String> receipts = new HashMap<>();

    public DigitalWalletAdapter(WalletSDK sdk) { this.sdk = sdk; }

    @Override
    public boolean processPayment(String transactionId, double amount) {
        int cents = (int)(amount * 100);
        String receipt = sdk.deductBalance(transactionId, cents);
        if ("WLT-OK".equals(receipt)) { receipts.put(transactionId, receipt); return true; }
        return false;
    }

    @Override
    public boolean refund(String transactionId) {
        String r = receipts.get(transactionId);
        return r != null && sdk.creditBack(r);
    }

    @Override public String getProviderName() { return "DigitalWallet"; }
}
