# UML Class Diagram Basics for Java - Complete Guide

## Overview
UML class diagrams visually represent classes, attributes, methods, and relationships. They often appear in Java/OOP exams.

---

## 1. UML class box

A class is usually drawn as a rectangle with three parts:

```text
+------------------------+
| Student                |  <- class name
+------------------------+
| - id: String           |  <- attributes / fields
| - name: String         |
+------------------------+
| + getName(): String    |  <- methods / operations
| + setName(name): void  |
+------------------------+
```

Important exam fact:

```text
Top block    = class name
Middle block = attributes
Bottom block = methods
```

---

## 2. Visibility symbols

| Symbol | Java equivalent | Meaning |
|---|---|---|
| `+` | `public` | accessible from everywhere |
| `-` | `private` | accessible only inside class |
| `#` | `protected` | accessible in package/subclasses |
| `~` | package-private | accessible in same package |

Example:

```text
- balance: double
+ deposit(amount: double): void
```

Java equivalent:

```java
private double balance;
public void deposit(double amount) { }
```

---

## 3. Attributes

UML:

```text
- name: String
- age: int
```

Java:

```java
private String name;
private int age;
```

---

## 4. Methods

UML:

```text
+ getName(): String
+ setName(name: String): void
```

Java:

```java
public String getName() { return name; }
public void setName(String name) { this.name = name; }
```

---

## 5. Inheritance

Inheritance uses a line with a hollow triangle pointing to the parent.

```text
Dog ─────▷ Animal
```

Java:

```java
class Dog extends Animal { }
```

---

## 6. Interface implementation

Interface implementation uses a dashed line with a hollow triangle.

```text
ArrayList - - -▷ List
```

Java:

```java
class MyList implements List { }
```

---

## 7. Association

An association means one class has a relationship with another.

```text
Student -------- Course
```

Java field example:

```java
class Student {
    private Course course;
}
```

---

## 8. Multiplicity

Multiplicity shows how many objects participate.

| UML | Meaning |
|---|---|
| `1` | exactly one |
| `0..1` | zero or one |
| `*` | many |
| `1..*` | one or more |

Example:

```text
Student 1 -------- * Exam
```

Means one student can have many exams.

Java:

```java
class Student {
    private List<Exam> exams;
}
```

---

## 9. Aggregation vs composition

### Aggregation
Weak “has-a” relationship. Parts can exist independently.

```text
Team ◇──── Player
```

### Composition
Strong ownership. Part usually does not exist independently.

```text
House ◆──── Room
```

---

## Common exam traps

1. Middle block of class = attributes.
2. Bottom block = methods.
3. Hollow triangle = inheritance/interface direction points to parent/interface.
4. `*` means many.
5. UML attributes usually map to Java fields.

---

## Mini quiz

### Q1. What is in the middle block of a UML class?
Answer: attributes/fields.

### Q2. What does `+` mean?
Answer: public.

### Q3. What does `1..*` mean?
Answer: one or more.
