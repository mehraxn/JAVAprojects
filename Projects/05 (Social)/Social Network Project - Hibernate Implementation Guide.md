# Social Network Project - Hibernate Implementation Guide

## Project Overview

This project implements a social network application using **Hibernate ORM** (Object-Relational Mapping) for data persistence. The application manages users (persons), friendships, groups, and posts with full database persistence using JPA (Java Persistence API) annotations and Hibernate as the ORM provider.

---

## Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Key Design Patterns](#key-design-patterns)
3. [File Changes and Implementation](#file-changes-and-implementation)
4. [Why We Don't Use Traditional CRUD](#why-we-dont-use-traditional-crud)
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

## Why We Don't Use Traditional CRUD

### Traditional CRUD Operations:
```java
// Traditional approach (what we DON'T do)
public void addPerson(String code, String name, String surname) {
  String sql = "INSERT INTO Person (code, name, surname) VALUES (?, ?, ?)";
  // Execute SQL manually
  // Handle connections, prepared statements, result sets
  // Manual error handling
}
```

### Our Approach with Hibernate:
```java
// Object-oriented approach (what we DO)
public void addPerson(String code, String name, String surname) throws PersonExistsException {
  if (personRepository.findById(code).isPresent()) {
    throw new PersonExistsException();
  }
  Person p = new Person(code, name, surname);
  personRepository.save(p);  // Hibernate generates SQL automatically
}
```

### Reasons We Don't Use Traditional CRUD:

#### 1. **Object-Relational Impedance Mismatch**
```
Object-Oriented World          Relational Database World
├── Objects                    ├── Tables
├── References                 ├── Foreign Keys
├── Inheritance                ├── No native inheritance
├── Collections                ├── Join Tables
└── Encapsulation              └── Normalized data
```

**Problem with SQL CRUD:**
```java
// To get a person with all their friends, you'd need:
// 1. Query person table
// 2. Query join table (person_friends)
// 3. Query person table again for friend details
// 4. Manually construct Person objects
// 5. Manually populate friends collection
```

**With Hibernate:**
```java
Person p = personRepository.findById("P001").get();
Set<Person> friends = p.getFriends();  // Automatic lazy loading!
```

#### 2. **Relationship Management Complexity**

**Without Hibernate:**
```java
public void addFriendship(String code1, String code2) {
  // Step 1: Manually insert into PERSON_FRIENDS table
  String sql1 = "INSERT INTO PERSON_FRIENDS (Person_code, friends_code) VALUES (?, ?)";
  executeSQL(sql1, code1, code2);
  
  // Step 2: Insert reverse relationship (bidirectional)
  String sql2 = "INSERT INTO PERSON_FRIENDS (Person_code, friends_code) VALUES (?, ?)";
  executeSQL(sql2, code2, code1);
  
  // Step 3: Handle duplicate prevention
  // Step 4: Handle transaction management
  // Step 5: Handle errors and rollback
}
```

**With Hibernate:**
```java
public void addFriendship(String code1, String code2) throws NoSuchCodeException {
  JPAUtil.executeInTransaction(() -> {
    Person p1 = personRepository.findById(code1).orElseThrow(NoSuchCodeException::new);
    Person p2 = personRepository.findById(code2).orElseThrow(NoSuchCodeException::new);
    
    p1.addFriend(p2);  // Hibernate handles the join table!
    p2.addFriend(p1);
    
    personRepository.update(p1);
    personRepository.update(p2);
  });
}
```

#### 3. **Automatic SQL Generation**

Hibernate generates optimal SQL based on operations:

```java
// When you call personRepository.save(person)
// Hibernate generates:
INSERT INTO Person (code, name, surname) VALUES (?, ?, ?)

// When you call personRepository.update(person)
// Hibernate generates:
UPDATE Person SET name = ?, surname = ? WHERE code = ?

// When you access p.getFriends()
// Hibernate generates (on demand):
SELECT f.* FROM Person f 
JOIN PERSON_FRIENDS pf ON f.code = pf.friends_code 
WHERE pf.Person_code = ?
```

You never write these SQL queries manually!

#### 4. **Lazy Loading**

```java
Person p = personRepository.findById("P001").get();
// At this point, only Person data is loaded

Set<Person> friends = p.getFriends();
// NOW Hibernate loads friends (only when accessed)

Set<Group> groups = p.getGroups();
// NOW Hibernate loads groups (only when accessed)
```

**Why This Matters:**
- Loads data only when needed
- Reduces memory usage
- Improves performance
- Would require complex caching with manual SQL

#### 5. **Transaction Management**

**Manual CRUD Approach:**
```java
Connection conn = null;
try {
  conn = getConnection();
  conn.setAutoCommit(false);
  
  // Execute multiple SQL statements
  executeSQL1(conn);
  executeSQL2(conn);
  executeSQL3(conn);
  
  conn.commit();
} catch (Exception e) {
  if (conn != null) conn.rollback();
  throw e;
} finally {
  if (conn != null) conn.close();
}
```

**Hibernate Approach:**
```java
JPAUtil.executeInTransaction(() -> {
  // All operations automatically in one transaction
  operation1();
  operation2();
  operation3();
  // Automatic commit or rollback
});
```

#### 6. **Database Independence**

With Hibernate, changing databases requires NO code changes:

```xml
<!-- Switch from H2 to MySQL -->
<property name="jakarta.persistence.jdbc.driver" value="com.mysql.cj.jdbc.Driver"/>
<property name="jakarta.persistence.jdbc.url" value="jdbc:mysql://localhost:3306/social"/>
```

With manual SQL, you'd need to:
- Rewrite SQL for different dialects
- Handle database-specific features
- Test everything again

#### 7. **Complex Queries Are Simpler**

**Requirement:** Get all posts from friends of a user, sorted by timestamp

**Manual SQL:**
```java
String sql = """
  SELECT p.* FROM Post p
  JOIN Person author ON p.author_code = author.code
  JOIN PERSON_FRIENDS pf ON author.code = pf.friends_code
  WHERE pf.Person_code = ?
  ORDER BY p.timestamp DESC
  LIMIT ? OFFSET ?
  """;
// Then manually map ResultSet to Post objects
```

**Hibernate JPQL:**
```java
String jpql = """
  SELECT p FROM Post p 
  WHERE p.author IN (
    SELECT f FROM Person u JOIN u.friends f WHERE u.code = :userCode
  )
  ORDER BY p.timestamp DESC
  """;
List<Post> posts = em.createQuery(jpql, Post.class)
  .setParameter("userCode", userCode)
  .setFirstResult(offset)
  .setMaxResults(pageLength)
  .getResultList();
// Returns Post objects directly!
```

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

| Aspect | Traditional CRUD | Hibernate ORM |
|--------|------------------|---------------|
| **Code Volume** | High (lots of SQL) | Low (object-oriented) |
| **Relationship Handling** | Manual joins | Automatic |
| **Transaction Management** | Manual | Automatic |
| **Database Independence** | Low | High |
| **Type Safety** | Low (strings) | High (objects) |
| **Lazy Loading** | Manual caching | Built-in |
| **SQL Generation** | Manual | Automatic |
| **Error Handling** | Complex | Simplified |
| **Maintainability** | Difficult | Easy |

---

## Key Takeaways

1. **Hibernate abstracts database complexity** - You work with objects, not SQL
2. **Repository pattern provides clean separation** - Business logic doesn't know about databases
3. **Annotations define relationships** - @Entity, @Id, @ManyToMany, @OneToMany
4. **JPAUtil manages lifecycle** - EntityManager and transactions handled automatically
5. **No manual SQL** - Hibernate generates optimized queries
6. **Lazy loading optimizes performance** - Data loaded only when needed
7. **Database independence** - Switch databases without code changes
8. **Transaction safety** - All-or-nothing operations guaranteed

---

## Conclusion

This project demonstrates **modern ORM-based development** rather than traditional CRUD operations. The benefits include:

- **Less code** (no SQL statements)
- **Safer code** (automatic transaction management)
- **Maintainable code** (clear object-oriented design)
- **Portable code** (database-independent)
- **Performant code** (optimized SQL generation and lazy loading)

The Repository pattern combined with Hibernate ORM provides a robust, scalable solution for data persistence in Java applications.