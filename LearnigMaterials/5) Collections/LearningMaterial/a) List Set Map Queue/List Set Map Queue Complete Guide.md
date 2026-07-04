# Java List, Set, Map, and Queue - Complete Guide

## Overview
The Java Collections Framework provides standard data structures for storing and processing groups of objects.

---

## 1. Collection hierarchy simplified

```text
Collection
├── List
├── Set
└── Queue

Map is separate because it stores key-value pairs.
```

---

## 2. List

A `List` is ordered and allows duplicates.

Common implementation:

```java
List<String> names = new ArrayList<>();
names.add("Sara");
names.add("Ali");
names.add("Sara");

System.out.println(names); // [Sara, Ali, Sara]
System.out.println(names.get(0)); // Sara
```

Use `List` when:

- order matters
- duplicates are allowed
- you access elements by index

---

## 3. Set

A `Set` does not allow duplicates.

```java
Set<String> names = new HashSet<>();
names.add("Sara");
names.add("Ali");
names.add("Sara");

System.out.println(names.size()); // 2
```

Common implementations:

| Implementation | Main idea |
|---|---|
| `HashSet` | fast, no guaranteed order |
| `LinkedHashSet` | insertion order |
| `TreeSet` | sorted order |

---

## 4. Map

A `Map` stores key-value pairs.

```java
Map<String, Integer> ages = new HashMap<>();
ages.put("Sara", 22);
ages.put("Ali", 24);

System.out.println(ages.get("Sara")); // 22
```

Keys are unique. Values can be duplicated.

```java
ages.put("Sara", 23);
System.out.println(ages.get("Sara")); // 23
```

The second `put` replaces the old value for the same key.

---

## 5. Queue

A `Queue` represents elements waiting to be processed.

```java
Queue<String> queue = new LinkedList<>();
queue.add("first");
queue.add("second");

System.out.println(queue.poll()); // first
System.out.println(queue.poll()); // second
```

Useful methods:

| Method | Meaning |
|---|---|
| `add` / `offer` | insert |
| `remove` / `poll` | remove head |
| `element` / `peek` | read head |

`poll()` returns `null` if empty; `remove()` throws exception.

---

## 6. Which collection should I choose?

| Need | Use |
|---|---|
| Ordered sequence with duplicates | `ArrayList` |
| Unique elements, fastest general lookup | `HashSet` |
| Unique elements in insertion order | `LinkedHashSet` |
| Unique sorted elements | `TreeSet` |
| Key-value lookup | `HashMap` |
| Sorted keys | `TreeMap` |
| First-in-first-out processing | `Queue` |

---

## 7. ArrayList vs LinkedList

| Feature | ArrayList | LinkedList |
|---|---|---|
| Access by index | fast | slower |
| Add/remove at end | fast | fast |
| Add/remove in middle | may shift elements | can be better after finding position |
| Common use | most lists | queue/deque operations |

In most normal cases, use `ArrayList`.

---

## Common mistakes

### Mistake 1: expecting HashSet order
`HashSet` does not guarantee order.

### Mistake 2: using List when duplicates must be blocked
Use `Set`.

### Mistake 3: using Map like a Collection
`Map` is not a subtype of `Collection`.

---

## Mini quiz

### Q1. Which collection rejects duplicates?
Answer: `Set`.

### Q2. Which collection stores key-value pairs?
Answer: `Map`.

### Q3. Which list implementation is usually the default choice?
Answer: `ArrayList`.
