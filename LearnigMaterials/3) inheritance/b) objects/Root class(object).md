# The Ultimate Guide to Java's `Object` Class

## Table of Contents
1. [Introduction](#introduction)
2. [The Object Class Hierarchy](#the-object-class-hierarchy)
3. [Why Object is the Root Class](#why-object-is-the-root-class)
4. [All Methods in Object Class](#all-methods-in-object-class)
5. [Object as Universal Reference Type](#object-as-universal-reference-type)
6. [Polymorphism with Object](#polymorphism-with-object)
7. [Understanding Your Example](#understanding-your-example)
8. [Practical Use Cases](#practical-use-cases)
9. [Common Pitfalls](#common-pitfalls)
10. [Best Practices](#best-practices)

---

## Introduction

The `Object` class is the **root of the class hierarchy** in Java. Every class in Java directly or indirectly inherits from `Object`. This means:

```
Object (the ultimate superclass)
  ↓
Every single class in Java
```

When you write:
```java
class A { }
```

Java automatically treats it as:
```java
class A extends Object { }
```

---

## The Object Class Hierarchy

### Visual Representation

```
                    Object
                      |
        +-------------+-------------+
        |             |             |
      String        Number          A
                      |             |
                +-----+-----+       B
                |           |       |
             Integer     Double     C
```

**Everything inherits from Object!**

### In Your Example

```
Object (root)
  ↓
  A
  ↓
  B
  ↓
  C
```

When you create a `C` object:
- C inherits from B
- B inherits from A  
- A inherits from Object (implicitly)

Therefore: **C IS-A B, C IS-A A, and C IS-A Object**

---

## Why Object is the Root Class

### 1. **Universal Compatibility**
Any reference can be stored as `Object`:
```java
Object o1 = new String("Hello");
Object o2 = new Integer(42);
Object o3 = new ArrayList<>();
Object o4 = new C();
```

### 2. **Generic Collections Before Generics**
Before Java 5 generics, collections stored `Object`:
```java
ArrayList list = new ArrayList();
list.add("String");
list.add(123);
list.add(new C());
// All stored as Object
```

### 3. **Common Behavior**
Every object needs certain basic operations:
- String representation (`toString()`)
- Equality checking (`equals()`)
- Hash code generation (`hashCode()`)
- Etc.

Object class provides these fundamental methods!

---

## All Methods in Object Class

### Complete List of Object Methods

| Method | Purpose | Should Override? |
|--------|---------|------------------|
| `toString()` | String representation | ✅ Usually YES |
| `equals(Object obj)` | Check equality | ✅ Usually YES |
| `hashCode()` | Hash code for collections | ✅ If equals() overridden |
| `clone()` | Create copy | ⚠️ Sometimes |
| `getClass()` | Get runtime class | ❌ Final - Cannot override |
| `finalize()` | Cleanup before GC | ❌ Deprecated |
| `wait()` | Thread synchronization | ❌ Final |
| `notify()` | Thread synchronization | ❌ Final |
| `notifyAll()` | Thread synchronization | ❌ Final |

### 1. `toString()` Method

**Default Behavior:**
```java
// Object's default toString():
// ClassName@HashCodeInHex
// Example: A@15db9742
```

**Your Example:**
```java
class A {
    public String toString() {
        return "I am class A";
    }
}

A a = new A();
System.out.println(a);  // Prints: I am class A
// Without override: prints A@15db9742
```

**Why Override?**
- Debugging: See meaningful information
- Logging: Better log messages
- User display: Readable output

### 2. `equals(Object obj)` Method

**Default Behavior:**
```java
// Object's default equals():
// Returns true only if both references point to SAME object
// Equivalent to: (this == obj)
```

**Example:**
```java
A a1 = new A();
A a2 = new A();
A a3 = a1;

System.out.println(a1.equals(a2));  // false (different objects)
System.out.println(a1.equals(a3));  // true (same object)
System.out.println(a1 == a3);       // true
```

**Proper Override:**
```java
class Employee {
    int id;
    String name;
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Employee emp = (Employee) obj;
        return id == emp.id && name.equals(emp.name);
    }
}
```

### 3. `hashCode()` Method

**The Contract:**
- If `a.equals(b)` is true, then `a.hashCode() == b.hashCode()` MUST be true
- Used by HashMap, HashSet, Hashtable

**Example:**
```java
class Employee {
    int id;
    String name;
    
    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
    
    @Override
    public boolean equals(Object obj) {
        // ... (as shown above)
    }
}
```

**⚠️ Critical Rule:**
> Always override `hashCode()` when you override `equals()`!

### 4. `getClass()` Method

**Returns the runtime class of the object:**

```java
C c = new C();
A a = c;  // Upcasting
Object o = c;  // Upcasting

System.out.println(c.getClass().getName());  // C
System.out.println(a.getClass().getName());  // C (not A!)
System.out.println(o.getClass().getName());  // C (not Object!)
```

**Use Cases:**
```java
// Check exact class type
if (obj.getClass() == String.class) {
    // obj is exactly a String
}

// Get class hierarchy (your example):
Class<?> cls = c.getClass();
while (cls != null) {
    System.out.println(cls.getName());  // C, B, A, java.lang.Object
    cls = cls.getSuperclass();
}
```

### 5. `clone()` Method

**Creates a shallow copy:**
```java
class Person implements Cloneable {
    String name;
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

Person p1 = new Person();
p1.name = "John";
Person p2 = (Person) p1.clone();
```

---

## Object as Universal Reference Type

### The Power of Object References

**Any object can be assigned to Object:**

```java
Object o1 = new String("Hello");
Object o2 = new Integer(42);
Object o3 = new ArrayList<>();
Object o4 = new C();
```

### From Your Example:

```java
A a = new A();
B b = new B();
C c = new C();

// All can be stored as Object:
Object o1 = a;
Object o2 = b;
Object o3 = c;

System.out.println(o1);  // Calls A's toString()
System.out.println(o2);  // Calls B's toString()
System.out.println(o3);  // Calls C's toString()
```

**Key Point:** Even though reference type is `Object`, the actual method called depends on the **real object type** (polymorphism)!

### Collections Example

```java
// Array of Objects (can hold anything!)
Object[] objects = new Object[5];
objects[0] = "String";
objects[1] = 42;
objects[2] = new C();
objects[3] = true;
objects[4] = 3.14;

// Print all (calls appropriate toString() for each)
for (Object obj : objects) {
    System.out.println(obj);
}
```

---

## Polymorphism with Object

### What is Polymorphism?

**"Many forms"** - Same method call, different behavior based on actual object type.

### From Your Example:

```java
Object o1 = new A();
Object o2 = new B();
Object o3 = new C();

System.out.println(o1);  // "I am class A"
System.out.println(o2);  // "I am class B"
System.out.println(o3);  // "I am class C"
```

**What's happening:**
1. All three variables are type `Object`
2. Each holds a different actual object (A, B, or C)
3. When `toString()` is called, Java looks at the **runtime type**
4. Calls the appropriate overridden version

### The Decision Process

```
System.out.println(o3);
         ↓
    Calls toString()
         ↓
    What is o3's real type? → C
         ↓
    Does C have toString()? → YES
         ↓
    Call C's toString()
         ↓
    Output: "I am class C"
```

### Method Resolution

**Compile time:**
- Java knows: `o3` is declared as `Object`
- Java knows: `Object` has `toString()` method
- ✅ Compilation succeeds

**Runtime:**
- Java checks: What is `o3` actually pointing to? → A `C` object
- Java looks: Does `C` override `toString()`? → Yes
- Java calls: `C`'s version of `toString()`

---

## Understanding Your Example

### Constructor Chain

When you create `C c = new C();`:

```
Output:
A's constructor called
B's constructor called
C's constructor called
```

**Why this order?**

1. C's constructor is called
2. C's constructor implicitly calls `super()` (B's constructor)
3. B's constructor implicitly calls `super()` (A's constructor)
4. A's constructor implicitly calls `super()` (Object's constructor)
5. Object's constructor executes (does nothing visible)
6. A's constructor body executes → prints
7. B's constructor body executes → prints
8. C's constructor body executes → prints

**Rule:** Constructors execute from **top to bottom** in the hierarchy!

### Upcasting Examples

```java
A a2 = b;   // B is-a A (valid)
A a3 = c;   // C is-a A (valid)
B b2 = c;   // C is-a B (valid)

System.out.println(a2);  // Calls B's toString()
System.out.println(a3);  // Calls C's toString()
System.out.println(b2);  // Calls C's toString()
```

**Visual:**
```
Reference Type  →  Actual Object  →  Method Called
     A          →       B         →   B's toString()
     A          →       C         →   C's toString()
     B          →       C         →   C's toString()
```

### Class Hierarchy Reflection

```java
Class<?> cls = c.getClass();
while (cls != null) {
    System.out.println(cls.getName());
    cls = cls.getSuperclass();
}

// Output:
// C
// B
// A
// java.lang.Object
```

This traverses the inheritance chain until reaching `Object` (which has no superclass).

---

## Practical Use Cases

### 1. Generic Utility Methods

**Before your method knows the exact type:**

```java
public static void printObject(Object obj) {
    System.out.println("Class: " + obj.getClass().getName());
    System.out.println("String: " + obj.toString());
    System.out.println("HashCode: " + obj.hashCode());
}

// Works with ANY object:
printObject(new A());
printObject("Hello");
printObject(42);
printObject(new ArrayList<>());
```

### 2. Collections of Mixed Types

```java
List<Object> mixedList = new ArrayList<>();
mixedList.add("String");
mixedList.add(123);
mixedList.add(new C());
mixedList.add(true);

for (Object obj : mixedList) {
    System.out.println(obj);  // Calls appropriate toString()
}
```

### 3. Method Parameters

```java
public void processAny(Object obj) {
    if (obj instanceof String) {
        String s = (String) obj;
        System.out.println("String length: " + s.length());
    } else if (obj instanceof Integer) {
        Integer i = (Integer) obj;
        System.out.println("Integer value: " + i);
    } else if (obj instanceof C) {
        C c = (C) obj;
        System.out.println("C object: " + c);
    }
}
```

### 4. Wait/Notify for Thread Synchronization

```java
class SharedResource {
    private Object lock = new Object();
    
    public void waitForSignal() throws InterruptedException {
        synchronized (lock) {
            lock.wait();  // Inherited from Object
        }
    }
    
    public void sendSignal() {
        synchronized (lock) {
            lock.notifyAll();  // Inherited from Object
        }
    }
}
```

---

## Common Pitfalls

### 1. Forgetting to Override hashCode()

```java
// ❌ BAD: Only overriding equals()
class Person {
    String name;
    
    @Override
    public boolean equals(Object obj) {
        // ... comparison logic
    }
    // Missing hashCode()!
}

// Problem:
Person p1 = new Person("John");
Person p2 = new Person("John");
HashSet<Person> set = new HashSet<>();
set.add(p1);
System.out.println(set.contains(p2));  // false! (expected true)
```

**✅ CORRECT:**
```java
class Person {
    String name;
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Person person = (Person) obj;
        return name.equals(person.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
```

### 2. Comparing with == Instead of equals()

```java
String s1 = new String("Hello");
String s2 = new String("Hello");

System.out.println(s1 == s2);       // false (different objects)
System.out.println(s1.equals(s2));  // true (same content)
```

### 3. ClassCastException with Object

```java
Object obj = "Hello";
Integer num = (Integer) obj;  // ❌ Runtime Error!
```

**Safe approach:**
```java
Object obj = "Hello";
if (obj instanceof Integer) {
    Integer num = (Integer) obj;
    // ... use num
}
```

### 4. Null Object References

```java
Object obj = null;
System.out.println(obj.toString());  // ❌ NullPointerException!
```

**Safe approach:**
```java
Object obj = null;
System.out.println(obj);  // ✅ Prints "null" (handled by println)
// Or:
if (obj != null) {
    System.out.println(obj.toString());
}
```

---

## Best Practices

### 1. Always Override toString()

```java
// ✅ GOOD
class Person {
    String name;
    int age;
    
    @Override
    public String toString() {
        return "Person{name='" + name + "', age=" + age + "}";
    }
}
```

### 2. Override equals() and hashCode() Together

```java
// ✅ GOOD - Use IDE or Objects utility
class Person {
    String name;
    int age;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return age == person.age && Objects.equals(name, person.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }
}
```

### 3. Use instanceof Before Casting

```java
public void process(Object obj) {
    if (obj instanceof String) {
        String s = (String) obj;
        // Safe to use s
    }
}
```

### 4. Prefer Specific Types Over Object

```java
// ❌ AVOID (unless truly necessary)
public Object getData() {
    return "some data";
}

// ✅ BETTER
public String getData() {
    return "some data";
}
```

### 5. Use Generics Instead of Object (Modern Java)

```java
// ❌ OLD WAY
List list = new ArrayList();
list.add("String");
String s = (String) list.get(0);  // Casting required

// ✅ NEW WAY
List<String> list = new ArrayList<>();
list.add("String");
String s = list.get(0);  // No casting needed
```

---

## Summary

### Key Takeaways

1. **Object is the root** of all classes in Java
2. **Every class inherits** from Object (directly or indirectly)
3. **Object provides fundamental methods** that all objects need
4. **Polymorphism works through Object** - same reference type, different behaviors
5. **Always override** `toString()`, `equals()`, and `hashCode()` when appropriate
6. **Object references** can hold any object, making them powerful but requiring careful casting

### The Power of Object Class

```java
// From your example:
Object o1 = new A();  // A inherits from Object
Object o2 = new B();  // B → A → Object
Object o3 = new C();  // C → B → A → Object

// All print different results (polymorphism):
System.out.println(o1);  // "I am class A"
System.out.println(o2);  // "I am class B"
System.out.println(o3);  // "I am class C"

// The reference is Object, but behavior is determined by actual type!
```

### Final Thought

The `Object` class is the foundation that makes Java's object-oriented features work. Understanding it is crucial for mastering Java inheritance, polymorphism, and proper object design.

---

**Happy Coding! 🚀**