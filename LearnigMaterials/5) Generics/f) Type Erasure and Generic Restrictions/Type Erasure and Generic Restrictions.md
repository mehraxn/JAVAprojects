# Java Type Erasure and Generic Restrictions - Complete Guide

## Overview
Java generics mostly exist at compile time. The compiler checks types, then removes much of the generic type information. This is called **type erasure**.

---

## 1. What is type erasure?

Source code:

```java
List<String> names = new ArrayList<>();
names.add("Sara");
String name = names.get(0);
```

After compilation, the JVM mostly sees something like:

```java
List names = new ArrayList();
names.add("Sara");
String name = (String) names.get(0);
```

The compiler inserted type checks and casts.

---

## 2. Why Java uses erasure

Generics were added in Java 5 while keeping compatibility with older Java code.

Before generics:

```java
List list = new ArrayList();
list.add("hello");
```

Java needed old code and new generic code to work together.

---

## 3. Generic type is not available normally at runtime

This does not work:

```java
if (list instanceof List<String>) { } // compile error
```

You can only check raw type:

```java
if (list instanceof List<?>) { }
```

---

## 4. Cannot create generic arrays

Wrong:

```java
T[] array = new T[10]; // compile error
```

Why? Because Java arrays know their runtime element type, but generics are erased.

Common workaround:

```java
@SuppressWarnings("unchecked")
T[] array = (T[]) new Object[10];
```

Use carefully.

---

## 5. Cannot instantiate type parameter

Wrong:

```java
public class Box<T> {
    public T create() {
        return new T(); // compile error
    }
}
```

Because Java does not know what `T` is at runtime.

Use a supplier:

```java
import java.util.function.Supplier;

public class Factory<T> {
    private Supplier<T> supplier;

    public Factory(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T create() {
        return supplier.get();
    }
}
```

Use:

```java
Factory<Student> factory = new Factory<>(Student::new);
Student s = factory.create();
```

---

## 6. Cannot use primitives as generic type arguments

Wrong:

```java
List<int> numbers; // compile error
```

Correct:

```java
List<Integer> numbers;
```

Java uses wrapper classes:

| Primitive | Wrapper |
|---|---|
| `int` | `Integer` |
| `double` | `Double` |
| `boolean` | `Boolean` |
| `char` | `Character` |

---

## 7. Raw types

Raw type:

```java
List list = new ArrayList();
```

Generic type:

```java
List<String> list = new ArrayList<>();
```

Avoid raw types because they remove type safety.

Example problem:

```java
List<String> names = new ArrayList<>();
List raw = names;
raw.add(123);
String first = names.get(0); // may cause ClassCastException later
```

---

## Common mistakes

### Mistake 1: using raw collections
Always prefer `List<String>` over `List`.

### Mistake 2: checking `instanceof List<String>`
Not allowed due to erasure.

### Mistake 3: using primitive type arguments
Use wrapper classes.

---

## Mini quiz

### Q1. Are generic type parameters fully available at runtime?
Answer: usually no, because of type erasure.

### Q2. Can you create `new T()`?
Answer: no.

### Q3. Why should raw types be avoided?
Answer: they remove compile-time type safety.
