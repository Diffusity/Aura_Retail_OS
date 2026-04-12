package aura.registry;

import aura.persistence.PersistenceManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// PATTERN: Singleton (Creational) — one global store for system-wide configuration and kiosk references
public class CentralRegistry {
    private static volatile CentralRegistry instance;
    private final Map<String, Object> config = new HashMap<>();
    private final Map<String, Object> registeredKiosks = new HashMap<>(); // Using Object to avoid forward ref to BaseKiosk
    private String systemMode = "NORMAL"; // NORMAL | EMERGENCY

    private CentralRegistry() {
        loadFromPersistence();
    }

    // Double-checked locking for thread safety
    public static CentralRegistry getInstance() {
        if (instance == null) {
            synchronized (CentralRegistry.class) {
                if (instance == null) instance = new CentralRegistry();
            }
        }
        return instance;
    }

    public void registerKiosk(String id, Object kiosk) { registeredKiosks.put(id, kiosk); }
    public Object getKiosk(String id) { return registeredKiosks.get(id); }
    public Map<String, Object> getAllKiosks() { return Collections.unmodifiableMap(registeredKiosks); }

    public void setSystemMode(String mode) {
        this.systemMode = mode;
        persistState();
    }
    public String getSystemMode() { return systemMode; }

    public void setConfig(String key, Object value) { config.put(key, value); persistState(); }
    public Object getConfig(String key) { return config.get(key); }

    private void persistState() { PersistenceManager.getInstance().saveRegistry(this); }

    private void loadFromPersistence() {
        Map<String, Object> saved = PersistenceManager.getInstance().loadConfig();
        if (saved.containsKey("systemMode")) this.systemMode = (String) saved.get("systemMode");
    }
}
