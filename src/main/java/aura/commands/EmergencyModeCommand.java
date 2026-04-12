package aura.commands;

import aura.events.EventBus;
import aura.interfaces.ICommand;
import aura.registry.CentralRegistry;

import java.util.Map;

public class EmergencyModeCommand implements ICommand {
    @Override
    public boolean execute() {
        CentralRegistry.getInstance().setSystemMode("EMERGENCY");
        // Priority dispatch: synchronous, fires before all other queued events
        EventBus.getInstance().publish("EmergencyModeActivatedEvent",
            Map.of("activatedBy", "SYSTEM", "timestamp", System.currentTimeMillis()));
        return true;
    }

    @Override
    public boolean undo() {
        CentralRegistry.getInstance().setSystemMode("NORMAL");
        return true;
    }

    @Override public void log() { System.out.println("[EmergencyModeCommand] Emergency mode activated."); }
    @Override public String getCommandType() { return "EMERGENCY_MODE"; }
}
