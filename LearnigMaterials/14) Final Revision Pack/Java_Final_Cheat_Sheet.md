# Java Final Cheat Sheet

A one-page-per-topic quick reference for the whole course. Use it the night before the exam.
For full explanations, follow the numbered folders `1)`–`13)`.

---

## 1. Constructors and `super`

- A constructor has the **same name as the class** and **no return type**.
- If you write no constructor, Java gives a **default no-arg** one. Adding any constructor
  removes that default — add it back yourself if you still need it.
- `this(...)` calls another constructor in the same class; `super(...)` calls the parent
  constructor. If used, either must be the **first statement**.
- If you do not call `super(...)`, Java inserts a hidden `super()` (the parent's no-arg
  constructor). If the parent has no no-arg constructor, you **must** call `super(args)`.

```java
class Animal { Animal(String n) { } }
class Dog extends Animal {
    Dog() { super("dog"); }   // required: Animal has no no-arg constructor
}
```

---

## 2. Inheritance and polymorphism

- `extends` for a class, `implements` for an interface. One class, one superclass.
- **Polymorphism**: a parent-typed variable can hold a child object.
- **Dynamic dispatch**: the **runtime object** decides which overridden method runs.

```java
Animal a = new Dog();
a.speak(); // Dog's speak() runs (chosen at runtime)
```

---

## 3. Overriding vs overloading

| | Overriding | Overloading |
|---|---|---|
| Where | subclass redefines parent method | same class, same method name |
| Signature | **same** name + parameters | **different** parameters |
| Chosen at | runtime (real object) | compile time (declared type) |
| Annotation | `@Override` | none |

---

## 4. Abstract class vs interface

| | Abstract class | Interface |
|---|---|---|
| Instantiate? | No | No |
| Fields | any (incl. instance state) | `public static final` constants only |
| Methods | abstract + concrete | abstract + `default` + `static` (+ private) |
| Inherit how many | one (`extends`) | many (`implements A, B`) |
| Constructor | yes | no |

Rule of thumb: **abstract class** = "is-a" with shared state; **interface** = a capability
that many unrelated classes can have.

---

## 5. `equals` and `hashCode`

- Default `equals` compares **references** (`==`). Override it to compare **contents**.
- **Contract**: if `a.equals(b)` is true, then `a.hashCode() == b.hashCode()` must be true.
- Always override **both** together, or `HashMap`/`HashSet` will misbehave.

```java
@Override public boolean equals(Object o) { /* compare fields */ }
@Override public int hashCode() { return Objects.hash(field1, field2); }
```

---

## 6. Collections

| Interface | Duplicates? | Ordered? | Common class |
|---|---|---|---|
| `List` | yes | insertion order | `ArrayList` |
| `Set` | no | depends | `HashSet` (none), `TreeSet` (sorted) |
| `Map` | keys unique | depends | `HashMap`, `TreeMap` (sorted keys) |
| `Queue` | yes | FIFO | `LinkedList`, `PriorityQueue` |

- `HashSet`/`HashMap` need correct `equals`+`hashCode`.
- `TreeSet`/`TreeMap` need ordering via `Comparable` or a `Comparator`.

---

## 7. Generics and PECS

- Generics are **invariant**: `List<Integer>` is **not** a `List<Number>`.
- **PECS** = **P**roducer **E**xtends, **C**onsumer **S**uper.
  - `? extends T` → you can **read** `T`, cannot add (except `null`).
  - `? super T` → you can **add** `T`, reads give `Object`.
- Generic method declares `<T>` before the return type: `static <T> T first(List<T> l)`.

---

## 8. `Comparable` vs `Comparator`

| | `Comparable<T>` | `Comparator<T>` |
|---|---|---|
| Method | `int compareTo(T o)` | `int compare(T a, T b)` |
| Order | natural, inside the class | external / custom |
| Use | `Collections.sort(list)` | `list.sort(comparator)` |

`compareTo`/`compare` return **negative / 0 / positive** (not a boolean).

```java
list.sort(Comparator.comparingInt(Student::getGrade).reversed());
```

---

## 9. Lambdas and functional interfaces

- A **functional interface** has exactly one abstract method; a lambda implements it.
- Common ones: `Predicate<T>` (`test`), `Function<T,R>` (`apply`), `Consumer<T>` (`accept`),
  `Supplier<T>` (`get`).

```java
Predicate<Integer> isEven = n -> n % 2 == 0;
Function<String,Integer> len = String::length; // method reference
```

---

## 10. Streams

- Pipeline = **source → intermediate ops (lazy) → terminal op**.
- Intermediate: `filter`, `map`, `sorted`, `distinct`, `limit`, `skip` (return a `Stream`).
- Terminal: `collect`, `count`, `forEach`, `toList`, `reduce`.
- Streams are **single-use** and do **not** modify the source.

```java
List<String> r = names.stream().filter(n -> n.length() > 3).map(String::toUpperCase).toList();
```

Collectors: `toList`, `toSet`, `toMap` (throws on duplicate keys unless you add a merge
function), `joining`, `groupingBy`, `partitioningBy`, `counting`, `summingInt`, `averagingInt`.

---

## 11. Optional

- A box that holds a value **or** nothing — avoids `null`.
- `Optional.of(x)` (x must be non-null), `Optional.ofNullable(x)`, `Optional.empty()`.
- Read safely with `isPresent`, `orElse(default)`, `ifPresent(...)`. `get()` is unsafe.

```java
Optional<String> name = Optional.ofNullable(maybeNull);
System.out.println(name.orElse("unknown"));
```

---

## 12. Exceptions

- **Checked** (e.g. `IOException`) must be caught or declared with `throws`.
- **Unchecked** (`RuntimeException` family, e.g. `NullPointerException`) need not be.
- `throw` = throw now (in a body); `throws` = declare (in a signature).
- `finally` almost always runs, even after a `return` in `try`/`catch`.
- **try-with-resources** auto-closes resources: `try (BufferedReader r = ...) { }`.

---

## 13. Java IO

- `Reader`/`Writer` = characters (text); `InputStream`/`OutputStream` = bytes.
- `Reader` has **no** `readLine()`; wrap it: `new BufferedReader(new FileReader(f))`.
- Modern NIO: `Path p = Path.of("a.txt");` `Files.readAllLines(p);` `Files.writeString(p, s);`.

---

## 14. Date and Time (`java.time`)

- `LocalDate` (date), `LocalTime` (time), `LocalDateTime` (both, no timezone) — all immutable.
- Format = object → String; parse = String → object; both use `DateTimeFormatter`.
- `Duration` = time-based (hours/min/sec); `Period` = date-based (years/months/days).
- `ChronoUnit.DAYS.between(a, b)` gives a single total number.

---

## 15. JUnit basics

- `@Test` marks a test; `@BeforeEach`/`@AfterEach` run around each test.
- Assertions: `assertEquals(expected, actual)`, `assertTrue`, `assertThrows(Ex.class, () -> ...)`.
- **Failure** = an assertion failed; **error** = an unexpected exception was thrown.

---

## 16. Git and Maven (theory)

- Git model: **Copy-Modify-Merge** (not lock-based). Flow: working dir → `git add` (staging)
  → `git commit` (history).
- Conflict markers `<<<<<<<`, `=======`, `>>>>>>>` must all be removed, then `git add`.
- Maven project = `pom.xml`; coordinates = `groupId : artifactId : version`.
- Lifecycle order: **compile → test → package** (a later phase runs earlier ones).
- `test` scope = available only for tests (e.g. JUnit).

---

## 17. JDBC

- Prefer `PreparedStatement` over `Statement` — it binds parameters and prevents SQL injection.
- Transactions: `conn.setAutoCommit(false); ... conn.commit();` (or `rollback()` on error).
- Use try-with-resources for `Connection`, `PreparedStatement`, `ResultSet`.

---

## 18. JPA / ORM

- `@Entity` (+ optional `@Table`), `@Id`, `@GeneratedValue`, `@Column`; needs a no-arg constructor.
- Relationships: `@OneToOne`, `@OneToMany`, `@ManyToOne`, `@ManyToMany`.
- Fetch types: **`EAGER`** loads related data immediately; **`LAZY`** loads on first access.
  Defaults: `@ManyToOne`/`@OneToOne` = EAGER, `@OneToMany`/`@ManyToMany` = LAZY.
- `cascade` propagates operations (e.g. save/delete) to related entities.
