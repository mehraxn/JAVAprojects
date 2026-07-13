# `collect()` and Collectors

## Simple Explanation

`collect()` is the **terminal operation** that takes the elements flowing through a stream
and gathers them into a final result — usually a `List`, `Set`, `Map`, or a single `String`.

You tell `collect()` *how* to gather using a **Collector**, and the `Collectors` class gives
you ready-made ones:

```java
import java.util.stream.Collectors;

List<String> list = stream.collect(Collectors.toList());
```

Think of it as: **"run the pipeline and put the results here."**

For the examples below we use this small `Product` class:

```java
class Product {
    String name;
    String category;
    double price;

    Product(String name, String category, double price) {
        this.name = name;
        this.category = category;
        this.price = price;
    }
    String getName()      { return name; }
    String getCategory()  { return category; }
    double getPrice()     { return price; }
}

List<Product> products = List.of(
    new Product("Apple",  "Fruit",  1.20),
    new Product("Banana", "Fruit",  0.80),
    new Product("Carrot", "Veg",    0.50),
    new Product("Donut",  "Bakery", 2.00)
);
```

---

## Why It Matters

- A stream on its own produces nothing usable until you **collect** it.
- `Collectors.groupingBy` and `Collectors.toMap` are among the most common exam and
  real-project operations.
- Knowing the `toMap` duplicate-key trap saves you from a runtime crash.

---

## Basic Example

```java
import java.util.List;
import java.util.stream.Collectors;

List<String> fruitNames = products.stream()
    .filter(p -> p.getCategory().equals("Fruit"))
    .map(Product::getName)
    .collect(Collectors.toList());

System.out.println(fruitNames); // [Apple, Banana]
```

> Note: On Java 16+ you can also write `.toList()` for an unmodifiable list. Use
> `Collectors.toList()` when you need the classic (modifiable) `ArrayList`-style result.

---

## Step-by-Step Explanation

### 1. `Collectors.toList()` — gather into a `List`

```java
List<String> names = products.stream()
    .map(Product::getName)
    .collect(Collectors.toList());
// [Apple, Banana, Carrot, Donut]
```

### 2. `Collectors.toSet()` — gather into a `Set` (removes duplicates)

```java
Set<String> categories = products.stream()
    .map(Product::getCategory)
    .collect(Collectors.toSet());
// [Fruit, Veg, Bakery]  (order not guaranteed)
```

### 3. `Collectors.joining()` — gather Strings into one String

```java
String all = products.stream()
    .map(Product::getName)
    .collect(Collectors.joining());
// AppleBananaCarrotDonut

String csv = products.stream()
    .map(Product::getName)
    .collect(Collectors.joining(", "));
// Apple, Banana, Carrot, Donut

String pretty = products.stream()
    .map(Product::getName)
    .collect(Collectors.joining(", ", "[", "]"));
// [Apple, Banana, Carrot, Donut]
```

`joining(separator, prefix, suffix)` only works on a stream of `String`.

### 4. `Collectors.toMap()` — build a `Map` from key and value functions

```java
Map<String, Double> priceByName = products.stream()
    .collect(Collectors.toMap(
        Product::getName,    // key
        Product::getPrice)); // value
// {Apple=1.2, Banana=0.8, Carrot=0.5, Donut=2.0}
```

If two elements produce the **same key**, `toMap` throws `IllegalStateException`
(see Common Mistakes). Fix it with a **merge function**:

```java
Map<String, Double> priceByCategory = products.stream()
    .collect(Collectors.toMap(
        Product::getCategory,
        Product::getPrice,
        (existing, replacement) -> existing)); // keep the first on a clash
```

### 5. `Collectors.groupingBy()` — group elements into a `Map<Key, List>`

```java
Map<String, List<Product>> byCategory = products.stream()
    .collect(Collectors.groupingBy(Product::getCategory));
// {Fruit=[Apple, Banana], Veg=[Carrot], Bakery=[Donut]}
```

You can group **and** transform with a second collector (a "downstream" collector):

```java
Map<String, Long> countByCategory = products.stream()
    .collect(Collectors.groupingBy(
        Product::getCategory,
        Collectors.counting()));
// {Fruit=2, Veg=1, Bakery=1}
```

### 6. `Collectors.partitioningBy()` — split into `true` / `false` groups

```java
Map<Boolean, List<Product>> cheapVsExpensive = products.stream()
    .collect(Collectors.partitioningBy(p -> p.getPrice() < 1.0));
// {false=[Apple, Donut], true=[Banana, Carrot]}
```

`partitioningBy` always returns a map with exactly **two** keys: `true` and `false`
(even if one group is empty). Use `groupingBy` when you have more than two groups.

---

## Common Mistakes

1. **Duplicate keys in `toMap()`.**
   ```java
   // Two products share the category "Fruit" -> crash
   products.stream()
       .collect(Collectors.toMap(Product::getCategory, Product::getPrice));
   // IllegalStateException: Duplicate key Fruit
   ```
   Fix by adding a merge function: `(a, b) -> a` (keep first), `(a, b) -> b` (keep last),
   or `Double::sum` (combine). Or use `groupingBy` if you actually want a list per key.

2. **Using `joining` on non-Strings.** Map to `String` first
   (`.map(Product::getName)`), otherwise it will not compile.

3. **Expecting order from `toSet()` / `HashMap`.** `toSet` and the default `toMap`
   result do not guarantee any order.

4. **Confusing `groupingBy` and `partitioningBy`.** `partitioningBy` is only for a
   yes/no condition (two groups). `groupingBy` is for many groups by a key.

5. **Forgetting `collect` is terminal.** After `collect(...)` the stream is used up;
   you cannot add more operations or reuse it.

---

## Exam Notes

- `collect()` is a **terminal** operation; it needs a `Collector`.
- `toList` / `toSet` / `toMap` build collections; `joining` builds a `String`.
- `toMap` **throws on duplicate keys** unless you supply a merge function.
- `groupingBy(key)` → `Map<Key, List<T>>`; add a downstream collector like
  `counting()` or `summingInt(...)` to summarize each group.
- `partitioningBy(predicate)` → `Map<Boolean, List<T>>` with keys `true` and `false`.

---

## Practice Questions

1. Which operation type is `collect()` — intermediate or terminal? What happens to the
   stream after it runs?

2. Write a pipeline that collects all product **names** into a single comma-separated
   `String` surrounded by square brackets.

3. This code crashes at runtime. Why, and how do you fix it?
   ```java
   products.stream().collect(Collectors.toMap(Product::getCategory, Product::getPrice));
   ```

4. What is the difference between `groupingBy` and `partitioningBy`? When would you
   choose each?

5. Write a pipeline that produces a `Map<String, Long>` of how many products are in each
   category. (Hint: `groupingBy` + `counting`.)

6. Given a `List<String> words`, collect only the distinct words into a `Set`.

7. What does the third argument of `Collectors.toMap(keyFn, valueFn, mergeFn)` do, and
   give one useful merge function.
