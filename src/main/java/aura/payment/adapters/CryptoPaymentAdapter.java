package aura.payment.adapters;

import aura.interfaces.IPaymentProvider;
import aura.payment.thirdparty.FakeCryptoAPI;

import java.util.HashMap;
import java.util.Map;

// PATTERN: Adapter (Structural) — demonstrates adding a new provider without modifying any existing file
// This is the ONLY new file added in Scenario B — proof of Open/Closed Principle
public class CryptoPaymentAdapter implements IPaymentProvider {
    private final FakeCryptoAPI api;
    private final Map<String, String> txHashes = new HashMap<>();

    public CryptoPaymentAdapter(FakeCryptoAPI api) { this.api = api; }

    @Override
    public boolean processPayment(String transactionId, double amount) {
        String hash = api.sendCrypto("wallet123", amount);
        if (hash != null) { txHashes.put(transactionId, hash); return true; }
        return false;
    }

    @Override
    public boolean refund(String transactionId) {
        String hash = txHashes.get(transactionId);
        return hash != null && api.reverseCrypto(hash);
    }

    @Override public String getProviderName() { return "Crypto"; }
}
