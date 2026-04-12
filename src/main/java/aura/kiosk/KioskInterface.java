package aura.kiosk;

import aura.commands.CommandInvoker;
import aura.commands.RefundCommand;
import aura.commands.RestockCommand;
import aura.interfaces.ICommand;
import aura.persistence.PersistenceManager;
import aura.transaction.TransactionIterator;

import java.util.LinkedHashMap;
import java.util.Map;

// PATTERN: Facade (Structural) — single simplified entry point; hides all subsystem complexity
// VERIFICATION: zero imports from hardware/ or payment/ — proves low coupling
public class KioskInterface {
    private final BaseKiosk kiosk;

    public KioskInterface(BaseKiosk kiosk) { this.kiosk = kiosk; }

    public boolean purchaseItem(String userId, String productId, int quantity) {
        return kiosk.processPurchase(userId, productId, quantity);
    }

    public boolean refundTransaction(String transactionId) {
        CommandInvoker invoker = new CommandInvoker();
        ICommand refund = new RefundCommand(transactionId, kiosk);
        return invoker.execute(refund);
    }

    public Map<String, Object> runDiagnostics() {
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("kioskId", kiosk.getKioskId());
        report.put("operationalStatus", kiosk.checkOperationalStatus());
        report.put("currentState", kiosk.getState().getStateName());
        report.put("dispenserOperational", kiosk.getDispenser() != null && kiosk.getDispenser().isOperational());
        report.put("recentTransactions", new TransactionIterator(
            PersistenceManager.getInstance().loadTransactions(), kiosk.getKioskId()
        ).getLastN(10));
        return report;
    }

    public boolean restockInventory(String productId, int quantity) {
        CommandInvoker invoker = new CommandInvoker();
        ICommand restock = new RestockCommand(productId, quantity, kiosk);
        return invoker.execute(restock);
    }

    // Expose underlying kiosk for decorators and builder
    public BaseKiosk getKiosk() { return kiosk; }
}
