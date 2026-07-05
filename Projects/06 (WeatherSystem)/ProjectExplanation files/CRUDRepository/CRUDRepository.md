# Understanding CRUDRepository.java

## Complete Guide to the Generic Repository Pattern in Weather Report System

---

## Table of Contents
1. [Overall File Explanation](#overall-file-explanation)
2. [What is CRUD?](#what-is-crud)
3. [What is a Repository Pattern?](#what-is-a-repository-pattern)
4. [Understanding Generic Types](#understanding-generic-types)
5. [R1 Requirements for This File](#r1-requirements-for-this-file)
6. [Complete Line-by-Line Explanation](#complete-line-by-line-explanation)
7. [JPA and Hibernate Concepts](#jpa-and-hibernate-concepts)
8. [Real-World Examples](#real-world-examples)
9. [Common Questions and Answers](#common-questions-and-answers)

---

## Overall File Explanation

### What Is This File?

**CRUDRepository.java** is a **generic repository class** that provides basic database operations (Create, Read, Update, Delete) for ALL entities in the Weather Report system.

### The Big Picture

Think of CRUDRepository as a **universal database assistant**:

```
You have many types of data:
• Network
• Gateway
• Sensor
• Measurement
• User
• Operator
• Parameter
• Threshold

Without CRUDRepository (BAD):
├─ NetworkRepository with create(), read(), update(), delete()
├─ GatewayRepository with create(), read(), update(), delete()
├─ SensorRepository with create(), read(), update(), delete()
├─ MeasurementRepository with create(), read(), update(), delete()
└─ ... (repeated code everywhere!) ❌

With CRUDRepository (GOOD):
└─ ONE generic CRUDRepository with create(), read(), update(), delete()
   ├─ Works for Network
   ├─ Works for Gateway
   ├─ Works for Sensor
   ├─ Works for Measurement
   └─ Works for EVERYTHING! ✅
```

### Key Responsibilities

**What this class does:**

1. **Create** - Save new entities to database
2. **Read** - Retrieve entities from database (by ID or all)
3. **Update** - Modify existing entities in database
4. **Delete** - Remove entities from database
5. **Abstraction** - Hide JPA/Hibernate complexity
6. **Reusability** - One implementation for all entities

### The Technology Stack

```
CRUDRepository.java
       ↓ uses
JPA (Jakarta Persistence API)
       ↓ interface
Hibernate (Implementation)
       ↓ generates SQL
H2 Database
       ↓ stores
Actual data on disk
```

**In simple terms:**

```
Your code:
  "Save this measurement"
       ↓
CRUDRepository:
  "I'll handle the database details"
       ↓
JPA/Hibernate:
  "I'll generate the SQL"
       ↓
Database:
  "Data saved!"
```

---

## What is CRUD?

CRUD is an acronym representing the four basic database operations:

### C - Create

**Purpose:** Add new records to the database

**Example:**
```java
Measurement m = new Measurement("NET_01", "GW_0001", "S_000001", 23.5, timestamp);
repo.create(m);  // Insert into database
```

**SQL equivalent:**
```sql
INSERT INTO Measurement (networkCode, gatewayCode, sensorCode, value, timestamp)
VALUES ('NET_01', 'GW_0001', 'S_000001', 23.5, '2024-01-15 10:30:00');
```

---

### R - Read

**Purpose:** Retrieve records from the database

**Two variants:**

**1. Read by ID (single record):**
```java
Measurement m = repo.read(47L);  // Get measurement with ID=47
```

**SQL equivalent:**
```sql
SELECT * FROM Measurement WHERE id = 47;
```

**2. Read all (all records):**
```java
List<Measurement> allMeasurements = repo.read();  // Get all measurements
```

**SQL equivalent:**
```sql
SELECT * FROM Measurement;
```

---

### U - Update

**Purpose:** Modify existing records in the database

**Example:**
```java
Measurement m = repo.read(47L);  // Get existing measurement
m.setValue(25.3);                // Change value
repo.update(m);                  // Save changes
```

**SQL equivalent:**
```sql
UPDATE Measurement 
SET value = 25.3 
WHERE id = 47;
```

---

### D - Delete

**Purpose:** Remove records from the database

**Example:**
```java
Measurement deleted = repo.delete(47L);  // Remove measurement with ID=47
```

**SQL equivalent:**
```sql
DELETE FROM Measurement WHERE id = 47;
```

---

### CRUD in Daily Life

**Analogy: Contact List on Your Phone**

```
CREATE:  Add new contact "John: +1234567890"
READ:    Look up John's phone number
UPDATE:  Change John's number to +0987654321
DELETE:  Remove John from contacts
```

**Every database application needs CRUD operations!**

---

## What is a Repository Pattern?

### The Problem It Solves

**Without Repository Pattern:**

```java
// In NetworkOperations.java
public Network createNetwork(...) {
    EntityManager em = getEntityManager();
    EntityTransaction tx = em.getTransaction();
    tx.begin();
    em.persist(network);
    tx.commit();
    em.close();
    // ... database code mixed with business logic
}

// In GatewayOperations.java
public Gateway createGateway(...) {
    EntityManager em = getEntityManager();
    EntityTransaction tx = em.getTransaction();
    tx.begin();
    em.persist(gateway);
    tx.commit();
    em.close();
    // ... same database code duplicated!
}

// In SensorOperations.java
public Sensor createSensor(...) {
    EntityManager em = getEntityManager();
    EntityTransaction tx = em.getTransaction();
    tx.begin();
    em.persist(sensor);
    tx.commit();
    em.close();
    // ... same database code again!
}

Problem: Database code DUPLICATED everywhere! ❌
```

**With Repository Pattern:**

```java
// CRUDRepository.java (ONE place)
public T create(T entity) {
    EntityManager em = getEntityManager();
    EntityTransaction tx = em.getTransaction();
    tx.begin();
    em.persist(entity);
    tx.commit();
    em.close();
    return entity;
}

// In NetworkOperations.java
CRUDRepository<Network, String> repo = new CRUDRepository<>(Network.class);
Network network = repo.create(network);  // Simple!

// In GatewayOperations.java
CRUDRepository<Gateway, String> repo = new CRUDRepository<>(Gateway.class);
Gateway gateway = repo.create(gateway);  // Same simple call!

// In SensorOperations.java
CRUDRepository<Sensor, String> repo = new CRUDRepository<>(Sensor.class);
Sensor sensor = repo.create(sensor);  // Same simple call!

Solution: Database code in ONE place! ✅
```

---

### Repository Pattern Benefits

**1. Separation of Concerns**

```
Business Logic Layer (Operations):
  "I need to save a network"
       ↓
Repository Layer (CRUDRepository):
  "I handle database operations"
       ↓
Persistence Layer (JPA/Hibernate):
  "I generate SQL and communicate with database"
```

**2. Code Reusability**

```
Write database code ONCE
Use it for ALL entities
Network, Gateway, Sensor, Measurement, etc.
```

**3. Testability**

```
Easy to mock repository in tests:
  Mock: "Pretend to save data"
  Test: "Check business logic without real database"
```

**4. Maintainability**

```
Change database logic?
  → Edit ONE file (CRUDRepository)
  → All entities benefit automatically!

Without repository:
  → Edit 10+ files
  → Easy to miss one
  → Bugs!
```

**5. Abstraction**

```
Operations don't need to know:
  • How to start transactions
  • How to generate SQL
  • How to handle database connections
  
Repository handles all of that!
```

---

### Real-World Analogy

**Repository = Library System**

```
Without Repository (Bad):
  • Every professor goes to shelves directly
  • Finds books themselves
  • Updates catalog manually
  • Risk of mistakes, chaos!

With Repository (Good):
  • Professors ask librarian: "I need book X"
  • Librarian finds it
  • Librarian updates catalog
  • Librarian knows the system
  • Clean, organized, professional!

CRUDRepository = The Librarian
  • Knows how to find things (read)
  • Knows how to add new books (create)
  • Knows how to update records (update)
  • Knows how to remove books (delete)
```

---

## Understanding Generic Types

Before diving into the code, we need to understand **generics** because this class uses them heavily!

### What Are Generics?

**Generics** allow you to write code that works with **any type**.

**Without Generics (Bad):**

```java
// Separate repository for each entity
public class MeasurementRepository {
    public Measurement create(Measurement m) { ... }
    public Measurement read(Long id) { ... }
}

public class NetworkRepository {
    public Network create(Network n) { ... }
    public Network read(String id) { ... }
}

public class GatewayRepository {
    public Gateway create(Gateway g) { ... }
    public Gateway read(String id) { ... }
}

// Duplicated code for each entity! ❌
```

**With Generics (Good):**

```java
// ONE repository for ALL entities
public class CRUDRepository<T, ID> {
    public T create(T entity) { ... }
    public T read(ID id) { ... }
}

// Use for Measurement:
CRUDRepository<Measurement, Long> measurementRepo = ...;
Measurement m = measurementRepo.create(measurement);

// Use for Network:
CRUDRepository<Network, String> networkRepo = ...;
Network n = networkRepo.create(network);

// Same code, different types! ✅
```

---

### Generic Type Parameters in CRUDRepository

```java
public class CRUDRepository<T, ID> {
                           └┬┘ └┬┘
                            │   │
                            │   └─ ID type (what type is the primary key?)
                            └─ Entity type (what are we storing?)
}
```

**T - Entity Type**

The type of object we're storing (Network, Gateway, Sensor, etc.)

```java
CRUDRepository<Measurement, Long>
               └────┬─────┘
                    │
            T = Measurement
```

**ID - Identifier Type**

The type of the primary key for that entity

```java
CRUDRepository<Measurement, Long>
                           └──┬─┘
                              │
                       ID = Long

Why Long?
  Measurement has: @Id private Long id;
```

---

### How Generics Work

**When you create an instance:**

```java
CRUDRepository<Network, String> repo = new CRUDRepository<>(Network.class);
                └──┬──┘ └──┬──┘
                   │       │
         T = Network    ID = String
```

**All T's in the code become Network:**

```java
public T create(T entity) { ... }
       ↓
public Network create(Network entity) { ... }

public T read(ID id) { ... }
       ↓
public Network read(String id) { ... }
```

**Magic!** One class becomes many specific classes!

---

### Generic Types in Our Entities

**Different entities use different ID types:**

```java
// Measurement uses Long (auto-generated number)
CRUDRepository<Measurement, Long>
@Entity
public class Measurement {
    @Id
    @GeneratedValue
    private Long id;  // ← ID type is Long
}

// Network uses String (user-defined code)
CRUDRepository<Network, String>
public class Network {
    private String code;  // ← ID is the code (String)
}

// Gateway uses String
CRUDRepository<Gateway, String>
public class Gateway {
    private String code;  // ← ID is the code (String)
}

// Sensor uses String
CRUDRepository<Sensor, String>
public class Sensor {
    private String code;  // ← ID is the code (String)
}
```

---

### Bounded Type Parameters

You might wonder: "Can T be ANY type?"

Not quite! There's an implicit constraint:

```java
public class CRUDRepository<T, ID> {
    // T must be an entity class (annotated with @Entity)
    // ID must be the type of its primary key
}
```

This is enforced by this method:

```java
protected String getEntityName() {
    Entity ea = entityClass.getAnnotation(jakarta.persistence.Entity.class);
    if (ea == null)
        throw new IllegalArgumentException("Class must be annotated as @Entity");
    // ...
}
```

**So T must be:**
- A JPA entity (has @Entity annotation)
- Has an @Id field of type ID

---

## R1 Requirements for This File

### What Does R1 Require?

According to the README, **Requirement R1** (Network management) needs:

1. **Data import functionality**
   - Import measurements from CSV files
   - Save measurements to database using repositories

2. **NetworkOperations implementation**
   - Create, update, delete networks
   - Create operators
   - Manage network-operator relationships
   - Generate network reports

### CRUDRepository's Role in R1

**CRUDRepository must be implemented to support:**

1. **Saving measurements** (in DataImportingService)
   ```java
   MeasurementRepository repo = new MeasurementRepository();
   repo.create(measurement);  // ← Needs CRUDRepository.create()
   ```

2. **Managing networks** (in NetworkOperations)
   ```java
   CRUDRepository<Network, String> repo = new CRUDRepository<>(Network.class);
   repo.create(network);   // Create network
   repo.read(code);        // Read network by code
   repo.read();            // Read all networks
   repo.update(network);   // Update network
   repo.delete(code);      // Delete network
   ```

3. **Managing operators** (in NetworkOperations)
   ```java
   CRUDRepository<Operator, String> repo = new CRUDRepository<>(Operator.class);
   repo.create(operator);  // Create operator
   ```

4. **Checking sensors in threshold validation**
   ```java
   CRUDRepository<Sensor, String> repo = new CRUDRepository<>(Sensor.class);
   repo.read();  // Get all sensors to find the one matching measurement
   ```

---

### What Needed to Be Implemented?

Looking at the code, we can see `//ADDED FOR R1` comments showing what was implemented:

**ALL FIVE CRUD METHODS:**

1. ✅ **Constructor** - Store the entity class
2. ✅ **create()** - Persist new entities
3. ✅ **read(ID id)** - Find entity by ID
4. ✅ **read()** - Get all entities
5. ✅ **update()** - Modify existing entities
6. ✅ **delete()** - Remove entities

**Plus helper method:**
- ✅ **getEntityName()** - Get entity name for JPQL queries

---

### Why R1 Can't Work Without This

```
DataImportingService.storeMeasurements()
        ↓
    repo.create(measurement)  ← Needs CRUDRepository!
        ↓
    Without CRUDRepository: Can't save measurements! ❌
    With CRUDRepository: Measurements saved! ✅

NetworkOperations.createNetwork()
        ↓
    repo.create(network)  ← Needs CRUDRepository!
        ↓
    Without CRUDRepository: Can't create networks! ❌
    With CRUDRepository: Networks created! ✅
```

**In summary:** CRUDRepository is the **foundation** of the entire persistence layer. Without it, nothing can be saved to or retrieved from the database!

---

## Complete Line-by-Line Explanation

Now let's examine EVERY line of code in detail!

### Package and Imports (Lines 1-9)

```java
package com.weather.report.repositories;
```

**What this does:**
Declares that this class belongs to the `repositories` package.

**Package structure:**
```
com.weather.report
    ├── repositories     ← This package
    │   ├── CRUDRepository.java  ← This file
    │   └── MeasurementRepository.java
    ├── model
    ├── operations
    └── services
```

---

```java
import java.util.List;
```

**What this does:**
Imports the `List` interface from Java's standard library.

**Used for:**
```java
public List<T> read() { ... }  // Returns a List of entities
```

**Why List?**
- Interface (flexible - ArrayList, LinkedList, etc.)
- Ordered collection
- Can contain duplicates
- Perfect for database query results

---

```java
import com.weather.report.persistence.PersistenceManager;  //ADDED FOR R1
```

**What this does:**
Imports our custom PersistenceManager class.

**What is PersistenceManager?**

```java
// PersistenceManager.java
public class PersistenceManager {
    public static EntityManager getEntityManager() {
        // Creates and returns EntityManager for database operations
    }
}
```

**Why we need it:**
```
Every database operation needs an EntityManager
PersistenceManager provides it centrally
One place to manage database connections
```

**Usage in our code:**
```java
EntityManager em = PersistenceManager.getEntityManager();
// Now we can use 'em' to interact with database
```

---

```java
import jakarta.persistence.Entity;
```

**What this does:**
Imports the `@Entity` annotation.

**Used in:**
```java
protected String getEntityName() {
    Entity ea = entityClass.getAnnotation(Entity.class);
    // Check if class has @Entity annotation
}
```

**Why Entity annotation?**
```java
@Entity  // ← Marks class as database table
public class Measurement {
    // This class will be stored in database
}
```

---

```java
import jakarta.persistence.EntityManager;  //ADDED FOR R1
```

**What this does:**
Imports the EntityManager interface.

**What is EntityManager?**

EntityManager is the **main interface** for interacting with the database in JPA.

Think of it as your **database assistant**:

```
EntityManager = Your interface to the database

What it can do:
• persist(entity)  - Save new entity
• find(class, id)  - Find entity by ID
• merge(entity)    - Update existing entity
• remove(entity)   - Delete entity
• createQuery()    - Execute JPQL queries
• getTransaction() - Manage transactions
```

**In our code:**
```java
EntityManager em = PersistenceManager.getEntityManager();
em.persist(measurement);  // Save to database
em.find(Measurement.class, 47L);  // Find by ID
em.close();  // Release resources
```

---

```java
import jakarta.persistence.EntityTransaction;  //ADDED FOR R1
```

**What this does:**
Imports the EntityTransaction interface.

**What is EntityTransaction?**

A **transaction** is a unit of work that must complete fully or not at all.

**Analogy: Bank Transfer**

```
Transfer $100 from Account A to Account B

WITHOUT transaction:
1. Deduct $100 from Account A  ✓
2. [POWER OUTAGE!]
3. Add $100 to Account B  ✗

Result: $100 disappeared! 💸 ❌

WITH transaction:
tx.begin()
1. Deduct $100 from Account A  ✓
2. [POWER OUTAGE!]
tx.rollback()  // Undo everything!

Result: Account A unchanged, no money lost ✅
```

**In database terms:**

```java
EntityTransaction tx = em.getTransaction();
tx.begin();           // Start transaction

em.persist(entity1);  // Operation 1
em.persist(entity2);  // Operation 2
em.persist(entity3);  // Operation 3

tx.commit();          // All operations succeed together
// OR
tx.rollback();        // All operations fail together
```

**ACID Properties:**

```
A - Atomic:     All or nothing
C - Consistent: Database stays valid
I - Isolated:   Transactions don't interfere
D - Durable:    Committed data persists
```

---

```java
import jakarta.persistence.TypedQuery;  //ADDED FOR R1
```

**What this does:**
Imports the TypedQuery interface.

**What is TypedQuery?**

A type-safe query that returns specific types of objects.

**Without TypedQuery (old way):**
```java
Query query = em.createQuery("SELECT m FROM Measurement m");
List results = query.getResultList();  // ← Returns raw List (no type safety)
Measurement m = (Measurement) results.get(0);  // ← Need to cast! Dangerous!
```

**With TypedQuery (our way):**
```java
TypedQuery<Measurement> query = em.createQuery("SELECT m FROM Measurement m", Measurement.class);
List<Measurement> results = query.getResultList();  // ← Returns List<Measurement>
Measurement m = results.get(0);  // ← No cast needed! Safe! ✅
```

**In our code:**
```java
String jpql = "SELECT e FROM Measurement e";
TypedQuery<Measurement> query = em.createQuery(jpql, Measurement.class);
List<Measurement> results = query.getResultList();
// results is guaranteed to contain Measurement objects
```

---

### Class Declaration (Lines 11-23)

```java
/**
 * Generic repository exposing basic CRUD operations backed by the persistence
 * layer.
 * <p>
 * Concrete repositories extend/compose this class to centralise common database
 * access
 * logic for all entities, as described in the README.
 *
 * @param <T>  entity type
 * @param <ID> identifier (primary key) type
 */
public class CRUDRepository<T, ID> {
```

**Breaking it down:**

```java
public class CRUDRepository<T, ID> {
       └──┬─┘              └─┬──┘
          │                  │
     Public class     Generic type parameters
```

**`public`:**
- Can be used from any package
- NetworkOperations, GatewayOperations, etc. can all use it

**`class`:**
- This is a class (not interface or abstract class)
- Can be instantiated: `new CRUDRepository<>(...)`

**`<T, ID>`:**
- Two generic type parameters
- T = Entity type (Measurement, Network, etc.)
- ID = Primary key type (Long, String, etc.)

---

**Javadoc breakdown:**

```java
/**
 * Generic repository exposing basic CRUD operations backed by the persistence
 * layer.
```
→ This class provides CRUD (Create, Read, Update, Delete) operations

```java
 * <p>
 * Concrete repositories extend/compose this class to centralise common database
 * access logic for all entities, as described in the README.
```
→ Other repositories (like MeasurementRepository) extend this to inherit functionality

```java
 * @param <T>  entity type
```
→ T represents the type of entity (e.g., Measurement, Network)

```java
 * @param <ID> identifier (primary key) type
```
→ ID represents the type of the primary key (e.g., Long for Measurement, String for Network)

---

### Field Declaration (Line 24)

```java
protected Class<T> entityClass;
```

**Breaking it down:**

```java
protected Class<T> entityClass;
└───┬───┘ └───┬──┘ └────┬────┘
    │         │         │
 Visibility  Type   Variable name
```

**`protected`:**
- Accessible within this class
- Accessible in subclasses (MeasurementRepository)
- NOT accessible from outside classes

**`Class<T>`:**
- This is the **class object** for type T
- Stores metadata about the class

**What is `Class<T>`?**

```java
// When you create:
CRUDRepository<Measurement, Long> repo = new CRUDRepository<>(Measurement.class);
                                                              └──────┬───────┘
                                                                     │
                                                         This is a Class<Measurement>

// The entityClass field stores: Measurement.class
// Which contains all information about the Measurement class:
//   - Class name: "Measurement"
//   - Fields: id, networkCode, gatewayCode, etc.
//   - Methods: getId(), getValue(), etc.
//   - Annotations: @Entity, @Id, etc.
```

**Why we need this:**

```java
// We need to tell JPA what type we're working with:
T entity = em.find(entityClass, id);
                   └────┬────┘
                        │
            "Find an object of type entityClass with this id"

// Without entityClass, JPA wouldn't know what table to query!
```

**Real example:**

```java
CRUDRepository<Network, String> repo = new CRUDRepository<>(Network.class);
// entityClass = Network.class

Network network = repo.read("NET_01");
// Internally: em.find(Network.class, "NET_01")
// JPA knows to query the Network table!
```

---

### Constructor (Lines 26-32)

```java
/**
 * Builds a repository for the given entity class.
 *
 * @param entityClass entity class handled by this repository
 */
public CRUDRepository(Class<T> entityClass) {
    this.entityClass = entityClass;  //ADDED FOR R1
}
```

**What this does:**
Initializes the repository with the entity class it will manage.

**Breaking down the constructor:**

```java
public CRUDRepository(Class<T> entityClass) {
       └──────┬──────┘ └─────┬─────────┘
              │               │
      Constructor name    Parameter
```

**Parameter:**
```java
Class<T> entityClass
```
- The class object for the entity type
- Example: `Measurement.class`, `Network.class`, `Gateway.class`

**Body:**
```java
this.entityClass = entityClass;
└───┬────┘         └─────┬────┘
    │                    │
 Field            Parameter value
```
- Stores the class object in the field
- Now all methods can use it

---

**Usage examples:**

```java
// Create repository for Measurement
CRUDRepository<Measurement, Long> measurementRepo = 
    new CRUDRepository<>(Measurement.class);
                        └──────┬───────┘
                               │
                    Passed to constructor
                    Stored in entityClass field

// Create repository for Network
CRUDRepository<Network, String> networkRepo = 
    new CRUDRepository<>(Network.class);
                        └────┬─────┘
                             │
                  Stored in entityClass field

// Create repository for Gateway
CRUDRepository<Gateway, String> gatewayRepo = 
    new CRUDRepository<>(Gateway.class);
```

---

**Why we need the constructor:**

Without it, methods wouldn't know what type to work with:

```java
// In read() method:
public T read(ID id) {
    EntityManager em = PersistenceManager.getEntityManager();
    T entity = em.find(???, id);  // What class should we find?
                        ↑
                   Need entityClass here!
    return entity;
}

// With entityClass from constructor:
public T read(ID id) {
    EntityManager em = PersistenceManager.getEntityManager();
    T entity = em.find(entityClass, id);  // ✅ Now we know!
    return entity;
}
```

---

### getEntityName Method (Lines 34-43)

```java
/**
 * Given an entity class retrieves the name of the entity to be used in the
 * queries.
 * 
 * @return the name of the entity (to be used in queries)
 */
protected String getEntityName() {
    Entity ea = entityClass.getAnnotation(jakarta.persistence.Entity.class);
    if (ea == null)
        throw new IllegalArgumentException("Class " + this.entityClass.getName() + " must be annotated as @Entity");
    if (ea.name().isEmpty())
        return this.entityClass.getSimpleName();
    return ea.name();
}
```

**What this does:**
Gets the name to use for this entity in JPQL queries.

**Why we need this:**

JPQL queries need the entity name:

```java
// JPQL query:
"SELECT e FROM Measurement e"
              └─────┬─────┘
                    │
              Entity name

// But what if entity has custom name?
@Entity(name = "WR_USER")
public class User { ... }

// Query should be:
"SELECT e FROM WR_USER e"  ← Custom name, not "User"!
```

---

**Line-by-line breakdown:**

#### Line 1: Get @Entity annotation

```java
Entity ea = entityClass.getAnnotation(jakarta.persistence.Entity.class);
└──┬─┘       └───────────┬──────────────────────────────────────────┘
   │                      │
Variable          Get annotation from class
```

**What is an annotation?**

```java
@Entity  // ← This is an annotation
public class Measurement {
    // ...
}

// Annotations are metadata attached to classes, methods, fields
// They can be read at runtime using reflection
```

**Getting the annotation:**

```java
Entity ea = entityClass.getAnnotation(Entity.class);

// If class has @Entity:
//   ea = Entity annotation object
//   ea.name() returns the entity name

// If class doesn't have @Entity:
//   ea = null
```

**Example:**

```java
// For Measurement:
@Entity
public class Measurement { ... }

Entity ea = Measurement.class.getAnnotation(Entity.class);
// ea = Entity annotation (not null)

// For regular class:
public class MyRegularClass { ... }

Entity ea = MyRegularClass.class.getAnnotation(Entity.class);
// ea = null (no @Entity annotation)
```

---

#### Line 2-3: Check if @Entity exists

```java
if (ea == null)
    throw new IllegalArgumentException("Class " + this.entityClass.getName() + " must be annotated as @Entity");
```

**What this does:**
Validates that the class has @Entity annotation.

**If not annotated:**
```java
CRUDRepository<MyRegularClass, Long> repo = new CRUDRepository<>(MyRegularClass.class);

// Later, when calling getEntityName():
Entity ea = MyRegularClass.class.getAnnotation(Entity.class);
// ea = null

if (ea == null)  // TRUE
    throw new IllegalArgumentException("Class MyRegularClass must be annotated as @Entity");

// Program crashes with clear error message ❌
```

**Why crash?**

```
CRUDRepository only works with JPA entities
If class is not @Entity, it can't be persisted
Better to fail fast with clear error message
Than to fail later with confusing database error
```

---

#### Line 4-5: Return simple name if no custom name

```java
if (ea.name().isEmpty())
    return this.entityClass.getSimpleName();
```

**What this does:**
If @Entity has no custom name, use the class's simple name.

**Example 1: No custom name**

```java
@Entity  // No name specified
public class Measurement { ... }

Entity ea = Measurement.class.getAnnotation(Entity.class);
ea.name()  // Returns "" (empty string)

if (ea.name().isEmpty())  // TRUE
    return this.entityClass.getSimpleName();  // "Measurement"
```

**Example 2: With custom name**

```java
@Entity(name = "WR_USER")  // Custom name specified
public class User { ... }

Entity ea = User.class.getAnnotation(Entity.class);
ea.name()  // Returns "WR_USER" (not empty)

if (ea.name().isEmpty())  // FALSE
// Skip to next line...
```

**getSimpleName() vs getName():**

```java
package com.weather.report.model.entities;

public class Measurement { ... }

Measurement.class.getSimpleName()  // "Measurement" (just the class name)
Measurement.class.getName()        // "com.weather.report.model.entities.Measurement" (full package path)

// For JPQL, we want simple name: "Measurement"
```

---

#### Line 6: Return custom name

```java
return ea.name();
```

**What this does:**
Returns the custom entity name if specified.

**Example:**

```java
@Entity(name = "WR_USER")
public class User { ... }

return ea.name();  // Returns "WR_USER"
```

---

**Complete flow:**

```
Step 1: Get @Entity annotation
    ↓
Step 2: Check if annotation exists
    ├─ No  → Throw exception (not an entity!)
    └─ Yes → Continue
    ↓
Step 3: Check if custom name specified
    ├─ No  → Return simple class name (e.g., "Measurement")
    └─ Yes → Return custom name (e.g., "WR_USER")
```

**Real examples:**

```java
// Example 1: Measurement (no custom name)
@Entity
public class Measurement { ... }

getEntityName() → "Measurement"

// Example 2: User (custom name)
@Entity(name = "WR_USER")
public class User { ... }

getEntityName() → "WR_USER"

// Example 3: Not an entity
public class MyClass { ... }

getEntityName() → IllegalArgumentException!
```

---

### create Method (Lines 45-61)

```java
/**
 * Persists a new entity instance.
 *
 * @param entity entity to persist
 * @return persisted entity
 */
public T create(T entity) {
    EntityManager em = PersistenceManager.getEntityManager();  //ADDED FOR R1
    EntityTransaction tx = em.getTransaction();                //ADDED FOR R1
    try {                                                      //ADDED FOR R1
        tx.begin();                                            //ADDED FOR R1
        em.persist(entity);                                    //ADDED FOR R1
        tx.commit();                                           //ADDED FOR R1
        return entity;                                         //ADDED FOR R1
    } catch (Exception e) {                                    //ADDED FOR R1
        if (tx.isActive()) {                                   //ADDED FOR R1
            tx.rollback();                                     //ADDED FOR R1
        }                                                      //ADDED FOR R1
        throw e;                                               //ADDED FOR R1
    } finally {                                                //ADDED FOR R1
        em.close();                                            //ADDED FOR R1
    }                                                          //ADDED FOR R1
}
```

**What this does:**
Saves a new entity to the database.

This is the **C** in CRUD (Create).

---

**Complete breakdown:**

#### Line 1: Method signature

```java
public T create(T entity) {
       │   │    └───┬───┘
       │   │        │
       │   │     Parameter
       │   └─ Method name
       └─ Return type (same as entity type)
```

**Generic type T:**
```java
// For Measurement:
public Measurement create(Measurement entity) { ... }

// For Network:
public Network create(Network entity) { ... }

// Same method works for all types!
```

---

#### Line 2: Get EntityManager

```java
EntityManager em = PersistenceManager.getEntityManager();
```

**What this does:**
Gets an EntityManager to interact with the database.

**EntityManager = Database connection + operations**

```java
EntityManager em = ...

// Now we can:
em.persist(entity);    // Save
em.find(class, id);    // Find
em.merge(entity);      // Update
em.remove(entity);     // Delete
em.createQuery(...);   // Query
em.getTransaction();   // Transactions
```

**Why create new EntityManager each time?**

```
EntityManager is NOT thread-safe
Each operation should have its own EntityManager
Create → Use → Close
```

---

#### Line 3: Get transaction

```java
EntityTransaction tx = em.getTransaction();
```

**What this does:**
Gets the transaction object for this EntityManager.

**Why we need transactions:**

```
Without transaction:
  em.persist(entity)  // Save
  [POWER OUTAGE]      // Database in inconsistent state! ❌

With transaction:
  tx.begin()
  em.persist(entity)  // Save
  [POWER OUTAGE]      // Transaction rolls back automatically
  Database unchanged (safe!) ✅
```

---

#### Line 4: Start try block

```java
try {
```

**What this does:**
Begins exception handling block.

**Why try-catch-finally?**

```java
try {
    // Normal operations
    // If any exception occurs, jump to catch
} catch (Exception e) {
    // Handle errors
    // Rollback transaction
} finally {
    // Always executed (success or failure)
    // Close resources
}
```

**Structure:**

```
try:      Execute main logic
          ↓
     Success? → finally → end
          ↓
      Error? → catch → finally → end
```

---

#### Line 5: Begin transaction

```java
tx.begin();
```

**What this does:**
Starts the database transaction.

**Transaction lifecycle:**

```
1. tx.begin()    ← Start
2. Operations     (persist, merge, remove)
3. tx.commit()   ← Finish (save changes)
   OR
   tx.rollback() ← Cancel (undo changes)
```

**Analogy: Draft email**

```
begin()   → Click "New Email"
          (Start composing)
          
persist() → Write email content
          (Make changes)
          
commit()  → Click "Send"
          (Changes saved permanently)
          
OR

rollback() → Click "Discard"
           (Changes thrown away)
```

---

#### Line 6: Persist entity

```java
em.persist(entity);
```

**What this does:**
Tells JPA to save this entity to the database.

**IMPORTANT:** This does NOT immediately write to database!

**What actually happens:**

```
em.persist(entity);
    ↓
Entity added to "persistence context"
    ↓
Marked as "needs to be saved"
    ↓
NOT YET in database!
    ↓
tx.commit()  ← NOW it's saved to database
```

**Why delay?**

```
Performance: Batch multiple operations
Safety: Can rollback if error occurs
Consistency: All-or-nothing (ACID)
```

**Example:**

```java
tx.begin();
em.persist(measurement1);  // Queued
em.persist(measurement2);  // Queued
em.persist(measurement3);  // Queued
tx.commit();               // All saved at once!

// Alternative with immediate save (slower):
save(measurement1);  // Database write 1
save(measurement2);  // Database write 2
save(measurement3);  // Database write 3
// 3 separate writes (slower)
```

---

#### Line 7: Commit transaction

```java
tx.commit();
```

**What this does:**
Commits the transaction, making all changes permanent.

**What happens during commit:**

```
1. JPA generates SQL:
   INSERT INTO Measurement (networkCode, gatewayCode, sensorCode, value, timestamp)
   VALUES ('NET_01', 'GW_0001', 'S_000001', 23.5, '2024-01-15 10:30:00');

2. Sends SQL to database

3. Database executes SQL

4. Changes are permanent ✓

5. Transaction ends
```

**Before vs After commit:**

```
BEFORE commit:
  Database: [Empty or old data]
  JPA: "I have entity to save"

AFTER commit:
  Database: [New data saved!]
  JPA: "Transaction complete"
```

---

#### Line 8: Return entity

```java
return entity;
```

**What this does:**
Returns the entity that was saved.

**Why return it?**

```java
Measurement m = new Measurement(...);
// m.id = null (not assigned yet)

Measurement saved = repo.create(m);
// saved.id = 47L (database assigned ID!)

// For entities with @GeneratedValue:
// Database assigns the ID
// We need to return the entity with its new ID
```

**Example:**

```java
// Create new measurement (no ID yet)
Measurement m = new Measurement("NET_01", "GW_0001", "S_000001", 23.5, timestamp);
System.out.println(m.getId());  // null

// Save to database
Measurement saved = repo.create(m);
System.out.println(saved.getId());  // 47L (database assigned!)

// Can now use the ID
System.out.println("Saved with ID: " + saved.getId());
```

---

#### Line 9: Catch exceptions

```java
} catch (Exception e) {
```

**What this does:**
Catches any exception that occurs during the try block.

**Why catch Exception?**

```
Many things can go wrong:
• Database connection lost
• Constraint violation (duplicate ID)
• Disk full
• Invalid data
• etc.

catch (Exception e) catches ALL of them
```

**What happens when exception occurs:**

```
try {
    tx.begin();
    em.persist(entity);
    tx.commit();  // Error here!
    return entity;  // ← Never executed
} catch (Exception e) {  // ← Jump here!
    // Handle error
}
```

---

#### Line 10-12: Rollback transaction

```java
if (tx.isActive()) {
    tx.rollback();
}
```

**What this does:**
Rolls back the transaction if it's still active.

**Why check isActive()?**

```java
tx.begin();          // Transaction active
em.persist(entity);
tx.commit();         // Success → Transaction no longer active

// If exception before commit:
tx.begin();          // Transaction active
em.persist(entity);  // Error!
// tx is still active! Need to rollback!

// If exception after commit:
tx.begin();
em.persist(entity);
tx.commit();         // Success → Transaction completed
// Some other error  // But tx already completed
// tx.isActive() = false, don't rollback
```

**The check:**

```java
if (tx.isActive()) {  // Is transaction still in progress?
    tx.rollback();     // Yes → Cancel it
}
// If transaction already completed, do nothing
```

**Why rollback?**

```
Scenario: Saving network with invalid data

tx.begin();
em.persist(network1);  // OK
em.persist(network2);  // OK
em.persist(network3);  // ERROR! (constraint violation)

WITHOUT rollback:
  network1 and network2 saved (partial save) ❌
  Inconsistent database state!

WITH rollback:
  tx.rollback();
  ALL changes undone ✅
  Database unchanged (consistent!)
```

---

#### Line 13: Re-throw exception

```java
throw e;
```

**What this does:**
Re-throws the exception after rollback.

**Why re-throw?**

```java
public T create(T entity) {
    try {
        // ... save entity ...
    } catch (Exception e) {
        tx.rollback();
        throw e;  // ← Let caller know there was an error!
    }
}

// Caller code:
try {
    repo.create(measurement);
} catch (Exception e) {
    System.err.println("Failed to save: " + e.getMessage());
}

// If we don't re-throw:
public T create(T entity) {
    try {
        // ...
    } catch (Exception e) {
        tx.rollback();
        // Nothing here? Exception swallowed! ❌
    }
}

// Caller thinks save succeeded:
repo.create(measurement);  // Actually failed!
System.out.println("Saved successfully!");  // Wrong! ❌
```

**The flow:**

```
repo.create(entity)
    ↓
Exception occurs
    ↓
catch block executes
    ↓
tx.rollback() (cleanup)
    ↓
throw e (notify caller)
    ↓
Caller's catch block
    ↓
Caller handles error
```

---

#### Line 14: Finally block

```java
} finally {
```

**What this does:**
Code in finally block ALWAYS executes, regardless of success or exception.

**Finally execution:**

```java
try {
    // Code
    return entity;  ← Even if return here, finally still runs!
} catch (Exception e) {
    throw e;  ← Even if exception thrown, finally still runs!
} finally {
    em.close();  ← ALWAYS executes!
}
```

**Scenarios:**

```
Scenario 1: Success
  try → return entity → finally → em.close() → end

Scenario 2: Exception
  try → error → catch → rollback → throw → finally → em.close() → end

Scenario 3: Return early
  try → return → finally → em.close() → actual return

ALWAYS: finally block runs!
```

---

#### Line 15: Close EntityManager

```java
em.close();
```

**What this does:**
Closes the EntityManager, releasing database resources.

**Why closing is CRITICAL:**

```
EntityManager holds:
• Database connection
• Memory buffers
• Transaction resources

WITHOUT close():
  Resources leak ❌
  Database connections exhausted
  System runs out of resources
  Application crashes!

WITH close():
  Resources released ✅
  Connection returned to pool
  Memory freed
  System healthy!
```

**Analogy: Library book**

```
try {
    Book book = library.borrowBook();  // Borrow
    book.read();                       // Use
    return book.getSummary();
} finally {
    library.returnBook(book);          // MUST return!
}

// If you don't return books, library runs out!
```

**Why in finally?**

```
finally guarantees execution:

Success case:
  em = get EntityManager
  persist entity
  commit
  em.close() ← Executed ✓

Error case:
  em = get EntityManager
  persist entity
  ERROR!
  rollback
  em.close() ← Still executed! ✓

Without finally:
  em = get EntityManager
  persist entity
  ERROR!
  em.close() ← NEVER executed! ❌ (resource leak!)
```

---

**Complete create() flow visualized:**

```
┌─────────────────────────────────────────────────────┐
│ create(entity)                                      │
├─────────────────────────────────────────────────────┤
│                                                     │
│ 1. Get EntityManager                               │
│    em = PersistenceManager.getEntityManager()      │
│                                                     │
│ 2. Get Transaction                                 │
│    tx = em.getTransaction()                        │
│                                                     │
│ 3. try {                                           │
│    4. Begin transaction                            │
│       tx.begin()                                   │
│                                                     │
│    5. Persist entity                               │
│       em.persist(entity)                           │
│       (Entity queued for save)                     │
│                                                     │
│    6. Commit transaction                           │
│       tx.commit()                                  │
│       (SQL generated and executed)                 │
│       (Entity saved to database!)                  │
│                                                     │
│    7. Return entity                                │
│       return entity                                │
│ }                                                   │
│                                                     │
│ catch (Exception e) {                              │
│    8. Check if transaction active                  │
│       if (tx.isActive())                          │
│                                                     │
│    9. Rollback transaction                         │
│       tx.rollback()                                │
│       (Undo all changes)                           │
│                                                     │
│    10. Re-throw exception                          │
│        throw e                                     │
│        (Notify caller of error)                    │
│ }                                                   │
│                                                     │
│ finally {                                           │
│    11. Close EntityManager                         │
│        em.close()                                  │
│        (Release resources - ALWAYS executed!)      │
│ }                                                   │
└─────────────────────────────────────────────────────┘
```

**Success path:**
```
1 → 2 → 3 → 4 → 5 → 6 → 7 → 11 → Return entity ✅
```

**Error path:**
```
1 → 2 → 3 → 4 → 5 → ERROR → 8 → 9 → 10 → 11 → Throw exception ❌
```

---

### read(ID id) Method (Lines 63-71)

```java
/**
 * Reads a single entity by identifier.
 *
 * @param id entity identifier (primary key)
 * @return found entity or {@code null} if absent
 */
public T read(ID id) {
    EntityManager em = PersistenceManager.getEntityManager(); //ADDED FOR R1
    try { //ADDED FOR R1
        T entity = em.find(entityClass, id); //ADDED FOR R1
        return entity; //ADDED FOR R1
    } finally { //ADDED FOR R1
        em.close(); //ADDED FOR R1
    } //ADDED FOR R1
}
```

**What this does:**
Finds and returns a single entity by its primary key (ID).

This is part of the **R** in CRUD (Read).

---

**Complete breakdown:**

#### Line 1: Method signature

```java
public T read(ID id) {
       │  │   └─┬─┘
       │  │     │
       │  │  Parameter (ID type)
       │  └─ Method name
       └─ Return type (entity type or null)
```

**Generic types:**

```java
// For Measurement (ID = Long):
public Measurement read(Long id) { ... }

// For Network (ID = String):
public Network read(String id) { ... }
```

**Usage:**

```java
CRUDRepository<Measurement, Long> repo = ...;
Measurement m = repo.read(47L);  // Find measurement with ID 47

CRUDRepository<Network, String> repo = ...;
Network n = repo.read("NET_01");  // Find network with code "NET_01"
```

---

#### Line 2: Get EntityManager

```java
EntityManager em = PersistenceManager.getEntityManager();
```

**Same as create():**
- Get EntityManager for database operations
- Fresh EntityManager for this operation
- Will be closed in finally block

---

#### Line 3: Start try block

```java
try {
```

**Why try-finally (no catch)?**

```java
try {
    // Read operation (might fail)
} finally {
    em.close();  // MUST close EntityManager
}

// No catch block because:
// - Read doesn't modify database (no need for rollback)
// - If error occurs, let it propagate to caller
// - Caller can decide how to handle
```

---

#### Line 4: Find entity

```java
T entity = em.find(entityClass, id);
```

**What this does:**
Finds an entity by its primary key.

**em.find() explained:**

```java
em.find(Class<T> entityClass, Object primaryKey)
        └────┬────────────┘  └───────┬──────┘
             │                        │
      What type to find?        What ID to find?
```

**How it works:**

```java
// 1. JPA generates SQL:
SELECT * FROM Measurement WHERE id = 47;

// 2. Database executes query

// 3. If found:
//    JPA creates Measurement object from row
//    Returns the object

// 4. If not found:
//    Returns null
```

**Examples:**

```java
// Example 1: Find measurement
CRUDRepository<Measurement, Long> repo = new CRUDRepository<>(Measurement.class);
Measurement m = repo.read(47L);

// Internally:
em.find(Measurement.class, 47L)
    ↓
SQL: SELECT * FROM Measurement WHERE id = 47;
    ↓
Result: Measurement{id: 47, value: 23.5, ...} or null

// Example 2: Find network
CRUDRepository<Network, String> repo = new CRUDRepository<>(Network.class);
Network n = repo.read("NET_01");

// Internally:
em.find(Network.class, "NET_01")
    ↓
SQL: SELECT * FROM Network WHERE code = 'NET_01';
    ↓
Result: Network{code: "NET_01", name: "City Network", ...} or null
```

---

#### Line 5: Return entity

```java
return entity;
```

**What this returns:**

```java
// If entity found:
Measurement{id: 47, networkCode: "NET_01", value: 23.5, ...}

// If entity not found:
null
```

**Caller must check for null:**

```java
Measurement m = repo.read(47L);
if (m != null) {
    System.out.println("Found: " + m.getValue());
} else {
    System.out.println("Measurement not found");
}
```

---

#### Line 6: Finally block

```java
} finally {
```

**Always executes:**

```java
// Success:
entity = em.find(...)  // Found
return entity          // Return
finally                // ← Executes before actual return
em.close()            // Close

// Not found:
entity = em.find(...)  // null
return entity          // Return null
finally                // ← Executes before actual return
em.close()            // Close

// Error:
entity = em.find(...)  // Exception!
finally                // ← Executes before exception propagates
em.close()            // Close
throw exception        // Then throw
```

---

#### Line 7: Close EntityManager

```java
em.close();
```

**Why close?**

Same reason as create():
- Release database connection
- Free memory
- Prevent resource leaks

**Simplified vs Full:**

```java
// read() - Simplified (read-only)
try {
    return em.find(...);
} finally {
    em.close();
}

// create() - Full (write operation)
try {
    tx.begin();
    em.persist(...);
    tx.commit();
    return entity;
} catch (Exception e) {
    tx.rollback();
    throw e;
} finally {
    em.close();
}

// Difference: read() doesn't need transactions!
```

---

**Why no transaction for read()?**

```
READ operations don't modify database:
  • No need to commit changes (no changes!)
  • No need to rollback (nothing to undo!)
  • Simpler code (just get and return)

WRITE operations modify database:
  • Need transaction for ACID properties
  • Need rollback capability
  • More complex error handling
```

---

**Complete read() flow:**

```
┌─────────────────────────────────────────┐
│ read(id)                                │
├─────────────────────────────────────────┤
│                                         │
│ 1. Get EntityManager                   │
│    em = PersistenceManager....()       │
│                                         │
│ 2. try {                               │
│    3. Find entity by ID                │
│       entity = em.find(entityClass, id)│
│       (JPA generates and executes SQL) │
│       (Returns entity or null)         │
│                                         │
│    4. Return entity                    │
│       return entity                    │
│ }                                       │
│                                         │
│ finally {                               │
│    5. Close EntityManager              │
│       em.close()                       │
│       (Always executed!)               │
│ }                                       │
└─────────────────────────────────────────┘
```

**Flow:**
```
1 → 2 → 3 → 4 → 5 → Return entity (or null)
```

---

### read() Method - Get All Entities (Lines 73-82)

```java
/**
 * Reads all entities of the managed type.
 *
 * @return list of all entities
 */
public List<T> read() {
    EntityManager em = PersistenceManager.getEntityManager();  //ADDED FOR R1
    try {                                                      //ADDED FOR R1
        String jpql = "SELECT e FROM " + getEntityName() + " e";  //ADDED FOR R1
        TypedQuery<T> query = em.createQuery(jpql, entityClass);  //ADDED FOR R1
        List<T> result = query.getResultList();                //ADDED FOR R1
        return result;                                         //ADDED FOR R1
    } finally {                                                //ADDED FOR R1
        em.close();                                            //ADDED FOR R1
    }                                                          //ADDED FOR R1
}
```

**What this does:**
Retrieves ALL entities of a given type from the database.

This is also part of the **R** in CRUD (Read).

---

**Complete breakdown:**

#### Line 1: Method signature

```java
public List<T> read() {
       └───┬──┘  │
           │     └─ No parameters (get ALL)
           └─ Returns list of entities
```

**Return type:**

```java
List<T>  // List of entity type

// For Measurement:
List<Measurement>

// For Network:
List<Network>
```

**Usage:**

```java
CRUDRepository<Measurement, Long> repo = ...;
List<Measurement> allMeasurements = repo.read();
// [Measurement1, Measurement2, Measurement3, ...]

CRUDRepository<Network, String> repo = ...;
List<Network> allNetworks = repo.read();
// [Network1, Network2, Network3, ...]
```

---

#### Line 2: Get EntityManager

```java
EntityManager em = PersistenceManager.getEntityManager();
```

Same as previous methods - get fresh EntityManager.

---

#### Line 3: Start try block

```java
try {
```

Will be paired with finally to ensure em.close() is called.

---

#### Line 4: Build JPQL query

```java
String jpql = "SELECT e FROM " + getEntityName() + " e";
```

**What is JPQL?**

**JPQL** = **J**ava **P**ersistence **Q**uery **L**anguage

```
JPQL is like SQL, but for Java objects:

SQL:  SELECT * FROM measurement_table WHERE id = 47;
      (Works with tables and columns)

JPQL: SELECT e FROM Measurement e WHERE e.id = 47;
      (Works with entities and fields)
```

**Breaking down the query:**

```java
"SELECT e FROM " + getEntityName() + " e"
```

**For Measurement:**

```java
getEntityName() = "Measurement"

jpql = "SELECT e FROM Measurement e"
        └──┬─┘      └─────┬─────┘ │
           │               │       └─ Alias (variable name)
           │               └─ Entity name
           └─ What to select (entire entity)
```

**JPQL vs SQL comparison:**

```
JPQL:  SELECT e FROM Measurement e
       ↓ JPA translates ↓
SQL:   SELECT id, network_code, gateway_code, sensor_code, value, timestamp
       FROM Measurement;
```

**The alias 'e':**

```java
SELECT e FROM Measurement e
                          │
                          └─ 'e' is an alias (like a variable)

// We can use it for conditions:
SELECT e FROM Measurement e WHERE e.value > 30
                                  │
                                  └─ Reference fields using alias
```

---

#### Line 5: Create typed query

```java
TypedQuery<T> query = em.createQuery(jpql, entityClass);
```

**What this does:**
Creates a type-safe query that returns entities of type T.

**Breaking it down:**

```java
em.createQuery(jpql, entityClass)
               │     └─────┬─────┘
               │           │
          JPQL string   Entity class
```

**TypedQuery vs Query:**

```java
// Without type safety (old way):
Query query = em.createQuery("SELECT e FROM Measurement e");
List results = query.getResultList();  // Raw List (no type!)
Measurement m = (Measurement) results.get(0);  // Need cast ❌

// With type safety (our way):
TypedQuery<Measurement> query = em.createQuery("SELECT e FROM Measurement e", Measurement.class);
List<Measurement> results = query.getResultList();  // List<Measurement>
Measurement m = results.get(0);  // No cast needed! ✅
```

**Why pass entityClass?**

```java
TypedQuery<T> query = em.createQuery(jpql, entityClass);
                                           └────┬────┘
                                                │
                          Tells JPA what type to return

// For Measurement:
TypedQuery<Measurement> query = em.createQuery(jpql, Measurement.class);
// Returns: List<Measurement>

// For Network:
TypedQuery<Network> query = em.createQuery(jpql, Network.class);
// Returns: List<Network>
```

---

#### Line 6: Execute query

```java
List<T> result = query.getResultList();
```

**What this does:**
Executes the query and returns all results as a list.

**What happens:**

```
1. JPA sends JPQL to Hibernate

2. Hibernate translates JPQL to SQL:
   SELECT * FROM Measurement;

3. Database executes SQL

4. Database returns rows:
   | id | networkCode | gatewayCode | sensorCode | value | timestamp |
   |----|-------------|-------------|------------|-------|-----------|
   | 1  | NET_01      | GW_0001     | S_000001   | 23.5  | ...       |
   | 2  | NET_01      | GW_0001     | S_000001   | 24.2  | ...       |
   | 3  | NET_01      | GW_0002     | S_000002   | 18.7  | ...       |

5. JPA converts each row to Measurement object:
   [
     Measurement{id:1, networkCode:"NET_01", value:23.5, ...},
     Measurement{id:2, networkCode:"NET_01", value:24.2, ...},
     Measurement{id:3, networkCode:"NET_01", value:18.7, ...}
   ]

6. Returns List<Measurement>
```

**Empty results:**

```java
// If no entities exist:
List<T> result = query.getResultList();
// result = [] (empty list, NOT null!)

// Safe to iterate:
for (T entity : result) {
    // Won't execute if empty
}
```

---

#### Line 7: Return result

```java
return result;
```

**Returns:**
- Empty list `[]` if no entities exist
- List of entities if found

**Never returns null!**

---

#### Line 8-9: Finally and close

```java
} finally {
    em.close();
}
```

Same as before - always close EntityManager to release resources.

---

**Complete read() flow:**

```
┌──────────────────────────────────────────────────────┐
│ read()  (get ALL entities)                          │
├──────────────────────────────────────────────────────┤
│                                                      │
│ 1. Get EntityManager                                │
│    em = PersistenceManager....()                    │
│                                                      │
│ 2. try {                                            │
│    3. Build JPQL query                              │
│       jpql = "SELECT e FROM " + getEntityName() + " e" │
│       Example: "SELECT e FROM Measurement e"        │
│                                                      │
│    4. Create typed query                            │
│       query = em.createQuery(jpql, entityClass)     │
│       (TypedQuery<T> for type safety)               │
│                                                      │
│    5. Execute query                                 │
│       result = query.getResultList()                │
│       (JPA → SQL → Database → Java objects)         │
│                                                      │
│    6. Return list                                   │
│       return result                                 │
│       (List<T> containing all entities)             │
│ }                                                    │
│                                                      │
│ finally {                                            │
│    7. Close EntityManager                           │
│       em.close()                                    │
│ }                                                    │
└──────────────────────────────────────────────────────┘
```

---

### update Method (Lines 84-99)

```java
/**
 * Updates an existing entity.
 *
 * @param entity entity with new state
 * @return updated entity
 */
public T update(T entity) {
    EntityManager em = PersistenceManager.getEntityManager(); //ADDED FOR R1
    EntityTransaction tx = em.getTransaction();               //ADDED FOR R1
    try {                                                     //ADDED FOR R1
        tx.begin();                                           //ADDED FOR R1
        T merged = em.merge(entity);                          //ADDED FOR R1
        tx.commit();                                          //ADDED FOR R1
        return merged;                                        //ADDED FOR R1
    } catch (Exception e) {                                   //ADDED FOR R1
        if (tx.isActive()) {                                  //ADDED FOR R1
            tx.rollback();                                    //ADDED FOR R1
        }                                                     //ADDED FOR R1
        throw e;                                              //ADDED FOR R1
    } finally {                                               //ADDED FOR R1
        em.close();                                           //ADDED FOR R1
    }                                                         //ADDED FOR R1
}
```

**What this does:**
Updates an existing entity in the database.

This is the **U** in CRUD (Update).

---

**Structure:**

Very similar to create(), but uses `em.merge()` instead of `em.persist()`.

```
create():  em.persist(entity)  → Insert new row
update():  em.merge(entity)    → Update existing row
```

---

**Complete breakdown:**

#### Lines 1-3: Setup

```java
public T update(T entity) {
    EntityManager em = PersistenceManager.getEntityManager();
    EntityTransaction tx = em.getTransaction();
```

Same as create() - get EntityManager and transaction.

---

#### Lines 4-7: Update operation

```java
try {
    tx.begin();
    T merged = em.merge(entity);
    tx.commit();
    return merged;
}
```

**Key difference: em.merge() instead of em.persist()**

**What is merge()?**

```java
T merged = em.merge(entity);
```

**em.merge() explained:**

```
merge() synchronizes entity state with database:

1. If entity exists in database:
   → Update it with new values

2. If entity doesn't exist:
   → Insert it as new

3. Returns managed entity (attached to EntityManager)
```

**persist() vs merge():**

```java
// persist() - For NEW entities only
Measurement m = new Measurement(...);  // New, no ID
em.persist(m);  // Insert into database

// merge() - For EXISTING entities (or detached)
Measurement m = repo.read(47L);  // Get from database
m.setValue(25.3);                // Modify
em.merge(m);                     // Update database
```

---

**Why return merged entity?**

```java
T merged = em.merge(entity);
return merged;  // Return the MERGED entity, not the original!
```

**The difference:**

```java
// Original entity (detached)
Measurement m = new Measurement(...);
m.setId(47L);
m.setValue(25.3);

// After merge
Measurement merged = em.merge(m);

// 'm' is still detached (not managed by EntityManager)
// 'merged' is managed (attached to EntityManager)

// Use 'merged' for further operations:
merged.setValue(30.0);  // This change is tracked
m.setValue(30.0);       // This change is NOT tracked
```

---

**Update flow example:**

```java
// 1. Get existing measurement
CRUDRepository<Measurement, Long> repo = ...;
Measurement m = repo.read(47L);
System.out.println(m.getValue());  // 23.5

// 2. Modify it
m.setValue(25.3);

// 3. Update in database
Measurement updated = repo.update(m);

// 4. Verify change
Measurement check = repo.read(47L);
System.out.println(check.getValue());  // 25.3 ✅
```

**Generated SQL:**

```sql
UPDATE Measurement
SET value = 25.3,
    timestamp = '2024-01-15 11:00:00'
WHERE id = 47;
```

---

#### Lines 8-13: Error handling

```java
} catch (Exception e) {
    if (tx.isActive()) {
        tx.rollback();
    }
    throw e;
} finally {
    em.close();
}
```

**Same as create():**
- Catch exceptions
- Rollback if transaction active
- Re-throw exception
- Always close EntityManager

---

**Complete update() flow:**

```
┌──────────────────────────────────────────────────────┐
│ update(entity)                                       │
├──────────────────────────────────────────────────────┤
│                                                      │
│ 1. Get EntityManager and Transaction                │
│                                                      │
│ 2. try {                                            │
│    3. Begin transaction                             │
│       tx.begin()                                    │
│                                                      │
│    4. Merge entity                                  │
│       merged = em.merge(entity)                     │
│       (JPA detects changes and updates database)    │
│                                                      │
│    5. Commit transaction                            │
│       tx.commit()                                   │
│       SQL: UPDATE table SET ... WHERE id = ...      │
│                                                      │
│    6. Return merged entity                          │
│       return merged                                 │
│ }                                                    │
│                                                      │
│ catch (Exception e) {                               │
│    7. Rollback if needed                            │
│    8. Re-throw exception                            │
│ }                                                    │
│                                                      │
│ finally {                                            │
│    9. Close EntityManager                           │
│ }                                                    │
└──────────────────────────────────────────────────────┘
```

---

### delete Method (Lines 101-119)

```java
/**
 * Deletes an entity by identifier (primary key).
 *
 * @param id entity identifier (primary key)
 * @return deleted entity
 */
public T delete(ID id) {
    EntityManager em = PersistenceManager.getEntityManager(); //ADDED FOR R1
    EntityTransaction tx = em.getTransaction();               //ADDED FOR R1
    try {                                                     //ADDED FOR R1
        tx.begin();                                           //ADDED FOR R1
        T entity = em.find(entityClass, id);                  //ADDED FOR R1
        if (entity != null) {                                 //ADDED FOR R1
            em.remove(entity);                                //ADDED FOR R1
        }                                                     //ADDED FOR R1
        tx.commit();                                          //ADDED FOR R1
        return entity;                                        //ADDED FOR R1
    } catch (Exception e) {                                   //ADDED FOR R1
        if (tx.isActive()) {                                  //ADDED FOR R1
            tx.rollback();                                    //ADDED FOR R1
        }                                                     //ADDED FOR R1
        throw e;                                              //ADDED FOR R1
    } finally {                                               //ADDED FOR R1
        em.close();                                           //ADDED FOR R1
    }                                                         //ADDED FOR R1
}
```

**What this does:**
Deletes an entity from the database by its ID.

This is the **D** in CRUD (Delete).

---

**Complete breakdown:**

#### Lines 1-3: Setup

```java
public T delete(ID id) {
    EntityManager em = PersistenceManager.getEntityManager();
    EntityTransaction tx = em.getTransaction();
```

Same as create() and update().

---

#### Lines 4-10: Delete operation

```java
try {
    tx.begin();
    T entity = em.find(entityClass, id);
    if (entity != null) {
        em.remove(entity);
    }
    tx.commit();
    return entity;
}
```

**Step-by-step:**

#### Step 1: Begin transaction

```java
tx.begin();
```

Need transaction because we're modifying the database.

---

#### Step 2: Find entity

```java
T entity = em.find(entityClass, id);
```

**Why find first?**

```
em.remove() requires a MANAGED entity

Managed entity = Entity currently tracked by EntityManager

To delete:
  1. Find entity (makes it managed)
  2. Remove entity (delete from database)
```

**Can't do this:**

```java
// This won't work:
Measurement m = new Measurement();
m.setId(47L);
em.remove(m);  // ❌ Entity not managed!
```

**Must do this:**

```java
// This works:
Measurement m = em.find(Measurement.class, 47L);  // Find = managed
em.remove(m);  // ✅ Entity is managed, can remove
```

---

#### Step 3: Check if entity exists

```java
if (entity != null) {
    em.remove(entity);
}
```

**Why check for null?**

```java
// Scenario: Entity doesn't exist
Measurement m = repo.delete(999L);
// ID 999 doesn't exist

entity = em.find(..., 999L);  // entity = null

if (entity != null) {  // FALSE
    em.remove(entity);  // Skipped
}

tx.commit();  // Commit (no changes made)
return entity;  // return null
```

**Without the check:**

```java
entity = em.find(..., 999L);  // null
em.remove(entity);  // NullPointerException! ❌
```

---

#### Step 4: Remove entity

```java
em.remove(entity);
```

**What this does:**
Marks entity for deletion.

**Flow:**

```
em.remove(entity)
    ↓
Entity marked for deletion
    ↓
NOT YET deleted from database!
    ↓
tx.commit()
    ↓
SQL: DELETE FROM Measurement WHERE id = 47;
    ↓
Entity deleted from database ✅
```

---

#### Step 5: Commit and return

```java
tx.commit();
return entity;
```

**What we return:**

```java
// If entity existed:
return entity;  // The deleted entity object

// If entity didn't exist:
return null;    // Nothing to delete
```

**Why return the deleted entity?**

```java
// Caller might want to log what was deleted:
Measurement deleted = repo.delete(47L);
if (deleted != null) {
    System.out.println("Deleted measurement: " + deleted.getValue());
} else {
    System.out.println("Measurement not found");
}
```

---

#### Lines 11-16: Error handling

```java
} catch (Exception e) {
    if (tx.isActive()) {
        tx.rollback();
    }
    throw e;
} finally {
    em.close();
}
```

Same as create() and update().

---

**Complete delete() flow:**

```
┌──────────────────────────────────────────────────────┐
│ delete(id)                                           │
├──────────────────────────────────────────────────────┤
│                                                      │
│ 1. Get EntityManager and Transaction                │
│                                                      │
│ 2. try {                                            │
│    3. Begin transaction                             │
│       tx.begin()                                    │
│                                                      │
│    4. Find entity by ID                             │
│       entity = em.find(entityClass, id)             │
│                                                      │
│    5. Check if entity exists                        │
│       if (entity != null) {                         │
│                                                      │
│    6. Remove entity                                 │
│       em.remove(entity)                             │
│       (Mark for deletion)                           │
│       }                                              │
│                                                      │
│    7. Commit transaction                            │
│       tx.commit()                                   │
│       SQL: DELETE FROM table WHERE id = ...         │
│                                                      │
│    8. Return entity                                 │
│       return entity (or null if not found)          │
│ }                                                    │
│                                                      │
│ catch (Exception e) {                               │
│    9. Rollback if needed                            │
│    10. Re-throw exception                           │
│ }                                                    │
│                                                      │
│ finally {                                            │
│    11. Close EntityManager                          │
│ }                                                    │
└──────────────────────────────────────────────────────┘
```

---

## JPA and Hibernate Concepts

### What is JPA?

**JPA** = **J**ava **P**ersistence **A**PI

```
JPA is a SPECIFICATION (interface/contract)
  • Defines how to map Java objects to database tables
  • Defines standard operations (persist, find, merge, remove)
  • Defines query language (JPQL)
  
JPA is NOT an implementation!
  • Just defines what should be done
  • Not how to do it
```

**Analogy:**

```
JPA = Electrical socket standard
  • Defines: 2 holes, 110V/220V, specific size
  • Any device following standard will work
  
Hibernate/EclipseLink = Actual socket manufacturers
  • Implement the standard
  • Work with any device (code) following standard
```

---

### What is Hibernate?

**Hibernate** is an **implementation** of JPA.

```
Hibernate:
  • Implements all JPA specifications
  • Adds extra features beyond JPA
  • Most popular JPA implementation
  • What our project uses
```

**The relationship:**

```
Your Code
    ↓ uses
JPA Interface (jakarta.persistence.*)
    ↓ implemented by
Hibernate
    ↓ generates
SQL
    ↓ executes on
H2 Database
```

---

### Key JPA/Hibernate Concepts

#### 1. Entity

```java
@Entity  // Marks class as database table
public class Measurement {
    @Id
    @GeneratedValue
    private Long id;  // Primary key
    
    private String networkCode;  // Column
    private double value;        // Column
}

// Database table:
CREATE TABLE Measurement (
    id BIGINT PRIMARY KEY,
    network_code VARCHAR(255),
    value DOUBLE
);
```

---

#### 2. EntityManager

```java
EntityManager em = ...;

// Your database assistant:
em.persist(entity);    // Save
em.find(Class, id);    // Find
em.merge(entity);      // Update
em.remove(entity);     // Delete
em.createQuery(jpql);  // Query
```

---

#### 3. Persistence Context

```
Persistence Context = EntityManager's memory

Managed entities:
  • Tracked by EntityManager
  • Changes automatically detected
  • Synchronized with database

Detached entities:
  • Not tracked
  • Changes not detected
  • Need merge() to sync
```

**Example:**

```java
// Managed
EntityManager em = ...;
Measurement m = em.find(Measurement.class, 47L);
m.setValue(25.3);  // Change tracked automatically!
tx.commit();       // Saves change ✅

em.close();        // EntityManager closed
// Now 'm' is DETACHED (no longer tracked)

m.setValue(30.0);  // Change NOT tracked! ❌
// Need to merge:
em = ...;
em.merge(m);      // Sync changes ✅
```

---

#### 4. Transactions

```
Transaction = All-or-nothing unit of work

ACID properties:
  A - Atomic:     All operations succeed or all fail
  C - Consistent: Database rules always maintained
  I - Isolated:   Transactions don't interfere
  D - Durable:    Committed data persists
```

---

#### 5. JPQL

```
JPQL = Query language for entities (not tables)

JPQL:  SELECT m FROM Measurement m WHERE m.value > 30
       (Entity names, field names)

SQL:   SELECT * FROM measurement WHERE value > 30
       (Table names, column names)
```

---

## Real-World Examples

### Example 1: Complete CRUD Operations on Measurement

```java
// Setup
CRUDRepository<Measurement, Long> repo = new CRUDRepository<>(Measurement.class);

// CREATE
Measurement m = new Measurement("NET_01", "GW_0001", "S_000001", 23.5, timestamp);
Measurement saved = repo.create(m);
System.out.println("Created with ID: " + saved.getId());  // 47

// READ (single)
Measurement found = repo.read(47L);
System.out.println("Value: " + found.getValue());  // 23.5

// READ (all)
List<Measurement> all = repo.read();
System.out.println("Total measurements: " + all.size());  // 1

// UPDATE
found.setValue(25.3);
Measurement updated = repo.update(found);
System.out.println("Updated value: " + updated.getValue());  // 25.3

// DELETE
Measurement deleted = repo.delete(47L);
System.out.println("Deleted: " + (deleted != null));  // true

// Verify deletion
Measurement check = repo.read(47L);
System.out.println("After delete: " + check);  // null
```

---

### Example 2: Network Management

```java
// Setup
CRUDRepository<Network, String> repo = new CRUDRepository<>(Network.class);

// CREATE network
Network network = new Network();
network.setCode("NET_01");
network.setName("City Network");
network.setDescription("Main city monitoring network");
Network created = repo.create(network);

// READ network
Network found = repo.read("NET_01");
System.out.println("Network: " + found.getName());  // City Network

// UPDATE network
found.setDescription("Updated description");
Network updated = repo.update(found);

// GET ALL networks
List<Network> allNetworks = repo.read();
for (Network n : allNetworks) {
    System.out.println("Code: " + n.getCode() + ", Name: " + n.getName());
}

// DELETE network
Network deleted = repo.delete("NET_01");
System.out.println("Deleted: " + deleted.getCode());  // NET_01
```

---

### Example 3: Batch Operations

```java
CRUDRepository<Measurement, Long> repo = new CRUDRepository<>(Measurement.class);

// Create multiple measurements
List<Measurement> measurements = Arrays.asList(
    new Measurement("NET_01", "GW_0001", "S_000001", 23.5, timestamp1),
    new Measurement("NET_01", "GW_0001", "S_000001", 24.2, timestamp2),
    new Measurement("NET_01", "GW_0001", "S_000001", 25.1, timestamp3)
);

for (Measurement m : measurements) {
    repo.create(m);
}

// Read all and process
List<Measurement> all = repo.read();
double average = all.stream()
    .mapToDouble(Measurement::getValue)
    .average()
    .orElse(0.0);

System.out.println("Average: " + average);  // 24.27
```

---

### Example 4: Error Handling

```java
CRUDRepository<Network, String> repo = new CRUDRepository<>(Network.class);

// Try to create network with duplicate ID
Network n1 = new Network();
n1.setCode("NET_01");
n1.setName("Network 1");
repo.create(n1);  // ✅ Success

Network n2 = new Network();
n2.setCode("NET_01");  // Same code!
n2.setName("Network 2");

try {
    repo.create(n2);  // ❌ Will fail (duplicate key)
} catch (Exception e) {
    System.err.println("Failed to create: " + e.getMessage());
    // Transaction was rolled back automatically
    // Database unchanged ✅
}

// Verify only first network exists
List<Network> all = repo.read();
System.out.println("Networks: " + all.size());  // 1
```

---

## Common Questions and Answers

### Q1: Why use generic types <T, ID> instead of separate classes?

**A:** Code reusability and maintainability!

**Without generics (BAD):**

```java
// MeasurementRepository.java (200 lines)
public class MeasurementRepository {
    public Measurement create(Measurement m) { ... }
    public Measurement read(Long id) { ... }
    public List<Measurement> read() { ... }
    public Measurement update(Measurement m) { ... }
    public Measurement delete(Long id) { ... }
}

// NetworkRepository.java (200 lines)
public class NetworkRepository {
    public Network create(Network n) { ... }
    public Network read(String code) { ... }
    public List<Network> read() { ... }
    public Network update(Network n) { ... }
    public Network delete(String code) { ... }
}

// ... 10 more repositories with duplicate code ❌
// Total: 2000+ lines of repeated code!
```

**With generics (GOOD):**

```java
// CRUDRepository.java (120 lines)
public class CRUDRepository<T, ID> {
    public T create(T entity) { ... }
    public T read(ID id) { ... }
    public List<T> read() { ... }
    public T update(T entity) { ... }
    public T delete(ID id) { ... }
}

// Use for ALL entities:
CRUDRepository<Measurement, Long> measurementRepo = ...;
CRUDRepository<Network, String> networkRepo = ...;
CRUDRepository<Gateway, String> gatewayRepo = ...;

// Total: 120 lines works for everything! ✅
```

**Benefits:**
- Write once, use everywhere
- Fix bug once, fixed everywhere
- Add feature once, available everywhere
- Less code = fewer bugs

---

### Q2: Why do we need transactions for write operations but not read?

**A:** Because writes must be atomic and reversible!

**Read operations:**

```java
// Reading doesn't modify database
Measurement m = repo.read(47L);

// If error occurs:
//   • Database unchanged
//   • No need to undo anything
//   • Just return null or throw exception
```

**Write operations:**

```java
// Writing modifies database
repo.create(measurement);

// If error occurs:
//   • Database partially modified? ❌
//   • Need to undo changes
//   • Transaction provides rollback ✅
```

**Example scenario:**

```java
// Updating multiple related entities
public void updateNetworkData(String networkCode) {
    tx.begin();
    
    Network network = repo.read(networkCode);
    network.setName("New Name");
    repo.update(network);  // ✓
    
    Gateway gateway = repo.read("GW_0001");
    gateway.setName("New Gateway");
    repo.update(gateway);  // ✓
    
    Sensor sensor = repo.read("S_000001");
    sensor.setName("New Sensor");
    repo.update(sensor);  // ❌ ERROR!
    
    // Without transaction:
    //   Network and Gateway updated, Sensor not
    //   Inconsistent state! ❌
    
    // With transaction:
    //   tx.rollback()
    //   ALL changes undone
    //   Consistent state! ✅
}
```

---

### Q3: What's the difference between persist() and merge()?

**A:** persist() is for NEW entities, merge() is for EXISTING/DETACHED entities.

**persist() - Insert new entity:**

```java
Measurement m = new Measurement(...);  // New object, no ID
m.getId();  // null

em.persist(m);
tx.commit();

m.getId();  // 47 (database assigned!)

// SQL: INSERT INTO Measurement VALUES (...)
```

**merge() - Update existing entity:**

```java
Measurement m = repo.read(47L);  // Existing entity
m.setValue(25.3);                // Modify

em.merge(m);
tx.commit();

// SQL: UPDATE Measurement SET value = 25.3 WHERE id = 47
```

**What if you use wrong one?**

```java
// Using persist() on existing entity:
Measurement m = new Measurement(...);
m.setId(47L);  // ID already exists in database!
em.persist(m);  // ❌ Exception! (duplicate key)

// Using merge() on new entity:
Measurement m = new Measurement(...);  // New, no ID
em.merge(m);  // ✅ Works! Inserts as new

// merge() is more flexible, but persist() is clearer intent
```

**Best practice:**

```
Creating new entity? → Use persist()
Updating existing entity? → Use merge()
Not sure? → Use merge() (handles both)
```

---

### Q4: Why close EntityManager in finally block?

**A:** To guarantee resource cleanup even if exceptions occur!

**Without finally:**

```java
public T create(T entity) {
    EntityManager em = PersistenceManager.getEntityManager();
    EntityTransaction tx = em.getTransaction();
    tx.begin();
    em.persist(entity);
    tx.commit();
    em.close();  // ← If exception occurs, this NEVER runs!
    return entity;
}

// Result: Resource leak! ❌
// Database connections exhausted!
```

**With finally:**

```java
public T create(T entity) {
    EntityManager em = PersistenceManager.getEntityManager();
    EntityTransaction tx = em.getTransaction();
    try {
        tx.begin();
        em.persist(entity);
        tx.commit();
        return entity;
    } catch (Exception e) {
        tx.rollback();
        throw e;
    } finally {
        em.close();  // ← ALWAYS runs, even if exception!
    }
}

// Result: Resources always released ✅
```

**Execution order:**

```
Success:
  1. try block
  2. return entity
  3. finally block (before actual return!)
  4. actual return

Error:
  1. try block
  2. exception
  3. catch block
  4. finally block
  5. throw exception
```

---

### Q5: Can I use CRUDRepository for entities without @Entity annotation?

**A:** No! It will throw IllegalArgumentException.

**Example:**

```java
// Regular class (not an entity)
public class MyClass {
    private Long id;
    private String name;
}

// Try to create repository
CRUDRepository<MyClass, Long> repo = new CRUDRepository<>(MyClass.class);

// Later, when calling any method that uses getEntityName():
repo.read();

// In getEntityName():
Entity ea = entityClass.getAnnotation(Entity.class);
// ea = null (no @Entity annotation)

if (ea == null)
    throw new IllegalArgumentException("Class MyClass must be annotated as @Entity");

// ❌ Exception thrown!
```

**Solution:**

```java
@Entity  // ← Add this!
public class MyClass {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
}

// Now it works:
CRUDRepository<MyClass, Long> repo = new CRUDRepository<>(MyClass.class);
repo.read();  // ✅ Works!
```

---

### Q6: What happens if I forget to commit a transaction?

**A:** Changes are NOT saved to the database!

**Without commit:**

```java
EntityManager em = ...;
EntityTransaction tx = em.getTransaction();
tx.begin();
em.persist(entity);
// Forgot tx.commit()!
em.close();

// Result: Entity NOT saved! ❌
// Changes discarded when EntityManager closes
```

**With commit:**

```java
EntityManager em = ...;
EntityTransaction tx = em.getTransaction();
tx.begin();
em.persist(entity);
tx.commit();  // ← Changes saved!
em.close();

// Result: Entity saved! ✅
```

**Analogy:**

```
tx.begin()   → Start writing a document
em.persist() → Type content
tx.commit()  → Click "Save" button
em.close()   → Close editor

Without commit = Like closing editor without saving!
```

---

### Q7: Why do we need both EntityManager and EntityTransaction?

**A:** They have different responsibilities!

**EntityManager:**
- Manages entities (persistence context)
- Performs database operations (persist, find, merge, remove)
- Creates queries
- Provides transaction access

**EntityTransaction:**
- Manages transaction lifecycle (begin, commit, rollback)
- Controls when changes are saved
- Handles error recovery

**Relationship:**

```java
EntityManager em = ...;           // Database manager
EntityTransaction tx = em.getTransaction();  // Transaction from manager

em.persist(entity);    // Manager: "I'll save this"
tx.commit();           // Transaction: "Do it now!"
```

**Can't use transaction without EntityManager:**

```java
EntityTransaction tx = new EntityTransaction();  // ❌ Can't create directly
```

**Must get from EntityManager:**

```java
EntityManager em = ...;
EntityTransaction tx = em.getTransaction();  // ✅ Get from manager
```

---

### Q8: What if read() returns empty list vs read(ID) returns null?

**A:** Different meanings!

**read() - Returns empty list:**

```java
List<Measurement> all = repo.read();

// If no entities:
all.size()  // 0
all.isEmpty()  // true
all == null  // false

// Safe to iterate:
for (Measurement m : all) {
    // Never executes if empty
}
```

**read(ID) - Returns null:**

```java
Measurement m = repo.read(999L);

// If not found:
m == null  // true

// Must check before use:
if (m != null) {
    System.out.println(m.getValue());
} else {
    System.out.println("Not found");
}
```

**Why different?**

```
read():    "Get all" → Always returns collection (may be empty)
read(ID):  "Get one" → Returns object or null
```

---

### Q9: Can I use CRUDRepository in multi-threaded environment?

**A:** Yes, but create NEW repository instance per thread!

**Thread-safe approach:**

```java
// Thread 1
Runnable task1 = () -> {
    CRUDRepository<Measurement, Long> repo = new CRUDRepository<>(Measurement.class);
    repo.create(measurement1);  // ✅ Own repository
};

// Thread 2
Runnable task2 = () -> {
    CRUDRepository<Measurement, Long> repo = new CRUDRepository<>(Measurement.class);
    repo.create(measurement2);  // ✅ Own repository
};

ExecutorService executor = Executors.newFixedThreadPool(2);
executor.submit(task1);
executor.submit(task2);
```

**NOT thread-safe:**

```java
// Shared repository
CRUDRepository<Measurement, Long> sharedRepo = new CRUDRepository<>(Measurement.class);

// Thread 1
Runnable task1 = () -> {
    sharedRepo.create(measurement1);  // ❌ Shared repository
};

// Thread 2
Runnable task2 = () -> {
    sharedRepo.create(measurement2);  // ❌ Race condition!
};

// Result: Unpredictable behavior, data corruption ❌
```

**Why?**

```
EntityManager is NOT thread-safe
Each CRUDRepository method creates new EntityManager
Repository itself is safe, but best practice: one per thread
```

---

### Q10: How does JPA know which database table to use?

**A:** From the @Entity annotation and class name!

**Default mapping:**

```java
@Entity
public class Measurement {
    // ...
}

// JPA assumes table name = class name
// Table: Measurement
```

**Custom table name:**

```java
@Entity
@Table(name = "measurements")  // Custom table name
public class Measurement {
    // ...
}

// Table: measurements
```

**For our getEntityName():**

```java
@Entity(name = "WR_USER")
public class User {
    // ...
}

getEntityName()  // Returns "WR_USER"

// JPQL: SELECT e FROM WR_USER e
// SQL:  SELECT * FROM wr_user;
```

**Complete mapping:**

```java
@Entity(name = "WR_USER")       // Entity name for JPQL
@Table(name = "weather_users")   // Table name for SQL
public class User {
    @Id
    private String username;     // Column: username
    
    @Column(name = "user_type")  // Column: user_type
    private UserType type;
}

// JPQL: SELECT u FROM WR_USER u
// SQL:  SELECT * FROM weather_users
```

---

### Q11: Where is ID originally defined? What is it?

**A:** ID is NOT defined in any file - it's a **generic type parameter**!

**Understanding Generic Type Parameters:**

```java
public class CRUDRepository<T, ID> {
                           └┬┘ └┬┘
                            │   │
                            │   └─ Generic type parameter (placeholder)
                            └─ Generic type parameter (placeholder)
}
```

**ID is declared RIGHT HERE in the class signature!**

```java
// CRUDRepository.java - Line 24
public class CRUDRepository<T, ID> {
    //                      └──┬──┘
    //                         │
    //          ID is DECLARED here as a generic type parameter
    //          It's not a class, not an interface
    //          It's a PLACEHOLDER that will be replaced with actual types
}
```

---

### What is a Generic Type Parameter?

**Think of it like a variable, but for types:**

```java
// Regular variable (for values):
int x = 5;  // x is a placeholder for a number
    │   │
    │   └─ Actual value
    └─ Variable name

// Generic type parameter (for types):
CRUDRepository<Measurement, Long>
               └─────┬────┘ └─┬┘
                     │        │
                     │        └─ Actual type for ID
                     └─ Actual type for T
```

**ID doesn't exist until you use the class:**

```java
// When you write this:
CRUDRepository<Network, String> repo = new CRUDRepository<>(Network.class);
//                      └──┬──┘
//                         │
//              ID becomes String (replaced everywhere!)

// Inside CRUDRepository, every ID becomes String:
public T read(ID id) {  // Before
       ↓
public Network read(String id) {  // After type substitution
}
```

---

### Where Does ID Come From?

**The declaration itself IS the source:**

```java
// File: CRUDRepository.java
public class CRUDRepository<T, ID> {
    //                         ↑
    //                         │
    //                    DECLARED HERE!
    //                    This is the ORIGIN of ID
    
    // Now ID can be used throughout the class:
    
    public T read(ID id) {  // ID used as parameter type
        //        └┬┘
        //         └─ Using the ID that was declared above
        ...
    }
    
    public T delete(ID id) {  // ID used again
        //          └┬┘
        //           └─ Same ID from class declaration
        ...
    }
}
```

**Analogy:**

```java
// This is like a function parameter:
public int calculate(int x) {
    //               └┬┘
    //                └─ x is declared here in the parameter list
    
    return x * 2;  // x is used in the body
    //     │
    //     └─ Same x from above
}

// Generic type parameter works the same way:
public class CRUDRepository<T, ID> {
    //                         └┬┘
    //                          └─ ID declared here in type parameter list
    
    public T read(ID id) {  // ID used in the class body
        //        └┬┘
        //         └─ Same ID from above
    }
}
```

---

### ID is NOT a File or Class!

**Common misconception:**

```
❌ ID.java (doesn't exist!)
❌ ID.class (doesn't exist!)
❌ interface ID { } (doesn't exist!)

✅ ID is just a placeholder name in the generic declaration!
```

**You could even name it differently:**

```java
// These are all equivalent:
public class CRUDRepository<T, ID> { ... }
public class CRUDRepository<T, IDType> { ... }
public class CRUDRepository<T, PK> { ... }  // PK = Primary Key
public class CRUDRepository<T, KeyType> { ... }
public class CRUDRepository<EntityType, PrimaryKeyType> { ... }

// The NAME doesn't matter - it's just a placeholder!
// Convention is to use short, meaningful names like T, ID, K, V
```

---

### How ID Gets Its Actual Type

**Step-by-step substitution:**

```java
// Step 1: Declare the generic class
public class CRUDRepository<T, ID> {
    //                         ↑  ↑
    //                         │  │
    //              Placeholders declared

    public T read(ID id) { ... }
}

// Step 2: Use the generic class with ACTUAL types
CRUDRepository<Measurement, Long> repo = ...;
//             └─────┬────┘ └─┬┘
//                   │        │
//         T becomes Measurement
//                 ID becomes Long

// Step 3: Compiler replaces T and ID everywhere
public Measurement read(Long id) { ... }
//     └────┬────┘      └─┬┘
//          │             │
//   T was replaced   ID was replaced
```

---

### Where We SPECIFY What ID Should Be

**Now, where do we actually USE ID by giving it real types?**

#### Location 1: **DataImportingService.java**

```java
// File: src/main/java/com/weather/report/services/DataImportingService.java

private static void checkMeasurement(Measurement measurement) {
    CRUDRepository<Sensor, String> sensorRepository = new CRUDRepository<>(Sensor.class);
    //                     └──┬──┘
    //                        │
    //              Here we SPECIFY: ID = String
    //              (Because Sensor's primary key is String code)
    
    Sensor currentSensor = sensorRepository.read()...
}
```

---

#### Location 2: **MeasurementRepository.java**

```java
// File: src/main/java/com/weather/report/repositories/MeasurementRepository.java

public class MeasurementRepository extends CRUDRepository<Measurement, Long> {
    //                                                      └────┬───┘
    //                                                           │
    //                                              Here we SPECIFY: ID = Long
    //                                    (Because Measurement's primary key is Long id)
    
    public MeasurementRepository() {
        super(Measurement.class);
    }
}
```

---

#### Location 3-5: **Operations Implementations** (assumed)

```java
// NetworkOperations implementation
CRUDRepository<Network, String> networkRepo = ...;
//                     └──┬──┘
//                        └─ ID = String (Network code)

// GatewayOperations implementation  
CRUDRepository<Gateway, String> gatewayRepo = ...;
//                     └──┬──┘
//                        └─ ID = String (Gateway code)

// SensorOperations implementation
CRUDRepository<Sensor, String> sensorRepo = ...;
//                    └──┬──┘
//                       └─ ID = String (Sensor code)
```

---

### Complete Understanding

```
┌────────────────────────────────────────────────────────┐
│ Step 1: ID is DECLARED in the class signature         │
│ ─────────────────────────────────────────             │
│ File: CRUDRepository.java                             │
│                                                        │
│ public class CRUDRepository<T, ID> {                  │
│                                 ↑                      │
│                                 │                      │
│                         ID is born here!               │
│                         (Generic type parameter)       │
│ }                                                      │
└────────────────────────────────────────────────────────┘
                          ↓
┌────────────────────────────────────────────────────────┐
│ Step 2: ID is USED in method signatures               │
│ ──────────────────────────────────────                │
│ Same file: CRUDRepository.java                        │
│                                                        │
│ public T read(ID id) { ... }                          │
│               ↑                                        │
│               └─ Using the ID from class declaration  │
│                                                        │
│ public T delete(ID id) { ... }                        │
│                 ↑                                      │
│                 └─ Using the ID from class declaration│
└────────────────────────────────────────────────────────┘
                          ↓
┌────────────────────────────────────────────────────────┐
│ Step 3: ID is SPECIFIED when creating instances       │
│ ──────────────────────────────────────────────────    │
│ Files: Throughout the project                         │
│                                                        │
│ CRUDRepository<Measurement, Long> repo = ...;         │
│                             └─┬┘                       │
│                               └─ ID becomes Long       │
│                                                        │
│ CRUDRepository<Network, String> repo = ...;           │
│                         └──┬──┘                        │
│                            └─ ID becomes String        │
└────────────────────────────────────────────────────────┘
```

---

### The Key Insight

**ID is a compile-time placeholder:**

```java
// At compile time:
CRUDRepository<Network, String> repo = new CRUDRepository<>(Network.class);
                        └──┬──┘
                           │
         Compiler sees: "Replace all ID with String"

// After compilation, it's as if you wrote:
public class CRUDRepository_Network_String {
    public Network read(String id) { ... }
    //                  └──┬──┘
    //                     │
    //              ID was replaced with String
    
    public Network delete(String id) { ... }
    //                    └──┬──┘
    //                       │
    //                ID was replaced with String
}
```

---

### Summary of Q11

**Where is ID defined?**
- ✅ In the class declaration: `public class CRUDRepository<T, ID>`
- ❌ NOT in a separate file
- ❌ NOT a class or interface

**What is ID?**
- A generic type parameter (placeholder for a type)
- Gets replaced with actual types when you use the class

**Where do we specify what ID should be?**
- DataImportingService.java: `CRUDRepository<Sensor, String>`
- MeasurementRepository.java: `CRUDRepository<Measurement, Long>`
- NetworkOperations impl: `CRUDRepository<Network, String>`
- GatewayOperations impl: `CRUDRepository<Gateway, String>`
- SensorOperations impl: `CRUDRepository<Sensor, String>`

---

**Where ID is used in our project:**

#### 1. DataImportingService.java (checking measurements)

```java
// File: DataImportingService.java
private static void checkMeasurement(Measurement measurement) {
    CRUDRepository<Sensor, String> sensorRepository = new CRUDRepository<>(Sensor.class);
    //                     └──┬──┘
    //                        │
    //                   ID = String (Sensor's primary key type)
    
    Sensor currentSensor = sensorRepository.read().stream()
        .filter(s -> measurement.getSensorCode().equals(s.getCode()))
        .findFirst()
        .orElse(null);
}
```

**Why String?** Because Sensor's primary key is a String code:
```java
public class Sensor {
    private String code;  // Primary key is String "S_000001"
}
```

---

#### 2. MeasurementRepository.java

```java
// File: MeasurementRepository.java
public class MeasurementRepository extends CRUDRepository<Measurement, Long> {
    //                                                      └────┬───┘
    //                                                           │
    //                                                      ID = Long
    
    public MeasurementRepository() {
        super(Measurement.class);
    }
}
```

**Why Long?** Because Measurement's primary key is auto-generated Long:
```java
@Entity
public class Measurement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Primary key is Long (auto-generated number)
}
```

---

#### 3. NetworkOperations Implementation (assumed)

```java
// In your NetworkOperations implementation (not shown in provided files):
public class NetworkOperationsImpl implements NetworkOperations {
    
    private CRUDRepository<Network, String> networkRepository;
    //                             └──┬──┘
    //                                │
    //                           ID = String
    
    public NetworkOperationsImpl() {
        this.networkRepository = new CRUDRepository<>(Network.class);
    }
    
    @Override
    public Network createNetwork(String code, ...) {
        Network network = new Network();
        network.setCode(code);  // code is the ID
        return networkRepository.create(network);
    }
    
    @Override
    public Network deleteNetwork(String code, ...) {
        return networkRepository.delete(code);  // code used as ID
        //                             └──┬──┘
        //                                │
        //                          ID type is String
    }
}
```

**Why String?** Because Network's primary key is the code:
```java
public class Network {
    private String code;  // Primary key is String "NET_01"
}
```

---

#### 4. GatewayOperations Implementation (assumed)

```java
// In your GatewayOperations implementation:
public class GatewayOperationsImpl implements GatewayOperations {
    
    private CRUDRepository<Gateway, String> gatewayRepository;
    //                             └──┬──┘
    //                                │
    //                           ID = String
    
    @Override
    public Gateway deleteGateway(String code, ...) {
        return gatewayRepository.delete(code);
        //                             └──┬──┘
        //                                │
        //                          String ID type
    }
}
```

---

#### 5. SensorOperations Implementation (assumed)

```java
// In your SensorOperations implementation:
public class SensorOperationsImpl implements SensorOperations {
    
    private CRUDRepository<Sensor, String> sensorRepository;
    //                            └──┬──┘
    //                               │
    //                          ID = String
    
    @Override
    public Sensor deleteSensor(String code, ...) {
        return sensorRepository.delete(code);
        //                            └──┬──┘
        //                               │
        //                         String ID type
    }
}
```

---

### Where ID Type is Actually Used in CRUDRepository Methods

**The ID generic type appears in these method signatures:**

```java
public class CRUDRepository<T, ID> {
    
    // Read by ID - ID type used as parameter
    public T read(ID id) {
        //        └┬┘
        //         │
        //    ID type here!
        EntityManager em = PersistenceManager.getEntityManager();
        try {
            T entity = em.find(entityClass, id);
            //                            └┬┘
            //                     ID used to find entity
            return entity;
        } finally {
            em.close();
        }
    }
    
    // Delete by ID - ID type used as parameter
    public T delete(ID id) {
        //          └┬┘
        //           │
        //      ID type here!
        EntityManager em = PersistenceManager.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T entity = em.find(entityClass, id);
            //                            └┬┘
            //                     ID used to find entity
            if (entity != null) {
                em.remove(entity);
            }
            tx.commit();
            return entity;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}
```

---

### Complete Mapping: Entity → ID Type

**Here's how each entity maps to its ID type:**

```
Entity Class         Primary Key Field    ID Type    Example Value
────────────────────────────────────────────────────────────────────
Measurement          Long id              Long       47L
Network              String code          String     "NET_01"
Gateway              String code          String     "GW_0001"
Sensor               String code          String     "S_000001"
Operator             String email         String     "john@example.com"
User                 String username      String     "admin"
Parameter            (composite)          String     (within gateway)
Threshold            (within sensor)      N/A        (not standalone)
```

---

### Visual Example: How ID Flows Through the System

```
┌─────────────────────────────────────────────────────────────┐
│ User calls NetworkOperations.deleteNetwork("NET_01", ...)  │
└────────────────────────────┬────────────────────────────────┘
                             │
                             ↓
┌─────────────────────────────────────────────────────────────┐
│ NetworkOperationsImpl                                       │
│ ────────────────────────                                    │
│ CRUDRepository<Network, String> repo = ...;                │
│                        └──┬──┘                              │
│                           │                                 │
│                      ID = String                            │
│                                                             │
│ return repo.delete("NET_01");                              │
│                    └───┬──┘                                 │
│                        │                                    │
│                  String value passed                        │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ↓
┌─────────────────────────────────────────────────────────────┐
│ CRUDRepository.delete(ID id)                                │
│                       └┬┘                                    │
│                        │                                     │
│                  ID = String (from generic)                 │
│                                                             │
│ public T delete(ID id) {                                   │
│     // id is "NET_01" (String type)                        │
│     T entity = em.find(entityClass, id);                   │
│     //                           └┬┘                        │
│     //                            │                         │
│     //                      "NET_01" passed to JPA          │
│     if (entity != null) {                                  │
│         em.remove(entity);                                 │
│     }                                                       │
│     return entity;                                          │
│ }                                                           │
└─────────────────────────────────────────────────────────────┘
                         │
                         ↓
┌─────────────────────────────────────────────────────────────┐
│ JPA/Hibernate                                               │
│ ─────────────                                               │
│ em.find(Network.class, "NET_01")                           │
│                        └───┬──┘                             │
│                            │                                │
│                      String ID value                        │
│                                                             │
│ Generated SQL:                                              │
│ SELECT * FROM Network WHERE code = 'NET_01'                │
└─────────────────────────────────────────────────────────────┘
```

---

### Why Different ID Types?

**Different entities have different ID strategies:**

**1. Auto-generated numeric IDs (Long):**
```java
@Entity
public class Measurement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Database auto-generates: 1, 2, 3, 4...
}

// Usage:
CRUDRepository<Measurement, Long> repo = ...;
Measurement m = repo.read(47L);  // Find by numeric ID
```

**Benefits:**
- Simple
- Fast
- Guaranteed unique
- No user input needed

**2. User-defined string codes (String):**
```java
public class Network {
    private String code;  // User sets: "NET_01", "NET_02", etc.
}

// Usage:
CRUDRepository<Network, String> repo = ...;
Network n = repo.read("NET_01");  // Find by code
```

**Benefits:**
- Meaningful IDs
- Human-readable
- Can encode information (NET_01 = Network #1)
- Matches business requirements

---

### Summary: Where ID is Used

| File | ID Type | Usage |
|------|---------|-------|
| **DataImportingService.java** | String | `CRUDRepository<Sensor, String>` for sensor lookup |
| **MeasurementRepository.java** | Long | `CRUDRepository<Measurement, Long>` for measurements |
| **NetworkOperations impl** | String | `CRUDRepository<Network, String>` for network CRUD |
| **GatewayOperations impl** | String | `CRUDRepository<Gateway, String>` for gateway CRUD |
| **SensorOperations impl** | String | `CRUDRepository<Sensor, String>` for sensor CRUD |

**In CRUDRepository itself:**
- `read(ID id)` method - finds entity by ID
- `delete(ID id)` method - deletes entity by ID

---

### Q12: Why is entityClass a protected field?

**A:** To allow subclass access while keeping it hidden from external classes!

**The declaration:**

```java
public class CRUDRepository<T, ID> {
    protected Class<T> entityClass;
    //  └───┬───┘
    //      │
    //  Protected modifier
}
```

---

### Understanding Access Modifiers

**Java has 4 access levels:**

```
                    Same     Same      Subclass   Different
                    Class    Package   (any pkg)  Package
────────────────────────────────────────────────────────────
private             ✅       ❌        ❌         ❌
(default/package)   ✅       ✅        ❌         ❌
protected           ✅       ✅        ✅         ❌
public              ✅       ✅        ✅         ✅
```

---

### Why Not Private?

**If entityClass were private:**

```java
public class CRUDRepository<T, ID> {
    private Class<T> entityClass;  // Private
}

public class MeasurementRepository extends CRUDRepository<Measurement, Long> {
    
    public void customMethod() {
        // Can't access entityClass! ❌
        System.out.println(entityClass.getName());  // Compile error!
        //                 └────┬────┘
        //                      │
        //              Not accessible in subclass
    }
}
```

**Problem:** Subclasses might need to access the entity class for custom functionality!

---

### Why Not Public?

**If entityClass were public:**

```java
public class CRUDRepository<T, ID> {
    public Class<T> entityClass;  // Public
}

// Any class can access and MODIFY it:
public class SomeRandomClass {
    public void breakThings() {
        CRUDRepository<Network, String> repo = new CRUDRepository<>(Network.class);
        
        // Can access and modify!
        repo.entityClass = Gateway.class;  // ❌ Breaking the repository!
        
        // Now repository is confused:
        // It thinks it's for Network but has Gateway class
        Network n = repo.read("NET_01");  // Runtime error!
    }
}
```

**Problem:** External classes shouldn't be able to change internal state!

---

### Why Protected is Perfect!

**Protected access allows:**

✅ **Subclasses can access it:**

```java
public class MeasurementRepository extends CRUDRepository<Measurement, Long> {
    
    public MeasurementRepository() {
        super(Measurement.class);
        // entityClass is now set to Measurement.class
    }
    
    // Can add custom methods that use entityClass:
    public String getEntityClassName() {
        return entityClass.getSimpleName();  // ✅ Accessible!
        //     └────┬────┘
        //          │
        //     Protected field accessible in subclass
    }
    
    public boolean isEntity() {
        return entityClass.isAnnotationPresent(Entity.class);  // ✅ Accessible!
    }
}
```

✅ **Classes in same package can access it:**

```java
package com.weather.report.repositories;

public class RepositoryUtil {
    public static String getTableName(CRUDRepository<?, ?> repo) {
        // Same package, can access protected field
        return repo.entityClass.getSimpleName();  // ✅ Accessible!
    }
}
```

❌ **External classes CANNOT access it:**

```java
package com.weather.report.services;  // Different package

public class DataImportingService {
    public void importData() {
        MeasurementRepository repo = new MeasurementRepository();
        
        // Cannot access entityClass from different package!
        System.out.println(repo.entityClass);  // ❌ Compile error!
    }
}
```

---

### Real-World Scenarios

#### Scenario 1: Custom Repository Method

```java
public class MeasurementRepository extends CRUDRepository<Measurement, Long> {
    
    // Custom method that needs entityClass
    public List<Measurement> findByNetworkCode(String networkCode) {
        EntityManager em = PersistenceManager.getEntityManager();
        try {
            // Need entity name for JPQL query
            String entityName = entityClass.getSimpleName();
            //                  └────┬────┘
            //                       │
            //              Can access because protected!
            
            String jpql = "SELECT m FROM " + entityName + " m WHERE m.networkCode = :code";
            TypedQuery<Measurement> query = em.createQuery(jpql, entityClass);
            //                                                    └────┬────┘
            //                                                         │
            //                                            Also used here!
            query.setParameter("code", networkCode);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
```

**If entityClass were private:** This custom method wouldn't work! ❌

**With protected:** Subclass can access and extend functionality! ✅

---

#### Scenario 2: Repository Utilities

```java
package com.weather.report.repositories;

// Same package, can access protected fields
public class RepositoryHelper {
    
    // Utility method for logging
    public static void logRepositoryOperation(CRUDRepository<?, ?> repo, String operation) {
        String entityType = repo.entityClass.getSimpleName();
        //                       └────┬────┘
        //                            │
        //                  Can access from same package!
        
        System.out.println("Operation: " + operation + " on entity: " + entityType);
    }
}

// Usage:
CRUDRepository<Network, String> repo = new CRUDRepository<>(Network.class);
RepositoryHelper.logRepositoryOperation(repo, "CREATE");
// Output: "Operation: CREATE on entity: Network"
```

---

### Encapsulation Benefit

**Protected provides the right balance:**

```
Private:   Too restrictive  → Subclasses can't extend functionality
Protected: Just right ✅     → Subclasses can access, outsiders can't
Public:    Too permissive   → Anyone can modify, breaks encapsulation
```

**Analogy: Family secrets**

```
Private:   Only you know (not even your kids)
Protected: You and your family know (inheritance)
Public:    Everyone knows (anyone can access)
```

---

### Other Protected Members in the Class

Looking at CRUDRepository, we also have:

```java
protected String getEntityName() {
    //  └───┬───┘
    //      │
    //  Also protected!
    
    Entity ea = entityClass.getAnnotation(Entity.class);
    //          └────┬────┘
    //               │
    //          Uses protected entityClass field
    
    if (ea == null)
        throw new IllegalArgumentException("...");
    if (ea.name().isEmpty())
        return this.entityClass.getSimpleName();
        //         └────┬────┘
        //              │
        //         Uses protected field
    return ea.name();
}
```

**Why getEntityName() is protected:**

- Utility method for subclasses
- Used in `read()` method to build JPQL queries
- Subclasses might override or extend it
- Not meant for external use

**Example override:**

```java
public class NetworkRepository extends CRUDRepository<Network, String> {
    
    @Override
    protected String getEntityName() {
        // Override to customize behavior
        return "NetworkEntity";  // Custom name
    }
}
```

---

### Summary: Why Protected?

**Question:** Why is entityClass protected?

**Answer:** 

1. **Allow subclass access** - Subclasses like MeasurementRepository can use it for custom methods
2. **Same package access** - Utility classes in repositories package can access it
3. **Hide from external code** - Services and operations can't accidentally modify it
4. **Maintain encapsulation** - Internal implementation detail, not part of public API
5. **Enable extensibility** - Subclasses can add functionality that uses entityClass

**Design principle:**

```
Protected = "For family use only"
  • Family (subclasses) can access and use
  • Neighbors (same package) can access
  • Strangers (other packages) cannot access
  
Perfect for internal fields that subclasses might need!
```

---

### How ID Flows Through the System

Let me show you a complete example of how ID works from declaration to usage:

**Example: Deleting a Network**

```java
// 1. Class Declaration (CRUDRepository.java)
public class CRUDRepository<T, ID> {
    //                         ↑  ↑
    //              T and ID declared here
    
    public T delete(ID id) {
        //          └┬┘
        //           └─ ID used here
        EntityManager em = PersistenceManager.getEntityManager();
        T entity = em.find(entityClass, id);
        //                            └┬┘
        //                             └─ ID used to find entity
        if (entity != null) {
            em.remove(entity);
        }
        return entity;
    }
}

// 2. Specify Types (NetworkOperations implementation)
public class NetworkOperationsImpl implements NetworkOperations {
    private CRUDRepository<Network, String> networkRepo;
    //                             └──┬──┘
    //                                └─ ID = String
    
    public NetworkOperationsImpl() {
        networkRepo = new CRUDRepository<>(Network.class);
    }
    
    @Override
    public Network deleteNetwork(String code, String username) {
        // Validation...
        return networkRepo.delete(code);
        //                        └─┬┘
        //                          └─ String passed (matches ID type!)
    }
}

// 3. Compiler Replaces ID with String
// The delete method becomes:
public Network delete(String id) {
    //                └──┬──┘
    //                   └─ ID replaced with String
    EntityManager em = PersistenceManager.getEntityManager();
    Network entity = em.find(Network.class, id);
    //                                      └┬┘
    //                                String passed to JPA
    if (entity != null) {
        em.remove(entity);
    }
    return entity;
}

// 4. JPA Executes SQL
// SQL: DELETE FROM Network WHERE code = 'NET_01'
//                                        └───┬──┘
//                                           String value
```

---

### Complete Entity → ID Type Mapping

**Here's how each entity in the project maps to its ID type:**

| Entity | File Location | Primary Key Field | ID Type | Example Value | Format |
|--------|---------------|-------------------|---------|---------------|--------|
| **Measurement** | model/entities/Measurement.java | `Long id` | **Long** | `47L` | Auto-generated |
| **Network** | model/entities/Network.java | `String code` | **String** | `"NET_01"` | NET_## |
| **Gateway** | model/entities/Gateway.java | `String code` | **String** | `"GW_0001"` | GW_#### |
| **Sensor** | model/entities/Sensor.java | `String code` | **String** | `"S_000001"` | S_###### |
| **Operator** | model/entities/Operator.java | `String email` | **String** | `"john@example.com"` | Email address |
| **User** | model/entities/User.java | `String username` | **String** | `"admin"` | Username |

---

### Why Different ID Types?

**Two strategies for primary keys:**

#### Strategy 1: Auto-generated (Long)

```java
@Entity
public class Measurement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Database generates: 1, 2, 3, 4...
}

// Usage:
CRUDRepository<Measurement, Long> repo = ...;
Measurement m = repo.read(47L);  // ← Long ID
```

**Pros:**
- ✅ Simple - no user input needed
- ✅ Fast - numeric comparison
- ✅ Guaranteed unique - database ensures it
- ✅ No validation needed

**Cons:**
- ❌ Not human-readable
- ❌ No business meaning
- ❌ Hard to remember

---

#### Strategy 2: User-defined codes (String)

```java
public class Network {
    private String code;  // User sets: "NET_01", "NET_02"
}

// Usage:
CRUDRepository<Network, String> repo = ...;
Network n = repo.read("NET_01");  // ← String ID
```

**Pros:**
- ✅ Human-readable - "NET_01" is meaningful
- ✅ Business-friendly - matches requirements
- ✅ Can encode information
- ✅ Easy to reference in documentation

**Cons:**
- ❌ Needs validation (format checking)
- ❌ User must provide it
- ❌ Slightly slower (string comparison)
- ❌ Risk of duplicates if not validated

---

### Where in Project Files?

Let me show you the EXACT locations in your project:

#### **File 1: DataImportingService.java**

```
Path: src/main/java/com/weather/report/services/DataImportingService.java

Line (approximately): In checkMeasurement() method

Code:
    CRUDRepository<Sensor, String> sensorRepository = 
        new CRUDRepository<>(Sensor.class);
    //             └──┬──┘
    //                └─ ID = String specified here
```

---

#### **File 2: MeasurementRepository.java**

```
Path: src/main/java/com/weather/report/repositories/MeasurementRepository.java

Line: Class declaration

Code:
    public class MeasurementRepository 
        extends CRUDRepository<Measurement, Long> {
        //                                 └─┬┘
        //                                   └─ ID = Long specified here
```

---

#### **Files 3-5: Operations Implementations**

```
Path: src/main/java/com/weather/report/operations/impl/
      (or wherever your implementations are)

Examples:
    // NetworkOperationsImpl.java
    CRUDRepository<Network, String> networkRepo;
    
    // GatewayOperationsImpl.java
    CRUDRepository<Gateway, String> gatewayRepo;
    
    // SensorOperationsImpl.java
    CRUDRepository<Sensor, String> sensorRepo;
```

---

**A:** Entities are Java classes that represent database tables. They are defined in the `model/entities` package!

---

### What is an Entity?

**Entity** = Java class that maps to a database table

```java
@Entity  // ← This annotation makes it an entity
public class Measurement {
    // Java class = Database table
    // Fields = Table columns
    // Objects = Table rows
}
```

**The mapping:**

```
Java Code:                    Database:
────────────────────────────────────────────────
@Entity                       CREATE TABLE
public class Measurement {    Measurement (
    
    @Id                           id BIGINT PRIMARY KEY,
    private Long id;              
    
    private String networkCode;   network_code VARCHAR(255),
    private String gatewayCode;   gateway_code VARCHAR(255),
    private String sensorCode;    sensor_code VARCHAR(255),
    private double value;         value DOUBLE,
    private LocalDateTime         timestamp TIMESTAMP
        timestamp;
}                             );
```

---

### All Entity Files in the Project

**Location:** All entities are in the `com.weather.report.model.entities` package

```
project/
├── src/main/java/com/weather/report/
│   ├── model/
│   │   └── entities/          ← All entity files here!
│   │       ├── Measurement.java    ✅ Entity
│   │       ├── Network.java        ✅ Entity  
│   │       ├── Gateway.java        ✅ Entity
│   │       ├── Sensor.java         ✅ Entity
│   │       ├── Operator.java       ✅ Entity
│   │       ├── User.java           ✅ Entity
│   │       ├── Parameter.java      ⚠️  Embedded entity (part of Gateway)
│   │       └── Threshold.java      ⚠️  Embedded entity (part of Sensor)
```

---

### Complete Entity List with Details

#### 1. **Measurement.java** - Sensor measurement data

```java
// File: com/weather/report/model/entities/Measurement.java

@Entity
public class Measurement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                    // Primary key (auto-generated)
    
    private String sensorCode;          // Which sensor
    private String gatewayCode;         // Which gateway
    private String networkCode;         // Which network
    private double value;               // Measured value
    private LocalDateTime timestamp;    // When measured
}

// Database table: Measurement
// Primary key type: Long (auto-generated)
```

**Purpose:** Stores individual measurements from sensors

**Used by:** 
- DataImportingService (import from CSV)
- MeasurementRepository (CRUD operations)
- All reports (data source)

---

#### 2. **Network.java** - Monitoring network

```java
// File: com/weather/report/model/entities/Network.java

public class Network extends Timestamped {
    private String code;                    // Primary key: "NET_01"
    private String name;                    // Network name
    private String description;             // Description
    private Collection<Operator> operators; // Who to notify
    
    // Inherited from Timestamped:
    // - String createdBy
    // - LocalDateTime createdAt
    // - String modifiedBy
    // - LocalDateTime modifiedAt
}

// Database table: Network
// Primary key type: String (code like "NET_01")
// Primary key format: "NET_" + two digits
```

**Purpose:** Represents a monitoring network (logical grouping)

**Used by:**
- NetworkOperations (manage networks)
- TopologyOperations (link with gateways)
- Reporting (network-level reports)

---

#### 3. **Gateway.java** - Device gateway

```java
// File: com/weather/report/model/entities/Gateway.java

public class Gateway extends Timestamped {
    private String code;                      // Primary key: "GW_0001"
    private String name;                      // Gateway name
    private String description;               // Description
    private Collection<Parameter> parameters; // Configuration parameters
    
    // Inherited from Timestamped:
    // - String createdBy
    // - LocalDateTime createdAt
    // - String modifiedBy
    // - LocalDateTime modifiedAt
}

// Database table: Gateway
// Primary key type: String (code like "GW_0001")
// Primary key format: "GW_" + four digits
```

**Purpose:** Groups sensors monitoring same physical quantity

**Used by:**
- GatewayOperations (manage gateways)
- TopologyOperations (link with networks and sensors)
- Reporting (gateway-level reports)

---

#### 4. **Sensor.java** - Measurement sensor

```java
// File: com/weather/report/model/entities/Sensor.java

public class Sensor extends Timestamped {
    private String code;            // Primary key: "S_000001"
    private String name;            // Sensor name
    private String description;     // Description
    private Threshold threshold;    // Alert threshold
    
    // Inherited from Timestamped:
    // - String createdBy
    // - LocalDateTime createdAt
    // - String modifiedBy
    // - LocalDateTime modifiedAt
}

// Database table: Sensor
// Primary key type: String (code like "S_000001")
// Primary key format: "S_" + six digits
```

**Purpose:** Measures physical quantities (temperature, humidity, etc.)

**Used by:**
- SensorOperations (manage sensors)
- TopologyOperations (link with gateways)
- DataImportingService (threshold checking)
- Reporting (sensor-level reports)

---

#### 5. **Operator.java** - Person who receives alerts

```java
// File: com/weather/report/model/entities/Operator.java

public class Operator {
    private String firstName;    // First name
    private String lastName;     // Last name
    private String email;        // Primary key (unique)
    private String phoneNumber;  // Optional phone
}

// Database table: Operator
// Primary key type: String (email address)
```

**Purpose:** Receives notifications when thresholds violated

**Used by:**
- NetworkOperations (associate with networks)
- AlertingService (send alerts)

---

#### 6. **User.java** - System user

```java
// File: com/weather/report/model/entities/User.java

@Entity(name = "WR_USER")  // Custom table name
public class User {
    @Id
    private String username;  // Primary key
    
    @Enumerated
    private UserType type;    // VIEWER or MAINTAINER
}

// Database table: WR_USER (custom name)
// Primary key type: String (username)
```

**Purpose:** Represents users of the system with permissions

**Used by:**
- All Operations (check authorization)
- WeatherReport.createUser() (user management)

---

#### 7. **Parameter.java** - Gateway configuration

```java
// File: com/weather/report/model/entities/Parameter.java

public class Parameter {
    public static final String EXPECTED_MEAN_CODE = "EXPECTED_MEAN";
    public static final String EXPECTED_STD_DEV_CODE = "EXPECTED_STD_DEV";
    public static final String BATTERY_CHARGE_PERCENTAGE_CODE = "BATTERY_CHARGE";
    
    private String code;         // Parameter code (unique within gateway)
    private String name;         // Parameter name
    private String description;  // Description
    private double value;        // Numeric value
}

// Not a standalone table - embedded in Gateway
// Part of Gateway's configuration
```

**Purpose:** Stores configuration values for gateways

**Special parameters:**
- EXPECTED_MEAN: Expected mean for outlier detection
- EXPECTED_STD_DEV: Expected standard deviation
- BATTERY_CHARGE: Current battery level

**Used by:**
- GatewayOperations (manage parameters)
- GatewayReport (use for calculations)

---

#### 8. **Threshold.java** - Alert threshold

```java
// File: com/weather/report/model/entities/Threshold.java

public class Threshold {
    private double value;         // Threshold value
    private ThresholdType type;   // Comparison type (LESS_THAN, GREATER_THAN, etc.)
}

// Not a standalone table - embedded in Sensor
// Part of Sensor's configuration
```

**Purpose:** Defines acceptable limits for sensor values

**Used by:**
- SensorOperations (manage thresholds)
- DataImportingService (check violations)

---

### Entity Hierarchy

**Inheritance structure:**

```
Timestamped (abstract base class)
    ↓
    ├── Network (tracks creation/modification)
    ├── Gateway (tracks creation/modification)
    └── Sensor  (tracks creation/modification)

Standalone entities:
    ├── Measurement (no inheritance)
    ├── User (no inheritance)
    └── Operator (no inheritance)

Embedded entities (not standalone tables):
    ├── Parameter (part of Gateway)
    └── Threshold (part of Sensor)
```

---

### Timestamped Base Class

```java
// File: com/weather/report/model/Timestamped.java

public class Timestamped {
    private String createdBy;           // Who created it
    private LocalDateTime createdAt;    // When created
    private String modifiedBy;          // Who last modified it
    private LocalDateTime modifiedAt;   // When last modified
    
    // Getters and setters...
}

// Not an @Entity itself, but provides fields for Network, Gateway, Sensor
```

**Why Timestamped?**

Audit trail - know who created/modified each entity and when!

---

### Entity Relationships

```
Network (1) ─────< has many >───── (N) Operator
   │
   └─< has many >─── (N) Gateway
                         │
                         ├─< has many >─── (N) Parameter
                         │
                         └─< has many >─── (N) Sensor
                                              │
                                              ├─< has one >── (1) Threshold
                                              │
                                              └─< produces >── (N) Measurement
```

**Reading the diagram:**
- Network can have many Operators
- Network can have many Gateways
- Gateway can have many Parameters
- Gateway can have many Sensors
- Sensor can have one Threshold
- Sensor produces many Measurements

---

### How to Identify an Entity

**Look for these markers:**

```java
@Entity  // ← Primary marker
public class SomeClass {
    
    @Id  // ← Has primary key
    private SomeType id;
    
    // Entity = Class with @Entity and @Id annotations
}
```

**In the project:**

```java
// Measurement.java
@Entity  // ✅ Entity
public class Measurement {
    @Id  // ✅ Has primary key
    private Long id;
}

// User.java
@Entity(name = "WR_USER")  // ✅ Entity (custom name)
public class User {
    @Id  // ✅ Has primary key
    private String username;
}

// Network.java
// No @Entity annotation visible in provided files,
// but README confirms it's an entity
public class Network extends Timestamped {
    private String code;  // Primary key
}
```

---

### Summary: All Entities

| Entity | File | Primary Key | Key Type | Auto-generated? |
|--------|------|-------------|----------|-----------------|
| **Measurement** | Measurement.java | id | Long | ✅ Yes |
| **Network** | Network.java | code | String | ❌ User sets |
| **Gateway** | Gateway.java | code | String | ❌ User sets |
| **Sensor** | Sensor.java | code | String | ❌ User sets |
| **Operator** | Operator.java | email | String | ❌ User sets |
| **User** | User.java | username | String | ❌ User sets |
| **Parameter** | Parameter.java | (embedded) | - | N/A |
| **Threshold** | Threshold.java | (embedded) | - | N/A |

---

### Why Entities Matter

**Entities are the foundation of the entire system:**

```
1. Define data structure
   ↓
2. JPA maps to database tables
   ↓
3. CRUDRepository operates on entities
   ↓
4. Operations use repositories to manage entities
   ↓
5. Services process entity data
   ↓
6. Reports aggregate entity data
```

**Without entities:** No database, no persistence, no system!

---

### Finding Entity Files

**In your IDE:**

```
src/main/java/
└── com/
    └── weather/
        └── report/
            └── model/
                └── entities/     ← Look here!
                    ├── Measurement.java
                    ├── Network.java
                    ├── Gateway.java
                    ├── Sensor.java
                    ├── Operator.java
                    ├── User.java
                    ├── Parameter.java
                    └── Threshold.java
```

**In file explorer:** Navigate to `model/entities` folder

**In your project:** All entity classes are in the same package!

---

## Summary

### CRUDRepository Purpose

**One class** that provides **database operations** for **all entities**.

### The Five Methods

```
create(entity)   → Save new entity to database
read(id)         → Find entity by ID
read()           → Get all entities
update(entity)   → Modify existing entity
delete(id)       → Remove entity from database
```

### Key Concepts

**Generic Types:** One implementation works for all entities

**Transactions:** Ensure data consistency (all-or-nothing)

**EntityManager:** Interface to database operations

**JPA/Hibernate:** Java Persistence API and its implementation

**Repository Pattern:** Separate database logic from business logic

### R1 Requirements Met

✅ **All CRUD methods implemented**
✅ **Can save measurements** (create)
✅ **Can manage networks** (create, read, update, delete)
✅ **Can manage operators** (create)
✅ **Can query sensors** (read)

### Best Practices

1. **Always close EntityManager** (use finally block)
2. **Use transactions for writes** (begin, commit, rollback)
3. **Check for null** (read(ID) may return null)
4. **Use appropriate method** (persist for new, merge for existing)
5. **Handle exceptions** (rollback on error)

---

**End of Document**

This comprehensive guide should help you fully understand the CRUDRepository class and its role in the Weather Report system!