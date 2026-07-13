# 03 — Classes, Objects, and Constructors Exercises

Topics: class design, fields, methods, constructors, `this`, encapsulation (private fields +
getters/setters), `toString`. Solutions under each **Solution** heading. Outputs checked by
static review.

---

## Exercise 1 — Model a `Book`

Create a `Book` class with private fields `title` (String) and `pages` (int), a constructor,
getters, and a `toString`. Create one book and print it.

**Expected output:**
```
Book{title='Clean Code', pages=464}
```

### Solution
```java
public class Book {
    private String title;
    private int pages;

    public Book(String title, int pages) {
        this.title = title;
        this.pages = pages;
    }

    public String getTitle() { return title; }
    public int getPages()    { return pages; }

    @Override
    public String toString() {
        return "Book{title='" + title + "', pages=" + pages + "}";
    }

    public static void main(String[] args) {
        Book b = new Book("Clean Code", 464);
        System.out.println(b);
    }
}
```

---

## Exercise 2 — Encapsulation with validation

Add a private `int pages` setter that rejects values below 1 (keep the old value and print a
message). Show it rejecting `setPages(-5)`.

**Expected output** (starting from 464 pages):
```
Invalid page count: -5
464
```

### Solution
```java
public void setPages(int pages) {
    if (pages < 1) {
        System.out.println("Invalid page count: " + pages);
        return; // keep old value
    }
    this.pages = pages;
}
// b.setPages(-5); System.out.println(b.getPages()); -> 464
```

---

## Exercise 3 — Constructor overloading with `this(...)`

Give `Book` a second constructor that takes only a title and defaults pages to `100`, reusing
the main constructor.

### Solution
```java
public Book(String title) {
    this(title, 100); // calls the two-arg constructor
}
```
`this(...)` must be the **first statement** in the constructor.

---

## Exercise 4 — Object counter with `static`

Add a `static int count` that increases every time a `Book` is created. Print the count after
creating three books.

**Expected output:**
```
3
```

### Solution
```java
public class Book {
    private static int count = 0;
    // fields...
    public Book(String title, int pages) {
        this.title = title;
        this.pages = pages;
        count++;
    }
    public static int getCount() { return count; }
}
// after creating 3 books: Book.getCount() -> 3
```
`count` belongs to the class, shared by all instances.

---

## Exercise 5 — Predict the output (constructor order)

```java
class Animal {
    Animal() { System.out.println("Animal constructor"); }
}
class Dog extends Animal {
    Dog() { System.out.println("Dog constructor"); }
}

public class Main {
    public static void main(String[] args) {
        new Dog();
    }
}
```

### Solution
```
Animal constructor
Dog constructor
```
The parent constructor runs first (an implicit `super()` call is inserted before the `Dog`
constructor body).

---

## Exercise 6 — Fix the bug (shadowed field)

The constructor does not store the value; every book ends up with pages 0. Fix it.
```java
public class Book {
    private int pages;
    public Book(int pages) {
        pages = pages; // bug
    }
    public int getPages() { return pages; }
}
```

### Solution
The assignment writes the parameter to itself. Use `this` to reach the field:
```java
public Book(int pages) {
    this.pages = pages;
}
```

---

## Exercise 7 — Fix the bug (no no-arg constructor)

This does not compile. Explain why and fix it two different ways.
```java
class Account {
    Account(double balance) { }
}
class SavingsAccount extends Account {
    SavingsAccount() { }   // error here
}
```

### Solution
`Account` has no no-arg constructor, so the implicit `super()` in `SavingsAccount()` fails.
Fix either by calling the existing parent constructor:
```java
SavingsAccount() { super(0.0); }
```
or by adding a no-arg constructor to `Account`:
```java
Account() { }
```

---

## Challenge — `BankAccount`

Design `BankAccount` with a private `balance`, a `deposit(double)` and `withdraw(double)` that
rejects overdrafts, and a getter. Show a deposit of 100 then a withdraw of 30.

**Expected output:**
```
70.0
```

### Solution
```java
public class BankAccount {
    private double balance;

    public void deposit(double amount) {
        if (amount > 0) balance += amount;
    }
    public void withdraw(double amount) {
        if (amount > 0 && amount <= balance) balance -= amount;
        else System.out.println("Withdrawal rejected");
    }
    public double getBalance() { return balance; }

    public static void main(String[] args) {
        BankAccount acc = new BankAccount();
        acc.deposit(100);
        acc.withdraw(30);
        System.out.println(acc.getBalance()); // 70.0
    }
}
```
