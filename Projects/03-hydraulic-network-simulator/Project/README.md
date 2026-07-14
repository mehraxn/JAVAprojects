# Hydraulic Network Simulator

A Java OOP project that models and simulates directed hydraulic networks. It focuses on inheritance, polymorphic flow rules, recursive graph traversal, observer events, safe graph mutation, and a fluent builder API.

## Features

- `Source`, `Tap`, and `Sink` elements.
- Two-way equal division with `Split`.
- Configurable proportional division with `Multisplit`.
- Recursive flow simulation with safe disconnected branches.
- Observer-based status events and optional maximum-flow alarms.
- Element deletion with incoming-reference rewiring.
- Fluent construction of simple and nested networks.
- Defensive input validation and array copying.
- Professor/base tests plus custom edge-case tests.
- Maven Wrapper, JaCoCo reporting, test scripts, and GitHub Actions CI.

## Tech stack and requirements

- Java 21
- Maven 3.9.x (or the included Maven Wrapper)
- JUnit 4/JUnit Jupiter with the Vintage engine
- JaCoCo for non-gating coverage reports

## Build and test

Linux/macOS:

```bash
./mvnw clean test
bash scripts/test.sh
```

Windows PowerShell:

```powershell
.\mvnw.cmd clean test
.\scripts\test.ps1
```

With a system Maven installation, run `mvn clean test`. Generate the HTML coverage report with `./mvnw clean test jacoco:report`; open `target/site/jacoco/index.html` afterward.

The project retains the original course source layout: production code is under `src/hydraulic`, professor and example tests are under `test/it` and `test/example`, and added edge-case tests are under `test/custom`.

## Architecture

`Element` defines shared identity, downstream connections, maximum-flow configuration, and the simulation contract. Concrete subclasses apply their own flow rule. `HSystem` stores elements, starts simulation at the first source, deletes and rewires elements, and renders a readable graph. `SimulationObserver` decouples calculations from output collection. `HBuilder` builds nested branches with a stack of branch frames.

See [Architecture](docs/ARCHITECTURE.md), [Design Decisions](docs/DESIGN_DECISIONS.md), and [Testing](docs/TESTING.md) for details.

## Simulation example

```java
HSystem system = HSystem.build()
    .addSource("Source").withFlow(100.0)
    .linkToTap("Valve").open()
    .linkToSplit("Junction").withOutputs()
        .linkToSink("North")
        .then().linkToSink("South")
        .done()
    .complete();

system.simulate(observer, true);
```

For multisplits, prefer `withProportions(...)`. The original `withPropotions(...)` spelling remains as a compatibility alias.

## Validation and safety

Names must be non-blank; flows and capacity limits cannot be negative; output indexes must exist; multisplit proportions must be finite, non-negative, have the correct length, and sum to one. Public element arrays and proportions are defensive copies. Deletion is refused when a branching target has multiple connected outputs.

## Known limitations

- The first source in insertion order is the simulation entry point.
- Flow traversal assumes an acyclic network.
- The model is in-memory and educational; it is not a pressure/pipe physics solver.
- There is no database, REST API, frontend, or deployment layer.

## Resume value

Built and validated a Java hydraulic network simulator using OOP inheritance, recursive flow simulation, split/multisplit branching, observer-based simulation events, max-flow safety checks, element deletion with rewiring, and a fluent builder API with automated tests.
