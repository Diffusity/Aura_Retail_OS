package aura.factory;

import aura.hardware.dispenser.ConveyorDispenserImpl;
import aura.hardware.dispenser.Dispenser;
import aura.interfaces.*;
import aura.inventory.StandardInventoryPolicy;
import aura.kiosk.BaseKiosk;
import aura.kiosk.FoodKiosk;
import aura.pricing.DiscountedPricingStrategy;
import aura.verification.AgeVerifier;

// PATTERN: Abstract Factory (ConcreteFactory, Creational)
public class FoodKioskFactory extends KioskFactory {
    @Override public IDispenser createDispenser()                  { return new Dispenser(new ConveyorDispenserImpl()); }
    @Override public IVerificationModule createVerificationModule() { return new AgeVerifier(); }
    @Override public IPricingStrategy createPricingStrategy()      { return new DiscountedPricingStrategy(0.10); }
    @Override public IInventoryPolicy createInventoryPolicy()      { return new StandardInventoryPolicy(); }
    @Override protected BaseKiosk instantiateKiosk(String id)      { return new FoodKiosk(id); }
}
