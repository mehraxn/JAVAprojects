# Generic Interfaces and `Comparable<T>`

## Simple Explanation

A **generic interface** is an interface that has a type parameter, written as `<T>`.
It lets one interface work with many different types while staying **type-safe**.

```java
interface Printer<T> {
    void print(T item);
}
```

Here `T` is a placeholder for a type. When a class implements the interface it decides
what `T` really is: `Printer<String>`, `Printer<Integer>`, and so on.

**`Comparable<T>`** is the most important generic interface for beginners. A class
implements `Comparable<T>` to say: *"objects of my type have a natural order, and here
is how to compare two of them."* It has a single method:

```java
public interface Comparable<T> {
    int compareTo(T other);
}
```

`compareTo` returns:

| Return value | Meaning |
|---|---|
| negative number | `this` comes **before** `other` |
| `0` | `this` and `other` are considered **equal in order** |
| positive number | `this` comes **after** `other` |

---

## Why It Matters

- Generic interfaces remove casting and prevent runtime type errors.
- `Comparable<T>` is what makes `Collections.sort(list)`, `list.sort(null)`,
  `TreeSet`, and `TreeMap` able to order your objects automatically.
- Almost every exam about generics or collections includes a "sort these objects"
  question that needs `Comparable`.

The two `.java` files in this folder (`1)Example.java` with `Printer<T>` and
`2)Example.java` with `Repository<T>`) show generic interfaces implemented for
specific types and for a fully generic class. Read them alongside these notes.

---

## Basic Example

```java
public class Student implements Comparable<Student> {
    private String name;
    private int grade;

    public Student(String name, int grade) {
        this.name = name;
        this.grade = grade;
    }

    public String getName()  { return name; }
    public int getGrade()    { return grade; }

    // Natural order: by grade, lowest first
    @Override
    public int compareTo(Student other) {
        return Integer.compare(this.grade, other.grade);
    }

    @Override
    public String toString() {
        return name + " (" + grade + ")";
    }
}
```

Using it:

```java
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Student> students = new ArrayList<>();
        students.add(new Student("Sara", 85));
        students.add(new Student("Tom", 72));
        students.add(new Student("Ali", 90));

        Collections.sort(students); // uses compareTo

        System.out.println(students);
        // [Tom (72), Sara (85), Ali (90)]
    }
}
```

---

## Step-by-Step Explanation

1. **`class Student implements Comparable<Student>`**
   The `<Student>` tells Java: "I will be compared with other `Student` objects."
   This is why the interface is *generic* — you fill in the type you want to compare against.

2. **`public int compareTo(Student other)`**
   Because `T` is `Student`, the parameter is already a `Student`.
   **No casting is needed.** That is the whole benefit of the generic version.

3. **`Integer.compare(this.grade, other.grade)`**
   A safe helper that returns negative / zero / positive for you.
   Avoid writing `this.grade - other.grade` (see Common Mistakes).

4. **`Collections.sort(students)`**
   `sort` only accepts a list whose elements are `Comparable`. It repeatedly calls
   `compareTo` to decide the order. Because `Student` implements `Comparable<Student>`,
   this just works.

### Why `Comparable` is generic

Before generics (Java 4 and earlier), the interface looked like this:

```java
public interface Comparable {
    int compareTo(Object other); // no type parameter
}
```

That forced you to **cast** inside every `compareTo`:

```java
public int compareTo(Object other) {
    Student s = (Student) other;      // unsafe cast, can throw ClassCastException
    return Integer.compare(this.grade, s.grade);
}
```

Making it `Comparable<T>` moved the type check to **compile time**. Now the compiler
guarantees you only compare a `Student` with a `Student`.

### `Comparable<T>` vs raw `Comparable`

| | `Comparable<Student>` (generic) | `Comparable` (raw / old style) |
|---|---|---|
| Method signature | `compareTo(Student other)` | `compareTo(Object other)` |
| Casting needed? | No | Yes: `(Student) other` |
| Error caught at | Compile time | Runtime (`ClassCastException`) |
| Recommended? | ✅ Yes | ❌ No, only legacy code |

---

## Common Mistakes

1. **Using subtraction for comparison**
   ```java
   return this.grade - other.grade; // can overflow for large ints
   ```
   Prefer `Integer.compare(this.grade, other.grade)`.

2. **Using raw `Comparable`** and then casting `Object` — this compiles but risks a
   `ClassCastException` at runtime. Always write `Comparable<YourType>`.

3. **Returning `true`/`false` or only `0` and `1`.** `compareTo` must be able to return
   a **negative** value too, otherwise sorting is wrong.

4. **Making `compareTo` inconsistent with `equals`.** For `TreeSet`/`TreeMap`, if
   `compareTo` returns `0`, the elements are treated as duplicates even if `equals`
   says they are different. Keep them consistent.

5. **Forgetting `@Override`.** Not fatal, but it lets the compiler catch a wrong
   signature such as `compareTo(Object other)` when you meant `compareTo(Student other)`.

---

## Exam Notes

- `Comparable<T>` has exactly **one** method: `int compareTo(T other)`.
- Natural ordering lives **inside** the class (that is `Comparable`). An external,
  custom ordering uses `Comparator` (a different interface — see the Collections and
  Comparator notes).
- `compareTo` returns **negative / zero / positive**, not a boolean.
- A generic interface can be implemented for a fixed type (`implements Printer<String>`)
  or kept generic in the implementing class (`class Box<T> implements Printer<T>`).
- Raw types (`Comparable` without `<...>`) compile with a warning and require casts.

---

## Practice Questions

1. Write the class header for a `Book` class whose objects can be compared with other
   `Book` objects using `Comparable`. (Answer: `class Book implements Comparable<Book>`.)

2. What are the three kinds of values `compareTo` can return, and what does each mean?

3. Rewrite this comparison safely: `return this.price - other.price;`
   (Answer: `return Integer.compare(this.price, other.price);` or `Double.compare` for doubles.)

4. True or false: with `Comparable<Student>` you must cast the parameter inside
   `compareTo`. Explain your answer.

5. Given the `Student` example above, change `compareTo` so students are ordered by
   **name** alphabetically instead of by grade. (Hint: `String` already implements
   `Comparable`, so you can call `this.name.compareTo(other.name)`.)

6. Look at `1)Example.java` in this folder. What concrete type does `StringPrinter`
   choose for `T`, and how would you declare a `Printer` that prints `Double` values?

7. Why was `Comparable` changed from `compareTo(Object)` to `compareTo(T)`? Give one
   concrete benefit.
