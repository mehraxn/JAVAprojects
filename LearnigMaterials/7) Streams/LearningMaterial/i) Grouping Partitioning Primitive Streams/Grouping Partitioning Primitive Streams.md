# Grouping, Partitioning, and Primitive Streams - Complete Guide

## Overview
Advanced stream operations often appear in exams: `groupingBy`, `partitioningBy`, `mapToInt`, `mapToDouble`, `summaryStatistics`, and numeric reductions.

---

## 1. `groupingBy`

Groups elements into a `Map`.

```java
List<String> words = List.of("apple", "banana", "cherry", "avocado", "blueberry");

Map<Integer, List<String>> byLength = words.stream()
    .collect(Collectors.groupingBy(String::length));

System.out.println(byLength.get(6));
```

Output:

```text
[banana, cherry]
```

Explanation:

- `apple` length 5
- `banana` length 6
- `cherry` length 6
- `avocado` length 7
- `blueberry` length 9

---

## 2. Grouping with counting

```java
Map<Integer, Long> countByLength = words.stream()
    .collect(Collectors.groupingBy(String::length, Collectors.counting()));
```

Result example:

```text
{5=1, 6=2, 7=1, 9=1}
```

---

## 3. Grouping objects

```java
class Student {
    private String city;
    private String name;

    public String getCity() { return city; }
    public String getName() { return name; }
}
```

```java
Map<String, List<Student>> byCity = students.stream()
    .collect(Collectors.groupingBy(Student::getCity));
```

---

## 4. `partitioningBy`

`partitioningBy` splits into two groups: `true` and `false`.

```java
Map<Boolean, List<Integer>> partitioned = numbers.stream()
    .collect(Collectors.partitioningBy(n -> n % 2 == 0));
```

Result:

```text
true  -> even numbers
false -> odd numbers
```

Use `partitioningBy` when the classifier is boolean.

---

## 5. Primitive streams

Object stream:

```java
Stream<Integer>
```

Primitive streams:

```java
IntStream
LongStream
DoubleStream
```

They are useful for numeric operations.

```java
int sum = numbers.stream()
    .mapToInt(Integer::intValue)
    .sum();
```

---

## 6. `mapToDouble` and max helper

```java
static double max(Stream<NutritionalElement> elements,
                  ToDoubleFunction<NutritionalElement> extractor) {
    return elements
        .mapToDouble(extractor)
        .max()
        .orElse(0.0);
}
```

Invoke for calories:

```java
max(elements.stream(), NutritionalElement::getCalories)
```

Why:

- first argument must be a stream
- second argument must be a function extracting a double

---

## 7. Summary statistics

```java
DoubleSummaryStatistics stats = products.stream()
    .mapToDouble(Product::getPrice)
    .summaryStatistics();

System.out.println(stats.getCount());
System.out.println(stats.getMin());
System.out.println(stats.getMax());
System.out.println(stats.getAverage());
System.out.println(stats.getSum());
```

---

## Common mistakes

### Mistake 1: expecting `groupingBy` to return a list
It returns a map.

### Mistake 2: using `groupingBy` for true/false split
Use `partitioningBy` for boolean grouping.

### Mistake 3: calling getter instead of method reference
Wrong:

```java
NutritionalElement.getCalories()
```

Correct:

```java
NutritionalElement::getCalories
```

---

## Mini quiz

### Q1. What is the key type in `groupingBy(String::length)`?
Answer: `Integer`.

### Q2. What does `partitioningBy` return?
Answer: `Map<Boolean, List<T>>` usually.

### Q3. Why use `mapToDouble`?
Answer: to convert objects into a `DoubleStream` for numeric operations.
