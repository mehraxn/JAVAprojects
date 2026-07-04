# WeatherReport Façade - System Entry Point

## Table of Contents
- [Overview](#overview)
- [What is the Façade Pattern?](#what-is-the-façade-pattern)
- [The WeatherReport Class](#the-weatherreport-class)
- [Class Structure](#class-structure)
- [Available Methods](#available-methods)
  - [Utility Methods](#utility-methods)
  - [Operations Access Methods](#operations-access-methods)
- [Operations Interfaces](#operations-interfaces)
- [Usage Examples](#usage-examples)
- [Architecture Diagram](#architecture-diagram)

---

## Overview

The `WeatherReport` class serves as the **main entry point** (façade) to the Weather Report system. It provides a unified interface for external code to interact with all system functionalities, including network management, gateway operations, sensor handling, and topology configuration.

---

## What is the Façade Pattern?

The **Façade Pattern** is a structural design pattern that provides a simplified interface to a complex subsystem. Instead of exposing all the internal complexity of a system, the façade offers a single, easy-to-use entry point.

### Benefits of the Façade Pattern:

| Benefit | Description |
|---------|-------------|
| **Simplicity** | Clients interact with one class instead of multiple subsystems |
| **Decoupling** | External code is isolated from internal implementation details |
| **Maintainability** | Changes to subsystems don't affect client code |
| **Single Entry Point** | All operations are accessible through one unified interface |

---

## The WeatherReport Class

```java
public class WeatherReport {
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    private final NetworkOperations networks = OperationsFactory.getNetworkOperations();
    private final GatewayOperations gateways = OperationsFactory.getGatewayOperations();
    private final SensorOperations sensors = OperationsFactory.getSensorOperations();
    private final TopologyOperations topology = OperationsFactory.getTopologyOperations();
}
```

### Key Characteristics:

1. **Centralized Access**: All system operations are accessible through this single class
2. **Delegation**: The façade delegates specific tasks to specialized operation interfaces
3. **Factory Integration**: Uses `OperationsFactory` to obtain concrete implementations
4. **Constants**: Defines system-wide constants like `DATE_FORMAT`

---

## Understanding the Private Final Fields

The four lines that initialize the operations are crucial to understanding how the façade works:

```java
private final NetworkOperations networks = OperationsFactory.getNetworkOperations();
private final GatewayOperations gateways = OperationsFactory.getGatewayOperations();
private final SensorOperations sensors = OperationsFactory.getSensorOperations();
private final TopologyOperations topology = OperationsFactory.getTopologyOperations();
```

### Breaking Down Each Part

Let's analyze one line in detail:

```java
private final NetworkOperations networks = OperationsFactory.getNetworkOperations();
```

| Part | Meaning |
|------|---------|
| `private` | Only the `WeatherReport` class can access this variable |
| `final` | Once assigned, this variable **cannot be changed** (immutable reference) |
| `NetworkOperations` | The **interface type** (not a concrete class!) |
| `networks` | The variable name |
| `OperationsFactory.getNetworkOperations()` | Factory method that returns a concrete implementation |

### Visual Breakdown

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│  private final NetworkOperations networks = OperationsFactory.getNetworkOperations();
│  ───┬─── ──┬── ───────┬──────── ───┬───   ────────────────────┬─────────────────────
│     │      │          │            │                          │
│     │      │          │            │                          └─► Factory method call
│     │      │          │            │                              Returns: concrete 
│     │      │          │            │                              implementation
│     │      │          │            │
│     │      │          │            └─► Variable name
│     │      │          │
│     │      │          └─► Interface type (defines the contract)
│     │      │
│     │      └─► Cannot be reassigned after initialization
│     │
│     └─► Only accessible within WeatherReport class
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### Why Use Interface + Factory?

#### ❌ The Problem Without Factory

```java
// BAD: Directly creating concrete class
private final NetworkOperationsImpl networks = new NetworkOperationsImpl();
```

**Problems:**
- The façade is **tightly coupled** to `NetworkOperationsImpl`
- Hard to change implementation later
- Hard to test (can't substitute with mock objects)

#### ✅ The Solution With Factory

```java
// GOOD: Using interface + factory
private final NetworkOperations networks = OperationsFactory.getNetworkOperations();
```

**Benefits:**
- Façade only knows about the **interface** (the contract)
- Factory decides **which implementation** to provide
- Easy to swap implementations without changing façade code
- Supports testing with mock objects

### How the Pieces Work Together

#### Step 1: The Interface (Contract)

```java
// NetworkOperations.java - defines WHAT operations exist
public interface NetworkOperations {
    Network createNetwork(String code, String name, String description, String username);
    Network updateNetwork(String code, String name, String description, String username);
    Network deleteNetwork(String code, String username);
    Collection<Network> getNetworks(String... codes);
    // ... more methods
}
```

The interface defines the **contract** - what methods must exist and their signatures.

#### Step 2: The Implementation (Hidden from Façade)

```java
// NetworkOperationsImpl.java - defines HOW operations work
public class NetworkOperationsImpl implements NetworkOperations {
    @Override
    public Network createNetwork(String code, String name, String description, String username) {
        // Actual implementation code here
        // Validation, database access, business logic, etc.
    }
    // ... implement all methods from the interface
}
```

The implementation contains the **actual logic** - how each method works internally.

#### Step 3: The Factory (Provides Implementation)

```java
// OperationsFactory.java - decides WHICH implementation to use
public class OperationsFactory {
    public static NetworkOperations getNetworkOperations() {
        return new NetworkOperationsImpl();  // Returns concrete implementation
    }
    
    public static GatewayOperations getGatewayOperations() {
        return new GatewayOperationsImpl();
    }
    
    public static SensorOperations getSensorOperations() {
        return new SensorOperationsImpl();
    }
    
    public static TopologyOperations getTopologyOperations() {
        return new TopologyOperationsImpl();
    }
}
```

The factory is the **central configuration point** - it decides which concrete class to instantiate.

#### Step 4: The Façade (Uses Interface)

```java
// WeatherReport.java - only knows about the interface
public class WeatherReport {
    private final NetworkOperations networks = OperationsFactory.getNetworkOperations();
    
    public NetworkOperations networks() {
        return networks;  // Returns the interface type
    }
}
```

The façade **doesn't know or care** about the concrete implementation - it only works with the interface.

### Execution Flow Example

```java
// When WeatherReport is instantiated:
WeatherReport wr = new WeatherReport();

// Internally, this sequence happens:
// 1. OperationsFactory.getNetworkOperations() is called
// 2. Factory creates: new NetworkOperationsImpl()
// 3. This object is stored in 'networks' variable
// 4. But 'networks' only sees it as 'NetworkOperations' interface

// When you use it:
wr.networks().createNetwork("NET_01", "Name", "Desc", "admin");

// What actually happens:
// 1. networks() returns the NetworkOperations reference
// 2. createNetwork() is called on the interface
// 3. Java's polymorphism invokes the actual method in NetworkOperationsImpl
// 4. The implementation executes (validation, DB operations, etc.)
// 5. Result is returned to the caller
```

### Real-World Analogy

Think of it like ordering at a restaurant:

| Concept | Restaurant Analogy |
|---------|-------------------|
| **Interface** (`NetworkOperations`) | The **menu** - lists what you can order |
| **Implementation** (`NetworkOperationsImpl`) | The **kitchen** - actually prepares the food |
| **Factory** (`OperationsFactory`) | The **waiter** - takes your order and brings food |
| **Façade** (`WeatherReport`) | **You** (customer) - only see the menu, not the kitchen |

You don't need to know **how** the kitchen works. You just look at the menu (interface), place your order, and the waiter (factory) brings you what you ordered. The kitchen (implementation) could change completely, but as long as they serve the same menu items, you wouldn't notice!

### Summary Table

| Field | Interface | Factory Method | Requirement |
|-------|-----------|----------------|-------------|
| `networks` | `NetworkOperations` | `getNetworkOperations()` | R1 |
| `gateways` | `GatewayOperations` | `getGatewayOperations()` | R2 |
| `sensors` | `SensorOperations` | `getSensorOperations()` | R3 |
| `topology` | `TopologyOperations` | `getTopologyOperations()` | R4 |

This design pattern is called **"Programming to an Interface"** combined with the **Factory Pattern** - two fundamental principles of good object-oriented design that promote loose coupling and high maintainability!

---

## Class Structure

```
┌─────────────────────────────────────────────────────────────┐
│                      WeatherReport                          │
│                     (Façade Class)                          │
├─────────────────────────────────────────────────────────────┤
│  Constants:                                                 │
│    - DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"                   │
├─────────────────────────────────────────────────────────────┤
│  Private Fields (obtained via OperationsFactory):           │
│    - NetworkOperations networks                             │
│    - GatewayOperations gateways                             │
│    - SensorOperations sensors                               │
│    - TopologyOperations topology                            │
├─────────────────────────────────────────────────────────────┤
│  Public Methods:                                            │
│    - importDataFromFile(String filePath)                    │
│    - createUser(String username, UserType type)             │
│    - networks() → NetworkOperations                         │
│    - gateways() → GatewayOperations                         │
│    - sensors() → SensorOperations                           │
│    - topology() → TopologyOperations                        │
└─────────────────────────────────────────────────────────────┘
```

---

## Available Methods

### Utility Methods

These methods provide general system functionalities directly through the façade:

#### 1. `importDataFromFile(String filePath)`

```java
public void importDataFromFile(String filePath) {
    DataImportingService.storeMeasurements(filePath);
}
```

**Purpose**: Imports weather measurements from CSV files into the system.

**Parameters**:
- `filePath`: Path to the CSV file containing measurement data

**CSV File Format**:
```csv
date, networkCode, gatewayCode, sensorCode, value
2024-01-15 10:30:00, NET_01, GW_0001, S_000001, 25.5
```

**Delegates to**: `DataImportingService.storeMeasurements()`

---

#### 2. `createUser(String username, UserType type)`

```java
public User createUser(String username, UserType type) {
    return new CRUDRepository<>(User.class).create(new User(username, type));
}
```

**Purpose**: Creates a new user in the system with specified permissions.

**Parameters**:
- `username`: Unique identifier for the user
- `type`: User permission level (`VIEWER` or `MAINTAINER`)

**Returns**: The newly created `User` object

**User Types**:
| Type | Permissions |
|------|-------------|
| `VIEWER` | Read-only operations (consulting data and reports) |
| `MAINTAINER` | Full access (creation, update, deletion of entities) |

---

### Operations Access Methods

These methods return specialized interfaces for domain-specific operations:

#### 3. `networks()`

```java
public NetworkOperations networks() {
    return networks;
}
```

**Returns**: `NetworkOperations` interface for managing networks and operators

**Available Operations**:
- Create, update, delete networks
- Create operators and associate them with networks
- Generate network reports

---

#### 4. `gateways()`

```java
public GatewayOperations gateways() {
    return gateways;
}
```

**Returns**: `GatewayOperations` interface for managing gateways and parameters

**Available Operations**:
- Create, update, delete gateways
- Manage gateway parameters (EXPECTED_MEAN, EXPECTED_STD_DEV, BATTERY_CHARGE)
- Generate gateway reports

---

#### 5. `sensors()`

```java
public SensorOperations sensors() {
    return sensors;
}
```

**Returns**: `SensorOperations` interface for managing sensors and thresholds

**Available Operations**:
- Create, update, delete sensors
- Configure thresholds for anomaly detection
- Generate sensor reports with statistical analysis

---

#### 6. `topology()`

```java
public TopologyOperations topology() {
    return topology;
}
```

**Returns**: `TopologyOperations` interface for managing entity relationships

**Available Operations**:
- Connect/disconnect gateways to/from networks
- Connect/disconnect sensors to/from gateways
- Query topology relationships

---

## Operations Interfaces

### NetworkOperations (R1)

| Method | Description |
|--------|-------------|
| `createNetwork(code, name, description, username)` | Creates a new network |
| `updateNetwork(code, name, description, username)` | Updates network details |
| `deleteNetwork(code, username)` | Deletes a network |
| `getNetworks(codes...)` | Retrieves networks by code |
| `createOperator(firstName, lastName, email, phone, username)` | Creates an operator |
| `addOperatorToNetwork(networkCode, email, username)` | Associates operator to network |
| `getNetworkReport(code, startDate, endDate)` | Generates network report |

### GatewayOperations (R2)

| Method | Description |
|--------|-------------|
| `createGateway(code, name, description, username)` | Creates a new gateway |
| `updateGateway(code, name, description, username)` | Updates gateway details |
| `deleteGateway(code, username)` | Deletes a gateway |
| `getGateways(codes...)` | Retrieves gateways by code |
| `createParameter(gatewayCode, code, name, desc, value, username)` | Creates a parameter |
| `updateParameter(gatewayCode, code, value, username)` | Updates parameter value |
| `getGatewayReport(code, startDate, endDate)` | Generates gateway report |

### SensorOperations (R3)

| Method | Description |
|--------|-------------|
| `createSensor(code, name, description, username)` | Creates a new sensor |
| `updateSensor(code, name, description, username)` | Updates sensor details |
| `deleteSensor(code, username)` | Deletes a sensor |
| `getSensors(codes...)` | Retrieves sensors by code |
| `createThreshold(sensorCode, type, value, username)` | Creates a threshold |
| `updateThreshold(sensorCode, type, value, username)` | Updates threshold |
| `getSensorReport(code, startDate, endDate)` | Generates sensor report |

### TopologyOperations (R4)

| Method | Description |
|--------|-------------|
| `getNetworkGateways(networkCode)` | Gets gateways in a network |
| `connectGateway(networkCode, gatewayCode, username)` | Links gateway to network |
| `disconnectGateway(networkCode, gatewayCode, username)` | Unlinks gateway from network |
| `getGatewaySensors(gatewayCode)` | Gets sensors in a gateway |
| `connectSensor(sensorCode, gatewayCode, username)` | Links sensor to gateway |
| `disconnectSensor(sensorCode, gatewayCode, username)` | Unlinks sensor from gateway |

---

## Usage Examples

### Basic System Setup

```java
// Create the façade instance
WeatherReport wr = new WeatherReport();

// Create a maintainer user
User admin = wr.createUser("admin", UserType.MAINTAINER);

// Create a viewer user
User viewer = wr.createUser("analyst", UserType.VIEWER);
```

### Managing Networks

```java
WeatherReport wr = new WeatherReport();

// Create a network
Network network = wr.networks().createNetwork(
    "NET_01",           // code (format: NET_XX)
    "Main Network",     // name (optional)
    "Primary monitoring network",  // description (optional)
    "admin"             // username (must be MAINTAINER)
);

// Create an operator
Operator operator = wr.networks().createOperator(
    "John",             // first name
    "Doe",              // last name
    "john@example.com", // email (unique identifier)
    "+1234567890",      // phone (optional)
    "admin"
);

// Associate operator to network
wr.networks().addOperatorToNetwork("NET_01", "john@example.com", "admin");
```

### Managing Gateways and Sensors

```java
// Create a gateway
Gateway gateway = wr.gateways().createGateway(
    "GW_0001",          // code (format: GW_XXXX)
    "Weather Station",
    "Main weather station",
    "admin"
);

// Add parameters to gateway
wr.gateways().createParameter("GW_0001", "EXPECTED_MEAN", null, null, 25.0, "admin");
wr.gateways().createParameter("GW_0001", "EXPECTED_STD_DEV", null, null, 5.0, "admin");
wr.gateways().createParameter("GW_0001", "BATTERY_CHARGE", null, null, 85.0, "admin");

// Create a sensor
Sensor sensor = wr.sensors().createSensor(
    "S_000001",         // code (format: S_XXXXXX)
    "Temperature Sensor",
    "Outdoor temperature",
    "admin"
);

// Set threshold for anomaly detection
wr.sensors().createThreshold("S_000001", ThresholdType.GREATER_THAN, 40.0, "admin");
```

### Setting Up Topology

```java
// Connect gateway to network
wr.topology().connectGateway("NET_01", "GW_0001", "admin");

// Connect sensor to gateway
wr.topology().connectSensor("S_000001", "GW_0001", "admin");

// Query topology
Collection<Gateway> networkGateways = wr.topology().getNetworkGateways("NET_01");
Collection<Sensor> gatewaySensors = wr.topology().getGatewaySensors("GW_0001");
```

### Importing Data and Generating Reports

```java
// Import measurements from CSV
wr.importDataFromFile("src/main/resources/csv/measurements.csv");

// Generate reports
NetworkReport netReport = wr.networks().getNetworkReport(
    "NET_01",
    "2024-01-01 00:00:00",  // startDate (optional)
    "2024-12-31 23:59:59"   // endDate (optional)
);

GatewayReport gwReport = wr.gateways().getGatewayReport("GW_0001", null, null);

SensorReport sensorReport = wr.sensors().getSensorReport("S_000001", null, null);
```

---

## Architecture Diagram

```
                    ┌──────────────────────┐
                    │    External Code     │
                    │   (Client Classes)   │
                    └──────────┬───────────┘
                               │
                               ▼
            ┌──────────────────────────────────────┐
            │           WeatherReport              │
            │            (FAÇADE)                  │
            │                                      │
            │  ┌────────────────────────────────┐  │
            │  │ Utility Methods:               │  │
            │  │  • importDataFromFile()        │  │
            │  │  • createUser()                │  │
            │  └────────────────────────────────┘  │
            │                                      │
            │  ┌────────────────────────────────┐  │
            │  │ Access Methods:                │  │
            │  │  • networks()                  │  │
            │  │  • gateways()                  │  │
            │  │  • sensors()                   │  │
            │  │  • topology()                  │  │
            │  └────────────────────────────────┘  │
            └──────────────────┬───────────────────┘
                               │
         ┌─────────────────────┼─────────────────────┐
         │                     │                     │
         ▼                     ▼                     ▼
┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
│OperationsFactory│  │DataImportingService│ │CRUDRepository   │
│                 │  │                 │  │                 │
│ Creates:        │  │ Handles:        │  │ Handles:        │
│ • NetworkOps    │  │ • CSV import    │  │ • User CRUD     │
│ • GatewayOps    │  │ • Measurements  │  │                 │
│ • SensorOps     │  │ • Threshold     │  │                 │
│ • TopologyOps   │  │   checks        │  │                 │
└────────┬────────┘  └─────────────────┘  └─────────────────┘
         │
         ▼
┌────────────────────────────────────────────────────────────┐
│                   Operations Interfaces                     │
├─────────────┬─────────────┬─────────────┬─────────────────┤
│ Network     │ Gateway     │ Sensor      │ Topology        │
│ Operations  │ Operations  │ Operations  │ Operations      │
│ (R1)        │ (R2)        │ (R3)        │ (R4)            │
└─────────────┴─────────────┴─────────────┴─────────────────┘
         │
         ▼
┌────────────────────────────────────────────────────────────┐
│                    Repository Layer                         │
│              (CRUDRepository, MeasurementRepository)        │
└────────────────────────────────────────────────────────────┘
         │
         ▼
┌────────────────────────────────────────────────────────────┐
│                  Persistence Layer                          │
│               (Hibernate JPA + H2 Database)                 │
└────────────────────────────────────────────────────────────┘
```

---

## Key Design Decisions

### 1. Separation of Concerns

The façade separates:
- **Common operations** (user creation, data import) - directly in the façade
- **Domain-specific operations** - delegated to specialized interfaces

### 2. Factory Pattern Integration

```java
private final NetworkOperations networks = OperationsFactory.getNetworkOperations();
```

Operations are obtained through `OperationsFactory`, allowing:
- Easy replacement of implementations
- Centralized configuration
- Support for testing with mocks

### 3. Immutable References

Operations interfaces are stored as `final` fields, ensuring:
- Thread-safety (reference cannot change)
- Consistent behavior throughout object lifecycle

### 4. Static Constants

```java
public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
```

System-wide constants are defined in the façade for:
- Consistency across all components
- Single point of configuration
- Easy access from any part of the system

---

## Summary

The `WeatherReport` façade provides a clean, organized entry point to the Weather Report system by:

1. **Exposing utility methods** for common tasks (user creation, data import)
2. **Delegating domain operations** to specialized interfaces
3. **Hiding complexity** of the underlying subsystems
4. **Providing a unified API** for external clients

This design ensures that clients can interact with the entire system through a single class while maintaining clear separation of concerns internally.