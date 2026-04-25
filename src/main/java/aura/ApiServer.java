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

public class ApiServer {
    public static void main(String[] args) {
        // Bootstrap system
        SystemBootstrap.initialize();
        seedData();

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
                JSONObject obj = new JSONObject();
                obj.put("kioskId", e.getKey());
                obj.put("type", e.getValue().getClass().getSimpleName());
                obj.put("state", e.getValue().getState().getStateName());
                obj.put("dispenserOperational", e.getValue().getDispenser() != null
                        && e.getValue().getDispenser().isOperational());
                arr.put(obj);
            }
            ctx.json(arr.toString());
        });

        // GET /api/kiosks/:id — diagnostics for one kiosk
        app.get("/api/kiosks/{id}", ctx -> {
            String id = ctx.pathParam("id");
            BaseKiosk kiosk = CentralRegistry.getInstance().getKiosk(id);
            if (kiosk == null) { ctx.status(404).json("{\"error\":\"not found\"}"); return; }
            KioskInterface facade = new KioskInterface(kiosk);
            ctx.json(new JSONObject(facade.runDiagnostics()).toString());
        });

        // POST /api/kiosks/:id/purchase — purchase item
        app.post("/api/kiosks/{id}/purchase", ctx -> {
            String id = ctx.pathParam("id");
            JSONObject body = new JSONObject(ctx.body());
            BaseKiosk kiosk = CentralRegistry.getInstance().getKiosk(id);
            if (kiosk == null) { ctx.status(404).json("{\"error\":\"not found\"}"); return; }
            KioskInterface facade = new KioskInterface(kiosk);
            boolean ok = facade.purchaseItem(
                body.getString("userId"),
                body.getString("productId"),
                body.getInt("quantity")
            );
            ctx.json(new JSONObject().put("success", ok).toString());
        });

        // POST /api/kiosks/:id/restock — restock inventory
        app.post("/api/kiosks/{id}/restock", ctx -> {
            String id = ctx.pathParam("id");
            JSONObject body = new JSONObject(ctx.body());
            BaseKiosk kiosk = CentralRegistry.getInstance().getKiosk(id);
            if (kiosk == null) { ctx.status(404).json("{\"error\":\"not found\"}"); return; }
            KioskInterface facade = new KioskInterface(kiosk);
            boolean ok = facade.restockInventory(
                body.getString("productId"),
                body.getInt("quantity")
            );
            ctx.json(new JSONObject().put("success", ok).toString());
        });

        // --- Transaction Endpoints ---

        // GET /api/transactions — list all transactions
        app.get("/api/transactions", ctx -> {
            List<Map<String, Object>> txns = PersistenceManager.getInstance().loadTransactions();
            ctx.json(new JSONArray(txns).toString());
        });

        // POST /api/transactions/:id/refund — refund a transaction
        app.post("/api/transactions/{id}/refund", ctx -> {
            String txId = ctx.pathParam("id");
            // Use first registered kiosk for refund context
            BaseKiosk kiosk = CentralRegistry.getInstance().getAllKiosks().values()
                .stream().findFirst().orElse(null);
            if (kiosk == null) { ctx.status(400).json("{\"error\":\"no kiosks\"}"); return; }
            KioskInterface facade = new KioskInterface(kiosk);
            boolean ok = facade.refundTransaction(txId);
            ctx.json(new JSONObject().put("success", ok).toString());
        });

        // --- Inventory Endpoint ---

        // GET /api/inventory/:kioskId — get inventory tree
        app.get("/api/inventory/{kioskId}", ctx -> {
            // Return access log and item count for now
            SecureInventoryProxy proxy = SecureInventoryProxy.getInstance();
            JSONObject result = new JSONObject();
            result.put("accessLog", new JSONArray(proxy.getAccessLog()));
            ctx.json(result.toString());
        });

        // --- Event Stream ---

        // GET /api/events/stream — SSE stream from EventBus
        app.get("/api/events/stream", ctx -> {
            // For demo: return empty JSON array (polling mock)
            ctx.json("[]");
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
                // Need to manually reset kiosks to ActiveState since there's no normal mode command
                CentralRegistry.getInstance().getAllKiosks().values().forEach(k -> 
                    k.setState(new aura.kiosk.state.ActiveState())
                );
            }
            ctx.json(new JSONObject()
                .put("systemMode", CentralRegistry.getInstance().getSystemMode()).toString());
        });

        // --- Simulation Endpoints ---

        // POST /api/simulate/:scenario — run a scenario
        app.post("/api/simulate/{scenario}", ctx -> {
            String scenario = ctx.pathParam("scenario");
            try {
                switch (scenario.toUpperCase()) {
                    case "A": ScenarioA_NewHardwareModule.main(new String[]{}); break;
                    case "B": ScenarioB_NewPaymentProvider.main(new String[]{}); break;
                    case "C": ScenarioC_NestedBundles.main(new String[]{}); break;
                    default: ctx.status(400).json("{\"error\":\"invalid scenario\"}"); return;
                }
                ctx.json(new JSONObject().put("success", true)
                    .put("scenario", scenario).toString());
            } catch (Exception e) {
                ctx.status(500).json(new JSONObject()
                    .put("error", e.getMessage()).toString());
            }
        });

        // --- Payment Endpoints ---

        // GET /api/payments — list registered payment providers
        app.get("/api/payments", ctx -> {
            JSONArray arr = new JSONArray();
            for (IPaymentProvider p : PaymentProviderRegistry.getInstance().getAllProviders()) {
                arr.put(new JSONObject().put("name", p.getProviderName()));
            }
            ctx.json(arr.toString());
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
            ctx.json(new JSONObject().put("registered", name).toString());
        });

        System.out.println("[ApiServer] Running on http://localhost:8080");
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
}
