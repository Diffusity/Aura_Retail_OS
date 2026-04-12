package aura.events;

/** Fired when a hardware component fails. */
public class HardwareFailureEvent {
    public static final String EVENT_TYPE = "HardwareFailureEvent";
    private final String component;
    private final String kioskId;
    private final String failureDetails;

    public HardwareFailureEvent(String component, String kioskId, String failureDetails) {
        this.component = component;
        this.kioskId = kioskId;
        this.failureDetails = failureDetails;
    }

    public java.util.Map<String, Object> toMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("component", component);
        map.put("kioskId", kioskId);
        map.put("failureDetails", failureDetails);
        return map;
    }
}
