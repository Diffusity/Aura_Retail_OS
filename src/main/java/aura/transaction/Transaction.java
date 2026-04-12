package aura.transaction;

public class Transaction {
    public enum Status { PENDING, COMPLETED, FAILED, REFUNDED }

    private final String txnId, userId, productId;
    private final int qty;
    private final double amount;
    private Status status;
    private final long timestamp;

    public Transaction(String txnId, String userId, String productId, int qty, double amount) {
        this.txnId = txnId; this.userId = userId; this.productId = productId;
        this.qty = qty; this.amount = amount;
        this.status = Status.COMPLETED;
        this.timestamp = System.currentTimeMillis();
    }

    public String getTxnId()     { return txnId; }
    public String getUserId()    { return userId; }
    public String getProductId() { return productId; }
    public int getQty()          { return qty; }
    public double getAmount()    { return amount; }
    public String getStatus()    { return status.name(); }
    public long getTimestamp()   { return timestamp; }
    public void setStatus(Status s) { this.status = s; }
}
