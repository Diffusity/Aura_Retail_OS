package aura.kiosk.state;

import aura.kiosk.BaseKiosk;
import aura.pricing.EmergencyPricingStrategy;
import aura.pricing.StandardPricingStrategy;

// PATTERN: State (ConcreteState, Behavioral) — restricted purchases (qty ≤ 2), no restocking
public class EmergencyLockdownState implements KioskState {
    @Override public boolean allowsPurchase()    { return true; }  // limited, enforced by policy
    @Override public boolean allowsRestock()     { return false; }
    @Override public boolean allowsDiagnostics() { return true; }
    @Override public String getStateName()       { return "EMERGENCY_LOCKDOWN"; }

    @Override
    public void onEnter(BaseKiosk k) {
        System.out.println("[State] EMERGENCY LOCKDOWN on " + k.getKioskId());
        k.setPricingStrategy(new EmergencyPricingStrategy()); // immediate pricing switch
    }

    @Override
    public void onExit(BaseKiosk k) { k.setPricingStrategy(new StandardPricingStrategy()); }
}
