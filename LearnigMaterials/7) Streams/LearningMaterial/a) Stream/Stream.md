# Stream Introduction

## Simple Explanation

A **Stream** is a pipeline for processing a sequence of elements (for example, the items
in a `List`). You describe **what** you want to do with the data — filter it, transform it,
collect it — and the stream does the looping for you.

A stream is **not** a data structure. It does not store elements. It only *flows* data
from a **source** (like a list) through a series of **operations** to a final **result**.

```java
List<String> names = List.of("Sara", "Tom", "Jonathan");

List<String> longNames = names.stream()   // source
    .filter(n -> n.length() > 3)           // intermediate operation
    .toList();                             // terminal operation

System.out.println(longNames); // [Sara, Jonathan]
```

---

## Why It Matters

- Streams let you replace long `for` loops with short, readable pipelines.
- They are used everywhere in modern Java together with lambdas and collectors.
- Exams love asking about **lazy evaluation** and the **single-use** rule, which are the
  two ideas that surprise beginners the most.

---

## Basic Example

```java
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<String> words = List.of("apple", "banana", "avocado", "cherry");

        // Keep only words starting with "a", make them UPPERCASE, collect to a list
        List<String> result = words.stream()
            .filter(w -> w.startsWith("a"))   // intermediate
            .map(String::toUpperCase)          // intermediate
            .toList();                         // terminal

        System.out.println(result); // [APPLE, AVOCADO]
    }
}
```

The same logic with a classic loop is longer and easier to get wrong:

```java
List<String> result = new ArrayList<>();
for (String w : words) {
    if (w.startsWith("a")) {
        result.add(w.toUpperCase());
    }
}
```

---

## Step-by-Step Explanation

A stream pipeline always has three parts:

1. **Source** — where the elements come from.
   ```java
   words.stream()          // from a collection
   Stream.of("a", "b")     // from fixed values
   Arrays.stream(array)    // from an array
   ```

2. **Intermediate operations** — transform or filter the stream and return **another
   stream**, so they can be chained. Examples: `filter`, `map`, `sorted`, `distinct`, `limit`.
   These are **lazy** (see below).

3. **Terminal operation** — produces a result or a side effect and **ends** the pipeline.
   Examples: `toList()`, `collect(...)`, `count()`, `forEach(...)`, `sum()`.
   After a terminal operation runs, the stream is **used up**.

### Lazy evaluation

Intermediate operations do **nothing** until a terminal operation is called.

```java
List<String> words = List.of("apple", "banana", "cherry");

words.stream()
     .filter(w -> {
         System.out.println("checking " + w);
         return true;
     });
// Prints NOTHING — there is no terminal operation, so filter never runs.
```

Add a terminal operation and the pipeline finally executes:

```java
words.stream()
     .filter(w -> {
         System.out.println("checking " + w);
         return true;
     })
     .toList(); // now it prints: checking apple / checking banana / checking cherry
```

Laziness lets streams be efficient — for example, `limit(2)` can stop early instead of
processing the whole source.

### Stream vs Collection

| | Collection (`List`, `Set`, ...) | Stream |
|---|---|---|
| Stores data? | Yes | No — it flows data |
| Reusable? | Yes, iterate as often as you like | No — single use |
| When work happens | Immediately | Lazily, on the terminal operation |
| Main purpose | Store and access elements | Process/transform elements |

### One-use nature of streams

A stream can be traversed **only once**. Calling a second terminal operation throws
`IllegalStateException`.

```java
Stream<String> s = List.of("a", "b").stream();
s.count();  // OK, uses the stream
s.count();  // ERROR: IllegalStateException: stream has already been operated upon or closed
```

If you need the pipeline again, create a **new** stream from the source:

```java
List<String> data = List.of("a", "b");
data.stream().count();
data.stream().count(); // fine — a fresh stream each time
```

---

## Common Mistakes

1. **Expecting output with no terminal operation.** Intermediate operations are lazy;
   nothing runs until a terminal operation is added.

2. **Reusing a stream.** Store the source (the list), not the stream, if you need it twice.

3. **Thinking the source is modified.** `filter`/`map` do not change the original list;
   they produce a new stream/result. The original `List` is unchanged.

4. **Confusing intermediate and terminal operations.** `map` and `filter` return a stream
   (intermediate); `collect`, `count`, `forEach`, `toList` end the pipeline (terminal).

5. **Forgetting to collect the result.** `stream().map(...)` alone gives a `Stream`, not a
   `List`. Add `.toList()` or `.collect(...)` to get a usable collection.

---

## Exam Notes

- A stream is a **pipeline**, not a collection; it does not store elements.
- Pipeline = **source → intermediate operations → terminal operation**.
- Intermediate operations are **lazy** and return a `Stream`.
- Terminal operations trigger execution and **consume** the stream.
- A stream is **single-use**; reusing it throws `IllegalStateException`.
- Streams do **not** modify their source.

---

## Practice Questions

1. Name the three parts of a stream pipeline and give one example operation for each.

2. What is printed by the following code, and why?
   ```java
   List.of(1, 2, 3).stream().map(x -> x * 2);
   ```
   (Answer: nothing — there is no terminal operation, so `map` never runs.)

3. Explain in one sentence why calling `count()` twice on the same stream variable fails.

4. Rewrite this loop as a stream pipeline that returns a `List<Integer>`:
   ```java
   List<Integer> evens = new ArrayList<>();
   for (int n : numbers) if (n % 2 == 0) evens.add(n);
   ```

5. True or false: `list.stream().filter(...)` removes elements from the original list.
   Explain.

6. Which of these are terminal operations: `filter`, `collect`, `map`, `count`, `sorted`,
   `forEach`? (Answer: `collect`, `count`, `forEach`.)

7. Given `List<String> names`, write a pipeline that keeps names longer than 4 characters,
   sorts them, and collects them into a list.
