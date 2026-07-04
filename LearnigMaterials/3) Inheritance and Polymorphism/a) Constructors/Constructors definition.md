# Java Constructors

In Java, **constructors** are special methods used to initialize objects of a class.
They have the same name as the class and **do not have a return type**.

There are two main types of constructors: **Explicit (User-Defined) Constructors** and **Invisible (Default) Constructors**.

---

## 1. Explicit Constructor (User-Defined Constructor)

An **explicit constructor** is one that you **define yourself** in a class.
It is used to initialize objects with custom values or behavior.

**Key points:**

* Has the same name as the class.
* Can take parameters to initialize object attributes.
* Can also be **without any arguments**.
* Executes automatically when the object is created.

### Example 1: Basic explicit constructor with arguments

```java
class Car {
    String model;
    int year;

    // Explicit constructor with arguments
    Car(String model, int year) {
        this.model = model;
        this.year = year;
    }

    void display() {
        System.out.println("Car Model: " + model + ", Year: " + year);
    }
}

public class Main {
    public static void main(String[] args) {
        Car car1 = new Car("Toyota", 2020);
        Car car2 = new Car("Honda", 2022);

        car1.display();
        car2.display();
    }
}
```

### Example 2: Explicit constructor without arguments

```java
class Student {
    String name;
    int age;

    // Explicit constructor with no arguments
    Student() {
        name = "Unknown";
        age = 0;
    }

    void display() {
        System.out.println("Name: " + name + ", Age: " + age);
    }
}

public class Main {
    public static void main(String[] args) {
        Student s1 = new Student(); // Constructor without arguments
        s1.display();
    }
}
```

**Output:**

```
Name: Unknown, Age: 0
```

### Example 3: Multiple explicit constructors (Constructor Overloading)

```java
class Book {
    String title;
    double price;

    // Constructor with 1 argument
    Book(String title) {
        this.title = title;
        this.price = 0.0;
    }

    // Constructor with 2 arguments
    Book(String title, double price) {
        this.title = title;
        this.price = price;
    }

    void display() {
        System.out.println("Title: " + title + ", Price: $" + price);
    }
}

public class Main {
    public static void main(String[] args) {
        Book b1 = new Book("Java Basics");
        Book b2 = new Book("Advanced Java", 49.99);

        b1.display();
        b2.display();
    }
}
```

---

## 2. Invisible Constructor (Default Constructor)

If you **do not provide any constructor**, Java automatically provides a **default constructor** (also called **invisible constructor**).

* Takes **no parameters**
* Initializes objects with default values (`0` for numeric types, `null` for objects, `false` for boolean)
* Disappears as soon as you define an explicit constructor.

### Example 1: Default constructor in action

```java
class Animal {
    String type;
    int age;

    // No explicit constructor defined
}

public class Main {
    public static void main(String[] args) {
        Animal a1 = new Animal(); // default constructor is called automatically
        System.out.println("Type: " + a1.type + ", Age: " + a1.age);
    }
}
```

**Output:**

```
Type: null, Age: 0
```

### Example 2: Default constructor disappears if explicit constructor exists

```java
class Animal {
    String type;
    int age;

    // Explicit constructor with arguments
    Animal(String type, int age) {
        this.type = type;
        this.age = age;
    }
}

public class Main {
    public static void main(String[] args) {
        // Animal a1 = new Animal(); // ❌ Compile-time error
        Animal a2 = new Animal("Dog", 5); // ✅ Works fine
        System.out.println("Type: " + a2.type + ", Age: " + a2.age);
    }
}
```

**Output:**

```
Type: Dog, Age: 5
```

---

### Summary

| Feature                       | Explicit Constructor   | Invisible/Default Constructor |
| ----------------------------- | ---------------------- | ----------------------------- |
| User-defined                  | ✅                      | ❌ (automatically provided)    |
| Parameters                    | Optional (can be none) | None                          |
| Initializes objects           | Custom values          | Default values                |
| Disappears if explicit exists | N/A                    | ✅                             |
