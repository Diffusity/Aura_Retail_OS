package aura.kiosk.state;

import aura.kiosk.BaseKiosk;

// PATTERN: State (ConcreteState, Behavioral) — only diagnostics; all purchases blocked
public class MaintenanceState implements KioskState {
    @Override public boolean allowsPurchase()    { return false; }
    @Override public boolean allowsRestock()     { return false; }
    @Override public boolean allowsDiagnostics() { return true; }
    @Override public String getStateName()       { return "MAINTENANCE"; }
    @Override public void onEnter(BaseKiosk k)   { System.out.println("[State] " + k.getKioskId() + " → MAINTENANCE"); }
    @Override public void onExit(BaseKiosk k)    { System.out.println("[State] " + k.getKioskId() + " exiting MAINTENANCE"); }
}
