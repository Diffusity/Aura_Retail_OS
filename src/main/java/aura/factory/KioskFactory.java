package aura.factory;

import aura.interfaces.*;
import aura.kiosk.BaseKiosk;

// PATTERN: Abstract Factory (Creational) — creates a compatible family of kiosk components
public abstract class KioskFactory {
    // Four abstract factory methods — each subclass returns its own compatible component
    public abstract IDispenser createDispenser();
    public abstract IVerificationModule createVerificationModule();
    public abstract IPricingStrategy createPricingStrategy();
    public abstract IInventoryPolicy createInventoryPolicy();

    // PATTERN: Factory Method (Creational) — subclasses decide the concrete kiosk type
    public BaseKiosk createKiosk(String kioskId) {
        BaseKiosk kiosk = instantiateKiosk(kioskId);
        kiosk.setDispenser(createDispenser());
        kiosk.setVerificationModule(createVerificationModule());
        kiosk.setPricingStrategy(createPricingStrategy());
        kiosk.setInventoryPolicy(createInventoryPolicy());
        return kiosk;
    }

    protected abstract BaseKiosk instantiateKiosk(String kioskId);
}
