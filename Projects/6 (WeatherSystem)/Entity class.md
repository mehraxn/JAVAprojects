# Understanding Entity Types

## Complete Explanation of Entity Types and Their Role in R1

---

## Table of Contents
1. [What is an Entity Type?](#what-is-an-entity-type)
2. [Entity Type vs Entity Instance](#entity-type-vs-entity-instance)
3. [How T (Entity Type) Works in CRUDRepository](#how-t-entity-type-works-in-crudrepository)
4. [All Entity Types in the Weather Report Project](#all-entity-types-in-the-weather-report-project)
5. [Entity Types Used in R1 Specifically](#entity-types-used-in-r1-specifically)
6. [Complete Examples](#complete-examples)

---

## What is an Entity Type?

### Simple Definition

**Entity Type** = The CLASS itself (not an object, the class)

```java
// Measurement is an ENTITY TYPE (the class)
public class Measurement { ... }

// Network is an ENTITY TYPE (the class)
public class Network { ... }

// Gateway is an ENTITY TYPE (the class)
public class Gateway { ... }
```

---

### Entity Type vs Regular Class

**Not every class is an entity type!**

```java
// This is an ENTITY TYPE (maps to database table)
@Entity
public class Measurement {
    @Id
    private Long id;
    // ... fields that become table columns
}

// This is NOT an entity type (just a regular class)
public class AlertingService {
    // ... just methods, doesn't map to database
}

// This is NOT an entity type (it's an interface)
public interface NetworkOperations {
    // ... just method signatures
}
```

**Entity Type Requirements:**
1. ✅ Must be a class (not interface, not enum)
2. ✅ Must have `@Entity` annotation (or be intended as entity)
3. ✅ Must have a primary key field with `@Id`
4. ✅ Must map to a database table

---

### Why Called "Type"?

In Java, "type" refers to the class/interface that defines what something is.

```java
// Variable declarations:
int x = 5;
└┬┘
 └─ int is the TYPE

String name = "John";
└──┬─┘
   └─ String is the TYPE

Measurement m = new Measurement();
└─────┬────┘
      └─ Measurement is the TYPE (entity type!)

Network n = new Network();
└───┬──┘
    └─ Network is the TYPE (entity type!)
```

**Entity Type = A type (class) that is an entity**

---

## Entity Type vs Entity Instance

This is CRITICAL to understand!

### Entity Type = The Class (Blueprint)

```java
// This is the ENTITY TYPE
public class Measurement {
    private Long id;
    private String networkCode;
    private double value;
    // ... more fields
}

// The class itself is the "type"
// Like a blueprint for a house
```

---

### Entity Instance = An Object (Actual Thing)

```java
// These are ENTITY INSTANCES (actual objects)
Measurement m1 = new Measurement("NET_01", "GW_0001", "S_000001", 23.5, timestamp);
//          └┬┘
//           └─ This is an INSTANCE (object)

Measurement m2 = new Measurement("NET_01", "GW_0001", "S_000001", 24.2, timestamp);
//          └┬┘
//           └─ This is another INSTANCE (different object)

// Each object is an instance of the Measurement TYPE
```

---

### The Analogy

```
Entity Type = Cookie Cutter (the mold)
  • One cookie cutter
  • Defines the shape
  • The template/blueprint

Entity Instance = Cookie (the actual cookie)
  • Many cookies
  • Each cookie is created from the cutter
  • Actual things you can eat

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

In Java:
Entity Type = class Measurement { }
  • One class definition
  • Defines structure
  • The blueprint

Entity Instance = new Measurement(...)
  • Many objects
  • Each object created from the class
  • Actual data in memory
```

---

### Visual Representation

```
┌─────────────────────────────────────────────┐
│ ENTITY TYPE: Measurement (the class)       │
├─────────────────────────────────────────────┤
│ Structure:                                  │
│   - Long id                                 │
│   - String networkCode                      │
│   - String gatewayCode                      │
│   - String sensorCode                       │
│   - double value                            │
│   - LocalDateTime timestamp                 │
└─────────────────────────────────────────────┘
                    │
        ┌───────────┴───────────┬───────────┐
        │                       │           │
        ↓                       ↓           ↓
┌──────────────┐   ┌──────────────┐   ┌──────────────┐
│ Instance #1  │   │ Instance #2  │   │ Instance #3  │
├──────────────┤   ├──────────────┤   ├──────────────┤
│ id: 1        │   │ id: 2        │   │ id: 3        │
│ network: N01 │   │ network: N01 │   │ network: N02 │
│ gateway: G01 │   │ gateway: G01 │   │ gateway: G02 │
│ sensor: S001 │   │ sensor: S001 │   │ sensor: S002 │
│ value: 23.5  │   │ value: 24.2  │   │ value: 18.7  │
│ time: 10:00  │   │ time: 10:15  │   │ time: 10:30  │
└──────────────┘   └──────────────┘   └──────────────┘

One TYPE, Many INSTANCES
```

---

## How T (Entity Type) Works in CRUDRepository

### T is a Placeholder for Entity Type

```java
public class CRUDRepository<T, ID> {
    //                      ↑
    //                      └─ T represents an ENTITY TYPE (a class)
    
    protected Class<T> entityClass;
    //        └───┬──┘
    //            └─ This stores which entity type (class) we're using
}
```

---

### When You Use CRUDRepository

```java
// Example 1: T = Measurement
CRUDRepository<Measurement, Long> measurementRepo = new CRUDRepository<>(Measurement.class);
//             └─────┬────┘                                           └──────┬──────┘
//                   │                                                        │
//            T = Measurement type                            Passing the Measurement class
//            (the class itself)                              (the entity type)

// Example 2: T = Network
CRUDRepository<Network, String> networkRepo = new CRUDRepository<>(Network.class);
//             └───┬──┘                                        └─────┬─────┘
//                 │                                                 │
//          T = Network type                               Passing the Network class
//          (the class itself)                             (the entity type)
```

---

### What Happens When T is Replaced

```java
// Original generic class:
public class CRUDRepository<T, ID> {
    public T create(T entity) { ... }
    public T read(ID id) { ... }
    public List<T> read() { ... }
    public T update(T entity) { ... }
    public T delete(ID id) { ... }
}

// When you use: CRUDRepository<Measurement, Long>
// T becomes Measurement everywhere:
public class CRUDRepository<Measurement, Long> {
    public Measurement create(Measurement entity) { ... }
    public Measurement read(Long id) { ... }
    public List<Measurement> read() { ... }
    public Measurement update(Measurement entity) { ... }
    public Measurement delete(Long id) { ... }
}

// When you use: CRUDRepository<Network, String>
// T becomes Network everywhere:
public class CRUDRepository<Network, String> {
    public Network create(Network entity) { ... }
    public Network read(String id) { ... }
    public List<Network> read() { ... }
    public Network update(Network entity) { ... }
    public Network delete(String id) { ... }
}
```

---

### Complete Example with Type Substitution

```java
// Step 1: Generic declaration
public class CRUDRepository<T, ID> {
    protected Class<T> entityClass;
    
    public T create(T entity) {
        EntityManager em = PersistenceManager.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(entity);  // entity is type T
            tx.commit();
            return entity;       // returns type T
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}

// Step 2: Usage with Measurement
CRUDRepository<Measurement, Long> repo = new CRUDRepository<>(Measurement.class);
//             └─────┬────┘
//                   └─ T = Measurement

// Step 3: Create a measurement
Measurement m = new Measurement("NET_01", "GW_0001", "S_000001", 23.5, timestamp);
//  └───┬──┘       └────────────────────┬───────────────────────┘
//      │                                │
//   Variable type              Creating an INSTANCE
//   (Measurement)              (actual object)

// Step 4: Save it
Measurement saved = repo.create(m);
//  └───┬──┘             │      └┬┘
//      │                │       │
//  Return type      Method  Parameter
//  (Measurement)     name    (Measurement instance)

// The create method signature becomes:
// public Measurement create(Measurement entity) { ... }
//        └─────┬────┘        └─────┬────┘
//              │                    │
//         T replaced          T replaced
//      with Measurement     with Measurement
```

---

## All Entity Types in the Weather Report Project

### Complete List of Entity Types

```
com.weather.report.model.entities/
    ├── Measurement.java      ← Entity Type #1
    ├── Network.java          ← Entity Type #2
    ├── Gateway.java          ← Entity Type #3
    ├── Sensor.java           ← Entity Type #4
    ├── Operator.java         ← Entity Type #5
    ├── User.java             ← Entity Type #6
    ├── Parameter.java        ← Entity Type #7 (embedded)
    └── Threshold.java        ← Entity Type #8 (embedded)
```

---

### Entity Type #1: Measurement

```java
// File: com/weather/report/model/entities/Measurement.java

@Entity
public class Measurement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String sensorCode;
    private String gatewayCode;
    private String networkCode;
    private double value;
    private LocalDateTime timestamp;
}

// This is an ENTITY TYPE
// Used as: CRUDRepository<Measurement, Long>
//                         └─────┬────┘
//                               └─ T = Measurement (the entity type)
```

---

### Entity Type #2: Network

```java
// File: com/weather/report/model/entities/Network.java

public class Network extends Timestamped {
    private String code;               // Primary key
    private String name;
    private String description;
    private Collection<Operator> operators;
}

// This is an ENTITY TYPE
// Used as: CRUDRepository<Network, String>
//                         └───┬──┘
//                             └─ T = Network (the entity type)
```

---

### Entity Type #3: Gateway

```java
// File: com/weather/report/model/entities/Gateway.java

public class Gateway extends Timestamped {
    private String code;               // Primary key
    private String name;
    private String description;
    private Collection<Parameter> parameters;
}

// This is an ENTITY TYPE
// Used as: CRUDRepository<Gateway, String>
//                         └───┬──┘
//                             └─ T = Gateway (the entity type)
```

---

### Entity Type #4: Sensor

```java
// File: com/weather/report/model/entities/Sensor.java

public class Sensor extends Timestamped {
    private String code;               // Primary key
    private String name;
    private String description;
    private Threshold threshold;
}

// This is an ENTITY TYPE
// Used as: CRUDRepository<Sensor, String>
//                         └──┬──┘
//                            └─ T = Sensor (the entity type)
```

---

### Entity Type #5: Operator

```java
// File: com/weather/report/model/entities/Operator.java

public class Operator {
    private String firstName;
    private String lastName;
    private String email;              // Primary key
    private String phoneNumber;
}

// This is an ENTITY TYPE
// Used as: CRUDRepository<Operator, String>
//                         └────┬───┘
//                              └─ T = Operator (the entity type)
```

---

### Entity Type #6: User

```java
// File: com/weather/report/model/entities/User.java

@Entity(name = "WR_USER")
public class User {
    @Id
    private String username;           // Primary key
    
    @Enumerated
    private UserType type;
}

// This is an ENTITY TYPE
// Used as: CRUDRepository<User, String>
//                         └─┬┘
//                           └─ T = User (the entity type)
```

---

### Entity Type #7: Parameter

```java
// File: com/weather/report/model/entities/Parameter.java

public class Parameter {
    private String code;               // Unique within gateway
    private String name;
    private String description;
    private double value;
}

// This is an ENTITY TYPE (embedded in Gateway)
// Not used directly with CRUDRepository
// Managed through Gateway
```

---

### Entity Type #8: Threshold

```java
// File: com/weather/report/model/entities/Threshold.java

public class Threshold {
    private double value;
    private ThresholdType type;
}

// This is an ENTITY TYPE (embedded in Sensor)
// Not used directly with CRUDRepository
// Managed through Sensor
```

---

### Summary Table: All Entity Types

| # | Entity Type | File | Used with CRUDRepository? | T in CRUDRepository<T, ID> |
|---|-------------|------|---------------------------|----------------------------|
| 1 | **Measurement** | Measurement.java | ✅ Yes | `CRUDRepository<Measurement, Long>` |
| 2 | **Network** | Network.java | ✅ Yes | `CRUDRepository<Network, String>` |
| 3 | **Gateway** | Gateway.java | ✅ Yes | `CRUDRepository<Gateway, String>` |
| 4 | **Sensor** | Sensor.java | ✅ Yes | `CRUDRepository<Sensor, String>` |
| 5 | **Operator** | Operator.java | ✅ Yes | `CRUDRepository<Operator, String>` |
| 6 | **User** | User.java | ✅ Yes | `CRUDRepository<User, String>` |
| 7 | **Parameter** | Parameter.java | ❌ No | Embedded in Gateway |
| 8 | **Threshold** | Threshold.java | ❌ No | Embedded in Sensor |

---

## Entity Types Used in R1 Specifically

### What is R1?

**R1 = Requirement 1 (Network Management)**

From the README:
> Requirement R1 concerns the management of `Network` and `Operator` entities, together with a reporting part at network level.

---

### Entity Types Used in R1

R1 uses **4 entity types**:

```
┌─────────────────────────────────────────────────────┐
│ R1: Network Management                              │
├─────────────────────────────────────────────────────┤
│                                                     │
│ Entity Types Used:                                  │
│                                                     │
│ 1. Measurement  ← For importing data               │
│ 2. Network      ← Main entity (create/update/del)  │
│ 3. Operator     ← For notifications                │
│ 4. Sensor       ← For threshold checking           │
│                                                     │
└─────────────────────────────────────────────────────┘
```

---

### Entity Type #1 in R1: Measurement

**Where used:** DataImportingService.storeMeasurements()

**Purpose:** Import measurement data from CSV files

**Code:**
```java
// File: DataImportingService.java

public static void storeMeasurements(String filePath) {
    // Read CSV file...
    // For each row:
    
    // Create Measurement instance (entity instance)
    Measurement measurement = new Measurement(
        //  └─────┬────┘
        //        └─ Using Measurement entity TYPE
        networkCode,
        gatewayCode,
        sensorCode,
        value,
        timestamp
    );
    
    // Save using CRUDRepository
    MeasurementRepository repo = new MeasurementRepository();
    //                                  └───────┬──────┘
    //            MeasurementRepository extends CRUDRepository<Measurement, Long>
    //                                                          └─────┬────┘
    //                                                                └─ T = Measurement
    
    repo.create(measurement);
    //          └─────┬────┘
    //                └─ Measurement instance (object)
    
    // Then check threshold
    checkMeasurement(measurement);
}
```

**How Measurement is used as entity type (T):**

```java
// MeasurementRepository.java
public class MeasurementRepository extends CRUDRepository<Measurement, Long> {
    //                                                     └─────┬────┘
    //                                                           │
    //                                        T = Measurement (entity type)
    
    public MeasurementRepository() {
        super(Measurement.class);  // Pass the entity type (class)
        //    └──────┬──────┘
        //           └─ The Measurement TYPE (not an instance!)
    }
}
```

---

### Entity Type #2 in R1: Network

**Where used:** NetworkOperations implementation

**Purpose:** Main entity for R1 - create, update, delete networks

**Code:**
```java
// In NetworkOperations implementation (assumed)

public class NetworkOperationsImpl implements NetworkOperations {
    
    private CRUDRepository<Network, String> networkRepository;
    //                     └───┬──┘
    //                         └─ T = Network (entity type)
    
    public NetworkOperationsImpl() {
        networkRepository = new CRUDRepository<>(Network.class);
        //                                       └────┬─────┘
        //                                            └─ Network TYPE
    }
    
    @Override
    public Network createNetwork(String code, String name, String description, String username) 
            throws IdAlreadyInUseException, InvalidInputDataException, UnauthorizedException {
        
        // Validate user...
        
        // Create Network instance (object)
        Network network = new Network();
        //  └─┬┘              └───┬──┘
        //    │                   └─ Using Network entity TYPE
        //    └─ Variable of Network type
        
        network.setCode(code);
        network.setName(name);
        network.setDescription(description);
        network.setCreatedBy(username);
        network.setCreatedAt(LocalDateTime.now());
        
        // Save using repository
        return networkRepository.create(network);
        //                              └───┬──┘
        //                                  └─ Network instance (object)
    }
    
    @Override
    public Network updateNetwork(String code, String name, String description, String username) 
            throws InvalidInputDataException, ElementNotFoundException, UnauthorizedException {
        
        // Find existing network
        Network network = networkRepository.read(code);
        //  └─┬┘                              └─┬┘
        //    │                                 └─ String ID (network code)
        //    └─ Network type (returned)
        
        if (network == null) {
            throw new ElementNotFoundException("Network not found: " + code);
        }
        
        // Update fields
        network.setName(name);
        network.setDescription(description);
        network.setModifiedBy(username);
        network.setModifiedAt(LocalDateTime.now());
        
        // Save changes
        return networkRepository.update(network);
        //                              └───┬──┘
        //                                  └─ Network instance
    }
    
    @Override
    public Network deleteNetwork(String code, String username) 
            throws InvalidInputDataException, ElementNotFoundException, UnauthorizedException {
        
        // Validate user...
        
        // Delete network
        Network deleted = networkRepository.delete(code);
        //  └─┬┘                                 └─┬┘
        //    │                                    └─ String ID
        //    └─ Network type (returned)
        
        if (deleted != null) {
            // Notify deletion
            AlertingService.notifyDeletion(username, code, Network.class);
            //                                              └────┬─────┘
            //                                                   └─ Network TYPE (class)
        }
        
        return deleted;
    }
    
    @Override
    public Collection<Network> getNetworks(String... codes) {
        //         └───┬──┘
        //             └─ Collection of Network type
        
        if (codes == null || codes.length == 0) {
            // Return all networks
            return networkRepository.read();
            //     └────────────┬──────────┘
            //                  └─ Returns List<Network>
            //                     (List of Network type)
        } else {
            // Filter by codes
            List<Network> allNetworks = networkRepository.read();
            //    └───┬──┘
            //        └─ List of Network type
            
            return allNetworks.stream()
                .filter(n -> Arrays.asList(codes).contains(n.getCode()))
                //      │
                //      └─ n is Network type (each element)
                .collect(Collectors.toList());
        }
    }
}
```

**How Network is used as entity type (T):**

```java
CRUDRepository<Network, String> networkRepository = new CRUDRepository<>(Network.class);
//             └───┬──┘                                              └────┬─────┘
//                 │                                                       │
//          T = Network type                                    Passing Network class
//          (the class itself)                                  (the entity type)

// All methods use Network type:
public Network create(Network entity) { ... }
//     └───┬──┘      └───┬──┘
//         │             └─ Parameter type = Network
//         └─ Return type = Network

public Network read(String id) { ... }
//     └───┬──┘
//         └─ Return type = Network

public List<Network> read() { ... }
//          └───┬──┘
//              └─ Element type = Network
```

---

### Entity Type #3 in R1: Operator

**Where used:** NetworkOperations.createOperator() and addOperatorToNetwork()

**Purpose:** Create operators and associate them with networks

**Code:**
```java
// In NetworkOperations implementation

public class NetworkOperationsImpl implements NetworkOperations {
    
    private CRUDRepository<Operator, String> operatorRepository;
    //                     └────┬───┘
    //                          └─ T = Operator (entity type)
    
    public NetworkOperationsImpl() {
        operatorRepository = new CRUDRepository<>(Operator.class);
        //                                        └────┬─────┘
        //                                             └─ Operator TYPE
    }
    
    @Override
    public Operator createOperator(String firstName, String lastName, String email, 
                                   String phoneNumber, String username) 
            throws InvalidInputDataException, IdAlreadyInUseException, UnauthorizedException {
        
        // Validate user...
        
        // Check if operator exists
        Operator existing = operatorRepository.read(email);
        //  └──┬──┘                                 └─┬┘
        //     │                                      └─ String ID (email)
        //     └─ Operator type (returned)
        
        if (existing != null) {
            throw new IdAlreadyInUseException("Operator already exists: " + email);
        }
        
        // Create Operator instance
        Operator operator = new Operator();
        //  └──┬──┘              └────┬───┘
        //     │                      └─ Using Operator entity TYPE
        //     └─ Variable of Operator type
        
        operator.setFirstName(firstName);
        operator.setLastName(lastName);
        operator.setEmail(email);
        operator.setPhoneNumber(phoneNumber);
        
        // Save using repository
        return operatorRepository.create(operator);
        //                                └────┬───┘
        //                                     └─ Operator instance
    }
    
    @Override
    public Network addOperatorToNetwork(String networkCode, String operatorEmail, String username) 
            throws ElementNotFoundException, InvalidInputDataException, UnauthorizedException {
        
        // Get network
        Network network = networkRepository.read(networkCode);
        if (network == null) {
            throw new ElementNotFoundException("Network not found");
        }
        
        // Get operator
        Operator operator = operatorRepository.read(operatorEmail);
        //  └──┬──┘                                 └──────┬─────┘
        //     │                                            └─ String ID (email)
        //     └─ Operator type (returned)
        
        if (operator == null) {
            throw new ElementNotFoundException("Operator not found");
        }
        
        // Add operator to network
        network.getOperators().add(operator);
        //                         └────┬───┘
        //                              └─ Operator instance
        
        // Save changes
        return networkRepository.update(network);
    }
}
```

**How Operator is used as entity type (T):**

```java
CRUDRepository<Operator, String> operatorRepository = new CRUDRepository<>(Operator.class);
//             └────┬───┘                                                └─────┬─────┘
//                  │                                                          │
//          T = Operator type                                        Passing Operator class
//                                                                   (the entity type)

// All methods use Operator type:
public Operator create(Operator entity) { ... }
//     └───┬──┘       └───┬───┘
//         │              └─ Parameter type = Operator
//         └─ Return type = Operator

public Operator read(String id) { ... }
//     └───┬──┘
//         └─ Return type = Operator
```

---

### Entity Type #4 in R1: Sensor

**Where used:** DataImportingService.checkMeasurement()

**Purpose:** Check thresholds for sensor measurements

**Code:**
```java
// File: DataImportingService.java

private static void checkMeasurement(Measurement measurement) {
    /***********************************************************************/
    /* Do not change these lines, use currentSensor to check for possible */
    /* threshold violation, tests mocks this db interaction */
    /***********************************************************************/
    CRUDRepository<Sensor, String> sensorRepository = new CRUDRepository<>(Sensor.class);
    //             └──┬──┘                                                 └────┬─────┘
    //                │                                                          │
    //         T = Sensor type                                          Passing Sensor class
    //                                                                   (the entity type)
    
    Sensor currentSensor = sensorRepository.read().stream()
    //  └┬┘                                   │
    //   │                                    └─ Returns List<Sensor>
    //   └─ Variable of Sensor type                    └───┬──┘
    //                                                      └─ List element type = Sensor
        .filter(s -> measurement.getSensorCode().equals(s.getCode()))
        //      │                                       │
        //      └─ s is Sensor type                    └─ s is Sensor type
        .findFirst()
        .orElse(null);
    /***********************************************************************/
    
    // Now check threshold
    if (currentSensor != null && currentSensor.getThreshold() != null) {
        //  └──────┬─────┘              └──────┬─────┘
        //         │                            └─ currentSensor is Sensor type
        //         └─ currentSensor is Sensor type
        
        Threshold threshold = currentSensor.getThreshold();
        double measuredValue = measurement.getValue();
        double thresholdValue = threshold.getValue();
        ThresholdType type = threshold.getType();
        
        boolean violated = false;
        
        // Check violation based on type...
        switch (type) {
            case LESS_THAN:
                violated = measuredValue < thresholdValue;
                break;
            case GREATER_THAN:
                violated = measuredValue > thresholdValue;
                break;
            // ... other cases
        }
        
        if (violated) {
            // Get network to notify operators
            CRUDRepository<Network, String> networkRepository = new CRUDRepository<>(Network.class);
            Network network = networkRepository.read(measurement.getNetworkCode());
            
            if (network != null && network.getOperators() != null) {
                AlertingService.notifyThresholdViolation(
                    network.getOperators(),
                    currentSensor.getCode()
                    //  └──────┬─────┘
                    //         └─ currentSensor is Sensor type
                );
            }
        }
    }
}
```

**How Sensor is used as entity type (T):**

```java
CRUDRepository<Sensor, String> sensorRepository = new CRUDRepository<>(Sensor.class);
//             └──┬──┘                                                └────┬─────┘
//                │                                                         │
//         T = Sensor type                                         Passing Sensor class
//                                                                  (the entity type)

// Methods use Sensor type:
public List<Sensor> read() { ... }
//          └──┬──┘
//             └─ Element type = Sensor

// When we call:
List<Sensor> allSensors = sensorRepository.read();
//   └──┬──┘
//      └─ List element type = Sensor
```

---

### R1 Entity Types Summary

```
┌──────────────────────────────────────────────────────────────┐
│ R1 (Network Management) - Entity Types Used                 │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│ 1. Measurement                                               │
│    • File: DataImportingService.java                         │
│    • Usage: Import and store measurements from CSV           │
│    • Repository: MeasurementRepository                       │
│    • T in: CRUDRepository<Measurement, Long>                │
│                                                              │
│ 2. Network                                                   │
│    • File: NetworkOperations implementation                  │
│    • Usage: Create, update, delete networks                  │
│    • Main entity for R1                                      │
│    • T in: CRUDRepository<Network, String>                  │
│                                                              │
│ 3. Operator                                                  │
│    • File: NetworkOperations implementation                  │
│    • Usage: Create operators, associate with networks        │
│    • T in: CRUDRepository<Operator, String>                 │
│                                                              │
│ 4. Sensor                                                    │
│    • File: DataImportingService.java                         │
│    • Usage: Check thresholds for measurements                │
│    • T in: CRUDRepository<Sensor, String>                   │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

---

## Complete Examples

### Example 1: Understanding T with Measurement

```java
// ===============================================
// PART 1: Define the entity TYPE
// ===============================================

@Entity
public class Measurement {
    //     └─────┬────┘
    //           └─ This is the ENTITY TYPE (the class)
    
    @Id
    @GeneratedValue
    private Long id;
    private String networkCode;
    private double value;
    // ... more fields
}

// ===============================================
// PART 2: Use the entity TYPE with CRUDRepository
// ===============================================

public class MeasurementRepository extends CRUDRepository<Measurement, Long> {
    //                                                     └─────┬────┘  └─┬┘
    //                                                           │         │
    //                                                  T = Measurement   ID = Long
    //                                                  (entity TYPE)     (ID type)
    
    public MeasurementRepository() {
        super(Measurement.class);
        //    └──────┬──────┘
        //           └─ Passing the Measurement TYPE (the class itself)
    }
}

// ===============================================
// PART 3: Create entity INSTANCES
// ===============================================

// Create repository (works with the Measurement TYPE)
MeasurementRepository repo = new MeasurementRepository();

// Create instances (actual objects)
Measurement m1 = new Measurement("NET_01", "GW_0001", "S_000001", 23.5, timestamp1);
//  └───┬──┘       └──────────────────────┬─────────────────────────┘
//      │                                  │
//   Variable type                   Creating an INSTANCE
//   (Measurement)                   (actual object)

Measurement m2 = new Measurement("NET_01", "GW_0001", "S_000001", 24.2, timestamp2);
//  └───┬──┘       └──────────────────────┬─────────────────────────┘
//      │                                  │
//   Variable type                   Another INSTANCE
//   (Measurement)                   (different object)

// ===============================================
// PART 4: Save instances using the TYPE-specific repository
// ===============================================

Measurement saved1 = repo.create(m1);
//  └───┬──┘             │      └┬┘
//      │                │       │
//  Return type      Method  Parameter
//  (Measurement)     name   (instance)

Measurement saved2 = repo.create(m2);
//  └───┬──┘                    └┬┘
//      │                        │
//  Return type            Another instance
//  (Measurement)

// ===============================================
// THE KEY INSIGHT:
// ===============================================
// 
// Measurement.class    ← The TYPE (used with CRUDRepository<T>)
// m1, m2               ← The INSTANCES (actual objects)
// 
// One TYPE, many INSTANCES
// Like: One cookie cutter, many cookies
```

---

### Example 2: Understanding T with Network

```java
// ===============================================
// PART 1: Define the entity TYPE
// ===============================================

public class Network extends Timestamped {
    //     └───┬──┘
    //         └─ This is the ENTITY TYPE (the class)
    
    private String code;           // Primary key
    private String name;
    private String description;
    private Collection<Operator> operators;
}

// ===============================================
// PART 2: Use the entity TYPE with CRUDRepository
// ===============================================

public class NetworkOperationsImpl implements NetworkOperations {
    
    private CRUDRepository<Network, String> networkRepository;
    //                     └───┬──┘ └──┬──┘
    //                         │      │
    //                  T = Network   ID = String
    //                  (entity TYPE) (ID type)
    
    public NetworkOperationsImpl() {
        networkRepository = new CRUDRepository<>(Network.class);
        //                                       └────┬─────┘
        //                                            └─ Passing the Network TYPE
    }
    
    // ===============================================
    // PART 3: Create and manage entity INSTANCES
    // ===============================================
    
    @Override
    public Network createNetwork(String code, String name, String description, String username) {
        // Create instance (object)
        Network network = new Network();
        //  └─┬┘              └───┬──┘
        //    │                   └─ Creating an INSTANCE using the Network TYPE
        //    └─ Variable type (Network)
        
        network.setCode(code);
        network.setName(name);
        network.setDescription(description);
        network.setCreatedBy(username);
        network.setCreatedAt(LocalDateTime.now());
        
        // Save instance
        return networkRepository.create(network);
        //                              └───┬──┘
        //                                  └─ The INSTANCE (object)
    }
    
    @Override
    public Collection<Network> getNetworks(String... codes) {
        //         └───┬──┘
        //             └─ Collection of Network TYPE
        
        List<Network> allNetworks = networkRepository.read();
        //    └───┬──┘                                  └─┬┘
        //        │                                       └─ Returns List<Network>
        //        └─ List of Network TYPE
        
        return allNetworks;  // Collection of Network INSTANCES
    }
}

// ===============================================
// PART 4: Usage example
// ===============================================

NetworkOperations ops = new NetworkOperationsImpl();

// Create three network instances
Network n1 = ops.createNetwork("NET_01", "City Network", "Main city", "admin");
//  └─┬┘                        └───────────────┬──────────────────┘
//    │                                         │
// Variable type                         Parameters to create instance
// (Network)

Network n2 = ops.createNetwork("NET_02", "Rural Network", "Rural area", "admin");
//  └─┬┘
//    └─ Another variable of Network TYPE

Network n3 = ops.createNetwork("NET_03", "Coastal Network", "Coast", "admin");
//  └─┬┘
//    └─ Yet another variable of Network TYPE

// Get all networks
Collection<Network> allNetworks = ops.getNetworks();
//             └─┬┘
//               └─ Collection containing Network TYPE elements

// ===============================================
// THE KEY INSIGHT:
// ===============================================
// 
// Network.class    ← The TYPE (used with CRUDRepository<T>)
// n1, n2, n3       ← The INSTANCES (actual objects)
// 
// One TYPE, many INSTANCES
```

---

### Example 3: Comparing All Entity Types in R1

```java
// ===============================================
// R1 uses 4 different ENTITY TYPES
// ===============================================

// TYPE 1: Measurement
CRUDRepository<Measurement, Long> measurementRepo = new CRUDRepository<>(Measurement.class);
//             └─────┬────┘                                           └──────┬──────┘
//                   │                                                        │
//            T = Measurement TYPE                               Measurement class (TYPE)

Measurement m = new Measurement(...);  // Create INSTANCE
measurementRepo.create(m);             // Save INSTANCE

// ───────────────────────────────────────────────

// TYPE 2: Network
CRUDRepository<Network, String> networkRepo = new CRUDRepository<>(Network.class);
//             └───┬──┘                                        └────┬─────┘
//                 │                                                │
//          T = Network TYPE                              Network class (TYPE)

Network n = new Network();  // Create INSTANCE
networkRepo.create(n);      // Save INSTANCE

// ───────────────────────────────────────────────

// TYPE 3: Operator
CRUDRepository<Operator, String> operatorRepo = new CRUDRepository<>(Operator.class);
//             └────┬───┘                                         └─────┬─────┘
//                  │                                                   │
//          T = Operator TYPE                               Operator class (TYPE)

Operator o = new Operator();  // Create INSTANCE
operatorRepo.create(o);       // Save INSTANCE

// ───────────────────────────────────────────────

// TYPE 4: Sensor
CRUDRepository<Sensor, String> sensorRepo = new CRUDRepository<>(Sensor.class);
//             └──┬──┘                                           └────┬─────┘
//                │                                                   │
//          T = Sensor TYPE                                 Sensor class (TYPE)

Sensor s = sensorRepository.read().stream().findFirst().orElse(null);  // Get INSTANCE
// └─┬┘                              │
//   │                               └─ Stream of Sensor TYPE elements
//   └─ Variable of Sensor TYPE

// ═══════════════════════════════════════════════
// SUMMARY:
// ═══════════════════════════════════════════════
//
// 4 Entity TYPES:
//   • Measurement.class  (the TYPE)
//   • Network.class      (the TYPE)
//   • Operator.class     (the TYPE)
//   • Sensor.class       (the TYPE)
//
// Many INSTANCES:
//   • m1, m2, m3...      (Measurement instances)
//   • n1, n2, n3...      (Network instances)
//   • o1, o2, o3...      (Operator instances)
//   • s1, s2, s3...      (Sensor instances)
```

---

## Final Summary

### What is an Entity Type?

```
Entity Type = The CLASS itself (not objects created from it)

Examples:
  • Measurement  ← Entity TYPE (the class)
  • Network      ← Entity TYPE (the class)
  • Gateway      ← Entity TYPE (the class)
  • Sensor       ← Entity TYPE (the class)
```

---

### Entity Type vs Entity Instance

```
TYPE:     The class definition (blueprint)
          One per entity
          Used with: CRUDRepository<T, ID>
          Example: Measurement.class

INSTANCE: Objects created from the class (actual data)
          Many per entity type
          Created with: new Measurement(...)
          Example: m1, m2, m3
```

---

### How T Works in CRUDRepository

```
CRUDRepository<T, ID>
               ↑
               └─ T is a placeholder for an Entity TYPE

When you write:
  CRUDRepository<Measurement, Long>
  
T becomes Measurement everywhere:
  public Measurement create(Measurement entity)
  public Measurement read(Long id)
  public List<Measurement> read()
```

---

### All Entity Types in Project

```
1. Measurement  ← CRUDRepository<Measurement, Long>
2. Network      ← CRUDRepository<Network, String>
3. Gateway      ← CRUDRepository<Gateway, String>
4. Sensor       ← CRUDRepository<Sensor, String>
5. Operator     ← CRUDRepository<Operator, String>
6. User         ← CRUDRepository<User, String>
7. Parameter    ← Embedded (not used with CRUDRepository directly)
8. Threshold    ← Embedded (not used with CRUDRepository directly)
```

---

### Entity Types in R1

```
R1 (Network Management) uses 4 entity types:

1. Measurement  ← Import measurements from CSV
2. Network      ← Main entity (create/update/delete)
3. Operator     ← Create and associate with networks
4. Sensor       ← Check thresholds

Each is used as T in CRUDRepository<T, ID>
```

---

## Key Takeaway

**Entity Type = The class that represents a database table**

```
Class (TYPE)          Objects (INSTANCES)
────────────          ───────────────────
Measurement    →      m1, m2, m3, m4...
Network        →      n1, n2, n3...
Operator       →      o1, o2, o3...
Sensor         →      s1, s2, s3...

One TYPE, many INSTANCES!
Like one cookie cutter, many cookies!
```

---

**END OF DOCUMENT**