# Access Modifiers, static, final, and Packages - Complete Guide

## Overview
These concepts control visibility, object/class ownership, constants, inheritance restrictions, and code organization.

---

## 1. Access modifiers

| Modifier | Same class | Same package | Subclass other package | Other package |
|---|---:|---:|---:|---:|
| `public` | yes | yes | yes | yes |
| `protected` | yes | yes | yes | no |
| package-private | yes | yes | no | no |
| `private` | yes | no | no | no |

Package-private means no modifier is written.

```java
class Helper { }
```

---

## 2. `private`

Use `private` for fields and helper methods that should not be used directly from outside the class.

```java
public class User {
    private String password;

    private boolean isStrongPassword(String password) {
        return password.length() >= 8;
    }
}
```

---

## 3. `public`

Use `public` for the external API of the class.

```java
public String getName() {
    return name;
}
```

---

## 4. `static`

`static` means the member belongs to the class, not to individual objects.

```java
public class Counter {
    private static int totalCounters = 0;
    private int value = 0;

    public Counter() {
        totalCounters++;
    }

    public static int getTotalCounters() {
        return totalCounters;
    }
}
```

Use:

```java
Counter c1 = new Counter();
Counter c2 = new Counter();
System.out.println(Counter.getTotalCounters()); // 2
```

### Static method limitation
A static method cannot directly access instance fields.

Wrong:

```java
public static void printValue() {
    System.out.println(value); // compile error
}
```

Why? Because `value` belongs to an object, but the static method belongs to the class.

---

## 5. `final`

### Final variable
Cannot be reassigned.

```java
final int MAX_USERS = 100;
```

### Final method
Cannot be overridden.

```java
public final void login() { }
```

### Final class
Cannot be extended.

```java
public final class StringUtils { }
```

---

## 6. Constants

Constants are usually:

```java
public static final double PI = 3.14159;
```

Naming convention: uppercase with underscores.

```java
public static final int MAX_LOGIN_ATTEMPTS = 3;
```

---

## 7. Packages

Packages organize classes and avoid name conflicts.

At the top of a file:

```java
package university.model;
```

Import another class:

```java
import university.model.Student;
```

Example project structure:

```text
src/
  university/
    model/
      Student.java
    app/
      Main.java
```

---

## Common mistakes

### Mistake 1: using static for everything
Most fields should usually belong to objects, not to the class.

### Mistake 2: public fields
Prefer private fields and public methods.

### Mistake 3: forgetting package folder structure
The package name should match the folder path.

---

## Mini quiz

### Q1. What does `static` mean?
Answer: belongs to the class, not to a specific object.

### Q2. Can a final class be extended?
Answer: no.

### Q3. What is package-private?
Answer: access level when no access modifier is written.
