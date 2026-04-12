package aura.kiosk.state;

import aura.kiosk.BaseKiosk;

// PATTERN: State (Behavioral) — defines allowed operations per kiosk operating mode
public interface KioskState {
    boolean allowsPurchase();
    boolean allowsRestock();
    boolean allowsDiagnostics();
    String getStateName();
    void onEnter(BaseKiosk kiosk);
    void onExit(BaseKiosk kiosk);
}
