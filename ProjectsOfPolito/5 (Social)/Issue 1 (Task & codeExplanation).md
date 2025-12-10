# R1 - SUBSCRIPTION TASK ANALYSIS

## EXACT TASK DESCRIPTION FROM REQUIREMENTS

```
R1 - Subscription
The interaction with the system is made using class Social.
You can register a new account using the method addPerson() which receives
as parameters a unique code, name and surname.
The method throws the exception PersonExistsException if the code
passed is already associated with a subscription.
The method getPerson() returns a string containing code, name and
surname of the person, in order, separated by blanks. If the code,
passed as a parameter, does not match any person, the method throws the
exception NoSuchCodeException.
💡 Hint:

use the Person class (already provided) to represent the person
use the repository pattern (already provided)

a PersonRepository class that provides the basic ORM-related operations
a personRepository object in the facade class that wraps the collection of Person objects
```

---

## TASKS BREAKDOWN FOR R1

### Task 1.1: Implement addPerson() method
- Accept three parameters: code (String), name (String), surname (String)
- Check if code already exists in database
- If exists: throw PersonExistsException
- If not exists: create new Person object and save to database

### Task 1.2: Implement getPerson() method
- Accept one parameter: code (String)
- Search for person with given code in database
- If not found: throw NoSuchCodeException
- If found: return formatted string "code name surname" separated by blanks

### Task 1.3: Use provided Person class
- Person class is already provided as JPA entity
- Has fields: code (ID), name, surname
- Has constructor and getters

### Task 1.4: Use repository pattern
- PersonRepository is already provided
- Extends GenericRepository<Person, String>
- Provides CRUD operations through JPA

---

## COMPLETE CODE SCRUTINY FOR R1

### FILE: Person.java

#### Original Person Class (Already Provided)
```java
@Entity
class Person {
  @Id
  private String code;
  private String name;
  private String surname;

  Person() {
    // default constructor is needed by JPA
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

  //....
}
```

#### Analysis of Person Class for R1:
- **@Entity annotation**: Marks this class as JPA entity that maps to database table
- **@Id annotation**: Marks 'code' field as primary key
- **Fields**:
  - `code`: String - unique identifier (primary key)
  - `name`: String - person's first name
  - `surname`: String - person's last name
- **Default constructor**: Required by JPA for entity instantiation
- **Parameterized constructor**: Used to create Person objects in our code
- **Getters**: Provide access to private fields
- **Package-private access**: Class and methods have package-private visibility (suitable for internal use)

**FOR R1**: This class is used AS-IS, no modifications needed. It perfectly supports both addPerson() and getPerson() operations.

---

### FILE: PersonRepository.java

#### Original PersonRepository Class (Already Provided)
```java
package social;

public class PersonRepository extends GenericRepository<Person, String> {

  public PersonRepository() {
    super(Person.class);
  }

}
```

#### Analysis of PersonRepository for R1:
- **Extends GenericRepository<Person, String>**: 
  - First type parameter: Person = entity type
  - Second type parameter: String = ID type (matches Person's code field)
- **Constructor**: Calls super with Person.class to initialize generic repository
- **Inherited methods available for R1**:
  - `findById(String code)`: Returns Optional<Person> - used in BOTH addPerson() and getPerson()
  - `save(Person person)`: Persists new person - used in addPerson()
  - `update(Person person)`: Updates existing person - NOT needed for R1
  - `delete(Person person)`: Deletes person - NOT needed for R1
  - `findAll()`: Returns all persons - NOT needed for R1

**FOR R1**: This class is used AS-IS, no modifications needed. The inherited findById() and save() methods are sufficient.

---

### FILE: Social.java

#### Code Added for R1

##### 1. Repository Instance Variable
```java
private final PersonRepository personRepository = new PersonRepository();
```

**Analysis**:
- **Type**: PersonRepository (our custom repository)
- **Modifier**: `private final` 
  - `private`: Only accessible within Social class (encapsulation)
  - `final`: Cannot be reassigned after initialization (immutable reference)
- **Initialization**: Creates new instance immediately (not lazy)
- **Purpose**: Provides access to database operations for Person entities
- **Pattern**: Dependency - Social class depends on PersonRepository

**Why this approach**:
- Simple instantiation suitable for this architecture
- Repository handles all database complexity
- Social class acts as facade, delegating persistence to repository

---

##### 2. addPerson() Method Implementation

```java
/**
 * Creates a new account for a person
 * 
 * @param code    nickname of the account
 * @param name    first name
 * @param surname last name
 * @throws PersonExistsException in case of duplicate code
 */
public void addPerson(String code, String name, String surname) throws PersonExistsException {
    if (personRepository.findById(code).isPresent()){    // check if db already contains the code
        throw new PersonExistsException();
    }
    Person person = new Person(code, name, surname);    // create the person as a POJO
    personRepository.save(person);                      // save it to db
}
```

**Line-by-Line Analysis**:

**Line 1**: `if (personRepository.findById(code).isPresent())`
- **personRepository.findById(code)**: 
  - Calls inherited method from GenericRepository
  - Executes JPA query: `SELECT p FROM Person p WHERE p.code = :code`
  - Returns: `Optional<Person>` (may contain Person or be empty)
- **.isPresent()**:
  - Method of Optional class
  - Returns: `true` if Optional contains a value, `false` if empty
  - Purpose: Check if person with this code already exists
- **if condition**: Enters block if person exists (validation check)

**Line 2**: `throw new PersonExistsException();`
- **throw**: Java keyword to throw exception
- **new PersonExistsException()**: Creates instance of custom exception
- **Purpose**: Signal to caller that operation failed due to duplicate code
- **Effect**: Method execution stops, exception propagates to caller
- **Note**: PersonExistsException is checked exception (must be declared in method signature)

**Line 4**: `Person person = new Person(code, name, surname);`
- **Person person**: Declares variable of type Person
- **new Person(code, name, surname)**: 
  - Calls parameterized constructor of Person class
  - Creates new Person object (POJO - Plain Old Java Object)
  - Object is in "transient" state (not yet in database)
- **Parameters passed**:
  - `code`: Will become the primary key (@Id field)
  - `name`: Person's first name
  - `surname`: Person's last name

**Line 5**: `personRepository.save(person);`
- **personRepository.save(person)**:
  - Calls inherited save() method from GenericRepository
  - Internally executes: `JPAUtil.transaction(em -> em.persist(person))`
  - **em.persist(person)**: JPA method that:
    - Changes object state from "transient" to "managed"
    - Generates SQL INSERT statement
    - Executes: `INSERT INTO Person (code, name, surname) VALUES (?, ?, ?)`
    - Person now exists in database
- **Return value**: void (method doesn't return anything)
- **Transaction**: Automatically handled by JPAUtil.transaction()
  - Begins transaction
  - Executes persist
  - Commits transaction
  - If error: rolls back transaction

**Method Flow Summary**:
1. Check if code exists → if YES → throw exception (stop here)
2. If code doesn't exist → continue
3. Create Person object in memory (transient state)
4. Save to database (managed/persistent state)
5. Method completes successfully (no return value)

**Exception Handling**:
- **Declared exception**: `throws PersonExistsException`
  - Must be handled by caller (checked exception)
- **Possible undeclared exceptions**:
  - Database connection errors (runtime exceptions)
  - JPA/Hibernate exceptions (wrapped by JPAUtil)

**Testing Scenarios**:
- **Scenario 1**: New unique code
  - Input: code="john123", name="John", surname="Doe"
  - Expected: Person saved successfully, no exception
- **Scenario 2**: Duplicate code
  - Input: code="john123" (already exists), name="Jane", surname="Smith"
  - Expected: PersonExistsException thrown, no database modification
- **Scenario 3**: Null values
  - Input: code=null, name="John", surname="Doe"
  - Expected: Database constraint violation (code is primary key, cannot be null)

---

##### 3. getPerson() Method Implementation

```java
/**
 * Retrieves information about the person given their account code.
 * The info consists in name and surname of the person, in order, separated by
 * blanks.
 * 
 * @param code account code
 * @return the information of the person
 * @throws NoSuchCodeException if a person with that code does not exist
 */
public String getPerson(String code) throws NoSuchCodeException {
    Optional<Person> personOpt = personRepository.findById(code); //ADDED FOR R1 - find person by code
    if (personOpt.isEmpty()) { //ADDED FOR R1 - check if person exists
      throw new NoSuchCodeException(); //ADDED FOR R1
    } //ADDED FOR R1
    Person person = personOpt.get(); //ADDED FOR R1
    return code + " " + person.getName() + " " + person.getSurname(); //ADDED FOR R1 - return formatted string
}
```

**Line-by-Line Analysis**:

**Line 1**: `Optional<Person> personOpt = personRepository.findById(code);`
- **Optional<Person>**: Java 8+ container that may or may not contain a Person object
  - Helps avoid NullPointerException
  - Forces explicit handling of "not found" case
- **personOpt**: Variable name (convention: "Opt" suffix for Optional)
- **personRepository.findById(code)**:
  - Calls inherited method from GenericRepository
  - Internally executes: `JPAUtil.withEntityManager(em -> Optional.ofNullable(em.find(Person.class, code)))`
  - **em.find(Person.class, code)**: JPA method that:
    - Generates SQL: `SELECT code, name, surname FROM Person WHERE code = ?`
    - Returns: Person object if found, null if not found
  - **Optional.ofNullable()**: Wraps result in Optional (handles null safely)
- **Result**: 
  - If person exists: Optional contains Person object
  - If person doesn't exist: Optional is empty (not null!)

**Line 2**: `if (personOpt.isEmpty())`
- **personOpt.isEmpty()**: 
  - Method of Optional class
  - Returns: `true` if Optional contains no value, `false` if contains value
  - Opposite of isPresent()
- **if condition**: Enters block if person was NOT found

**Line 3**: `throw new NoSuchCodeException();`
- **throw**: Java keyword to throw exception
- **new NoSuchCodeException()**: Creates instance of custom exception
- **Purpose**: Signal to caller that requested code doesn't exist
- **Effect**: Method execution stops, exception propagates to caller
- **Note**: NoSuchCodeException is checked exception (must be declared in method signature)

**Line 5**: `Person person = personOpt.get();`
- **Person person**: Declares variable of type Person
- **personOpt.get()**:
  - Method of Optional class
  - Extracts the Person object from Optional
  - **IMPORTANT**: Only safe to call after checking isEmpty() or isPresent()
  - If called on empty Optional: throws NoSuchElementException (we avoid this by checking first)
- **Result**: person variable now holds the Person object from database

**Line 6**: `return code + " " + person.getName() + " " + person.getSurname();`
- **Return type**: String (as declared in method signature)
- **String concatenation**: Using + operator
  - `code`: Parameter passed to method (String)
  - `" "`: Space character (String literal)
  - `person.getName()`: Calls getter, returns person's name (String)
  - `" "`: Another space character
  - `person.getSurname()`: Calls getter, returns person's surname (String)
- **Result format**: "code name surname"
  - Example: If code="john123", name="John", surname="Doe"
  - Returns: "john123 John Doe"
- **Important**: Format is specified in requirements:
  - "code, name and surname of the person, in order, separated by blanks"
  - We return code first (even though it's redundant since caller already has it)

**Method Flow Summary**:
1. Search database for person with given code
2. If not found → throw NoSuchCodeException (stop here)
3. If found → extract Person object from Optional
4. Format string: "code name surname"
5. Return formatted string to caller

**Exception Handling**:
- **Declared exception**: `throws NoSuchCodeException`
  - Must be handled by caller (checked exception)
- **Possible undeclared exceptions**:
  - Database connection errors (runtime exceptions)
  - JPA/Hibernate exceptions (wrapped by JPAUtil)

**Alternative Implementation Considerations**:
- **Current approach**: Uses Optional.isEmpty() + get()
  - Pro: Clear and explicit
  - Pro: Easy to understand for junior programmers
- **Alternative 1**: Optional.orElseThrow()
  ```java
  Person person = personRepository.findById(code)
                    .orElseThrow(() -> new NoSuchCodeException());
  return code + " " + person.getName() + " " + person.getSurname();
  ```
  - Pro: More concise, functional style
  - Con: Harder for beginners to understand lambda expressions
- **Alternative 2**: Optional.map()
  ```java
  return personRepository.findById(code)
           .map(p -> code + " " + p.getName() + " " + p.getSurname())
           .orElseThrow(() -> new NoSuchCodeException());
  ```
  - Pro: Most concise, pure functional style
  - Con: Much harder for beginners
- **Decision**: Current approach is best for junior programmers (clear and simple)

**Testing Scenarios**:
- **Scenario 1**: Person exists
  - Input: code="john123" (exists in DB with name="John", surname="Doe")
  - Expected: Returns "john123 John Doe"
- **Scenario 2**: Person doesn't exist
  - Input: code="unknown999" (not in DB)
  - Expected: NoSuchCodeException thrown
- **Scenario 3**: Null code
  - Input: code=null
  - Expected: Will find nothing (findById handles null), throws NoSuchCodeException
- **Scenario 4**: Empty string code
  - Input: code=""
  - Expected: Will find nothing (no person with empty code), throws NoSuchCodeException

**String Format Verification**:
- Requirement states: "code, name and surname of the person, in order, separated by blanks"
- Our implementation: `code + " " + person.getName() + " " + person.getSurname()`
- **Verification**: ✓ CORRECT
  - Code comes first ✓
  - Name comes second ✓
  - Surname comes third ✓
  - Separated by single space (" ") ✓
  - Order matches requirement ✓

---

## DATA FLOW DIAGRAM FOR R1

```
addPerson() Flow:
==================
User/Test → Social.addPerson(code, name, surname)
              ↓
         PersonRepository.findById(code)
              ↓
         JPAUtil.withEntityManager()
              ↓
         EntityManager.find()
              ↓
         [Database Query: SELECT * FROM Person WHERE code = ?]
              ↓
         Optional<Person>
              ↓
    ┌─────────┴──────────┐
    │                    │
 isEmpty?             isPresent?
    │                    │
    NO                  YES
    ↓                    ↓
Create Person      throw PersonExistsException
    ↓                    ↓
PersonRepository   Method ends with exception
.save(person)          (propagates to caller)
    ↓
JPAUtil.transaction()
    ↓
EntityManager.persist()
    ↓
[Database: INSERT INTO Person VALUES (...)]
    ↓
Method completes successfully
(no return value)


getPerson() Flow:
==================
User/Test → Social.getPerson(code)
              ↓
         PersonRepository.findById(code)
              ↓
         JPAUtil.withEntityManager()
              ↓
         EntityManager.find()
              ↓
         [Database Query: SELECT * FROM Person WHERE code = ?]
              ↓
         Optional<Person>
              ↓
    ┌─────────┴──────────┐
    │                    │
 isEmpty?             isPresent?
    │                    │
   YES                   NO
    ↓                    ↓
throw              Extract Person with get()
NoSuchCodeException      ↓
    ↓              Format string:
Method ends        "code name surname"
with exception          ↓
(propagates         Return string
to caller)          to caller
```

---

## DEPENDENCY ANALYSIS FOR R1

### Classes Used:
1. **Social** (main facade class)
   - Depends on: PersonRepository
   - Purpose: Provides public API for R1 operations

2. **PersonRepository** (data access layer)
   - Depends on: GenericRepository, Person, JPAUtil
   - Purpose: Handles database operations for Person entities

3. **GenericRepository** (base repository class)
   - Depends on: JPAUtil, Jakarta Persistence API
   - Purpose: Provides generic CRUD operations

4. **Person** (entity class)
   - Depends on: Jakarta Persistence API (annotations)
   - Purpose: Represents person data and maps to database table

5. **JPAUtil** (utility class)
   - Depends on: Jakarta Persistence API, H2 Database
   - Purpose: Manages EntityManager lifecycle and transactions

### External Dependencies:
1. **Jakarta Persistence API (JPA)**
   - Annotations: @Entity, @Id
   - Interfaces: EntityManager, EntityTransaction
   - Classes: Persistence

2. **Hibernate ORM**
   - Implementation of JPA specification
   - Handles SQL generation and execution

3. **H2 Database**
   - In-memory database for testing
   - Stores Person records

4. **Java Standard Library**
   - Optional<T> class
   - String class
   - Exception handling

---

## EXCEPTION HANDLING STRATEGY FOR R1

### Custom Exceptions:

#### PersonExistsException
```java
public class PersonExistsException extends Exception {
  private static final long serialVersionUID = 1L;
}
```
- **Type**: Checked exception (extends Exception)
- **When thrown**: When trying to add person with duplicate code
- **Purpose**: Inform caller that person already exists
- **Handling**: Must be caught or declared by caller

#### NoSuchCodeException
```java
public class NoSuchCodeException extends Exception {
  private static final long serialVersionUID = 1L;
}
```
- **Type**: Checked exception (extends Exception)
- **When thrown**: When trying to get person with non-existent code
- **Purpose**: Inform caller that person doesn't exist
- **Handling**: Must be caught or declared by caller

### Exception Flow:

**addPerson() exceptions**:
1. **PersonExistsException**: 
   - Thrown explicitly by our code
   - Caller must handle
2. **RuntimeException** (potential):
   - Database connection failures
   - JPA/Hibernate errors
   - Wrapped by JPAUtil

**getPerson() exceptions**:
1. **NoSuchCodeException**:
   - Thrown explicitly by our code
   - Caller must handle
2. **RuntimeException** (potential):
   - Database connection failures
   - JPA/Hibernate errors
   - Wrapped by JPAUtil

---

## DATABASE SCHEMA FOR R1

### Person Table Structure:
```sql
CREATE TABLE Person (
    code VARCHAR(255) NOT NULL PRIMARY KEY,
    name VARCHAR(255),
    surname VARCHAR(255)
);
```

### Column Details:
- **code**: 
  - Type: VARCHAR(255)
  - Constraint: PRIMARY KEY, NOT NULL
  - Purpose: Unique identifier for person
  - Maps to: Person.code field (@Id annotation)

- **name**:
  - Type: VARCHAR(255)
  - Constraint: None (nullable)
  - Purpose: Person's first name
  - Maps to: Person.name field

- **surname**:
  - Type: VARCHAR(255)
  - Constraint: None (nullable)
  - Purpose: Person's last name
  - Maps to: Person.surname field

### SQL Operations Generated:

**For addPerson()**:
```sql
-- Step 1: Check if exists (findById)
SELECT code, name, surname 
FROM Person 
WHERE code = ?;

-- Step 2: If not exists, insert (save)
INSERT INTO Person (code, name, surname) 
VALUES (?, ?, ?);
```

**For getPerson()**:
```sql
-- Query person by code
SELECT code, name, surname 
FROM Person 
WHERE code = ?;
```

---

## COMPLETE EXAMPLE EXECUTION TRACE

### Example 1: Successful addPerson()

**Input**: `addPerson("john123", "John", "Doe")`

**Execution Trace**:
```
1. Call: Social.addPerson("john123", "John", "Doe")
2. Call: personRepository.findById("john123")
3. Call: JPAUtil.withEntityManager(em -> ...)
4. Call: em.find(Person.class, "john123")
5. Execute SQL: SELECT code, name, surname FROM Person WHERE code = 'john123'
6. Database returns: null (no person found)
7. Return: Optional.ofNullable(null) = Optional.empty()
8. Check: personOpt.isPresent() = false
9. Skip: if block (don't throw exception)
10. Create: new Person("john123", "John", "Doe")
11. Call: personRepository.save(person)
12. Call: JPAUtil.transaction(em -> em.persist(person))
13. Begin transaction
14. Call: em.persist(person)
15. Execute SQL: INSERT INTO Person (code, name, surname) VALUES ('john123', 'John', 'Doe')
16. Database stores: Person record
17. Commit transaction
18. Method completes successfully
```

**Result**: Person "john123" saved to database, no exception, method returns void

---

### Example 2: Duplicate addPerson()

**Setup**: Person "john123" already exists in database

**Input**: `addPerson("john123", "Jane", "Smith")`

**Execution Trace**:
```
1. Call: Social.addPerson("john123", "Jane", "Smith")
2. Call: personRepository.findById("john123")
3. Call: JPAUtil.withEntityManager(em -> ...)
4. Call: em.find(Person.class, "john123")
5. Execute SQL: SELECT code, name, surname FROM Person WHERE code = 'john123'
6. Database returns: Person object with code="john123", name="John", surname="Doe"
7. Return: Optional.ofNullable(person) = Optional[Person]
8. Check: personOpt.isPresent() = true
9. Enter: if block
10. Throw: new PersonExistsException()
11. Method execution stops
12. Exception propagates to caller
```

**Result**: PersonExistsException thrown, no database modification, Jane Smith not added

---

### Example 3: Successful getPerson()

**Setup**: Person "john123" exists in database with name="John", surname="Doe"

**Input**: `getPerson("john123")`

**Execution Trace**:
```
1. Call: Social.getPerson("john123")
2. Call: personRepository.findById("john123")
3. Call: JPAUtil.withEntityManager(em -> ...)
4. Call: em.find(Person.class, "john123")
5. Execute SQL: SELECT code, name, surname FROM Person WHERE code = 'john123'
6. Database returns: Person object with code="john123", name="John", surname="Doe"
7. Return: Optional.ofNullable(person) = Optional[Person]
8. Check: personOpt.isEmpty() = false
9. Skip: if block (don't throw exception)
10. Extract: Person person = personOpt.get()
11. Get: person.getName() returns "John"
12. Get: person.getSurname() returns "Doe"
13. Concatenate: "john123" + " " + "John" + " " + "Doe" = "john123 John Doe"
14. Return: "john123 John Doe"
```

**Result**: Returns string "john123 John Doe"

---

### Example 4: Person Not Found

**Setup**: Person "unknown999" does NOT exist in database

**Input**: `getPerson("unknown999")`

**Execution Trace**:
```
1. Call: Social.getPerson("unknown999")
2. Call: personRepository.findById("unknown999")
3. Call: JPAUtil.withEntityManager(em -> ...)
4. Call: em.find(Person.class, "unknown999")
5. Execute SQL: SELECT code, name, surname FROM Person WHERE code = 'unknown999'
6. Database returns: null (no person found)
7. Return: Optional.ofNullable(null) = Optional.empty()
8. Check: personOpt.isEmpty() = true
9. Enter: if block
10. Throw: new NoSuchCodeException()
11. Method execution stops
12. Exception propagates to caller
```

**Result**: NoSuchCodeException thrown, no string returned

---

## KEY DESIGN PATTERNS USED IN R1

### 1. Repository Pattern
- **Purpose**: Separate data access logic from business logic
- **Implementation**: PersonRepository class
- **Benefits**:
  - Social class doesn't know about JPA/database details
  - Easy to test (can mock repository)
  - Can change database implementation without affecting Social class

### 2. Facade Pattern
- **Purpose**: Provide simple interface to complex subsystem
- **Implementation**: Social class
- **Benefits**:
  - Hides complexity of repository, JPA, transactions
  - Simple API for users: just call addPerson() or getPerson()
  - Centralizes business logic

### 3. Data Access Object (DAO) Pattern
- **Purpose**: Abstract persistence mechanism
- **Implementation**: GenericRepository base class
- **Benefits**:
  - Reusable CRUD operations
  - Consistent interface for all entities
  - Reduces code duplication

### 4. Entity Pattern
- **Purpose**: Represent domain object that maps to database
- **Implementation**: Person class with JPA annotations
- **Benefits**:
  - Clear mapping between objects and database tables
  - Type-safe access to data
  - Object-oriented representation of data

---

## TESTING CONSIDERATIONS FOR R1

### Unit Tests Needed:

#### Test addPerson():
1. **testAddPersonSuccess**: Add person with unique code → should succeed
2. **testAddPersonDuplicateCode**: Add person with existing code → should throw PersonExistsException
3. **testAddPersonNullCode**: Add person with null code → behavior depends on JPA (likely fail)
4. **testAddPersonEmptyCode**: Add person with empty string code → should succeed (valid string)
5. **testAddPersonNullName**: Add person with null name → should succeed (name can be null)
6. **testAddPersonSpecialCharacters**: Add person with special characters in fields → should succeed

#### Test getPerson():
1. **testGetPersonSuccess**: Get existing person → should return "code name surname"
2. **testGetPersonNotFound**: Get non-existent person → should throw NoSuchCodeException
3. **testGetPersonNullCode**: Get person with null code → should throw NoSuchCodeException
4. **testGetPersonEmptyCode**: Get person with empty code → should throw NoSuchCodeException
5. **testGetPersonFormat**: Verify exact format of returned string
6. **testGetPersonWithSpacesInName**: Person with spaces in name → format should be correct

### Integration Tests Needed:
1. **testAddThenGet**: Add person, then get same person → should return correct data
2. **testAddMultiple**: Add multiple persons → all should succeed
3. **testConcurrentAdd**: Multiple threads adding same code → one should succeed, others fail
4. **testDatabasePersistence**: Add person, restart application, get person → should still exist

---

## POTENTIAL IMPROVEMENTS AND EDGE CASES

### Edge Cases to Consider:
1. **Very long strings**: What if code/name/surname is 1000 characters?
   - Current: Database column is VARCHAR(255), will truncate or fail
   - Solution: Add validation in addPerson()

2. **Special characters**: What if name contains quotes, newlines, emojis?
   - Current: JPA should handle properly, but worth testing
   - Solution: May need sanitization depending on requirements

3. **Case sensitivity**: Is "john123" different from "JOHN123"?
   - Current: Depends on database collation
   - Solution: Normalize to lowercase? Document behavior?

4. **Whitespace**: What if code is "  john123  " (with spaces)?
   - Current: Treated as different from "john123"
   - Solution: Add trim() in addPerson()?

5. **Null safety**: What if name or surname is null?
   - Current: Allowed (no NOT NULL constraint)
   - getPerson() will return: "code null null"
   - Solution: Decide if nulls should be allowed, add validation

### Code Quality Improvements:
1. **Add input validation**:
   ```java
   public void addPerson(String code, String name, String surname) {
       if (code == null || code.trim().isEmpty()) {
           throw new IllegalArgumentException("Code cannot be null or empty");
       }
       // ... rest of method
   }
   ```

2. **Add logging**:
   ```java
   public void addPerson(String code, String name, String surname) {
       logger.debug("Adding person with code: {}", code);
       // ... method implementation
       logger.info("Successfully added person: {}", code);
   }
   ```

3. **Extract string formatting**:
   ```java
   private String formatPersonInfo(String code, Person person) {
       return code + " " + person.getName() + " " + person.getSurname();
   }
   ```

---

## SUMMARY OF R1 IMPLEMENTATION

### What Was Required:
✅ Method to register new account (addPerson)
✅ Check for duplicate codes
✅ Throw PersonExistsException for duplicates
✅ Method to retrieve person information (getPerson)
✅ Return formatted string "code name surname"
✅ Throw NoSuchCodeException if not found
✅ Use Person class
✅ Use repository pattern

### What Was Implemented:
✅ All requirements met
✅ Clean, readable code
✅ Proper exception handling
✅ Database integration through JPA/Hibernate
✅ Transaction management
✅ Suitable for junior programmers (clear and simple)

### Code Quality Assessment:
- **Readability**: Excellent (clear variable names, simple logic)
- **Maintainability**: Good (follows standard patterns)
- **Testability**: Good (can mock repository)
- **Performance**: Adequate (standard JPA operations)
- **Error Handling**: Complete (all exceptions properly handled)
- **Documentation**: Good (JavaDoc comments present)

### Final Notes:
The R1 implementation is **complete, correct, and well-suited for junior programmers**. It follows Java best practices, uses standard design patterns, and properly integrates with the JPA/Hibernate framework. The code is production-ready for the requirements specified.