# Complete Guide to Java Functional Interfaces

## What is a Functional Interface?

A **functional interface** is an interface with exactly **one abstract method**. These interfaces are the foundation of lambda expressions and functional programming in Java.

### Key Characteristics:
- Contains exactly one abstract method
- Can be implemented using lambda expressions
- Pure functional semantics (no side effects)
- Result depends only on input arguments
- Available in `java.util.function` package

---

## 1. Function<T, R>

**Purpose:** Transforms an input of type T into an output of type R

**Method:** `R apply(T t)`

### Example: String Length Calculator

```java
import java.util.function.Function;

public class FunctionExample {
    public static void main(String[] args) {
        // Lambda expression: takes a String, returns its length
        Function<String, Integer> stringLength = str -> str.length();
        
        // Using the function
        Integer length = stringLength.apply("Hello World");
        System.out.println("Length: " + length);  // Output: 11
        
        // Another example: Convert string to uppercase
        Function<String, String> toUpperCase = str -> str.toUpperCase();
        String result = toUpperCase.apply("java");
        System.out.println("Uppercase: " + result);  // Output: JAVA
        
        // Chaining functions
        Function<Integer, Integer> multiplyBy2 = x -> x * 2;
        Function<Integer, Integer> add10 = x -> x + 10;
        
        // First multiply by 2, then add 10
        Integer chainResult = multiplyBy2.andThen(add10).apply(5);
        System.out.println("Chain result: " + chainResult);  // Output: 20
    }
}
```

**Explanation:** Function takes one input and produces one output. It's perfect for transformation operations like converting types, calculations, or data mapping.

---

## 2. BiFunction<T, U, R>

**Purpose:** Takes two inputs (T and U) and produces one output (R)

**Method:** `R apply(T t, U u)`

### Example: Calculator Operations

```java
import java.util.function.BiFunction;

public class BiFunctionExample {
    public static void main(String[] args) {
        // Lambda: takes two integers, returns their sum
        BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;
        
        Integer sum = add.apply(10, 20);
        System.out.println("Sum: " + sum);  // Output: 30
        
        // String concatenation with custom separator
        BiFunction<String, String, String> concat = 
            (str1, str2) -> str1 + " - " + str2;
        
        String combined = concat.apply("Java", "Programming");
        System.out.println(combined);  // Output: Java - Programming
        
        // Calculate power (base, exponent)
        BiFunction<Double, Double, Double> power = 
            (base, exponent) -> Math.pow(base, exponent);
        
        Double result = power.apply(2.0, 3.0);
        System.out.println("2^3 = " + result);  // Output: 8.0
        
        // Practical example: Calculate discounted price
        BiFunction<Double, Double, Double> applyDiscount = 
            (price, discountPercent) -> price - (price * discountPercent / 100);
        
        Double finalPrice = applyDiscount.apply(100.0, 15.0);
        System.out.println("Final price: $" + finalPrice);  // Output: $85.0
    }
}
```

**Explanation:** BiFunction is used when you need to combine or process two inputs to produce a single result. Common in mathematical operations, data merging, or business logic.

---

## 3. BinaryOperator<T>

**Purpose:** Takes two inputs of the SAME type and returns a result of the SAME type

**Method:** `T apply(T t, T u)`

**Note:** BinaryOperator is a specialization of BiFunction where all types are the same.

### Example: Mathematical Operations

```java
import java.util.function.BinaryOperator;

public class BinaryOperatorExample {
    public static void main(String[] args) {
        // Find maximum of two numbers
        BinaryOperator<Integer> max = (a, b) -> a > b ? a : b;
        
        Integer maximum = max.apply(15, 25);
        System.out.println("Max: " + maximum);  // Output: 25
        
        // Find minimum of two numbers
        BinaryOperator<Integer> min = (a, b) -> a < b ? a : b;
        
        Integer minimum = min.apply(15, 25);
        System.out.println("Min: " + minimum);  // Output: 15
        
        // String concatenation
        BinaryOperator<String> concatenate = (s1, s2) -> s1 + s2;
        
        String fullName = concatenate.apply("John", "Doe");
        System.out.println("Full name: " + fullName);  // Output: JohnDoe
        
        // Practical example: Merge two lists
        BinaryOperator<String> mergeWithComma = 
            (list1, list2) -> list1 + ", " + list2;
        
        String merged = mergeWithComma.apply("Apple, Banana", "Orange, Grape");
        System.out.println("Merged: " + merged);
        // Output: Apple, Banana, Orange, Grape
        
        // Using with streams (reduce operation)
        java.util.stream.Stream<Integer> numbers = 
            java.util.stream.Stream.of(1, 2, 3, 4, 5);
        
        BinaryOperator<Integer> sum = (a, b) -> a + b;
        Integer total = numbers.reduce(0, sum);
        System.out.println("Total: " + total);  // Output: 15
    }
}
```

**Explanation:** BinaryOperator is perfect when both inputs and output share the same type. Common in reduction operations, comparisons, and aggregations.

---

## 4. UnaryOperator<T>

**Purpose:** Takes one input of type T and returns a result of the SAME type T

**Method:** `T apply(T t)`

**Note:** UnaryOperator is a specialization of Function where input and output types are the same.

### Example: Transformation Operations

```java
import java.util.function.UnaryOperator;

public class UnaryOperatorExample {
    public static void main(String[] args) {
        // Square a number
        UnaryOperator<Integer> square = x -> x * x;
        
        Integer squared = square.apply(5);
        System.out.println("Square: " + squared);  // Output: 25
        
        // Convert string to uppercase
        UnaryOperator<String> toUpper = str -> str.toUpperCase();
        
        String upper = toUpper.apply("hello");
        System.out.println("Uppercase: " + upper);  // Output: HELLO
        
        // Add prefix to string
        UnaryOperator<String> addPrefix = str -> "Dr. " + str;
        
        String withPrefix = addPrefix.apply("Smith");
        System.out.println(withPrefix);  // Output: Dr. Smith
        
        // Double a number
        UnaryOperator<Double> doubleValue = x -> x * 2;
        
        Double doubled = doubleValue.apply(3.5);
        System.out.println("Doubled: " + doubled);  // Output: 7.0
        
        // Practical example: Apply 10% increase
        UnaryOperator<Double> increaseBy10Percent = price -> price * 1.10;
        
        Double newPrice = increaseBy10Percent.apply(100.0);
        System.out.println("New price: $" + newPrice);  // Output: $110.0
        
        // Chain operations
        UnaryOperator<String> trim = str -> str.trim();
        UnaryOperator<String> toLowerCase = str -> str.toLowerCase();
        
        String processed = trim.andThen(toLowerCase).apply("  HELLO WORLD  ");
        System.out.println("Processed: '" + processed + "'");
        // Output: 'hello world'
    }
}
```

**Explanation:** UnaryOperator is ideal for transformations where the type doesn't change, like mathematical operations on numbers or string manipulations.

---

## 5. Predicate<T>

**Purpose:** Tests a condition on input T and returns a boolean

**Method:** `boolean test(T t)`

### Example: Condition Testing

```java
import java.util.function.Predicate;
import java.util.Arrays;
import java.util.List;

public class PredicateExample {
    public static void main(String[] args) {
        // Check if number is even
        Predicate<Integer> isEven = num -> num % 2 == 0;
        
        System.out.println("Is 4 even? " + isEven.test(4));    // true
        System.out.println("Is 7 even? " + isEven.test(7));    // false
        
        // Check if string is empty
        Predicate<String> isEmpty = str -> str.isEmpty();
        
        System.out.println("Is '' empty? " + isEmpty.test(""));        // true
        System.out.println("Is 'Java' empty? " + isEmpty.test("Java")); // false
        
        // Check if number is positive
        Predicate<Integer> isPositive = num -> num > 0;
        
        System.out.println("Is 5 positive? " + isPositive.test(5));    // true
        System.out.println("Is -3 positive? " + isPositive.test(-3));  // false
        
        // Combining predicates with AND
        Predicate<Integer> isEvenAndPositive = isEven.and(isPositive);
        
        System.out.println("Is 4 even and positive? " + 
            isEvenAndPositive.test(4));   // true
        System.out.println("Is -4 even and positive? " + 
            isEvenAndPositive.test(-4));  // false
        
        // Combining predicates with OR
        Predicate<Integer> isEvenOrPositive = isEven.or(isPositive);
        
        System.out.println("Is -4 even or positive? " + 
            isEvenOrPositive.test(-4));   // true (it's even)
        
        // Negating a predicate
        Predicate<Integer> isOdd = isEven.negate();
        
        System.out.println("Is 5 odd? " + isOdd.test(5));  // true
        
        // Practical example: Filter a list
        List<String> names = Arrays.asList("Alice", "Bob", "Alexander", "Anna");
        
        Predicate<String> startsWithA = name -> name.startsWith("A");
        
        System.out.println("\nNames starting with 'A':");
        names.stream()
             .filter(startsWithA)
             .forEach(System.out::println);
        // Output: Alice, Alexander, Anna
        
        // Filter numbers
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        System.out.println("\nEven numbers:");
        numbers.stream()
               .filter(isEven)
               .forEach(System.out::println);
        // Output: 2, 4, 6, 8, 10
    }
}
```

**Explanation:** Predicate is used for filtering and validation. It's essential in stream operations, conditional logic, and data validation scenarios.

---

## 6. Consumer<T>

**Purpose:** Accepts an input and performs an action, returns nothing (void)

**Method:** `void accept(T t)`

### Example: Side-Effect Operations

```java
import java.util.function.Consumer;
import java.util.Arrays;
import java.util.List;

public class ConsumerExample {
    public static void main(String[] args) {
        // Print a string
        Consumer<String> printer = str -> System.out.println(str);
        
        printer.accept("Hello, World!");  // Output: Hello, World!
        
        // Print with custom format
        Consumer<Integer> printNumber = 
            num -> System.out.println("Number: " + num);
        
        printNumber.accept(42);  // Output: Number: 42
        
        // Modify object state (side effect)
        class Account {
            private double balance;
            
            public Account(double balance) {
                this.balance = balance;
            }
            
            public void setBalance(double balance) {
                this.balance = balance;
            }
            
            public double getBalance() {
                return balance;
            }
        }
        
        Account account = new Account(1000.0);
        
        Consumer<Account> addBonus = 
            acc -> acc.setBalance(acc.getBalance() + 100);
        
        System.out.println("Before: $" + account.getBalance());  // 1000.0
        addBonus.accept(account);
        System.out.println("After: $" + account.getBalance());   // 1100.0
        
        // Chain consumers
        Consumer<String> upperCase = str -> System.out.println(str.toUpperCase());
        Consumer<String> addPrefix = str -> System.out.println(">> " + str);
        
        Consumer<String> combined = upperCase.andThen(addPrefix);
        
        combined.accept("hello");
        // Output:
        // HELLO
        // >> hello
        
        // Practical example: Process list items
        List<String> fruits = Arrays.asList("Apple", "Banana", "Orange");
        
        Consumer<String> printWithIndex = fruit -> 
            System.out.println("- " + fruit);
        
        System.out.println("\nFruit list:");
        fruits.forEach(printWithIndex);
        // Output:
        // - Apple
        // - Banana
        // - Orange
        
        // Another example: Log messages
        Consumer<String> logger = message -> 
            System.out.println("[LOG] " + 
                java.time.LocalTime.now() + ": " + message);
        
        logger.accept("Application started");
        logger.accept("Processing data");
    }
}
```

**Explanation:** Consumer is used for operations that have side effects, like printing, logging, modifying objects, or sending data. It doesn't return anything.

---

## 7. BiConsumer<T, U>

**Purpose:** Accepts two inputs and performs an action, returns nothing (void)

**Method:** `void accept(T t, U u)`

### Example: Two-Parameter Side-Effect Operations

```java
import java.util.function.BiConsumer;
import java.util.HashMap;
import java.util.Map;

public class BiConsumerExample {
    public static void main(String[] args) {
        // Print two values
        BiConsumer<String, Integer> printKeyValue = 
            (key, value) -> System.out.println(key + " = " + value);
        
        printKeyValue.accept("Age", 25);  // Output: Age = 25
        printKeyValue.accept("Score", 95);  // Output: Score = 95
        
        // Add two numbers and print
        BiConsumer<Integer, Integer> printSum = 
            (a, b) -> System.out.println("Sum: " + (a + b));
        
        printSum.accept(10, 20);  // Output: Sum: 30
        
        // Update a map
        Map<String, Integer> inventory = new HashMap<>();
        
        BiConsumer<String, Integer> addToInventory = 
            (item, quantity) -> {
                inventory.put(item, 
                    inventory.getOrDefault(item, 0) + quantity);
                System.out.println("Added " + quantity + " " + item);
            };
        
        addToInventory.accept("Apples", 10);   // Added 10 Apples
        addToInventory.accept("Oranges", 5);   // Added 5 Oranges
        addToInventory.accept("Apples", 5);    // Added 5 Apples
        
        System.out.println("\nInventory: " + inventory);
        // Output: {Apples=15, Oranges=5}
        
        // Chain BiConsumers
        BiConsumer<String, String> printFirst = 
            (a, b) -> System.out.println("First: " + a);
        BiConsumer<String, String> printSecond = 
            (a, b) -> System.out.println("Second: " + b);
        
        BiConsumer<String, String> printBoth = printFirst.andThen(printSecond);
        
        printBoth.accept("Hello", "World");
        // Output:
        // First: Hello
        // Second: World
        
        // Practical example: Process map entries
        Map<String, Double> prices = new HashMap<>();
        prices.put("Laptop", 999.99);
        prices.put("Mouse", 29.99);
        prices.put("Keyboard", 79.99);
        
        BiConsumer<String, Double> printPrice = 
            (product, price) -> 
                System.out.printf("%s: $%.2f%n", product, price);
        
        System.out.println("\nPrice List:");
        prices.forEach(printPrice);
        // Output:
        // Laptop: $999.99
        // Mouse: $29.99
        // Keyboard: $79.99
        
        // Example: Logger with level and message
        BiConsumer<String, String> log = 
            (level, message) -> 
                System.out.println("[" + level + "] " + message);
        
        log.accept("INFO", "Application started");
        log.accept("WARNING", "Low memory");
        log.accept("ERROR", "Connection failed");
    }
}
```

**Explanation:** BiConsumer is perfect for operations that need two inputs but don't return anything, like updating maps, logging with context, or processing key-value pairs.

---

## 8. Supplier<T>

**Purpose:** Supplies (produces) a value without taking any input

**Method:** `T get()`

### Example: Value Generation

```java
import java.util.function.Supplier;
import java.util.Random;
import java.util.UUID;

public class SupplierExample {
    public static void main(String[] args) {
        // Supply a constant value
        Supplier<String> greeting = () -> "Hello, World!";
        
        System.out.println(greeting.get());  // Output: Hello, World!
        System.out.println(greeting.get());  // Output: Hello, World!
        
        // Supply current time
        Supplier<Long> currentTime = () -> System.currentTimeMillis();
        
        System.out.println("Time 1: " + currentTime.get());
        try { Thread.sleep(100); } catch (InterruptedException e) {}
        System.out.println("Time 2: " + currentTime.get());
        // Different values each time
        
        // Supply random number
        Random random = new Random();
        Supplier<Integer> randomNumber = () -> random.nextInt(100);
        
        System.out.println("Random 1: " + randomNumber.get());
        System.out.println("Random 2: " + randomNumber.get());
        System.out.println("Random 3: " + randomNumber.get());
        
        // Supply new objects
        Supplier<UUID> uuidSupplier = () -> UUID.randomUUID();
        
        System.out.println("UUID 1: " + uuidSupplier.get());
        System.out.println("UUID 2: " + uuidSupplier.get());
        
        // Supply default value
        Supplier<String> defaultName = () -> "Anonymous";
        
        String name = null;
        String displayName = (name != null) ? name : defaultName.get();
        System.out.println("Display name: " + displayName);
        // Output: Anonymous
        
        // Practical example: Lazy initialization
        class ExpensiveObject {
            public ExpensiveObject() {
                System.out.println("Creating expensive object...");
                // Simulate expensive operation
            }
            
            public void doSomething() {
                System.out.println("Doing something...");
            }
        }
        
        Supplier<ExpensiveObject> lazyObject = () -> new ExpensiveObject();
        
        System.out.println("\nBefore getting object");
        // Object not created yet
        
        ExpensiveObject obj = lazyObject.get();
        // Output: Creating expensive object...
        
        obj.doSomething();
        // Output: Doing something...
        
        // Factory pattern example
        Supplier<int[]> arrayFactory = () -> new int[10];
        
        int[] array1 = arrayFactory.get();
        int[] array2 = arrayFactory.get();
        
        System.out.println("\nArray 1 and 2 are different: " + 
            (array1 != array2));  // true
        
        // Supply configuration values
        Supplier<String> databaseUrl = () -> "jdbc:mysql://localhost:3306/mydb";
        Supplier<Integer> maxConnections = () -> 10;
        
        System.out.println("\nDB URL: " + databaseUrl.get());
        System.out.println("Max connections: " + maxConnections.get());
    }
}
```

**Explanation:** Supplier is used for lazy initialization, factory methods, generating values, and providing default values. It's called when you need a value but don't have any input to provide.

---

## Summary Comparison

| Interface | Input | Output | Use Case |
|-----------|-------|--------|----------|
| **Function<T,R>** | T | R | Transform one value to another type |
| **BiFunction<T,U,R>** | T, U | R | Combine two values into one result |
| **BinaryOperator<T>** | T, T | T | Combine two same-type values |
| **UnaryOperator<T>** | T | T | Transform value of same type |
| **Predicate<T>** | T | boolean | Test a condition |
| **Consumer<T>** | T | void | Perform action with input |
| **BiConsumer<T,U>** | T, U | void | Perform action with two inputs |
| **Supplier<T>** | none | T | Generate/supply a value |

---

## Key Benefits of Functional Interfaces

1. **Concise Code**: Lambda expressions make code shorter and more readable
2. **Functional Programming**: Enable functional programming paradigm in Java
3. **Stream API**: Work seamlessly with Java Streams
4. **No Side Effects**: Encourage pure functions and immutability
5. **Reusability**: Can be passed as parameters and reused
6. **Type Safety**: Strongly typed with compile-time checking

---

## Common Use Cases

- **Stream Operations**: `filter()`, `map()`, `reduce()`, `forEach()`
- **Collections**: Processing lists, sets, and maps
- **Event Handling**: GUI callbacks and event listeners
- **Asynchronous Programming**: CompletableFuture operations
- **Optional**: `map()`, `filter()`, `ifPresent()`, `orElseGet()`
- **Factory Patterns**: Creating objects on demand
- **Strategy Pattern**: Passing behavior as parameters

---

## Best Practices

1. Keep lambda expressions simple and readable
2. Use method references when possible: `String::toUpperCase`
3. Avoid side effects in pure functional interfaces
4. Don't overuse - traditional methods are fine too
5. Consider performance for frequently called operations
6. Use meaningful variable names in complex lambdas
7. Chain operations for better readability

---

*This guide covers all common functional interfaces in Java 8+. Practice these examples to master functional programming in Java!*