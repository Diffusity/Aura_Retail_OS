package aura.payment.thirdparty;

// Yet another incompatible API — UPI gateway
public class UPIGateway {
    public boolean sendPaymentRequest(String vpa, double amount, String ref) {
        System.out.println("[UPIGateway] UPI request to " + vpa + " amount=" + amount + " ref=" + ref);
        return true;
    }
    public boolean initiateRefund(String ref, double amount) {
        System.out.println("[UPIGateway] Refund ref=" + ref);
        return true;
    }
}
