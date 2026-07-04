# `equals`, `hashCode`, and `toString` - Complete Guide

## Overview
Every Java class inherits methods from `Object`. Three of the most important are:

- `equals`
- `hashCode`
- `toString`

They are especially important with collections like `HashSet` and `HashMap`.

---

## 1. Default behavior from `Object`

If you do not override anything:

```java
Student a = new Student("Sara", 22);
Student b = new Student("Sara", 22);

System.out.println(a.equals(b)); // false by default
```

Default `equals` checks object identity, like `==`.

---

## 2. Why override `equals`?

Usually you want two objects to be equal if their important fields are equal.

```java
public class Student {
    private String id;
    private String name;

    public Student(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;

        Student other = (Student) obj;
        return id.equals(other.id);
    }
}
```

Here, students are considered equal if they have the same `id`.

---

## 3. Why override `hashCode`?

Rule:

```text
If two objects are equal according to equals(), they must have the same hashCode().
```

Correct implementation:

```java
@Override
public int hashCode() {
    return id.hashCode();
}
```

Better using `Objects`:

```java
import java.util.Objects;

@Override
public int hashCode() {
    return Objects.hash(id);
}
```

---

## 4. Why it matters in `HashSet`

```java
Set<Student> students = new HashSet<>();
students.add(new Student("S1", "Sara"));
students.add(new Student("S1", "Sara Again"));

System.out.println(students.size());
```

If `equals` and `hashCode` are correctly implemented, output is:

```text
1
```

If not, Java may treat them as different objects.

---

## 5. `toString`

`toString` controls how an object is converted to text.

```java
@Override
public String toString() {
    return "Student{id='" + id + "', name='" + name + "'}";
}
```

Use:

```java
System.out.println(student);
```

Output:

```text
Student{id='S1', name='Sara'}
```

---

## 6. Complete example

```java
import java.util.Objects;

public class Student {
    private final String id;
    private final String name;

    public Student(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Student other = (Student) obj;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Student{id='" + id + "', name='" + name + "'}";
    }
}
```

---

## Common mistakes

### Mistake 1: overriding `equals` but not `hashCode`
This breaks hash-based collections.

### Mistake 2: using mutable fields in hashCode
If the field changes after inserting into `HashSet`, lookup may fail.

### Mistake 3: using `==` for strings inside equals
Use `Objects.equals(a, b)` or `a.equals(b)`.

---

## Mini quiz

### Q1. If `a.equals(b)` is true, what must be true about hash codes?
Answer: `a.hashCode() == b.hashCode()`.

### Q2. Does `toString` affect equality?
Answer: no.

### Q3. Why does `HashSet` need `hashCode`?
Answer: to place and find objects efficiently in hash buckets.
