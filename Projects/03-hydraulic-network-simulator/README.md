# Hydraulic Network Simulator

## Overview

This Java OOP project models a directed hydraulic network and recursively propagates flow through sources, taps, sinks, splits, and multisplits. The canonical Maven implementation is in [`Project/`](Project); [`Raw files/`](Raw%20files) preserves the original course version.

## Features

- `Source`, `Tap`, `Sink`, `Split`, and configurable `Multisplit` elements.
- Recursive flow simulation across connected elements.
- Observer callbacks for flow events and maximum-flow alarms.
- Element deletion with safe incoming-reference rewiring.
- Fluent builder API for nested networks.
- Validation, defensive copies, supplied tests, and custom edge-case tests.

## What This Project Demonstrates

- Inheritance and polymorphic flow behavior.
- Recursive graph traversal and branching.
- Observer and fluent-builder patterns.
- Safe graph mutation, validation, and defensive design.
- Compatibility with an existing educational API and professor tests.

## Tech Stack

- Java 21.
- Maven and Maven Wrapper.
- JUnit 4/JUnit Jupiter with the Vintage engine.
- JaCoCo for optional, non-gating coverage reports.

## Project Structure

```text
.
├── Project/                   # Canonical implementation and Maven build
│   ├── src/hydraulic/
│   ├── test/
│   ├── docs/
│   ├── scripts/
│   ├── pom.xml
│   └── TEST_RESULTS.md
├── Raw files/                # Preserved original course project
├── ImplementationExplanation.md
└── Q&A 1.md
```

## How to Run

```bash
cd Project
./mvnw clean test
```

Windows PowerShell:

```powershell
cd Project
.\mvnw.cmd clean test
```

With global Maven, use `mvn clean test`. The [canonical README](Project/README.md) includes a builder example and coverage command.

## Testing

The canonical project includes professor/example tests plus custom tests for simulation, deletion, splits, multisplits, builders, and validation. See [`Project/TEST_RESULTS.md`](Project/TEST_RESULTS.md) for recorded results and [`Project/docs/TESTING.md`](Project/docs/TESTING.md) for details.

## Known Limitations

- In-memory educational model; it is not a pressure or pipe-physics solver.
- Simulation assumes an acyclic network and starts from the first source.
- No database, REST API, frontend, or deployment layer.
- Generated artifacts inside the preserved raw tree are intentionally untouched in this phase.

## Resume Value

Built a Java hydraulic network simulator using inheritance, recursive flow propagation, observer events, max-flow checks, graph rewiring, and a fluent builder API.
