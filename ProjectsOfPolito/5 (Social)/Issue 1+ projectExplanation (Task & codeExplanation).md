# Social Network Application - Complete Project Guide

## Table of Contents
1. [Project Overview](#project-overview)
2. [Architecture & Design Patterns](#architecture--design-patterns)
3. [Technology Stack](#technology-stack)
4. [Project Structure](#project-structure)
5. [R1: Subscription Feature - Deep Dive](#r1-subscription-feature---deep-dive)
6. [Implementation Guide](#implementation-guide)
7. [Testing R1](#testing-r1)

---

## Project Overview

### What is This Project?

This is a **Social Network Application** built using Java and Hibernate ORM. Think of it like building a simplified version of Facebook or LinkedIn, where you can:

- Create user accounts (profiles)
- Add friends (connections)
- Create groups (communities)
- Post content (status updates)
- View statistics about the network

The project is divided into 5 main requirements (R1-R5), each adding more functionality:

| Requirement | Feature | Description |
|-------------|---------|-------------|
| **R1** | **Subscription** | Register users and retrieve their information |
| R2 | Friends | Add bidirectional friendships between users |
| R3 | Groups | Create and manage groups, add members |
| R4 | Statistics | Get insights (most popular users, largest groups) |
| R5 | Posts | Create and retrieve posts with pagination |

### Why Use Hibernate ORM?

**ORM (Object-Relational Mapping)** is a technique that lets you:
- Work with Java objects instead of writing SQL queries
- Automatically convert between Java classes and database tables
- Save objects to the database without writing INSERT statements
- Retrieve objects without writing SELECT statements

**Example without ORM:**
```java
// You'd have to write SQL manually
String sql = "INSERT INTO persons (code, name, surname) VALUES (?, ?, ?)";
PreparedStatement ps = connection.prepareStatement(sql);
ps.setString(1, code);
ps.setString(2, name);
ps.setString(3, surname);
ps.executeUpdate();
```

**With ORM (Hibernate):**
```java
// Just create an object and save it!
Person person = new Person(code, name, surname);
personRepository.save(person);
```

---

## Architecture & Design Patterns

### 1. **Facade Pattern** (Main Entry Point)

The `Social` class acts as a **Facade** - a simplified interface that hides the complexity of the system.

```
User Code → Social.java (Facade) → Repositories → Database
```

**Why?** Users don't need to know about repositories, transactions, or entity managers. They just call simple methods like `addPerson()`.

### 2. **Repository Pattern** (Data Access Layer)

Repositories handle all database operations. This separates business logic from data access.

```
Social (Business Logic)
   ↓
PersonRepository (Data Access)
   ↓
Database (PostgreSQL/H2/MySQL)
```

**Benefits:**
- Easy to test (can mock repositories)
- Can switch databases without changing business logic
- Centralizes all database queries

### 3. **Entity Pattern** (Domain Models)

Entities are Java classes that represent database tables. They use JPA annotations to define the mapping.

```java
@Entity  // This class maps to a database table
class Person {
    @Id  // This is the primary key
    private String code;
    
    private String name;
    private String surname;
}
```

---

## Technology Stack

### Core Technologies

| Technology | Purpose | Version |
|-----------|---------|---------|
| **Java** | Programming Language | 17+ |
| **Jakarta EE (JPA)** | Java Persistence API | 3.x |
| **Hibernate** | ORM Implementation | 6.x |
| **H2 Database** | In-memory database (testing) | Latest |

### Key Libraries

- **jakarta.persistence**: Provides JPA annotations and interfaces
- **Hibernate Core**: Implements JPA specification
- **H2**: Lightweight database for testing

---

## Project Structure

```
social/
│
├── Social.java                    # Facade class (main interface)
│   └── Methods: addPerson(), getPerson(), addFriendship(), etc.
│
├── Person.java                    # Entity class
│   └── Represents a user in the database
│
├── PersonRepository.java          # Data access for Person entities
│   └── Extends GenericRepository
│
├── GenericRepository.java         # Base repository class
│   └── Provides CRUD operations (Create, Read, Update, Delete)
│
├── JPAUtil.java                   # Utility for managing EntityManager
│   └── Handles transactions and database connections
│
└── Exception Classes
    ├── PersonExistsException.java
    ├── NoSuchCodeException.java
    └── GroupExistsException.java
```

### Data Flow Example

```
User calls: social.addPerson("john123", "John", "Doe")
     ↓
Social.addPerson() checks if code exists
     ↓
Creates new Person object
     ↓
PersonRepository.save() persists to database
     ↓
JPAUtil manages EntityManager and transaction
     ↓
Hibernate generates SQL: INSERT INTO Person VALUES (...)
     ↓
Database stores the record
```

---

## R1: Subscription Feature - Deep Dive

### What Does R1 Do?

R1 implements the **user registration and retrieval system**. It allows:

1. **Adding new users** to the social network
2. **Retrieving user information** by their unique code
3. **Error handling** for duplicate users and missing users

### Requirements Summary

| Requirement | Method | Input | Output | Exception |
|------------|--------|-------|--------|-----------|
| Register user | `addPerson()` | code, name, surname | void | `PersonExistsException` if code exists |
| Get user info | `getPerson()` | code | "code name surname" | `NoSuchCodeException` if not found |

---

## Implementation Guide

### Step 1: Understanding the Person Entity

The `Person` class is a **JPA Entity** that maps to a database table.

```java
@Entity
class Person {
  @Id
  private String code;      // Primary Key (unique identifier)
  private String name;      // First name
  private String surname;   // Last name

  @ManyToMany
  private Set<Person> friends = new HashSet<>();

  @ManyToMany
  private Set<Group> groups = new HashSet<>();

  @OneToMany(mappedBy = "author")
  private Set<Post> posts = new HashSet<>();

  Person() {
    // Default constructor required by JPA
  }

  Person(String code, String name, String surname) {
    this.code = code;
    this.name = name;
    this.surname = surname;
  }

  String getCode() {
    return code;
  }

  String getName() {
    return name;
  }

  String getSurname() {
    return surname;
  }
}
```

#### Key JPA Annotations Explained

| Annotation | Purpose | Example |
|-----------|---------|---------|
| `@Entity` | Marks class as a database table | `Person` table created automatically |
| `@Id` | Marks primary key field | `code` becomes the unique identifier |
| `@ManyToMany` | Defines many-to-many relationship | One person has many friends; each friend is also a person |
| `@OneToMany` | Defines one-to-many relationship | One person has many posts |

**Database Table Created:**
```sql
CREATE TABLE Person (
    code VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255),
    surname VARCHAR(255)
);
```

---

### Step 2: Understanding PersonRepository

The repository provides CRUD operations for Person entities.

```java
public class PersonRepository extends GenericRepository<Person, String> {

  public PersonRepository() {
    super(Person.class);
  }

}
```

#### What Does It Inherit?

From `GenericRepository<Person, String>`:
- `Person`: The entity type
- `String`: The type of the ID (code field)

#### Available Methods

| Method | Purpose | Example |
|--------|---------|---------|
| `findById(String id)` | Find person by code | `Optional<Person> p = repo.findById("john123")` |
| `findAll()` | Get all persons | `List<Person> all = repo.findAll()` |
| `save(Person p)` | Insert new person | `repo.save(person)` |
| `update(Person p)` | Update existing person | `repo.update(person)` |
| `delete(Person p)` | Delete person | `repo.delete(person)` |

---

### Step 3: Understanding the Social Facade

The `Social` class is the **main interface** users interact with.

```java
public class Social {

  private final PersonRepository personRepository = new PersonRepository();
  
  // R1 Methods:
  public void addPerson(String code, String name, String surname) 
      throws PersonExistsException { ... }
  
  public String getPerson(String code) 
      throws NoSuchCodeException { ... }
}
```

---

### Step 4: Implementing addPerson() - Line by Line

```java
public void addPerson(String code, String name, String surname) 
    throws PersonExistsException {
    
    // Line 35: Check if person already exists
    if (personRepository.findById(code).isPresent()){
        throw new PersonExistsException();
    }
    
    // Line 38: Create new Person object (POJO)
    Person person = new Person(code, name, surname);
    
    // Line 39: Save to database
    personRepository.save(person);
}
```

#### Detailed Explanation

**Line 35: Existence Check**
```java
if (personRepository.findById(code).isPresent()){
```

- `findById(code)` → Returns `Optional<Person>`
- `.isPresent()` → Returns `true` if person exists, `false` otherwise
- If exists → throw exception (business rule: no duplicate codes)

**Why Optional?**
`Optional` is a Java container that may or may not contain a value. It prevents `NullPointerException`.

```java
// Instead of:
Person p = findById("john123");
if (p != null) { ... }  // Could forget this check!

// Use Optional:
Optional<Person> opt = findById("john123");
if (opt.isPresent()) { ... }  // Forces you to check
```

**Line 38: Create Person Object**
```java
Person person = new Person(code, name, surname);
```

- Creates a new `Person` object in memory
- At this point, it's **NOT** in the database yet
- It's just a regular Java object (POJO = Plain Old Java Object)

**Line 39: Persist to Database**
```java
personRepository.save(person);
```

This line does several things behind the scenes:

1. **JPAUtil.transaction()** starts a database transaction
2. **EntityManager.persist()** tells Hibernate to save the object
3. **Hibernate generates SQL**: `INSERT INTO Person VALUES ('john123', 'John', 'Doe')`
4. **Transaction commits** - changes are permanently saved
5. **EntityManager closes** - resources are released

#### Execution Flow Diagram

```
addPerson("john123", "John", "Doe")
    ↓
[Check if exists]
    ↓
Repository.findById("john123")
    ↓
JPAUtil.withEntityManager(...)
    ↓
EntityManager.find(Person.class, "john123")
    ↓
SQL: SELECT * FROM Person WHERE code = 'john123'
    ↓
Result: No rows found → Optional.empty()
    ↓
isPresent() → false (person doesn't exist)
    ↓
[Create Person object]
    ↓
person = new Person("john123", "John", "Doe")
    ↓
[Save to database]
    ↓
Repository.save(person)
    ↓
JPAUtil.transaction(...)
    ↓
EntityManager.persist(person)
    ↓
SQL: INSERT INTO Person (code, name, surname) VALUES ('john123', 'John', 'Doe')
    ↓
Transaction commits
    ↓
Success! Person is now in database
```

---

### Step 5: Implementing getPerson() - Line by Line

```java
public String getPerson(String code) throws NoSuchCodeException {
    
    // Line 51: Try to find person by code
    Person p = personRepository.findById(code).orElse(null);
    
    // Line 52: If not found, throw exception
    if (p == null) throw new NoSuchCodeException();
    
    // Line 53: Return formatted string
    return p.getCode() + " " + p.getName() + " " + p.getSurname();
}
```

#### Detailed Explanation

**Line 51: Retrieve Person**
```java
Person p = personRepository.findById(code).orElse(null);
```

- `findById(code)` → Returns `Optional<Person>`
- `.orElse(null)` → If present, return the person; otherwise return `null`

**Alternative ways to handle Optional:**

```java
// Method 1: orElse
Person p = findById(code).orElse(null);
if (p == null) throw exception;

// Method 2: orElseThrow (more elegant)
Person p = findById(code)
    .orElseThrow(() -> new NoSuchCodeException());

// Method 3: ifPresent
findById(code).ifPresent(p -> {
    // Do something with person
});
```

**Line 52: Validation**
```java
if (p == null) throw new NoSuchCodeException();
```

- If person not found, throw exception
- This is a **checked exception** (must be declared in method signature)

**Line 53: Format Output**
```java
return p.getCode() + " " + p.getName() + " " + p.getSurname();
```

- Concatenates code, name, and surname with spaces
- Example: `"john123 John Doe"`

#### Execution Flow Diagram

```
getPerson("john123")
    ↓
[Search database]
    ↓
Repository.findById("john123")
    ↓
JPAUtil.withEntityManager(...)
    ↓
EntityManager.find(Person.class, "john123")
    ↓
SQL: SELECT * FROM Person WHERE code = 'john123'
    ↓
Result: Found row → Person(code="john123", name="John", surname="Doe")
    ↓
Optional<Person> containing the person
    ↓
.orElse(null) → Returns the person
    ↓
p != null → continue
    ↓
Format string: "john123" + " " + "John" + " " + "Doe"
    ↓
Return: "john123 John Doe"
```

---

### Step 6: Understanding Exception Handling

#### PersonExistsException

```java
public class PersonExistsException extends Exception {
  private static final long serialVersionUID = 1L;
}
```

**When thrown?**
- When trying to add a person with a code that already exists

**Example scenario:**
```java
social.addPerson("john123", "John", "Doe");    // ✓ Success
social.addPerson("john123", "Jane", "Smith");  // ✗ PersonExistsException!
```

#### NoSuchCodeException

```java
public class NoSuchCodeException extends Exception {
	private static final long serialVersionUID = 1L;
}
```

**When thrown?**
- When trying to retrieve or operate on a person that doesn't exist

**Example scenario:**
```java
String info = social.getPerson("unknown123");  // ✗ NoSuchCodeException!
```

#### Why Checked Exceptions?

These are **checked exceptions** (extend `Exception`), which means:

1. **Must be declared** in method signature: `throws PersonExistsException`
2. **Must be handled** by caller using try-catch or declaring throws
3. **Compiler enforces** this - won't compile without handling

**Alternative: Unchecked Exceptions**
```java
// Unchecked - extends RuntimeException
public class PersonExistsException extends RuntimeException { }

// No need to declare or catch
public void addPerson(String code, String name, String surname) {
    // No throws clause needed
}
```

---

### Step 7: Understanding JPAUtil

The `JPAUtil` class manages the JPA infrastructure.

#### Key Methods

**1. getEntityManager()**
```java
public static EntityManager getEntityManager() {
    // Creates or retrieves an EntityManager
    // EntityManager is the JPA interface for interacting with database
}
```

**2. withEntityManager()**
```java
public static <T> T withEntityManager(Function<EntityManager, T> action) {
    EntityManager em = getEntityManager();
    try {
        return action.apply(em);
    } finally {
        closeEntityManager();
    }
}
```

**Usage in repository:**
```java
public Optional<E> findById(I id) {
    return JPAUtil.withEntityManager(
        em -> Optional.ofNullable(em.find(entityClass, id))
    );
}
```

**What happens:**
1. Get EntityManager
2. Execute: `em.find(Person.class, "john123")`
3. Return result wrapped in Optional
4. Close EntityManager

**3. transaction()**
```java
public static void transaction(ThrowingConsumer<EntityManager> action) {
    EntityManager em = getEntityManager();
    EntityTransaction tx = em.getTransaction();
    try {
        tx.begin();          // Start transaction
        action.accept(em);   // Execute operations
        tx.commit();         // Save changes
    } catch (Exception ex) {
        tx.rollback();       // Undo if error
        throw ex;
    }
}
```

**Usage in repository:**
```java
public void save(E entity) {
    JPAUtil.transaction(em -> em.persist(entity));
}
```

---

## Testing R1

### Example Test Cases

```java
public class TestR1 {
    
    @Test
    public void testAddPerson() throws PersonExistsException {
        Social social = new Social();
        
        // Should succeed
        social.addPerson("john123", "John", "Doe");
        
        // Verify person was added
        String info = social.getPerson("john123");
        assertEquals("john123 John Doe", info);
    }
    
    @Test(expected = PersonExistsException.class)
    public void testAddDuplicatePerson() throws PersonExistsException {
        Social social = new Social();
        
        // First add should succeed
        social.addPerson("john123", "John", "Doe");
        
        // Second add should throw exception
        social.addPerson("john123", "Jane", "Smith");
    }
    
    @Test(expected = NoSuchCodeException.class)
    public void testGetNonExistentPerson() throws NoSuchCodeException {
        Social social = new Social();
        
        // Should throw exception
        social.getPerson("unknown123");
    }
    
    @Test
    public void testGetPerson() throws Exception {
        Social social = new Social();
        
        // Add person
        social.addPerson("alice99", "Alice", "Smith");
        
        // Retrieve and verify
        String info = social.getPerson("alice99");
        assertEquals("alice99 Alice Smith", info);
        assertTrue(info.contains("Alice"));
        assertTrue(info.contains("Smith"));
    }
}
```

### Manual Testing

```java
public class Main {
    public static void main(String[] args) {
        Social social = new Social();
        
        try {
            // Test 1: Add person
            System.out.println("Adding John...");
            social.addPerson("john123", "John", "Doe");
            System.out.println("✓ Success!");
            
            // Test 2: Get person
            System.out.println("Retrieving John...");
            String info = social.getPerson("john123");
            System.out.println("Person info: " + info);
            System.out.println("✓ Success!");
            
            // Test 3: Try duplicate
            System.out.println("Trying to add duplicate...");
            social.addPerson("john123", "Jane", "Smith");
            
        } catch (PersonExistsException e) {
            System.out.println("✗ Person already exists!");
        } catch (NoSuchCodeException e) {
            System.out.println("✗ Person not found!");
        }
    }
}
```

**Expected Output:**
```
Adding John...
*** new EntityManager
✓ Success!
Retrieving John...
*** new EntityManager
Person info: john123 John Doe
✓ Success!
Trying to add duplicate...
*** new EntityManager
✗ Person already exists!
```

---

## Common Mistakes and Solutions

### Mistake 1: Forgetting to Save

```java
// ❌ WRONG - Person not saved to database
Person person = new Person(code, name, surname);
// Missing: personRepository.save(person);
```

```java
// ✅ CORRECT
Person person = new Person(code, name, surname);
personRepository.save(person);
```

### Mistake 2: Not Checking for Null

```java
// ❌ WRONG - Could throw NullPointerException
Person p = personRepository.findById(code).orElse(null);
return p.getCode() + " " + p.getName();  // Crash if p is null!
```

```java
// ✅ CORRECT
Person p = personRepository.findById(code).orElse(null);
if (p == null) throw new NoSuchCodeException();
return p.getCode() + " " + p.getName();
```

### Mistake 3: Not Handling Exceptions

```java
// ❌ WRONG - Won't compile
public void test() {
    social.addPerson("john", "John", "Doe");  // Error: unhandled exception
}
```

```java
// ✅ CORRECT - Option 1: Try-Catch
public void test() {
    try {
        social.addPerson("john", "John", "Doe");
    } catch (PersonExistsException e) {
        System.out.println("Person already exists");
    }
}

// ✅ CORRECT - Option 2: Declare throws
public void test() throws PersonExistsException {
    social.addPerson("john", "John", "Doe");
}
```

### Mistake 4: Creating Entity Without Default Constructor

```java
// ❌ WRONG - JPA requires default constructor
@Entity
class Person {
    private String code;
    
    Person(String code) {  // Only parameterized constructor
        this.code = code;
    }
}
```

```java
// ✅ CORRECT - Add default constructor
@Entity
class Person {
    private String code;
    
    Person() {  // Default constructor for JPA
    }
    
    Person(String code) {
        this.code = code;
    }
}
```

---

## Key Takeaways

### R1 Implementation Checklist

- [x] **Person Entity**: Annotated with `@Entity` and `@Id`
- [x] **PersonRepository**: Extends `GenericRepository<Person, String>`
- [x] **addPerson() Method**:
  - Check if code exists
  - Throw `PersonExistsException` if duplicate
  - Create Person object
  - Save to database using repository
- [x] **getPerson() Method**:
  - Find person by code
  - Throw `NoSuchCodeException` if not found
  - Return formatted string: "code name surname"
- [x] **Exception Classes**: Defined as checked exceptions
- [x] **JPAUtil**: Manages EntityManager and transactions

### Design Principles Applied

1. **Separation of Concerns**: Business logic (Social) separated from data access (Repository)
2. **Single Responsibility**: Each class has one purpose
3. **Repository Pattern**: Centralizes data access
4. **Facade Pattern**: Simple interface hides complexity
5. **Exception Handling**: Clear error reporting

---

## Next Steps

After mastering R1, you can move to:

- **R2 (Friends)**: Implement bidirectional relationships using `@ManyToMany`
- **R3 (Groups)**: Create groups and manage memberships
- **R4 (Statistics)**: Query data to find interesting insights
- **R5 (Posts)**: Implement posting and pagination features

Each requirement builds on the previous one, using the same patterns and infrastructure established in R1.

---

## Additional Resources

### JPA & Hibernate
- [Jakarta EE Tutorial](https://jakarta.ee/learn/)
- [Hibernate Documentation](https://hibernate.org/orm/documentation/)
- [JPA Annotations Guide](https://jakarta.ee/specifications/persistence/3.0/apidocs/)

### Design Patterns
- [Repository Pattern](https://martinfowler.com/eaaCatalog/repository.html)
- [Facade Pattern](https://refactoring.guru/design-patterns/facade)

### Java Optional
- [Oracle Optional Guide](https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html)

---

**Happy Coding! 🚀**

*Remember: Understanding R1 thoroughly makes all other requirements easier, as they follow the same patterns and principles.*