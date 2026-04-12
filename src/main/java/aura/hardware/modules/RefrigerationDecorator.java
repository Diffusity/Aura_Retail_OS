package aura.hardware.modules;

import aura.hardware.HardwareProxy;
import aura.interfaces.IHardwareModule;
import aura.kiosk.BaseKiosk;

// PATTERN: Decorator (ConcreteDecorator, Structural) — adds refrigeration without modifying BaseKiosk
public class RefrigerationDecorator extends KioskDecorator {
    private final IHardwareModule refrigerationUnit;
    private double currentTemperature = 4.0; // Celsius

    public RefrigerationDecorator(BaseKiosk kiosk) {
        super(kiosk);
        this.refrigerationUnit = new HardwareProxy(new RefrigerationUnit());
        this.refrigerationUnit.initialize();
        System.out.println("[RefrigerationDecorator] Module attached to " + kiosk.getKioskId());
    }

    public double getCurrentTemperature() { return currentTemperature; }
    public boolean isRefrigerated()       { return refrigerationUnit.isAvailable(); }

    // Override checkHardwareHealth to include refrigeration check
    @Override
    protected boolean checkHardwareHealth() {
        if (!refrigerationUnit.isAvailable()) {
            System.out.println("[RefrigerationDecorator] Refrigeration unit failed — blocking purchases.");
            return false;
        }
        return wrappedKiosk.checkOperationalStatus();
    }
}
