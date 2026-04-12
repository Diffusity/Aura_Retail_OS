package aura.simulation;

import aura.interfaces.IPaymentProvider;
import aura.payment.PaymentProviderRegistry;
import aura.payment.adapters.CryptoPaymentAdapter;
import aura.payment.thirdparty.FakeCryptoAPI;

import java.util.stream.Collectors;

// PATTERN: Adapter demo — Scenario B
public class ScenarioB_NewPaymentProvider {
    public static void main(String[] args) {
        SystemBootstrap.initialize();
        System.out.println("\n=== PATH B SCENARIO B: Integrating a New Payment Provider ===\n");

        PaymentProviderRegistry registry = PaymentProviderRegistry.getInstance();
        System.out.println("[ADAPTER] Existing providers: " + registry.getAllProviders().stream()
            .map(IPaymentProvider::getProviderName).collect(Collectors.joining(", ")));

        // PATTERN: Adapter — only a new CryptoPaymentAdapter class was added; nothing else changed
        registry.register(new CryptoPaymentAdapter(new FakeCryptoAPI()));
        System.out.println("[ADAPTER] Registered CryptoPaymentAdapter at runtime.");
        System.out.println("[ADAPTER] No existing file was modified. Open/Closed Principle demonstrated.");

        IPaymentProvider crypto = registry.getProvider("Crypto");
        boolean result = crypto.processPayment("TX-CRYPTO-001", 49.99);
        System.out.println("[ADAPTER] Crypto payment via IPaymentProvider.processPayment() → " + result);
        System.out.println("[ADAPTER] FakeCryptoAPI.sendCrypto() called internally — incompatible API hidden.");
        System.out.println("=== SCENARIO B COMPLETE ===");
    }
}
