# Java Classes, Objects, and Encapsulation - Complete Guide

## Overview
A class is a blueprint. An object is an instance created from that blueprint. Encapsulation means protecting object data and exposing controlled methods to access or modify it.

---

## 1. Class and object

Example class:

```java
public class Student {
    String name;
    int age;

    void introduce() {
        System.out.println("I am " + name + " and I am " + age + " years old.");
    }
}
```

Create objects:

```java
public class Main {
    public static void main(String[] args) {
        Student s1 = new Student();
        s1.name = "Sara";
        s1.age = 22;
        s1.introduce();
    }
}
```

Output:

```text
I am Sara and I am 22 years old.
```

---

## 2. Fields and methods

Fields store object state:

```java
String name;
int age;
```

Methods define behavior:

```java
void introduce() { }
```

---

## 3. Constructor

A constructor initializes an object when it is created.

```java
public class Student {
    private String name;
    private int age;

    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
```

Use:

```java
Student s = new Student("Sara", 22);
```

---

## 4. Encapsulation

Instead of exposing fields directly, make them `private` and use public methods.

```java
public class BankAccount {
    private double balance;

    public BankAccount(double initialBalance) {
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }
        this.balance = initialBalance;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        balance += amount;
    }
}
```

Why this is better:

- invalid values can be rejected
- internal representation can change later
- object rules are centralized in one place

---

## 5. Getters and setters

Getter:

```java
public String getName() {
    return name;
}
```

Setter:

```java
public void setName(String name) {
    if (name == null || name.isBlank()) {
        throw new IllegalArgumentException("Name cannot be empty");
    }
    this.name = name;
}
```

Not every field must have a setter. If a value should not change, do not provide a setter.

---

## 6. The `this` keyword

`this` refers to the current object.

```java
public Student(String name) {
    this.name = name;
}
```

Without `this`, Java would think both names refer to the parameter:

```java
name = name; // useless
```

---

## 7. Common mistakes

### Mistake 1: making every field public
This breaks encapsulation.

### Mistake 2: setter without validation
A setter should protect the object from invalid states.

### Mistake 3: confusing class and object
The class is the blueprint; the object is the concrete instance.

---

## Mini quiz

### Q1. What is an object?
Answer: an instance of a class.

### Q2. Why use private fields?
Answer: to protect data and control access through methods.

### Q3. What does `this` refer to?
Answer: the current object.
