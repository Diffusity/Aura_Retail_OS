# Aura Retail OS — Implementation Guide (Path B: Modular Hardware Platform)
> **Course:** IT620 — Object Oriented Programming  
> **Project:** Aura Retail OS  
> **Path:** B — Modular Hardware Platform  
> **Language:** Java (recommended) or Python  
> **Target:** This document is a self-contained specification for any AI agent or developer to implement the full project from scratch.

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Package Structure](#2-package-structure)
3. [Design Patterns Master List](#3-design-patterns-master-list)
4. [Phase-by-Phase Implementation](#4-phase-by-phase-implementation)
   - [Phase 1 — Foundation](#phase-1--foundation--architecture--core-abstractions)
   - [Phase 2 — Kiosk Creation](#phase-2--kiosk-creation--abstract-factory--facade)
   - [Phase 3 — Hardware Abstraction](#phase-3--hardware-abstraction--bridge--decorator)
   - [Phase 4 — Inventory System](#phase-4--inventory-system--composite--proxy)
   - [Phase 5 — Payment System](#phase-5--payment-system--adapter--strategy)
   - [Phase 6 — Transaction System](#phase-6--transaction-system--command--observer)
   - [Phase 7 — System Constraints](#phase-7--system-constraints--integration)
   - [Phase 8 — Simulation Scenarios](#phase-8--simulation-scenarios--documentation)
5. [System Constraints Enforcement](#5-system-constraints-enforcement)
6. [Class Specifications](#6-class-specifications)
7. [Simulation Scenarios (Path B Required)](#7-simulation-scenarios-path-b-required)
8. [Deliverables Checklist](#8-deliverables-checklist)
9. [Grading Notes for AI Agents](#9-grading-notes-for-ai-agents)

---

## 1. Project Overview

### Context
The city of Zephyrus deploys autonomous retail kiosks (Aura Kiosks) across hospitals, metro stations, university campuses, and disaster zones. The original monolithic system failed due to hardware coupling, payment incompatibility, and no modularity. This project builds **Aura Retail OS** — a modular, OOP-driven replacement.

### Path B Focus
Path B treats the kiosk as a **long-term hardware platform**. The design priorities are:
- Hardware implementations can be swapped without changing high-level logic (Bridge)
- New hardware modules attach dynamically without modifying base classes (Decorator)
- Incompatible payment APIs are unified through a common interface (Adapter)
- Inventory supports individual products and deeply nested bundles (Composite)
- All inventory access passes through a security layer (Proxy)

### Core OOP Principles to Demonstrate
Every class and pattern must visibly demonstrate at least one of:
- **Encapsulation** — private fields, controlled access
- **Abstraction** — interfaces and abstract classes hide complexity
- **Inheritance** — meaningful method overriding in subclasses
- **Low coupling** — subsystems interact only through interfaces, never concrete classes

---

## 2. Package Structure

```
aura/
├── interfaces/              # All interfaces and abstract base classes
│   ├── IDispenser.java
│   ├── IDispenserImpl.java
│   ├── IPaymentProvider.java
│   ├── IPricingStrategy.java
│   ├── IInventoryItem.java
│   ├── IInventoryPolicy.java
│   ├── IVerificationModule.java
│   ├── IHardwareModule.java
│   ├── ICommand.java
│   ├── IFailureHandler.java
│   ├── IEventSubscriber.java
│   └── IEventPublisher.java
│
├── registry/                # Singleton registry
│   └── CentralRegistry.java
│
├── persistence/             # JSON/CSV persistence
│   └── PersistenceManager.java
│
├── kiosk/                   # Kiosk core system
│   ├── BaseKiosk.java       # Abstract base
│   ├── PharmacyKiosk.java
│   ├── FoodKiosk.java
│   ├── EmergencyReliefKiosk.java
│   ├── KioskInterface.java  # Facade
│   ├── KioskBuilder.java    # Builder
│   └── state/              # State pattern
│       ├── KioskState.java  # Interface
│       ├── ActiveState.java
│       ├── MaintenanceState.java
│       └── EmergencyLockdownState.java
│
├── factory/                 # Abstract Factory
│   ├── KioskFactory.java    # Abstract factory
│   ├── PharmacyKioskFactory.java
│   ├── FoodKioskFactory.java
│   └── EmergencyReliefKioskFactory.java
│
├── hardware/                # Hardware Abstraction Layer
│   ├── dispenser/
│   │   ├── Dispenser.java              # Abstraction (Bridge)
│   │   ├── DispenserImpl.java          # Implementor (Bridge)
│   │   ├── SpiralDispenserImpl.java
│   │   ├── RoboticArmDispenserImpl.java
│   │   └── ConveyorDispenserImpl.java
│   ├── modules/             # Optional hardware (Decorator)
│   │   ├── KioskDecorator.java         # Abstract decorator
│   │   ├── RefrigerationDecorator.java
│   │   ├── SolarMonitorDecorator.java
│   │   └── NetworkConnectivityDecorator.java
│   └── HardwareProxy.java   # Proxy for safe access
│
├── inventory/               # Inventory system
│   ├── InventoryManager.java
│   ├── SecureInventoryProxy.java       # Proxy
│   ├── Product.java                    # Composite leaf
│   ├── ProductBundle.java              # Composite node
│   └── ProductCatalogue.java           # Flyweight factory
│
├── payment/                 # Payment system
│   ├── PaymentProviderRegistry.java
│   ├── adapters/
│   │   ├── CreditCardAdapter.java      # Adapter
│   │   ├── DigitalWalletAdapter.java   # Adapter
│   │   └── UPIAdapter.java            # Adapter
│   └── thirdparty/          # Simulated incompatible APIs
│       ├── LegacyCreditCardAPI.java
│       ├── WalletSDK.java
│       └── UPIGateway.java
│
├── pricing/                 # Strategy pattern
│   ├── StandardPricingStrategy.java
│   ├── DiscountedPricingStrategy.java
│   └── EmergencyPricingStrategy.java
│
├── commands/                # Command pattern
│   ├── CommandInvoker.java
│   ├── PurchaseItemCommand.java
│   ├── RefundCommand.java
│   ├── RestockCommand.java
│   └── EmergencyModeCommand.java
│
├── transaction/             # Transaction management
│   ├── Transaction.java
│   ├── TransactionMemento.java         # Memento
│   ├── TransactionCaretaker.java       # Memento caretaker
│   ├── TransactionLog.java
│   └── TransactionIterator.java        # Iterator
│
├── events/                  # Observer pattern
│   ├── EventBus.java
│   ├── LowStockEvent.java
│   ├── HardwareFailureEvent.java
│   ├── TransactionFailedEvent.java
│   ├── TransactionCompletedEvent.java
│   └── EmergencyModeActivatedEvent.java
│
├── monitoring/              # City monitoring subscribers
│   ├── CityMonitoringCenter.java       # Observer
│   ├── SupplyChainSystem.java          # Observer
│   └── MaintenanceService.java         # Observer
│
├── failure/                 # Chain of Responsibility
│   ├── RetryHandler.java
│   ├── RecalibrationHandler.java
│   └── TechnicianAlertHandler.java
│
├── verification/            # Verification modules
│   ├── PrescriptionVerifier.java
│   ├── AgeVerifier.java
│   └── EmergencyAccessVerifier.java
│
└── simulation/              # Runnable demo scenarios
    ├── ScenarioA_NewHardwareModule.java
    ├── ScenarioB_NewPaymentProvider.java
    └── ScenarioC_NestedBundles.java
```

---

## 3. Design Patterns Master List

Every pattern below must be implemented. Add the comment `// PATTERN: [Name] — [one-line reason]` directly above the class or method where the pattern is applied.

### Creational Patterns

| Pattern | Where Applied | Purpose |
|---|---|---|
| **Singleton** | `CentralRegistry`, `PersistenceManager`, `EventBus` | One global instance for shared system state |
| **Abstract Factory** | `KioskFactory` hierarchy | Creates compatible families of kiosk components |
| **Factory Method** | Inside each concrete factory | Creates specific component types per kiosk family |
| **Builder** | `KioskBuilder` | Assembles kiosks with optional hardware modules step-by-step |
| **Prototype** | `BaseKiosk.clone()` | Clones kiosk configurations for rapid deployment |

### Structural Patterns

| Pattern | Where Applied | Purpose |
|---|---|---|
| **Adapter** | `CreditCardAdapter`, `DigitalWalletAdapter`, `UPIAdapter` | Unifies incompatible payment provider APIs |
| **Bridge** | `Dispenser` ↔ `DispenserImpl` hierarchy | Decouples dispenser abstraction from implementation |
| **Composite** | `Product` (leaf) + `ProductBundle` (composite) | Supports nested inventory bundles with uniform interface |
| **Decorator** | `RefrigerationDecorator`, `SolarMonitorDecorator`, etc. | Attaches optional hardware modules without modifying base class |
| **Facade** | `KioskInterface` | Exposes 4 simplified operations; hides all internal complexity |
| **Proxy** | `SecureInventoryProxy`, `HardwareProxy` | Adds security/health checks before delegating to real objects |
| **Flyweight** | `ProductCatalogue` | Shares intrinsic product metadata across kiosk instances |

### Behavioral Patterns

| Pattern | Where Applied | Purpose |
|---|---|---|
| **Command** | `PurchaseItemCommand`, `RefundCommand`, `RestockCommand` | Models all operations as executable, loggable, undoable objects |
| **Observer** | `EventBus` → `CityMonitoringCenter`, `SupplyChainSystem`, etc. | Decoupled event-driven communication between subsystems |
| **Strategy** | `IPricingStrategy` implementations | Swappable pricing logic at runtime |
| **State** | `ActiveState`, `MaintenanceState`, `EmergencyLockdownState` | Kiosk behaviour changes based on current operating state |
| **Chain of Responsibility** | `RetryHandler → RecalibrationHandler → TechnicianAlertHandler` | Failure escalation without tight coupling between handlers |
| **Template Method** | `BaseKiosk.checkOperationalStatus()` | Defines the sequence; subclasses override individual checks |
| **Memento** | `TransactionMemento` + `TransactionCaretaker` | Saves and restores transaction state for rollback |
| **Iterator** | `TransactionIterator` | Traverses transaction log without exposing storage structure |

---

## 4. Phase-by-Phase Implementation

---

### Phase 1 — Foundation: Architecture & Core Abstractions

**Goal:** Establish all interfaces, the Singleton registry, and the persistence layer before writing any logic.

#### Step 1.1 — Define all core interfaces

Create the following in the `interfaces/` package. These are contracts; no implementation logic goes here.

```java
// interfaces/IDispenser.java
// PATTERN: Abstraction — hides hardware implementation from kiosk logic
public interface IDispenser {
    boolean dispense(String productId, int quantity);
    boolean isOperational();
    void setImpl(IDispenserImpl impl); // supports Bridge pattern
}

// interfaces/IDispenserImpl.java
// PATTERN: Bridge (Implementor) — separates what from how
public interface IDispenserImpl {
    boolean performDispense(String productId, int quantity);
    void calibrate();
    String getHardwareType();
}

// interfaces/IPaymentProvider.java
// PATTERN: Abstraction — uniform interface for all payment systems
public interface IPaymentProvider {
    boolean processPayment(String transactionId, double amount);
    boolean refund(String transactionId);
    String getProviderName();
}

// interfaces/IPricingStrategy.java
// PATTERN: Strategy — interchangeable pricing algorithms
public interface IPricingStrategy {
    double computePrice(IInventoryItem item, Map<String, Object> context);
}

// interfaces/IInventoryItem.java
// PATTERN: Composite (Component) — uniform interface for products and bundles
public interface IInventoryItem {
    String getId();
    String getName();
    int getAvailableStock();
    double getBasePrice();
    boolean isAvailable();
    void add(IInventoryItem item);       // only meaningful in bundles
    void remove(IInventoryItem item);    // only meaningful in bundles
    List<IInventoryItem> getChildren();  // only meaningful in bundles
}

// interfaces/IInventoryPolicy.java
public interface IInventoryPolicy {
    int getMaxPurchaseQuantity(String productId, boolean emergencyMode);
    boolean canRestock(String productId);
}

// interfaces/IVerificationModule.java
public interface IVerificationModule {
    boolean verify(String userId, String productId);
}

// interfaces/IHardwareModule.java
// PATTERN: Abstraction — optional hardware capability contract
public interface IHardwareModule {
    boolean isAvailable();
    String getModuleType();
    void initialize();
    void shutdown();
}

// interfaces/ICommand.java
// PATTERN: Command — encapsulates an operation with execute/undo
public interface ICommand {
    boolean execute();
    boolean undo();
    void log();
    String getCommandType();
}

// interfaces/IFailureHandler.java
// PATTERN: Chain of Responsibility — each handler decides to handle or pass forward
public interface IFailureHandler {
    void setNext(IFailureHandler next);
    boolean handle(String failureType, String context);
}

// interfaces/IEventSubscriber.java
// PATTERN: Observer — subscriber contract
public interface IEventSubscriber {
    void onEvent(String eventType, Map<String, Object> eventData);
    List<String> getSubscribedEvents();
}
```

#### Step 1.2 — Implement CentralRegistry as Singleton

```java
// registry/CentralRegistry.java
// PATTERN: Singleton — one global store for system-wide configuration and kiosk references
public class CentralRegistry {
    private static CentralRegistry instance;
    private Map<String, Object> config = new HashMap<>();
    private Map<String, BaseKiosk> registeredKiosks = new HashMap<>();
    private String systemMode = "NORMAL"; // NORMAL | EMERGENCY

    private CentralRegistry() {
        loadFromPersistence();
    }

    public static synchronized CentralRegistry getInstance() {
        if (instance == null) instance = new CentralRegistry();
        return instance;
    }

    public void registerKiosk(String id, BaseKiosk kiosk) { registeredKiosks.put(id, kiosk); }
    public BaseKiosk getKiosk(String id) { return registeredKiosks.get(id); }
    public void setSystemMode(String mode) { this.systemMode = mode; persistState(); }
    public String getSystemMode() { return systemMode; }
    public void setConfig(String key, Object value) { config.put(key, value); persistState(); }
    public Object getConfig(String key) { return config.get(key); }

    private void persistState() { PersistenceManager.getInstance().saveRegistry(this); }
    private void loadFromPersistence() { /* load from JSON on startup */ }
}
```

#### Step 1.3 — PersistenceManager as Singleton

```java
// persistence/PersistenceManager.java
// PATTERN: Singleton — single point of I/O for all subsystems
public class PersistenceManager {
    private static PersistenceManager instance;
    private static final String INVENTORY_FILE = "data/inventory.json";
    private static final String TRANSACTIONS_FILE = "data/transactions.json";
    private static final String CONFIG_FILE = "data/config.json";

    private PersistenceManager() { ensureDirectoryExists("data/"); }

    public static synchronized PersistenceManager getInstance() {
        if (instance == null) instance = new PersistenceManager();
        return instance;
    }

    public void saveInventory(List<IInventoryItem> items) { /* serialize to JSON */ }
    public List<Map<String, Object>> loadInventory() { /* deserialize from JSON */ return new ArrayList<>(); }
    public void saveTransaction(Transaction t) { /* append to transactions JSON */ }
    public List<Map<String, Object>> loadTransactions() { /* deserialize */ return new ArrayList<>(); }
    public void saveRegistry(CentralRegistry registry) { /* serialize config and kiosk IDs */ }
    public Map<String, Object> loadConfig() { /* deserialize */ return new HashMap<>(); }
}
```

**Key rule:** Every write to any file must go through `PersistenceManager`. No other class does file I/O.

---

### Phase 2 — Kiosk Creation: Abstract Factory & Facade

**Goal:** Implement the three kiosk types and the `KioskInterface` Facade.

#### Step 2.1 — Abstract Factory hierarchy

```java
// factory/KioskFactory.java
// PATTERN: Abstract Factory — creates a compatible family of kiosk components
public abstract class KioskFactory {
    public abstract IDispenser createDispenser();
    public abstract IVerificationModule createVerificationModule();
    public abstract IPricingStrategy createPricingStrategy();
    public abstract IInventoryPolicy createInventoryPolicy();

    // Factory Method within the Abstract Factory
    public BaseKiosk createKiosk(String kioskId) {
        // PATTERN: Factory Method — subclasses decide the concrete kiosk type
        BaseKiosk kiosk = instantiateKiosk(kioskId);
        kiosk.setDispenser(createDispenser());
        kiosk.setVerificationModule(createVerificationModule());
        kiosk.setPricingStrategy(createPricingStrategy());
        kiosk.setInventoryPolicy(createInventoryPolicy());
        return kiosk;
    }

    protected abstract BaseKiosk instantiateKiosk(String kioskId);
}

// factory/PharmacyKioskFactory.java
public class PharmacyKioskFactory extends KioskFactory {
    @Override public IDispenser createDispenser() { return new Dispenser(new RoboticArmDispenserImpl()); }
    @Override public IVerificationModule createVerificationModule() { return new PrescriptionVerifier(); }
    @Override public IPricingStrategy createPricingStrategy() { return new StandardPricingStrategy(); }
    @Override public IInventoryPolicy createInventoryPolicy() { return new PharmacyInventoryPolicy(); }
    @Override protected BaseKiosk instantiateKiosk(String id) { return new PharmacyKiosk(id); }
}

// factory/FoodKioskFactory.java
public class FoodKioskFactory extends KioskFactory {
    @Override public IDispenser createDispenser() { return new Dispenser(new SpiralDispenserImpl()); }
    @Override public IVerificationModule createVerificationModule() { return new AgeVerifier(); }
    @Override public IPricingStrategy createPricingStrategy() { return new DiscountedPricingStrategy(); }
    @Override public IInventoryPolicy createInventoryPolicy() { return new StandardInventoryPolicy(); }
    @Override protected BaseKiosk instantiateKiosk(String id) { return new FoodKiosk(id); }
}

// factory/EmergencyReliefKioskFactory.java
public class EmergencyReliefKioskFactory extends KioskFactory {
    @Override public IDispenser createDispenser() { return new Dispenser(new ConveyorDispenserImpl()); }
    @Override public IVerificationModule createVerificationModule() { return new EmergencyAccessVerifier(); }
    @Override public IPricingStrategy createPricingStrategy() { return new EmergencyPricingStrategy(); }
    @Override public IInventoryPolicy createInventoryPolicy() { return new EmergencyInventoryPolicy(); }
    @Override protected BaseKiosk instantiateKiosk(String id) { return new EmergencyReliefKiosk(id); }
}
```

#### Step 2.2 — Abstract BaseKiosk with Template Method

```java
// kiosk/BaseKiosk.java
// PATTERN: Template Method — defines the purchase flow skeleton; subclasses override specific steps
// PATTERN: Abstraction + Encapsulation — all fields private; behaviour exposed via methods
public abstract class BaseKiosk implements Cloneable {
    private final String kioskId;
    private IDispenser dispenser;
    private IVerificationModule verificationModule;
    private IPricingStrategy pricingStrategy;
    private IInventoryPolicy inventoryPolicy;
    private KioskState currentState;  // PATTERN: State
    private List<IHardwareModule> attachedModules = new ArrayList<>();

    public BaseKiosk(String kioskId) {
        this.kioskId = kioskId;
        this.currentState = new ActiveState(this);
    }

    // PATTERN: Template Method — purchase flow skeleton
    public final boolean processPurchase(String userId, String productId, int quantity) {
        if (!checkOperationalStatus()) return false;            // step 1
        if (!verifyUser(userId, productId)) return false;       // step 2 — overridden by subclass
        if (!checkInventory(productId, quantity)) return false; // step 3
        double price = computePrice(productId);                 // step 4
        return executeTransaction(userId, productId, quantity, price); // step 5
    }

    // PATTERN: Template Method (hook) — each kiosk type overrides verification logic
    protected boolean verifyUser(String userId, String productId) {
        return verificationModule.verify(userId, productId);
    }

    // PATTERN: Template Method — operational check sequence
    public final boolean checkOperationalStatus() {
        return checkHardwareHealth() && checkSystemMode() && checkNetworkAvailability();
    }

    protected abstract boolean checkHardwareHealth();
    protected abstract boolean checkNetworkAvailability();

    private boolean checkSystemMode() {
        return currentState.allowsPurchase();
    }

    protected double computePrice(String productId) {
        IInventoryItem item = getInventoryItem(productId);
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("kioskId", kioskId);
        ctx.put("systemMode", CentralRegistry.getInstance().getSystemMode());
        return pricingStrategy.computePrice(item, ctx);
    }

    private boolean executeTransaction(String userId, String productId, int qty, double price) {
        CommandInvoker invoker = new CommandInvoker();
        ICommand purchase = new PurchaseItemCommand(this, userId, productId, qty, price);
        return invoker.execute(purchase);
    }

    // PATTERN: Prototype — clone a configured kiosk for rapid deployment
    @Override
    public BaseKiosk clone() {
        try { return (BaseKiosk) super.clone(); }
        catch (CloneNotSupportedException e) { throw new RuntimeException(e); }
    }

    // State transition (called by State objects)
    public void setState(KioskState newState) { this.currentState = newState; }
    public KioskState getState() { return currentState; }

    // Getters/setters for components (package-private for factory use)
    public void setDispenser(IDispenser d) { this.dispenser = d; }
    public void setVerificationModule(IVerificationModule v) { this.verificationModule = v; }
    public void setPricingStrategy(IPricingStrategy p) { this.pricingStrategy = p; }
    public void setInventoryPolicy(IInventoryPolicy ip) { this.inventoryPolicy = ip; }
    public String getKioskId() { return kioskId; }
    public IDispenser getDispenser() { return dispenser; }
    public IInventoryPolicy getInventoryPolicy() { return inventoryPolicy; }

    protected abstract IInventoryItem getInventoryItem(String productId);
    protected abstract boolean checkInventory(String productId, int quantity);
}
```

#### Step 2.3 — Concrete Kiosk subclasses

```java
// kiosk/PharmacyKiosk.java
// PATTERN: Inheritance — overrides verification to require prescription check
public class PharmacyKiosk extends BaseKiosk {
    public PharmacyKiosk(String id) { super(id); }

    @Override
    protected boolean verifyUser(String userId, String productId) {
        // Pharmacy-specific: must have valid prescription on file
        boolean hasPrescription = super.verifyUser(userId, productId);
        if (!hasPrescription) System.out.println("[PharmacyKiosk] Purchase denied: no valid prescription.");
        return hasPrescription;
    }

    @Override
    protected boolean checkHardwareHealth() {
        // Pharmacy kiosks require the dispenser to be operational at all times
        return getDispenser().isOperational();
    }

    @Override
    protected boolean checkNetworkAvailability() {
        // Must be online to verify prescriptions in real time
        return NetworkConnectivityDecorator.isNetworkAvailable();
    }

    @Override
    protected IInventoryItem getInventoryItem(String productId) {
        return SecureInventoryProxy.getInstance().getItem(productId);
    }

    @Override
    protected boolean checkInventory(String productId, int qty) {
        IInventoryItem item = getInventoryItem(productId);
        return item != null && item.getAvailableStock() >= qty;
    }
}

// kiosk/FoodKiosk.java — similar structure, different overrides
// kiosk/EmergencyReliefKiosk.java — enforces emergency purchase limits in checkInventory()
```

#### Step 2.4 — KioskInterface Facade

```java
// kiosk/KioskInterface.java
// PATTERN: Facade — single simplified entry point; hides all subsystem complexity
// External systems ONLY interact with this class — never with BaseKiosk directly
public class KioskInterface {
    private final BaseKiosk kiosk;

    public KioskInterface(BaseKiosk kiosk) {
        this.kiosk = kiosk;
    }

    // All four required Facade operations:

    public boolean purchaseItem(String userId, String productId, int quantity) {
        return kiosk.processPurchase(userId, productId, quantity);
    }

    public boolean refundTransaction(String transactionId) {
        CommandInvoker invoker = new CommandInvoker();
        ICommand refund = new RefundCommand(transactionId, kiosk);
        return invoker.execute(refund);
    }

    public Map<String, Object> runDiagnostics() {
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("kioskId", kiosk.getKioskId());
        report.put("operationalStatus", kiosk.checkOperationalStatus());
        report.put("currentState", kiosk.getState().getStateName());
        report.put("dispenserOperational", kiosk.getDispenser().isOperational());
        report.put("recentTransactions", new TransactionIterator(
            PersistenceManager.getInstance().loadTransactions(), kiosk.getKioskId()
        ).getLastN(10));
        return report;
    }

    public boolean restockInventory(String productId, int quantity) {
        CommandInvoker invoker = new CommandInvoker();
        ICommand restock = new RestockCommand(productId, quantity, kiosk);
        return invoker.execute(restock);
    }
}
```

#### Step 2.5 — KioskBuilder

```java
// kiosk/KioskBuilder.java
// PATTERN: Builder — constructs complex kiosk configurations step-by-step
public class KioskBuilder {
    private KioskFactory factory;
    private boolean withRefrigeration = false;
    private boolean withSolar = false;
    private boolean withNetwork = false;
    private IDispenserImpl customDispenserImpl = null;

    public KioskBuilder(KioskFactory factory) {
        this.factory = factory;
    }

    public KioskBuilder addRefrigeration() { withRefrigeration = true; return this; }
    public KioskBuilder addSolarMonitor() { withSolar = true; return this; }
    public KioskBuilder addNetworkModule() { withNetwork = true; return this; }
    public KioskBuilder withDispenserImpl(IDispenserImpl impl) { customDispenserImpl = impl; return this; }

    public KioskInterface build(String kioskId) {
        BaseKiosk kiosk = factory.createKiosk(kioskId);

        // PATTERN: Bridge — swap dispenser implementation at build time
        if (customDispenserImpl != null) {
            kiosk.getDispenser().setImpl(customDispenserImpl);
        }

        // PATTERN: Decorator — wrap kiosk with optional hardware modules
        if (withRefrigeration) kiosk = new RefrigerationDecorator(kiosk);
        if (withSolar) kiosk = new SolarMonitorDecorator(kiosk);
        if (withNetwork) kiosk = new NetworkConnectivityDecorator(kiosk);

        CentralRegistry.getInstance().registerKiosk(kioskId, kiosk);
        return new KioskInterface(kiosk);
    }
}
```

---

### Phase 3 — Hardware Abstraction: Bridge & Decorator

**Goal:** Decouple dispenser logic from hardware; attach optional modules without modifying base kiosk.

#### Step 3.1 — Bridge: Dispenser abstraction and implementations

```java
// hardware/dispenser/Dispenser.java
// PATTERN: Bridge (Abstraction) — holds a reference to DispenserImpl, delegates work to it
public class Dispenser implements IDispenser {
    private IDispenserImpl impl;  // Bridge: abstraction holds implementor

    public Dispenser(IDispenserImpl impl) { this.impl = impl; }

    // PATTERN: Bridge — swap implementation at runtime without changing this class
    @Override
    public void setImpl(IDispenserImpl newImpl) { this.impl = newImpl; }

    @Override
    public boolean dispense(String productId, int quantity) {
        if (!isOperational()) {
            EventBus.getInstance().publish("HardwareFailureEvent",
                Map.of("component", "dispenser", "productId", productId));
            return false;
        }
        return impl.performDispense(productId, quantity);
    }

    @Override
    public boolean isOperational() { return impl != null; }
}

// hardware/dispenser/SpiralDispenserImpl.java
// PATTERN: Bridge (ConcreteImplementor)
public class SpiralDispenserImpl implements IDispenserImpl {
    @Override public boolean performDispense(String productId, int qty) {
        System.out.println("[SpiralDispenser] Rotating spiral motor for: " + productId);
        return true;
    }
    @Override public void calibrate() { System.out.println("[SpiralDispenser] Calibrating spiral..."); }
    @Override public String getHardwareType() { return "SPIRAL"; }
}

// hardware/dispenser/RoboticArmDispenserImpl.java
public class RoboticArmDispenserImpl implements IDispenserImpl {
    @Override public boolean performDispense(String productId, int qty) {
        System.out.println("[RoboticArm] Arm extending to retrieve: " + productId);
        return true;
    }
    @Override public void calibrate() { System.out.println("[RoboticArm] Running arm calibration sequence..."); }
    @Override public String getHardwareType() { return "ROBOTIC_ARM"; }
}

// hardware/dispenser/ConveyorDispenserImpl.java — similar structure
```

#### Step 3.2 — HardwareProxy

```java
// hardware/HardwareProxy.java
// PATTERN: Proxy — checks hardware availability before delegating to real module
public class HardwareProxy implements IHardwareModule {
    private final IHardwareModule realModule;
    private boolean forcedUnavailable = false;

    public HardwareProxy(IHardwareModule realModule) { this.realModule = realModule; }

    @Override
    public boolean isAvailable() {
        if (forcedUnavailable) return false;
        return realModule.isAvailable();
    }

    @Override
    public void initialize() {
        if (isAvailable()) realModule.initialize();
        else System.out.println("[HardwareProxy] Cannot initialize: module unavailable.");
    }

    public void setForcedUnavailable(boolean unavailable) { this.forcedUnavailable = unavailable; }
    @Override public String getModuleType() { return realModule.getModuleType(); }
    @Override public void shutdown() { if (realModule.isAvailable()) realModule.shutdown(); }
}
```

#### Step 3.3 — Decorator for optional hardware modules

```java
// hardware/modules/KioskDecorator.java
// PATTERN: Decorator (Abstract) — wraps a BaseKiosk, forwarding all calls and adding capability
public abstract class KioskDecorator extends BaseKiosk {
    protected final BaseKiosk wrappedKiosk;

    public KioskDecorator(BaseKiosk kiosk) {
        super(kiosk.getKioskId());
        this.wrappedKiosk = kiosk;
        // Inherit all components from the wrapped kiosk
        this.setDispenser(kiosk.getDispenser());
        this.setVerificationModule(kiosk.getVerificationModule());
        this.setPricingStrategy(kiosk.getPricingStrategy());
        this.setInventoryPolicy(kiosk.getInventoryPolicy());
    }

    @Override protected boolean checkHardwareHealth() { return wrappedKiosk.checkOperationalStatus(); }
    @Override protected boolean checkNetworkAvailability() { return true; }
    @Override protected IInventoryItem getInventoryItem(String id) { return wrappedKiosk.getInventoryItem(id); }
    @Override protected boolean checkInventory(String id, int qty) { return wrappedKiosk.checkInventory(id, qty); }
}

// hardware/modules/RefrigerationDecorator.java
// PATTERN: Decorator (Concrete) — adds refrigeration without modifying BaseKiosk
public class RefrigerationDecorator extends KioskDecorator {
    private double currentTemperature = 4.0; // Celsius
    private final IHardwareModule refrigerationUnit;

    public RefrigerationDecorator(BaseKiosk kiosk) {
        super(kiosk);
        this.refrigerationUnit = new HardwareProxy(new RefrigerationUnit());
        this.refrigerationUnit.initialize();
        System.out.println("[RefrigerationDecorator] Refrigeration module attached to " + kiosk.getKioskId());
    }

    public double getCurrentTemperature() { return currentTemperature; }

    public boolean requiresRefrigeration(String productId) {
        // Products marked as refrigerated cannot be dispensed if unit is unavailable
        return refrigerationUnit.isAvailable();
    }

    @Override
    protected boolean checkHardwareHealth() {
        return refrigerationUnit.isAvailable() && wrappedKiosk.checkOperationalStatus();
    }
}

// hardware/modules/SolarMonitorDecorator.java — adds solar power monitoring
// hardware/modules/NetworkConnectivityDecorator.java — adds network checks
```

---

### Phase 4 — Inventory System: Composite & Proxy

**Goal:** Support individual products and nested bundles; enforce secure access.

#### Step 4.1 — Composite pattern for inventory hierarchy

```java
// inventory/Product.java
// PATTERN: Composite (Leaf) — an individual product
public class Product implements IInventoryItem {
    private final String id;
    private final String name;
    private int stockCount;
    private int reservedCount = 0;     // reserved in active transactions
    private int hardwareFaultCount = 0; // unavailable due to hardware
    private double basePrice;
    private boolean requiresRefrigeration = false;

    public Product(String id, String name, int stock, double price) {
        this.id = id; this.name = name; this.stockCount = stock; this.basePrice = price;
    }

    // PATTERN: Derived attribute — never stored, always computed
    @Override
    public int getAvailableStock() {
        return Math.max(0, stockCount - reservedCount - hardwareFaultCount);
    }

    @Override public boolean isAvailable() { return getAvailableStock() > 0; }

    // Reserve stock during transaction (committed or released on outcome)
    public void reserve(int qty) { reservedCount += qty; }
    public void releaseReservation(int qty) { reservedCount = Math.max(0, reservedCount - qty); }
    public void commitReservation(int qty) { stockCount -= qty; reservedCount = Math.max(0, reservedCount - qty); }

    public void markHardwareUnavailable(int qty) { hardwareFaultCount += qty; }
    public void clearHardwareFault() { hardwareFaultCount = 0; }

    // Leaf nodes: composite operations are no-ops
    @Override public void add(IInventoryItem item) { throw new UnsupportedOperationException("Product is a leaf"); }
    @Override public void remove(IInventoryItem item) { throw new UnsupportedOperationException("Product is a leaf"); }
    @Override public List<IInventoryItem> getChildren() { return Collections.emptyList(); }

    @Override public String getId() { return id; }
    @Override public String getName() { return name; }
    @Override public double getBasePrice() { return basePrice; }
}

// inventory/ProductBundle.java
// PATTERN: Composite (Composite node) — contains other bundles or products
public class ProductBundle implements IInventoryItem {
    private final String id;
    private final String name;
    private final List<IInventoryItem> children = new ArrayList<>();
    private double bundleDiscount = 0.0;

    public ProductBundle(String id, String name) { this.id = id; this.name = name; }

    @Override
    public void add(IInventoryItem item) { children.add(item); }

    @Override
    public void remove(IInventoryItem item) { children.remove(item); }

    @Override
    public List<IInventoryItem> getChildren() { return Collections.unmodifiableList(children); }

    // PATTERN: Composite — availability propagates recursively through all children
    @Override
    public int getAvailableStock() {
        return children.stream()
            .mapToInt(IInventoryItem::getAvailableStock)
            .min()
            .orElse(0); // bundle is available only if ALL children are available
    }

    @Override
    public boolean isAvailable() {
        return children.stream().allMatch(IInventoryItem::isAvailable);
    }

    // PATTERN: Composite — price is the sum of all children prices minus discount
    @Override
    public double getBasePrice() {
        double total = children.stream().mapToDouble(IInventoryItem::getBasePrice).sum();
        return total * (1.0 - bundleDiscount);
    }

    @Override public String getId() { return id; }
    @Override public String getName() { return name; }
    public void setBundleDiscount(double discount) { this.bundleDiscount = discount; }
}
```

#### Step 4.2 — Secure Inventory Proxy

```java
// inventory/SecureInventoryProxy.java
// PATTERN: Proxy — adds authorization, logging, and access restriction before delegating
public class SecureInventoryProxy {
    private static SecureInventoryProxy instance;
    private final InventoryManager realManager;
    private final List<String> accessLog = new ArrayList<>();

    private SecureInventoryProxy() { this.realManager = new InventoryManager(); }

    public static synchronized SecureInventoryProxy getInstance() {
        if (instance == null) instance = new SecureInventoryProxy();
        return instance;
    }

    public IInventoryItem getItem(String productId) {
        logAccess("READ", productId);
        return realManager.getItem(productId);
    }

    // Only commits when transaction is fully successful
    public boolean commitInventoryUpdate(String productId, int qty, String transactionId) {
        logAccess("COMMIT", productId + " qty=" + qty + " txId=" + transactionId);
        if (!isAuthorized(transactionId)) {
            System.out.println("[SecureInventoryProxy] DENIED: unauthorized commit attempt.");
            return false;
        }
        return realManager.commitUpdate(productId, qty);
    }

    public boolean reserveStock(String productId, int qty) {
        logAccess("RESERVE", productId + " qty=" + qty);
        return realManager.reserve(productId, qty);
    }

    public void releaseReservation(String productId, int qty) {
        logAccess("RELEASE", productId + " qty=" + qty);
        realManager.release(productId, qty);
    }

    private boolean isAuthorized(String transactionId) {
        return transactionId != null && !transactionId.isEmpty();
    }

    private void logAccess(String operation, String detail) {
        String entry = "[" + LocalDateTime.now() + "] " + operation + ": " + detail;
        accessLog.add(entry);
        System.out.println("[SecureInventoryProxy] " + entry);
    }

    public List<String> getAccessLog() { return Collections.unmodifiableList(accessLog); }
}
```

#### Step 4.3 — Flyweight for shared product metadata

```java
// inventory/ProductCatalogue.java
// PATTERN: Flyweight — shared intrinsic product data; extrinsic data (stock) stays per-kiosk
public class ProductCatalogue {
    // Flyweight store: intrinsic data shared across all kiosks
    private static final Map<String, ProductMetadata> sharedMetadata = new HashMap<>();

    public static ProductMetadata getMetadata(String productId) {
        return sharedMetadata.get(productId);
    }

    public static void registerProduct(String id, String name, String category, double basePrice) {
        // PATTERN: Flyweight — store only once regardless of how many kiosks use it
        sharedMetadata.putIfAbsent(id, new ProductMetadata(id, name, category, basePrice));
    }

    // Intrinsic state (shared, immutable)
    public static class ProductMetadata {
        public final String id, name, category;
        public final double basePrice;
        ProductMetadata(String id, String name, String category, double price) {
            this.id = id; this.name = name; this.category = category; this.basePrice = price;
        }
    }
}
```

---

### Phase 5 — Payment System: Adapter & Strategy

**Goal:** Unify incompatible payment APIs; support swappable pricing.

#### Step 5.1 — Simulated third-party APIs (incompatible)

```java
// payment/thirdparty/LegacyCreditCardAPI.java
// Simulates an incompatible third-party API — not our interface
public class LegacyCreditCardAPI {
    public int initiateCharge(double dollarAmount, String cardToken) {
        // Returns a charge reference ID, or -1 on failure
        System.out.println("[CreditCardAPI] Charging $" + dollarAmount);
        return 12345; // simulated success
    }
    public boolean reverseCharge(int chargeRefId) {
        System.out.println("[CreditCardAPI] Reversing charge: " + chargeRefId);
        return true;
    }
}

// payment/thirdparty/WalletSDK.java — completely different API
public class WalletSDK {
    public String deductBalance(String walletId, int amountInCents) { /* returns receipt code */ return "WLT-OK"; }
    public boolean creditBack(String receiptCode) { return true; }
}

// payment/thirdparty/UPIGateway.java — yet another incompatible API
public class UPIGateway {
    public boolean sendPaymentRequest(String vpa, double amount, String ref) { return true; }
    public boolean initiateRefund(String ref, double amount) { return true; }
}
```

#### Step 5.2 — Adapter for each provider

```java
// payment/adapters/CreditCardAdapter.java
// PATTERN: Adapter — translates LegacyCreditCardAPI into IPaymentProvider
public class CreditCardAdapter implements IPaymentProvider {
    private final LegacyCreditCardAPI legacyApi;
    private final Map<String, Integer> chargeRefs = new HashMap<>(); // txId → chargeRefId

    public CreditCardAdapter(LegacyCreditCardAPI api) { this.legacyApi = api; }

    @Override
    public boolean processPayment(String transactionId, double amount) {
        int ref = legacyApi.initiateCharge(amount, transactionId);
        if (ref != -1) { chargeRefs.put(transactionId, ref); return true; }
        return false;
    }

    @Override
    public boolean refund(String transactionId) {
        Integer ref = chargeRefs.get(transactionId);
        return ref != null && legacyApi.reverseCharge(ref);
    }

    @Override public String getProviderName() { return "CreditCard"; }
}

// payment/adapters/DigitalWalletAdapter.java
// PATTERN: Adapter — translates WalletSDK into IPaymentProvider
public class DigitalWalletAdapter implements IPaymentProvider {
    private final WalletSDK sdk;
    private final Map<String, String> receipts = new HashMap<>();

    public DigitalWalletAdapter(WalletSDK sdk) { this.sdk = sdk; }

    @Override
    public boolean processPayment(String transactionId, double amount) {
        int cents = (int)(amount * 100);
        String receipt = sdk.deductBalance(transactionId, cents);
        if ("WLT-OK".equals(receipt)) { receipts.put(transactionId, receipt); return true; }
        return false;
    }

    @Override
    public boolean refund(String transactionId) {
        String receipt = receipts.get(transactionId);
        return receipt != null && sdk.creditBack(receipt);
    }

    @Override public String getProviderName() { return "DigitalWallet"; }
}

// payment/adapters/UPIAdapter.java — similar structure
```

#### Step 5.3 — Payment Provider Registry

```java
// payment/PaymentProviderRegistry.java
// Holds all registered adapters; adding a new provider requires only registering a new adapter
public class PaymentProviderRegistry {
    private static PaymentProviderRegistry instance;
    private final Map<String, IPaymentProvider> providers = new LinkedHashMap<>();

    private PaymentProviderRegistry() {
        // Register built-in adapters
        register(new CreditCardAdapter(new LegacyCreditCardAPI()));
        register(new DigitalWalletAdapter(new WalletSDK()));
        register(new UPIAdapter(new UPIGateway()));
    }

    public static synchronized PaymentProviderRegistry getInstance() {
        if (instance == null) instance = new PaymentProviderRegistry();
        return instance;
    }

    // PATTERN: Open/Closed — adding a new provider NEVER modifies this class
    public void register(IPaymentProvider provider) {
        providers.put(provider.getProviderName(), provider);
        System.out.println("[PaymentRegistry] Registered provider: " + provider.getProviderName());
    }

    public IPaymentProvider getProvider(String name) { return providers.get(name); }
    public Collection<IPaymentProvider> getAllProviders() { return providers.values(); }
}
```

#### Step 5.4 — Strategy pattern for pricing

```java
// pricing/StandardPricingStrategy.java
// PATTERN: Strategy (ConcreteStrategy)
public class StandardPricingStrategy implements IPricingStrategy {
    @Override
    public double computePrice(IInventoryItem item, Map<String, Object> context) {
        return item.getBasePrice(); // no modifications
    }
}

// pricing/DiscountedPricingStrategy.java
public class DiscountedPricingStrategy implements IPricingStrategy {
    private double discountRate;
    public DiscountedPricingStrategy(double rate) { this.discountRate = rate; }

    @Override
    public double computePrice(IInventoryItem item, Map<String, Object> context) {
        return item.getBasePrice() * (1.0 - discountRate);
    }
}

// pricing/EmergencyPricingStrategy.java
public class EmergencyPricingStrategy implements IPricingStrategy {
    @Override
    public double computePrice(IInventoryItem item, Map<String, Object> context) {
        // Emergency mode: essential items at cost price
        return item.getBasePrice() * 0.0; // free distribution in disaster zone
    }
}
```

---

### Phase 6 — Transaction System: Command & Observer

**Goal:** Model all operations as commands; connect subsystems through events.

#### Step 6.1 — Command pattern

```java
// commands/CommandInvoker.java
// PATTERN: Command — manages execution, undo, and history logging
public class CommandInvoker {
    private final Deque<ICommand> history = new ArrayDeque<>();

    public boolean execute(ICommand command) {
        boolean success = command.execute();
        command.log();
        if (success) history.push(command);
        return success;
    }

    public boolean undoLast() {
        if (history.isEmpty()) return false;
        ICommand last = history.pop();
        return last.undo();
    }
}

// commands/PurchaseItemCommand.java
// PATTERN: Command (ConcreteCommand) + Memento (saves state for rollback)
public class PurchaseItemCommand implements ICommand {
    private final BaseKiosk kiosk;
    private final String userId, productId;
    private final int quantity;
    private final double price;
    private final String paymentProvider;
    private TransactionMemento savedState;  // PATTERN: Memento
    private String transactionId;

    public PurchaseItemCommand(BaseKiosk kiosk, String userId, String productId, int qty, double price) {
        this.kiosk = kiosk; this.userId = userId;
        this.productId = productId; this.quantity = qty; this.price = price;
        this.paymentProvider = "CreditCard"; // default; extend to accept as param
        this.transactionId = UUID.randomUUID().toString();
    }

    @Override
    public boolean execute() {
        SecureInventoryProxy inv = SecureInventoryProxy.getInstance();

        // Step 1: Save state before any mutation (Memento)
        savedState = new TransactionMemento(transactionId, productId, quantity, price);

        // Step 2: Reserve inventory
        if (!inv.reserveStock(productId, quantity)) {
            EventBus.getInstance().publish("TransactionFailedEvent",
                Map.of("reason", "INSUFFICIENT_STOCK", "productId", productId));
            return false;
        }

        // Step 3: Process payment
        IPaymentProvider provider = PaymentProviderRegistry.getInstance().getProvider(paymentProvider);
        if (!provider.processPayment(transactionId, price * quantity)) {
            inv.releaseReservation(productId, quantity); // rollback reservation
            EventBus.getInstance().publish("TransactionFailedEvent",
                Map.of("reason", "PAYMENT_FAILED", "txId", transactionId));
            return false;
        }

        // Step 4: Dispense product
        if (!kiosk.getDispenser().dispense(productId, quantity)) {
            // PATTERN: Memento — restore state on dispenser failure
            provider.refund(transactionId);
            inv.releaseReservation(productId, quantity);
            EventBus.getInstance().publish("TransactionFailedEvent",
                Map.of("reason", "DISPENSER_FAILURE", "txId", transactionId));
            return false;
        }

        // Step 5: Commit inventory (only here, after full success)
        inv.commitInventoryUpdate(productId, quantity, transactionId);

        // Step 6: Notify observers
        EventBus.getInstance().publish("TransactionCompletedEvent",
            Map.of("txId", transactionId, "productId", productId, "qty", quantity));

        // Step 7: Persist transaction
        PersistenceManager.getInstance().saveTransaction(
            new Transaction(transactionId, userId, productId, quantity, price));

        return true;
    }

    @Override
    public boolean undo() {
        // Triggered when rolling back a completed transaction (refund flow)
        if (savedState == null) return false;
        IPaymentProvider provider = PaymentProviderRegistry.getInstance().getProvider(paymentProvider);
        boolean refunded = provider.refund(transactionId);
        if (refunded) SecureInventoryProxy.getInstance().releaseReservation(productId, quantity);
        return refunded;
    }

    @Override
    public void log() {
        System.out.println("[PurchaseItemCommand] txId=" + transactionId +
            " product=" + productId + " qty=" + quantity + " price=" + (price * quantity));
        PersistenceManager.getInstance().saveTransaction(
            new Transaction(transactionId, userId, productId, quantity, price));
    }

    @Override public String getCommandType() { return "PURCHASE"; }
}

// commands/RefundCommand.java — mirrors purchase, triggers provider.refund()
// commands/RestockCommand.java — updates inventory stock count via SecureInventoryProxy
// commands/EmergencyModeCommand.java — triggers CentralRegistry mode change + broadcasts event
```

#### Step 6.2 — Memento

```java
// transaction/TransactionMemento.java
// PATTERN: Memento — captures transaction state snapshot before execution
public class TransactionMemento {
    private final String transactionId;
    private final String productId;
    private final int quantity;
    private final double price;
    private final long timestamp;
    private final String status;

    public TransactionMemento(String txId, String productId, int qty, double price) {
        this.transactionId = txId; this.productId = productId;
        this.quantity = qty; this.price = price;
        this.timestamp = System.currentTimeMillis();
        this.status = "PENDING";
    }

    // Getters only — state is immutable once saved
    public String getTransactionId() { return transactionId; }
    public String getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public String getStatus() { return status; }
}
```

#### Step 6.3 — Observer: EventBus and subscribers

```java
// events/EventBus.java
// PATTERN: Observer (Subject/Publisher) — Singleton event bus for all system events
public class EventBus implements IEventPublisher {
    private static EventBus instance;
    private final Map<String, List<IEventSubscriber>> subscribers = new HashMap<>();

    private EventBus() {}

    public static synchronized EventBus getInstance() {
        if (instance == null) instance = new EventBus();
        return instance;
    }

    public void subscribe(String eventType, IEventSubscriber subscriber) {
        subscribers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(subscriber);
    }

    @Override
    public void publish(String eventType, Map<String, Object> eventData) {
        System.out.println("[EventBus] Publishing: " + eventType + " → " + eventData);
        List<IEventSubscriber> subs = subscribers.getOrDefault(eventType, Collections.emptyList());
        for (IEventSubscriber sub : subs) {
            sub.onEvent(eventType, eventData);
        }
    }
}

// monitoring/CityMonitoringCenter.java
// PATTERN: Observer (ConcreteSubscriber)
public class CityMonitoringCenter implements IEventSubscriber {
    @Override
    public void onEvent(String eventType, Map<String, Object> eventData) {
        switch (eventType) {
            case "HardwareFailureEvent":
                System.out.println("[CityMonitor] ALERT: Hardware failure reported — " + eventData);
                break;
            case "TransactionFailedEvent":
                System.out.println("[CityMonitor] Transaction failure logged — " + eventData);
                break;
            case "EmergencyModeActivatedEvent":
                System.out.println("[CityMonitor] EMERGENCY MODE ACTIVATED — " + eventData);
                break;
        }
    }

    @Override
    public List<String> getSubscribedEvents() {
        return List.of("HardwareFailureEvent", "TransactionFailedEvent", "EmergencyModeActivatedEvent");
    }
}

// monitoring/SupplyChainSystem.java — subscribes to LowStockEvent
// monitoring/MaintenanceService.java — subscribes to HardwareFailureEvent, triggers Chain of Responsibility
```

#### Step 6.4 — Chain of Responsibility for failure handling

```java
// failure/RetryHandler.java
// PATTERN: Chain of Responsibility (ConcreteHandler)
public class RetryHandler implements IFailureHandler {
    private IFailureHandler next;
    private static final int MAX_RETRIES = 3;

    @Override public void setNext(IFailureHandler next) { this.next = next; }

    @Override
    public boolean handle(String failureType, String context) {
        System.out.println("[RetryHandler] Attempting retry for: " + failureType);
        for (int i = 0; i < MAX_RETRIES; i++) {
            System.out.println("[RetryHandler] Retry attempt " + (i + 1));
            // Simulate retry logic — in real impl, re-attempt the operation
            if (simulateRetrySuccess()) {
                System.out.println("[RetryHandler] Resolved after " + (i + 1) + " retries.");
                return true;
            }
        }
        System.out.println("[RetryHandler] Max retries exceeded. Passing to next handler.");
        return next != null && next.handle(failureType, context);
    }

    private boolean simulateRetrySuccess() { return Math.random() > 0.7; }
}

// failure/RecalibrationHandler.java
public class RecalibrationHandler implements IFailureHandler {
    private IFailureHandler next;

    @Override public void setNext(IFailureHandler next) { this.next = next; }

    @Override
    public boolean handle(String failureType, String context) {
        System.out.println("[RecalibrationHandler] Running hardware recalibration...");
        // attempt calibration via dispenser
        boolean fixed = attemptRecalibration(failureType);
        if (fixed) { System.out.println("[RecalibrationHandler] Recalibration successful."); return true; }
        System.out.println("[RecalibrationHandler] Recalibration failed. Escalating...");
        return next != null && next.handle(failureType, context);
    }

    private boolean attemptRecalibration(String failureType) { return Math.random() > 0.5; }
}

// failure/TechnicianAlertHandler.java — final handler, always sends alert, returns false
public class TechnicianAlertHandler implements IFailureHandler {
    @Override public void setNext(IFailureHandler next) { /* terminal handler */ }

    @Override
    public boolean handle(String failureType, String context) {
        System.out.println("[TechnicianAlert] ALERT: Manual intervention required.");
        System.out.println("[TechnicianAlert] Failure type: " + failureType + " | Context: " + context);
        EventBus.getInstance().publish("HardwareFailureEvent",
            Map.of("requiresTechnician", true, "failureType", failureType));
        return false; // unresolved — kiosk enters maintenance mode
    }
}
```

#### Step 6.5 — Iterator for transaction history

```java
// transaction/TransactionIterator.java
// PATTERN: Iterator — traverses transaction log without exposing internal structure
public class TransactionIterator implements Iterator<Map<String, Object>> {
    private final List<Map<String, Object>> transactions;
    private final String kioskIdFilter;
    private int cursor = 0;

    public TransactionIterator(List<Map<String, Object>> all, String kioskIdFilter) {
        this.kioskIdFilter = kioskIdFilter;
        this.transactions = all.stream()
            .filter(t -> kioskIdFilter == null || kioskIdFilter.equals(t.get("kioskId")))
            .collect(Collectors.toList());
    }

    @Override public boolean hasNext() { return cursor < transactions.size(); }
    @Override public Map<String, Object> next() { return transactions.get(cursor++); }

    public List<Map<String, Object>> getLastN(int n) {
        int start = Math.max(0, transactions.size() - n);
        return transactions.subList(start, transactions.size());
    }
}
```

---

### Phase 7 — System Constraints & Integration

**Goal:** Wire all subsystems together and enforce every stated constraint.

#### Step 7.1 — State pattern for kiosk modes

```java
// kiosk/state/KioskState.java
public interface KioskState {
    boolean allowsPurchase();
    boolean allowsRestock();
    boolean allowsDiagnostics();
    String getStateName();
    void onEnter(BaseKiosk kiosk);
    void onExit(BaseKiosk kiosk);
}

// kiosk/state/ActiveState.java
// PATTERN: State (ConcreteState)
public class ActiveState implements KioskState {
    private final BaseKiosk kiosk;
    public ActiveState(BaseKiosk kiosk) { this.kiosk = kiosk; }

    @Override public boolean allowsPurchase() { return true; }
    @Override public boolean allowsRestock() { return true; }
    @Override public boolean allowsDiagnostics() { return true; }
    @Override public String getStateName() { return "ACTIVE"; }
    @Override public void onEnter(BaseKiosk k) { System.out.println("[State] Kiosk " + k.getKioskId() + " → ACTIVE"); }
    @Override public void onExit(BaseKiosk k) {}
}

// kiosk/state/EmergencyLockdownState.java
// PATTERN: State — restricts operations during emergency
public class EmergencyLockdownState implements KioskState {
    @Override public boolean allowsPurchase() { return true; }  // limited purchases still allowed
    @Override public boolean allowsRestock() { return false; }  // no restocking during emergency
    @Override public boolean allowsDiagnostics() { return true; }
    @Override public String getStateName() { return "EMERGENCY_LOCKDOWN"; }
    @Override public void onEnter(BaseKiosk k) {
        System.out.println("[State] EMERGENCY LOCKDOWN activated on kiosk " + k.getKioskId());
        // Switch to emergency pricing immediately
        k.setPricingStrategy(new EmergencyPricingStrategy());
    }
    @Override public void onExit(BaseKiosk k) { k.setPricingStrategy(new StandardPricingStrategy()); }
}

// kiosk/state/MaintenanceState.java — blocks all purchases and restocks
```

#### Step 7.2 — Emergency mode activation wiring

When `EmergencyModeActivatedEvent` is published:
1. `CentralRegistry.setSystemMode("EMERGENCY")`
2. All registered kiosks switch state to `EmergencyLockdownState`
3. `EmergencyPricingStrategy` is activated
4. Purchase limits enforced via `EmergencyInventoryPolicy.getMaxPurchaseQuantity()` returning a small cap (e.g., 2 units per user)

#### Step 7.3 — Failure chain assembly (wiring)

```java
// Wire the chain in MaintenanceService or wherever failures are handled:
IFailureHandler retry = new RetryHandler();
IFailureHandler recal = new RecalibrationHandler();
IFailureHandler alert = new TechnicianAlertHandler();
retry.setNext(recal);
recal.setNext(alert);

// Usage — triggered from EventBus subscriber:
retry.handle("DISPENSER_FAILURE", "kioskId=K001");
```

#### Step 7.4 — Observer wiring at system startup

```java
// At system startup, register all observers:
EventBus bus = EventBus.getInstance();

CityMonitoringCenter monitor = new CityMonitoringCenter();
SupplyChainSystem supplyChain = new SupplyChainSystem();
MaintenanceService maintenance = new MaintenanceService();

bus.subscribe("HardwareFailureEvent", monitor);
bus.subscribe("HardwareFailureEvent", maintenance);
bus.subscribe("LowStockEvent", supplyChain);
bus.subscribe("TransactionFailedEvent", monitor);
bus.subscribe("TransactionCompletedEvent", supplyChain);
bus.subscribe("EmergencyModeActivatedEvent", monitor);
```

---

### Phase 8 — Simulation Scenarios & Documentation

**Goal:** Implement the three Path B simulation scenarios as runnable demos.

#### Scenario A — Adding a new hardware module (Decorator)

```java
// simulation/ScenarioA_NewHardwareModule.java
public class ScenarioA_NewHardwareModule {
    public static void main(String[] args) {
        System.out.println("=== PATH B SCENARIO A: Adding a New Hardware Module ===\n");

        // Build a base food kiosk
        KioskInterface baseKiosk = new KioskBuilder(new FoodKioskFactory())
            .build("KIOSK-001");

        System.out.println("Base kiosk created. Diagnostics: " + baseKiosk.runDiagnostics());

        // Add refrigeration dynamically — NO changes to BaseKiosk or FoodKiosk
        KioskInterface refrigeratedKiosk = new KioskBuilder(new FoodKioskFactory())
            .addRefrigeration()
            .addSolarMonitor()
            .build("KIOSK-002");

        System.out.println("\nRefrigerated kiosk created. Diagnostics: " + refrigeratedKiosk.runDiagnostics());
        System.out.println("\n[DEMO] Hardware module added via Decorator pattern — FoodKiosk class was NOT modified.");
    }
}
```

#### Scenario B — New payment provider (Adapter)

```java
// simulation/ScenarioB_NewPaymentProvider.java
public class ScenarioB_NewPaymentProvider {
    public static void main(String[] args) {
        System.out.println("=== PATH B SCENARIO B: Integrating a New Payment Provider ===\n");

        // Show existing providers
        PaymentProviderRegistry registry = PaymentProviderRegistry.getInstance();
        System.out.println("Current providers: " + registry.getAllProviders().stream()
            .map(IPaymentProvider::getProviderName).collect(Collectors.joining(", ")));

        // Add a NEW provider — only requires a new Adapter class
        // PATTERN: Adapter — CryptoPaymentAdapter wraps fictional CryptoAPI
        registry.register(new CryptoPaymentAdapter(new FakeCryptoAPI()));

        System.out.println("After adding crypto: " + registry.getAllProviders().stream()
            .map(IPaymentProvider::getProviderName).collect(Collectors.joining(", ")));

        // Process payment using new provider
        IPaymentProvider crypto = registry.getProvider("Crypto");
        boolean result = crypto.processPayment("TX-CRYPTO-001", 49.99);
        System.out.println("\n[DEMO] Crypto payment processed: " + result);
        System.out.println("[DEMO] Zero changes to existing Adapters, KioskInterface, or Transaction classes.");
    }
}
```

#### Scenario C — Nested bundle inventory (Composite)

```java
// simulation/ScenarioC_NestedBundles.java
public class ScenarioC_NestedBundles {
    public static void main(String[] args) {
        System.out.println("=== PATH B SCENARIO C: Nested Product Bundle Inventory ===\n");

        // Individual products (leaves)
        Product bandages = new Product("P001", "Bandages", 50, 2.00);
        Product antiseptic = new Product("P002", "Antiseptic", 30, 4.00);
        Product paracetamol = new Product("P003", "Paracetamol", 20, 1.50);
        Product waterBottle = new Product("P004", "Water Bottle", 100, 1.00);
        Product energyBar = new Product("P005", "Energy Bar", 80, 2.50);

        // Mid-level bundle (contains products)
        ProductBundle medKit = new ProductBundle("B001", "Med Kit");
        medKit.add(bandages);
        medKit.add(antiseptic);
        medKit.add(paracetamol);
        medKit.setBundleDiscount(0.10); // 10% bundle discount

        // Top-level bundle (contains another bundle + products)
        ProductBundle emergencyKit = new ProductBundle("B002", "Emergency Kit");
        emergencyKit.add(medKit);        // nested bundle!
        emergencyKit.add(waterBottle);
        emergencyKit.add(energyBar);
        emergencyKit.setBundleDiscount(0.15);

        // PATTERN: Composite — getAvailableStock() propagates recursively
        System.out.println("Med Kit available stock: " + medKit.getAvailableStock());
        System.out.println("Emergency Kit available stock: " + emergencyKit.getAvailableStock());
        System.out.println("Emergency Kit base price: $" + emergencyKit.getBasePrice());

        // Reduce paracetamol stock to zero — should affect all parent bundles
        paracetamol.commitReservation(20); // deplete stock

        System.out.println("\nAfter depleting paracetamol:");
        System.out.println("Med Kit available: " + medKit.isAvailable());           // false
        System.out.println("Emergency Kit available: " + emergencyKit.isAvailable()); // false
        System.out.println("\n[DEMO] Composite propagation works recursively through the bundle tree.");
    }
}
```

---

## 5. System Constraints Enforcement

Every constraint from the project spec must be satisfied. Here is where each one is enforced:

| Constraint | Enforcement Location | Mechanism |
|---|---|---|
| **Atomic transactions** | `PurchaseItemCommand.execute()` | Memento + step-by-step rollback with `releaseReservation` and `refund` |
| **Emergency purchase limit** | `EmergencyInventoryPolicy.getMaxPurchaseQuantity()` | Returns cap of 2; checked in `checkInventory()` |
| **Hardware dependency** | `HardwareProxy.isAvailable()` → `Product.isAvailable()` | If proxy returns false, product marked unavailable; purchase blocked |
| **Inventory consistency** | `SecureInventoryProxy.commitInventoryUpdate()` only called on success | Inventory reservation is separate from commit; commit happens last |
| **Available stock as derived attribute** | `Product.getAvailableStock()` computed at call time | `stockCount - reservedCount - hardwareFaultCount`; never stored |
| **Computed product price** | `BaseKiosk.computePrice()` via `IPricingStrategy.computePrice()` | Always computed at purchase time using current strategy |
| **Kiosk operational status** | `BaseKiosk.checkOperationalStatus()` Template Method | Derived from hardware health + state + network; never stored |

---

## 6. Class Specifications

### Key class responsibilities summary

| Class | Pattern(s) | Primary Responsibility |
|---|---|---|
| `CentralRegistry` | Singleton | Global configuration and kiosk lookup |
| `PersistenceManager` | Singleton | All file I/O (JSON/CSV) |
| `KioskFactory` | Abstract Factory | Creates compatible kiosk component families |
| `KioskBuilder` | Builder | Assembles kiosks with optional modules |
| `KioskInterface` | Facade | 4-method external API; hides all internals |
| `BaseKiosk` | Template Method, Prototype | Abstract kiosk with purchase flow skeleton |
| `Dispenser` | Bridge (Abstraction) | High-level dispensing logic |
| `*DispenserImpl` | Bridge (Implementation) | Hardware-specific dispensing |
| `KioskDecorator` | Decorator | Abstract wrapper for optional modules |
| `RefrigerationDecorator` | Decorator | Adds refrigeration capability |
| `SecureInventoryProxy` | Proxy, Singleton | Auth + logging over InventoryManager |
| `HardwareProxy` | Proxy | Health-checks hardware before delegation |
| `Product` | Composite (Leaf) | Individual inventory item with derived stock |
| `ProductBundle` | Composite (Node) | Recursive bundle with propagated availability |
| `ProductCatalogue` | Flyweight | Shared product metadata factory |
| `*Adapter` | Adapter | Translates provider API to IPaymentProvider |
| `PaymentProviderRegistry` | Singleton | Holds and retrieves payment adapters |
| `*PricingStrategy` | Strategy | Interchangeable pricing algorithms |
| `CommandInvoker` | Command | Executes, undoes, and logs commands |
| `PurchaseItemCommand` | Command, Memento | Full purchase lifecycle with rollback |
| `TransactionMemento` | Memento | Immutable state snapshot before execution |
| `EventBus` | Observer (Subject), Singleton | Publishes events to all subscribers |
| `CityMonitoringCenter` | Observer (Subscriber) | Receives and logs system events |
| `*State` | State | Defines allowed operations per kiosk mode |
| `RetryHandler` | Chain of Responsibility | First failure handler; retries 3 times |
| `RecalibrationHandler` | Chain of Responsibility | Attempts hardware recalibration |
| `TechnicianAlertHandler` | Chain of Responsibility | Terminal handler; sends alert |
| `TransactionIterator` | Iterator | Filtered traversal of transaction log |

---

## 7. Simulation Scenarios (Path B Required)

All three scenarios below must be runnable and produce clear console output demonstrating the pattern in action.

### Scenario A: Adding a new hardware module
- **Pattern demonstrated:** Decorator, Bridge
- **What to show:** A FoodKiosk created without refrigeration, then a second one created with refrigeration and solar. Show that `FoodKiosk.java` is unchanged. Show that the refrigerated kiosk blocks purchase if refrigeration unit fails.

### Scenario B: Integrating a new payment provider
- **Pattern demonstrated:** Adapter
- **What to show:** Register a `CryptoPaymentAdapter` at runtime. Process a payment through it. Show that no existing files were modified — only the new adapter class was added.

### Scenario C: Nested bundle inventory expansion
- **Pattern demonstrated:** Composite
- **What to show:** A three-level hierarchy (EmergencyKit → MedKit → Product). Show that `getAvailableStock()` propagates up correctly. Deplete one leaf product and show that all ancestor bundles become unavailable automatically.

---

## 8. Deliverables Checklist

### Subtask 1 (Week 2)
- [ ] High-level architecture diagram showing 5 subsystems and their interfaces
- [ ] Preliminary class diagram (all interfaces + major classes, 20+ classes)
- [ ] Design pattern list: name, category, class, justification for each

### Subtask 2 (Week 5)
- [ ] Partial source code: at minimum `KioskFactory` hierarchy + `Dispenser`/`DispenserImpl` Bridge + one `Adapter`
- [ ] Basic simulation: create a kiosk via factory, add a decorator, process a purchase, print log
- [ ] Updated class diagram reflecting implementation decisions

### Final Submission (Week 7–8)
- [ ] Complete source code — all 8 phases implemented
- [ ] `// PATTERN: [Name]` comment on every pattern implementation
- [ ] Persistence working — inventory and transactions saved to JSON files
- [ ] Design report:
  - Problem understanding
  - Subsystem architecture description
  - Per-pattern section: problem it solves, alternative considered, why chosen
  - Major classes explained
  - Team member contributions
- [ ] Three UML diagrams:
  - Class diagram (full system)
  - Sequence diagram (purchase flow: KioskInterface → Command → Memento → Observer → PersistenceManager)
  - Subsystem architecture diagram (package boundaries and cross-package interface dependencies)
- [ ] All three Path B simulation scenarios runnable with clear output
- [ ] README with: system overview, how to run each scenario, pattern table (pattern → class → file)
- [ ] 10–12 min presentation covering: architecture (2 min) + live demo of 3 scenarios (4 min) + pattern walkthrough with code (4 min) + team contributions (2 min)

---

## 9. Grading Notes for AI Agents

These are the highest-leverage actions for maximising marks:

### Source code annotations
Every pattern implementation must have this comment directly above the class declaration:
```java
// PATTERN: [PatternName] ([GoF Category]) — [one-line justification]
// Example: PATTERN: Bridge (Structural) — decouples Dispenser abstraction from hardware implementation
```

### Report: justify every pattern with the "problem without it" argument
For each pattern, write:
> "Without [Pattern], [specific problem]. For example, without Bridge, swapping the `SpiralDispenserImpl` for `RoboticArmDispenserImpl` would require modifying `KioskInterface`, `BaseKiosk`, and all factory classes — violating Open/Closed Principle."

### Four OOP principles — make them explicit
- **Encapsulation:** State in the report: "All fields in `Product`, `BaseKiosk`, `CentralRegistry` are private. External access is controlled via methods only." Point to specific private fields.
- **Abstraction:** Show the `interfaces/` package in the class diagram. State: "All cross-subsystem dependencies point to interfaces, never to concrete classes."
- **Inheritance:** Show the `BaseKiosk → PharmacyKiosk` override in the sequence diagram. Explain what the override adds.
- **Low coupling:** Quantify: "`KioskInterface` has 0 import statements from the `hardware/` or `payment/` packages."

### Constraint proof in the report
For each of the 4 system constraints, include a sequence diagram or annotated code trace showing the exact chain of calls that enforces it. Graders want to see that constraints are mechanically enforced, not just described.

### Pattern count target
Aim for 13+ distinct patterns implemented. The full list in this document covers all 23 relevant GoF patterns. Priority order if time is limited:
1. Abstract Factory (required by spec)
2. Facade (required by spec)
3. Command (required by spec)
4. Singleton (required by spec — CentralRegistry)
5. Bridge (core Path B requirement)
6. Decorator (core Path B requirement)
7. Adapter (core Path B requirement)
8. Composite (core Path B requirement)
9. Proxy (core Path B requirement)
10. Observer (required by spec — monitoring)
11. Strategy (pricing policies)
12. State (kiosk operational modes)
13. Memento (atomic transaction rollback)
14. Chain of Responsibility (failure handling)
15. Template Method (purchase flow skeleton)
16. Builder (optional module assembly)
17. Iterator (transaction log traversal)
18. Flyweight (shared product metadata — bonus)
19. Prototype (kiosk cloning — bonus)
20. Factory Method (within Abstract Factory — bonus)

---

*End of IMPLEMENTATION.md — this document contains all context necessary for any AI agent or developer to implement the Aura Retail OS project (Path B) from scratch and achieve maximum marks.*
