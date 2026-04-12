package aura.hardware.modules;

import aura.kiosk.BaseKiosk;

// PATTERN: Decorator (ConcreteDecorator, Structural) — adds solar power monitoring
public class SolarMonitorDecorator extends KioskDecorator {
    private double solarOutput = 75.0; // percentage

    public SolarMonitorDecorator(BaseKiosk kiosk) {
        super(kiosk);
        System.out.println("[SolarMonitorDecorator] Solar module attached to " + kiosk.getKioskId());
    }

    public double getSolarOutput() { return solarOutput; }

    @Override
    protected boolean checkHardwareHealth() {
        System.out.println("[SolarMonitorDecorator] Solar output: " + solarOutput + "%");
        return wrappedKiosk.checkOperationalStatus();
    }
}
