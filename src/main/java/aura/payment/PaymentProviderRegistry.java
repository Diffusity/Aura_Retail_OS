package aura.payment;

import aura.interfaces.IPaymentProvider;
import aura.payment.adapters.CreditCardAdapter;
import aura.payment.adapters.DigitalWalletAdapter;
import aura.payment.adapters.UPIAdapter;
import aura.payment.thirdparty.LegacyCreditCardAPI;
import aura.payment.thirdparty.UPIGateway;
import aura.payment.thirdparty.WalletSDK;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

// PATTERN: Singleton (Creational) — holds and retrieves all registered payment adapters
public class PaymentProviderRegistry {
    private static volatile PaymentProviderRegistry instance;
    private final Map<String, IPaymentProvider> providers = new LinkedHashMap<>();

    private PaymentProviderRegistry() {
        // Register built-in adapters at construction
        register(new CreditCardAdapter(new LegacyCreditCardAPI()));
        register(new DigitalWalletAdapter(new WalletSDK()));
        register(new UPIAdapter(new UPIGateway()));
    }

    public static PaymentProviderRegistry getInstance() {
        if (instance == null) {
            synchronized (PaymentProviderRegistry.class) {
                if (instance == null) instance = new PaymentProviderRegistry();
            }
        }
        return instance;
    }

    // PATTERN: Open/Closed — adding a new provider never requires modifying this class
    public void register(IPaymentProvider provider) {
        providers.put(provider.getProviderName(), provider);
        System.out.println("[PaymentRegistry] Registered: " + provider.getProviderName());
    }

    public IPaymentProvider getProvider(String name) { return providers.get(name); }
    public Collection<IPaymentProvider> getAllProviders() { return providers.values(); }
}
