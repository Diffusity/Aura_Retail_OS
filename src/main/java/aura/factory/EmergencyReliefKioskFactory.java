package aura.factory;

import aura.hardware.dispenser.ConveyorDispenserImpl;
import aura.hardware.dispenser.Dispenser;
import aura.interfaces.*;
import aura.inventory.EmergencyInventoryPolicy;
import aura.kiosk.BaseKiosk;
import aura.kiosk.EmergencyReliefKiosk;
import aura.pricing.EmergencyPricingStrategy;
import aura.verification.EmergencyAccessVerifier;

// PATTERN: Abstract Factory (ConcreteFactory, Creational)
public class EmergencyReliefKioskFactory extends KioskFactory {
    @Override public IDispenser createDispenser()                  { return new Dispenser(new ConveyorDispenserImpl()); }
    @Override public IVerificationModule createVerificationModule() { return new EmergencyAccessVerifier(); }
    @Override public IPricingStrategy createPricingStrategy()      { return new EmergencyPricingStrategy(); }
    @Override public IInventoryPolicy createInventoryPolicy()      { return new EmergencyInventoryPolicy(); }
    @Override protected BaseKiosk instantiateKiosk(String id)      { return new EmergencyReliefKiosk(id); }
}
