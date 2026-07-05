# Complete Code Analysis for R1 (Subscription Feature)

## Quick Answer

**No, `getPerson()` is NOT the only code for R1!**

R1 consists of **multiple components** across different files. The "ADDED FOR R1" comments only appear on `getPerson()` because `addPerson()` was likely provided as a **reference implementation** or **skeleton code**.

---

## All Code Components for R1

### 1. **Social.java** - Two Methods

#### Method 1: `addPerson()` ✓ Part of R1
```java
public void addPerson(String code, String name, String surname) throws PersonExistsException {
    // Simple checks do not need transaction wrapper if they are single operations
    if (personRepository.findById(code).isPresent()){    // check if db already contains the code
        throw new PersonExistsException();
    }
    Person person = new Person(code, name, surname);    // create the person as a POJO
    personRepository.save(person);                      // save it to db
}
```

**Why no "ADDED FOR R1" comment?**
- Likely provided as **reference implementation** for students
- Shows the pattern students should follow
- May have been completed by instructor/provided code

#### Method 2: `getPerson()` ✓ Part of R1
```java
public String getPerson(String code) throws NoSuchCodeException {
    Person p = personRepository.findById(code).orElse(null);       // ADDED FOR R1
    if (p == null) throw new NoSuchCodeException();                // ADDED FOR R1
    return p.getCode() + " " + p.getName() + " " + p.getSurname(); // ADDED FOR R1
}
```

**Why has "ADDED FOR R1" comment?**
- This is what **students implemented**
- Following the pattern from `addPerson()`
- Students wrote these 3 lines themselves

---

### 2. **Person.java** - Entity Class

```java
package social;

import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

@Entity  // ← Required for R1
class Person {
  @Id  // ← Required for R1
  private String code;       // ← Required for R1
  private String name;       // ← Required for R1
  private String surname;    // ← Required for R1

  @ManyToMany
  private Set<Person> friends = new HashSet<>();  // For R2

  @ManyToMany
  private Set<Group> groups = new HashSet<>();    // For R3

  @OneToMany(mappedBy = "author")
  private Set<Post> posts = new HashSet<>();      // For R5

  Person() {  // ← Required for R1 (JPA needs this)
    // default constructor is needed by JPA
  }

  Person(String code, String name, String surname) {  // ← Required for R1
    this.code = code;
    this.name = name;
    this.surname = surname;
  }

  String getCode() {      // ← Required for R1
    return code;
  }

  String getName() {      // ← Required for R1
    return name;
  }

  String getSurname() {   // ← Required for R1
    return surname;
  }

  // Methods for R2, R3, R5...
}
```

**What's needed for R1:**
- `@Entity` annotation - marks class as database entity
- `@Id` annotation - marks primary key
- Three fields: `code`, `name`, `surname`
- Default constructor (for JPA)
- Parameterized constructor (for our code)
- Three getter methods

**Not needed for R1:**
- `friends` field (for R2)
- `groups` field (for R3)
- `posts` field (for R5)
- Methods like `addFriend()`, `addGroup()`, etc.

---

### 3. **PersonRepository.java** - Data Access Layer

```java
package social;

public class PersonRepository extends GenericRepository<Person, String> {

  public PersonRepository() {
    super(Person.class);
  }

}
```

**Entire file is required for R1:**
- Extends `GenericRepository<Person, String>`
- Provides CRUD operations through inheritance
- No additional methods needed for R1

---

### 4. **Exception Classes**

#### PersonExistsException.java ✓ Required for R1
```java
package social;

public class PersonExistsException extends Exception {
  private static final long serialVersionUID = 1L;
}
```

#### NoSuchCodeException.java ✓ Required for R1
```java
package social;

public class NoSuchCodeException extends Exception {
	private static final long serialVersionUID = 1L;
}
```

**Both exceptions are needed for R1:**
- `PersonExistsException` - thrown by `addPerson()`
- `NoSuchCodeException` - thrown by `getPerson()`

---

### 5. **Infrastructure Code** (Provided/Pre-existing)

#### GenericRepository.java ✓ Used by R1
```java
package social;

public class GenericRepository<E, I> {
    // CRUD methods that PersonRepository inherits
    public Optional<E> findById(I id) { ... }
    public void save(E entity) { ... }
    public void update(E entity) { ... }
    public void delete(E entity) { ... }
    public List<E> findAll() { ... }
}
```

**Not written for R1, but essential:**
- Provides data access methods
- Already implemented
- Used by `PersonRepository`

#### JPAUtil.java ✓ Used by R1
```java
package social;

public class JPAUtil {
    // Transaction and EntityManager management
    public static EntityManager getEntityManager() { ... }
    public static <T> T withEntityManager(...) { ... }
    public static void transaction(...) { ... }
}
```

**Not written for R1, but essential:**
- Manages database connections
- Handles transactions
- Already implemented

---

## Complete R1 Code Checklist

### Files You Need to Create/Modify for R1:

| File | What to Add | Status |
|------|-------------|--------|
| **Person.java** | Entity class with `@Entity`, `@Id`, fields, constructors, getters | ✓ Provided or students create |
| **PersonRepository.java** | Extend `GenericRepository<Person, String>` | ✓ Provided or students create |
| **PersonExistsException.java** | Exception class extending `Exception` | ✓ Provided or students create |
| **NoSuchCodeException.java** | Exception class extending `Exception` | ✓ Provided or students create |
| **Social.java** | `addPerson()` method | ✓ Provided as reference |
| **Social.java** | `getPerson()` method | **✓ Students implement** |

### Infrastructure (Pre-existing):

| File | Purpose | Status |
|------|---------|--------|
| **GenericRepository.java** | Base repository with CRUD | ✓ Provided |
| **JPAUtil.java** | JPA utility for transactions | ✓ Provided |

---

## Why Comments Only on `getPerson()`?

### Scenario 1: Incremental Teaching
```java
// Step 1: Instructor provides addPerson() as example
public void addPerson(String code, String name, String surname) 
    throws PersonExistsException {
    // Full implementation shown as example
}

// Step 2: Students implement getPerson() following the pattern
public String getPerson(String code) throws NoSuchCodeException {
    // Students write this themselves
    Person p = personRepository.findById(code).orElse(null); // ADDED FOR R1
    if (p == null) throw new NoSuchCodeException();          // ADDED FOR R1
    return p.getCode() + " " + p.getName() + " " + p.getSurname(); // ADDED FOR R1
}
```

### Scenario 2: Testing Strategy
```java
// Provided code (already tested)
public void addPerson(...) {
    // Works correctly, no need to mark
}

// Student code (needs to be tested)
public String getPerson(...) {
    // Marked so instructor knows what to grade
    // ADDED FOR R1 indicates student work
}
```

---

## What Students Actually Write for R1

Based on the comment markers, students likely write:

### 1. In Social.java
```java
public String getPerson(String code) throws NoSuchCodeException {
    Person p = personRepository.findById(code).orElse(null);       // Line 1
    if (p == null) throw new NoSuchCodeException();                // Line 2
    return p.getCode() + " " + p.getName() + " " + p.getSurname(); // Line 3
}
```

### 2. Possibly the Entity and Repository (if not provided)
- Complete Person.java entity class
- Complete PersonRepository.java
- Exception classes

---

## Line-by-Line: What Each Line Does

### In `addPerson()` (Provided as Reference)

```java
// Line 1: Check if person already exists
if (personRepository.findById(code).isPresent()){
    // findById returns Optional<Person>
    // isPresent() returns true if person found
    
// Line 2: Throw exception if duplicate
    throw new PersonExistsException();
}

// Line 3: Create new Person object in memory
Person person = new Person(code, name, surname);

// Line 4: Save to database
personRepository.save(person);
    // Triggers: JPAUtil.transaction() → EntityManager.persist() → INSERT SQL
```

### In `getPerson()` (Students Write This)

```java
// Line 1: Try to find person in database
Person p = personRepository.findById(code).orElse(null);
    // findById returns Optional<Person>
    // orElse(null) extracts value or returns null if not found

// Line 2: Validate result
if (p == null) throw new NoSuchCodeException();
    // If person not found, throw exception

// Line 3: Format and return result
return p.getCode() + " " + p.getName() + " " + p.getSurname();
    // Concatenate fields with spaces
    // Example: "john123 John Doe"
```

---

## Common Misconceptions

### ❌ Misconception 1: "Only getPerson() is R1"
**Reality:** R1 includes both `addPerson()` and `getPerson()`, plus all supporting code (entities, repositories, exceptions)

### ❌ Misconception 2: "Comments mark all R1 code"
**Reality:** Comments only mark what **students wrote**. Pre-existing or reference code isn't marked.

### ❌ Misconception 3: "I only need to write 3 lines"
**Reality:** You need all the infrastructure:
- Person entity
- PersonRepository
- Exception classes
- Both methods in Social.java

---

## Testing R1 - Both Methods Required

```java
@Test
public void testR1Complete() throws Exception {
    Social social = new Social();
    
    // Test 1: addPerson() - Add a person
    social.addPerson("john123", "John", "Doe");
    
    // Test 2: getPerson() - Retrieve the person
    String info = social.getPerson("john123");
    
    // Verify both methods work together
    assertEquals("john123 John Doe", info);
    
    // Test 3: addPerson() - Try duplicate
    try {
        social.addPerson("john123", "Jane", "Smith");
        fail("Should throw PersonExistsException");
    } catch (PersonExistsException e) {
        // Expected
    }
    
    // Test 4: getPerson() - Try non-existent
    try {
        social.getPerson("unknown");
        fail("Should throw NoSuchCodeException");
    } catch (NoSuchCodeException e) {
        // Expected
    }
}
```

**Both methods must work for R1 to be complete!**

---

## Summary Table: R1 Code Components

| Component | File | Lines of Code | Who Writes | Comments |
|-----------|------|---------------|------------|----------|
| Entity | Person.java | ~40 lines | Provided or Students | Core R1 code |
| Repository | PersonRepository.java | ~6 lines | Provided or Students | Core R1 code |
| Exception 1 | PersonExistsException.java | ~4 lines | Provided or Students | Core R1 code |
| Exception 2 | NoSuchCodeException.java | ~4 lines | Provided or Students | Core R1 code |
| Add Method | Social.java | 8 lines | Provided as reference | Core R1 functionality |
| Get Method | Social.java | 3 lines | **Students write** | **Marked with comments** |
| Infrastructure | GenericRepository.java | ~80 lines | Pre-existing | Not part of assignment |
| Infrastructure | JPAUtil.java | ~200 lines | Pre-existing | Not part of assignment |

**Total student-written code for R1:** Approximately 50-60 lines (if all components need to be created)

**Minimum student-written code:** 3 lines (if only `getPerson()` needs implementation)

---

## Conclusion

**R1 is NOT just 3 lines of code!**

While only `getPerson()` has "ADDED FOR R1" comments, the complete R1 implementation includes:

1. ✓ Person entity class (entire file)
2. ✓ PersonRepository class (entire file)  
3. ✓ Two exception classes (entire files)
4. ✓ `addPerson()` method (8 lines in Social.java)
5. ✓ `getPerson()` method (3 lines in Social.java - marked with comments)

The comments indicate what **students implemented themselves**, but the requirement includes all the supporting infrastructure needed to make the system work.

Think of it like building a car:
- The **engine** (infrastructure) is provided
- The **chassis** (Person entity, repositories) might be provided or you build it
- The **steering wheel** (`addPerson()`) is shown as an example
- The **gas pedal** (`getPerson()`) is what you implement

But you need **all parts** for the car to run! 🚗