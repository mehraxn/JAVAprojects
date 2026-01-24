# Java Stream Intermediate Operations (Quick README)

This README explains common **intermediate operations** in Java Streams.
Intermediate operations **return a new stream**, so you can chain them. They do **not** run until you call a **terminal operation** like `toList()`, `count()`, `forEach()`, etc.

---

## 1) `filter(Predicate<T>)`

**What it does:** keeps only elements that match a condition.

**Example:** keep only even numbers.

````java
List<Integer> evens = List.of(1,2,3,4,5,6).stream()
    .filter(n -> n % 2 == 0)
    .toList();
// result: [2, 4, 6]


---

## 2) `limit(long n)`
**What it does:** takes only the **first `n`** elements of the stream.

**Example:** take the first 3 numbers.
```java
List<Integer> first3 = List.of(10,20,30,40,50).stream()
    .limit(3)
    .toList();
// result: [10, 20, 30]
````

---

## 3) `skip(long n)`

**What it does:** skips the **first `n`** elements.

**Example:** skip the first 2 numbers.

```java
List<Integer> after2 = List.of(10,20,30,40,50).stream()
    .skip(2)
    .toList();
// result: [30, 40, 50]
```

---

## 4) `sorted()` / `sorted(Comparator<T>)`

**What it does:** sorts the elements.

Below are the **custom comparator methods** (as real methods), and then how to use them.

### Comparator methods

```java
import java.util.Comparator;

// 4.a) Natural order comparator (same behavior as sorted())
static Comparator<Integer> naturalOrderComparator() {
    return (a, b) -> a.compareTo(b);
    // equivalent: return Comparator.naturalOrder();
}

// 4.b) Reverse order comparator
static Comparator<Integer> reverseOrderComparator() {
    return (a, b) -> b.compareTo(a);
    // equivalent: return Comparator.reverseOrder();
}

// 4.c) String length descending comparator
static Comparator<String> lengthDescComparator() {
    return (s1, s2) -> Integer.compare(s2.length(), s1.length());
}
```

### 4.a) Natural order

```java
List<Integer> asc = List.of(4,1,3,2).stream()
    .sorted(naturalOrderComparator())
    .toList();
// result: [1, 2, 3, 4]
```

### 4.b) Reversed order (requested)

```java
List<Integer> desc = List.of(4,1,3,2).stream()
    .sorted(reverseOrderComparator())
    .toList();
// result: [4, 3, 2, 1]
```

### 4.c) Custom sort + reversed (by string length descending)

```java
List<String> byLengthDesc = List.of("bbb", "a", "cc").stream()
    .sorted(lengthDescComparator())
    .toList();
// result: ["bbb", "cc", "a"]
```

---

## 5) `distinct()`

**What it does:** removes duplicates (it uses `equals()` and `hashCode()` of the elements).

**Example:** remove repeated integers.

```java
List<Integer> unique = List.of(1,2,2,3,3,3).stream()
    .distinct()
    .toList();
// result: [1, 2, 3]
```

---

## 6) `map(Function<T, R>)`

**What it does:** transforms each element from type `T` into type `R`.

**Example:** convert strings into their lengths.

```java
List<Integer> lengths = List.of("cat", "house", "hi").stream()
    .map(String::length)
    .toList();
// result: [3, 5, 2]
```

---

## One small pipeline using several operations

This is just to show how chaining looks:

```java
List<Integer> result = List.of(5, 1, 2, 2, 9, 3, 4, 4).stream()
    .distinct()               // remove duplicates
    .filter(n -> n > 2)       // keep numbers > 2
    .sorted(Comparator.reverseOrder()) // sort descending
    .skip(1)                  // skip the first (largest)
    .limit(3)                 // take next 3
    .toList();
// possible result: [5, 4, 3]
```

---

### Notes

* `filter`, `map`, `sorted`, `distinct`, `limit`, `skip` are **intermediate**.
* A stream runs only when you add a **terminal** operation like `toList()`.
