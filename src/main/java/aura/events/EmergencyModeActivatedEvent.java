package aura.events;

/** Fired when the system switches to EMERGENCY mode. */
public class EmergencyModeActivatedEvent {
    public static final String EVENT_TYPE = "EmergencyModeActivatedEvent";
    private final String kioskId;
    private final String reason;

    public EmergencyModeActivatedEvent(String kioskId, String reason) {
        this.kioskId = kioskId;
        this.reason = reason;
    }

    public java.util.Map<String, Object> toMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("kioskId", kioskId);
        map.put("reason", reason);
        return map;
    }
}
