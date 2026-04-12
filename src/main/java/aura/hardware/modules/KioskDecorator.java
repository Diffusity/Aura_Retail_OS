package aura.hardware.modules;

import aura.interfaces.IHardwareModule;
import aura.kiosk.BaseKiosk;
import aura.interfaces.IInventoryItem;

// PATTERN: Decorator (Abstract, Structural) — wraps a BaseKiosk; forwards all calls and adds capability
public abstract class KioskDecorator extends BaseKiosk {
    protected final BaseKiosk wrappedKiosk;

    public KioskDecorator(BaseKiosk kiosk) {
        super(kiosk.getKioskId());
        this.wrappedKiosk = kiosk;
        // Inherit all components from the wrapped kiosk
        this.setDispenser(kiosk.getDispenser());
        this.setVerificationModule(kiosk.getVerificationModule());
        this.setPricingStrategy(kiosk.getPricingStrategy());
        this.setInventoryPolicy(kiosk.getInventoryPolicy());
    }

    @Override protected boolean checkHardwareHealth()      { return wrappedKiosk.checkOperationalStatus(); }
    @Override protected boolean checkNetworkAvailability() { return true; }
    @Override public IInventoryItem getInventoryItem(String id)  { return wrappedKiosk.getInventoryItem(id); }
    @Override public boolean checkInventory(String id, int qty)  { return wrappedKiosk.checkInventory(id, qty); }
}
