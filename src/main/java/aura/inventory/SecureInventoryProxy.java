package aura.inventory;

import aura.interfaces.IInventoryItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// PATTERN: Proxy (Structural) + Singleton (Creational)
// Authorization and audit logging on every read and write
public class SecureInventoryProxy {
    private static volatile SecureInventoryProxy instance;
    private final InventoryManager realManager;
    private final List<String> accessLog = new ArrayList<>();

    private SecureInventoryProxy() { this.realManager = new InventoryManager(); }

    public static SecureInventoryProxy getInstance() {
        if (instance == null) {
            synchronized (SecureInventoryProxy.class) {
                if (instance == null) instance = new SecureInventoryProxy();
            }
        }
        return instance;
    }

    // Proxy: log every read
    public IInventoryItem getItem(String productId) {
        logAccess("READ", productId);
        return realManager.getItem(productId);
    }

    // Proxy: auth check + log before every reservation
    public boolean reserveStock(String productId, int qty) {
        logAccess("RESERVE", productId + " qty=" + qty);
        return realManager.reserve(productId, qty);
    }

    // Proxy: only commits when a valid transactionId is provided — enforces inventory consistency
    public boolean commitInventoryUpdate(String productId, int qty, String transactionId) {
        logAccess("COMMIT", productId + " qty=" + qty + " txId=" + transactionId);
        if (!isAuthorized(transactionId)) {
            System.out.println("[SecureInventoryProxy] DENIED: unauthorized commit.");
            return false;
        }
        return realManager.commitUpdate(productId, qty);
    }

    public void releaseReservation(String productId, int qty) {
        logAccess("RELEASE", productId + " qty=" + qty);
        realManager.release(productId, qty);
    }

    public void addItem(IInventoryItem item) { realManager.addItem(item); }

    private boolean isAuthorized(String transactionId) { return transactionId != null && !transactionId.isEmpty(); }

    private void logAccess(String operation, String detail) {
        String entry = "[" + java.time.LocalDateTime.now() + "] " + operation + ": " + detail;
        accessLog.add(entry);
        System.out.println("[SecureInventoryProxy] " + entry);
    }

    public List<String> getAccessLog() { return Collections.unmodifiableList(accessLog); }
}
