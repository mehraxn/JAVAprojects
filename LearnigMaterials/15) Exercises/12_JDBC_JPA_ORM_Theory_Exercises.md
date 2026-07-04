# 12 — JDBC, JPA, and ORM (Theory + Code-Reading Exercises)

Topics: JDBC basics, `PreparedStatement`, transactions, JPA entity mapping, relationships,
cascade, fetch types.

**Note:** These are **learning/theory exercises only**. The code shows how the APIs look — it is
**not** a runnable local project (no database, JDBC driver, or JPA provider is installed here).
Practise reading and correcting the code. Checked by static review.

---

## Part A — JDBC

## Exercise 1 — Write a safe query

Write a `PreparedStatement` query that selects users by `name` and prints each name from the
`ResultSet`. Assume a `Connection conn` exists.

### Solution
```java
String sql = "SELECT id, name FROM users WHERE name = ?";
try (PreparedStatement ps = conn.prepareStatement(sql)) {
    ps.setString(1, "Ann");                 // parameter index starts at 1
    try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            System.out.println(rs.getString("name"));
        }
    }
}
```

---

## Exercise 2 — Theory questions

1. Why prefer `PreparedStatement` over `Statement`?
2. What does `ResultSet.next()` return and do?
3. How do you group multiple updates into one transaction?

### Solution
1. It binds parameters safely (prevents SQL injection), can be precompiled/reused, and handles
   escaping/quoting of values.
2. It advances the cursor to the next row and returns `true` if there is one, `false` at the
   end. The cursor starts **before** the first row.
3. `conn.setAutoCommit(false);` then run the statements and call `conn.commit()` (or
   `conn.rollback()` on error).

---

## Exercise 3 — Fix the bug (SQL injection risk)

Rewrite this to be safe against SQL injection.
```java
String name = userInput;
String sql = "SELECT * FROM users WHERE name = '" + name + "'";
Statement st = conn.createStatement();
ResultSet rs = st.executeQuery(sql);
```

### Solution
Use a `PreparedStatement` with a parameter placeholder instead of string concatenation:
```java
String sql = "SELECT * FROM users WHERE name = ?";
PreparedStatement ps = conn.prepareStatement(sql);
ps.setString(1, userInput);
ResultSet rs = ps.executeQuery();
```

---

## Part B — JPA entity mapping

## Exercise 4 — Write an entity

Write a JPA entity `Student` mapped to table `students`, with an auto-generated `id` and a
non-null `name`.

### Solution
```java
import jakarta.persistence.*;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    protected Student() { }                 // required by JPA
    public Student(String name) { this.name = name; }

    public Long getId()     { return id; }
    public String getName() { return name; }
}
```

---

## Exercise 5 — Fix the bug (missing no-arg constructor)

A JPA provider cannot use this entity. Why, and how to fix?
```java
@Entity
public class Course {
    @Id private Long id;
    private String title;
    public Course(String title) { this.title = title; }
}
```

### Solution
JPA needs a **no-argument constructor** to instantiate the entity by reflection. The declared
constructor removed the default, so add one (it may be `protected`):
```java
protected Course() { }
```

---

## Part C — Relationships, cascade, fetch

## Exercise 6 — Map a @ManyToOne

Many `Student`s belong to one `Course`. Write the mapping on the `Student` side.

### Solution
```java
@Entity
public class Student {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}
```

---

## Exercise 7 — Theory: relationships and fetch types

1. Which annotation goes on the "many students → one course" side?
2. What are the default fetch types for to-one vs to-many associations?
3. What does `cascade = CascadeType.ALL` do?

### Solution
1. `@ManyToOne` (on `Student`). The inverse side (`Course`) would use
   `@OneToMany(mappedBy = "course")`.
2. To-one (`@ManyToOne`, `@OneToOne`) default to **EAGER**; to-many (`@OneToMany`,
   `@ManyToMany`) default to **LAZY**.
3. It propagates operations (persist, merge, remove, ...) from the parent entity to its related
   entities — e.g. saving the parent also saves the children.

---

## Exercise 8 — Predict the behaviour (LAZY loading)

An entity has `@OneToMany(fetch = FetchType.LAZY) List<Order> orders;`. The code loads a
customer, closes the persistence session, then calls `customer.getOrders()`.
What happens?

### Solution
It typically throws a **`LazyInitializationException`**. Because `orders` is LAZY, it was not
loaded while the session was open, and the closed session can no longer fetch it. Fix by
fetching the data while the session is open (e.g. a `JOIN FETCH` query) or accessing it before
closing.

---

## Exercise 9 — Fix the bug (wrong side / wrong default assumed)

A developer writes `@OneToMany` on the "many" side and expects it to be EAGER. Explain the two
mistakes.
```java
@Entity
public class Student {
    @OneToMany            // (a) wrong annotation for this side
    private Course course; // (b) also: single object, not a collection
}
```

### Solution
- (a) The "many students → one course" side must be `@ManyToOne`, not `@OneToMany`.
- (b) `@OneToMany` maps a **collection** (e.g. `List<Student>`), not a single object.
Also note `@ManyToOne` is EAGER by default, while `@OneToMany` would be LAZY. Correct version:
```java
@ManyToOne
@JoinColumn(name = "course_id")
private Course course;
```

---

## Challenge — Design a small schema (theory)

For a library, model `Author` and `Book` where one author writes many books. State which
annotations go on which side, the owning side, and the default fetch type of each.

### Solution (example answer)
- `Book` has `@ManyToOne Author author;` with `@JoinColumn(name = "author_id")` — this is the
  **owning side** (holds the foreign key). Default fetch: **EAGER**.
- `Author` has `@OneToMany(mappedBy = "author") List<Book> books;` — the **inverse side**.
  Default fetch: **LAZY**.
- Add `cascade` on the `Author` side if saving an author should also save their books.
