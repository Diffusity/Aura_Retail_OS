package aura.events;

/** Fired when a transaction completes successfully. */
public class TransactionCompletedEvent {
    public static final String EVENT_TYPE = "TransactionCompletedEvent";
    private final String txnId;
    private final String productId;
    private final double amount;
    private final String kioskId;

    public TransactionCompletedEvent(String txnId, String productId, double amount, String kioskId) {
        this.txnId = txnId;
        this.productId = productId;
        this.amount = amount;
        this.kioskId = kioskId;
    }

    public java.util.Map<String, Object> toMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("txnId", txnId);
        map.put("productId", productId);
        map.put("amount", amount);
        map.put("kioskId", kioskId);
        return map;
    }
}
