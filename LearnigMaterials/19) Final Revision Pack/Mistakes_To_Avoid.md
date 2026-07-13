# Mistakes to Avoid (Common Traps)

The errors that lose the most exam marks and cause the most bugs — grouped by topic, each
with the **wrong** version and the **right** fix. This complements `Common Java Exam Traps.md`
in this folder.

---

## Basics and OOP

**1. Comparing objects with `==` instead of `equals`.**
```java
if (name == "Ali") { ... }        // wrong for content comparison
if (name.equals("Ali")) { ... }   // right
```
`==` compares references; `equals` compares content.

**2. Overriding `equals` but not `hashCode` (or vice versa).**
Always override **both**, or `HashMap`/`HashSet` will break. Use `Objects.hash(...)`.

**3. Forgetting the parent constructor call.**
If the superclass has no no-arg constructor, you must call `super(args)` as the **first**
statement — otherwise it will not compile.

**4. Thinking fields or static methods are polymorphic.**
Only overridden **instance methods** use the runtime object. Fields and `static` methods are
resolved by the declared type.

**5. Confusing overriding and overloading.**
Overriding = same signature in a subclass (runtime). Overloading = same name, different
parameters (compile time). Changing only the return type is **not** an overload.

---

## Strings and numbers

**6. Expecting a String method to change the string.**
```java
s.toUpperCase();          // result discarded — s is unchanged
s = s.toUpperCase();      // right
```
Strings are immutable.

**7. Integer division surprise.**
```java
double avg = 5 / 2;       // 2.0, not 2.5 (int division first)
double avg = 5.0 / 2;     // 2.5
```

**8. Comparing wrapper objects with `==`.**
`Integer` values outside -128..127 are different objects. Use `.equals()` or unbox to `int`.

---

## Collections

**9. Modifying a list while iterating it with for-each.**
```java
for (String x : list) { list.remove(x); }   // ConcurrentModificationException
list.removeIf(x -> condition);              // right
```

**10. Trying to change an immutable list.**
```java
List<Integer> l = List.of(1, 2, 3);
l.add(4);                                   // UnsupportedOperationException
List<Integer> l = new ArrayList<>(List.of(1, 2, 3)); // right, if you need to modify
```

**11. Using a mutable object as a `HashMap` key and then changing it.**
Its `hashCode` changes and you can no longer find it. Keep keys effectively immutable.

**12. `TreeSet`/`TreeMap` without ordering.**
Elements/keys must be `Comparable` or you must supply a `Comparator`, or you get
`ClassCastException` at runtime.

---

## Generics

**13. Assuming `List<Sub>` is a `List<Super>`.**
Generics are invariant. Use `List<? extends Super>` to read from many subtype lists.

**14. Trying to add through `? extends T`.**
```java
List<? extends Number> l = new ArrayList<Integer>();
l.add(1);                                   // does not compile (producer, read-only)
```
Remember PECS: **P**roducer **E**xtends (read), **C**onsumer **S**uper (write).

---

## Comparable / Comparator

**15. Returning a boolean or only 0/1 from `compareTo`.**
It must return **negative / zero / positive**.

**16. Using subtraction that can overflow.**
```java
return a.value - b.value;                   // can overflow for large ints
return Integer.compare(a.value, b.value);   // right
```

**17. `compareTo` inconsistent with `equals`.**
In `TreeSet`/`TreeMap`, ordering decides equality. If `compareTo` returns 0, the element is
treated as a duplicate even if `equals` disagrees.

---

## Lambdas and streams

**18. Expecting output with no terminal operation.**
Intermediate operations are lazy; nothing runs until a terminal op like `collect`/`forEach`.

**19. Reusing a stream.**
```java
Stream<X> s = list.stream();
s.count();
s.count();                                  // IllegalStateException
```
Create a new stream from the source each time.

**20. `Collectors.toMap` with duplicate keys.**
Throws `IllegalStateException`. Add a merge function: `toMap(k, v, (a, b) -> a)`.

**21. Method call instead of method reference.**
```java
.map(Student.getName())                     // wrong
.map(Student::getName)                      // right
```

---

## Optional

**22. Calling `get()` without checking.**
```java
optional.get();                             // NoSuchElementException if empty
optional.orElse(defaultValue);              // safe
```

**23. `Optional.of(null)`.**
Throws `NullPointerException`. Use `Optional.ofNullable(x)` when `x` might be null.

---

## Exceptions and IO

**24. Calling `readLine()` on a plain `Reader`.**
`Reader`/`FileReader` do not have it — wrap in `BufferedReader`.

**25. `return` inside `finally`.**
It overrides the `try`/`catch` return and hides exceptions. Avoid it.

**26. Catching a broad exception before a specific one.**
```java
catch (Exception e) { }                     // must come AFTER specific catches
catch (IOException e) { }                    // otherwise unreachable -> compile error
```

**27. Forgetting to close resources.**
Use **try-with-resources** so files/connections close automatically.

---

## Date/Time

**28. Ignoring immutability.**
```java
date.plusDays(1);                           // result lost
date = date.plusDays(1);                    // right
```

**29. Using `Duration` for months/years.**
Months vary in length — use `Period` (or `ChronoUnit.MONTHS`).

**30. Pattern/text mismatch when parsing.**
`parse("10-01-2026", ofPattern("dd/MM/yyyy"))` throws `DateTimeParseException`. Match the
separators and case (`MM` = month, `mm` = minutes, `HH` = 24h, `hh` = 12h).

---

## JUnit, Git, Maven

**31. Confusing test failure and error.**
Failure = an assertion did not hold. Error = an unexpected exception was thrown.

**32. Leaving conflict markers in a file.**
All of `<<<<<<<`, `=======`, `>>>>>>>` must be removed, then `git add`, before committing.

**33. Giving JUnit the wrong Maven scope.**
Test libraries should use `<scope>test</scope>` so they are not shipped in the package.

---

## JDBC, JPA, ORM

**34. Building SQL by string concatenation with user input.**
Opens SQL injection. Use `PreparedStatement` with `?` parameters (index starts at **1**).

**35. Forgetting the no-arg constructor on an entity.**
JPA cannot instantiate it — add a (possibly `protected`) no-arg constructor.

**36. Assuming all associations load automatically.**
To-many associations are **LAZY** by default; accessing them after the session closes throws
`LazyInitializationException`. Fetch what you need while the session is open.

---

## Final reminder

Before the exam, re-read this file plus `Java_Final_Cheat_Sheet.md`, then attempt
`Java_Code_Tracing_Practice.md` under time pressure. Most lost marks come from the traps
above, not from hard theory.
