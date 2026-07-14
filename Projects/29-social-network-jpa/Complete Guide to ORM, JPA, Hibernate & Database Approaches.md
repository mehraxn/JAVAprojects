# Social Network Project - Hibernate Implementation Guide

## Project Overview

This project implements a social network application using **Hibernate ORM** (Object-Relational Mapping) for data persistence. The application manages users (persons), friendships, groups, and posts with full database persistence using JPA (Java Persistence API) annotations and Hibernate as the ORM provider.

---

## Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Key Design Patterns](#key-design-patterns)
3. [File Changes and Implementation](#file-changes-and-implementation)
4. [Do We Use CRUD in This Project?](#do-we-use-crud-in-this-project-yes-we-do)
5. [Hibernate Configuration](#hibernate-configuration)
6. [Relationship Mappings](#relationship-mappings)
7. [Transaction Management](#transaction-management)

---

## Architecture Overview

The project follows a **layered architecture**:

```
┌─────────────────────────────┐
│   Facade Layer (Social)     │  ← Main interface for users
└─────────────────────────────┘
            ↓
┌─────────────────────────────┐
│   Repository Layer          │  ← Data access abstraction
│   (GenericRepository)       │
└─────────────────────────────┘
            ↓
┌─────────────────────────────┐
│   JPA/Hibernate Layer       │  ← ORM framework
│   (JPAUtil)                 │
└─────────────────────────────┘
            ↓
┌─────────────────────────────┐
│   Database (H2/MySQL/etc)   │  ← Actual data storage
└─────────────────────────────┘
```

---

## Key Design Patterns

### 1. **Repository Pattern**
Instead of direct database access, we use repositories to abstract data operations:
- `GenericRepository<E, I>` - Base repository with common operations
- `PersonRepository` - Specific repository for Person entities
- `GroupRepository` - Specific repository for Group entities
- `PostRepository` - Specific repository for Post entities

### 2. **Facade Pattern**
The `Social` class acts as a facade, providing a simplified interface to the complex subsystem.

### 3. **Entity Manager Pattern**
`JPAUtil` manages EntityManager lifecycle and transactions.

---

## File Changes and Implementation

### 1. **Person.java** (Entity Class)

**What Changed:**
```java
@Entity  // Mark this class as a JPA entity
class Person {
  @Id  // Mark 'code' as the primary key
  private String code;
  
  @ManyToMany  // Many persons can have many friends
  private Set<Person> friends = new HashSet<>();
  
  @ManyToMany  // Many persons can belong to many groups
  private Set<Group> groups = new HashSet<>();
  
  @OneToMany(mappedBy = "author")  // One person can have many posts
  private Set<Post> posts = new HashSet<>();
}
```

**Why These Changes:**

1. **@Entity**: Tells Hibernate this class should be mapped to a database table
   - Table name defaults to "Person"
   - Each instance represents one row in the table

2. **@Id**: Defines the primary key
   - The `code` field uniquely identifies each person
   - Hibernate uses this for all database operations

3. **@ManyToMany for friends**:
   - Represents bidirectional friendship
   - Hibernate creates a join table: `PERSON_FRIENDS`
   - Columns: `Person_code` (FK), `friends_code` (FK)
   
4. **@ManyToMany for groups**:
   - A person can join multiple groups
   - A group can have multiple members
   - Join table: `PERSON_GROUPS`

5. **@OneToMany for posts**:
   - One person authors many posts
   - `mappedBy = "author"` means the Post entity owns the relationship
   - Hibernate won't create an extra join table (Post has the foreign key)

**No Default Constructor Issue:**
```java
Person() {
  // default constructor is needed by JPA
}
```
JPA/Hibernate requires a no-argument constructor to create instances via reflection when loading from database.

---

### 2. **Group.java** (New Entity Class)

**Implementation:**
```java
@Entity
class Group {
  @Id
  private String name;  // Group name is the primary key
  
  @ManyToMany(mappedBy = "groups")
  private Set<Person> members = new HashSet<>();
  
  Group() {}  // JPA requirement
  
  Group(String name) {
    this.name = name;
  }
}
```

**Why:**
- Groups need to persist in the database
- `mappedBy = "groups"` indicates Person owns the relationship (avoids duplicate join table)
- The bidirectional relationship is maintained: Person → Groups ↔ Group → Members

---

### 3. **Post.java** (New Entity Class)

**Implementation:**
```java
@Entity
class Post {
  @Id
  private String id;  // Unique post identifier
  
  @ManyToOne
  @JoinColumn(name = "author_code")
  private Person author;  // Who created this post
  
  private String content;
  private long timestamp;
  
  Post() {}
  
  Post(String id, Person author, String content) {
    this.id = id;
    this.author = author;
    this.content = content;
    this.timestamp = System.currentTimeMillis();
  }
}
```

**Why:**
- **@ManyToOne**: Many posts belong to one author
- **@JoinColumn**: Specifies the foreign key column name in the Post table
- Posts are persisted with automatic timestamp generation
- The relationship is owned by Post (it has the foreign key)

---

### 4. **Social.java** (Facade Class)

**Key Implementation Patterns:**

```java
public class Social {
  private PersonRepository personRepository = new PersonRepository();
  private GroupRepository groupRepository = new GroupRepository();
  private PostRepository postRepository = new PostRepository();
  
  // Example method showing transaction usage
  public void addFriendship(String code1, String code2) throws NoSuchCodeException {
    JPAUtil.executeInTransaction(() -> {
      // All operations in one transaction
      Person p1 = personRepository.findById(code1)
        .orElseThrow(NoSuchCodeException::new);
      Person p2 = personRepository.findById(code2)
        .orElseThrow(NoSuchCodeException::new);
      
      // Bidirectional relationship
      p1.addFriend(p2);
      p2.addFriend(p1);
      
      // Update both persons
      personRepository.update(p1);
      personRepository.update(p2);
    });
  }
}
```

**Why:**
- Uses repositories instead of direct database access
- `JPAUtil.executeInTransaction()` ensures all operations succeed or fail together
- Maintains referential integrity through transactions

---

### 5. **GenericRepository.java** (Already Provided)

**This is the core abstraction layer:**

```java
public class GenericRepository<E, I> {
  // E = Entity type (Person, Group, Post)
  // I = ID type (String for all our entities)
  
  public Optional<E> findById(I id) {
    return JPAUtil.withEntityManager(
      em -> Optional.ofNullable(em.find(entityClass, id))
    );
  }
  
  public List<E> findAll() {
    return JPAUtil.withEntityManager(
      em -> em.createQuery("SELECT e FROM " + entityName + " e", entityClass)
              .getResultList()
    );
  }
  
  public void save(E entity) {
    JPAUtil.transaction(em -> em.persist(entity));
  }
  
  public void update(E entity) {
    JPAUtil.transaction(em -> em.merge(entity));
  }
  
  public void delete(E entity) {
    JPAUtil.transaction(em -> em.remove(em.contains(entity) ? entity : em.merge(entity)));
  }
}
```

**Why This Design:**
- **Generic**: Works for any entity type (Person, Group, Post)
- **Encapsulation**: Hides JPA/Hibernate complexity
- **Reusability**: All specific repositories (PersonRepository, GroupRepository) inherit these methods
- **Transaction Management**: Each method handles transactions via JPAUtil

---

### 6. **JPAUtil.java** (Already Provided)

**Key Responsibilities:**

1. **EntityManager Lifecycle Management**
```java
public static EntityManager getEntityManager() {
  // Creates or returns existing EntityManager
  // EntityManager is like a "database session"
}
```

2. **Transaction Management**
```java
public static <X extends Exception> void transaction(ThrowingConsumer<EntityManager,X> action) throws X {
  // Begins transaction
  // Executes action
  // Commits or rolls back on error
}
```

3. **Context Management**
```java
public static <T, E extends Exception> T executeInContext(ThrowingSupplier<T,E> action) throws E {
  // Keeps EntityManager open for multiple operations
  // Allows lazy loading of relationships
}
```

**Why This Matters:**
- **Automatic Transaction Handling**: No manual begin/commit/rollback
- **Thread Safety**: Uses ThreadLocal for EntityManager
- **Resource Management**: Automatic cleanup of database connections
- **Test Mode Support**: Switches to in-memory H2 database for testing

---

## Do We Use CRUD in This Project? YES, We Do!

### Understanding CRUD Operations

**CRUD stands for:**
- **C**reate - Adding new data
- **R**ead - Retrieving data  
- **U**pdate - Modifying existing data
- **D**elete - Removing data

### We Absolutely Use CRUD - Look at GenericRepository:

```java
public class GenericRepository<E, I> {
  
  public void save(E entity) {           // CREATE operation
    JPAUtil.transaction(em -> em.persist(entity));
  }
  
  public Optional<E> findById(I id) {    // READ operation
    return JPAUtil.withEntityManager(
      em -> Optional.ofNullable(em.find(entityClass, id))
    );
  }
  
  public List<E> findAll() {             // READ operation (all records)
    return JPAUtil.withEntityManager(
      em -> em.createQuery("SELECT e FROM " + entityName + " e", entityClass)
              .getResultList()
    );
  }
  
  public void update(E entity) {         // UPDATE operation
    JPAUtil.transaction(em -> em.merge(entity));
  }
  
  public void delete(E entity) {         // DELETE operation
    JPAUtil.transaction(em -> em.remove(em.contains(entity) ? entity : em.merge(entity)));
  }
}
```

**These ARE CRUD operations!** The Repository pattern is built on CRUD.

### So What's Different? Manual SQL vs ORM-Based CRUD

The real question is: **Why don't we write CRUD with manual SQL queries?**

#### Traditional Manual SQL CRUD:
```java
// Traditional approach (what we DON'T do)
public void addPerson(String code, String name, String surname) {
  Connection conn = null;
  PreparedStatement stmt = null;
  try {
    conn = DriverManager.getConnection(DB_URL, USER, PASS);
    String sql = "INSERT INTO Person (code, name, surname) VALUES (?, ?, ?)";
    stmt = conn.prepareStatement(sql);
    stmt.setString(1, code);
    stmt.setString(2, name);
    stmt.setString(3, surname);
    stmt.executeUpdate();
  } catch (SQLException e) {
    e.printStackTrace();
  } finally {
    // Close resources
    if (stmt != null) stmt.close();
    if (conn != null) conn.close();
  }
}
```

#### Our Hibernate ORM-Based CRUD:
```java
// Object-oriented approach (what we DO)
public void addPerson(String code, String name, String surname) throws PersonExistsException {
  if (personRepository.findById(code).isPresent()) {
    throw new PersonExistsException();
  }
  Person p = new Person(code, name, surname);
  personRepository.save(p);  // CRUD CREATE - Hibernate generates SQL automatically!
}
```

### Why We Use ORM-Based CRUD Instead of Manual SQL CRUD:


#### 1. **No Manual SQL Management**

**Manual SQL CRUD Requires:**
```java
// CREATE
String insertSQL = "INSERT INTO Person (code, name, surname) VALUES (?, ?, ?)";
PreparedStatement pstmt = conn.prepareStatement(insertSQL);
pstmt.setString(1, code);
pstmt.setString(2, name);
pstmt.setString(3, surname);
pstmt.executeUpdate();

// READ
String selectSQL = "SELECT * FROM Person WHERE code = ?";
PreparedStatement pstmt = conn.prepareStatement(selectSQL);
pstmt.setString(1, code);
ResultSet rs = pstmt.executeQuery();
if (rs.next()) {
  Person p = new Person();
  p.setCode(rs.getString("code"));
  p.setName(rs.getString("name"));
  p.setSurname(rs.getString("surname"));
}

// UPDATE
String updateSQL = "UPDATE Person SET name = ?, surname = ? WHERE code = ?";
// ... more boilerplate

// DELETE
String deleteSQL = "DELETE FROM Person WHERE code = ?";
// ... more boilerplate
```

**Hibernate ORM CRUD:**
```java
// CREATE - Hibernate generates: INSERT INTO Person (code, name, surname) VALUES (?, ?, ?)
personRepository.save(person);

// READ - Hibernate generates: SELECT * FROM Person WHERE code = ?
Optional<Person> p = personRepository.findById(code);

// UPDATE - Hibernate generates: UPDATE Person SET name = ?, surname = ? WHERE code = ?
personRepository.update(person);

// DELETE - Hibernate generates: DELETE FROM Person WHERE code = ?
personRepository.delete(person);
```

**Why This Matters:**
- ✅ No SQL strings to write and maintain
- ✅ No PreparedStatement management
- ✅ No ResultSet mapping to objects
- ✅ No resource cleanup (connections, statements)
- ✅ Hibernate generates optimal SQL automatically

---

#### 2. **Complex CRUD Operations: Relationships**

**The Real Challenge:** CRUD becomes extremely complex with relationships.

**Example: Adding a friendship (bidirectional ManyToMany)**

**Manual SQL CRUD Approach:**
```java
public void addFriendship(String code1, String code2) throws SQLException {
  Connection conn = null;
  try {
    conn = getConnection();
    conn.setAutoCommit(false);  // Start transaction
    
    // Step 1: Check if both persons exist (READ)
    String checkSQL = "SELECT code FROM Person WHERE code = ?";
    PreparedStatement checkStmt1 = conn.prepareStatement(checkSQL);
    checkStmt1.setString(1, code1);
    ResultSet rs1 = checkStmt1.executeQuery();
    if (!rs1.next()) throw new NoSuchCodeException();
    
    PreparedStatement checkStmt2 = conn.prepareStatement(checkSQL);
    checkStmt2.setString(1, code2);
    ResultSet rs2 = checkStmt2.executeQuery();
    if (!rs2.next()) throw new NoSuchCodeException();
    
    // Step 2: Check if friendship already exists (READ)
    String checkFriendSQL = "SELECT * FROM PERSON_FRIENDS WHERE Person_code = ? AND friends_code = ?";
    PreparedStatement checkFriend = conn.prepareStatement(checkFriendSQL);
    checkFriend.setString(1, code1);
    checkFriend.setString(2, code2);
    ResultSet rsFriend = checkFriend.executeQuery();
    if (rsFriend.next()) {
      conn.rollback();
      return; // Already friends
    }
    
    // Step 3: Insert friendship direction 1 (CREATE)
    String insertSQL1 = "INSERT INTO PERSON_FRIENDS (Person_code, friends_code) VALUES (?, ?)";
    PreparedStatement pstmt1 = conn.prepareStatement(insertSQL1);
    pstmt1.setString(1, code1);
    pstmt1.setString(2, code2);
    pstmt1.executeUpdate();
    
    // Step 4: Insert friendship direction 2 (CREATE) - bidirectional!
    String insertSQL2 = "INSERT INTO PERSON_FRIENDS (Person_code, friends_code) VALUES (?, ?)";
    PreparedStatement pstmt2 = conn.prepareStatement(insertSQL2);
    pstmt2.setString(1, code2);
    pstmt2.setString(2, code1);
    pstmt2.executeUpdate();
    
    conn.commit();  // Commit transaction
    
  } catch (Exception e) {
    if (conn != null) conn.rollback();
    throw e;
  } finally {
    // Close all resources: 4 PreparedStatements, 3 ResultSets, 1 Connection
    // ... lots of cleanup code
  }
}
```

**Count the manual operations:** 2 READs + 1 READ + 2 CREATEs = 5 CRUD operations, all manual!

**Hibernate ORM CRUD Approach:**
```java
public void addFriendship(String code1, String code2) throws NoSuchCodeException {
  JPAUtil.executeInTransaction(() -> {
    // READ operations
    Person p1 = personRepository.findById(code1).orElseThrow(NoSuchCodeException::new);
    Person p2 = personRepository.findById(code2).orElseThrow(NoSuchCodeException::new);
    
    // Modify objects
    p1.addFriend(p2);
    p2.addFriend(p1);
    
    // UPDATE operations - Hibernate handles the join table automatically!
    personRepository.update(p1);
    personRepository.update(p2);
  });
}
```

**What Hibernate Does Automatically:**
1. ✅ Checks if persons exist (implicit)
2. ✅ Checks for duplicate friendships (implicit)
3. ✅ Inserts into PERSON_FRIENDS join table (both directions)
4. ✅ Manages the transaction
5. ✅ Rolls back on error
6. ✅ Closes all resources

**Result:** 80% less code, 100% safer, fully automatic CRUD on relationships!

---

#### 3. **CRUD with Lazy Loading - Impossible with Manual SQL**

**Scenario:** Get a person and their friends, but only load friends if actually needed.

**Manual SQL CRUD:**
```java
public Person getPerson(String code) {
  // Option 1: Load everything eagerly (wasteful)
  String sql = """
    SELECT p.*, f.* 
    FROM Person p 
    LEFT JOIN PERSON_FRIENDS pf ON p.code = pf.Person_code
    LEFT JOIN Person f ON pf.friends_code = f.code
    WHERE p.code = ?
    """;
  // Lots of data loaded even if never used
  
  // Option 2: Load on demand (complex caching required)
  // Need to implement your own caching mechanism
  // Track what's been loaded
  // Handle lazy initialization exceptions
}
```

**Hibernate ORM CRUD:**
```java
Person p = personRepository.findById("P001").get();  // READ operation
// At this point: Only Person data loaded (one SELECT query)

// ... maybe some code ...

if (needFriends) {
  Set<Person> friends = p.getFriends();  // Implicit READ operation
  // NOW Hibernate executes: SELECT ... FROM Person JOIN PERSON_FRIENDS WHERE ...
}
```

**Why This Matters:**
- ✅ Loads data only when accessed (efficient)
- ✅ No manual caching logic
- ✅ Transparent to the developer
- ✅ Works automatically with all relationships

**This is CRUD with intelligence!**

---

#### 4. **CRUD Operations with Transactions**

**Manual SQL CRUD Transaction:**
```java
public void transferPersonToNewGroup(String personCode, String oldGroup, String newGroup) {
  Connection conn = null;
  try {
    conn = getConnection();
    conn.setAutoCommit(false);
    
    // READ: Check person exists
    // READ: Check old group exists
    // READ: Check new group exists
    // DELETE: Remove from old group (in join table)
    // CREATE: Add to new group (in join table)
    
    conn.commit();
  } catch (Exception e) {
    if (conn != null) conn.rollback();
    throw e;
  } finally {
    if (conn != null) conn.close();
  }
}
```

**Hibernate ORM CRUD Transaction:**
```java
public void transferPersonToNewGroup(String personCode, String oldGroup, String newGroup) {
  JPAUtil.executeInTransaction(() -> {
    Person p = personRepository.findById(personCode).orElseThrow();      // READ
    Group oldG = groupRepository.findById(oldGroup).orElseThrow();       // READ
    Group newG = groupRepository.findById(newGroup).orElseThrow();       // READ
    
    p.getGroups().remove(oldG);  // Hibernate handles DELETE in join table
    p.getGroups().add(newG);     // Hibernate handles CREATE in join table
    
    personRepository.update(p);  // UPDATE
  });
  // Automatic commit or rollback!
}
```

**All CRUD operations are automatic, transactional, and safe!**

---

#### 5. **CRUD Statistics Queries**

**Requirement:** Find the person with the most friends (R4 requirement)

**Manual SQL CRUD:**
```java
public String personWithLargestNumberOfFriends() {
  String sql = """
    SELECT Person_code, COUNT(*) as friend_count
    FROM PERSON_FRIENDS
    GROUP BY Person_code
    ORDER BY friend_count DESC
    LIMIT 1
    """;
  // Execute query
  // Extract result
  // Return code
  // Handle edge cases (no persons, no friends)
}
```

**Hibernate ORM CRUD (using JPQL):**
```java
public String personWithLargestNumberOfFriends() {
  return JPAUtil.withEntityManager(em -> {
    String jpql = """
      SELECT p.code 
      FROM Person p 
      ORDER BY SIZE(p.friends) DESC
      """;
    return em.createQuery(jpql, String.class)
             .setMaxResults(1)
             .getSingleResult();
  });
}
```

**Benefits:**
- ✅ Object-oriented query language (JPQL)
- ✅ Works with entity relationships directly
- ✅ Database-independent (same query for MySQL, PostgreSQL, H2)
- ✅ Type-safe results

---

#### 6. **Database Independence in CRUD**

**Manual SQL CRUD:**
```java
// MySQL syntax
String sql = "SELECT * FROM Person LIMIT 10";

// If switching to Oracle, must rewrite:
String sql = "SELECT * FROM Person WHERE ROWNUM <= 10";

// If switching to SQL Server:
String sql = "SELECT TOP 10 * FROM Person";
```

**Hibernate ORM CRUD:**
```java
// Works on MySQL, PostgreSQL, Oracle, H2, SQL Server, etc.
List<Person> persons = personRepository.findAll();

// Or with JPQL pagination (database-independent)
List<Person> persons = em.createQuery("SELECT p FROM Person p", Person.class)
                        .setMaxResults(10)
                        .getResultList();
```

Hibernate translates to the correct SQL dialect automatically!

---

#### 7. **CRUD Error Handling**

**Manual SQL CRUD:**
```java
try {
  // CRUD operations
} catch (SQLException e) {
  if (e.getErrorCode() == 1062) {  // MySQL duplicate key
    throw new PersonExistsException();
  } else if (e.getErrorCode() == 23505) {  // PostgreSQL duplicate key
    throw new PersonExistsException();
  }
  // Different error codes for different databases!
}
```

**Hibernate ORM CRUD:**
```java
try {
  personRepository.save(person);
} catch (PersistenceException e) {
  if (e.getCause() instanceof ConstraintViolationException) {
    throw new PersonExistsException();
  }
}
// Same exception handling for all databases!
```

---

### Summary: We Use CRUD, But Smart CRUD

| CRUD Operation | Manual SQL | Hibernate ORM |
|----------------|------------|---------------|
| **Create** | INSERT statements | `repository.save(entity)` |
| **Read** | SELECT + ResultSet mapping | `repository.findById(id)` |
| **Update** | UPDATE statements | `repository.update(entity)` |
| **Delete** | DELETE statements | `repository.delete(entity)` |
| **Relationships** | Manual join table management | Automatic via annotations |
| **Transactions** | Manual begin/commit/rollback | Automatic via JPAUtil |
| **Lazy Loading** | Manual caching | Built-in |
| **Error Handling** | Database-specific | Unified |

### The Key Insight:

**We absolutely use CRUD operations** - they're fundamental to data management. The GenericRepository implements all CRUD operations.

**What we DON'T use:** Manual SQL-based CRUD with PreparedStatements, ResultSets, and explicit SQL strings.

**What we DO use:** ORM-based CRUD where Hibernate automatically generates and executes SQL based on object operations.

This is called **Object-Relational Mapping (ORM)** - mapping object operations (CRUD on objects) to relational operations (CRUD on database tables).

---

## Hibernate Configuration

### persistence.xml Structure

```xml
<persistence-unit name="socialPU">
  <!-- Entity Classes -->
  <class>social.Person</class>
  <class>social.Group</class>
  <class>social.Post</class>
  
  <!-- Database Connection -->
  <properties>
    <property name="jakarta.persistence.jdbc.url" value="jdbc:h2:./social"/>
    <property name="jakarta.persistence.jdbc.driver" value="org.h2.Driver"/>
    
    <!-- Hibernate Settings -->
    <property name="hibernate.hbm2ddl.auto" value="update"/>
    <!-- Creates/updates tables automatically -->
    
    <property name="hibernate.show_sql" value="true"/>
    <!-- Shows generated SQL in console -->
    
    <property name="hibernate.format_sql" value="true"/>
    <!-- Makes SQL readable -->
  </properties>
</persistence-unit>
```

---

## Relationship Mappings

### 1. Person ↔ Person (Friends) - ManyToMany Bidirectional

```
Person Table          PERSON_FRIENDS (Join Table)      Person Table
┌──────────┐          ┌──────────────────────┐          ┌──────────┐
│ code     │◄────────┤ Person_code (FK)     │          │ code     │
│ name     │          │ friends_code (FK)    ├─────────►│ name     │
│ surname  │          └──────────────────────┘          │ surname  │
└──────────┘                                            └──────────┘
```

### 2. Person ↔ Group - ManyToMany Bidirectional

```
Person Table          PERSON_GROUPS (Join Table)       Group Table
┌──────────┐          ┌──────────────────────┐          ┌──────────┐
│ code     │◄────────┤ Person_code (FK)     │          │ name     │
│ name     │          │ groups_name (FK)     ├─────────►└──────────┘
│ surname  │          └──────────────────────┘
└──────────┘
```

### 3. Person → Post - OneToMany / ManyToOne

```
Person Table                             Post Table
┌──────────┐                             ┌──────────────┐
│ code (PK)│◄───────────────────────────┤ author_code  │
│ name     │                             │ id (PK)      │
│ surname  │                             │ content      │
└──────────┘                             │ timestamp    │
                                          └──────────────┘
```

---

## Transaction Management

### Transaction Scope Examples

#### Single Operation (Automatic Transaction)
```java
personRepository.save(person);
// JPAUtil.transaction() wraps this automatically
```

#### Multiple Operations (Explicit Transaction)
```java
JPAUtil.executeInTransaction(() -> {
  Person p = personRepository.findById(code).orElseThrow();
  p.addFriend(friend);
  personRepository.update(p);
  personRepository.update(friend);
  // All succeed or all rollback
});
```

#### With Return Value
```java
List<Post> posts = JPAUtil.executeInContext(() -> {
  Person p = personRepository.findById(code).orElseThrow();
  return new ArrayList<>(p.getPosts());  // Can access lazy collections
});
```

---

## Summary of Benefits

| Aspect | Manual SQL CRUD | Hibernate ORM CRUD |
|--------|------------------|---------------|
| **CRUD Operations** | Manual SQL strings | Object methods (save/find/update/delete) |
| **Code Volume** | High (lots of boilerplate) | Low (object-oriented) |
| **Relationship Handling** | Manual joins & join tables | Automatic via annotations |
| **Transaction Management** | Manual begin/commit/rollback | Automatic via JPAUtil |
| **Database Independence** | Low (SQL dialect specific) | High (Hibernate abstracts) |
| **Type Safety** | Low (string queries) | High (objects & JPQL) |
| **Lazy Loading** | Manual caching required | Built-in automatic |
| **SQL Generation** | Manual writing | Automatic generation |
| **Error Handling** | Database-specific codes | Unified exceptions |
| **Maintainability** | Difficult (scattered SQL) | Easy (centralized in entities) |

---

## Key Takeaways

1. **We DO use CRUD operations** - Create, Read, Update, Delete are fundamental
2. **CRUD through ORM, not manual SQL** - GenericRepository provides CRUD via Hibernate
3. **Hibernate abstracts database complexity** - You work with objects, Hibernate generates SQL
4. **Repository pattern provides clean separation** - Business logic doesn't write SQL
5. **Annotations define relationships** - @Entity, @Id, @ManyToMany, @OneToMany tell Hibernate how to map
6. **JPAUtil manages lifecycle** - EntityManager and transactions handled automatically
7. **Automatic SQL generation** - Hibernate creates optimized queries based on operations
8. **Lazy loading optimizes performance** - Related data loaded only when accessed
9. **Database independence** - Switch databases without changing Java code
10. **Transaction safety** - All-or-nothing operations guaranteed

---

## Conclusion

This project demonstrates **modern ORM-based CRUD development** rather than traditional manual SQL CRUD. 

**The CRUD operations are the same** (Create, Read, Update, Delete), but the **implementation is completely different**:

- ❌ **No manual SQL strings** - Hibernate generates them
- ❌ **No PreparedStatement management** - EntityManager handles it
- ❌ **No ResultSet mapping** - Objects returned directly
- ❌ **No connection pooling code** - Hibernate manages it
- ❌ **No transaction boilerplate** - JPAUtil handles it

Instead, we have:

- ✅ **Clean object operations** - `repository.save(person)`
- ✅ **Automatic relationship management** - `person.addFriend(friend)`
- ✅ **Type-safe queries** - JPQL instead of SQL strings
- ✅ **Database independence** - Change DB with configuration only
- ✅ **Built-in lazy loading** - Performance optimization automatic

**The benefits:**

- **Less code** (no SQL statements)
- **Safer code** (automatic transaction management)
- **Maintainable code** (clear object-oriented design)
- **Portable code** (database-independent)
- **Performant code** (optimized SQL generation and lazy loading)

The Repository pattern combined with Hibernate ORM provides a robust, scalable solution for CRUD operations in Java applications.