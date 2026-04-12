package aura.hardware.modules;

import aura.interfaces.IHardwareModule;

// PATTERN: Decorator (Structural) — base decorator for optional hardware modules
// STUB — full implementation in Phase 3 (Subtask 2)
public abstract class KioskDecorator implements IHardwareModule {
    protected final IHardwareModule wrapped;

    public KioskDecorator(IHardwareModule wrapped) {
        this.wrapped = wrapped;
    }

    @Override public boolean isAvailable()   { return wrapped.isAvailable(); }
    @Override public String getModuleType()  { return wrapped.getModuleType(); }
    @Override public void initialize()       { wrapped.initialize(); }
    @Override public void shutdown()         { wrapped.shutdown(); }
}
