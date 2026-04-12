package aura.hardware.modules;

import aura.kiosk.BaseKiosk;

// PATTERN: Decorator (ConcreteDecorator, Structural) — adds network connectivity checks
public class NetworkConnectivityDecorator extends KioskDecorator {
    private boolean networkAvailable = true;

    public NetworkConnectivityDecorator(BaseKiosk kiosk) {
        super(kiosk);
        System.out.println("[NetworkConnectivityDecorator] Network module attached to " + kiosk.getKioskId());
    }

    public String getNetworkStatus()           { return networkAvailable ? "ONLINE" : "OFFLINE"; }
    public static boolean isNetworkAvailable() { return true; } // Simulated

    @Override
    protected boolean checkNetworkAvailability() {
        System.out.println("[NetworkDecorator] Network status: " + getNetworkStatus());
        return networkAvailable;
    }
}
