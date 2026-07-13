# 05 — Abstract Classes and Interfaces Exercises

Topics: abstract classes, abstract methods, interfaces, `default`/`static` interface methods,
implementing multiple interfaces, marker interfaces. Solutions under each **Solution** heading.
Code checked by static review.

---

## Exercise 1 — Abstract class

Create an abstract class `Shape` with an abstract method `double area()` and a concrete method
`describe()` that prints `"Area = " + area()`. Create a `Square` subclass.

**Expected output** (for a square of side 3):
```
Area = 9.0
```

### Solution
```java
abstract class Shape {
    abstract double area();
    void describe() { System.out.println("Area = " + area()); }
}
class Square extends Shape {
    private double side;
    Square(double side) { this.side = side; }
    @Override double area() { return side * side; }
}
// new Square(3).describe(); -> Area = 9.0
```

---

## Exercise 2 — Interface

Define an interface `Playable` with a method `play()`. Make `Guitar` implement it.

**Expected output:**
```
Strum strum
```

### Solution
```java
interface Playable {
    void play();
}
class Guitar implements Playable {
    @Override public void play() { System.out.println("Strum strum"); }
}
// new Guitar().play(); -> Strum strum
```

---

## Exercise 3 — Default method

Add a `default` method `stop()` to `Playable` that prints `"Stopped"`. Show a `Guitar` calling
it without implementing it.

**Expected output:**
```
Stopped
```

### Solution
```java
interface Playable {
    void play();
    default void stop() { System.out.println("Stopped"); }
}
// new Guitar().stop(); -> Stopped (inherited default)
```

---

## Exercise 4 — Implement multiple interfaces

Create interfaces `Swimmer` (`swim()`) and `Runner` (`run()`), and a class `Athlete` that
implements both.

### Solution
```java
interface Swimmer { void swim(); }
interface Runner  { void run(); }

class Athlete implements Swimmer, Runner {
    @Override public void swim() { System.out.println("Swimming"); }
    @Override public void run()  { System.out.println("Running"); }
}
```
A class can implement **many** interfaces (but extend only one class).

---

## Exercise 5 — Abstract class vs interface (theory)

Answer in one or two sentences each:
1. Can an interface have instance fields with state?
2. Can an abstract class have a constructor?
3. How many classes can you `extends`? How many interfaces can you `implements`?

### Solution
1. No — interface fields are implicitly `public static final` constants.
2. Yes — it runs when a subclass object is created (via `super(...)`).
3. One class; any number of interfaces.

---

## Exercise 6 — Predict the output (default + override)

```java
interface Greeter {
    default String hello() { return "Hello from interface"; }
}
class Friendly implements Greeter {
    @Override public String hello() { return "Hi!"; }
}

public class Main {
    public static void main(String[] args) {
        Greeter g = new Friendly();
        System.out.println(g.hello());
    }
}
```

### Solution
```
Hi!
```
The class overrides the default method, and dynamic dispatch picks the class's version.

---

## Exercise 7 — Fix the bug (instantiating an abstract class)

This does not compile. Fix it.
```java
abstract class Vehicle {
    abstract void move();
}
public class Main {
    public static void main(String[] args) {
        Vehicle v = new Vehicle(); // error
    }
}
```

### Solution
You cannot instantiate an abstract class. Create a concrete subclass:
```java
class Car extends Vehicle {
    @Override void move() { System.out.println("Driving"); }
}
// Vehicle v = new Car();
```

---

## Exercise 8 — Fix the bug (unimplemented interface method)

This does not compile. Fix it two ways.
```java
interface Printable { void print(); }
class Report implements Printable {
    // no print() method
}
```

### Solution
Either implement the method:
```java
class Report implements Printable {
    @Override public void print() { System.out.println("Report"); }
}
```
or declare the class `abstract` (so a subclass must implement it):
```java
abstract class Report implements Printable { }
```

---

## Challenge — Shapes with an interface

Define `interface HasArea { double area(); }`. Implement `Rectangle` and `Circle`. Put them in
a `HasArea[]` and print the total area of a `Rectangle(2,3)` and `Circle(1)`.

### Solution
```java
interface HasArea { double area(); }

class Rectangle implements HasArea {
    private double w, h;
    Rectangle(double w, double h) { this.w = w; this.h = h; }
    @Override public double area() { return w * h; }
}
class Circle implements HasArea {
    private double r;
    Circle(double r) { this.r = r; }
    @Override public double area() { return Math.PI * r * r; }
}

HasArea[] shapes = { new Rectangle(2, 3), new Circle(1) };
double total = 0;
for (HasArea s : shapes) total += s.area();
System.out.println(total); // 6.0 + 3.141592653589793 = 9.141592653589793
```
