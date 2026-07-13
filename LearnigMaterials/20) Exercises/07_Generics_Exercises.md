# 07 — Generics Exercises

Topics: generic classes, generic methods, bounded types, wildcards, PECS. Solutions under each
**Solution** heading. Code checked by static review.

---

## Exercise 1 — Generic box

Create a generic class `Box<T>` that stores one value with `set`/`get`. Store a `String` and
print it.

**Expected output:**
```
hello
```

### Solution
```java
class Box<T> {
    private T value;
    public void set(T value) { this.value = value; }
    public T get() { return value; }
}

Box<String> b = new Box<>();
b.set("hello");
System.out.println(b.get()); // hello
```

---

## Exercise 2 — Generic method

Write a generic method `<T> T firstOf(List<T> list)` returning the first element.

**Expected output** (for `List.of("x", "y")`):
```
x
```

### Solution
```java
static <T> T firstOf(List<T> list) {
    return list.get(0);
}
// firstOf(List.of("x", "y")) -> "x"
```

---

## Exercise 3 — Bounded type parameter

Write `<T extends Number> double sum(List<T> list)` that sums any list of numbers.

**Expected output** (for `List.of(1, 2, 3)`):
```
6.0
```

### Solution
```java
static <T extends Number> double sum(List<T> list) {
    double total = 0;
    for (T n : list) {
        total += n.doubleValue();
    }
    return total;
}
// sum(List.of(1, 2, 3)) -> 6.0
```

---

## Exercise 4 — Producer with `? extends`

Write `double total(List<? extends Number> list)` and call it with a `List<Integer>` and a
`List<Double>`.

### Solution
```java
static double total(List<? extends Number> list) {
    double t = 0;
    for (Number n : list) t += n.doubleValue();
    return t;
}
// total(List.of(1, 2))     -> 3.0
// total(List.of(1.5, 2.5)) -> 4.0
```
`? extends Number` lets the method **read** numbers from lists of any subtype.

---

## Exercise 5 — Consumer with `? super`

Write `void addInts(List<? super Integer> list)` that adds `1` and `2` to the list.

### Solution
```java
static void addInts(List<? super Integer> list) {
    list.add(1);
    list.add(2);
}
// works with List<Integer>, List<Number>, List<Object>
```
`? super Integer` lets the method **add** `Integer`s (PECS: Consumer Super).

---

## Exercise 6 — Predict the output

```java
class Box<T> {
    private T value;
    Box(T value) { this.value = value; }
    T get() { return value; }
}

public class Main {
    public static void main(String[] args) {
        Box<Integer> b = new Box<>(42);
        Integer x = b.get();
        System.out.println(x + 1);
    }
}
```

### Solution
```
43
```
`T` is `Integer`, so `get()` returns an `Integer` with no cast; `x + 1` unboxes to 43.

---

## Exercise 7 — Predict the output (will it compile?)

```java
List<Integer> ints = new ArrayList<>();
List<Number> nums = ints;
```

### Solution
It does **not** compile. Generics are invariant: `List<Integer>` is not a `List<Number>`.
(If it compiled, you could add a `Double` into an `Integer` list.)

---

## Exercise 8 — Fix the bug (adding through `? extends`)

This does not compile. Explain and fix.
```java
static void addOne(List<? extends Number> list) {
    list.add(1); // error
}
```

### Solution
With `? extends Number` you can only **read**, not add (the real type is unknown — it might be
`List<Double>`). If you need to add `Integer`s, use `? super Integer`:
```java
static void addOne(List<? super Integer> list) {
    list.add(1);
}
```

---

## Exercise 9 — Fix the bug (raw type warning / unsafe)

This compiles with a warning and risks a `ClassCastException`. Make it type-safe.
```java
List box = new ArrayList();
box.add("text");
Integer n = (Integer) box.get(0); // ClassCastException at runtime
```

### Solution
Use a typed list so the compiler catches the mistake:
```java
List<String> box = new ArrayList<>();
box.add("text");
String s = box.get(0); // no cast, no runtime surprise
```

---

## Challenge — Generic `Pair<A, B>`

Create a generic `Pair<A, B>` with two fields and a `toString`. Create a `Pair<String, Integer>`
and print it.

**Expected output:**
```
(age, 30)
```

### Solution
```java
class Pair<A, B> {
    private final A first;
    private final B second;
    Pair(A first, B second) { this.first = first; this.second = second; }
    A getFirst()  { return first; }
    B getSecond() { return second; }
    @Override public String toString() { return "(" + first + ", " + second + ")"; }
}

Pair<String, Integer> p = new Pair<>("age", 30);
System.out.println(p); // (age, 30)
```
