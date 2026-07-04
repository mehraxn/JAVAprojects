# Polymorphism and Dynamic Dispatch - Complete Guide

## Overview
Polymorphism means one variable type can refer to objects of different subclasses. Dynamic dispatch means Java chooses the overridden method based on the **real runtime object**.

---

## 1. Basic inheritance example

```java
class Animal {
    public void speak() {
        System.out.println("Some animal sound");
    }
}

class Dog extends Animal {
    @Override
    public void speak() {
        System.out.println("Woof");
    }
}

class Cat extends Animal {
    @Override
    public void speak() {
        System.out.println("Meow");
    }
}
```

Use:

```java
Animal a1 = new Dog();
Animal a2 = new Cat();

a1.speak();
a2.speak();
```

Output:

```text
Woof
Meow
```

Even though the variable type is `Animal`, Java calls the method of the real object.

---

## 2. Compile-time type vs runtime type

```java
Animal a = new Dog();
```

- Compile-time type: `Animal`
- Runtime type: `Dog`

What you can call is checked using the compile-time type.

```java
class Dog extends Animal {
    public void fetch() { }
}

Animal a = new Dog();
a.speak(); // ok
// a.fetch(); // compile error
```

To call `fetch`, you need casting:

```java
if (a instanceof Dog) {
    Dog d = (Dog) a;
    d.fetch();
}
```

---

## 3. Dynamic dispatch

Dynamic dispatch applies to overridden instance methods.

```java
Animal a = new Dog();
a.speak(); // Dog version
```

Java decides at runtime which method implementation to execute.

---

## 4. Fields are not polymorphic

Fields are resolved by the variable type, not runtime object.

```java
class Parent {
    String name = "parent";
}

class Child extends Parent {
    String name = "child";
}

Parent p = new Child();
System.out.println(p.name); // parent
```

Methods are polymorphic. Fields are not.

---

## 5. Static methods are not overridden

Static methods belong to the class, not the object.

```java
class Parent {
    static void hello() {
        System.out.println("Parent");
    }
}

class Child extends Parent {
    static void hello() {
        System.out.println("Child");
    }
}

Parent p = new Child();
p.hello(); // Parent
```

This is method hiding, not overriding.

---

## 6. Why polymorphism is useful

You can write flexible code:

```java
public static void makeAnimalSpeak(Animal animal) {
    animal.speak();
}
```

Use:

```java
makeAnimalSpeak(new Dog());
makeAnimalSpeak(new Cat());
```

No need for separate methods for each subclass.

---

## Common mistakes

### Mistake 1: thinking variable type decides overridden method
Runtime object decides overridden instance method.

### Mistake 2: thinking fields are polymorphic
They are not.

### Mistake 3: thinking static methods are overridden
They are hidden.

---

## Mini quiz

### Q1. What does this print?

```java
Animal a = new Dog();
a.speak();
```

Answer: Dog's version of `speak`.

### Q2. Are fields dynamically dispatched?
Answer: no.

### Q3. Are static methods overridden?
Answer: no, they are hidden.
