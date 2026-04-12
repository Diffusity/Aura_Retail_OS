package aura.payment.thirdparty;

// Simulates an incompatible 3rd-party API — intentionally different method signatures
public class LegacyCreditCardAPI {
    public int initiateCharge(double dollarAmount, String cardToken) {
        System.out.println("[CreditCardAPI] Charging $" + String.format("%.2f", dollarAmount) + " token=" + cardToken);
        return 12345; // simulated charge reference ID
    }
    public boolean reverseCharge(int chargeRefId) {
        System.out.println("[CreditCardAPI] Reversing charge ref=" + chargeRefId);
        return true;
    }
}
