# JPA Entity Annotations - Complete Guide

## Overview
JPA maps Java objects to database tables. Hibernate is a common JPA implementation.

---

## 1. Entity class

```java
import jakarta.persistence.*;

@Entity
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    protected Student() {
        // required by JPA
    }

    public Student(String name) {
        this.name = name;
    }
}
```

---

## 2. Important annotations

| Annotation | Meaning |
|---|---|
| `@Entity` | class is persistent |
| `@Id` | primary key |
| `@GeneratedValue` | generated primary key |
| `@Column` | column settings |
| `@Table` | table settings |
| `@Transient` | not stored in DB |

---

## 3. `@Column`

```java
@Column(nullable = false, unique = true, length = 100)
private String email;
```

Meaning:

- cannot be null
- must be unique
- max length 100

You can also rename the column in the database:

```java
@Column(name = "full_name")
private String name;   // Java field "name" -> DB column "full_name"
```

If you omit `@Column`, JPA still maps the field using its Java name as the column name.

---

## 3b. `@Table`

`@Table` customises the database **table** for an entity (name, schema, constraints).
It is optional — without it, the table name defaults to the class name.

```java
@Entity
@Table(name = "students")
public class Student {
    // ...
}
```

Here the class is `Student` but the table is called `students`.

---

## 3c. `@GeneratedValue` strategies

`@GeneratedValue` tells JPA to create the primary key value for you. The `strategy`
chooses **how**:

| Strategy | Meaning |
|---|---|
| `GenerationType.IDENTITY` | the database auto-increments the id column |
| `GenerationType.SEQUENCE` | uses a database sequence object |
| `GenerationType.AUTO` | let the JPA provider pick a suitable strategy |

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```

For beginners, `IDENTITY` (auto-increment) is the easiest to reason about.

---

## 4. No-argument constructor

JPA needs a no-argument constructor.

```java
protected Student() { }
```

It can be protected.

---

## 5. Entity identity

Usually entities have an `id`:

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```

Be careful with `equals` and `hashCode` in entities, especially before the id is generated.

---

## 6. `@Transient`

A field marked transient is not persisted.

```java
@Transient
private int temporaryScore;
```

---

## 7. Embeddable objects

For value objects:

```java
@Embeddable
public class Address {
    private String city;
    private String street;
}
```

Use:

```java
@Embedded
private Address address;
```

---

## Common mistakes

### Mistake 1: forgetting no-argument constructor
JPA needs it.

### Mistake 2: forgetting `@Id`
Every entity needs an identifier.

### Mistake 3: making all fields public
Use private fields and methods.

---

## 8. Full simple entity (everything together)

This one class shows `@Entity`, `@Table`, `@Id`, `@GeneratedValue`, `@Column`, and the
required no-argument constructor working together.

```java
import jakarta.persistence.*;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "email", unique = true)
    private String email;

    // Required by JPA (can be protected)
    protected Student() {
    }

    // Convenience constructor for your own code
    public Student(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public Long getId()      { return id; }
    public String getName()  { return name; }
    public String getEmail() { return email; }
}
```

What each part does:

- `@Entity` — this class is stored in the database.
- `@Table(name = "students")` — store it in a table called `students`.
- `@Id` + `@GeneratedValue(IDENTITY)` — `id` is the primary key, auto-generated.
- `@Column(...)` — extra rules for the `name` and `email` columns.
- `protected Student()` — the no-arg constructor JPA needs to build objects.
- You do **not** set `id` yourself; the database fills it in on save.

---

## Exam Notes

- `@Entity` is required to make a class persistent; every entity needs an `@Id`.
- JPA requires a **no-argument constructor** (may be `protected`); the compiler-provided
  default disappears once you add another constructor, so add it back yourself.
- `@Table` and `@Column` are **optional**; without them names default to the class/field name.
- `@GeneratedValue` lets the provider/database create the key; `IDENTITY` = auto-increment.
- `@Transient` fields are **not** saved to the database.
- Keep fields `private` with getters, not `public`.

---

## Mini quiz

### Q1. Which annotation marks a persistent class?
Answer: `@Entity`.

### Q2. Which annotation marks a primary key?
Answer: `@Id`.

### Q3. Which annotation excludes a field from persistence?
Answer: `@Transient`.

---

## More Practice Questions

1. Which annotation would you use to store a class called `Product` in a table named
   `products`? Write the two lines above the class.

2. Why does JPA require a no-argument constructor, and what access level can it have?

3. What does `@GeneratedValue(strategy = GenerationType.IDENTITY)` do?

4. Write a `@Column` annotation for a `username` field that cannot be null and must be
   unique.

5. If you omit `@Table` and `@Column`, what table and column names does JPA use?

6. True or false: you should normally set the `id` value yourself before saving an entity
   with `@GeneratedValue`. Explain.

7. Which annotation prevents a field (for example a temporary calculated score) from being
   stored in the database?
