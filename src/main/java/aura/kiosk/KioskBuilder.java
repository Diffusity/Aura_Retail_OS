package aura.kiosk;

import aura.factory.KioskFactory;
import aura.hardware.modules.NetworkConnectivityDecorator;
import aura.hardware.modules.RefrigerationDecorator;
import aura.hardware.modules.SolarMonitorDecorator;
import aura.interfaces.IDispenserImpl;
import aura.registry.CentralRegistry;

// PATTERN: Builder (Creational) — constructs complex kiosk configurations step-by-step
public class KioskBuilder {
    private final KioskFactory factory;
    private boolean withRefrigeration = false;
    private boolean withSolar         = false;
    private boolean withNetwork       = false;
    private IDispenserImpl customDispenserImpl = null;

    public KioskBuilder(KioskFactory factory) { this.factory = factory; }

    public KioskBuilder addRefrigeration()                    { withRefrigeration = true; return this; }
    public KioskBuilder addSolarMonitor()                     { withSolar = true; return this; }
    public KioskBuilder addNetworkModule()                    { withNetwork = true; return this; }
    public KioskBuilder withDispenserImpl(IDispenserImpl impl){ customDispenserImpl = impl; return this; }

    public KioskInterface build(String kioskId) {
        BaseKiosk kiosk = factory.createKiosk(kioskId);

        // PATTERN: Bridge — swap dispenser implementation at build time without changing BaseKiosk
        if (customDispenserImpl != null) kiosk.getDispenser().setImpl(customDispenserImpl);

        // PATTERN: Decorator — wrap kiosk with optional hardware modules in desired order
        if (withRefrigeration) kiosk = new RefrigerationDecorator(kiosk);
        if (withSolar)         kiosk = new SolarMonitorDecorator(kiosk);
        if (withNetwork)       kiosk = new NetworkConnectivityDecorator(kiosk);

        CentralRegistry.getInstance().registerKiosk(kioskId, kiosk);
        return new KioskInterface(kiosk);
    }
}
