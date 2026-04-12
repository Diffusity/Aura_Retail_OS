package aura.hardware;

import aura.events.EventBus;
import aura.interfaces.IHardwareModule;

import java.util.Map;

// PATTERN: Proxy (Structural) — checks hardware availability before delegating to real module
// If unavailable: publishes HardwareFailureEvent; triggers Chain of Responsibility via MaintenanceService
public class HardwareProxy implements IHardwareModule {
    private final IHardwareModule realModule;
    private boolean forcedUnavailable = false;

    public HardwareProxy(IHardwareModule realModule) { this.realModule = realModule; }

    @Override
    public boolean isAvailable() {
        if (forcedUnavailable) return false;
        return realModule.isAvailable();
    }

    @Override
    public void initialize() {
        if (isAvailable()) realModule.initialize();
        else {
            System.out.println("[HardwareProxy] Cannot initialize: module unavailable.");
            EventBus.getInstance().publish("HardwareFailureEvent",
                Map.of("component", getModuleType(), "reason", "UNAVAILABLE_ON_INIT"));
        }
    }

    // Allows tests and Scenario A to simulate hardware failure
    public void setForcedUnavailable(boolean unavailable) { this.forcedUnavailable = unavailable; }

    @Override public String getModuleType() { return realModule.getModuleType(); }
    @Override public void shutdown()        { if (realModule.isAvailable()) realModule.shutdown(); }
}
