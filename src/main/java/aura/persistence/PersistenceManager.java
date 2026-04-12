package aura.persistence;

import aura.interfaces.IInventoryItem;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// PATTERN: Singleton (Creational) — single point of I/O; prevents file conflicts between subsystems
public class PersistenceManager {
    private static volatile PersistenceManager instance;
    private static final String INVENTORY_FILE   = "data/inventory.json";
    private static final String TRANSACTIONS_FILE = "data/transactions.json";
    private static final String CONFIG_FILE       = "data/config.json";

    private PersistenceManager() {
        ensureDirectoryExists("data/");
    }

    public static PersistenceManager getInstance() {
        if (instance == null) {
            synchronized (PersistenceManager.class) {
                if (instance == null) instance = new PersistenceManager();
            }
        }
        return instance;
    }

    // Serialize a list of inventory items to JSON
    public void saveInventory(List<IInventoryItem> items) {
        JSONArray arr = new JSONArray();
        for (IInventoryItem item : items) {
            arr.put(serializeItem(item));
        }
        writeFile(INVENTORY_FILE, arr.toString(2));
    }

    public List<Map<String, Object>> loadInventory() {
        return parseJsonArray(readFile(INVENTORY_FILE));
    }

    // Recursive bundle serializer
    private JSONObject serializeItem(IInventoryItem item) {
        JSONObject obj = new JSONObject();
        obj.put("id", item.getId());
        obj.put("name", item.getName());
        obj.put("basePrice", item.getBasePrice());
        obj.put("availableStock", item.getAvailableStock());
        obj.put("isAvailable", item.isAvailable());
        obj.put("type", item.getChildren().isEmpty() ? "product" : "bundle");
        if (!item.getChildren().isEmpty()) {
            JSONArray children = new JSONArray();
            item.getChildren().forEach(child -> children.put(serializeItem(child)));
            obj.put("children", children);
        }
        return obj;
    }

    // Append-only transaction log — requires a Transaction-like map for now
    public void saveTransactionMap(Map<String, Object> txn) {
        List<Map<String, Object>> existing = loadTransactions();
        JSONArray arr = new JSONArray();
        existing.forEach(m -> arr.put(new JSONObject(m)));
        JSONObject entry = new JSONObject(txn);
        arr.put(entry);
        writeFile(TRANSACTIONS_FILE, arr.toString(2));
    }

    public List<Map<String, Object>> loadTransactions() {
        return parseJsonArray(readFile(TRANSACTIONS_FILE));
    }

    public void saveRegistry(aura.registry.CentralRegistry registry) {
        JSONObject obj = new JSONObject();
        obj.put("systemMode", registry.getSystemMode());
        writeFile(CONFIG_FILE, obj.toString(2));
    }

    public Map<String, Object> loadConfig() {
        String raw = readFile(CONFIG_FILE);
        if (raw == null || raw.isBlank()) return new HashMap<>();
        try {
            JSONObject obj = new JSONObject(raw);
            Map<String, Object> map = new HashMap<>();
            obj.keys().forEachRemaining(k -> map.put(k, obj.get(k)));
            return map;
        } catch (Exception e) { return new HashMap<>(); }
    }

    private void writeFile(String path, String content) {
        try (java.io.FileWriter fw = new java.io.FileWriter(path)) { fw.write(content); }
        catch (Exception e) { System.err.println("[PersistenceManager] Write failed: " + path); }
    }

    private String readFile(String path) {
        try { return new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(path))); }
        catch (Exception e) { return ""; }
    }

    private List<Map<String, Object>> parseJsonArray(String raw) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (raw == null || raw.isBlank()) return result;
        try {
            JSONArray arr = new JSONArray(raw);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                Map<String, Object> map = new HashMap<>();
                obj.keys().forEachRemaining(k -> map.put(k, obj.get(k)));
                result.add(map);
            }
        } catch (Exception ignored) {}
        return result;
    }

    private void ensureDirectoryExists(String dir) {
        new java.io.File(dir).mkdirs();
    }
}
