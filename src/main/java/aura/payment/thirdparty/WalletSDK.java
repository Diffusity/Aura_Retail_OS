package aura.payment.thirdparty;

// Completely different API signature — incompatible with LegacyCreditCardAPI
public class WalletSDK {
    public String deductBalance(String walletId, int amountInCents) {
        System.out.println("[WalletSDK] Deducting " + amountInCents + " cents from wallet=" + walletId);
        return "WLT-OK";
    }
    public boolean creditBack(String receiptCode) {
        System.out.println("[WalletSDK] Crediting back receipt=" + receiptCode);
        return true;
    }
}
