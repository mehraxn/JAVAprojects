# JDBC, JPA, and ORM Questions (with Answers)

Covers JDBC basics, JPA entity mapping, ORM relationships, cascade, and fetch types. Theory,
multiple choice, and short code. Answers follow each question. Code checked by static review.

---

## 1. JDBC

**Q1.** What is JDBC?
**Answer:** Java Database Connectivity — the standard Java API for connecting to a relational
database, sending SQL, and reading results.

**Q2 (MCQ).** Which should you use for SQL that includes user input, and why?
a. `Statement`  b. `PreparedStatement`  c. `ResultSet`  d. `DriverManager`
**Answer:** b. `PreparedStatement` — it binds parameters safely, preventing **SQL injection**,
and can be precompiled/reused.

**Q3.** Show a safe parameterized query with `PreparedStatement`.
**Answer:**
```java
String sql = "SELECT * FROM users WHERE name = ?";
try (PreparedStatement ps = conn.prepareStatement(sql)) {
    ps.setString(1, userName);      // parameter index starts at 1
    try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            System.out.println(rs.getString("name"));
        }
    }
}
```

**Q4.** What does `ResultSet.next()` return and do?
**Answer:** It moves the cursor to the next row and returns `true` if there is one, `false`
when there are no more rows. (The cursor starts **before** the first row.)

**Q5.** How do you run several statements as one transaction?
**Answer:** Turn off auto-commit, run the statements, then commit (or rollback on error):
```java
conn.setAutoCommit(false);
try {
    // ... several updates ...
    conn.commit();
} catch (SQLException e) {
    conn.rollback();
}
```

**Q6.** Why use try-with-resources for `Connection`, `PreparedStatement`, `ResultSet`?
**Answer:** They are `AutoCloseable`; try-with-resources closes them automatically, preventing
leaked database connections/cursors.

**Q7 (MCQ).** `PreparedStatement` parameter indexes start at:
a. 0  b. 1  c. -1  d. depends
**Answer:** b. 1.

---

## 2. JPA entity mapping

**Q8.** What does `@Entity` do, and what else must every entity have?
**Answer:** `@Entity` marks a class as persistent (mapped to a table). Every entity needs an
`@Id` (primary key) and a **no-argument constructor** (may be `protected`).

**Q9.** Match the annotation to its job: `@Id`, `@GeneratedValue`, `@Column`, `@Table`.
**Answer:** `@Id` = primary key; `@GeneratedValue` = auto-generate the key value; `@Column` =
column settings (name, nullable, unique, length); `@Table` = the table name/settings.

**Q10.** Write a minimal entity for a `Product` stored in a `products` table with an
auto-generated id and a non-null `name`.
**Answer:**
```java
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    protected Product() { }              // required by JPA
    public Product(String name) { this.name = name; }
}
```

**Q11 (MCQ).** Why must a JPA entity have a no-arg constructor?
a. For `toString`  b. So the JPA provider can instantiate it via reflection
c. To avoid inheritance  d. It is optional
**Answer:** b. The provider (e.g. Hibernate) creates entity instances with the no-arg
constructor, then fills the fields.

**Q12.** What does `@Transient` do?
**Answer:** Marks a field that should **not** be persisted to the database.

---

## 3. ORM relationships

**Q13.** Name the four relationship annotations and a real example of each.
**Answer:**
- `@OneToOne` — a `Person` and their `Passport`.
- `@OneToMany` — a `Course` has many `Student`s.
- `@ManyToOne` — many `Student`s belong to one `Course`.
- `@ManyToMany` — `Student`s and `Course`s they enrol in.

**Q14 (MCQ).** Many students belong to one course. On the `Student` side you use:
a. `@OneToMany`  b. `@ManyToOne`  c. `@OneToOne`  d. `@ManyToMany`
**Answer:** b. `@ManyToOne`.

**Q15.** In a bidirectional relationship, what is the "owning side"?
**Answer:** The side that owns the foreign key / join column — the one **without** `mappedBy`.
The other side uses `mappedBy` and is the inverse side.

**Q16.** Sketch a `@ManyToOne` mapping from `Student` to `Course`.
**Answer:**
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

## 4. Cascade and fetch types

**Q17.** What does `cascade` do?
**Answer:** It propagates entity operations (e.g. `PERSIST`, `REMOVE`) from a parent entity to
its related entities. For example, saving an `Order` with `cascade = CascadeType.ALL` also
saves its `OrderItem`s.

**Q18.** Explain `EAGER` vs `LAZY` fetching.
**Answer:** **`EAGER`** loads the related data immediately together with the entity.
**`LAZY`** delays loading the related data until it is first accessed.

**Q19 (MCQ).** What are the **default** fetch types?
a. all EAGER
b. all LAZY
c. `@ManyToOne`/`@OneToOne` = EAGER; `@OneToMany`/`@ManyToMany` = LAZY
d. `@ManyToOne` = LAZY; `@OneToMany` = EAGER
**Answer:** c. To-one defaults to EAGER; to-many defaults to LAZY.

**Q20.** What is the "N+1 query problem", and how is it related to fetching?
**Answer:** When loading N parent rows and then a separate query for each parent's related
data, you run 1 + N queries. It often appears with LAZY collections accessed in a loop. Fixes
include a `JOIN FETCH` query or batch fetching.

**Q21.** When would you prefer LAZY over EAGER?
**Answer:** When the related data is large or not always needed — LAZY avoids loading it until
required, improving performance. EAGER is fine for small, always-needed associations.

**Q22 (short).** What common exception can LAZY loading cause if the entity is used after the
persistence session is closed?
**Answer:** A `LazyInitializationException` — the related data was not loaded and the session
is no longer open to fetch it.

---

## Quick recap

- `PreparedStatement` (params from index **1**) beats `Statement` for user input.
- Transactions: `setAutoCommit(false)` → `commit()` / `rollback()`.
- Entity = `@Entity` + `@Id` + no-arg constructor.
- To-one = EAGER by default; to-many = LAZY by default.
- `cascade` propagates operations; watch out for N+1 and `LazyInitializationException`.
