# Java Map Methods: `compute`, `computeIfAbsent`, `merge`, and Frequency Counting - Complete Guide

## Overview
`Map` is used very often in Java exams and projects. Besides `put` and `get`, Java provides powerful update methods like `compute`, `computeIfAbsent`, and `merge`.

---

## 1. Basic map operations

```java
Map<String, Integer> ages = new HashMap<>();

ages.put("Sara", 22);
ages.put("Ali", 24);

System.out.println(ages.get("Sara")); // 22
System.out.println(ages.containsKey("Tom")); // false
```

---

## 2. The old frequency-counting style

```java
Map<String, Long> frequency = new HashMap<>();
String word = "java";

if (frequency.containsKey(word)) {
    frequency.put(word, frequency.get(word) + 1);
} else {
    frequency.put(word, 1L);
}
```

This works, but it is verbose.

---

## 3. `compute`

`compute` recalculates the value for a key.

```java
frequency.compute(word, (w, count) -> count == null ? 1L : count + 1);
```

Explanation:

- `w` is the key
- `count` is the old value, or `null` if the key did not exist
- the returned value becomes the new value in the map

Complete example:

```java
import java.util.*;

public class WordFrequency {
    public static void main(String[] args) {
        List<String> words = List.of("java", "stream", "java", "map");
        Map<String, Long> frequency = new HashMap<>();

        for (String word : words) {
            frequency.compute(word, (w, c) -> c == null ? 1L : c + 1);
        }

        System.out.println(frequency);
    }
}
```

Possible output:

```text
{stream=1, java=2, map=1}
```

---

## 4. Important trap: `c++`

This looks correct but is dangerous:

```java
frequency.compute(word, (w, c) -> c == null ? 1L : c++);
```

Problem: `c++` returns the old value, not the incremented value.

Use:

```java
c + 1
```

Correct:

```java
frequency.compute(word, (w, c) -> c == null ? 1L : c + 1);
```

---

## 5. `merge`

`merge` is very good for counting.

```java
frequency.merge(word, 1L, Long::sum);
```

Meaning:

- if `word` is absent, insert `1L`
- if present, combine old value and `1L` using `Long::sum`

Complete example:

```java
Map<String, Long> frequency = new HashMap<>();

for (String word : List.of("a", "b", "a")) {
    frequency.merge(word, 1L, Long::sum);
}

System.out.println(frequency); // {a=2, b=1}
```

---

## 6. `computeIfAbsent`

`computeIfAbsent` creates a value only when the key is missing.

Very common with map of lists:

```java
Map<String, List<String>> studentsByCity = new HashMap<>();

studentsByCity.computeIfAbsent("Turin", city -> new ArrayList<>())
              .add("Sara");

studentsByCity.computeIfAbsent("Turin", city -> new ArrayList<>())
              .add("Ali");

System.out.println(studentsByCity);
```

Output:

```text
{Turin=[Sara, Ali]}
```

---

## 7. `getOrDefault`

```java
int count = map.getOrDefault("java", 0);
```

If key exists, returns its value. If not, returns the default.

Counting example:

```java
map.put(word, map.getOrDefault(word, 0) + 1);
```

---

## Common mistakes

### Mistake 1: using `computeIfAbsent` for simple increment
`computeIfAbsent` only creates missing values. It does not automatically update existing counts.

### Mistake 2: using `c++` in lambda
Use `c + 1` instead.

### Mistake 3: forgetting absent key produces `null` in `compute`
Always handle `c == null`.

---

## Mini quiz

### Q1. Best simple way to count words with a map?
Answer: `frequency.merge(word, 1L, Long::sum)`.

### Q2. What does `computeIfAbsent` do?
Answer: creates and stores a value only if the key is absent.

### Q3. Why is `c++` wrong in `compute`?
Answer: it returns the old value.
