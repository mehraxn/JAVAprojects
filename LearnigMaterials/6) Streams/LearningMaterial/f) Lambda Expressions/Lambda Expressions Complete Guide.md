# Java Lambda Expressions - Complete Guide

## Overview
A lambda expression is a short way to write an implementation of a functional interface. Lambdas are essential for streams, collection operations, and callbacks.

---

## 1. Functional interface reminder

A functional interface has exactly one abstract method.

```java
@FunctionalInterface
interface Greeting {
    void sayHello(String name);
}
```

Without lambda:

```java
Greeting g = new Greeting() {
    @Override
    public void sayHello(String name) {
        System.out.println("Hello " + name);
    }
};
```

With lambda:

```java
Greeting g = name -> System.out.println("Hello " + name);
```

---

## 2. Lambda syntax

General form:

```java
(parameters) -> expressionOrBlock
```

Examples:

```java
x -> x * 2
(x, y) -> x + y
name -> System.out.println(name)
() -> System.out.println("Hello")
```

Block body:

```java
(x, y) -> {
    int sum = x + y;
    return sum;
}
```

---

## 3. Common functional interfaces

| Interface | Method | Example use |
|---|---|---|
| `Predicate<T>` | `boolean test(T t)` | filtering |
| `Function<T,R>` | `R apply(T t)` | mapping |
| `Consumer<T>` | `void accept(T t)` | printing/side effects |
| `Supplier<T>` | `T get()` | creating values |
| `Comparator<T>` | `int compare(T a, T b)` | sorting |

---

## 4. Predicate example

```java
Predicate<Integer> isEven = n -> n % 2 == 0;
System.out.println(isEven.test(4)); // true
```

Use with stream:

```java
List<Integer> evens = numbers.stream()
    .filter(n -> n % 2 == 0)
    .toList();
```

---

## 5. Function example

```java
Function<String, Integer> length = s -> s.length();
System.out.println(length.apply("Java")); // 4
```

Use with stream:

```java
List<Integer> lengths = words.stream()
    .map(s -> s.length())
    .toList();
```

---

## 6. Consumer example

```java
Consumer<String> printer = s -> System.out.println(s);
printer.accept("hello");
```

Use with `forEach`:

```java
names.forEach(name -> System.out.println(name));
```

---

## 7. Effectively final variables

A lambda can use local variables only if they are final or effectively final.

```java
int bonus = 10;
Function<Integer, Integer> addBonus = x -> x + bonus;
```

This is okay because `bonus` is not changed.

Wrong:

```java
int bonus = 10;
bonus++;
Function<Integer, Integer> addBonus = x -> x + bonus; // not effectively final
```

---

## Common mistakes

### Mistake 1: using lambda with non-functional interface
Lambda works only when the target type has one abstract method.

### Mistake 2: forgetting return in block body

```java
x -> { x * 2; } // wrong
x -> { return x * 2; } // correct
```

### Mistake 3: modifying captured local variables
Local variables used in lambdas must be final or effectively final.

---

## Mini quiz

### Q1. What is a functional interface?
Answer: an interface with exactly one abstract method.

### Q2. Which functional interface returns boolean?
Answer: `Predicate<T>`.

### Q3. Can a lambda modify a local variable from outside?
Answer: no, it must be final or effectively final.
