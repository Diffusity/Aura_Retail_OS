package aura.kiosk.state;

import aura.kiosk.BaseKiosk;

// PATTERN: State (ConcreteState, Behavioral) — all operations permitted
public class ActiveState implements KioskState {
    @Override public boolean allowsPurchase()    { return true; }
    @Override public boolean allowsRestock()     { return true; }
    @Override public boolean allowsDiagnostics() { return true; }
    @Override public String getStateName()       { return "ACTIVE"; }
    @Override public void onEnter(BaseKiosk k)   { System.out.println("[State] " + k.getKioskId() + " → ACTIVE"); }
    @Override public void onExit(BaseKiosk k)    {}
}
