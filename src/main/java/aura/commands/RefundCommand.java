package aura.commands;

import aura.interfaces.ICommand;
import aura.interfaces.IPaymentProvider;
import aura.kiosk.BaseKiosk;
import aura.payment.PaymentProviderRegistry;

public class RefundCommand implements ICommand {
    private final String transactionId;
    private final BaseKiosk kiosk;

    public RefundCommand(String txId, BaseKiosk kiosk) {
        this.transactionId = txId;
        this.kiosk = kiosk;
    }

    @Override
    public boolean execute() {
        IPaymentProvider provider = PaymentProviderRegistry.getInstance().getProvider("CreditCard");
        if (provider == null) return false;
        boolean refunded = provider.refund(transactionId);
        if (refunded) System.out.println("[RefundCommand] Refund successful for txId=" + transactionId);
        return refunded;
    }

    @Override public boolean undo() { return false; } // refund cannot be undone
    @Override public void log() { System.out.println("[RefundCommand] txId=" + transactionId); }
    @Override public String getCommandType() { return "REFUND"; }
}
