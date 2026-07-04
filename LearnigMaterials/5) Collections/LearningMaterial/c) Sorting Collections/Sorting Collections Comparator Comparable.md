# Sorting Collections with Comparable and Comparator - Complete Guide

## Overview
Sorting is one of the most common operations in Java. You can sort using natural ordering with `Comparable` or custom ordering with `Comparator`.

---

## 1. Sorting simple values

```java
List<Integer> numbers = new ArrayList<>(List.of(5, 1, 3));
Collections.sort(numbers);
System.out.println(numbers); // [1, 3, 5]
```

Or:

```java
numbers.sort(null);
```

---

## 2. Comparable

Use `Comparable` when the class has a natural order.

```java
public class Student implements Comparable<Student> {
    private String name;
    private int age;

    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public int compareTo(Student other) {
        return this.name.compareTo(other.name);
    }

    @Override
    public String toString() {
        return name + " (" + age + ")";
    }
}
```

Use:

```java
List<Student> students = new ArrayList<>();
students.add(new Student("Sara", 22));
students.add(new Student("Ali", 24));

Collections.sort(students);
System.out.println(students);
```

---

## 3. Comparator

Use `Comparator` for external/custom sorting rules.

```java
students.sort(Comparator.comparing(Student::getAge));
```

Descending order:

```java
students.sort(Comparator.comparing(Student::getAge).reversed());
```

Multiple fields:

```java
students.sort(
    Comparator.comparing(Student::getLastName)
              .thenComparing(Student::getFirstName)
              .thenComparing(Student::getAge)
);
```

---

## 4. `comparingInt`, `comparingDouble`, `comparingLong`

For primitive values, prefer primitive comparator helpers:

```java
students.sort(Comparator.comparingInt(Student::getAge));
```

This avoids unnecessary boxing.

---

## 5. Sorting streams

```java
List<String> words = List.of("There", "must", "be", "some", "way");

List<String> sorted = words.stream()
    .sorted(Comparator.comparingInt(String::length).reversed())
    .toList();

System.out.println(sorted);
```

This sorts by decreasing word length.

---

## 6. Important type inference trap

This can sometimes confuse the compiler depending on imports/context:

```java
.sorted(comparing(s -> s.length()).reversed())
```

Safer:

```java
.sorted(Comparator.comparingInt(String::length).reversed())
```

Or use static import clearly:

```java
import static java.util.Comparator.comparingInt;
```

Then:

```java
.sorted(comparingInt(String::length).reversed())
```

---

## 7. Return values from compare methods

A comparison returns:

| Return | Meaning |
|---|---|
| negative | first object comes before second |
| zero | equal order |
| positive | first object comes after second |

Avoid subtraction for comparison if overflow is possible:

```java
return this.age - other.age; // not always safe
```

Better:

```java
return Integer.compare(this.age, other.age);
```

---

## Common mistakes

### Mistake 1: confusing Comparator and Comparable
`Comparable` is inside the class. `Comparator` is external.

### Mistake 2: returning boolean from compare
Compare methods return `int`, not boolean.

### Mistake 3: forgetting imports
Use `java.util.Comparator` and `java.util.Collections`.

---

## Mini quiz

### Q1. Which interface defines natural ordering?
Answer: `Comparable`.

### Q2. Which method sorts a list in-place with custom rule?
Answer: `list.sort(comparator)`.

### Q3. How to reverse a comparator?
Answer: call `.reversed()`.
