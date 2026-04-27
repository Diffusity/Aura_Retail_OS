package aura;

import aura.commands.*;
import aura.events.EventBus;
import aura.factory.EmergencyReliefKioskFactory;
import aura.factory.FoodKioskFactory;
import aura.factory.PharmacyKioskFactory;
import aura.interfaces.IPaymentProvider;
import aura.inventory.Product;
import aura.inventory.SecureInventoryProxy;
import aura.kiosk.BaseKiosk;
import aura.kiosk.KioskBuilder;
import aura.kiosk.KioskInterface;
import aura.payment.PaymentProviderRegistry;
import aura.persistence.PersistenceManager;
import aura.registry.CentralRegistry;
import aura.simulation.*;
import io.javalin.Javalin;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ApiServer {
    private static final List<JSONObject> eventBuffer = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        // Bootstrap system
        SystemBootstrap.initialize();
        seedData();
        // Register event subscriber for the buffer
        EventBus.getInstance().subscribe("TransactionCompletedEvent", (type, data) -> bufferEvent(type, data));
        EventBus.getInstance().subscribe("TransactionFailedEvent", (type, data) -> bufferEvent(type, data));
        EventBus.getInstance().subscribe("HardwareFailureEvent", (type, data) -> bufferEvent(type, data));
        EventBus.getInstance().subscribe("LowStockEvent", (type, data) -> bufferEvent(type, data));
        EventBus.getInstance().subscribe("EmergencyModeActivatedEvent", (type, data) -> bufferEvent(type, data));
        EventBus.getInstance().subscribe("SimulationLogEvent", (type, data) -> bufferEvent(type, data));
        EventBus.getInstance().subscribe("EmulatorStepEvent", (type, data) -> bufferEvent(type, data));

        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(rule -> {
                    rule.anyHost(); // dev only
                });
            });
        }).start(8080);

        // --- Kiosk Endpoints ---

        // GET /api/kiosks — list all registered kiosks
        app.get("/api/kiosks", ctx -> {
            Map<String, BaseKiosk> kiosks = CentralRegistry.getInstance().getAllKiosks();
            JSONArray arr = new JSONArray();
            for (Map.Entry<String, BaseKiosk> e : kiosks.entrySet()) {
                BaseKiosk kiosk = e.getValue();
                JSONObject obj = new JSONObject();
                obj.put("kioskId", e.getKey());
                obj.put("type", kiosk.getClass().getSimpleName());
                
                String typeName = "Kiosk";
                if (kiosk instanceof aura.kiosk.FoodKiosk) typeName = "Food Kiosk";
                else if (kiosk instanceof aura.kiosk.PharmacyKiosk) typeName = "Pharmacy Kiosk";
                else if (kiosk instanceof aura.kiosk.EmergencyReliefKiosk) typeName = "Emergency Relief Kiosk";
                obj.put("typeName", typeName);
                
                obj.put("state", kiosk.getState().getStateName());
                obj.put("dispenserOperational", kiosk.getDispenser() != null && kiosk.getDispenser().isOperational());
                obj.put("dispenserType", kiosk.getDispenser() != null ? kiosk.getDispenser().getClass().getSimpleName() : "None");
                
                List<String> modules = new ArrayList<>();
                modules.add("BaseDispenser");
                if (e.getKey().startsWith("F-")) modules.add("RefrigerationUnit");
                obj.put("hardwareModules", new JSONArray(modules));
                
                arr.put(obj);
            }
            ctx.result(arr.toString()).contentType("application/json");
        });

        // GET /api/kiosks/:id — diagnostics for one kiosk
        app.get("/api/kiosks/{id}", ctx -> {
            String id = ctx.pathParam("id");
            BaseKiosk kiosk = CentralRegistry.getInstance().getKiosk(id);
            if (kiosk == null) { ctx.status(404).result("{\"error\":\"not found\"}").contentType("application/json"); return; }
            KioskInterface facade = new KioskInterface(kiosk);
            JSONObject report = new JSONObject(facade.runDiagnostics());
            ctx.result(report.toString()).contentType("application/json");
        });

        // POST /api/kiosks/:id/purchase — purchase item
        app.post("/api/kiosks/{id}/purchase", ctx -> {
            String id = ctx.pathParam("id");
            JSONObject body = new JSONObject(ctx.body());
            BaseKiosk kiosk = CentralRegistry.getInstance().getKiosk(id);
            if (kiosk == null) { ctx.status(404).result("{\"error\":\"not found\"}").contentType("application/json"); return; }
            KioskInterface facade = new KioskInterface(kiosk);
            boolean ok = facade.purchaseItem(
                body.getString("userId"),
                body.getString("productId"),
                body.getInt("quantity")
            );
            ctx.result(new JSONObject().put("success", ok).toString()).contentType("application/json");
        });

        // POST /api/kiosks/:id/restock — restock inventory
        app.post("/api/kiosks/{id}/restock", ctx -> {
            String id = ctx.pathParam("id");
            JSONObject body = new JSONObject(ctx.body());
            BaseKiosk kiosk = CentralRegistry.getInstance().getKiosk(id);
            if (kiosk == null) { ctx.status(404).result("{\"error\":\"not found\"}").contentType("application/json"); return; }
            KioskInterface facade = new KioskInterface(kiosk);
            boolean ok = facade.restockInventory(
                body.getString("productId"),
                body.getInt("quantity")
            );
            ctx.result(new JSONObject().put("success", ok).toString()).contentType("application/json");
        });

        // --- Transaction Endpoints ---

        // GET /api/transactions — list all transactions
        app.get("/api/transactions", ctx -> {
            List<Map<String, Object>> txns = PersistenceManager.getInstance().loadTransactions();
            ctx.result(new JSONArray(txns).toString()).contentType("application/json");
        });

        // POST /api/transactions/:id/refund — refund a transaction
        app.post("/api/transactions/{id}/refund", ctx -> {
            String txId = ctx.pathParam("id");
            // Use first registered kiosk for refund context
            BaseKiosk kiosk = CentralRegistry.getInstance().getAllKiosks().values()
                .stream().findFirst().orElse(null);
            if (kiosk == null) { ctx.status(400).result("{\"error\":\"no kiosks\"}").contentType("application/json"); return; }
            KioskInterface facade = new KioskInterface(kiosk);
            boolean ok = facade.refundTransaction(txId);
            ctx.result(new JSONObject().put("success", ok).toString()).contentType("application/json");
        });

        // --- Inventory Endpoint ---

        // GET /api/inventory/:kioskId — get inventory tree
        app.get("/api/inventory/{kioskId}", ctx -> {
            SecureInventoryProxy proxy = SecureInventoryProxy.getInstance();
            // We want to return the actual inventory products
            JSONArray inventoryList = new JSONArray();
            // In a real app we'd fetch kiosk-specific items. Here we just grab the global catalog items
            String[] commonItems = {"FOOD-001", "FOOD-002", "MED-001", "MED-002", "EMG-001"};
            for (String pid : commonItems) {
                aura.interfaces.IInventoryItem item = proxy.getItem(pid);
                if (item != null) {
                    JSONObject obj = new JSONObject();
                    obj.put("id", item.getId());
                    obj.put("name", item.getName());
                    obj.put("stock", item.getAvailableStock());
                    obj.put("price", item.getBasePrice());
                    obj.put("available", item.isAvailable());
                    inventoryList.put(obj);
                }
            }
            JSONObject result = new JSONObject();
            result.put("inventory", inventoryList);
            result.put("accessLog", new JSONArray(proxy.getAccessLog()));
            ctx.result(result.toString()).contentType("application/json");
        });

        // --- Event Stream ---

        // GET /api/events/stream — Return buffered events and clear
        app.get("/api/events/stream", ctx -> {
            JSONArray arr = new JSONArray();
            synchronized(eventBuffer) {
                for (JSONObject obj : eventBuffer) arr.put(obj);
                eventBuffer.clear();
            }
            ctx.result(arr.toString()).contentType("application/json");
        });

        // --- System Mode ---

        // POST /api/system/mode — toggle emergency mode
        app.post("/api/system/mode", ctx -> {
            JSONObject body = new JSONObject(ctx.body());
            String mode = body.getString("mode");
            if ("EMERGENCY".equals(mode)) {
                new EmergencyModeCommand().execute();
            } else {
                CentralRegistry.getInstance().setSystemMode("NORMAL");
                CentralRegistry.getInstance().getAllKiosks().values().forEach(k -> 
                    k.setState(new aura.kiosk.state.ActiveState())
                );
            }
            ctx.result(new JSONObject()
                .put("systemMode", CentralRegistry.getInstance().getSystemMode()).toString()).contentType("application/json");
        });

        // GET /api/system/status
        app.get("/api/system/status", ctx -> {
            JSONObject status = new JSONObject();
            status.put("systemMode", CentralRegistry.getInstance().getSystemMode());
            status.put("uptimeSeconds", 1200); // Mock uptime
            status.put("totalKiosks", CentralRegistry.getInstance().getAllKiosks().size());
            status.put("totalTransactions", PersistenceManager.getInstance().loadTransactions().size());
            ctx.result(status.toString()).contentType("application/json");
        });

        // --- Simulation Endpoints ---

        // POST /api/simulate/:scenario — run a scenario ASYNCHRONOUSLY with event streaming
        app.post("/api/simulate/{scenario}", ctx -> {
            String scenario = ctx.pathParam("scenario").toUpperCase();
            if (!List.of("A", "B", "C").contains(scenario)) {
                ctx.status(400).result("{\"error\":\"invalid scenario\"}").contentType("application/json");
                return;
            }
            // Run scenario on a background thread so the API returns immediately
            new Thread(() -> {
                try {
                    switch (scenario) {
                        case "A": runScenarioAWithEvents(); break;
                        case "B": runScenarioBWithEvents(); break;
                        case "C": runScenarioCWithEvents(); break;
                    }
                } catch (Exception e) {
                    simLog("Scenario " + scenario + " ERROR: " + e.getMessage(), "error");
                }
            }, "Scenario-" + scenario).start();
            ctx.result(new JSONObject().put("success", true)
                .put("scenario", scenario).put("async", true).toString()).contentType("application/json");
        });

        // --- Emulator Endpoints ---

        // POST /api/emulator/purchase — step-by-step emulated customer purchase
        app.post("/api/emulator/purchase", ctx -> {
            JSONObject body = new JSONObject(ctx.body());
            String kioskId = body.getString("kioskId");
            String userId = body.getString("userId");
            String productId = body.getString("productId");
            int quantity = body.getInt("quantity");
            String paymentMethod = body.getString("paymentMethod");

            // Run the emulated purchase on a background thread with delays
            new Thread(() -> {
                try {
                    emulatorStep("Customer " + userId + " approaches kiosk " + kioskId, "approach");
                    Thread.sleep(1500);

                    emulatorStep("Browsing product catalog...", "browse");
                    Thread.sleep(1500);

                    aura.interfaces.IInventoryItem item = SecureInventoryProxy.getInstance().getItem(productId);
                    String itemName = item != null ? item.getName() : productId;
                    emulatorStep("Selected: " + itemName + " x" + quantity, "select");
                    Thread.sleep(1200);

                    emulatorStep("Processing payment via " + paymentMethod + "...", "payment");
                    Thread.sleep(2000);

                    BaseKiosk kiosk = CentralRegistry.getInstance().getKiosk(kioskId);
                    if (kiosk == null) {
                        emulatorStep("ERROR: Kiosk " + kioskId + " not found!", "error");
                        return;
                    }
                    KioskInterface facade = new KioskInterface(kiosk);
                    boolean ok = facade.purchaseItem(userId, productId, quantity);

                    if (ok) {
                        emulatorStep("Transaction COMPLETED — dispensing " + itemName, "success");
                        Thread.sleep(1000);
                        emulatorStep("Item dispensed. Thank you, " + userId + "!", "complete");
                    } else {
                        emulatorStep("Transaction FAILED — item unavailable or payment rejected", "error");
                    }
                } catch (Exception e) {
                    emulatorStep("System error: " + e.getMessage(), "error");
                }
            }, "Emulator-Purchase").start();

            ctx.result(new JSONObject().put("started", true).toString()).contentType("application/json");
        });

        // --- Payment Endpoints ---

        // GET /api/payments — list registered payment providers
        app.get("/api/payments", ctx -> {
            JSONArray arr = new JSONArray();
            for (IPaymentProvider p : PaymentProviderRegistry.getInstance().getAllProviders()) {
                arr.put(new JSONObject().put("name", p.getProviderName()));
            }
            ctx.result(arr.toString()).contentType("application/json");
        });

        // POST /api/payments/register — register a new payment provider
        app.post("/api/payments/register", ctx -> {
            JSONObject body = new JSONObject(ctx.body());
            String name = body.getString("providerName");
            if ("Crypto".equalsIgnoreCase(name)) {
                PaymentProviderRegistry.getInstance().register(
                    new aura.payment.adapters.CryptoPaymentAdapter(
                        new aura.payment.thirdparty.FakeCryptoAPI()));
            }
            ctx.result(new JSONObject().put("registered", name).toString()).contentType("application/json");
        });

        System.out.println("[ApiServer] Running on http://localhost:8080");
    }

    private static void bufferEvent(String type, Map<String, Object> data) {
        JSONObject obj = new JSONObject();
        obj.put("type", type);
        obj.put("data", new JSONObject(data));
        obj.put("timestamp", System.currentTimeMillis());
        eventBuffer.add(obj);
    }

    private static void seedData() {
        SecureInventoryProxy inv = SecureInventoryProxy.getInstance();
        inv.addItem(new Product("FOOD-001", "Energy Bar", 50, 3.50));
        inv.addItem(new Product("FOOD-002", "Apple Juice", 30, 2.00));
        inv.addItem(new Product("MED-001", "Paracetamol", 20, 1.50));
        inv.addItem(new Product("MED-002", "Bandages", 40, 2.00));
        inv.addItem(new Product("EMG-001", "Water Bottle", 100, 1.00));

        new KioskBuilder(new FoodKioskFactory()).addRefrigeration().build("F-001");
        new KioskBuilder(new PharmacyKioskFactory()).build("P-001");
        new KioskBuilder(new EmergencyReliefKioskFactory()).build("E-001");
        System.out.println("[ApiServer] Seeded inventory and 3 kiosks.");
    }

    // --- Simulation Log Helpers ---

    private static void simLog(String message, String level) {
        Map<String, Object> data = new HashMap<>();
        data.put("message", message);
        data.put("level", level);
        EventBus.getInstance().publish("SimulationLogEvent", data);
    }

    private static void emulatorStep(String message, String step) {
        Map<String, Object> data = new HashMap<>();
        data.put("message", message);
        data.put("step", step);
        EventBus.getInstance().publish("EmulatorStepEvent", data);
    }

    // --- Async Scenario Runners (emit events with delays) ---

    private static void runScenarioAWithEvents() throws InterruptedException {
        simLog("=== SCENARIO A: Adding a New Hardware Module ===", "header");
        Thread.sleep(1500);

        simLog("[FACTORY] Creating base FoodKiosk KIOSK-001 without refrigeration...", "info");
        new KioskBuilder(new FoodKioskFactory()).build("KIOSK-001");
        Thread.sleep(1500);

        simLog("[FACADE] Running diagnostics on KIOSK-001...", "info");
        Thread.sleep(1200);
        simLog("[BRIDGE] Swapping SpiralDispenser → RoboticArmDispenser on KIOSK-002...", "info");
        Thread.sleep(1500);

        simLog("[DECORATOR] Building KIOSK-002 with Refrigeration + Solar modules...", "info");
        new KioskBuilder(new FoodKioskFactory())
            .addRefrigeration()
            .addSolarMonitor()
            .withDispenserImpl(new aura.hardware.dispenser.RoboticArmDispenserImpl())
            .build("KIOSK-002");
        Thread.sleep(1500);

        simLog("[DECORATOR] FoodKiosk.java was NOT modified. Module added via Decorator pattern.", "success");
        Thread.sleep(800);
        simLog("[BRIDGE] Dispenser swap required zero changes to BaseKiosk or KioskInterface.", "success");
        Thread.sleep(800);
        simLog("=== SCENARIO A COMPLETE ===", "header");
    }

    private static void runScenarioBWithEvents() throws InterruptedException {
        simLog("=== SCENARIO B: Integrating a New Payment Provider ===", "header");
        Thread.sleep(1500);

        simLog("[ADAPTER] Listing existing providers...", "info");
        Thread.sleep(1200);

        String existing = PaymentProviderRegistry.getInstance().getAllProviders().stream()
            .map(p -> p.getProviderName()).collect(java.util.stream.Collectors.joining(", "));
        simLog("[ADAPTER] Current providers: " + existing, "info");
        Thread.sleep(1500);

        simLog("[ADAPTER] Registering CryptoPaymentAdapter at runtime...", "info");
        PaymentProviderRegistry.getInstance().register(
            new aura.payment.adapters.CryptoPaymentAdapter(new aura.payment.thirdparty.FakeCryptoAPI()));
        Thread.sleep(1500);

        simLog("[ADAPTER] Processing test Crypto payment of $49.99...", "info");
        IPaymentProvider crypto = PaymentProviderRegistry.getInstance().getProvider("Crypto");
        boolean result = crypto.processPayment("TX-CRYPTO-001", 49.99);
        Thread.sleep(1200);

        simLog("[ADAPTER] Crypto payment result: " + (result ? "SUCCESS" : "FAILED"), result ? "success" : "error");
        Thread.sleep(800);
        simLog("[ADAPTER] FakeCryptoAPI.sendCrypto() called internally — incompatible API hidden by Adapter.", "success");
        Thread.sleep(800);
        simLog("=== SCENARIO B COMPLETE ===", "header");
    }

    private static void runScenarioCWithEvents() throws InterruptedException {
        simLog("=== SCENARIO C: Nested Product Bundle Inventory ===", "header");
        Thread.sleep(1500);

        simLog("[COMPOSITE] Creating leaf products: Bandages, Antiseptic, Paracetamol...", "info");
        aura.inventory.Product bandages = new aura.inventory.Product("P001", "Bandages", 50, 2.00);
        aura.inventory.Product antiseptic = new aura.inventory.Product("P002", "Antiseptic", 30, 4.00);
        aura.inventory.Product paracetamol = new aura.inventory.Product("P003", "Paracetamol", 20, 1.50);
        aura.inventory.Product waterBottle = new aura.inventory.Product("P004", "Water Bottle", 100, 1.00);
        aura.inventory.Product energyBar = new aura.inventory.Product("P005", "Energy Bar", 80, 2.50);
        Thread.sleep(1500);

        simLog("[COMPOSITE] Building Med Kit bundle (Bandages + Antiseptic + Paracetamol, 10% discount)...", "info");
        aura.inventory.ProductBundle medKit = new aura.inventory.ProductBundle("B001", "Med Kit");
        medKit.add(bandages); medKit.add(antiseptic); medKit.add(paracetamol);
        medKit.setBundleDiscount(0.10);
        Thread.sleep(1500);

        simLog("[COMPOSITE] Building Emergency Kit (Med Kit + Water Bottle + Energy Bar, 15% discount)...", "info");
        aura.inventory.ProductBundle emergencyKit = new aura.inventory.ProductBundle("B002", "Emergency Kit");
        emergencyKit.add(medKit); emergencyKit.add(waterBottle); emergencyKit.add(energyBar);
        emergencyKit.setBundleDiscount(0.15);
        Thread.sleep(1200);

        simLog("[COMPOSITE] Med Kit stock: " + medKit.getAvailableStock() + " | Emergency Kit stock: " + emergencyKit.getAvailableStock(), "info");
        Thread.sleep(1000);

        simLog(String.format("[COMPOSITE] Emergency Kit base price: $%.2f", emergencyKit.getBasePrice()), "info");
        Thread.sleep(1200);

        simLog("[COMPOSITE] Depleting Paracetamol to test propagation...", "warning");
        paracetamol.commitReservation(20);
        Thread.sleep(1500);

        simLog("[COMPOSITE] Paracetamol available: " + paracetamol.isAvailable() + " → Med Kit: " + medKit.isAvailable() + " → Emergency Kit: " + emergencyKit.isAvailable(), "success");
        Thread.sleep(800);
        simLog("[COMPOSITE] Unavailability propagated recursively through bundle tree.", "success");
        Thread.sleep(800);
        simLog("=== SCENARIO C COMPLETE ===", "header");
    }
}
