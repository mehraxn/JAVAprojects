# 06 — Collections Exercises

Topics: `List`, `Set`, `Map`, `Queue`, iteration, `compute`/`merge`, sorting. Solutions under
each **Solution** heading. Outputs checked by static review. Where a `HashMap`/`HashSet` order
is not guaranteed, it is noted.

---

## Exercise 1 — Build and print a list

Create an `ArrayList<String>`, add `"a"`, `"b"`, `"c"`, and print it.

**Expected output:**
```
[a, b, c]
```

### Solution
```java
import java.util.ArrayList;
import java.util.List;

List<String> list = new ArrayList<>();
list.add("a");
list.add("b");
list.add("c");
System.out.println(list); // [a, b, c]
```

---

## Exercise 2 — Remove duplicates with a Set

Given `List<Integer> nums = List.of(1, 2, 2, 3, 3, 3);`, count the distinct values.

**Expected output:**
```
3
```

### Solution
```java
import java.util.HashSet;
import java.util.Set;

Set<Integer> unique = new HashSet<>(List.of(1, 2, 2, 3, 3, 3));
System.out.println(unique.size()); // 3
```

---

## Exercise 3 — Map lookup

Create a `Map<String, Integer>` of ages (`"Ann"->30`, `"Bob"->25`) and print Ann's age.

**Expected output:**
```
30
```

### Solution
```java
import java.util.HashMap;
import java.util.Map;

Map<String, Integer> ages = new HashMap<>();
ages.put("Ann", 30);
ages.put("Bob", 25);
System.out.println(ages.get("Ann")); // 30
```

---

## Exercise 4 — Word frequency with `merge`

Count how many times each word appears in `List.of("a", "b", "a", "c", "a")`. Print the count
for `"a"`.

**Expected output:**
```
3
```

### Solution
```java
Map<String, Integer> freq = new HashMap<>();
for (String w : List.of("a", "b", "a", "c", "a")) {
    freq.merge(w, 1, Integer::sum);
}
System.out.println(freq.get("a")); // 3
```

---

## Exercise 5 — Sort a list of objects

Sort a `List<String>` by length (shortest first) and print it.
```java
List<String> words = new ArrayList<>(List.of("banana", "fig", "apple"));
```

**Expected output:**
```
[fig, apple, banana]
```

### Solution
```java
import java.util.Comparator;

words.sort(Comparator.comparingInt(String::length));
System.out.println(words); // [fig, apple, banana]
```

---

## Exercise 6 — Queue (FIFO)

Use a `Queue<String>` (a `LinkedList`), add `"first"` then `"second"`, and poll one element.

**Expected output:**
```
first
```

### Solution
```java
import java.util.LinkedList;
import java.util.Queue;

Queue<String> q = new LinkedList<>();
q.offer("first");
q.offer("second");
System.out.println(q.poll()); // first (FIFO: first in, first out)
```

---

## Exercise 7 — Predict the output

```java
Set<String> set = new HashSet<>();
System.out.println(set.add("x"));
System.out.println(set.add("x"));
System.out.println(set.size());
```

### Solution
```
true
false
1
```
`add` returns `true` when the element is new, `false` when it is already present. The set holds
`"x"` once.

---

## Exercise 8 — Fix the bug (modifying while iterating)

This throws `ConcurrentModificationException`. Fix it two ways.
```java
List<Integer> list = new ArrayList<>(List.of(1, 2, 3, 4));
for (int n : list) {
    if (n % 2 == 0) list.remove(Integer.valueOf(n));
}
```

### Solution
Do not structurally modify a list during a for-each loop. Use `removeIf`:
```java
list.removeIf(n -> n % 2 == 0); // [1, 3]
```
or an explicit `Iterator`:
```java
Iterator<Integer> it = list.iterator();
while (it.hasNext()) {
    if (it.next() % 2 == 0) it.remove();
}
```

---

## Exercise 9 — Fix the bug (immutable list)

This throws `UnsupportedOperationException`. Fix it.
```java
List<String> list = List.of("a", "b");
list.add("c");
```

### Solution
`List.of(...)` is immutable. Wrap it in a modifiable list:
```java
List<String> list = new ArrayList<>(List.of("a", "b"));
list.add("c"); // [a, b, c]
```

---

## Challenge — Group names by first letter

Given `List.of("Ann", "Al", "Bob", "Bea")`, build a `Map<Character, List<String>>` grouping by
first letter (without streams, using `computeIfAbsent`).

### Solution
```java
Map<Character, List<String>> byLetter = new HashMap<>();
for (String name : List.of("Ann", "Al", "Bob", "Bea")) {
    byLetter.computeIfAbsent(name.charAt(0), k -> new ArrayList<>()).add(name);
}
System.out.println(byLetter.get('A')); // [Ann, Al]
System.out.println(byLetter.get('B')); // [Bob, Bea]
```
