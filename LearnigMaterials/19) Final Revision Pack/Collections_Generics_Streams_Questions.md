# Collections, Generics, and Streams Questions (with Answers)

Covers collections, generics & PECS, `Comparable` vs `Comparator`, lambdas, streams, and
`Optional`. Mix of theory, multiple choice, and "predict the output". Answers follow each
question. Code checked by static review.

---

## 1. Collections

**Q1.** Match the interface to its rule: `List`, `Set`, `Map`, `Queue`.
**Answer:** `List` = ordered, allows duplicates. `Set` = no duplicates. `Map` = unique keys →
values. `Queue` = FIFO ordering.

**Q2 (MCQ).** Which keeps keys **sorted**?
a. `HashMap`  b. `LinkedHashMap`  c. `TreeMap`  d. `Hashtable`
**Answer:** c. `TreeMap` (sorted by key). `LinkedHashMap` keeps **insertion** order, not sorted.

**Q3.** What two methods must your class implement correctly to be a good `HashSet`/`HashMap`
key?
**Answer:** `equals` and `hashCode` (consistently with each other).

**Q4 (predict).**
```java
Set<String> set = new HashSet<>();
set.add("a");
set.add("a");
set.add("b");
System.out.println(set.size());
```
**Answer:** `2`. A `Set` ignores the duplicate `"a"`.

**Q5.** When should you use `ArrayList` vs an array?
**Answer:** Use an **array** for a fixed size and slightly less overhead; use `ArrayList` when
the size changes at runtime (it grows automatically).

---

## 2. Generics and PECS

**Q6.** Why does `List<Integer>` not compile as a `List<Number>`?
**Answer:** Generics are **invariant**. If it were allowed, you could add a `Double` into a
list meant for `Integer`, breaking type safety.

**Q7.** State the PECS rule and what each side allows.
**Answer:** **Producer Extends, Consumer Super.**
- `? extends T`: you can **read** `T` (it produces values), but cannot add (except `null`).
- `? super T`: you can **add** `T` (it consumes values), but reads only give `Object`.

**Q8 (MCQ).** Which parameter lets a method **read** `Number`s from any numeric list?
a. `List<Number>`  b. `List<? extends Number>`  c. `List<? super Number>`  d. `List<Object>`
**Answer:** b. `List<? extends Number>`.

**Q9.** Write a generic method that returns the first element of any list.
**Answer:**
```java
public static <T> T first(List<T> list) {
    return list.get(0);
}
```

**Q10 (predict — will it compile?).**
```java
List<? extends Number> nums = new ArrayList<Integer>();
nums.add(5);
```
**Answer:** Does **not** compile. With `? extends Number` you cannot add elements (only read).

---

## 3. Comparable vs Comparator

**Q11.** State the method and typical use of each.
**Answer:** `Comparable<T>` → `int compareTo(T o)`, the class's **natural** order, used by
`Collections.sort(list)`. `Comparator<T>` → `int compare(T a, T b)`, an **external/custom**
order, passed to `list.sort(comparator)`.

**Q12.** What must `compareTo`/`compare` return?
**Answer:** A **negative** number, **zero**, or a **positive** number (before / equal / after).
Not a boolean.

**Q13 (predict).**
```java
List<String> names = new ArrayList<>(List.of("Tom", "ali", "Bob"));
names.sort(Comparator.comparing(String::length).thenComparing(Comparator.naturalOrder()));
System.out.println(names);
```
**Answer:** `[Bob, Tom, ali]`.
**Why:** All have length 3, so `thenComparing` natural order applies. Natural `String` order is
by Unicode: uppercase letters (`B`=66, `T`=84) come before lowercase (`a`=97), so `Bob` < `Tom` < `ali`.

**Q14.** Give a one-line comparator that sorts `Student` by grade descending.
**Answer:**
```java
students.sort(Comparator.comparingInt(Student::getGrade).reversed());
```

---

## 4. Lambdas

**Q15.** What is a functional interface?
**Answer:** An interface with exactly **one abstract method**; a lambda can implement it.

**Q16.** Match method to interface: `test`, `apply`, `accept`, `get`.
**Answer:** `test` → `Predicate`, `apply` → `Function`, `accept` → `Consumer`, `get` → `Supplier`.

**Q17 (predict).**
```java
Function<Integer, Integer> square = n -> n * n;
Function<Integer, Integer> plusOne = n -> n + 1;
System.out.println(square.andThen(plusOne).apply(3));
```
**Answer:** `10`. `andThen` runs `square` first (9), then `plusOne` (10).

**Q18.** Rewrite `s -> s.length()` as a method reference.
**Answer:** `String::length`.

---

## 5. Streams

**Q19.** What are the three parts of a stream pipeline?
**Answer:** A **source**, zero or more **intermediate operations** (lazy), and one **terminal
operation** that triggers execution.

**Q20 (MCQ).** Which is an intermediate operation?
a. `count`  b. `collect`  c. `map`  d. `forEach`
**Answer:** c. `map`.

**Q21 (predict).**
```java
List<Integer> nums = List.of(1, 2, 3, 4, 5, 6);
int sum = nums.stream()
              .filter(n -> n % 2 == 0)
              .mapToInt(Integer::intValue)
              .sum();
System.out.println(sum);
```
**Answer:** `12` (2 + 4 + 6).

**Q22 (predict).**
```java
List<String> words = List.of("apple", "banana", "avocado", "cherry");
Map<Character, Long> byFirst = words.stream()
    .collect(Collectors.groupingBy(w -> w.charAt(0), Collectors.counting()));
System.out.println(byFirst.get('a'));
```
**Answer:** `2` ("apple", "avocado").

**Q23.** Why does this crash, and how do you fix it?
```java
Map<Integer, String> m = Stream.of("aa", "bb", "cc")
    .collect(Collectors.toMap(String::length, s -> s));
```
**Answer:** All three strings have length 2, so `toMap` sees **duplicate keys** and throws
`IllegalStateException`. Fix with a merge function, e.g.
`Collectors.toMap(String::length, s -> s, (a, b) -> a)`.

**Q24.** What happens if you reuse a stream after a terminal operation?
**Answer:** `IllegalStateException` — streams are single-use; create a new one from the source.

---

## 6. Optional

**Q25.** Why does `Optional` exist?
**Answer:** To represent "a value or nothing" explicitly, reducing `NullPointerException`s and
forcing callers to handle the empty case.

**Q26 (MCQ).** Which safely returns a fallback when empty?
a. `get()`  b. `orElse("x")`  c. `isPresent()`  d. `of("x")`
**Answer:** b. `orElse("x")`.

**Q27 (predict).**
```java
Optional<String> o = Optional.ofNullable(null);
System.out.println(o.map(String::toUpperCase).orElse("EMPTY"));
```
**Answer:** `EMPTY`. `map` on an empty Optional stays empty, so `orElse` supplies the fallback.

**Q28.** What is the difference between `Optional.of(x)` and `Optional.ofNullable(x)`?
**Answer:** `of(x)` throws `NullPointerException` if `x` is null; `ofNullable(x)` returns an
empty Optional when `x` is null.

---

## Self-check checklist

- [ ] I can pick the right collection for a scenario.
- [ ] I can explain PECS with a read example and a write example.
- [ ] I know `compareTo`/`compare` return negative/zero/positive.
- [ ] I can name the 4 core functional interfaces and their methods.
- [ ] I can trace a filter/map/collect pipeline.
- [ ] I know the `toMap` duplicate-key trap and its fix.
- [ ] I use `orElse`/`ifPresent`, not `get()`, on Optionals.
