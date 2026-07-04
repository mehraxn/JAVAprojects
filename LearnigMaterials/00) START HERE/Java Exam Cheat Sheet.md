# Java Exam Cheat Sheet

## 1. JVM / JRE / JDK

| Term | Meaning |
|---|---|
| JVM | Runs Java bytecode |
| JRE | JVM + libraries needed to run Java programs |
| JDK | JRE + development tools like `javac` |

Compilation flow:

```text
Main.java  --javac-->  Main.class  --java/JVM-->  program runs
```

---

## 2. Primitive vs reference types

Primitive examples:

```java
int x = 10;
double price = 9.99;
boolean active = true;
char letter = 'A';
```

Reference examples:

```java
String name = "Ali";
Student s = new Student();
int[] numbers = new int[5];
```

Important: primitives store simple values; references store a reference to an object.

---

## 3. `==` vs `.equals()`

```java
String a = new String("hi");
String b = new String("hi");

System.out.println(a == b);      // false, different objects
System.out.println(a.equals(b)); // true, same text
```

Use `.equals()` for object content comparison.

---

## 4. Overloading vs overriding

| Concept | Meaning |
|---|---|
| Overloading | Same method name, different parameters, same class usually |
| Overriding | Subclass replaces superclass method implementation |

```java
void print(String s) {}
void print(int x) {}       // overloading

@Override
void speak() {}            // overriding
```

---

## 5. Interface fields and methods

Interface fields are implicitly:

```java
public static final
```

Interface methods can be:

- abstract
- default
- static
- private helper methods

---

## 6. Collections quick guide

| Interface | Allows duplicates? | Ordered? | Example |
|---|---:|---:|---|
| `List` | Yes | Yes | `ArrayList` |
| `Set` | No | Depends | `HashSet`, `TreeSet` |
| `Map` | Keys no, values yes | Depends | `HashMap`, `TreeMap` |
| `Queue` | Yes | Yes | `LinkedList`, `PriorityQueue` |

---

## 7. Comparator vs Comparable

`Comparable` = natural order inside the class:

```java
class Student implements Comparable<Student> {
    public int compareTo(Student other) {
        return this.name.compareTo(other.name);
    }
}
```

`Comparator` = external sorting rule:

```java
students.sort(Comparator.comparing(Student::getAge));
```

---

## 8. Stream pipeline

```java
list.stream()
    .filter(x -> x > 0)     // intermediate
    .map(x -> x * 2)        // intermediate
    .toList();              // terminal
```

Intermediate operations are lazy. Nothing runs until a terminal operation is called.

---

## 9. Checked vs unchecked exceptions

| Type | Must handle/declare? | Example |
|---|---:|---|
| Checked | Yes | `IOException` |
| Unchecked | No | `NullPointerException` |

---

## 10. JUnit failure vs error

| Result | Meaning |
|---|---|
| Failure | Assertion failed |
| Error | Unexpected exception happened |

```java
assertEquals(5, calculator.add(2, 2)); // failure if result is 4
throw new RuntimeException();          // error if unexpected
```

---

## 11. JPA relationship annotations

| Relationship | Annotation |
|---|---|
| One object to one object | `@OneToOne` |
| One object to many objects | `@OneToMany` |
| Many objects to one object | `@ManyToOne` |
| Many objects to many objects | `@ManyToMany` |

---

## 12. Common exam traps

1. `Reader` does not have `readLine()`; `BufferedReader` does.
2. `c++` returns the old value; use `c + 1` in `Map.compute`.
3. Streams are single-use.
4. `HashSet` needs correct `equals` and `hashCode`.
5. `TreeSet` needs ordering using `Comparable` or `Comparator`.
6. `finally` usually runs even if there is a `return` in `try` or `catch`.
7. Interface attributes are not instance fields.
8. `Optional.get()` is unsafe without checking presence.

---

## Where to go next

This page is the quick overview. For deeper revision use `14) Final Revision Pack`:

- `Java_Final_Cheat_Sheet.md` — a longer cheat sheet covering every topic.
- `Mistakes_To_Avoid.md` and `Common Java Exam Traps.md` — more traps, wrong-vs-right.
- `Java_Code_Tracing_Practice.md` — "predict the output" practice.
- Topic question sets (OOP, Collections/Generics/Streams, Exceptions/IO/DateTime, JDBC/JPA/ORM).

For a full study plan see `Complete Java Study Roadmap.md`; for a description of every file see
`All Markdown Files Index.md` (both in this `00) START HERE` folder).
