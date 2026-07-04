# Java Generics Wildcards and PECS - Complete Guide

## Overview
Wildcards make generic code more flexible. They are written using `?`. The most important rule is **PECS**:

```text
Producer Extends, Consumer Super
```

---

## 1. The problem without wildcards

Suppose we have:

```java
class Animal { }
class Dog extends Animal { }
```

You might expect this to work:

```java
List<Dog> dogs = new ArrayList<>();
List<Animal> animals = dogs; // compile error
```

But it does not. Why?

Because if Java allowed it, this would become possible:

```java
animals.add(new Cat());
```

Then a list of dogs would contain a cat. So Java prevents it.

---

## 2. Unbounded wildcard `?`

```java
public static void printAll(List<?> list) {
    for (Object item : list) {
        System.out.println(item);
    }
}
```

This method accepts any list:

```java
printAll(List.of("a", "b"));
printAll(List.of(1, 2, 3));
```

But you cannot safely add values except `null`:

```java
List<?> list = new ArrayList<String>();
// list.add("hello"); // compile error
```

---

## 3. Upper bounded wildcard `? extends T`

Use when the list **produces** values for you to read.

```java
public static double totalWeight(List<? extends Animal> animals) {
    double total = 0;
    for (Animal a : animals) {
        total += a.getWeight();
    }
    return total;
}
```

It accepts:

```java
List<Animal>
List<Dog>
List<Cat>
```

But you cannot add a new `Animal` safely:

```java
List<? extends Animal> animals = new ArrayList<Dog>();
// animals.add(new Animal()); // compile error
```

Reason: the actual list might be `List<Dog>`, so adding a generic `Animal` could be wrong.

---

## 4. Lower bounded wildcard `? super T`

Use when the list **consumes** values that you add.

```java
public static void addDogs(List<? super Dog> list) {
    list.add(new Dog());
}
```

It accepts:

```java
List<Dog>
List<Animal>
List<Object>
```

When reading from it, the safe type is only `Object`:

```java
Object item = list.get(0);
```

---

## 5. PECS rule

### Producer Extends
If a structure gives/produces values for you, use `extends`.

```java
void printAnimals(List<? extends Animal> animals)
```

### Consumer Super
If a structure receives/consumes values from you, use `super`.

```java
void addDogs(List<? super Dog> dogs)
```

---

## 6. Complete copy example

```java
public static <T> void copy(List<? extends T> source, List<? super T> destination) {
    for (T item : source) {
        destination.add(item);
    }
}
```

Explanation:

- source produces `T` values → `extends`
- destination consumes `T` values → `super`

---

## 7. Wildcards vs type parameters

Use wildcard when you do not need to connect multiple types.

```java
void print(List<?> list)
```

Use type parameter when the same type relationship matters:

```java
<T> T first(List<T> list) {
    return list.get(0);
}
```

---

## 8. Why `List<Integer>` is not a `List<Number>`

`Integer` **is a** `Number`, so beginners expect `List<Integer>` to be a `List<Number>`.
It is not, and here is the concrete reason:

```java
List<Integer> ints = new ArrayList<>();
List<Number> nums = ints;   // COMPILE ERROR (this line is not allowed)

// If the line above were allowed, this would follow:
nums.add(3.14);             // adds a Double into a List<Integer>!
int x = ints.get(0);        // boom — a Double is not an int
```

Java blocks it at the assignment to keep the list type-safe. Generics are **invariant**:
`List<Integer>` and `List<Number>` are unrelated types even though `Integer` extends `Number`.

To accept "a list of Number or any subtype and read from it", use a wildcard:

```java
static double sum(List<? extends Number> list) {   // producer -> extends
    double total = 0;
    for (Number n : list) {
        total += n.doubleValue();
    }
    return total;
}

sum(List.of(1, 2, 3));       // List<Integer> — OK
sum(List.of(1.5, 2.5));      // List<Double>  — OK
```

`List<? extends Number>` is the flexible parameter type; `List<Number>` is not.

---

## 9. Generic methods: `<T> T pickFirst(List<T> list)`

A **generic method** declares its own type parameter in `< >` **before** the return type.
This links the input and output types so no casting is needed.

```java
public static <T> T pickFirst(List<T> list) {
    return list.get(0);   // returns the list's real element type
}
```

Usage — the compiler infers `T` from the argument:

```java
List<String> names = List.of("Sara", "Tom");
String first = pickFirst(names);   // T = String, no cast needed

List<Integer> nums = List.of(10, 20, 30);
int n = pickFirst(nums);           // T = Integer
```

Compare the pieces:

- `<T>` (before `T pickFirst`) **declares** the type parameter.
- `T` (return type) uses it, so the method returns the exact element type.
- Use a **generic method** when input and output types must match; use a **wildcard**
  when you only pass the collection through and do not need that link.

A producer-style version that also accepts subtypes:

```java
public static <T> T pickFirst(List<? extends T> list) {
    return list.get(0);
}
```

---

## Common mistakes

### Mistake 1: expecting `List<Dog>` to be a subtype of `List<Animal>`
It is not.

### Mistake 2: trying to add to `List<? extends Animal>`
You cannot safely add except `null`.

### Mistake 3: forgetting PECS
Producer Extends, Consumer Super.

---

## Exam Notes

- **PECS**: **P**roducer **E**xtends, **C**onsumer **S**uper.
- `? extends T` — you can **read** `T` from it, but you **cannot add** (except `null`).
- `? super T` — you can **add** `T` to it, but reads only give you `Object`.
- Generics are **invariant**: `List<Integer>` is **not** a `List<Number>`.
- A generic method declares `<T>` **before** the return type: `static <T> T pickFirst(List<T> l)`.
- Use a **wildcard** to pass a collection through; use a **type parameter** when input and
  output types must be linked.

---

## Mini quiz

### Q1. Which wildcard should you use to read animals from a list?
Answer: `? extends Animal`.

### Q2. Which wildcard should you use to add dogs to a list?
Answer: `? super Dog`.

### Q3. Is `List<Integer>` a subtype of `List<Number>`?
Answer: no.

---

## More Practice Questions

1. Explain in one sentence why `List<Number> nums = ints;` (where `ints` is a
   `List<Integer>`) does not compile.

2. Write the header of a method `sum` that accepts any list of `Number` or its subtypes so
   it can be called with both `List<Integer>` and `List<Double>`.

3. In `<T> T pickFirst(List<T> list)`, what is the job of the `<T>` written before the
   return type?

4. You have `List<? extends Animal> animals`. Can you call `animals.add(new Dog())`? Why or
   why not?

5. You have `List<? super Dog> list`. What is the only type you are guaranteed to get back
   when you read an element? (Answer: `Object`.)

6. Rewrite `<T> T pickFirst(List<T> list)` so it also accepts a list of any subtype of `T`.

7. According to PECS, which wildcard belongs on a parameter you only **read from**, and
   which on one you only **write to**?
