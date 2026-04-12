package aura.kiosk;

import aura.commands.CommandInvoker;
import aura.commands.PurchaseItemCommand;
import aura.interfaces.*;
import aura.kiosk.state.ActiveState;
import aura.kiosk.state.KioskState;
import aura.registry.CentralRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// PATTERN: Template Method (Behavioral) — defines purchase flow skeleton; subclasses override specific steps
// PATTERN: Prototype (Creational) — clone() enables rapid kiosk deployment from templates
// PATTERN: Encapsulation — all fields private; components accessed only through methods
public abstract class BaseKiosk implements Cloneable {
    private final String kioskId;
    private IDispenser dispenser;
    private IVerificationModule verificationModule;
    private IPricingStrategy pricingStrategy;
    private IInventoryPolicy inventoryPolicy;
    private KioskState currentState;
    private final List<IHardwareModule> attachedModules = new ArrayList<>();

    public BaseKiosk(String kioskId) {
        this.kioskId = kioskId;
        this.currentState = new ActiveState();
        this.currentState.onEnter(this);
    }

    // PATTERN: Template Method — purchase flow skeleton (final = cannot be overridden)
    public final boolean processPurchase(String userId, String productId, int quantity) {
        if (!checkOperationalStatus()) return false;             // step 1: hardware + state + network
        if (!verifyUser(userId, productId)) return false;        // step 2: kiosk-type specific
        if (!checkInventory(productId, quantity)) return false;  // step 3: derived stock check
        double price = computePrice(productId);                  // step 4: strategy-computed
        return executeTransaction(userId, productId, quantity, price); // step 5: command
    }

    // PATTERN: Template Method (hook) — each kiosk type overrides verification logic
    protected boolean verifyUser(String userId, String productId) {
        return verificationModule != null && verificationModule.verify(userId, productId);
    }

    // PATTERN: Template Method — operational check is also a skeleton
    public final boolean checkOperationalStatus() {
        return checkHardwareHealth() && checkSystemMode() && checkNetworkAvailability();
    }

    // Abstract hooks — each kiosk type defines its own hardware and network requirements
    protected abstract boolean checkHardwareHealth();
    protected abstract boolean checkNetworkAvailability();

    private boolean checkSystemMode() { return currentState.allowsPurchase(); }

    protected double computePrice(String productId) {
        IInventoryItem item = getInventoryItem(productId);
        if (item == null) return 0.0;
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("kioskId", kioskId);
        ctx.put("systemMode", CentralRegistry.getInstance().getSystemMode());
        return pricingStrategy != null ? pricingStrategy.computePrice(item, ctx) : 0.0;
    }

    private boolean executeTransaction(String userId, String productId, int qty, double price) {
        CommandInvoker invoker = new CommandInvoker();
        ICommand purchase = new PurchaseItemCommand(this, userId, productId, qty, price);
        return invoker.execute(purchase);
    }

    // PATTERN: Prototype — clone a configured kiosk for rapid deployment at a new location
    @Override
    public BaseKiosk clone() {
        try { return (BaseKiosk) super.clone(); }
        catch (CloneNotSupportedException e) { throw new RuntimeException(e); }
    }

    public void setState(KioskState newState) {
        currentState.onExit(this);
        this.currentState = newState;
        newState.onEnter(this);
    }
    public KioskState getState() { return currentState; }

    // Component setters (used by factory and builder only)
    public void setDispenser(IDispenser d)                   { this.dispenser = d; }
    public void setVerificationModule(IVerificationModule v) { this.verificationModule = v; }
    public void setPricingStrategy(IPricingStrategy p)       { this.pricingStrategy = p; }
    public void setInventoryPolicy(IInventoryPolicy ip)      { this.inventoryPolicy = ip; }

    // Getters
    public String getKioskId()                         { return kioskId; }
    public IDispenser getDispenser()                   { return dispenser; }
    public IInventoryPolicy getInventoryPolicy()       { return inventoryPolicy; }
    public IVerificationModule getVerificationModule() { return verificationModule; }
    public IPricingStrategy getPricingStrategy()       { return pricingStrategy; }

    public abstract IInventoryItem getInventoryItem(String productId);
    public abstract boolean checkInventory(String productId, int quantity);
}
