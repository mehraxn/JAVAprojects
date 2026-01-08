# Understanding the createNetwork Method

## Complete Guide to Network Creation in Weather Report System

---

## Table of Contents
1. [Overview - What Does This Method Do?](#overview---what-does-this-method-do)
2. [The NetworkOperationsImpl Class](#the-networkoperationsimpl-class)
3. [Method Signature and Exceptions](#method-signature-and-exceptions)
4. [Line-by-Line Explanation](#line-by-line-explanation)
   - **🔐 Deep Dive: User Authorization** (validateUserIsMaintainer)
   - **✅ Deep Dive: Code Validation** (isValidNetworkCode)
   - **🗄️ Deep Dive: Repository Pattern**
   - **⏰ Deep Dive: Timestamped Metadata**
5. [Visual Data Flow](#visual-data-flow)
6. [Complete Example Scenarios](#complete-example-scenarios)
7. [Exception Handling Guide](#exception-handling-guide)
8. [Why Each Part is Necessary](#why-each-part-is-necessary)
9. [Common Questions](#common-questions)

---

## Overview - What Does This Method Do?

### High-Level Purpose

The `createNetwork` method creates a new monitoring network in the Weather Report system. It's like registering a new department in a company - you need proper authorization, a unique identifier, and some basic information.

**In Simple Terms:**
Think of it like creating a new account on a website:
1. Check if you're authorized to create accounts
2. Validate the information you provided
3. Make sure the username (code) isn't already taken
4. Create the account with all the details
5. Record who created it and when
6. Save it to the database

**In Our System:**
1. Verify the user has MAINTAINER permissions
2. Validate the network code format (NET_##)
3. Check the code isn't already in use
4. Create a new Network object
5. Set audit metadata (createdBy, createdAt, etc.)
6. Save to database and return the created network

### What is a Network?

A **Network** in the Weather Report system is:
- A logical grouping of gateways and sensors
- Identified by a unique code (format: `NET_##`)
- Can have a name and description
- Has operators who receive threshold violation alerts
- Tracks who created/modified it and when

**Example Networks:**
```
NET_01 - "North Campus Temperature Monitoring"
NET_02 - "Building A Climate Control"
NET_03 - "Outdoor Weather Stations"
```

---

## The NetworkOperationsImpl Class

### Class Overview

```java
public class NetworkOperationsImpl implements NetworkOperations {

    private final CRUDRepository<Network, String> networkRepository = new CRUDRepository<>(Network.class);
    private final CRUDRepository<Operator, String> operatorRepository = new CRUDRepository<>(Operator.class);
    private final CRUDRepository<User, String> userRepository = new CRUDRepository<>(User.class);
    
    // ... methods ...
}
```

### Understanding the Class Structure

#### The `implements NetworkOperations` Part

```java
public class NetworkOperationsImpl implements NetworkOperations {
```

**What this means:**
- `NetworkOperationsImpl` is a **concrete class** (actual implementation)
- `NetworkOperations` is an **interface** (contract/blueprint)
- The class **promises** to implement all methods defined in the interface

**Why use an interface?**

```
┌─────────────────────────────────────────┐
│         NetworkOperations               │  ← Interface (Contract)
│         (interface)                     │
├─────────────────────────────────────────┤
│ + createNetwork(...)                    │
│ + updateNetwork(...)                    │
│ + deleteNetwork(...)                    │
│ + getNetworks(...)                      │
│ + createOperator(...)                   │
│ + addOperatorToNetwork(...)             │
│ + getNetworkReport(...)                 │
└─────────────────────────────────────────┘
                    △
                    │ implements
                    │
┌─────────────────────────────────────────┐
│       NetworkOperationsImpl             │  ← Concrete Class
│            (class)                      │
├─────────────────────────────────────────┤
│ - networkRepository                     │
│ - operatorRepository                    │
│ - userRepository                        │
├─────────────────────────────────────────┤
│ + createNetwork(...) { actual code }    │
│ + updateNetwork(...) { actual code }    │
│ + ... all interface methods ...         │
│ - validateUserIsMaintainer(...) helper  │
│ - isValidNetworkCode(...) helper        │
└─────────────────────────────────────────┘
```

**Benefits:**
- 🔄 **Flexibility:** Can swap implementations without changing code that uses the interface
- 🧪 **Testability:** Can mock the interface for testing
- 📋 **Contract:** Forces implementation of all required methods
- 🏗️ **Design:** Separates "what" (interface) from "how" (implementation)

#### The Repository Fields

```java
private final CRUDRepository<Network, String> networkRepository = new CRUDRepository<>(Network.class);
private final CRUDRepository<Operator, String> operatorRepository = new CRUDRepository<>(Operator.class);
private final CRUDRepository<User, String> userRepository = new CRUDRepository<>(User.class);
```

**Breaking it down:**

| Field | Generic Types | Purpose |
|-------|---------------|---------|
| `networkRepository` | `<Network, String>` | Stores/retrieves Networks (ID is String code) |
| `operatorRepository` | `<Operator, String>` | Stores/retrieves Operators (ID is String email) |
| `userRepository` | `<User, String>` | Stores/retrieves Users (ID is String username) |

**Why `private final`?**
- `private` - Only this class can access these fields
- `final` - Can't reassign after initialization (safety)

**Visual representation:**
```
NetworkOperationsImpl
├── networkRepository ──────> [Network Database Table]
│                              | code (PK) | name | description | ... |
│                              | NET_01    | ...  | ...         | ... |
│                              | NET_02    | ...  | ...         | ... |
│
├── operatorRepository ─────> [Operator Database Table]
│                              | email (PK)       | firstName | lastName | ... |
│                              | john@example.com | John      | Doe      | ... |
│
└── userRepository ─────────> [User Database Table]
                               | username (PK) | type       |
                               | admin         | MAINTAINER |
                               | viewer1       | VIEWER     |
```

---

## Method Signature and Exceptions

### The Complete Signature

```java
@Override
public Network createNetwork(String code, String name, String description, String username)
        throws IdAlreadyInUseException, InvalidInputDataException, UnauthorizedException
```

### Breaking Down Each Part

#### `@Override`
```java
@Override
```

**What it means:**
- This method **overrides** a method from the interface/parent class
- Compiler checks that we're actually overriding something
- If we misspell the method name, compiler will error

**Without @Override (dangerous):**
```java
// Oops! Typo in method name
public Network createNetwerk(...) { }  // No error, but doesn't implement interface!
```

**With @Override (safe):**
```java
@Override
public Network createNetwerk(...) { }  // COMPILE ERROR! No method to override!
```

#### Access Modifier and Return Type
```java
public Network
```

- `public` - Anyone can call this method
- `Network` - Returns a `Network` object (the created network)

#### Method Name and Parameters
```java
createNetwork(String code, String name, String description, String username)
```

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `code` | String | ✅ Yes | Unique identifier (format: `NET_##`) |
| `name` | String | ❌ No | Human-readable name |
| `description` | String | ❌ No | Detailed description |
| `username` | String | ✅ Yes | User performing the action |

#### Exceptions Thrown
```java
throws IdAlreadyInUseException, InvalidInputDataException, UnauthorizedException
```

| Exception | When Thrown | Example |
|-----------|-------------|---------|
| `UnauthorizedException` | User doesn't exist or isn't MAINTAINER | viewer1 tries to create network |
| `InvalidInputDataException` | Code is null, empty, or wrong format | Code is "NETWORK_1" instead of "NET_01" |
| `IdAlreadyInUseException` | Code already exists in database | Creating NET_01 when it already exists |

---

## Line-by-Line Explanation

### The Complete Method

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

---

### Section 1: Authorization Check (Line 1)

#### Line 1: Validate User Authorization
```java
validateUserIsMaintainer(username);
```

**What this does:**
Calls a private helper method to verify the user is authorized to create networks.

**Why this is FIRST:**
- Security first! Always check authorization before doing anything
- Prevents unauthorized users from even attempting operations
- Fail fast - don't waste resources if user can't do this anyway

---

### 🔐 Deep Dive: validateUserIsMaintainer Method

Let's examine this helper method in detail:

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

#### Step-by-Step Breakdown

**Step 1: Check for null username**
```java
if (username == null) {
    throw new UnauthorizedException("Username is null");
}
```

**Why check for null?**
```java
// If username is null and we try:
User user = userRepository.read(null);  // Might crash or return weird results!

// Better to catch it early with a clear error message
```

**Step 2: Look up the user in database**
```java
User user = userRepository.read(username);
```

**What this does:**
- Searches the User database table for a user with this username
- Returns the `User` object if found, or `null` if not found

**Database query equivalent:**
```sql
SELECT * FROM WR_USER WHERE username = 'admin';
```

**Step 3: Check if user exists**
```java
if (user == null) {
    throw new UnauthorizedException("User " + username + " is not authorized.");
}
```

**Why this matters:**
- Username might be typed wrong
- User account might have been deleted
- Someone might be trying to use a fake username

**Step 4: Check user type**
```java
if (user.getType() != UserType.MAINTAINER) {
    throw new UnauthorizedException("User " + username + " is not authorized.");
}
```

**User Types in the system:**
```java
public enum UserType {
    VIEWER,      // Can only READ data
    MAINTAINER   // Can CREATE, UPDATE, DELETE
}
```

**Permission Matrix:**

| Action | VIEWER | MAINTAINER |
|--------|--------|------------|
| View networks | ✅ | ✅ |
| View reports | ✅ | ✅ |
| Create network | ❌ | ✅ |
| Update network | ❌ | ✅ |
| Delete network | ❌ | ✅ |
| Create operator | ❌ | ✅ |

#### Visual Flow of Authorization

```
validateUserIsMaintainer("admin")
        │
        ▼
┌───────────────────────┐
│ username == null?     │
│       NO              │
└───────────┬───────────┘
            │
            ▼
┌───────────────────────────────────┐
│ userRepository.read("admin")      │
│ → Returns User(admin, MAINTAINER) │
└───────────────┬───────────────────┘
                │
                ▼
┌───────────────────────┐
│ user == null?         │
│       NO              │
└───────────┬───────────┘
            │
            ▼
┌───────────────────────────────┐
│ user.getType() != MAINTAINER? │
│       NO (it IS MAINTAINER)   │
└───────────────┬───────────────┘
                │
                ▼
┌───────────────────────┐
│ Method returns        │
│ (no exception thrown) │
│ ✅ User is authorized │
└───────────────────────┘
```

**Failure scenario:**
```
validateUserIsMaintainer("viewer1")
        │
        ▼
┌───────────────────────┐
│ username == null?     │
│       NO              │
└───────────┬───────────┘
            │
            ▼
┌───────────────────────────────┐
│ userRepository.read("viewer1") │
│ → Returns User(viewer1, VIEWER)│
└───────────────┬───────────────┘
                │
                ▼
┌───────────────────────┐
│ user == null?         │
│       NO              │
└───────────┬───────────┘
            │
            ▼
┌───────────────────────────────┐
│ user.getType() != MAINTAINER? │
│       YES (it's VIEWER!)      │
└───────────────┬───────────────┘
                │
                ▼
┌─────────────────────────────────────┐
│ throw UnauthorizedException(        │
│   "User viewer1 is not authorized." │
│ )                                   │
│ ❌ Method exits with exception      │
└─────────────────────────────────────┘
```

---

### Section 2: Code Null/Empty Check (Lines 2-4)

```java
if (code == null || code.isEmpty()) {
    throw new InvalidInputDataException("Network code is missing.");
}
```

**What this does:**
Checks that a network code was actually provided.

**Breaking down the condition:**

```java
code == null        // Was no code passed at all?
||                  // OR
code.isEmpty()      // Was an empty string passed?
```

**Examples:**

```java
// These will throw InvalidInputDataException:
createNetwork(null, "Name", "Desc", "admin");     // code is null
createNetwork("", "Name", "Desc", "admin");       // code is empty string

// These will pass this check:
createNetwork("NET_01", "Name", "Desc", "admin"); // valid code
createNetwork("INVALID", "Name", "Desc", "admin"); // passes this check, fails next one
```

**Why check `null` before `isEmpty()`?**

```java
// If code is null:
code.isEmpty()  // NullPointerException! Can't call method on null!

// Order matters:
code == null || code.isEmpty()
     ↓
// Java uses "short-circuit evaluation"
// If first part is true, second part is NOT evaluated
// So if code is null, isEmpty() is never called!
```

**Short-circuit evaluation explained:**

```
code = null

code == null  →  true
                 ↓
       (short-circuit! stop here)
                 ↓
Result: true (throws exception)

code.isEmpty() is NEVER called! ✅ Safe!
```

---

### Section 3: Code Format Validation (Lines 5-8)

```java
boolean codeMatchesFormat = isValidNetworkCode(code);
if (!codeMatchesFormat) {
    throw new InvalidInputDataException("Invalid network code format.");
}
```

**What this does:**
Validates that the code follows the required format: `NET_` followed by two digits.

---

### ✅ Deep Dive: isValidNetworkCode Method

```java
private boolean isValidNetworkCode(String code) {
    if (code == null || code.length() != 6 || !code.startsWith("NET_")) {
        return false;
    }
    return Character.isDigit(code.charAt(4)) && Character.isDigit(code.charAt(5));
}
```

#### The Network Code Format

**Required format:** `NET_##` where `#` is a digit (0-9)

**Valid examples:**
```
NET_01 ✅
NET_99 ✅
NET_00 ✅
NET_42 ✅
```

**Invalid examples:**
```
NET_1     ❌ (only 5 characters)
NET_001   ❌ (7 characters)
NETWORK_1 ❌ (doesn't start with NET_)
NET_AB    ❌ (letters instead of digits)
net_01    ❌ (lowercase)
NET-01    ❌ (hyphen instead of underscore)
```

#### Step-by-Step Validation

**Step 1: Quick disqualification checks**
```java
if (code == null || code.length() != 6 || !code.startsWith("NET_")) {
    return false;
}
```

| Check | Purpose | Example Failure |
|-------|---------|-----------------|
| `code == null` | No code at all | `null` |
| `code.length() != 6` | Wrong length | `"NET_1"` (5 chars) |
| `!code.startsWith("NET_")` | Wrong prefix | `"NETWORK_01"` |

**Why check length exactly 6?**
```
N E T _ 0 1
0 1 2 3 4 5  ← Character positions (indices)
└─────────┘
  6 characters total
```

**Step 2: Verify last two characters are digits**
```java
return Character.isDigit(code.charAt(4)) && Character.isDigit(code.charAt(5));
```

**What is `Character.isDigit()`?**
```java
Character.isDigit('0')  // true
Character.isDigit('5')  // true
Character.isDigit('9')  // true
Character.isDigit('A')  // false
Character.isDigit('!')  // false
```

**What is `charAt(index)`?**
```java
String code = "NET_01";
//            012345

code.charAt(0)  // 'N'
code.charAt(1)  // 'E'
code.charAt(2)  // 'T'
code.charAt(3)  // '_'
code.charAt(4)  // '0'  ← First digit to check
code.charAt(5)  // '1'  ← Second digit to check
```

#### Validation Visual

```
Input: "NET_01"

Step 1: Basic checks
├── code == null?        → false ✅
├── code.length() != 6?  → false (length is 6) ✅
└── !code.startsWith("NET_")? → false (starts with NET_) ✅

Step 2: Digit checks
├── Character.isDigit(code.charAt(4))
│   └── Character.isDigit('0') → true ✅
├── Character.isDigit(code.charAt(5))
│   └── Character.isDigit('1') → true ✅
└── true && true → true ✅

Result: VALID ✅
```

```
Input: "NET_AB"

Step 1: Basic checks
├── code == null?        → false ✅
├── code.length() != 6?  → false (length is 6) ✅
└── !code.startsWith("NET_")? → false (starts with NET_) ✅

Step 2: Digit checks
├── Character.isDigit(code.charAt(4))
│   └── Character.isDigit('A') → false ❌
└── Returns false immediately (short-circuit)

Result: INVALID ❌
```

---

### Section 4: Uniqueness Check (Lines 9-12)

```java
Network existingNetwork = networkRepository.read(code);
if (existingNetwork != null) {
    throw new IdAlreadyInUseException("Network code already in use.");
}
```

**What this does:**
Checks if a network with this code already exists in the database.

#### Step-by-Step Breakdown

**Step 1: Try to find existing network**
```java
Network existingNetwork = networkRepository.read(code);
```

**What `networkRepository.read(code)` does:**
- Looks in the database for a Network with this code
- Returns the `Network` object if found
- Returns `null` if not found

**Database query equivalent:**
```sql
SELECT * FROM Network WHERE code = 'NET_01';
```

**Step 2: Check if we found one**
```java
if (existingNetwork != null) {
    throw new IdAlreadyInUseException("Network code already in use.");
}
```

**Why this matters:**
- Network codes must be UNIQUE
- Two networks can't have the same code
- This is enforced at the application level (and usually also at the database level with a unique constraint)

**Scenarios:**

```
Scenario 1: Code NET_01 doesn't exist yet
  networkRepository.read("NET_01") → null
  existingNetwork != null? → false
  → Continue execution ✅

Scenario 2: Code NET_01 already exists
  networkRepository.read("NET_01") → Network{code="NET_01", name="Existing"}
  existingNetwork != null? → true
  → throw IdAlreadyInUseException ❌
```

---

### 🗄️ Deep Dive: Repository Pattern

#### What is a Repository?

A **Repository** is a design pattern that provides an abstraction layer between your business logic and the database.

**Without Repository (bad):**
```java
// Business logic mixed with database code
public Network createNetwork(...) {
    EntityManager em = Persistence.createEntityManagerFactory("myPU").createEntityManager();
    em.getTransaction().begin();
    em.persist(network);
    em.getTransaction().commit();
    em.close();
    // Messy! Database details everywhere!
}
```

**With Repository (good):**
```java
// Clean separation
public Network createNetwork(...) {
    Network network = new Network(code, name, description);
    networkRepository.create(network);  // Repository handles the database details
    // Clean! No database code here!
}
```

#### The CRUDRepository Class

**CRUD** = Create, Read, Update, Delete

```java
CRUDRepository<Network, String> networkRepository = new CRUDRepository<>(Network.class);
//             ↑        ↑
//          Entity   ID Type
//           Type
```

**Methods available:**

| Method | Purpose | Example |
|--------|---------|---------|
| `create(entity)` | Save new entity | `repo.create(network)` |
| `read(id)` | Get by ID | `repo.read("NET_01")` |
| `read()` | Get all | `repo.read()` |
| `update(entity)` | Update existing | `repo.update(network)` |
| `delete(id)` | Delete by ID | `repo.delete("NET_01")` |

#### Visual: Repository as Middleman

```
┌─────────────────────────────────────┐
│     NetworkOperationsImpl           │
│     (Business Logic)                │
├─────────────────────────────────────┤
│ createNetwork() {                   │
│   networkRepository.create(network) │───┐
│ }                                   │   │
│                                     │   │
│ updateNetwork() {                   │   │
│   networkRepository.update(network) │───┤
│ }                                   │   │
│                                     │   │
│ getNetworks() {                     │   │
│   networkRepository.read()          │───┤
│ }                                   │   │
└─────────────────────────────────────┘   │
                                          │
              ┌───────────────────────────┘
              │
              ▼
┌─────────────────────────────────────┐
│     CRUDRepository                  │
│     (Data Access Layer)             │
├─────────────────────────────────────┤
│ create() {                          │
│   em.persist(entity);               │───┐
│ }                                   │   │
│                                     │   │
│ read() {                            │   │
│   em.find(entityClass, id);         │───┤
│ }                                   │   │
│                                     │   │
│ update() {                          │   │
│   em.merge(entity);                 │───┤
│ }                                   │   │
└─────────────────────────────────────┘   │
                                          │
              ┌───────────────────────────┘
              │
              ▼
┌─────────────────────────────────────┐
│     Database (H2)                   │
├─────────────────────────────────────┤
│ Network Table                       │
│ ┌──────┬───────┬─────────────┐      │
│ │ code │ name  │ description │      │
│ ├──────┼───────┼─────────────┤      │
│ │NET_01│ North │ North area  │      │
│ │NET_02│ South │ South area  │      │
│ └──────┴───────┴─────────────┘      │
└─────────────────────────────────────┘
```

---

### Section 5: Create Network Object (Line 13)

```java
Network newNetwork = new Network(code, name, description);
```

**What this does:**
Creates a new `Network` object in memory (not yet saved to database).

**The Network constructor:**
```java
// In Network.java (assumed):
public Network(String code, String name, String description) {
    this.code = code;
    this.name = name;
    this.description = description;
}
```

**Example:**
```java
new Network("NET_01", "North Campus", "Monitors the north campus area")

// Creates:
Network {
    code: "NET_01",
    name: "North Campus",
    description: "Monitors the north campus area",
    operators: null (not set yet),
    createdBy: null (not set yet),
    createdAt: null (not set yet),
    modifiedBy: null (not set yet),
    modifiedAt: null (not set yet)
}
```

**Important:** The network exists in memory but is NOT in the database yet!

```
Memory (RAM):                    Database:
┌───────────────────┐            ┌───────────────────┐
│ newNetwork object │            │ Network Table     │
│ ┌───────────────┐ │            │ (empty or no      │
│ │ code: NET_01  │ │            │  NET_01 row yet)  │
│ │ name: North   │ │            └───────────────────┘
│ │ desc: ...     │ │
│ └───────────────┘ │
└───────────────────┘
```

---

### Section 6: Set Audit Metadata (Lines 14-19)

```java
LocalDateTime currentTime = LocalDateTime.now();
newNetwork.setCreatedBy(username);
newNetwork.setCreatedAt(currentTime);
newNetwork.setModifiedBy(username);
newNetwork.setModifiedAt(currentTime);
```

**What this does:**
Records WHO created the network and WHEN.

---

### ⏰ Deep Dive: Timestamped Metadata

#### What is the Timestamped Class?

`Network` extends `Timestamped`, which provides audit fields:

```java
public class Timestamped {
    private String createdBy;      // Username who created
    private LocalDateTime createdAt;   // When created
    private String modifiedBy;     // Username who last modified
    private LocalDateTime modifiedAt;  // When last modified
    
    // getters and setters...
}

public class Network extends Timestamped {
    private String code;
    private String name;
    private String description;
    // ...
}
```

**Inheritance visualization:**
```
┌───────────────────────────┐
│     Timestamped           │
├───────────────────────────┤
│ - createdBy: String       │
│ - createdAt: LocalDateTime│
│ - modifiedBy: String      │
│ - modifiedAt: LocalDateTime│
├───────────────────────────┤
│ + setCreatedBy()          │
│ + setCreatedAt()          │
│ + setModifiedBy()         │
│ + setModifiedAt()         │
│ + getters...              │
└───────────────────────────┘
            △
            │ extends
            │
┌───────────────────────────┐
│        Network            │
├───────────────────────────┤
│ - code: String            │
│ - name: String            │
│ - description: String     │
│ - operators: Collection   │
├───────────────────────────┤
│ + Network(code, name, ...) │
│ + getCode(), setCode()    │
│ + getName(), setName()    │
│ + ... inherited methods   │
└───────────────────────────┘
```

#### Why Track This Information?

**Auditing purposes:**
- 📝 Know WHO made changes
- 🕐 Know WHEN changes were made
- 🔍 Investigate issues ("Who deleted network NET_01?")
- 📊 Analytics ("Which user creates the most networks?")
- 🔐 Accountability

#### Step-by-Step Metadata Setting

**Line 14: Get current time**
```java
LocalDateTime currentTime = LocalDateTime.now();
```

**What is `LocalDateTime`?**
- Java class representing date AND time
- `now()` returns the current date/time

**Example value:**
```java
LocalDateTime.now()  // → 2024-01-15T14:30:45.123
//                        Date      Time
```

**Line 15: Set creator**
```java
newNetwork.setCreatedBy(username);
```

Records the username of who created this network.

**Line 16: Set creation time**
```java
newNetwork.setCreatedAt(currentTime);
```

Records when the network was created.

**Lines 17-18: Set modifier (same as creator for new entity)**
```java
newNetwork.setModifiedBy(username);
newNetwork.setModifiedAt(currentTime);
```

**Why set modified fields on CREATE?**
- Keeps data consistent (no null values)
- The first "modification" is the creation
- Makes queries easier ("get networks modified after X date" includes newly created ones)

**After setting metadata:**
```java
Network {
    code: "NET_01",
    name: "North Campus",
    description: "Monitors the north campus area",
    operators: null,
    createdBy: "admin",           // ← SET
    createdAt: 2024-01-15T14:30:45, // ← SET
    modifiedBy: "admin",          // ← SET
    modifiedAt: 2024-01-15T14:30:45 // ← SET
}
```

---

### Section 7: Save to Database (Lines 20-21)

```java
Network createdNetwork = networkRepository.create(newNetwork);
return createdNetwork;
```

**What this does:**
Saves the network to the database and returns the saved entity.

#### Line 20: Persist to Database
```java
Network createdNetwork = networkRepository.create(newNetwork);
```

**What happens inside `create()`:**

```java
// Inside CRUDRepository.create():
public T create(T entity) {
    EntityManager em = PersistenceManager.getEntityManager();
    em.getTransaction().begin();
    em.persist(entity);           // ← INSERT into database
    em.getTransaction().commit();
    em.close();
    return entity;
}
```

**Database operation:**
```sql
INSERT INTO Network (code, name, description, createdBy, createdAt, modifiedBy, modifiedAt)
VALUES ('NET_01', 'North Campus', 'Monitors the north campus area', 'admin', '2024-01-15 14:30:45', 'admin', '2024-01-15 14:30:45');
```

**After `create()`:**
```
Memory (RAM):                    Database:
┌───────────────────┐            ┌───────────────────────────────────┐
│ createdNetwork    │            │ Network Table                     │
│ ┌───────────────┐ │            │ ┌──────┬───────┬──────┬─────────┐ │
│ │ code: NET_01  │ │  ←─────────│ │NET_01│ North │ ...  │ admin   │ │
│ │ name: North   │ │  synced!   │ │      │Campus │      │         │ │
│ │ desc: ...     │ │            │ └──────┴───────┴──────┴─────────┘ │
│ │ createdBy:    │ │            └───────────────────────────────────┘
│ │   admin       │ │
│ └───────────────┘ │
└───────────────────┘
```

#### Line 21: Return the Created Network
```java
return createdNetwork;
```

**Why return the network?**
- Caller might need the created object
- Confirms creation was successful
- Allows chaining operations

**Example usage:**
```java
Network network = operations.createNetwork("NET_01", "North", "Description", "admin");
System.out.println("Created: " + network.getCode());  // "Created: NET_01"
```

---

## Visual Data Flow

### Complete Method Flow Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│  createNetwork("NET_01", "North Campus", "Description", "admin")            │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  STEP 1: Authorization Check                                                │
│  validateUserIsMaintainer("admin")                                          │
│  ├─ username == null? NO                                                    │
│  ├─ User exists? YES (admin found)                                          │
│  └─ User is MAINTAINER? YES ✅                                              │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  STEP 2: Code Null/Empty Check                                              │
│  code == null || code.isEmpty()?                                            │
│  "NET_01" == null? NO                                                       │
│  "NET_01".isEmpty()? NO ✅                                                   │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  STEP 3: Code Format Validation                                             │
│  isValidNetworkCode("NET_01")                                               │
│  ├─ Length == 6? YES                                                        │
│  ├─ Starts with "NET_"? YES                                                 │
│  ├─ Position 4 is digit? YES ('0')                                          │
│  └─ Position 5 is digit? YES ('1') ✅                                        │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  STEP 4: Uniqueness Check                                                   │
│  networkRepository.read("NET_01")                                           │
│  └─ Returns null (doesn't exist yet) ✅                                      │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  STEP 5: Create Network Object                                              │
│  new Network("NET_01", "North Campus", "Description")                       │
│  └─ Creates object in memory (not saved yet)                                │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  STEP 6: Set Audit Metadata                                                 │
│  currentTime = LocalDateTime.now()                                          │
│  setCreatedBy("admin")                                                      │
│  setCreatedAt(currentTime)                                                  │
│  setModifiedBy("admin")                                                     │
│  setModifiedAt(currentTime)                                                 │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  STEP 7: Save to Database                                                   │
│  networkRepository.create(newNetwork)                                       │
│  └─ INSERT INTO Network VALUES (...) ✅                                      │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  STEP 8: Return Created Network                                             │
│  return createdNetwork; ✅                                                   │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## Complete Example Scenarios

### Scenario 1: Successful Creation ✅

**Input:**
```java
createNetwork("NET_01", "North Campus", "Monitors north campus temperature", "admin")
```

**User in database:**
```
| username | type       |
|----------|------------|
| admin    | MAINTAINER |
```

**Networks in database (before):**
```
| code   | name  | description |
|--------|-------|-------------|
| (empty table)              |
```

**Execution:**
```
1. validateUserIsMaintainer("admin") → PASS (admin is MAINTAINER)
2. code null/empty? → NO, "NET_01" is valid
3. isValidNetworkCode("NET_01") → true
4. networkRepository.read("NET_01") → null (doesn't exist)
5. new Network("NET_01", "North Campus", "Monitors...")
6. Set createdBy=admin, createdAt=now, modifiedBy=admin, modifiedAt=now
7. networkRepository.create(network) → SAVED
8. return network
```

**Networks in database (after):**
```
| code   | name         | description                    | createdBy | createdAt           |
|--------|--------------|--------------------------------|-----------|---------------------|
| NET_01 | North Campus | Monitors north campus temp... | admin     | 2024-01-15 14:30:45 |
```

**Result:** Network created successfully! ✅

---

### Scenario 2: Unauthorized User ❌

**Input:**
```java
createNetwork("NET_02", "South Campus", "Description", "viewer1")
```

**User in database:**
```
| username | type   |
|----------|--------|
| viewer1  | VIEWER |
```

**Execution:**
```
1. validateUserIsMaintainer("viewer1")
   ├─ username null? NO
   ├─ user exists? YES (viewer1 found)
   └─ user.getType() == MAINTAINER? NO! (it's VIEWER)
   
   → throw UnauthorizedException("User viewer1 is not authorized.")
```

**Result:** ❌ UnauthorizedException - User doesn't have permission

---

### Scenario 3: Invalid Code Format ❌

**Input:**
```java
createNetwork("NETWORK_01", "East Campus", "Description", "admin")
```

**Execution:**
```
1. validateUserIsMaintainer("admin") → PASS
2. code null/empty? → NO
3. isValidNetworkCode("NETWORK_01")
   ├─ code.length() == 6? → NO! (length is 10)
   └─ return false
   
   → throw InvalidInputDataException("Invalid network code format.")
```

**Result:** ❌ InvalidInputDataException - Code doesn't match NET_## format

---

### Scenario 4: Duplicate Code ❌

**Input:**
```java
createNetwork("NET_01", "West Campus", "Description", "admin")
```

**Networks already in database:**
```
| code   | name         |
|--------|--------------|
| NET_01 | North Campus |  ← Already exists!
```

**Execution:**
```
1. validateUserIsMaintainer("admin") → PASS
2. code null/empty? → NO
3. isValidNetworkCode("NET_01") → true
4. networkRepository.read("NET_01") → Network{code="NET_01", name="North Campus"}
   existingNetwork != null? → YES!
   
   → throw IdAlreadyInUseException("Network code already in use.")
```

**Result:** ❌ IdAlreadyInUseException - Code NET_01 is already taken

---

### Scenario 5: Null Code ❌

**Input:**
```java
createNetwork(null, "New Network", "Description", "admin")
```

**Execution:**
```
1. validateUserIsMaintainer("admin") → PASS
2. code == null? → YES!
   
   → throw InvalidInputDataException("Network code is missing.")
```

**Result:** ❌ InvalidInputDataException - No code provided

---

## Exception Handling Guide

### Exception Hierarchy

```
Exception
└── WeatherReportException
    ├── UnauthorizedException (errorCode: 400)
    ├── InvalidInputDataException (errorCode: 200)
    ├── IdAlreadyInUseException (errorCode: 300)
    └── ElementNotFoundException (errorCode: 100)
```

### When Each Exception is Thrown

```
┌─────────────────────────────────────────────────────────────────┐
│                    createNetwork()                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │ validateUserIsMaintainer(username)                        │  │
│  │  ├─ username == null → UnauthorizedException              │  │
│  │  ├─ user not found → UnauthorizedException                │  │
│  │  └─ user not MAINTAINER → UnauthorizedException           │  │
│  └───────────────────────────────────────────────────────────┘  │
│                           │                                     │
│                           ▼                                     │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │ code == null || code.isEmpty()                            │  │
│  │  └─ true → InvalidInputDataException                      │  │
│  └───────────────────────────────────────────────────────────┘  │
│                           │                                     │
│                           ▼                                     │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │ !isValidNetworkCode(code)                                 │  │
│  │  └─ true → InvalidInputDataException                      │  │
│  └───────────────────────────────────────────────────────────┘  │
│                           │                                     │
│                           ▼                                     │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │ existingNetwork != null                                   │  │
│  │  └─ true → IdAlreadyInUseException                        │  │
│  └───────────────────────────────────────────────────────────┘  │
│                           │                                     │
│                           ▼                                     │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │ Create, set metadata, save, return                        │  │
│  │  └─ Success! Return Network                               │  │
│  └───────────────────────────────────────────────────────────┘  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### Quick Reference Table

| Condition | Exception | Message |
|-----------|-----------|---------|
| `username == null` | UnauthorizedException | "Username is null" |
| User doesn't exist | UnauthorizedException | "User X is not authorized." |
| User is VIEWER | UnauthorizedException | "User X is not authorized." |
| `code == null` or `code.isEmpty()` | InvalidInputDataException | "Network code is missing." |
| Code doesn't match `NET_##` | InvalidInputDataException | "Invalid network code format." |
| Code already exists in DB | IdAlreadyInUseException | "Network code already in use." |

---

## Why Each Part is Necessary

### Summary Table

| Step | Code | Purpose | What If Skipped? |
|------|------|---------|------------------|
| 1 | `validateUserIsMaintainer()` | Security check | Unauthorized users could create networks |
| 2 | `code == null \|\| code.isEmpty()` | Input validation | NullPointerException or invalid data |
| 3 | `isValidNetworkCode()` | Format validation | Inconsistent codes like "NETWORK_123" |
| 4 | `networkRepository.read()` | Uniqueness check | Duplicate networks, database errors |
| 5 | `new Network(...)` | Object creation | Nothing to save |
| 6 | `setCreatedBy()`, etc. | Audit trail | No tracking of who/when |
| 7 | `networkRepository.create()` | Persistence | Data lost when program ends |
| 8 | `return createdNetwork` | Feedback | Caller doesn't know result |

### Validation Order Matters!

**Why this order?**

```
1. Authorization FIRST
   ↓ (don't waste resources on unauthorized users)
2. Null/Empty check
   ↓ (prevent NullPointerException in next steps)
3. Format validation
   ↓ (ensure consistent data format)
4. Uniqueness check
   ↓ (prevent duplicate entries)
5-8. Create and save
```

**Wrong order example:**
```java
// BAD: Check uniqueness before authorization
Network existing = networkRepository.read(code);  // Wasted DB call!
if (existing != null) throw ...;
validateUserIsMaintainer(username);  // User wasn't even authorized!
```

---

## Common Questions

### Q1: Why validate authorization first?

**A:** Security best practice - "fail fast" on security issues:
- Don't reveal information to unauthorized users
- Don't waste resources (database queries) for unauthorized requests
- Authorization errors should be caught immediately

### Q2: Why is `name` and `description` optional but `code` required?

**A:** 
- `code` is the **unique identifier** - networks are identified by code
- `name` and `description` are **descriptive** - nice to have, not essential
- A network can exist with just a code: `createNetwork("NET_01", null, null, "admin")`

### Q3: What happens if the database save fails?

**A:** The `networkRepository.create()` method handles database transactions:
- If save fails, transaction is rolled back
- An exception propagates up to the caller
- Network is NOT partially saved

### Q4: Why set `modifiedBy`/`modifiedAt` on creation?

**A:** Consistency and convenience:
- No null values in audit fields
- First "modification" is creation
- Simplifies queries ("find networks modified after date X" includes new ones)

### Q5: Could two users create the same network simultaneously?

**A:** Possible race condition! Mitigation strategies:
- Database unique constraint on `code` column
- Optimistic locking
- Transaction isolation levels

```
Time →
User A: read(NET_01) → null → create(NET_01)
User B:      read(NET_01) → null → create(NET_01) → ERROR!
                                   ↑ Database constraint violation
```

### Q6: Why use `CRUDRepository` instead of direct database access?

**A:** Separation of concerns:
- Business logic doesn't know about Hibernate/JPA
- Easy to test (mock the repository)
- Easy to change database implementation
- Cleaner, more maintainable code

### Q7: What's the difference between `InvalidInputDataException` and `IdAlreadyInUseException`?

**A:** 
- **InvalidInputDataException**: Data format/content is wrong (null, empty, wrong format)
- **IdAlreadyInUseException**: Data is valid but conflicts with existing data (duplicate code)

---

## Summary

### What This Method Does

```
INPUT:   code, name, description, username
         ↓
PROCESS: Validate user authorization
         Validate code exists and format
         Check code uniqueness
         Create Network object
         Set audit metadata
         Save to database
         ↓
OUTPUT:  Created Network object
         OR Exception if validation fails
```

### Key Concepts

1. **Authorization First** - Always check permissions before processing
2. **Input Validation** - Validate all required fields
3. **Format Validation** - Ensure data matches expected patterns
4. **Uniqueness Check** - Prevent duplicates
5. **Audit Trail** - Track who/when for all changes
6. **Repository Pattern** - Separate business logic from database code

### The Method in One Sentence

> "Verify the user can create networks, validate the code format and uniqueness, create a Network with audit metadata, save it to the database, and return the created network."

---

**End of Document**