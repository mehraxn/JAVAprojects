# Social Network Application - R2 & R3 Implementation Guide

## Table of Contents
1. [R2: Friends Feature](#r2-friends-feature)
   - [Requirements](#r2-requirements)
   - [Code Implementation](#r2-code-implementation)
   - [Deep Code Analysis](#r2-deep-code-analysis)
   - [Questions & Answers](#r2-questions--answers)
2. [R3: Groups Feature](#r3-groups-feature)
   - [Requirements](#r3-requirements)
   - [Code Implementation](#r3-code-implementation)
   - [Deep Code Analysis](#r3-deep-code-analysis)
   - [Questions & Answers](#r3-questions--answers)
3. [Database Relationships](#database-relationships)
4. [Testing Examples](#testing-examples)

---

# R2: Friends Feature

## R2 Requirements

### What Does R2 Do?

R2 implements a **friendship system** where users can connect with each other as friends. The key concept is that friendship is **bidirectional**:

```
If Alice adds Bob as a friend
    ↓
Bob automatically becomes Alice's friend
    ↓
Alice and Bob are BOTH friends with each other
```

### Methods to Implement

| Method | Purpose | Input | Output | Exceptions |
|--------|---------|-------|--------|------------|
| `addFriendship()` | Create friendship between two people | code1, code2 | void | `NoSuchCodeException` if either person doesn't exist |
| `listOfFriends()` | Get all friends of a person | personCode | Collection of friend codes | `NoSuchCodeException` if person doesn't exist |

### Key Requirements

1. **Bidirectional Relationship**: If A → B then B → A automatically
2. **Validation**: Both persons must exist in the database
3. **Return Format**: `listOfFriends()` returns person codes, not Person objects
4. **Empty Collection**: If a person has no friends, return empty collection (not null)

---

## R2 Code Implementation

### 1. Person Entity - Adding Friends Field

```java
@Entity
class Person {
  @Id
  private String code;
  private String name;
  private String surname;

  @ManyToMany  // ← R2: Many persons can have many friends
  private Set<Person> friends = new HashSet<>();  // ← R2: Collection of friends

  @ManyToMany  // For R3
  private Set<Group> groups = new HashSet<>();

  @OneToMany(mappedBy = "author")  // For R5
  private Set<Post> posts = new HashSet<>();

  Person() {
    // Default constructor for JPA
  }

  Person(String code, String name, String surname) {
    this.code = code;
    this.name = name;
    this.surname = surname;
  }

  String getCode() { return code; }
  String getName() { return name; }
  String getSurname() { return surname; }

  // ========== R2 METHODS ==========
  
  public Set<Person> getFriends() {
    return friends;
  }

  public void addFriend(Person p) {
    this.friends.add(p);
  }
}
```

### 2. Social.java - addFriendship() Method

```java
public void addFriendship(String codePerson1, String codePerson2)
    throws NoSuchCodeException {
    
    try {
        JPAUtil.executeInTransaction(() -> {
            // Step 1: Find both persons
            Person p1 = personRepository.findById(codePerson1).orElse(null);
            Person p2 = personRepository.findById(codePerson2).orElse(null);
            
            // Step 2: Validate both exist
            if (p1 == null || p2 == null) throw new NoSuchCodeException();
            
            // Step 3: Add bidirectional friendship
            p1.addFriend(p2);  // Add p2 to p1's friends
            p2.addFriend(p1);  // Add p1 to p2's friends
            
            // Step 4: Update both in database
            personRepository.update(p1);
            personRepository.update(p2);
        });
    } catch (NoSuchCodeException e) {
        throw e;  // Re-throw NoSuchCodeException
    } catch (Exception e) {
        throw new RuntimeException(e);  // Wrap other exceptions
    }
}
```

### 3. Social.java - listOfFriends() Method

```java
public Collection<String> listOfFriends(String codePerson)
    throws NoSuchCodeException {
    
    try {
        return JPAUtil.executeInContext(() -> {
            // Step 1: Find the person
            Person p = personRepository.findById(codePerson).orElse(null);
            
            // Step 2: Validate person exists
            if (p == null) throw new NoSuchCodeException();
            
            // Step 3: Extract friend codes
            List<String> friendsCodes = new ArrayList<>();
            for (Person friend : p.getFriends()) {
                friendsCodes.add(friend.getCode());
            }
            
            // Step 4: Return collection
            return friendsCodes;
        });
    } catch (NoSuchCodeException e) {
        throw e;
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}
```

---

## R2 Deep Code Analysis

### Part 1: Understanding @ManyToMany Relationship

#### What is @ManyToMany?

```java
@ManyToMany
private Set<Person> friends = new HashSet<>();
```

This annotation tells JPA that:
- **One person** can have **many friends**
- **Each friend** can have **many persons** (as friends)

**Real-World Analogy:**
```
Alice's friends: Bob, Charlie, David
Bob's friends: Alice, Eve, Frank
Charlie's friends: Alice, Grace
...
```

#### How Database Stores @ManyToMany

JPA creates **three tables**:

**Table 1: Person**
```
┌──────────┬────────┬──────────┐
│   code   │  name  │ surname  │
├──────────┼────────┼──────────┤
│  alice   │ Alice  │  Smith   │
│  bob     │ Bob    │  Jones   │
│  charlie │ Charlie│  Brown   │
└──────────┴────────┴──────────┘
```

**Table 2: Person_Person (Join Table - Created Automatically)**
```
┌────────────────────┬────────────────────┐
│  Person_code       │  friends_code      │
├────────────────────┼────────────────────┤
│  alice             │  bob               │
│  alice             │  charlie           │
│  bob               │  alice             │
│  bob               │  eve               │
│  charlie           │  alice             │
└────────────────────┴────────────────────┘
```

**How it works:**
- Each row in join table represents one friendship link
- If alice → bob, there's a row: (alice, bob)
- If bob → alice, there's a row: (bob, alice)
- Both rows needed for bidirectional friendship

### Part 2: Why Use executeInTransaction?

```java
JPAUtil.executeInTransaction(() -> {
    // All operations here in one transaction
    Person p1 = personRepository.findById(codePerson1).orElse(null);
    Person p2 = personRepository.findById(codePerson2).orElse(null);
    if (p1 == null || p2 == null) throw new NoSuchCodeException();
    p1.addFriend(p2);
    p2.addFriend(p1);
    personRepository.update(p1);
    personRepository.update(p2);
});
```

**Why is this needed?**

#### Without Transaction:
```java
// ❌ WRONG - No transaction
Person p1 = personRepository.findById("alice").orElse(null);
Person p2 = personRepository.findById("bob").orElse(null);
p1.addFriend(p2);
personRepository.update(p1);  // Saved to DB
// System crashes here! ☠
p2.addFriend(p1);
personRepository.update(p2);  // Never executed!

// Result: alice has bob as friend, but bob doesn't have alice
// ❌ Inconsistent database state!
```

#### With Transaction:
```java
// ✓ CORRECT - All or nothing
JPAUtil.executeInTransaction(() -> {
    Person p1 = personRepository.findById("alice").orElse(null);
    Person p2 = personRepository.findById("bob").orElse(null);
    p1.addFriend(p2);
    p2.addFriend(p1);
    personRepository.update(p1);
    personRepository.update(p2);
    // If crash happens anywhere:
    // → Transaction rolls back
    // → NO changes saved
    // → Database remains consistent ✓
});
```

**Transaction guarantees:**
- ✓ **Atomicity**: All operations succeed or all fail
- ✓ **Consistency**: Database always in valid state
- ✓ **Isolation**: Other threads don't see partial changes
- ✓ **Durability**: Once committed, changes are permanent

### Part 3: Understanding Bidirectional Updates

```java
p1.addFriend(p2);  // Line 1
p2.addFriend(p1);  // Line 2
personRepository.update(p1);  // Line 3
personRepository.update(p2);  // Line 4
```

**Visual Execution Flow:**

```
Initial State:
alice: friends = []
bob:   friends = []

After Line 1 (p1.addFriend(p2)):
alice: friends = [bob]  ← Changed in memory
bob:   friends = []

After Line 2 (p2.addFriend(p1)):
alice: friends = [bob]
bob:   friends = [alice]  ← Changed in memory

After Line 3 (update p1):
Database updated:
  INSERT INTO Person_Person VALUES ('alice', 'bob')

After Line 4 (update p2):
Database updated:
  INSERT INTO Person_Person VALUES ('bob', 'alice')

Final State:
alice ↔ bob (bidirectional friendship established)
```

### Part 4: Why Use executeInContext for listOfFriends?

```java
JPAUtil.executeInContext(() -> {
    Person p = personRepository.findById(codePerson).orElse(null);
    if (p == null) throw new NoSuchCodeException();
    
    List<String> friendsCodes = new ArrayList<>();
    for (Person friend : p.getFriends()) {  // ← Accessing relationship
        friendsCodes.add(friend.getCode());
    }
    return friendsCodes;
});
```

**Why executeInContext vs executeInTransaction?**

| Method | Purpose | When to Use |
|--------|---------|-------------|
| `executeInTransaction` | Write operations (INSERT, UPDATE, DELETE) | Creating/modifying data |
| `executeInContext` | Read operations (SELECT) | Querying data |

**Key difference:**

```java
// executeInTransaction - Starts actual database transaction
executeInTransaction(() -> {
    // Changes are saved to database
    personRepository.update(person);
    // COMMIT happens at the end
});

// executeInContext - Keeps EntityManager open
executeInContext(() -> {
    // Can access lazy-loaded relationships
    person.getFriends();  // Works! ✓
    // No COMMIT - read-only
});
```

**The Lazy Loading Problem:**

```java
// ❌ WITHOUT executeInContext
Person p = personRepository.findById("alice").orElse(null);
// EntityManager closes here!

for (Person friend : p.getFriends()) {  
    // ❌ LazyInitializationException!
    // EntityManager is closed, can't load friends
}

// ✓ WITH executeInContext
JPAUtil.executeInContext(() -> {
    Person p = personRepository.findById("alice").orElse(null);
    // EntityManager still open!
    
    for (Person friend : p.getFriends()) {  
        // ✓ Works! EntityManager loads friends on demand
        friendsCodes.add(friend.getCode());
    }
});
// EntityManager closes after lambda completes
```

---

## R2 Questions & Answers

### Q1: Why use Set instead of List for friends?

```java
private Set<Person> friends = new HashSet<>();  // Why Set?
```

**Answer:**

**Set characteristics:**
- ✓ No duplicates automatically
- ✓ Unordered (doesn't matter for friends)
- ✓ Better performance for contains() checks

**Example:**
```java
// With Set
person.addFriend(bob);
person.addFriend(bob);  // Ignored - already in set
// Result: friends = [bob]  ✓ Correct

// With List (if we used it)
person.addFriend(bob);
person.addFriend(bob);
// Result: friends = [bob, bob]  ❌ Duplicate!
```

### Q2: Why do we need both p1.addFriend(p2) AND p2.addFriend(p1)?

**Answer: Database Bidirectionality**

```java
// If we only did this:
p1.addFriend(p2);
personRepository.update(p1);

// Database state:
Person_Person join table:
┌──────────┬─────────────┐
│ Person   │ friends     │
├──────────┼─────────────┤
│ alice    │ bob         │  ← Only one direction!
└──────────┴─────────────┘

// When we query:
alice.getFriends()  → [bob]  ✓ Works
bob.getFriends()    → []     ❌ Empty! Bob doesn't have alice as friend

// With both updates:
p1.addFriend(p2);
p2.addFriend(p1);

// Database state:
┌──────────┬─────────────┐
│ Person   │ friends     │
├──────────┼─────────────┤
│ alice    │ bob         │  ← Both directions!
│ bob      │ alice       │
└──────────┴─────────────┘

// When we query:
alice.getFriends()  → [bob]    ✓ Works
bob.getFriends()    → [alice]  ✓ Works
```

### Q3: What happens if we call addFriendship with the same person twice?

```java
social.addFriendship("alice", "bob");
social.addFriendship("alice", "bob");  // Second call
```

**Answer: Set Prevents Duplicates**

```java
// First call:
p1.addFriend(p2);  // friends = [bob]
p2.addFriend(p1);  // friends = [alice]
update(p1);
update(p2);

// Second call:
p1.addFriend(p2);  // friends = [bob] (already there, Set ignores)
p2.addFriend(p1);  // friends = [alice] (already there, Set ignores)
update(p1);  // No change
update(p2);  // No change

// Result: Still just one friendship (no duplicates) ✓
```

### Q4: Can a person be friends with themselves?

```java
social.addFriendship("alice", "alice");  // Self-friendship?
```

**Answer: Current code ALLOWS it**

```java
// Code doesn't prevent it:
Person p1 = findById("alice");  // p1 = alice
Person p2 = findById("alice");  // p2 = alice (same object!)
p1.addFriend(p2);  // alice adds alice to friends
p2.addFriend(p1);  // alice adds alice to friends (redundant)

// Result: alice is in her own friends list ❌
```

**To prevent this, add validation:**
```java
if (codePerson1.equals(codePerson2)) {
    throw new IllegalArgumentException("Person cannot be friend with themselves");
}
```

### Q5: Why wrap NoSuchCodeException in try-catch if we just re-throw it?

```java
try {
    JPAUtil.executeInTransaction(() -> {
        if (p1 == null || p2 == null) throw new NoSuchCodeException();
        // ...
    });
} catch (NoSuchCodeException e) {
    throw e;  // ← Why catch and re-throw?
}
```

**Answer: Lambda Exception Handling**

The issue is that `executeInTransaction` accepts a **lambda** that can throw **any** exception:

```java
// JPAUtil method signature:
public static void executeInTransaction(ThrowingRunnable<E> action) throws E

// But NoSuchCodeException is checked, and lambdas have issues with checked exceptions
// We need to catch it explicitly and re-throw to satisfy Java's type system

// Without try-catch:
JPAUtil.executeInTransaction(() -> {
    throw new NoSuchCodeException();  // ❌ Compiler error!
    // "Unhandled exception: NoSuchCodeException"
});

// With try-catch:
try {
    JPAUtil.executeInTransaction(() -> {
        throw new NoSuchCodeException();  // ✓ Works
    });
} catch (NoSuchCodeException e) {
    throw e;  // Re-throw to caller
}
```

### Q6: Why return List<String> instead of Set<String> in listOfFriends?

```java
List<String> friendsCodes = new ArrayList<>();  // Why List?
for (Person friend : p.getFriends()) {  // getFriends() returns Set
    friendsCodes.add(friend.getCode());
}
return friendsCodes;  // Return List, not Set
```

**Answer: Method signature returns Collection**

```java
public Collection<String> listOfFriends(String codePerson)

// Collection is interface that both List and Set implement
// We can return either:
return new ArrayList<>();  // List is a Collection ✓
return new HashSet<>();    // Set is a Collection ✓

// List is chosen because:
// - More commonly used
// - Consistent with other methods
// - Doesn't matter since friends are already unique (from Set)
```

---

# R3: Groups Feature

## R3 Requirements

### What Does R3 Do?

R3 implements a **group system** where:
- Users can create groups with unique names
- Users can join groups (subscribe)
- Groups can be managed (add, update name, delete)

**Key Concept:**
```
Group: "Java Developers"
   ↓
Members: Alice, Bob, Charlie (Many persons in one group)

Person: Alice
   ↓
Groups: "Java Developers", "Book Club", "Gamers" (One person in many groups)
```

### Methods to Implement

| Method | Purpose | Input | Output | Exceptions |
|--------|---------|-------|--------|------------|
| `addGroup()` | Create new group | groupName | void | `GroupExistsException` if name exists |
| `deleteGroup()` | Delete group | groupName | void | `NoSuchCodeException` if not found |
| `updateGroupName()` | Rename group | oldName, newName | void | `GroupExistsException` if newName exists<br>`NoSuchCodeException` if oldName not found |
| `listOfGroups()` | Get all group names | none | Collection of names | None (never throws) |
| `addPersonToGroup()` | Add person to group | personCode, groupName | void | `NoSuchCodeException` if either not found |
| `listOfPeopleInGroup()` | Get group members | groupName | Collection of codes | None (returns empty if not found) |

---

## R3 Code Implementation

### 1. Group Entity Class

```java
@Entity
@Table(name = "SocialGroup")  // ← Can't use "Group" - reserved SQL keyword
class Group {

    @Id
    private String name;  // ← Group name is the primary key

    @ManyToMany(mappedBy = "groups")  // ← Bidirectional relationship
    private Set<Person> members = new HashSet<>();

    public Group() {
        // Default constructor for JPA
    }

    public Group(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Set<Person> getMembers() {
        return members;
    }
}
```

### 2. Person Entity - Adding Groups Field

```java
@Entity
class Person {
    @Id
    private String code;
    private String name;
    private String surname;

    @ManyToMany
    private Set<Person> friends = new HashSet<>();

    @ManyToMany  // ← R3: Person can be in many groups
    private Set<Group> groups = new HashSet<>();  // ← R3: Collection of groups

    // ... constructors and getters ...

    public Set<Group> getGroups() {
        return groups;
    }

    public void addGroup(Group g) {
        this.groups.add(g);
    }
}
```

### 3. Social.java - Group Repository

```java
public class Social {
    private final PersonRepository personRepository = new PersonRepository();
    
    // R3: Generic repository for Group entity
    private final GenericRepository<Group, String> groupRepository = 
        new GenericRepository<>(Group.class);
    
    // ... methods ...
}
```

### 4. Social.java - addGroup() Method

```java
public void addGroup(String groupName) throws GroupExistsException {
    try {
        JPAUtil.executeInTransaction(() -> {
            // Step 1: Check if group already exists
            if (groupRepository.findById(groupName).isPresent()) {
                throw new GroupExistsException();
            }
            
            // Step 2: Create new group
            Group g = new Group(groupName);
            
            // Step 3: Save to database
            groupRepository.save(g);
        });
    } catch (GroupExistsException e) {
        throw e;
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}
```

### 5. Social.java - deleteGroup() Method

```java
public void deleteGroup(String groupName) throws NoSuchCodeException {
    try {
        JPAUtil.executeInTransaction(() -> {
            // Step 1: Find the group
            Group g = groupRepository.findById(groupName).orElse(null);
            
            // Step 2: Validate existence
            if (g == null) throw new NoSuchCodeException();
            
            // Step 3: Delete from database
            groupRepository.delete(g);
        });
    } catch (NoSuchCodeException e) {
        throw e;
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}
```

### 6. Social.java - updateGroupName() Method

```java
public void updateGroupName(String groupName, String newName) 
    throws NoSuchCodeException, GroupExistsException {
    
    try {
        JPAUtil.executeInTransaction(() -> {
            // Step 1: Find old group
            Group g = groupRepository.findById(groupName).orElse(null);
            if (g == null) throw new NoSuchCodeException();
            
            // Step 2: Check if new name already exists
            if (groupRepository.findById(newName).isPresent()) {
                throw new GroupExistsException();
            }
            
            // Step 3: Create new group with new name
            Group newGroup = new Group(newName);
            groupRepository.save(newGroup);
            
            // Step 4: Transfer all members
            for(Person member : g.getMembers()) {
                newGroup.getMembers().add(member);
                member.getGroups().remove(g);
                member.getGroups().add(newGroup);
                personRepository.update(member);
            }
            
            // Step 5: Delete old group
            groupRepository.delete(g);
        });
    } catch (NoSuchCodeException e) {
        throw e;
    } catch (GroupExistsException e) {
        throw e;
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}
```

### 7. Social.java - listOfGroups() Method

```java
public Collection<String> listOfGroups() {
    try {
        return JPAUtil.executeInContext(() -> {
            List<String> names = new ArrayList<>();
            
            // Get all groups and extract names
            for (Group g : groupRepository.findAll()) {
                names.add(g.getName());
            }
            
            return names;
        });
    } catch (Exception e) {
        return new ArrayList<>();  // Return empty on error
    }
}
```

### 8. Social.java - addPersonToGroup() Method

```java
public void addPersonToGroup(String codePerson, String groupName) 
    throws NoSuchCodeException {
    
    try {
        JPAUtil.executeInTransaction(() -> {
            // Step 1: Find both person and group
            Person p = personRepository.findById(codePerson).orElse(null);
            Group g = groupRepository.findById(groupName).orElse(null);
            
            // Step 2: Validate both exist
            if (p == null || g == null) throw new NoSuchCodeException();
            
            // Step 3: Add group to person's groups
            p.addGroup(g);
            
            // Step 4: Update person
            personRepository.update(p);
        });
    } catch (NoSuchCodeException e) {
        throw e;
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}
```

### 9. Social.java - listOfPeopleInGroup() Method

```java
public Collection<String> listOfPeopleInGroup(String groupName) {
    try {
        return JPAUtil.executeInContext(() -> {
            // Step 1: Find the group
            Group g = groupRepository.findById(groupName).orElse(null);
            
            List<String> codes = new ArrayList<>();
            
            // Step 2: Return empty if not found
            if (g == null) return codes;
            
            // Step 3: Extract member codes
            for (Person p : g.getMembers()) {
                codes.add(p.getCode());
            }
            
            return codes;
        });
    } catch (Exception e) {
        return new ArrayList<>();
    }
}
```

---

## R3 Deep Code Analysis

### Part 1: Understanding @Table(name = "SocialGroup")

```java
@Entity
@Table(name = "SocialGroup")
class Group {
    // ...
}
```

**Why not just "Group"?**

```sql
-- ❌ This would fail in SQL
CREATE TABLE Group (
    name VARCHAR(255) PRIMARY KEY
);
-- Error: "GROUP" is a reserved SQL keyword (used in GROUP BY)

-- ✓ This works
CREATE TABLE SocialGroup (
    name VARCHAR(255) PRIMARY KEY
);
```

**Reserved SQL keywords:**
- GROUP, ORDER, SELECT, WHERE, FROM, JOIN, etc.
- JPA would fail to create table without @Table annotation

### Part 2: Understanding @ManyToMany(mappedBy = "groups")

```java
// In Group class:
@ManyToMany(mappedBy = "groups")
private Set<Person> members = new HashSet<>();

// In Person class:
@ManyToMany
private Set<Group> groups = new HashSet<>();
```

**What does mappedBy mean?**

```
Person ←→ Group relationship

┌─────────────────────┐         ┌─────────────────────┐
│      Person         │         │       Group         │
├─────────────────────┤         ├─────────────────────┤
│ @ManyToMany         │         │ @ManyToMany         │
│ groups              │ ←────── │ (mappedBy="groups") │
│ (OWNER)             │         │ members             │
│                     │         │ (INVERSE)           │
└─────────────────────┘         └─────────────────────┘
```

**Roles:**
- **Person.groups** = OWNER side (manages the relationship)
- **Group.members** = INVERSE side (just reads the relationship)

**Database structure:**
```
Person_Group join table (created automatically):
┌─────────────┬────────────────┐
│ Person_code │ groups_name    │
├─────────────┼────────────────┤
│ alice       │ JavaDevelopers │
│ alice       │ BookClub       │
│ bob         │ JavaDevelopers │
└─────────────┴────────────────┘
```

**Why mappedBy?**

```java
// Without mappedBy - JPA creates TWO join tables:
// - Person_Group (from Person side)
// - Group_Person (from Group side)
// ❌ Redundant and confusing!

// With mappedBy - JPA creates ONE join table:
// - Person_Group (managed by Person side)
// ✓ Clean and efficient!
```

### Part 3: Why updateGroupName() is Complex

```java
public void updateGroupName(String groupName, String newName) {
    // Step 3: Create new group
    Group newGroup = new Group(newName);
    groupRepository.save(newGroup);
    
    // Step 4: Transfer all members
    for(Person member : g.getMembers()) {
        newGroup.getMembers().add(member);
        member.getGroups().remove(g);
        member.getGroups().add(newGroup);
        personRepository.update(member);
    }
    
    // Step 5: Delete old group
    groupRepository.delete(g);
}
```

**Why not just rename?**

```java
// ❌ Why not just do this?
Group g = groupRepository.findById(groupName).orElse(null);
g.setName(newName);  // Just change the name!
groupRepository.update(g);

// Problem: name is the PRIMARY KEY!
@Id
private String name;

// You can't change a primary key in database!
// SQL error: "Cannot update primary key"
```

**Visual representation of updateGroupName():**

```
Initial State:
Group: "OldName"
  Members: alice, bob, charlie

Step 1: Create new group
Group: "OldName"          Group: "NewName"
  Members: alice, bob       Members: []

Step 2: Transfer members
Group: "OldName"          Group: "NewName"
  Members: alice, bob       Members: alice
  
alice.groups: remove "OldName", add "NewName"

Step 3: Continue transfer
Group: "OldName"          Group: "NewName"
  Members: []               Members: alice, bob, charlie

Step 4: Delete old group
Group: "OldName" ❌ DELETED
                          Group: "NewName" ✓
                            Members: alice, bob, charlie
```

### Part 4: Unidirectional Update in addPersonToGroup()

```java
public void addPersonToGroup(String codePerson, String groupName) {
    Person p = personRepository.findById(codePerson).orElse(null);
    Group g = groupRepository.findById(groupName).orElse(null);
    
    p.addGroup(g);  // ← Only update Person side
    personRepository.update(p);
    
    // ❌ NOT updating Group side:
    // g.getMembers().add(p);
    // groupRepository.update(g);
}
```

**Why only update one side?**

```java
// Because of @ManyToMany ownership:
// Person.groups = OWNER (manages join table)
// Group.members = INVERSE (just reads join table)

// When we do:
p.addGroup(g);
personRepository.update(p);

// Database:
INSERT INTO Person_Group VALUES ('alice', 'JavaDevelopers')
// ✓ Relationship is saved!

// Then when we read:
Group g = groupRepository.findById("JavaDevelopers");
Set<Person> members = g.getMembers();
// JPA automatically reads from Person_Group join table
// members = [alice] ✓

// If we tried updating Group side:
g.getMembers().add(p);  // ← Changes in-memory set
groupRepository.update(g);  // ← Has no effect!
// Because Group is INVERSE side (mappedBy="groups")
// JPA ignores changes to inverse side
```

**Conclusion: Always update the OWNER side (Person)**

---

## R3 Questions & Answers

### Q1: Why does Group use name as @Id instead of a separate ID?

```java
@Entity
class Group {
    @Id
    private String name;  // Name is primary key
    
    // Why not:
    // @Id
    // private Long id;
    // private String name;
}
```

**Answer: Natural Key vs Surrogate Key**

**With name as ID (Natural Key):**
```
✓ Simpler - no extra field
✓ More readable - can query by name directly
✓ Name must be unique (enforced by database)
❌ Can't rename easily (primary key issue)
❌ Name could be long (performance concern)
```

**With separate ID (Surrogate Key):**
```
✓ Can rename group easily
✓ Better performance (integer vs string)
✓ More flexible
❌ More complex - need extra field
❌ Name uniqueness not automatic
```

**Design choice depends on:**
- Are renames common? → Use ID
- Is name always unique? → Use name as ID
- Performance critical? → Use ID

**In this project:** Name as ID is acceptable because:
- Groups are simple entities
- Renaming implemented (though complex)
- Name is naturally unique

### Q2: What happens to group members when we delete a group?

```java
public void deleteGroup(String groupName) {
    Group g = groupRepository.findById(groupName).orElse(null);
    groupRepository.delete(g);  // What happens to members?
}
```

**Answer: Members are NOT deleted**

```java
// Before deletion:
Group "JavaDevelopers"
  Members: alice, bob, charlie

Person alice:
  Groups: ["JavaDevelopers", "BookClub"]

// After deleteGroup("JavaDevelopers"):
Group "JavaDevelopers" ❌ DELETED

Person alice:
  Groups: ["BookClub"]  ✓ alice still exists, just removed from deleted group

// Database:
Person table: alice still exists ✓
Person_Group: Rows with "JavaDevelopers" deleted ✓
```

**JPA handles this automatically:**
- Delete group → JPA deletes rows in Person_Group join table
- Persons remain untouched
- Next time person loads, deleted group not in their groups

### Q3: Why does listOfPeopleInGroup() not throw exception when group doesn't exist?

```java
public Collection<String> listOfPeopleInGroup(String groupName) {
    Group g = groupRepository.findById(groupName).orElse(null);
    if (g == null) return codes;  // ← Return empty, not throw exception
    // ...
}
```

**Answer: Design Decision**

**Option 1: Throw exception (not chosen)**
```java
if (g == null) throw new NoSuchCodeException();

// Usage:
try {
    Collection<String> people = social.listOfPeopleInGroup("NonExistent");
} catch (NoSuchCodeException e) {
    // Must handle exception
}
```

**Option 2: Return empty (chosen)**
```java
if (g == null) return new ArrayList<>();

// Usage:
Collection<String> people = social.listOfPeopleInGroup("NonExistent");
// people = [] (empty list)
// No exception handling needed ✓
```

**Why return empty?**
- ✓ More convenient for caller
- ✓ Consistent with "group with no members"
- ✓ Follows null object pattern
- ✓ Less error handling needed

### Q4: Why do we update each member individually in updateGroupName()?

```java
for(Person member : g.getMembers()) {
    newGroup.getMembers().add(member);
    member.getGroups().remove(g);      // ← Update person
    member.getGroups().add(newGroup);  // ← Update person
    personRepository.update(member);   // ← Save person
}
```

**Answer: Maintaining Bidirectional Relationship**

```java
// If we only did this:
for(Person member : g.getMembers()) {
    newGroup.getMembers().add(member);  // Only update Group side
}
groupRepository.update(newGroup);

// Problem: Group is INVERSE side (mappedBy)
// JPA won't save these changes to database!

// We MUST update Person side (OWNER):
member.getGroups().remove(g);      // Remove old group from person
member.getGroups().add(newGroup);  // Add new group to person
personRepository.update(member);   // Save changes

// Database:
Person_Group join table:
DELETE FROM Person_Group WHERE groups_name = 'OldName'
INSERT INTO Person_Group VALUES ('alice', 'NewName')
```

### Q5: Can a person join the same group multiple times?

```java
social.addPersonToGroup("alice", "JavaDevelopers");
social.addPersonToGroup("alice", "JavaDevelopers");  // Second call
```

**Answer: No, Set prevents duplicates**

```java
// First call:
p.addGroup(g);  // groups = ["JavaDevelopers"]

// Second call:
p.addGroup(g);  // groups = ["JavaDevelopers"] (Set ignores duplicate)

// Result: Person is in group only once ✓
```

### Q6: Why use GenericRepository directly for Group instead of creating GroupRepository?

```java
// In Social.java:
private final GenericRepository<Group, String> groupRepository = 
    new GenericRepository<>(Group.class);

// Why not:
// private final GroupRepository groupRepository = new GroupRepository();
```

**Answer: No Additional Methods Needed**

```java
// PersonRepository exists because we might add custom queries:
public class PersonRepository extends GenericRepository<Person, String> {
    // Could add:
    // public List<Person> findByName(String name) { ... }
    // public List<Person> findPopularPeople() { ... }
}

// GroupRepository would just be:
public class GroupRepository extends GenericRepository<Group, String> {
    // No additional methods needed!
}

// So we skip it and use GenericRepository directly:
private final GenericRepository<Group, String> groupRepository = 
    new GenericRepository<>(Group.class);
// ✓ Simpler, fewer files, same functionality
```

---

# Database Relationships

## Complete Entity Relationship Diagram

```
┌─────────────────────────────────────┐
│            Person                   │
│  ┌───────────────────────────────┐ │
│  │ @Id code: String              │ │
│  │ name: String                  │ │
│  │ surname: String               │ │
│  │                               │ │
│  │ @ManyToMany                   │ │
│  │ friends: Set<Person>          │ │ ──┐ Self-referencing
│  │                               │ │   │ (Person → Person)
│  │ @ManyToMany                   │ │   │
│  │ groups: Set<Group>            │ │   │
│  │                               │ │   │
│  │ @OneToMany                    │ │   │
│  │ posts: Set<Post>              │ │   │
│  └───────────────────────────────┘ │   │
└─────────────────────────────────────┘   │
              │                            │
              │                            │
              │ Many-to-Many               │
              │                            │
              ↓                            │
┌─────────────────────────────────────┐   │
│            Group                    │   │
│  ┌───────────────────────────────┐ │   │
│  │ @Id name: String              │ │   │
│  │                               │ │   │
│  │ @ManyToMany(mappedBy="groups│ │←──┘
│  │ members: Set<Person>          │ │
│  └───────────────────────────────┘ │
└─────────────────────────────────────┘
```

## Database Tables

**Person Table:**
```sql
CREATE TABLE Person (
    code VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255),
    surname VARCHAR(255)
);
```

**SocialGroup Table:**
```sql
CREATE TABLE SocialGroup (
    name VARCHAR(255) PRIMARY KEY
);
```

**Person_Person Join Table (Friends):**
```sql
CREATE TABLE Person_Person (
    Person_code VARCHAR(255),
    friends_code VARCHAR(255),
    FOREIGN KEY (Person_code) REFERENCES Person(code),
    FOREIGN KEY (friends_code) REFERENCES Person(code)
);
```

**Person_Group Join Table (Memberships):**
```sql
CREATE TABLE Person_Group (
    Person_code VARCHAR(255),
    groups_name VARCHAR(255),
    FOREIGN KEY (Person_code) REFERENCES Person(code),
    FOREIGN KEY (groups_name) REFERENCES SocialGroup(name)
);
```

---

# Testing Examples

## R2 Testing

```java
@Test
public void testR2Friendship() throws Exception {
    Social social = new Social();
    
    // Setup: Add two persons
    social.addPerson("alice", "Alice", "Smith");
    social.addPerson("bob", "Bob", "Jones");
    
    // Test 1: Add friendship
    social.addFriendship("alice", "bob");
    
    // Test 2: Check alice's friends
    Collection<String> aliceFriends = social.listOfFriends("alice");
    assertTrue(aliceFriends.contains("bob"));
    assertEquals(1, aliceFriends.size());
    
    // Test 3: Check bob's friends (bidirectional)
    Collection<String> bobFriends = social.listOfFriends("bob");
    assertTrue(bobFriends.contains("alice"));
    assertEquals(1, bobFriends.size());
    
    // Test 4: No friends initially
    social.addPerson("charlie", "Charlie", "Brown");
    Collection<String> charlieFriends = social.listOfFriends("charlie");
    assertTrue(charlieFriends.isEmpty());
    
    // Test 5: Non-existent person
    try {
        social.addFriendship("alice", "nonexistent");
        fail("Should throw NoSuchCodeException");
    } catch (NoSuchCodeException e) {
        // Expected
    }
}
```

## R3 Testing

```java
@Test
public void testR3Groups() throws Exception {
    Social social = new Social();
    
    // Setup
    social.addPerson("alice", "Alice", "Smith");
    social.addPerson("bob", "Bob", "Jones");
    
    // Test 1: Add group
    social.addGroup("JavaDevelopers");
    Collection<String> groups = social.listOfGroups();
    assertTrue(groups.contains("JavaDevelopers"));
    
    // Test 2: Add person to group
    social.addPersonToGroup("alice", "JavaDevelopers");
    Collection<String> members = social.listOfPeopleInGroup("JavaDevelopers");
    assertTrue(members.contains("alice"));
    
    // Test 3: Multiple members
    social.addPersonToGroup("bob", "JavaDevelopers");
    members = social.listOfPeopleInGroup("JavaDevelopers");
    assertEquals(2, members.size());
    
    // Test 4: Update group name
    social.updateGroupName("JavaDevelopers", "JavaExperts");
    groups = social.listOfGroups();
    assertFalse(groups.contains("JavaDevelopers"));
    assertTrue(groups.contains("JavaExperts"));
    
    // Test 5: Members transferred
    members = social.listOfPeopleInGroup("JavaExperts");
    assertTrue(members.contains("alice"));
    assertTrue(members.contains("bob"));
    
    // Test 6: Delete group
    social.deleteGroup("JavaExperts");
    groups = social.listOfGroups();
    assertFalse(groups.contains("JavaExperts"));
    
    // Test 7: Duplicate group name
    social.addGroup("TestGroup");
    try {
        social.addGroup("TestGroup");
        fail("Should throw GroupExistsException");
    } catch (GroupExistsException e) {
        // Expected
    }
}
```

---

## Summary Comparison: R2 vs R3

| Aspect | R2 (Friends) | R3 (Groups) |
|--------|--------------|-------------|
| **Relationship Type** | Self-referencing (Person ↔ Person) | Two entities (Person ↔ Group) |
| **Bidirectional?** | Yes (manually enforced) | Yes (automatic with mappedBy) |
| **Primary Key** | String (code) on both sides | String (code) and String (name) |
| **Owner Side** | Person (both directions) | Person (owns relationship) |
| **Join Table** | Person_Person | Person_Group |
| **Complexity** | Medium (must update both directions) | High (name is primary key) |
| **CRUD Operations** | Create friendship, List friends | Create/Update/Delete group, Memberships |

---

**Key Takeaways:**

1. **R2 focuses on self-referencing relationships** - persons connecting with persons
2. **R3 introduces a new entity** - groups as separate objects
3. **Both use @ManyToMany** but with different configurations
4. **Transactions are crucial** for maintaining data consistency
5. **Understanding owner vs inverse side** is key to proper JPA usage

---

**Next:** Move to R4 (Statistics) and R5 (Posts) to complete the social network!