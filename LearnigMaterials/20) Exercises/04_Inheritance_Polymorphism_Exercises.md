# 04 — Inheritance and Polymorphism Exercises

Topics: `extends`, `super`, method overriding, dynamic dispatch, overriding vs overloading.
Solutions under each **Solution** heading. Outputs checked by static review.

---

## Exercise 1 — Base class and subclass

Create `Shape` with a method `double area()` returning `0`, and `Circle extends Shape` with a
`radius` that overrides `area()`. Print the area of a circle with radius 2.

**Expected output:**
```
12.566370614359172
```

### Solution
```java
class Shape {
    double area() { return 0; }
}
class Circle extends Shape {
    private double radius;
    Circle(double radius) { this.radius = radius; }
    @Override
    double area() { return Math.PI * radius * radius; }
}
// new Circle(2).area() -> Math.PI * 4 = 12.566370614359172
```

---

## Exercise 2 — Using `super`

Add `toString` to `Shape` returning `"Shape"`, and to `Circle` returning
`"Circle extends " + super.toString()`.

**Expected output:**
```
Circle extends Shape
```

### Solution
```java
class Shape {
    @Override public String toString() { return "Shape"; }
}
class Circle extends Shape {
    @Override public String toString() {
        return "Circle extends " + super.toString();
    }
}
```

---

## Exercise 3 — Polymorphic array

Create a `Shape[]` holding a `Circle(1)` and a `Shape`, loop over it and print each area.

**Expected output:**
```
3.141592653589793
0.0
```

### Solution
```java
Shape[] shapes = { new Circle(1), new Shape() };
for (Shape s : shapes) {
    System.out.println(s.area());
}
```
Each call uses the real object's `area()` (dynamic dispatch).

---

## Exercise 4 — Predict the output (dynamic dispatch)

```java
class Animal { String sound() { return "..."; } }
class Cat extends Animal { String sound() { return "Meow"; } }

public class Main {
    public static void main(String[] args) {
        Animal a = new Cat();
        System.out.println(a.sound());
    }
}
```

### Solution
```
Meow
```
The variable type is `Animal`, but the **runtime object** is `Cat`, so `Cat.sound()` runs.

---

## Exercise 5 — Predict the output (fields are not polymorphic)

```java
class A { int x = 1; }
class B extends A { int x = 2; }

public class Main {
    public static void main(String[] args) {
        A ref = new B();
        System.out.println(ref.x);
        System.out.println(((B) ref).x);
    }
}
```

### Solution
```
1
2
```
Fields are resolved by the **declared type**. `ref.x` uses `A`'s field (1); casting to `B`
exposes `B`'s field (2). (Only overridden methods, not fields, are polymorphic.)

---

## Exercise 6 — Overriding vs overloading

Identify which is overriding and which is overloading:
```java
class Printer {
    void print(String s) { }        // (1)
    void print(int n) { }           // (2)
}
class ColorPrinter extends Printer {
    @Override void print(String s) { } // (3)
}
```

### Solution
- (2) is **overloading** of (1) — same name, different parameter type, same class.
- (3) is **overriding** of (1) — same signature in a subclass.
Overloading is chosen at compile time by argument types; overriding at runtime by the object.

---

## Exercise 7 — Fix the bug (wrong `super` usage)

This does not compile: the subclass constructor must pass a name to the parent. Fix it.
```java
class Employee {
    Employee(String name) { }
}
class Manager extends Employee {
    Manager() { }   // error
}
```

### Solution
`Employee` has no no-arg constructor, so add an explicit `super(...)` call:
```java
class Manager extends Employee {
    Manager() { super("Unknown"); }
}
```

---

## Exercise 8 — Fix the bug (accidental overloading instead of overriding)

The author meant to override `sound()`, but the subclass never gets used polymorphically —
`speak` still prints `"..."`. Why, and how to fix?
```java
class Animal {
    String sound() { return "..."; }
    void speak() { System.out.println(sound()); }
}
class Dog extends Animal {
    String sound(String tone) { return "Woof"; }  // intended override?
}
// new Dog().speak();  prints "..."
```

### Solution
`sound(String tone)` has a **different parameter list**, so it is an *overload*, not an
override — `Animal.sound()` is still used. Match the signature exactly (and add `@Override` to
let the compiler catch mistakes):
```java
class Dog extends Animal {
    @Override
    String sound() { return "Woof"; }
}
// new Dog().speak(); now prints "Woof"
```

---

## Challenge — Payroll hierarchy

Create `Employee` with `double monthlyPay()` and `Manager extends Employee` that adds a bonus.
Store both in an `Employee[]` and print total monthly pay.

### Solution
```java
class Employee {
    protected double salary;
    Employee(double salary) { this.salary = salary; }
    double monthlyPay() { return salary; }
}
class Manager extends Employee {
    private double bonus;
    Manager(double salary, double bonus) { super(salary); this.bonus = bonus; }
    @Override double monthlyPay() { return salary + bonus; }
}

Employee[] staff = { new Employee(3000), new Manager(5000, 1000) };
double total = 0;
for (Employee e : staff) total += e.monthlyPay();
System.out.println(total); // 9000.0
```
