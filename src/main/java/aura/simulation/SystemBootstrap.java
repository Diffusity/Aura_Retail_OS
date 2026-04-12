package aura.simulation;

import aura.events.EventBus;
import aura.kiosk.state.EmergencyLockdownState;
import aura.monitoring.CityMonitoringCenter;
import aura.monitoring.MaintenanceService;
import aura.monitoring.SupplyChainSystem;
import aura.registry.CentralRegistry;

// Wires all observers and subscribes to all events at system startup
public class SystemBootstrap {
    public static void initialize() {
        EventBus bus = EventBus.getInstance();

        CityMonitoringCenter monitor    = new CityMonitoringCenter();
        SupplyChainSystem supplyChain   = new SupplyChainSystem();
        MaintenanceService maintenance  = new MaintenanceService();

        bus.subscribe("HardwareFailureEvent",       monitor);
        bus.subscribe("HardwareFailureEvent",       maintenance);
        bus.subscribe("LowStockEvent",              supplyChain);
        bus.subscribe("TransactionFailedEvent",     monitor);
        bus.subscribe("TransactionCompletedEvent",  supplyChain);
        bus.subscribe("EmergencyModeActivatedEvent", monitor);

        // Emergency mode: transition all kiosks to EmergencyLockdownState
        bus.subscribe("EmergencyModeActivatedEvent", (EventBus.EventListener) (type, data) -> {
            CentralRegistry.getInstance().getAllKiosks().values()
                .forEach(k -> k.setState(new EmergencyLockdownState()));
            System.out.println("[Bootstrap] All kiosks → EmergencyLockdownState.");
        });

        System.out.println("[SystemBootstrap] All observers registered.");
    }
}
