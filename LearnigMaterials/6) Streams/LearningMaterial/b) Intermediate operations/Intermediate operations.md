# Java Stream Intermediate Operations - Complete Guide

## Overview
Intermediate operations are operations that transform a stream into another stream. They are **lazy**, meaning they don't execute until a terminal operation is called. These operations can be chained together to form a pipeline.

---

## 1. filter()

**Purpose:** Selects elements from a stream that match a given condition (predicate).

**Signature:** `Stream<T> filter(Predicate<T> predicate)`

**How it works:** 
- Takes a `Predicate<T>` (a function that returns true/false)
- Only elements where the predicate returns `true` pass through
- Returns a new stream with filtered elements

**Example:**
```java
import java.util.List;
import java.util.stream.Collectors;

public class FilterExample {
    public static void main(String[] args) {
        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // Filter even numbers only
        List<Integer> evenNumbers = numbers.stream()
            .filter(n -> n % 2 == 0)
            .collect(Collectors.toList());
        
        System.out.println("Original: " + numbers);
        System.out.println("Even numbers: " + evenNumbers);
        
        // Filter numbers greater than 5
        List<Integer> greaterThanFive = numbers.stream()
            .filter(n -> n > 5)
            .collect(Collectors.toList());
        
        System.out.println("Greater than 5: " + greaterThanFive);
    }
}
```

**Output:**
```
Original: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
Even numbers: [2, 4, 6, 8, 10]
Greater than 5: [6, 7, 8, 9, 10]
```

---

## 2. limit()

**Purpose:** Truncates the stream to contain at most the specified number of elements.

**Signature:** `Stream<T> limit(int maxSize)`

**How it works:**
- Takes an integer specifying maximum elements
- Returns a stream with at most that many elements
- Useful for pagination or getting first N elements

**Example:**
```java
import java.util.List;
import java.util.stream.Collectors;

public class LimitExample {
    public static void main(String[] args) {
        List<String> fruits = List.of("Apple", "Banana", "Cherry", 
                                      "Date", "Elderberry", "Fig");
        
        // Get first 3 fruits
        List<String> firstThree = fruits.stream()
            .limit(3)
            .collect(Collectors.toList());
        
        System.out.println("All fruits: " + fruits);
        System.out.println("First 3: " + firstThree);
        
        // Combine with filter
        List<String> firstTwoLongNames = fruits.stream()
            .filter(f -> f.length() > 5)
            .limit(2)
            .collect(Collectors.toList());
        
        System.out.println("First 2 long names: " + firstTwoLongNames);
    }
}
```

**Output:**
```
All fruits: [Apple, Banana, Cherry, Date, Elderberry, Fig]
First 3: [Apple, Banana, Cherry]
First 2 long names: [Banana, Cherry]
```

---

## 3. skip()

**Purpose:** Skips the first N elements of the stream.

**Signature:** `Stream<T> skip(int n)`

**How it works:**
- Takes an integer specifying how many elements to skip
- Returns a stream without the first N elements
- Useful for pagination (skip items already shown)

**Example:**
```java
import java.util.List;
import java.util.stream.Collectors;

public class SkipExample {
    public static void main(String[] args) {
        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // Skip first 5 numbers
        List<Integer> afterSkip = numbers.stream()
            .skip(5)
            .collect(Collectors.toList());
        
        System.out.println("Original: " + numbers);
        System.out.println("After skip(5): " + afterSkip);
        
        // Pagination: skip 3, take 3 (page 2 with size 3)
        List<Integer> page2 = numbers.stream()
            .skip(3)
            .limit(3)
            .collect(Collectors.toList());
        
        System.out.println("Page 2 (skip 3, limit 3): " + page2);
    }
}
```

**Output:**
```
Original: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
After skip(5): [6, 7, 8, 9, 10]
Page 2 (skip 3, limit 3): [4, 5, 6]
```

---

## 4. sorted()

**Purpose:** Sorts the elements of the stream.

**Signature:** 
- `Stream<T> sorted()` - natural order
- `Stream<T> sorted(Comparator<T> comparator)` - custom order

**How it works:**
- Without arguments: uses natural ordering (elements must implement Comparable)
- With Comparator: uses custom comparison logic
- Returns a new stream with sorted elements

**Example 1: Sorting Numbers**
```java
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SortedExample {
    public static void main(String[] args) {
        List<Integer> numbers = List.of(5, 2, 8, 1, 9, 3);
        
        // Natural order (ascending)
        List<Integer> ascending = numbers.stream()
            .sorted()
            .collect(Collectors.toList());
        
        System.out.println("Original: " + numbers);
        System.out.println("Ascending: " + ascending);
        
        // Descending order using Comparator
        List<Integer> descending = numbers.stream()
            .sorted(Comparator.reverseOrder())
            .collect(Collectors.toList());
        
        System.out.println("Descending: " + descending);
        
        // Custom comparator: sort by absolute distance from 5
        List<Integer> byDistance = numbers.stream()
            .sorted(Comparator.comparingInt(n -> Math.abs(n - 5)))
            .collect(Collectors.toList());
        
        System.out.println("By distance from 5: " + byDistance);
    }
}
```

**Output:**
```
Original: [5, 2, 8, 1, 9, 3]
Ascending: [1, 2, 3, 5, 8, 9]
Descending: [9, 8, 5, 3, 2, 1]
By distance from 5: [5, 3, 8, 2, 9, 1]
```

**Example 2: Sorting Strings by Length**
```java
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SortedByLengthExample {
    public static void main(String[] args) {
        List<String> words = List.of("banana", "kiwi", "apple", "strawberry", "fig");
        
        // Sort by string length (shortest to longest)
        List<String> byLength = words.stream()
            .sorted(Comparator.comparingInt(String::length))
            .collect(Collectors.toList());
        
        System.out.println("Original: " + words);
        System.out.println("Sorted by length: " + byLength);
        
        // Sort by length descending (longest to shortest)
        List<String> byLengthDesc = words.stream()
            .sorted(Comparator.comparingInt(String::length).reversed())
            .collect(Collectors.toList());
        
        System.out.println("Sorted by length (desc): " + byLengthDesc);
    }
}
```

**Output:**
```
Original: [banana, kiwi, apple, strawberry, fig]
Sorted by length: [fig, kiwi, apple, banana, strawberry]
Sorted by length (desc): [strawberry, banana, apple, kiwi, fig]
```

**Example 3: Sorting List of Lists by Number of Elements**
```java
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SortedListOfListsExample {
    public static void main(String[] args) {
        List<List<String>> listOfLists = List.of(
            List.of("apple", "banana", "cherry"),
            List.of("dog"),
            List.of("red", "green", "blue", "yellow", "orange"),
            List.of("java", "python")
        );
        
        // Sort by number of elements (smallest to largest)
        List<List<String>> sortedBySize = listOfLists.stream()
            .sorted(Comparator.comparingInt(List::size))
            .collect(Collectors.toList());
        
        System.out.println("Original:");
        listOfLists.forEach(System.out::println);
        
        System.out.println("\nSorted by size:");
        sortedBySize.forEach(System.out::println);
        
        // Sort by size descending (largest to smallest)
        List<List<String>> sortedBySizeDesc = listOfLists.stream()
            .sorted(Comparator.comparingInt(List::size).reversed())
            .collect(Collectors.toList());
        
        System.out.println("\nSorted by size (desc):");
        sortedBySizeDesc.forEach(System.out::println);
    }
}
```

**Output:**
```
Original:
[apple, banana, cherry]
[dog]
[red, green, blue, yellow, orange]
[java, python]

Sorted by size:
[dog]
[java, python]
[apple, banana, cherry]
[red, green, blue, yellow, orange]

Sorted by size (desc):
[red, green, blue, yellow, orange]
[apple, banana, cherry]
[java, python]
[dog]
```

---

## 5. distinct()

**Purpose:** Removes duplicate elements from the stream.

**Signature:** `Stream<T> distinct()`

**How it works:**
- Uses `equals()` method to determine duplicates
- Returns a stream with only unique elements
- Order of first occurrence is preserved

**Example:**
```java
import java.util.List;
import java.util.stream.Collectors;

public class DistinctExample {
    public static void main(String[] args) {
        List<Integer> numbersWithDuplicates = 
            List.of(1, 2, 2, 3, 4, 4, 4, 5, 1, 3);
        
        // Remove duplicates
        List<Integer> unique = numbersWithDuplicates.stream()
            .distinct()
            .collect(Collectors.toList());
        
        System.out.println("With duplicates: " + numbersWithDuplicates);
        System.out.println("Unique: " + unique);
        
        // Combine with sorted
        List<Integer> uniqueSorted = numbersWithDuplicates.stream()
            .distinct()
            .sorted()
            .collect(Collectors.toList());
        
        System.out.println("Unique and sorted: " + uniqueSorted);
        
        // String example
        List<String> words = List.of("hello", "world", "hello", "java");
        List<String> uniqueWords = words.stream()
            .distinct()
            .collect(Collectors.toList());
        
        System.out.println("Unique words: " + uniqueWords);
    }
}
```

**Output:**
```
With duplicates: [1, 2, 2, 3, 4, 4, 4, 5, 1, 3]
Unique: [1, 2, 3, 4, 5]
Unique and sorted: [1, 2, 3, 4, 5]
Unique words: [hello, world, java]
```

---

## 6. map()

**Purpose:** Transforms each element of the stream using a given function.

**Signature:** `Stream<R> map(Function<T, R> mapper)`

**How it works:**
- Takes a `Function<T, R>` that converts type T to type R
- Applies the function to each element
- Returns a new stream with transformed elements

**Example:**
```java
import java.util.List;
import java.util.stream.Collectors;

public class MapExample {
    public static void main(String[] args) {
        List<String> names = List.of("alice", "bob", "charlie");
        
        // Convert to uppercase
        List<String> uppercase = names.stream()
            .map(String::toUpperCase)
            .collect(Collectors.toList());
        
        System.out.println("Original: " + names);
        System.out.println("Uppercase: " + uppercase);
        
        // Get string lengths
        List<Integer> lengths = names.stream()
            .map(String::length)
            .collect(Collectors.toList());
        
        System.out.println("Lengths: " + lengths);
        
        // Square numbers
        List<Integer> numbers = List.of(1, 2, 3, 4, 5);
        List<Integer> squared = numbers.stream()
            .map(n -> n * n)
            .collect(Collectors.toList());
        
        System.out.println("Numbers: " + numbers);
        System.out.println("Squared: " + squared);
    }
}
```

**Output:**
```
Original: [alice, bob, charlie]
Uppercase: [ALICE, BOB, CHARLIE]
Lengths: [5, 3, 7]
Numbers: [1, 2, 3, 4, 5]
Squared: [1, 4, 9, 16, 25]
```

---

## Complete Pipeline Example

Combining multiple operations:

```java
import java.util.List;
import java.util.stream.Collectors;

public class CombinedExample {
    public static void main(String[] args) {
        List<String> words = List.of("apple", "banana", "apricot", 
                                     "cherry", "avocado", "blueberry");
        
        // Complex pipeline: 
        // 1. Filter words starting with 'a'
        // 2. Map to uppercase
        // 3. Sort alphabetically
        // 4. Limit to 2
        List<String> result = words.stream()
            .filter(w -> w.startsWith("a"))
            .map(String::toUpperCase)
            .sorted()
            .limit(2)
            .collect(Collectors.toList());
        
        System.out.println("Original: " + words);
        System.out.println("Processed: " + result);
    }
}
```

**Output:**
```
Original: [apple, banana, apricot, cherry, avocado, blueberry]
Processed: [APPLE, APRICOT]
```

---

## Key Concepts

### Lazy Evaluation
Intermediate operations are **lazy** - they don't execute until a terminal operation (like `collect()`, `forEach()`, `count()`) is called.

### Method Chaining
Operations can be chained together to create powerful data processing pipelines.

### Immutability
Stream operations don't modify the original collection - they create new streams.

### Short-circuiting
Operations like `limit()` can cause the stream to stop processing early, improving performance.

---

## Common Use Cases

1. **filter()** - Remove unwanted data, apply conditions
2. **map()** - Transform data, extract properties
3. **sorted()** - Order results
4. **distinct()** - Remove duplicates
5. **limit()** - Pagination, top-N results
6. **skip()** - Pagination, ignore initial elements

---

## Best Practices

1. Keep lambda expressions simple and readable
2. Chain operations in logical order
3. Use method references when possible (`String::toUpperCase`)
4. Remember that streams are one-time use
5. Consider performance with large datasets
6. Use `parallel()` for CPU-intensive operations on large datasets