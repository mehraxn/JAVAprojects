# Java Interfaces: Abstract, Default, Static, Private, and Marker Interfaces - Complete Guide

## Overview
An interface defines a contract. A class that implements the interface promises to provide the required behavior.

---

## 1. Basic interface

```java
public interface Payable {
    double calculatePayment();
}
```

Implementation:

```java
public class Employee implements Payable {
    private double salary;

    public Employee(double salary) {
        this.salary = salary;
    }

    @Override
    public double calculatePayment() {
        return salary;
    }
}
```

---

## 2. Interface methods are usually public abstract

This:

```java
interface A {
    void test();
}
```

means:

```java
interface A {
    public abstract void test();
}
```

So implementing methods must be public:

```java
class B implements A {
    @Override
    public void test() { }
}
```

---

## 3. Interface fields are constants

Fields in interfaces are implicitly:

```java
public static final
```

Example:

```java
interface Config {
    int MAX_USERS = 100;
}
```

Means:

```java
public static final int MAX_USERS = 100;
```

You cannot have normal instance fields in an interface.

---

## 4. Default methods

A default method has implementation inside an interface.

```java
interface Logger {
    default void info(String message) {
        System.out.println("INFO: " + message);
    }
}
```

A class can use it directly:

```java
class ConsoleLogger implements Logger { }
```

Use:

```java
new ConsoleLogger().info("started");
```

Default methods were added to allow interfaces to evolve without breaking all implementing classes.

---

## 5. Static methods in interfaces

```java
interface MathUtils {
    static int square(int x) {
        return x * x;
    }
}
```

Call using interface name:

```java
int result = MathUtils.square(5);
```

You do not call it through an implementing object.

---

## 6. Private methods in interfaces

Private methods are helpers for default or static methods.

```java
interface Printer {
    default void printInfo(String text) {
        printWithPrefix("INFO", text);
    }

    private void printWithPrefix(String prefix, String text) {
        System.out.println(prefix + ": " + text);
    }
}
```

---

## 7. Marker interfaces

A marker interface has no methods.

```java
public interface Auditable { }
```

It marks a class as having some meaning.

Classic Java example:

```java
java.io.Serializable
```

A marker interface is why the statement “an interface must have at least one method” is false.

---

## 8. Multiple interfaces

A class can implement multiple interfaces:

```java
class Report implements Printable, Exportable {
    // methods here
}
```

This is one reason interfaces are powerful: Java does not allow multiple class inheritance, but it allows multiple interface implementation.

---

## Common mistakes

### Mistake 1: thinking interfaces cannot have implemented methods
They can have `default`, `static`, and private methods.

### Mistake 2: thinking interface fields are instance fields
They are constants: `public static final`.

### Mistake 3: using static interface methods through objects
Call them through the interface name.

---

## Mini quiz

### Q1. Can an interface be empty?
Answer: yes.

### Q2. Can an interface have static methods?
Answer: yes.

### Q3. Are interface fields non-static?
Answer: no, they are implicitly static and final.
