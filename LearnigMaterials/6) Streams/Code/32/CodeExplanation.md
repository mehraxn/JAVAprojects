# Java Stream Collect: Complete Technical Guide

## Overview of the Code

```java
class Acc { int n; }
int s = Stream.of(numbers)
    .collect(Acc::new,              // supplier
             (a,i) -> a.n+=i,       // accumulator
             (a1,a2)->a1.n+=a2.n    // combiner
    ).n;
```

This code demonstrates the **three-argument form** of `Stream.collect()`, which is a powerful reduction operation that creates a mutable result container.

---

## The Three Components Explained

### 1. **Supplier: `Acc::new`**

**Purpose:** Creates a new empty result container for accumulating values.

**Signature:** `Supplier<R> supplier`

**What it does:**
- Called at the beginning of the reduction operation
- In sequential streams: called **once** to create the single container
- In parallel streams: called **multiple times** (once per thread) to create containers for each parallel segment

**In this example:**
```java
Acc::new  // Method reference to constructor
// Equivalent to: () -> new Acc()
```

Each invocation creates a fresh `Acc` object with `n = 0` (default int value).

**Key Insight:** The supplier must produce a **new, independent** container each time. Returning the same instance would cause race conditions in parallel streams.

---

### 2. **Accumulator: `(a,i) -> a.n+=i`**

**Purpose:** Incorporates a single stream element into the result container.

**Signature:** `BiConsumer<R, T> accumulator`

**What it does:**
- Called for **each element** in the stream
- Takes two parameters:
  - `a`: The accumulator container (type `Acc`)
  - `i`: The current stream element (type of stream elements)
- Mutates the container by adding the element's contribution

**In this example:**
```java
(a, i) -> a.n += i
// a: Acc object (container)
// i: Integer from stream
// Effect: adds integer value to a.n
```

**Execution pattern:**
- For stream `[5, 10, 15]`
- Container starts: `Acc{n=0}`
- After element 5: `Acc{n=5}`
- After element 10: `Acc{n=15}`
- After element 15: `Acc{n=30}`

**Critical Point:** The accumulator **mutates** the container; it doesn't return a new one. This is why `collect()` is called a "mutable reduction."

---

### 3. **Combiner: `(a1,a2) -> a1.n+=a2.n`**

**Purpose:** Merges two partial result containers into one.

**Signature:** `BiConsumer<R, R> combiner`

**What it does:**
- Called in **parallel streams** to merge results from different threads
- Takes two containers that were independently populated
- Combines them by mutating the first container

**In this example:**
```java
(a1, a2) -> a1.n += a2.n
// a1: First Acc container
// a2: Second Acc container
// Effect: adds a2's value into a1
```

**When is it used?**

**Sequential Stream:**
- Combiner is **NOT called** (only one thread, one container)

**Parallel Stream:**
```java
// Stream split into chunks across threads
Thread 1: [1, 2, 3] → Acc{n=6}
Thread 2: [4, 5, 6] → Acc{n=15}
Thread 3: [7, 8, 9] → Acc{n=24}

// Combiner merges them:
Acc{n=6} + Acc{n=15} → Acc{n=21}
Acc{n=21} + Acc{n=24} → Acc{n=45}
```

**Why it matters:** Without a correct combiner, parallel streams would produce incorrect results.

---

## Stream\<Integer\> vs Stream\<String\>: The Critical Difference

### Example 1: Stream of Integers (Numeric Sum)

```java
class Acc { int n; }

Integer[] numbers = {5, 10, 15, 20};
int sum = Stream.of(numbers)
    .collect(Acc::new,
             (a, i) -> a.n += i,      // i is Integer
             (a1, a2) -> a1.n += a2.n
    ).n;
// Result: 50
```

**Accumulator behavior:**
- `i` is an `Integer`
- `a.n += i` performs **numeric addition**
- Auto-unboxing converts `Integer` to `int`

---

### Example 2: Stream of Strings (Concatenation)

```java
class Acc { String s = ""; }

String[] words = {"Hello", " ", "World", "!"};
String result = Stream.of(words)
    .collect(Acc::new,
             (a, str) -> a.s += str,  // str is String
             (a1, a2) -> a1.s += a2.s
    ).s;
// Result: "Hello World!"
```

**Accumulator behavior:**
- `str` is a `String`
- `a.s += str` performs **string concatenation**
- Completely different operation than numeric addition

---

### Example 3: Stream of Strings (Length Sum)

```java
class Acc { int n; }

String[] words = {"Java", "Stream", "API"};
int totalLength = Stream.of(words)
    .collect(Acc::new,
             (a, str) -> a.n += str.length(),  // Extract length
             (a1, a2) -> a1.n += a2.n
    ).n;
// Result: 13 (4 + 6 + 3)
```

**Accumulator behavior:**
- `str` is a `String`
- We extract numeric property (`length()`)
- Then perform numeric addition

---

## Key Differences Summary

| Aspect | Stream\<Integer\> | Stream\<String\> |
|--------|------------------|------------------|
| **Element Type** | `Integer` objects | `String` objects |
| **Accumulator Parameter** | `(Acc a, Integer i)` | `(Acc a, String str)` |
| **Common Operation** | Numeric summation | Concatenation or analysis |
| **Container Field** | Typically `int` or `long` | Typically `String` or `StringBuilder` |
| **Auto-unboxing** | Yes (Integer → int) | N/A |
| **Performance Note** | Efficient for numeric ops | String concatenation can be slow (use StringBuilder) |

---

## Complete Working Examples

### Integer Stream Example

```java
class IntAcc { 
    int sum = 0; 
}

Integer[] numbers = {1, 2, 3, 4, 5};

int result = Stream.of(numbers)
    .collect(
        IntAcc::new,                    // Create new IntAcc
        (acc, num) -> acc.sum += num,   // Add each integer
        (acc1, acc2) -> acc1.sum += acc2.sum  // Merge sums
    ).sum;

System.out.println(result);  // Output: 15
```

---

### String Stream Example (Efficient)

```java
class StrAcc { 
    StringBuilder sb = new StringBuilder(); 
}

String[] words = {"Java", " ", "is", " ", "powerful"};

String result = Stream.of(words)
    .collect(
        StrAcc::new,                       // Create new StrAcc
        (acc, str) -> acc.sb.append(str),  // Append each string
        (acc1, acc2) -> acc1.sb.append(acc2.sb)  // Merge builders
    ).sb.toString();

System.out.println(result);  // Output: "Java is powerful"
```

**Why StringBuilder?** String concatenation with `+=` creates many intermediate String objects. StringBuilder is mutable and much more efficient.

---

## Advanced Insights

### 1. **Why Not Just Use `reduce()`?**

`reduce()` is for **immutable reduction**:
```java
// reduce creates new Integer objects
int sum = Stream.of(1, 2, 3).reduce(0, Integer::sum);
```

`collect()` is for **mutable reduction**:
```java
// collect mutates one container
List<Integer> list = Stream.of(1, 2, 3)
    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
```

**Benefit:** Mutable reduction avoids creating intermediate objects, improving performance.

---

### 2. **Collector Requirements**

For correct parallel execution, the combiner must satisfy:
- **Associativity:** `combiner(a, combiner(b, c))` = `combiner(combiner(a, b), c)`
- **Identity:** Combining with an empty container doesn't change the result

---

### 3. **Built-in Collectors vs Custom**

Your code uses the **low-level** three-argument form. Java provides high-level collectors:

```java
// Instead of custom accumulator for summing:
int sum = Stream.of(1, 2, 3).collect(Collectors.summingInt(i -> i));

// Instead of custom accumulator for strings:
String joined = Stream.of("a", "b", "c").collect(Collectors.joining());
```

Use custom collectors when built-in ones don't fit your needs.

---

## Conclusion

The three components work together:
1. **Supplier** creates containers
2. **Accumulator** fills containers with stream elements
3. **Combiner** merges containers in parallel execution

The stream element type (Integer vs String) determines what operations make sense in the accumulator. The container type should match what you're collecting—numeric containers for sums, string containers for concatenation, or more complex structures for sophisticated aggregations.

Understanding these mechanics unlocks powerful custom aggregation operations beyond what built-in collectors provide.