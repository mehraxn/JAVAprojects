# Understanding NetworkOperationsImpl

## Complete Guide to Network and Operator Management in the Weather Report System

---

## Table of Contents

1. [Overview](#1-overview)
2. [Class Structure](#2-class-structure)
3. [Dependencies and Repositories](#3-dependencies-and-repositories)
4. [Helper Methods](#4-helper-methods)
   - [validateUserIsMaintainer()](#validateuserismaintainer)
   - [isValidNetworkCode()](#isvalidnetworkcode)
   - [isOperatorInList()](#isoperatorinlist)
5. [CRUD Operations for Network](#5-crud-operations-for-network)
   - [createNetwork()](#createnetwork)
   - [updateNetwork()](#updatenetwork)
   - [deleteNetwork()](#deletenetwork)
   - [getNetworks()](#getnetworks)
6. [Operator Operations](#6-operator-operations)
   - [createOperator()](#createoperator)
   - [addOperatorToNetwork()](#addoperatortonetwork)
7. [Report Generation](#7-report-generation)
   - [getNetworkReport()](#getnetworkreport)
8. [Exception Handling Summary](#8-exception-handling-summary)
9. [Visual Diagrams](#9-visual-diagrams)
10. [Common Questions](#10-common-questions)

---

## 1. Overview

### What is NetworkOperationsImpl?

`NetworkOperationsImpl` is the **concrete implementation** of the `NetworkOperations` interface. It provides all the functionality for:

- **Managing Networks**: Create, Read, Update, Delete (CRUD)
- **Managing Operators**: Create operators and assign them to networks
- **Generating Reports**: Produce network-level reports with statistics

### Where Does This Requirement Come From?

From the project README (R1 - Network requirement):

> *"The concrete implementation of NetworkOperations must: create, update and delete Network and Operator entities; use the metadata inherited from Timestamped to record creation and modification information of networks; notify the deletion of a Network through the call AlertingService.notifyDeletion(...)"*

### Class Declaration

```java
public class NetworkOperationsImpl implements NetworkOperations {
```

This class **implements** the `NetworkOperations` interface, meaning it must provide concrete implementations for all methods defined in that interface.

---

## 2. Class Structure

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        NetworkOperationsImpl                            │
├─────────────────────────────────────────────────────────────────────────┤
│  FIELDS (Repositories)                                                  │
│  ├── networkRepository    : CRUDRepository<Network, String>             │
│  ├── operatorRepository   : CRUDRepository<Operator, String>            │
│  └── userRepository       : CRUDRepository<User, String>                │
├─────────────────────────────────────────────────────────────────────────┤
│  PRIVATE HELPER METHODS                                                 │
│  ├── validateUserIsMaintainer(username)                                 │
│  ├── isValidNetworkCode(code)                                           │
│  └── isOperatorInList(operator, list)                                   │
├─────────────────────────────────────────────────────────────────────────┤
│  PUBLIC METHODS (from NetworkOperations interface)                      │
│  ├── createNetwork(code, name, description, username)                   │
│  ├── updateNetwork(code, name, description, username)                   │
│  ├── deleteNetwork(networkCode, username)                               │
│  ├── getNetworks(codes...)                                              │
│  ├── createOperator(firstName, lastName, email, phoneNumber, username)  │
│  ├── addOperatorToNetwork(networkCode, operatorEmail, username)         │
│  └── getNetworkReport(networkCode, startDate, endDate)                  │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 3. Dependencies and Repositories

```java
private final CRUDRepository<Network, String> networkRepository = new CRUDRepository<>(Network.class);
private final CRUDRepository<Operator, String> operatorRepository = new CRUDRepository<>(Operator.class);
private final CRUDRepository<User, String> userRepository = new CRUDRepository<>(User.class);
```

### Line-by-Line Explanation

| Line | Repository | Entity Type | ID Type | Purpose |
|------|------------|-------------|---------|---------|
| 1 | `networkRepository` | `Network` | `String` (code) | Store/retrieve networks |
| 2 | `operatorRepository` | `Operator` | `String` (email) | Store/retrieve operators |
| 3 | `userRepository` | `User` | `String` (username) | Check user permissions |

### Why Three Repositories?

```
┌─────────────────────────────────────────────────────────────────────────┐
│  DATA ACCESS PATTERN                                                    │
│                                                                         │
│  NetworkOperationsImpl                                                  │
│         │                                                               │
│         ├──► networkRepository ──► Database (Network table)             │
│         │                                                               │
│         ├──► operatorRepository ──► Database (Operator table)           │
│         │                                                               │
│         └──► userRepository ──► Database (User table)                   │
│                                                                         │
│  Each repository handles ONE entity type.                               │
│  This follows the Single Responsibility Principle (SRP).                │
└─────────────────────────────────────────────────────────────────────────┘
```

### Why `final`?

```java
private final CRUDRepository<Network, String> networkRepository = ...
//      ↑
//      final means the reference cannot be changed after initialization
```

**Benefits:**
- Thread safety (reference is immutable)
- Prevents accidental reassignment
- Signals intent: "this repository instance should never change"

---

## 4. Helper Methods

### validateUserIsMaintainer()

```java
private void validateUserIsMaintainer(String username) throws UnauthorizedException {
    if (username == null) {
        throw new UnauthorizedException("Username is null");
    }
    
    User user = userRepository.read(username);
    
    if (user == null) {
        throw new UnauthorizedException("User " + username + " is not authorized.");
    }
    
    if (user.getType() != UserType.MAINTAINER) {
        throw new UnauthorizedException("User " + username + " is not authorized.");
    }
}
```

#### Purpose

This method enforces the **authorization rule** from the project requirements:

> *"A MAINTAINER user can perform both read and write operations (creation, update and deletion of entities and configurations)."*

#### Step-by-Step Breakdown

```
┌─────────────────────────────────────────────────────────────────────────┐
│  STEP 1: Check if username is null                                      │
│                                                                         │
│  if (username == null) {                                                │
│      throw new UnauthorizedException("Username is null");               │
│  }                                                                      │
│                                                                         │
│  Why? Can't look up a user without a username.                          │
└─────────────────────────────────────────────────────────────────────────┘
                                    ↓
┌─────────────────────────────────────────────────────────────────────────┐
│  STEP 2: Look up the user in the database                               │
│                                                                         │
│  User user = userRepository.read(username);                             │
│                                                                         │
│  This queries the database for a User with this username.               │
│  Returns null if not found.                                             │
└─────────────────────────────────────────────────────────────────────────┘
                                    ↓
┌─────────────────────────────────────────────────────────────────────────┐
│  STEP 3: Check if user exists                                           │
│                                                                         │
│  if (user == null) {                                                    │
│      throw new UnauthorizedException("User ... is not authorized.");    │
│  }                                                                      │
│                                                                         │
│  Why? Unknown users cannot perform operations.                          │
└─────────────────────────────────────────────────────────────────────────┘
                                    ↓
┌─────────────────────────────────────────────────────────────────────────┐
│  STEP 4: Check if user is a MAINTAINER                                  │
│                                                                         │
│  if (user.getType() != UserType.MAINTAINER) {                           │
│      throw new UnauthorizedException("User ... is not authorized.");    │
│  }                                                                      │
│                                                                         │
│  Why? Only MAINTAINERs can create/update/delete.                        │
│  VIEWERs can only read data.                                            │
└─────────────────────────────────────────────────────────────────────────┘
```

#### Visual Flow

```
validateUserIsMaintainer("john_doe")
         │
         ▼
   username == null? ──YES──► throw UnauthorizedException
         │
         NO
         ▼
   user = userRepository.read("john_doe")
         │
         ▼
   user == null? ──YES──► throw UnauthorizedException
         │                (user doesn't exist)
         NO
         ▼
   user.getType() != MAINTAINER? ──YES──► throw UnauthorizedException
         │                               (user is VIEWER)
         NO
         ▼
   ✓ Validation passed (method returns normally)
```

---

### isValidNetworkCode()

```java
private boolean isValidNetworkCode(String code) {
    if (code == null || code.length() != 6 || !code.startsWith("NET_")) {
        return false;
    }
    return Character.isDigit(code.charAt(4)) && Character.isDigit(code.charAt(5));
}
```

#### Where Does This Format Come From?

From the project README:

> *"The code of a network must be a string that starts with "NET_" and is followed by two decimal digits."*

#### Format Specification

```
Valid Network Code: NET_XX
                    │││││└─ Position 5: Must be a digit (0-9)
                    ││││└── Position 4: Must be a digit (0-9)
                    │││└─── Position 3: '_'
                    ││└──── Position 2: 'T'
                    │└───── Position 1: 'E'
                    └────── Position 0: 'N'

Total length: 6 characters
```

#### Step-by-Step Validation

```java
// STEP 1: Quick rejection checks (fail fast)
if (code == null || code.length() != 6 || !code.startsWith("NET_")) {
    return false;
}
```

| Check | Purpose | Example Failures |
|-------|---------|------------------|
| `code == null` | Prevent NullPointerException | `null` |
| `code.length() != 6` | Correct length | `"NET_1"`, `"NET_123"` |
| `!code.startsWith("NET_")` | Correct prefix | `"NTW_01"`, `"net_01"` |

```java
// STEP 2: Check that positions 4 and 5 are digits
return Character.isDigit(code.charAt(4)) && Character.isDigit(code.charAt(5));
```

| Position | Expected | `Character.isDigit()` |
|----------|----------|----------------------|
| `charAt(4)` | `'0'` to `'9'` | Returns `true` if digit |
| `charAt(5)` | `'0'` to `'9'` | Returns `true` if digit |

#### Examples

| Input | Result | Reason |
|-------|--------|--------|
| `"NET_01"` | ✅ `true` | Valid format |
| `"NET_99"` | ✅ `true` | Valid format |
| `"NET_00"` | ✅ `true` | Valid format |
| `null` | ❌ `false` | Null input |
| `"NET_1"` | ❌ `false` | Too short (5 chars) |
| `"NET_123"` | ❌ `false` | Too long (7 chars) |
| `"NET_AB"` | ❌ `false` | Letters instead of digits |
| `"net_01"` | ❌ `false` | Lowercase (doesn't start with "NET_") |
| `"NTW_01"` | ❌ `false` | Wrong prefix |

---

### isOperatorInList()

```java
private boolean isOperatorInList(Operator operatorToFind, Collection<Operator> operatorList) {
    String emailToFind = operatorToFind.getEmail();
    
    for (Operator operator : operatorList) {
        String currentEmail = operator.getEmail();
        
        if (currentEmail.equals(emailToFind)) {
            return true;
        }
    }
    
    return false;
}
```

#### Purpose

Checks if an operator is already in a collection by comparing **email addresses** (the unique identifier for operators).

#### Why Compare by Email?

From the project README:

> *"An operator is uniquely identified by its email address."*

#### Step-by-Step

```
┌─────────────────────────────────────────────────────────────────────────┐
│  STEP 1: Extract the email we're looking for                            │
│                                                                         │
│  String emailToFind = operatorToFind.getEmail();                        │
│                                                                         │
│  Example: emailToFind = "alice@example.com"                             │
└─────────────────────────────────────────────────────────────────────────┘
                                    ↓
┌─────────────────────────────────────────────────────────────────────────┐
│  STEP 2: Loop through all operators in the list                         │
│                                                                         │
│  for (Operator operator : operatorList) {                               │
│      String currentEmail = operator.getEmail();                         │
│                                                                         │
│      if (currentEmail.equals(emailToFind)) {                            │
│          return true;  // Found it!                                     │
│      }                                                                  │
│  }                                                                      │
└─────────────────────────────────────────────────────────────────────────┘
                                    ↓
┌─────────────────────────────────────────────────────────────────────────┐
│  STEP 3: If we exit the loop, operator was not found                    │
│                                                                         │
│  return false;                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

#### Visual Example

```
operatorToFind: { email: "alice@example.com", ... }

operatorList: [
    { email: "bob@example.com", ... },      ← "bob@..." ≠ "alice@..." → continue
    { email: "charlie@example.com", ... },  ← "charlie@..." ≠ "alice@..." → continue
    { email: "alice@example.com", ... }     ← "alice@..." = "alice@..." → return true!
]

Result: true (operator found)
```

---

## 5. CRUD Operations for Network

### createNetwork()

```java
@Override
public Network createNetwork(String code, String name, String description, String username)
        throws IdAlreadyInUseException, InvalidInputDataException, UnauthorizedException {
    
    validateUserIsMaintainer(username);
    
    if (code == null || code.isEmpty()) {
        throw new InvalidInputDataException("Network code is missing.");
    }
    
    boolean codeMatchesFormat = isValidNetworkCode(code);
    if (!codeMatchesFormat) {
        throw new InvalidInputDataException("Invalid network code format.");
    }
    
    Network existingNetwork = networkRepository.read(code);
    if (existingNetwork != null) {
        throw new IdAlreadyInUseException("Network code already in use.");
    }
    
    Network newNetwork = new Network(code, name, description);
    
    LocalDateTime currentTime = LocalDateTime.now();
    newNetwork.setCreatedBy(username);
    newNetwork.setCreatedAt(currentTime);
    newNetwork.setModifiedBy(username);
    newNetwork.setModifiedAt(currentTime);
    
    Network createdNetwork = networkRepository.create(newNetwork);
    return createdNetwork;
}
```

#### Method Signature Breakdown

```java
public Network createNetwork(
    String code,           // Mandatory - unique identifier (e.g., "NET_01")
    String name,           // Optional - human-readable name
    String description,    // Optional - description text
    String username        // Mandatory - who is performing the action
)
throws IdAlreadyInUseException,    // If code already exists
       InvalidInputDataException,   // If code is invalid
       UnauthorizedException        // If user can't perform this action
```

#### Step-by-Step Flow

```
createNetwork("NET_01", "Main Network", "Description", "admin")
         │
         ▼
┌─────────────────────────────────────────────────────────────────────────┐
│  STEP 1: Authorization Check                                            │
│                                                                         │
│  validateUserIsMaintainer(username);                                    │
│                                                                         │
│  Ensures "admin" exists and is a MAINTAINER.                            │
│  Throws UnauthorizedException if not.                                   │
└─────────────────────────────────────────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────────────────────────────────────┐
│  STEP 2: Validate Code is Present                                       │
│                                                                         │
│  if (code == null || code.isEmpty()) {                                  │
│      throw new InvalidInputDataException("Network code is missing.");   │
│  }                                                                      │
│                                                                         │
│  Code is mandatory - can't create network without it.                   │
└─────────────────────────────────────────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────────────────────────────────────┐
│  STEP 3: Validate Code Format                                           │
│                                                                         │
│  boolean codeMatchesFormat = isValidNetworkCode(code);                  │
│  if (!codeMatchesFormat) {                                              │
│      throw new InvalidInputDataException("Invalid network code...");    │
│  }                                                                      │
│                                                                         │
│  Must match pattern: NET_XX (where X is a digit)                        │
└─────────────────────────────────────────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────────────────────────────────────┐
│  STEP 4: Check for Duplicate                                            │
│                                                                         │
│  Network existingNetwork = networkRepository.read(code);                │
│  if (existingNetwork != null) {                                         │
│      throw new IdAlreadyInUseException("Network code already in use."); │
│  }                                                                      │
│                                                                         │
│  Network codes must be unique in the system.                            │
└─────────────────────────────────────────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────────────────────────────────────┐
│  STEP 5: Create Network Object                                          │
│                                                                         │
│  Network newNetwork = new Network(code, name, description);             │
│                                                                         │
│  Instantiate the entity with provided values.                           │
└─────────────────────────────────────────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────────────────────────────────────┐
│  STEP 6: Set Timestamped Metadata                                       │
│                                                                         │
│  LocalDateTime currentTime = LocalDateTime.now();                       │
│  newNetwork.setCreatedBy(username);    // Who created it                │
│  newNetwork.setCreatedAt(currentTime); // When it was created           │
│  newNetwork.setModifiedBy(username);   // Who last modified (same)      │
│  newNetwork.setModifiedAt(currentTime);// When last modified (same)     │
│                                                                         │
│  Required by: "The system records who created or modified a network,    │
│  a gateway or a sensor, and when the operation was performed."          │
└─────────────────────────────────────────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────────────────────────────────────┐
│  STEP 7: Persist and Return                                             │
│                                                                         │
│  Network createdNetwork = networkRepository.create(newNetwork);         │
│  return createdNetwork;                                                 │
│                                                                         │
│  Save to database and return the saved entity.                          │
└─────────────────────────────────────────────────────────────────────────┘
```

#### Why Set ModifiedBy/ModifiedAt on Creation?

When a network is first created:
- `createdBy` = `modifiedBy` (same person)
- `createdAt` = `modifiedAt` (same time)

This makes sense because creation IS the first modification.

---

### updateNetwork()

```java
@Override
public Network updateNetwork(String code, String name, String description, String username)
        throws InvalidInputDataException, ElementNotFoundException, UnauthorizedException {
    
    validateUserIsMaintainer(username);
    
    if (code == null) {
        throw new InvalidInputDataException("Code is null");
    }
    
    Network existingNetwork = networkRepository.read(code);
    
    if (existingNetwork == null) {
        throw new ElementNotFoundException("Network not found.");
    }
    
    existingNetwork.setName(name);
    existingNetwork.setDescription(description);
    
    LocalDateTime currentTime = LocalDateTime.now();
    existingNetwork.setModifiedBy(username);
    existingNetwork.setModifiedAt(currentTime);
    
    Network updatedNetwork = networkRepository.update(existingNetwork);
    return updatedNetwork;
}
```

#### Key Differences from createNetwork()

| Aspect | createNetwork() | updateNetwork() |
|--------|-----------------|-----------------|
| Code validation | Checks format | Only checks not null |
| Duplicate check | Must NOT exist | Must exist |
| Exception if exists | `IdAlreadyInUseException` | - |
| Exception if not exists | - | `ElementNotFoundException` |
| Timestamped fields | Sets created + modified | Only updates modified |

#### Why No Format Validation for Code?

```java
// In updateNetwork:
if (code == null) {
    throw new InvalidInputDataException("Code is null");
}

// No isValidNetworkCode() check!
```

**Reason:** If a network with this code exists in the database, it must have passed format validation when it was created. Re-validating is unnecessary.

#### Step-by-Step Flow

```
updateNetwork("NET_01", "New Name", "New Description", "admin")
         │
         ▼
   validateUserIsMaintainer("admin")
         │
         ▼
   code == null? ──YES──► throw InvalidInputDataException
         │
         NO
         ▼
   existingNetwork = networkRepository.read("NET_01")
         │
         ▼
   existingNetwork == null? ──YES──► throw ElementNotFoundException
         │
         NO
         ▼
   existingNetwork.setName("New Name")
   existingNetwork.setDescription("New Description")
         │
         ▼
   existingNetwork.setModifiedBy("admin")
   existingNetwork.setModifiedAt(now)
         │
         ▼
   networkRepository.update(existingNetwork)
         │
         ▼
   return updatedNetwork
```

---

### deleteNetwork()

```java
@Override
public Network deleteNetwork(String networkCode, String username)
        throws InvalidInputDataException, ElementNotFoundException, UnauthorizedException {
    
    validateUserIsMaintainer(username);
    
    Network existingNetwork = networkRepository.read(networkCode);
    
    if (existingNetwork == null) {
        throw new ElementNotFoundException("Network not found.");
    }
    
    networkRepository.delete(networkCode);
    
    AlertingService.notifyDeletion(username, networkCode, Network.class);
    
    return existingNetwork;
}
```

#### Special Requirement: Notification

From the project README:

> *"Deletions of Network, Gateway and Sensor must be notified through the mechanism provided by the system."*

This is why we call:
```java
AlertingService.notifyDeletion(username, networkCode, Network.class);
```

---

#### ⚠️ IMPORTANT: What Happens to Gateways and Sensors When a Network is Deleted?

**Answer: NOTHING! Only the Network is deleted. Gateways and Sensors are NOT deleted.**

##### The Code Evidence

Looking at the `deleteNetwork()` method:
```java
networkRepository.delete(networkCode);  // ← Only deletes the Network!
// There is NO:
// - gatewayRepository.delete(...)
// - sensorRepository.delete(...)
// - cascade deletion logic
```

##### Why? The Project Requirements Explain This

From the project README:

> *"Creation, modification and deletion of networks, gateways, sensors and their associated elements... These operations concern the **existence of objects in the system, independently of any hierarchical relationships**."*

And:

> *"Association and disassociation between elements (for example linking a gateway to a network, or a sensor to a gateway). **Association operations belong to topology management and are distinct from the operations that deal with object creation.**"*

##### What This Means

The system separates two concepts:

| Concept | Operations | Responsibility |
|---------|------------|----------------|
| **Entity Existence** | Create, Update, Delete | `NetworkOperations`, `GatewayOperations`, `SensorOperations` |
| **Relationships (Topology)** | Connect, Disconnect | `TopologyOperations` |

**These are INDEPENDENT!** Deleting an entity does NOT automatically affect related entities.

##### Visual Example

```
BEFORE deleteNetwork("NET_01"):
┌─────────────────────────────────────────────────────────────────────────┐
│                              NET_01                                     │
│                                │                                        │
│              ┌─────────────────┼─────────────────┐                      │
│              │                 │                 │                      │
│              ▼                 ▼                 ▼                      │
│           GW_0001           GW_0002           GW_0003                   │
│              │                 │                 │                      │
│         ┌────┴────┐       ┌────┴────┐       ┌────┴────┐                │
│         ▼         ▼       ▼         ▼       ▼         ▼                │
│     S_000001  S_000002  S_000003  S_000004  S_000005  S_000006          │
└─────────────────────────────────────────────────────────────────────────┘

AFTER deleteNetwork("NET_01"):
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                         │
│                           [NET_01 DELETED]                              │
│                                                                         │
│           GW_0001           GW_0002           GW_0003  ← Still exist!   │
│              │                 │                 │       (ORPHANED)     │
│         ┌────┴────┐       ┌────┴────┐       ┌────┴────┐                │
│         ▼         ▼       ▼         ▼       ▼         ▼                │
│     S_000001  S_000002  S_000003  S_000004  S_000005  S_000006          │
│                                                        ↑                │
│                                               Still exist! (ORPHANED)   │
└─────────────────────────────────────────────────────────────────────────┘
```

##### What Are "Orphaned" Entities?

After deleting a network:
- **Gateways** still exist in the database, but have no parent network
- **Sensors** still exist in the database, still connected to their gateways
- These entities can be:
  - Connected to a different network later (via `TopologyOperations`)
  - Deleted individually (via `GatewayOperations.deleteGateway()` or `SensorOperations.deleteSensor()`)
  - Left as orphans (the system allows this)

##### Why Design It This Way?

| Design Choice | Benefit |
|---------------|---------|
| **No cascade delete** | Prevents accidental data loss |
| **Separation of concerns** | Entity lifecycle ≠ Relationship lifecycle |
| **Flexibility** | Gateways/Sensors can be reassigned to new networks |
| **Data preservation** | Historical data in orphaned sensors is preserved |

##### Comparison with Database CASCADE DELETE

```
┌─────────────────────────────────────────────────────────────────────────┐
│  DATABASE APPROACH                                                      │
│                                                                         │
│  With CASCADE DELETE:                                                   │
│  DELETE FROM networks WHERE code = 'NET_01'                             │
│  → Automatically deletes all related gateways                           │
│  → Automatically deletes all related sensors                            │
│  → All data is GONE!                                                    │
│                                                                         │
│  Without CASCADE DELETE (this system's approach):                       │
│  DELETE FROM networks WHERE code = 'NET_01'                             │
│  → Only network is deleted                                              │
│  → Gateways remain (with null/broken network reference)                 │
│  → Sensors remain (still connected to gateways)                         │
│  → Data is PRESERVED!                                                   │
└─────────────────────────────────────────────────────────────────────────┘
```

##### Summary Table

| When You Delete | What Gets Deleted | What Remains |
|-----------------|-------------------|--------------|
| **Network** | Only the Network | All Gateways, All Sensors, All Operators |
| **Gateway** | Only the Gateway | Network (if any), All Sensors |
| **Sensor** | Only the Sensor | Network (if any), Gateway (if any) |

---

#### Why Return the Deleted Network?

```java
return existingNetwork;  // Returns the network that was deleted
```

**Reasons:**
1. Allows caller to confirm what was deleted
2. Useful for logging/auditing
3. Allows undo functionality (if needed)

#### Step-by-Step Flow

```
deleteNetwork("NET_01", "admin")
         │
         ▼
   validateUserIsMaintainer("admin")
         │
         ▼
   existingNetwork = networkRepository.read("NET_01")
         │
         ▼
   existingNetwork == null? ──YES──► throw ElementNotFoundException
         │
         NO
         ▼
   networkRepository.delete("NET_01")  ← ONLY removes the Network from DB
         │                               (Gateways and Sensors remain!)
         ▼
   AlertingService.notifyDeletion("admin", "NET_01", Network.class)
         │                              ↑
         │              Logs: "User admin deleted NET_01 Network"
         ▼
   return existingNetwork  ← Returns the deleted network object
```

---

### getNetworks()

```java
@Override
public Collection<Network> getNetworks(String... codes) {
    boolean noCodesProvided = (codes == null || codes.length == 0);
    
    if (noCodesProvided) {
        List<Network> allNetworks = networkRepository.read();
        return allNetworks;
    }
    
    List<Network> foundNetworks = new ArrayList<>();
    
    for (String networkCode : codes) {
        Network network = networkRepository.read(networkCode);
        
        if (network != null) {
            foundNetworks.add(network);
        }
    }
    
    return foundNetworks;
}
```

#### Understanding Varargs (`String... codes`)

```java
public Collection<Network> getNetworks(String... codes)
//                                      ↑
//                                      This is "varargs" syntax
```

**Varargs allows:**
```java
getNetworks()                           // No arguments
getNetworks("NET_01")                   // One argument
getNetworks("NET_01", "NET_02")         // Two arguments
getNetworks("NET_01", "NET_02", "NET_03") // Three arguments
// etc.
```

Inside the method, `codes` behaves like a `String[]` array.

#### Behavior from Requirements

From the project README:

> *"If a code passed as input does not correspond to an element present in the system, it is simply ignored. If the method is invoked without any input parameters, it must return all Network elements present in the system."*

#### Two Modes of Operation

```
┌─────────────────────────────────────────────────────────────────────────┐
│  MODE 1: No codes provided → Return ALL networks                        │
│                                                                         │
│  getNetworks()  or  getNetworks(null)  or  getNetworks(new String[0])  │
│                                                                         │
│  boolean noCodesProvided = (codes == null || codes.length == 0);        │
│  if (noCodesProvided) {                                                 │
│      return networkRepository.read();  // Returns all networks          │
│  }                                                                      │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│  MODE 2: Codes provided → Return only matching networks                 │
│                                                                         │
│  getNetworks("NET_01", "NET_02", "NET_99")                              │
│                                                                         │
│  For each code:                                                         │
│    - Try to find network with that code                                 │
│    - If found, add to result list                                       │
│    - If not found, IGNORE (no exception!)                               │
│                                                                         │
│  Example:                                                               │
│    - "NET_01" exists → added                                            │
│    - "NET_02" exists → added                                            │
│    - "NET_99" doesn't exist → ignored                                   │
│    Result: [NET_01, NET_02]                                             │
└─────────────────────────────────────────────────────────────────────────┘
```

---

#### ❓ Why Return ALL Networks When No Codes Provided? Why Not Return Null?

##### Answer: It's Required by the Specification

From the project README:

> *"If the method is invoked without any input parameters, **it must return all Network elements** present in the system."*

This is an **explicit requirement**, not a design choice.

##### Why This Design Makes Sense

| Call | Interpretation | Result |
|------|----------------|--------|
| `getNetworks()` | "Give me networks" (no filter) | ALL networks |
| `getNetworks("NET_01")` | "Give me this specific one" | Only NET_01 |
| `getNetworks("NET_01", "NET_02")` | "Give me these specific ones" | NET_01 and NET_02 |

**Think of it like a search engine:**
- Search with filter → Returns filtered results
- Search without filter → Returns everything

##### Why NOT Return Null?

```
┌─────────────────────────────────────────────────────────────────────────┐
│  ❌ BAD: Return null                                                    │
│                                                                         │
│  Collection<Network> networks = getNetworks();                          │
│  for (Network n : networks) {  // ← NullPointerException!               │
│      System.out.println(n);                                             │
│  }                                                                      │
│                                                                         │
│  Problems:                                                              │
│  • Caller must ALWAYS check for null before using                       │
│  • Easy to forget null check → application crashes                      │
│  • null is AMBIGUOUS: does it mean "no results" or "error"?             │
│  • Violates "Null Object Pattern" best practice                         │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│  ✅ GOOD: Return all networks (Current Implementation)                  │
│                                                                         │
│  Collection<Network> networks = getNetworks();                          │
│  for (Network n : networks) {  // ← Works perfectly!                    │
│      System.out.println(n);    //   Even if empty, no crash             │
│  }                                                                      │
│                                                                         │
│  Benefits:                                                              │
│  • No null checks needed                                                │
│  • Consistent return type (always a Collection)                         │
│  • "No filter" = "give me everything" (intuitive API)                   │
│  • Follows Java best practices                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

##### Why NOT Return an Empty List?

Returning an empty list when no codes are provided would also be wrong:

```java
// ❌ WRONG interpretation:
if (noCodesProvided) {
    return new ArrayList<>();  // Empty list - but user wanted ALL networks!
}
```

This would be **semantically incorrect**:
- User calls `getNetworks()` expecting to see networks
- Getting an empty list suggests "there are no networks"
- But there ARE networks - the user just didn't specify which ones!

##### Real-World Analogy

| Action | Expected Result |
|--------|-----------------|
| "Show me files named report.pdf" | Only files named report.pdf |
| "Show me files" (no filter) | ALL files in the system |
| "Show me files" → returns nothing | ❌ Confusing! User expects to see files |
| "Show me files" → returns null | ❌ Even worse - application crashes! |

##### Summary: The Three Possible Designs

| Design | When No Codes Provided | Verdict |
|--------|------------------------|---------|
| Return `null` | Caller gets null | ❌ Causes NullPointerException |
| Return empty list | Caller gets `[]` | ❌ Semantically wrong (implies no data exists) |
| **Return all networks** | Caller gets all data | ✅ Correct! Matches requirements |

---

#### Why No Authorization Check?

Notice there's no `validateUserIsMaintainer()` call:

```java
public Collection<Network> getNetworks(String... codes) {
    // No validateUserIsMaintainer() here!
```

**Reason:** Reading networks is a **read operation**. Both VIEWERs and MAINTAINERs can read. Only write operations (create, update, delete) require MAINTAINER authorization.

---

## 6. Operator Operations

### createOperator()

```java
@Override
public Operator createOperator(String firstName, String lastName, String email, String phoneNumber, String username)
        throws InvalidInputDataException, IdAlreadyInUseException, UnauthorizedException {
    
    validateUserIsMaintainer(username);
    
    if (email == null || email.isEmpty()) {
        throw new InvalidInputDataException("Operator email is missing.");
    }
    
    if (firstName == null || lastName == null) {
        throw new InvalidInputDataException("Operator name is incomplete.");
    }
    
    Operator existingOperator = operatorRepository.read(email);
    if (existingOperator != null) {
        throw new IdAlreadyInUseException("Operator already exists.");
    }
    
    Operator newOperator = new Operator(email, firstName, lastName, phoneNumber);
    
    Operator createdOperator = operatorRepository.create(newOperator);
    return createdOperator;
}
```

#### Parameters

| Parameter | Required | Unique | Notes |
|-----------|----------|--------|-------|
| `firstName` | ✅ Yes | ❌ No | Must not be null |
| `lastName` | ✅ Yes | ❌ No | Must not be null |
| `email` | ✅ Yes | ✅ Yes | Unique identifier for operators |
| `phoneNumber` | ❌ No | ❌ No | Optional (can be null) |
| `username` | ✅ Yes | - | Must be a MAINTAINER |

#### Why No Timestamped Fields?

Notice we don't set `createdBy`, `createdAt`, etc.:

```java
Operator newOperator = new Operator(email, firstName, lastName, phoneNumber);
// No setCreatedBy(), setCreatedAt(), etc.
```

**Reason:** Looking at the class hierarchy:
- `Network extends Timestamped` ✅
- `Gateway extends Timestamped` ✅
- `Sensor extends Timestamped` ✅
- `Operator` does NOT extend `Timestamped` ❌

Operators don't have audit metadata in this system.

---

### addOperatorToNetwork()

```java
@Override
public Network addOperatorToNetwork(String networkCode, String operatorEmail, String username)
        throws ElementNotFoundException, InvalidInputDataException, UnauthorizedException {
    
    validateUserIsMaintainer(username);
    
    if (networkCode == null || operatorEmail == null) {
        throw new InvalidInputDataException("Null parameters");
    }
    
    Network network = networkRepository.read(networkCode);
    if (network == null) {
        throw new ElementNotFoundException("Network not found");
    }
    
    Operator operator = operatorRepository.read(operatorEmail);
    if (operator == null) {
        throw new ElementNotFoundException("Operator not found");
    }
    
    Collection<Operator> networkOperators = network.getOperators();
    if (networkOperators == null) {
        networkOperators = new ArrayList<>();
        network.setOperators(networkOperators);
    }
    
    boolean operatorAlreadyInNetwork = isOperatorInList(operator, networkOperators);
    
    if (!operatorAlreadyInNetwork) {
        networkOperators.add(operator);
        
        LocalDateTime currentTime = LocalDateTime.now();
        network.setModifiedBy(username);
        network.setModifiedAt(currentTime);
        
        networkRepository.update(network);
    }
    
    return network;
}
```

#### Purpose

Associates an **existing** operator with an **existing** network. Operators receive notifications when threshold violations occur in their networks.

#### Step-by-Step Flow

```
addOperatorToNetwork("NET_01", "alice@example.com", "admin")
         │
         ▼
   validateUserIsMaintainer("admin")
         │
         ▼
   networkCode == null OR operatorEmail == null?
         │
      YES ──► throw InvalidInputDataException
         │
         NO
         ▼
   network = networkRepository.read("NET_01")
         │
   network == null? ──YES──► throw ElementNotFoundException("Network not found")
         │
         NO
         ▼
   operator = operatorRepository.read("alice@example.com")
         │
   operator == null? ──YES──► throw ElementNotFoundException("Operator not found")
         │
         NO
         ▼
   networkOperators = network.getOperators()
         │
   networkOperators == null? ──YES──► Initialize new ArrayList
         │                            network.setOperators(new ArrayList())
         ▼
   isOperatorInList(operator, networkOperators)?
         │
      YES ──► Do nothing (operator already in network)
         │
         NO
         ▼
   networkOperators.add(operator)
   network.setModifiedBy("admin")
   network.setModifiedAt(now)
   networkRepository.update(network)
         │
         ▼
   return network
```

#### Why Initialize the Operators List?

```java
Collection<Operator> networkOperators = network.getOperators();
if (networkOperators == null) {
    networkOperators = new ArrayList<>();
    network.setOperators(networkOperators);
}
```

**Reason:** A newly created network might have a `null` operators collection. We need to initialize it before adding operators to avoid `NullPointerException`.

#### Why Check if Operator Already Exists?

```java
boolean operatorAlreadyInNetwork = isOperatorInList(operator, networkOperators);

if (!operatorAlreadyInNetwork) {
    // Only add if not already present
}
```

**Reason:** Prevents duplicates. Adding the same operator twice would be redundant and could cause issues.

---

## 7. Report Generation

### getNetworkReport()

```java
@Override
public NetworkReport getNetworkReport(String networkCode, String startDate, String endDate)
        throws InvalidInputDataException, ElementNotFoundException {
    
    if (networkCode == null) {
        throw new InvalidInputDataException("Network code null");
    }
    
    Network network = networkRepository.read(networkCode);
    
    if (network == null) {
        throw new ElementNotFoundException("Network " + networkCode + " not found.");
    }
    
    return new NetworkReportImpl(networkCode, startDate, endDate);
}
```

#### Parameters

| Parameter | Required | Format | Notes |
|-----------|----------|--------|-------|
| `networkCode` | ✅ Yes | `"NET_XX"` | Network to report on |
| `startDate` | ❌ No | `"yyyy-MM-dd HH:mm:ss"` | Lower bound (null = no lower bound) |
| `endDate` | ❌ No | `"yyyy-MM-dd HH:mm:ss"` | Upper bound (null = no upper bound) |

#### Why No Authorization Check?

```java
// No validateUserIsMaintainer() here!
```

**Reason:** Generating reports is a **read operation**. Both VIEWERs and MAINTAINERs can view reports.

#### The Report Implementation

The actual report logic is delegated to `NetworkReportImpl`:

```java
return new NetworkReportImpl(networkCode, startDate, endDate);
```

`NetworkReportImpl` handles:
- Querying measurements from the database
- Filtering by date range
- Calculating statistics (most active gateways, load ratios, etc.)
- Building the histogram

This follows the **Single Responsibility Principle**: `NetworkOperationsImpl` handles operations, `NetworkReportImpl` handles report generation.

---

## 8. Exception Handling Summary

### Exception Types and When They're Thrown

| Exception | When Thrown | Methods |
|-----------|-------------|---------|
| `UnauthorizedException` | User is null, doesn't exist, or isn't MAINTAINER | All write operations |
| `InvalidInputDataException` | Required data is null, empty, or wrong format | All methods with mandatory params |
| `IdAlreadyInUseException` | Trying to create with existing code/email | `createNetwork`, `createOperator` |
| `ElementNotFoundException` | Trying to access non-existent entity | `updateNetwork`, `deleteNetwork`, `addOperatorToNetwork`, `getNetworkReport` |

### Exception Flow Diagram

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         EXCEPTION DECISION TREE                         │
│                                                                         │
│  Is this a write operation (create/update/delete)?                      │
│      │                                                                  │
│      YES → Is user null or not MAINTAINER?                              │
│      │         │                                                        │
│      │         YES → throw UnauthorizedException                        │
│      │         │                                                        │
│      │         NO → Continue                                            │
│      │                                                                  │
│  Is required data null or invalid?                                      │
│      │                                                                  │
│      YES → throw InvalidInputDataException                              │
│      │                                                                  │
│      NO → Continue                                                      │
│                                                                         │
│  For CREATE: Does entity already exist?                                 │
│      │                                                                  │
│      YES → throw IdAlreadyInUseException                                │
│      │                                                                  │
│      NO → Continue                                                      │
│                                                                         │
│  For UPDATE/DELETE/other: Does entity exist?                            │
│      │                                                                  │
│      NO → throw ElementNotFoundException                                │
│      │                                                                  │
│      YES → Proceed with operation                                       │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 9. Visual Diagrams

### Complete Class Interaction Diagram

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           External Caller                               │
│                     (e.g., WeatherReport facade)                        │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                        NetworkOperationsImpl                            │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                      Public Methods                              │   │
│  │  createNetwork() updateNetwork() deleteNetwork() getNetworks()   │   │
│  │  createOperator() addOperatorToNetwork() getNetworkReport()      │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                          │           │           │                      │
│                          ▼           ▼           ▼                      │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐                  │
│  │ network      │  │ operator     │  │ user         │                  │
│  │ Repository   │  │ Repository   │  │ Repository   │                  │
│  └──────────────┘  └──────────────┘  └──────────────┘                  │
└─────────────────────────────────────────────────────────────────────────┘
         │                    │                    │
         ▼                    ▼                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                            DATABASE                                     │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐                  │
│  │   Network    │  │   Operator   │  │    User      │                  │
│  │    Table     │  │    Table     │  │    Table     │                  │
│  └──────────────┘  └──────────────┘  └──────────────┘                  │
└─────────────────────────────────────────────────────────────────────────┘
```

### Network Lifecycle

```
                        createNetwork()
                              │
                              ▼
                    ┌─────────────────┐
                    │     CREATED     │
                    │                 │
                    │ createdBy: user │
                    │ createdAt: now  │
                    │ modifiedBy: user│
                    │ modifiedAt: now │
                    └─────────────────┘
                              │
            ┌─────────────────┼─────────────────┐
            │                 │                 │
            ▼                 ▼                 ▼
    updateNetwork()   addOperatorToNetwork()  deleteNetwork()
            │                 │                 │
            ▼                 ▼                 ▼
    ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
    │     UPDATED     │  │     UPDATED     │  │     DELETED     │
    │                 │  │                 │  │                 │
    │ modifiedBy: new │  │ modifiedBy: new │  │ AlertingService │
    │ modifiedAt: now │  │ modifiedAt: now │  │ .notifyDeletion │
    └─────────────────┘  └─────────────────┘  └─────────────────┘
```

---

## 10. Common Questions

### Q1: Why does `getNetworks()` return ALL networks when no codes are provided, instead of null?

**Two reasons:**

1. **It's required by the specification:**
   > *"If the method is invoked without any input parameters, it must return all Network elements present in the system."*

2. **It's good API design:**
   - Returning `null` would cause `NullPointerException` if caller forgets to check
   - Returning empty list would be semantically wrong (implies no data exists)
   - "No filter" = "give me everything" is intuitive behavior

See the [getNetworks() section](#getnetworks) for a detailed explanation.

---

### Q2: Why does `getNetworks()` not throw exceptions for invalid codes?

From the requirements:
> *"If a code passed as input does not correspond to an element present in the system, it is simply ignored."*

This is by design - invalid codes are silently skipped.

---

### Q3: Why is there no `removeOperatorFromNetwork()` method?

Looking at the `NetworkOperations` interface, this method is not defined. The requirements only specify adding operators to networks, not removing them.

---

### Q4: Why doesn't `createOperator()` validate email format?

The code only checks:
```java
if (email == null || email.isEmpty()) {
    throw new InvalidInputDataException("Operator email is missing.");
}
```

There's no regex validation for email format (like `xxx@xxx.xxx`). The requirements don't specify email format validation, only that email is mandatory and unique.

---

### Q5: Can the same operator belong to multiple networks?

Yes! From the requirements:
> *"The same operator may be responsible for multiple networks."*

The `addOperatorToNetwork()` method allows adding an existing operator to any number of networks.

---

### Q6: What happens to operators when a network is deleted?

Looking at `deleteNetwork()`:
```java
networkRepository.delete(networkCode);
AlertingService.notifyDeletion(username, networkCode, Network.class);
```

The operators themselves are NOT deleted. Only the network-operator association is removed. Operators can still be associated with other networks.

---

### Q7: What happens to gateways and sensors when a network is deleted?

**They are NOT deleted!** Only the network itself is removed.

From the requirements:
> *"These operations concern the existence of objects in the system, independently of any hierarchical relationships."*

The gateways and sensors become "orphaned" - they still exist but are no longer connected to any network. They can be:
- Reconnected to another network via `TopologyOperations`
- Deleted individually
- Left as orphans

See the [deleteNetwork() section](#deletenetwork) for a detailed visual explanation.

---

### Q8: Why doesn't the system use cascade delete?

**Design benefits of NO cascade delete:**

| Benefit | Explanation |
|---------|-------------|
| **Prevents data loss** | Accidental network deletion doesn't destroy all data |
| **Separation of concerns** | Entity existence is separate from relationships |
| **Flexibility** | Gateways/Sensors can be reassigned |
| **Data preservation** | Measurement history in sensors is preserved |

This is a deliberate architectural choice documented in the requirements.

---

## Summary

`NetworkOperationsImpl` provides complete CRUD functionality for Networks and Operators:

| Operation | Authorization | Timestamped | Notification |
|-----------|---------------|-------------|--------------|
| createNetwork | MAINTAINER | ✅ Yes | ❌ No |
| updateNetwork | MAINTAINER | ✅ Modified only | ❌ No |
| deleteNetwork | MAINTAINER | N/A | ✅ Yes |
| getNetworks | None (read) | N/A | ❌ No |
| createOperator | MAINTAINER | ❌ No | ❌ No |
| addOperatorToNetwork | MAINTAINER | ✅ Network modified | ❌ No |
| getNetworkReport | None (read) | N/A | ❌ No |

Key design patterns used:
- **Repository Pattern**: Data access through `CRUDRepository`
- **Facade Pattern**: `WeatherReport` delegates to `NetworkOperationsImpl`
- **Single Responsibility**: Each method does one thing well
- **Fail Fast**: Validation at the start of methods