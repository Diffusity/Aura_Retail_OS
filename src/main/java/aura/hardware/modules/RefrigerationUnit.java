package aura.hardware.modules;

import aura.interfaces.IHardwareModule;

/**
 * Simple concrete IHardwareModule representing a refrigeration unit.
 * Used by RefrigerationDecorator via HardwareProxy.
 */
public class RefrigerationUnit implements IHardwareModule {
    private boolean available = true;

    @Override public boolean isAvailable()  { return available; }
    @Override public String getModuleType() { return "REFRIGERATION"; }
    @Override public void initialize()      { System.out.println("[RefrigerationUnit] Cooling system started."); }
    @Override public void shutdown()        { System.out.println("[RefrigerationUnit] Cooling system stopped."); available = false; }
}
