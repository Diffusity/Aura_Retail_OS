package aura.payment.thirdparty;

// For Scenario B — crypto payment API
public class FakeCryptoAPI {
    public String sendCrypto(String walletAddr, double amount) {
        System.out.println("[CryptoAPI] Sending " + amount + " to " + walletAddr);
        return "CRYPTO-TX-" + System.currentTimeMillis();
    }
    public boolean reverseCrypto(String txHash) {
        System.out.println("[CryptoAPI] Reversing tx=" + txHash);
        return true;
    }
}
