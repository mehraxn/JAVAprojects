# Common Exam Questions (Mixed)

A broad mixed set across the whole course, with answers. This set focuses on **JUnit**,
**Git/Maven theory**, and general concepts. For topic-specific drills see the other files
in this folder. Answers are given right after each question — cover them and test yourself.

---

## Part A — Multiple choice

**Q1.** Which comparison checks object **content** (not identity)?
a. `==`  b. `.equals()`  c. `hashCode()`  d. `compareTo() == 1`

**Answer:** b. `.equals()` compares content (when overridden); `==` compares references.

---

**Q2.** Which collection forbids duplicate elements?
a. `List`  b. `ArrayList`  c. `Set`  d. `Queue`

**Answer:** c. `Set`.

---

**Q3.** Which is a **checked** exception?
a. `NullPointerException`  b. `IOException`  c. `ArithmeticException`  d. `ArrayIndexOutOfBoundsException`

**Answer:** b. `IOException` (the rest are unchecked `RuntimeException`s).

---

**Q4.** Which annotation marks a JUnit 5 test method?
a. `@TestCase`  b. `@Test`  c. `@RunWith`  d. `@Assert`

**Answer:** b. `@Test`.

---

**Q5.** In JUnit, what is the difference between a test **failure** and a test **error**?
a. No difference
b. Failure = assertion did not hold; Error = unexpected exception thrown
c. Failure = compilation problem; Error = wrong answer
d. Failure = timeout; Error = assertion

**Answer:** b. A **failure** is a failed assertion (e.g. `assertEquals` mismatch); an
**error** is an unexpected exception during the test.

---

**Q6.** Which strategy does Git use for collaboration?
a. Lock-Modify-Unlock  b. Check-out/Check-in  c. Copy-Modify-Merge  d. Lock-Unlock-Modify

**Answer:** c. Copy-Modify-Merge.

---

**Q7.** In Maven, which file defines the project and its dependencies?
a. `build.gradle`  b. `pom.xml`  c. `project.json`  d. `MANIFEST.MF`

**Answer:** b. `pom.xml`.

---

**Q8.** What are the three Maven **coordinates**?
a. name, path, jar
b. groupId, artifactId, version
c. compile, test, package
d. src, target, resources

**Answer:** b. `groupId`, `artifactId`, `version`.

---

**Q9.** Which JDBC type should you use with user input to avoid SQL injection?
a. `Statement`  b. `PreparedStatement`  c. `ResultSet`  d. `DriverManager`

**Answer:** b. `PreparedStatement`.

---

**Q10.** Which stream operation is **terminal**?
a. `filter`  b. `map`  c. `sorted`  d. `collect`

**Answer:** d. `collect`.

---

## Part B — True / False

**Q11.** An interface can have a constructor. **Answer:** False.

**Q12.** A `finally` block runs even if the `try` block returns a value. **Answer:** True (almost always).

**Q13.** `List<Dog>` is a subtype of `List<Animal>` when `Dog extends Animal`. **Answer:** False (generics are invariant).

**Q14.** A stream can be reused after a terminal operation. **Answer:** False (single-use).

**Q15.** In Maven, running `package` also runs `compile` and `test` first. **Answer:** True.

**Q16.** `@ManyToOne` relationships are `LAZY` by default. **Answer:** False (they are `EAGER` by default; `@OneToMany` is `LAZY`).

---

## Part C — Short answer

**Q17.** What is the difference between `throw` and `throws`?
**Answer:** `throw` actually throws an exception object from inside a method body;
`throws` declares in the method signature that the method may throw an exception, so callers
must handle or re-declare it.

**Q18.** Give the JUnit assertion to check that calling `service.load(null)` throws
`IllegalArgumentException`.
**Answer:**
```java
assertThrows(IllegalArgumentException.class, () -> service.load(null));
```

**Q19.** What does `@BeforeEach` do in JUnit?
**Answer:** It marks a method that runs **before every** `@Test` method — used to set up
fresh test data so tests do not affect each other.

**Q20.** In Git, list the steps to resolve a merge conflict.
**Answer:** Open the conflicted file, choose the final content and delete all conflict
markers (`<<<<<<<`, `=======`, `>>>>>>>`), then `git add <file>` and `git commit`.

**Q21.** Why is `PreparedStatement` preferred over `Statement`?
**Answer:** It binds parameters safely (preventing SQL injection), can be reused/precompiled,
and handles quoting/escaping of values for you.

**Q22.** What does Maven **dependency scope `test`** mean?
**Answer:** The dependency is available only when compiling and running tests (e.g. JUnit),
and is not included in the final packaged artifact.

**Q23.** Name the four JPA relationship annotations.
**Answer:** `@OneToOne`, `@OneToMany`, `@ManyToOne`, `@ManyToMany`.

---

## Part D — Mini "predict the output"

**Q24.**
```java
System.out.println(10 / 4);
System.out.println(10 % 4);
System.out.println(10.0 / 4);
```
**Answer:** `2`, then `2`, then `2.5`. (Integer division truncates; `%` is remainder; a
`double` operand gives real division.)

**Q25.**
```java
List<String> list = List.of("a", "b");
list.add("c");
```
**Answer:** Throws `UnsupportedOperationException` — `List.of(...)` is **immutable**.

---

## How to use this set

1. Cover the answers and attempt each question.
2. For anything you miss, open the matching topic folder (`1)`–`13)`).
3. Then do the topic drills: `OOP_Theory_Questions.md`,
   `Collections_Generics_Streams_Questions.md`, `Exceptions_IO_DateTime_Questions.md`,
   `JDBC_JPA_ORM_Questions.md`, and `Java_Code_Tracing_Practice.md`.
