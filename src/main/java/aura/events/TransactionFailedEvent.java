package aura.events;

/** Fired when a transaction fails at any step. */
public class TransactionFailedEvent {
    public static final String EVENT_TYPE = "TransactionFailedEvent";
    private final String txnId;
    private final String reason;
    private final String kioskId;

    public TransactionFailedEvent(String txnId, String reason, String kioskId) {
        this.txnId = txnId;
        this.reason = reason;
        this.kioskId = kioskId;
    }

    public java.util.Map<String, Object> toMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("txnId", txnId);
        map.put("reason", reason);
        map.put("kioskId", kioskId);
        return map;
    }
}
