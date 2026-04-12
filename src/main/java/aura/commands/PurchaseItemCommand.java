package aura.commands;

import aura.events.EventBus;
import aura.interfaces.ICommand;
import aura.interfaces.IPaymentProvider;
import aura.inventory.SecureInventoryProxy;
import aura.kiosk.BaseKiosk;
import aura.payment.PaymentProviderRegistry;
import aura.persistence.PersistenceManager;
import aura.transaction.Transaction;
import aura.transaction.TransactionMemento;

import java.util.Map;
import java.util.UUID;

// PATTERN: Command (ConcreteCommand, Behavioral) + Memento (Behavioral) — full purchase lifecycle
// System constraint: atomic transaction — either all 5 steps complete, or all are rolled back
public class PurchaseItemCommand implements ICommand {
    private final BaseKiosk kiosk;
    private final String userId, productId;
    private final int quantity;
    private final double price;
    private final String paymentProvider;
    private TransactionMemento savedState; // PATTERN: Memento — snapshot before execution
    private final String transactionId;

    public PurchaseItemCommand(BaseKiosk kiosk, String userId, String productId, int qty, double price) {
        this.kiosk = kiosk;
        this.userId = userId;
        this.productId = productId;
        this.quantity = qty;
        this.price = price;
        this.paymentProvider = "CreditCard";
        this.transactionId = UUID.randomUUID().toString();
    }

    @Override
    public boolean execute() {
        SecureInventoryProxy inv = SecureInventoryProxy.getInstance();

        // Step 1: Save state snapshot before any mutation (Memento)
        savedState = new TransactionMemento(transactionId, productId, quantity, price);

        // Step 2: Reserve inventory (locks stock, does NOT commit yet)
        if (!inv.reserveStock(productId, quantity)) {
            EventBus.getInstance().publish("TransactionFailedEvent",
                Map.of("reason", "INSUFFICIENT_STOCK", "productId", productId, "txId", transactionId));
            return false;
        }

        // Step 3: Process payment
        IPaymentProvider provider = PaymentProviderRegistry.getInstance().getProvider(paymentProvider);
        if (provider == null || !provider.processPayment(transactionId, price * quantity)) {
            inv.releaseReservation(productId, quantity); // rollback step 2
            EventBus.getInstance().publish("TransactionFailedEvent",
                Map.of("reason", "PAYMENT_FAILED", "txId", transactionId));
            return false;
        }

        // Step 4: Dispense product
        if (kiosk.getDispenser() == null || !kiosk.getDispenser().dispense(productId, quantity)) {
            provider.refund(transactionId);              // rollback step 3
            inv.releaseReservation(productId, quantity); // rollback step 2
            EventBus.getInstance().publish("TransactionFailedEvent",
                Map.of("reason", "DISPENSER_FAILURE", "txId", transactionId));
            return false;
        }

        // Step 5: Commit inventory — ONLY here, after full success
        inv.commitInventoryUpdate(productId, quantity, transactionId);

        // Step 6: Publish success event
        EventBus.getInstance().publish("TransactionCompletedEvent",
            Map.of("txId", transactionId, "productId", productId, "qty", quantity, "amount", price * quantity));

        // Step 7: Persist transaction record
        PersistenceManager.getInstance().saveTransaction(
            new Transaction(transactionId, userId, productId, quantity, price));

        return true;
    }

    @Override
    public boolean undo() {
        if (savedState == null) return false;
        IPaymentProvider provider = PaymentProviderRegistry.getInstance().getProvider(paymentProvider);
        if (provider == null) return false;
        boolean refunded = provider.refund(transactionId);
        if (refunded) SecureInventoryProxy.getInstance().releaseReservation(productId, quantity);
        return refunded;
    }

    @Override
    public void log() {
        System.out.println("[PurchaseItemCommand] txId=" + transactionId +
            " product=" + productId + " qty=" + quantity + " total=" + String.format("%.2f", price * quantity));
    }

    @Override public String getCommandType() { return "PURCHASE"; }
}
