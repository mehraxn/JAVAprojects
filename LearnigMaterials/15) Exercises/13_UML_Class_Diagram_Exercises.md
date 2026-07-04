# 13 — UML Class Diagram Exercises

Topics: reading and drawing UML class diagrams, mapping UML to Java code and back,
visibility markers, associations, inheritance, and interfaces.

UML is drawn here as **text boxes** (no images needed). Solutions under each **Solution**
heading. Java code checked by static review.

---

## UML quick reference

A class box has three parts:
```
+-------------------+
|    ClassName      |   <- name
+-------------------+
| - field: Type     |   <- attributes
+-------------------+
| + method(): Type  |   <- operations (methods)
+-------------------+
```

Visibility markers: `+` public, `-` private, `#` protected.

Relationships:
- Inheritance (extends): a solid line with a hollow triangle pointing to the parent.
- Interface realization (implements): a dashed line with a hollow triangle to the interface.
- Association (has-a): a plain line, often with multiplicity like `1`, `*`, `0..1`.

---

## Exercise 1 — Read a UML box and write the class

Convert this UML to Java:
```
+---------------------------+
|          Person           |
+---------------------------+
| - name: String            |
| - age: int                |
+---------------------------+
| + getName(): String       |
| + getAge(): int           |
+---------------------------+
```

### Solution
```java
public class Person {
    private String name;
    private int age;

    public String getName() { return name; }
    public int getAge()     { return age; }
}
```
`-` maps to `private`, `+` to `public`. The `: Type` after a name is its type / return type.

---

## Exercise 2 — Write UML from code

Draw the UML class box for:
```java
public class Rectangle {
    private double width;
    private double height;
    public double area() { return width * height; }
}
```

### Solution
```
+---------------------------+
|        Rectangle          |
+---------------------------+
| - width: double           |
| - height: double          |
+---------------------------+
| + area(): double          |
+---------------------------+
```

---

## Exercise 3 — Inheritance

Draw the UML for `Dog extends Animal`, where `Animal` has `+ sound(): String` and `Dog`
overrides it.

### Solution
```
+------------------+
|     Animal       |
+------------------+
| + sound(): String|
+------------------+
        ^
        | (extends: hollow triangle to parent)
+------------------+
|      Dog         |
+------------------+
| + sound(): String|
+------------------+
```
Code:
```java
class Animal { public String sound() { return "..."; } }
class Dog extends Animal {
    @Override public String sound() { return "Woof"; }
}
```

---

## Exercise 4 — Interface realization

Draw UML for `Guitar` implementing interface `Playable` with `+ play(): void`.

### Solution
```
+------------------+
| <<interface>>    |
|    Playable      |
+------------------+
| + play(): void   |
+------------------+
        ^
        ¦ (realization: dashed line, hollow triangle)
+------------------+
|     Guitar       |
+------------------+
| + play(): void   |
+------------------+
```
Code:
```java
interface Playable { void play(); }
class Guitar implements Playable {
    @Override public void play() { System.out.println("Strum"); }
}
```

---

## Exercise 5 — Association with multiplicity

An `Order` has many `OrderLine`s (one-to-many). Draw the association and write the field.

### Solution
```
+-----------+  1        *  +--------------+
|   Order   |--------------|  OrderLine   |
+-----------+              +--------------+
```
`1` on the `Order` side, `*` (many) on the `OrderLine` side.
Code (on the `Order` side):
```java
class Order {
    private List<OrderLine> lines = new ArrayList<>();
}
```

---

## Exercise 6 — Multiplicity meaning (theory)

What do these multiplicities mean: `1`, `0..1`, `*`, `1..*` ?

### Solution
- `1` — exactly one.
- `0..1` — zero or one (optional).
- `*` — zero or more (many).
- `1..*` — one or more (at least one).

---

## Exercise 7 — Fix the bug (UML does not match code)

The UML says `balance` is private, but the code exposes it as public. Make the code match the
UML.
```
+-----------------------+
|       Account         |
+-----------------------+
| - balance: double     |
+-----------------------+
| + getBalance(): double|
+-----------------------+
```
```java
public class Account {
    public double balance;              // does not match "- balance"
    public double getBalance() { return balance; }
}
```

### Solution
The `-` marker means `private`. Match the diagram:
```java
public class Account {
    private double balance;
    public double getBalance() { return balance; }
}
```

---

## Exercise 8 — Fix the bug (wrong relationship arrow)

A student drew `implements` as a solid line with a hollow triangle (that is the `extends`
notation). The code below uses an interface. Describe the correct UML.
```java
interface Drawable { void draw(); }
class Circle implements Drawable {
    @Override public void draw() { }
}
```

### Solution
Interface **realization** (`implements`) uses a **dashed** line with a hollow triangle pointing
to the interface — not a solid line. A solid line with a hollow triangle means class
inheritance (`extends`). The interface box should also carry the `<<interface>>` stereotype.

---

## Challenge — Model a small domain

Draw UML (as text boxes) for a `Library` that has many `Book`s, where `Book` has `title` and
`isbn`, and an interface `Borrowable` with `+ borrow(): void` that `Book` implements. Then
sketch the Java skeleton.

### Solution (example)
```
+------------------+ 1     * +------------------+     +--------------------+
|     Library      |--------|       Book       |----¦> |   <<interface>>    |
+------------------+        +------------------+       |    Borrowable      |
| - name: String   |        | - title: String  |       +--------------------+
+------------------+        | - isbn: String   |       | + borrow(): void   |
| + addBook(Book)  |        +------------------+       +--------------------+
+------------------+        | + borrow(): void |
                           +------------------+
```
```java
interface Borrowable { void borrow(); }

class Book implements Borrowable {
    private String title;
    private String isbn;
    @Override public void borrow() { /* ... */ }
}

class Library {
    private String name;
    private List<Book> books = new ArrayList<>();
    public void addBook(Book book) { books.add(book); }
}
```
