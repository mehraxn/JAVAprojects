# Method Overloading vs Method Overriding - Complete Guide

## Overview
Overloading and overriding are often confused because both use method names. They are completely different concepts.

---

## 1. Overloading

Overloading means multiple methods have the same name but different parameter lists.

```java
public class Printer {
    public void print(String text) {
        System.out.println(text);
    }

    public void print(int number) {
        System.out.println(number);
    }

    public void print(String text, int times) {
        for (int i = 0; i < times; i++) {
            System.out.println(text);
        }
    }
}
```

The compiler chooses which method to call based on the arguments.

```java
Printer p = new Printer();
p.print("hello");
p.print(10);
p.print("hi", 3);
```

---

## 2. Overriding

Overriding means a subclass provides a new implementation for a superclass method.

```java
class Animal {
    public void speak() {
        System.out.println("Some sound");
    }
}

class Dog extends Animal {
    @Override
    public void speak() {
        System.out.println("Woof");
    }
}
```

Use:

```java
Animal a = new Dog();
a.speak(); // Woof
```

---

## 3. Differences table

| Feature | Overloading | Overriding |
|---|---|---|
| Same method name | yes | yes |
| Same parameter list | no | yes |
| Inheritance required | no | yes |
| Decision time | compile time | runtime |
| Return type only enough? | no | no |
| Uses `@Override` | no | yes |

---

## 4. Rules for overriding

### Same method signature

```java
@Override
public void speak() { }
```

### Cannot reduce visibility

If parent method is `public`, child method cannot be `private`.

Wrong:

```java
class Parent {
    public void test() { }
}

class Child extends Parent {
    @Override
    private void test() { } // compile error
}
```

### Return type must be same or covariant

```java
class Animal { }
class Dog extends Animal { }

class Parent {
    Animal create() { return new Animal(); }
}

class Child extends Parent {
    @Override
    Dog create() { return new Dog(); }
}
```

This is allowed because `Dog` is a subtype of `Animal`.

---

## 5. Why use `@Override`?

`@Override` asks the compiler to check that you are really overriding.

```java
@Override
public void speak() { }
```

If you accidentally write the wrong method name or parameters, the compiler catches it.

---

## Common mistakes

### Mistake 1: overloading accidentally instead of overriding

```java
class Parent {
    void show(Object o) { }
}

class Child extends Parent {
    void show(String s) { } // overload, not override
}
```

### Mistake 2: thinking return type alone overloads
It does not.

### Mistake 3: forgetting runtime dispatch for overriding
Overridden methods are selected based on runtime object.

---

## Mini quiz

### Q1. Is this overloading or overriding?

```java
void add(int x) { }
void add(double x) { }
```

Answer: overloading.

### Q2. Is overriding decided at compile time or runtime?
Answer: runtime.

### Q3. Why use `@Override`?
Answer: to let the compiler verify that a method actually overrides another method.
