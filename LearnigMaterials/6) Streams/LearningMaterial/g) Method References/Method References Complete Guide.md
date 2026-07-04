# Java Method References - Complete Guide

## Overview
A method reference is a shorter form of a lambda expression when the lambda only calls an existing method.

---

## 1. Basic example

Lambda:

```java
names.forEach(name -> System.out.println(name));
```

Method reference:

```java
names.forEach(System.out::println);
```

Both mean: for each name, call `System.out.println(name)`.

---

## 2. Types of method references

| Type | Syntax | Example |
|---|---|---|
| Static method | `ClassName::staticMethod` | `Integer::parseInt` |
| Instance method of object | `object::method` | `System.out::println` |
| Instance method of class | `ClassName::method` | `String::length` |
| Constructor | `ClassName::new` | `ArrayList::new` |

---

## 3. Static method reference

Lambda:

```java
Function<String, Integer> parser = s -> Integer.parseInt(s);
```

Method reference:

```java
Function<String, Integer> parser = Integer::parseInt;
```

Use:

```java
System.out.println(parser.apply("123")); // 123
```

---

## 4. Instance method of existing object

```java
PrintStream out = System.out;
Consumer<String> printer = out::println;
printer.accept("Hello");
```

Most common:

```java
names.forEach(System.out::println);
```

---

## 5. Instance method of class

```java
Function<String, Integer> lengthFunction = String::length;
System.out.println(lengthFunction.apply("Java")); // 4
```

This means:

```java
s -> s.length()
```

---

## 6. Constructor reference

```java
Supplier<ArrayList<String>> supplier = ArrayList::new;
ArrayList<String> list = supplier.get();
```

With stream:

```java
List<Student> students = names.stream()
    .map(Student::new)
    .toList();
```

This means:

```java
name -> new Student(name)
```

---

## 7. Method reference in exam question

If a method expects:

```java
ToDoubleFunction<NutritionalElement> extractor
```

and class has:

```java
double getCalories()
```

then this method reference matches:

```java
NutritionalElement::getCalories
```

Example:

```java
max(elements.stream(), NutritionalElement::getCalories)
```

---

## Common mistakes

### Mistake 1: calling the method instead of passing a reference

Wrong:

```java
NutritionalElement.getCalories()
```

Correct:

```java
NutritionalElement::getCalories
```

### Mistake 2: using method reference when parameters do not match
The method reference must match the functional interface method.

### Mistake 3: thinking method references are new methods
They are only shorthand for lambdas.

---

## Mini quiz

### Q1. What does `String::length` mean?
Answer: `s -> s.length()`.

### Q2. What does `System.out::println` mean?
Answer: `x -> System.out.println(x)`.

### Q3. What does `Student::new` mean?
Answer: constructor reference, usually `x -> new Student(x)` depending on constructor.
