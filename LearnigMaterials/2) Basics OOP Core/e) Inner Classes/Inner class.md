# Understanding Inner Classes in Java

## What Is an Inner Class?

An **inner class** in Java is a class defined inside another class. It helps logically group classes that are only used in one place, increases encapsulation, and makes code more readable and maintainable.

Inner classes have direct access to the outer class’s members (including private ones), making them useful when a class requires close coupling with its container class.

---

## Why Use an Inner Class?

### 1. **Encapsulation and Information Hiding**

Inner classes allow developers to hide implementation details from the outside world. If a class is only meaningful to its outer class, keeping it inside prevents pollution of the package namespace.

### 2. **Logical Grouping**

When one class is logically tied to another—for example, a `Node` inside a `LinkedList`—placing it inside the outer class helps represent that relationship clearly.

### 3. **Better Access to Outer Class Attributes**

Inner classes have access to all fields and methods of the outer class, even `private` ones. This allows tighter integration.

### 4. **Organized Code Structure**

It makes the code more modular and improves maintainability.

---

## Types of Inner Classes

* **Member inner classes (non‑static)**
* **Static inner classes**
* **Local inner classes** (defined inside a block/method)
* **Anonymous inner classes**

This README focuses mainly on **member inner classes**.

---

## Public vs Private Inner Classes

Inner classes can use all access levels:

### **Public Inner Class**

* Visible anywhere the outer class is visible.
* Allows external instantiation using: `OuterClass.InnerClass obj = outer.new InnerClass();`

#### Example

```java
public class Car {
    public class Engine {
        public void start() {
            System.out.println("Engine started");
        }
    }
}

// Outside the class
Car car = new Car();
Car.Engine engine = car.new Engine();
engine.start();
```

### **Private Inner Class**

* Only accessible inside the outer class.
* Cannot be instantiated from outside.
* Used when the inner class is an implementation detail and should not be exposed.

#### Example

```java
public class Car {
    private class Engine {
        private int horsepower = 150;
        void start() {
            System.out.println("Engine started with " + horsepower + " HP");
        }
    }

    public void startCar() {
        Engine e = new Engine();
        e.start();
    }
}

// Outside the class
Car car = new Car();
car.startCar();      // OK
// Car.Engine engine = car.new Engine(); // ERROR: Engine is private
```

---

## Why Put Attributes Inside an Inner Class?

Inner classes often contain attributes that represent a component or state directly related to the outer class.

### Benefits

* Keeps related data tightly coupled with its logic.
* Helps isolate the internal structure of the outer class.
* Reduces exposure of potentially sensitive or irrelevant details.

#### Example: Node in a Linked List

```java
public class LinkedList {
    private class Node {
        int value;
        Node next;

        Node(int value) {
            this.value = value;
        }
    }

    private Node head;

    public void add(int value) {
        Node newNode = new Node(value);
        newNode.next = head;
        head = newNode;
    }
}
```

### Purpose of attributes in inner classes

* `value` and `next` belong **only** to `Node`, not the outer `LinkedList`.
* Hiding them prevents misuse from outside code.
* The outer class controls all interactions, ensuring a safe, consistent design.

---

## Summary

* Inner classes help **encapsulate**, **organize**, and **logically group** code.
* **Public inner classes** allow external access when needed.
* **Private inner classes** hide internal details and help enforce strong encapsulation.
* Attributes in inner classes represent tightly bound components of the outer class.

Inner classes are a powerful feature when used to structure complex logic in a clean and secure way.
