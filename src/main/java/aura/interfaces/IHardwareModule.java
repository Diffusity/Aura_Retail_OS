package aura.interfaces;

// PATTERN: Abstraction — optional hardware capability contract
public interface IHardwareModule {
    boolean isAvailable();
    String getModuleType();
    void initialize();
    void shutdown();
}
