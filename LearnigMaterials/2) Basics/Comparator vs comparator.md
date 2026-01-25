# Comparator in Java: the Interface vs “Comparator objects”

Java developers often say “a comparator” in two different ways:

1. **`Comparator` (capital C)**: the *standard Java interface type* `java.util.Comparator<T>`.
2. **a comparator (lowercase)**: an *object instance* that implements that interface and can compare two objects.

This README explains both meanings in detail, with examples.

---

## 1. What is `Comparator<T>` (the interface)?

### 1.1 Definition

`Comparator<T>` is a **standard library interface** in **`java.util`**.

* It represents a *strategy* (a rule) for ordering objects of type `T`.
* It is **generic**: `Comparator<Person>` means a comparator for `Person` objects.
* It is a **functional interface** (it has one primary abstract method), so you can implement it with a lambda.

### 1.2 The core method: `compare`

The interface’s key method is:

```java
int compare(T a, T b)
```

The method returns:

* **negative**: `a` should come **before** `b`
* **zero**: `a` and `b` are **equivalent** in ordering
* **positive**: `a` should come **after** `b`

This is exactly the same “negative / zero / positive” rule as `compareTo`, but here the rule lives **outside** the class being compared.

### 1.3 Why Java needs the `Comparator` interface

If a class **does not implement** `Comparable`, Java has no built-in “natural” ordering for it.

A `Comparator` is how you tell Java:

* “Sort these objects by age”
* “Sort these objects by name length”
* “Sort these objects by price descending”

…and so on.

### 1.4 When you use a `Comparator`

You use it when:

* You want to **sort** a collection without changing the class.
* You want **multiple different orderings** for the same class.
* You cannot (or don’t want to) modify the class to implement `Comparable`.

Common places you see it:

* `list.sort(comparator)`
* `Collections.sort(list, comparator)`
* `stream.sorted(comparator)`
* `TreeSet<>(comparator)` / `TreeMap<>(comparator)`

---

## 2. What is a “Comparator object” (an object instance)?

### 2.1 Meaning

A **Comparator object** is any object whose class **implements** `Comparator<T>`.

So:

* `Comparator<T>` is the **type** (interface)
* The thing stored in a variable like `Comparator<T> c = ...;` is an **object** that implements that interface.

This is why people say:

> “Create a comparator”

They mean:

> “Create an object that implements `Comparator`.”

### 2.2 How comparator objects are created

You can create comparator objects in several standard ways:

1. **Lambda expression** (most common)
2. **Anonymous class**
3. **A named class that implements Comparator**
4. **Factory methods** like `Comparator.comparing(...)`

---

# Examples

## Example A — `Comparator` as an interface type (and a comparator object via lambda)

### A.1 A simple domain class

```java
class Person {
    final String name;
    final int age;

    Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return name + "(" + age + ")";
    }
}
```

### A.2 Use `Comparator<Person>` (interface type) and assign a comparator object to it

```java
import java.util.*;

public class Demo {
    public static void main(String[] args) {
        List<Person> people = new ArrayList<>(List.of(
            new Person("Mina", 27),
            new Person("Ali", 19),
            new Person("Sara", 22)
        ));

        // Comparator<Person> is the INTERFACE TYPE
        // The lambda creates a COMPARATOR OBJECT implementing that interface
        Comparator<Person> byAgeAscending = (p1, p2) -> Integer.compare(p1.age, p2.age);

        people.sort(byAgeAscending);
        System.out.println(people);
    }
}
```

**What this shows**

* `Comparator<Person>` is the **interface**.
* The lambda `(p1, p2) -> ...` is turned into an **object** at runtime.
* That object has a `compare(p1, p2)` method.

---

## Example B — Comparator object via anonymous class (older style)

```java
import java.util.*;

public class Demo {
    public static void main(String[] args) {
        List<String> words = new ArrayList<>(List.of("bbb", "a", "cccc"));

        Comparator<String> byLength = new Comparator<>() {
            @Override
            public int compare(String s1, String s2) {
                return Integer.compare(s1.length(), s2.length());
            }
        };

        words.sort(byLength);
        System.out.println(words);
    }
}
```

**What this shows**

* You explicitly create a new object with `new Comparator<>() { ... }`.
* That object implements the `Comparator<String>` interface.

---

## Example C — Comparator object as a named class (reusable comparator)

When you want to reuse the comparator in many places, you can make a class:

```java
import java.util.Comparator;

class PersonByNameComparator implements Comparator<Person> {
    @Override
    public int compare(Person a, Person b) {
        return a.name.compareTo(b.name); // String has compareTo
    }
}
```

Use it like this:

```java
import java.util.*;

public class Demo {
    public static void main(String[] args) {
        List<Person> people = new ArrayList<>(List.of(
            new Person("Mina", 27),
            new Person("Ali", 19),
            new Person("Sara", 22)
        ));

        Comparator<Person> byName = new PersonByNameComparator();
        people.sort(byName);
        System.out.println(people);
    }
}
```

**What this shows**

* `PersonByNameComparator` is a normal class.
* Each `new PersonByNameComparator()` is a **comparator object**.

---

## Example D — Standard factory methods: `Comparator.comparing(...)`

Java provides helper methods to build comparators safely and cleanly.

```java
import java.util.*;
import static java.util.Comparator.*;

public class Demo {
    public static void main(String[] args) {
        List<Person> people = new ArrayList<>(List.of(
            new Person("Mina", 27),
            new Person("Ali", 19),
            new Person("Sara", 22)
        ));

        // Build a comparator object using a standard factory method
        Comparator<Person> byAge = comparing(p -> p.age);

        // Reverse it (descending)
        Comparator<Person> byAgeDesc = byAge.reversed();

        people.sort(byAgeDesc);
        System.out.println(people);
    }
}
```

**What this shows**

* `comparing(...)` returns a **comparator object**.
* You can chain operations like `reversed()`.

---

# Comparator vs Comparable (very important difference)

## Comparable

* Implemented **inside the class**.
* Defines the *natural ordering*.
* Method: `compareTo`.

Example: `String`, `Integer` already implement `Comparable`.

## Comparator

* Defined **outside the class**.
* Lets you create **many orderings** without changing the class.
* Method: `compare`.

**Practical rule:**

* Use **Comparable** when your type has one obvious “default” ordering.
* Use **Comparator** when you want custom ordering, multiple orderings, or you can’t modify the class.

---

# Summary (in one clear sentence)

* **`Comparator<T>`** is a **standard Java interface type**.
* **A comparator** is an **object instance** that implements that interface and provides a `compare(a, b)` rule.
