package aura.transaction;

// PATTERN: Memento (Behavioral) — immutable snapshot of transaction state before execution
// All fields final — no setters — guarantees rollback fidelity
public class TransactionMemento {
    private final String transactionId;
    private final String productId;
    private final int quantity;
    private final double price;
    private final long timestamp;
    private final String status;

    public TransactionMemento(String txId, String productId, int qty, double price) {
        this.transactionId = txId;
        this.productId = productId;
        this.quantity = qty;
        this.price = price;
        this.timestamp = System.currentTimeMillis();
        this.status = "PENDING";
    }

    public String getTransactionId() { return transactionId; }
    public String getProductId()     { return productId; }
    public int getQuantity()         { return quantity; }
    public double getPrice()         { return price; }
    public String getStatus()        { return status; }
    public long getTimestamp()       { return timestamp; }
}
