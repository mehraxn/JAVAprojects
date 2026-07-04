# 09 — Lambdas, Streams, and Optional Exercises

Topics: functional interfaces, lambdas, method references, stream pipelines, collectors,
`Optional`. Solutions under each **Solution** heading. Outputs checked by static review.

Shared data for several tasks:
```java
record Product(String name, String category, double price) { }

List<Product> products = List.of(
    new Product("Apple",  "Fruit",  1.20),
    new Product("Banana", "Fruit",  0.80),
    new Product("Carrot", "Veg",    0.50),
    new Product("Donut",  "Bakery", 2.00)
);
```

---

## Exercise 1 — Lambda for a functional interface

Create a `Predicate<Integer> isPositive` and test it on `-3` and `5`.

**Expected output:**
```
false
true
```

### Solution
```java
import java.util.function.Predicate;

Predicate<Integer> isPositive = n -> n > 0;
System.out.println(isPositive.test(-3)); // false
System.out.println(isPositive.test(5));  // true
```

---

## Exercise 2 — Method reference

Convert `s -> s.toUpperCase()` to a method reference and map a list of names to uppercase.

**Expected output:**
```
[ANN, BOB]
```

### Solution
```java
List<String> upper = List.of("Ann", "Bob").stream()
    .map(String::toUpperCase)
    .toList();
System.out.println(upper); // [ANN, BOB]
```

---

## Exercise 3 — filter + map + collect

From `products`, get the **names** of products cheaper than 1.50, as a list.

**Expected output:**
```
[Banana, Carrot]
```

### Solution
```java
List<String> cheap = products.stream()
    .filter(p -> p.price() < 1.50)
    .map(Product::name)
    .toList();
System.out.println(cheap); // [Banana, Carrot]
```

---

## Exercise 4 — count and sum

Count the "Fruit" products and sum all prices.

**Expected output:**
```
2
4.5
```

### Solution
```java
long fruitCount = products.stream()
    .filter(p -> p.category().equals("Fruit"))
    .count();
System.out.println(fruitCount); // 2

double totalPrice = products.stream()
    .mapToDouble(Product::price)
    .sum();
System.out.println(totalPrice); // 1.20 + 0.80 + 0.50 + 2.00 = 4.5
```

---

## Exercise 5 — groupingBy

Group products by category into a `Map<String, List<Product>>`, then print how many are in
"Fruit".

**Expected output:**
```
2
```

### Solution
```java
import java.util.stream.Collectors;

Map<String, List<Product>> byCategory = products.stream()
    .collect(Collectors.groupingBy(Product::category));
System.out.println(byCategory.get("Fruit").size()); // 2
```

---

## Exercise 6 — Optional

Write a method `Optional<Product> findByName(String name)` using streams, then print the price
of `"Apple"` or `-1` if missing.

**Expected output:**
```
1.2
```

### Solution
```java
import java.util.Optional;

Optional<Product> apple = products.stream()
    .filter(p -> p.name().equals("Apple"))
    .findFirst();

double price = apple.map(Product::price).orElse(-1.0);
System.out.println(price); // 1.2
```

---

## Exercise 7 — Predict the output

```java
List<Integer> nums = List.of(1, 2, 3, 4, 5);
int result = nums.stream()
    .filter(n -> n % 2 == 1)
    .mapToInt(n -> n * n)
    .sum();
System.out.println(result);
```

### Solution
```
35
```
Odd numbers 1, 3, 5 → squared 1, 9, 25 → sum 35.

---

## Exercise 8 — Predict the output (laziness)

```java
List.of("a", "b", "c").stream()
    .filter(s -> {
        System.out.println("filtering " + s);
        return true;
    });
```

### Solution
Prints **nothing**. There is no terminal operation, so the lazy `filter` never runs.

---

## Exercise 9 — Fix the bug (reusing a stream)

This throws `IllegalStateException`. Fix it.
```java
Stream<Integer> s = List.of(1, 2, 3).stream();
System.out.println(s.count());
System.out.println(s.count());
```

### Solution
A stream is single-use. Create a new stream from the source each time:
```java
List<Integer> list = List.of(1, 2, 3);
System.out.println(list.stream().count()); // 3
System.out.println(list.stream().count()); // 3
```

---

## Exercise 10 — Fix the bug (duplicate keys in toMap)

This throws `IllegalStateException` because two products share a category. Fix it so equal
categories keep the **first** price seen.
```java
Map<String, Double> priceByCategory = products.stream()
    .collect(Collectors.toMap(Product::category, Product::price));
```

### Solution
Add a merge function to resolve duplicate keys:
```java
Map<String, Double> priceByCategory = products.stream()
    .collect(Collectors.toMap(
        Product::category,
        Product::price,
        (existing, duplicate) -> existing)); // keep the first
```

---

## Challenge — Average price per category

Produce a `Map<String, Double>` of the average price per category and print the "Fruit" average.

**Expected output:**
```
1.0
```

### Solution
```java
Map<String, Double> avgByCategory = products.stream()
    .collect(Collectors.groupingBy(
        Product::category,
        Collectors.averagingDouble(Product::price)));
System.out.println(avgByCategory.get("Fruit")); // (1.20 + 0.80) / 2 = 1.0
```
