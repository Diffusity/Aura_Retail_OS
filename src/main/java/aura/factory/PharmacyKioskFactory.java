package aura.factory;

import aura.hardware.dispenser.Dispenser;
import aura.hardware.dispenser.RoboticArmDispenserImpl;
import aura.interfaces.*;
import aura.inventory.PharmacyInventoryPolicy;
import aura.kiosk.BaseKiosk;
import aura.kiosk.PharmacyKiosk;
import aura.pricing.StandardPricingStrategy;
import aura.verification.PrescriptionVerifier;

// PATTERN: Abstract Factory (ConcreteFactory, Creational)
public class PharmacyKioskFactory extends KioskFactory {
    @Override public IDispenser createDispenser()                  { return new Dispenser(new RoboticArmDispenserImpl()); }
    @Override public IVerificationModule createVerificationModule() { return new PrescriptionVerifier(); }
    @Override public IPricingStrategy createPricingStrategy()      { return new StandardPricingStrategy(); }
    @Override public IInventoryPolicy createInventoryPolicy()      { return new PharmacyInventoryPolicy(); }
    @Override protected BaseKiosk instantiateKiosk(String id)      { return new PharmacyKiosk(id); }
}
