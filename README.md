# Aura Retail OS

This project implements the **Aura Retail OS** — a modular, OOP-driven autonomous retail kiosk system. This implementation focuses on the Path B objective (Modular Hardware Platform).

## Prerequisites

To compile and run the simulations, ensure your system has the following installed:

- **Java Development Kit (JDK):** Version 17 or higher.
- **Apache Maven:** Version 3.6+ (for building and dependency management).
- **Terminal/Command Prompt:** To execute the Maven goals.

## How to Run the Simulations

The project includes predefined simulation scenarios highlighting the core architecture and various design patterns implemented.

First, navigate to the root directory `d:\OOPs_project` and compile the codebase:
```bash
mvn clean compile
```

Once compiled, you can run the simulations using the `exec:java` Maven plugin depending on which scenario you want to test.

### Scenario A: Adding a New Hardware Module (Decorator + Bridge)
Demonstrates dynamically adding a hardware module (Refrigeration and Solar) via the **Decorator** pattern, and swapping out the underlying hardware dispenser implementation via the **Bridge** pattern.
```bash
mvn exec:java -Dexec.mainClass="aura.simulation.ScenarioA_NewHardwareModule"
```

### Scenario B: Integrating a New Payment Provider (Adapter)
Demonstrates the **Adapter** pattern by registering a completely new third-party payment provider API (Crypto) at runtime and successfully processing a transaction. 
```bash
mvn exec:java -Dexec.mainClass="aura.simulation.ScenarioB_NewPaymentProvider"
```

### Scenario C: Nested Bundle Inventory Expansion (Composite)
Demonstrates the **Composite** pattern by creating an N-level deep inventory hierarchy (products inside bundles) and showing how attributes like stock levels and discounts propagate recursively.
```bash
mvn exec:java -Dexec.mainClass="aura.simulation.ScenarioC_NestedBundles"
```

### Complete Subtask 2 Demo
Runs the end-to-end integration demo showcasing transactions, state transitions, subsystem interactions, the command invoker, and error handling.
```bash
mvn exec:java -Dexec.mainClass="aura.simulation.SubTask2Demo"
```

### Foundation Layer Tests
Runs foundational validation and checks persistence loading.
```bash
mvn exec:java -Dexec.mainClass="aura.simulation.FoundationTest"
```
