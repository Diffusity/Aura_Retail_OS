package aura.kiosk.state;

import aura.kiosk.BaseKiosk;

// PATTERN: State (ConcreteState, Behavioral) — purchases only, no restocking
public class PowerSavingState implements KioskState {
    @Override public boolean allowsPurchase()    { return true; }
    @Override public boolean allowsRestock()     { return false; }
    @Override public boolean allowsDiagnostics() { return true; }
    @Override public String getStateName()       { return "POWER_SAVING"; }
    @Override public void onEnter(BaseKiosk k)   { System.out.println("[State] " + k.getKioskId() + " → POWER_SAVING"); }
    @Override public void onExit(BaseKiosk k)    {}
}
