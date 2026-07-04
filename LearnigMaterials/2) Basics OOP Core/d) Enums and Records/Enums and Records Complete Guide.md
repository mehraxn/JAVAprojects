# Java Enums and Records - Complete Guide

## Overview
Enums represent a fixed set of constants. Records are compact classes for immutable data carriers.

---

## 1. Enum basics

Use an enum when a value must be one from a fixed set.

```java
public enum Role {
    ADMIN,
    GUIDE,
    PARTICIPANT
}
```

Use:

```java
Role role = Role.ADMIN;

if (role == Role.ADMIN) {
    System.out.println("Admin user");
}
```

For enums, `==` comparison is correct because enum constants are singletons.

---

## 2. Enum with switch

```java
switch (role) {
    case ADMIN:
        System.out.println("Full access");
        break;
    case GUIDE:
        System.out.println("Guide dashboard");
        break;
    case PARTICIPANT:
        System.out.println("Participant dashboard");
        break;
}
```

---

## 3. Enum with fields and methods

```java
public enum Difficulty {
    EASY(1),
    MEDIUM(2),
    HARD(3);

    private final int level;

    Difficulty(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
```

Use:

```java
System.out.println(Difficulty.HARD.getLevel()); // 3
```

---

## 4. Useful enum methods

```java
Role[] roles = Role.values();
Role admin = Role.valueOf("ADMIN");
String name = Role.ADMIN.name();
int position = Role.ADMIN.ordinal();
```

Be careful with `ordinal()`. It depends on declaration order, so avoid using it for database values.

---

## 5. Records

A record is a short way to create an immutable data class.

```java
public record Student(String id, String name, int age) { }
```

Java automatically creates:

- private final fields
- constructor
- accessors
- `equals`
- `hashCode`
- `toString`

Use:

```java
Student s = new Student("S1", "Sara", 22);
System.out.println(s.name());
System.out.println(s);
```

Output:

```text
Sara
Student[id=S1, name=Sara, age=22]
```

---

## 6. Record validation

You can add validation using a compact constructor.

```java
public record Student(String id, String name) {
    public Student {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id cannot be blank");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name cannot be blank");
        }
    }
}
```

---

## 7. Enum vs class vs record

| Use case | Choose |
|---|---|
| Fixed set of constants | enum |
| Object with behavior and mutable state | class |
| Immutable data carrier | record |

---

## Common mistakes

### Mistake 1: storing enum ordinal in database
Use enum name instead, because ordinal changes if order changes.

### Mistake 2: trying to set record fields
Record fields are final.

### Mistake 3: overusing records
Records are good for data, not for every domain object with complex behavior.

---

## Mini quiz

### Q1. Can enum values be compared with `==`?
Answer: yes.

### Q2. Are record fields mutable?
Answer: no, they are final.

### Q3. What method returns all enum constants?
Answer: `values()`.
