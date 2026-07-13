# Java Stream Mapping Operations: Complete Guide

## Table of Contents
1. [Basic map() Operation](#1-basic-map-operation)
2. [Primitive Stream Mapping](#2-primitive-stream-mapping)
3. [flatMap() Operation](#3-flatmap-operation)
4. [map() vs flatMap() Comparison](#4-map-vs-flatmap-comparison)
5. [Real-World Examples](#5-real-world-examples)

---

## 1. Basic map() Operation

### Definition
`map()` transforms each element of a stream using a mapper function. It's a **one-to-one** transformation.

**Signature:** `<R> Stream<R> map(Function<T, R> mapper)`

### Example 1: Transform Strings to Uppercase
```java
List<String> names = Arrays.asList("alice", "bob", "charlie");

List<String> upperNames = names.stream()
    .map(String::toUpperCase)
    .collect(Collectors.toList());

System.out.println(upperNames);
// Output: [ALICE, BOB, CHARLIE]
```

### Example 2: Extract Object Properties
```java
class Student {
    private String name;
    private int age;
    
    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    public String getName() { return name; }
    public int getAge() { return age; }
}

List<Student> students = Arrays.asList(
    new Student("John", 20),
    new Student("Emma", 22),
    new Student("Mike", 19)
);

List<String> studentNames = students.stream()
    .map(Student::getName)
    .collect(Collectors.toList());

System.out.println(studentNames);
// Output: [John, Emma, Mike]
```

### Example 3: Complex Transformation
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

List<String> result = numbers.stream()
    .map(n -> "Number: " + (n * n))
    .collect(Collectors.toList());

System.out.println(result);
// Output: [Number: 1, Number: 4, Number: 9, Number: 16, Number: 25]
```

---

## 2. Primitive Stream Mapping

### Why Primitive Streams?
Primitive streams (`IntStream`, `LongStream`, `DoubleStream`) avoid boxing/unboxing overhead, improving performance.

### mapToInt() Example
```java
List<String> words = Arrays.asList("Java", "Python", "C++", "JavaScript");

IntStream lengths = words.stream()
    .mapToInt(String::length);

lengths.forEach(System.out::println);
// Output: 4, 6, 3, 10
```

### mapToLong() Example
```java
List<Integer> numbers = Arrays.asList(100, 200, 300);

long sum = numbers.stream()
    .mapToLong(n -> n * 1000L)
    .sum();

System.out.println(sum);
// Output: 600000
```

### mapToDouble() Example
```java
class Product {
    private String name;
    private double price;
    
    public Product(String name, double price) {
        this.name = name;
        this.price = price;
    }
    
    public double getPrice() { return price; }
}

List<Product> products = Arrays.asList(
    new Product("Laptop", 999.99),
    new Product("Mouse", 29.99),
    new Product("Keyboard", 79.99)
);

double avgPrice = products.stream()
    .mapToDouble(Product::getPrice)
    .average()
    .orElse(0.0);

System.out.println("Average: $" + avgPrice);
// Output: Average: $369.99
```

---

## 3. flatMap() Operation

### Definition
`flatMap()` transforms each element into a **stream** and then flattens all streams into a single stream. It's a **one-to-many** transformation.

**Signature:** `<R> Stream<R> flatMap(Function<T, Stream<R>> mapper)`

### Example 1: Flattening Lists
```java
List<List<String>> listOfLists = Arrays.asList(
    Arrays.asList("a", "b"),
    Arrays.asList("c", "d", "e"),
    Arrays.asList("f")
);

List<String> flatList = listOfLists.stream()
    .flatMap(Collection::stream)
    .collect(Collectors.toList());

System.out.println(flatList);
// Output: [a, b, c, d, e, f]
```

### Example 2: Split Strings into Words
```java
List<String> sentences = Arrays.asList(
    "Hello World",
    "Java Streams",
    "FlatMap Example"
);

List<String> words = sentences.stream()
    .flatMap(sentence -> Arrays.stream(sentence.split(" ")))
    .collect(Collectors.toList());

System.out.println(words);
// Output: [Hello, World, Java, Streams, FlatMap, Example]
```

### Example 3: Nested Objects
```java
class Department {
    private String name;
    private List<String> employees;
    
    public Department(String name, List<String> employees) {
        this.name = name;
        this.employees = employees;
    }
    
    public List<String> getEmployees() { return employees; }
}

List<Department> departments = Arrays.asList(
    new Department("IT", Arrays.asList("Alice", "Bob")),
    new Department("HR", Arrays.asList("Charlie", "Diana", "Eve")),
    new Department("Sales", Arrays.asList("Frank"))
);

List<String> allEmployees = departments.stream()
    .flatMap(dept -> dept.getEmployees().stream())
    .collect(Collectors.toList());

System.out.println(allEmployees);
// Output: [Alice, Bob, Charlie, Diana, Eve, Frank]
```

### Example 4: Multiple Courses per Student
```java
class Student {
    private String name;
    private List<String> courses;
    
    public Student(String name, List<String> courses) {
        this.name = name;
        this.courses = courses;
    }
    
    public List<String> getCourses() { return courses; }
}

List<Student> students = Arrays.asList(
    new Student("John", Arrays.asList("Math", "Physics")),
    new Student("Emma", Arrays.asList("Chemistry", "Biology", "Math")),
    new Student("Mike", Arrays.asList("Physics"))
);

List<String> allCourses = students.stream()
    .flatMap(student -> student.getCourses().stream())
    .distinct()
    .collect(Collectors.toList());

System.out.println(allCourses);
// Output: [Math, Physics, Chemistry, Biology]
```

### Example 5: Optional Values
```java
List<Optional<String>> optionals = Arrays.asList(
    Optional.of("Apple"),
    Optional.empty(),
    Optional.of("Banana"),
    Optional.empty(),
    Optional.of("Cherry")
);

List<String> values = optionals.stream()
    .flatMap(Optional::stream)  // Java 9+
    .collect(Collectors.toList());

System.out.println(values);
// Output: [Apple, Banana, Cherry]
```

### Example 6: Character Stream from String
```java
List<String> words = Arrays.asList("Java", "Stream");

List<Character> chars = words.stream()
    .flatMap(word -> word.chars()
        .mapToObj(c -> (char) c))
    .collect(Collectors.toList());

System.out.println(chars);
// Output: [J, a, v, a, S, t, r, e, a, m]
```

---

## 4. map() vs flatMap() Comparison

### Key Differences

| Aspect | map() | flatMap() |
|--------|-------|-----------|
| **Transformation** | One-to-one | One-to-many |
| **Input** | Takes a Function | Takes a Function that returns Stream |
| **Output** | Stream of transformed elements | Flattened stream |
| **Use Case** | Simple transformations | Nested structures, collections |

### Visual Comparison Example

```java
// Dataset: Students with enrolled courses
class Student {
    private String name;
    private List<String> courses;
    
    public Student(String name, List<String> courses) {
        this.name = name;
        this.courses = courses;
    }
    
    public List<String> getCourses() { return courses; }
}

List<Student> students = Arrays.asList(
    new Student("Alice", Arrays.asList("Math", "Physics")),
    new Student("Bob", Arrays.asList("Chemistry"))
);

// Using map() - Returns Stream<List<String>>
List<List<String>> withMap = students.stream()
    .map(Student::getCourses)
    .collect(Collectors.toList());

System.out.println("With map(): " + withMap);
// Output: With map(): [[Math, Physics], [Chemistry]]
// Note: We get nested lists!

// Using flatMap() - Returns Stream<String>
List<String> withFlatMap = students.stream()
    .flatMap(student -> student.getCourses().stream())
    .collect(Collectors.toList());

System.out.println("With flatMap(): " + withFlatMap);
// Output: With flatMap(): [Math, Physics, Chemistry]
// Note: We get a flat list!
```

### When to Use Which?

**Use `map()` when:**
- You need one-to-one transformation
- Each element maps to exactly one result
- You're extracting properties or converting types

**Use `flatMap()` when:**
- You have nested collections/streams
- Each element can produce multiple results
- You need to flatten hierarchical data
- Working with Optional, arrays, or collections within elements

---

## 5. Real-World Examples

### Example 1: E-commerce Order Processing
```java
class Order {
    private String orderId;
    private List<String> items;
    
    public Order(String orderId, List<String> items) {
        this.orderId = orderId;
        this.items = items;
    }
    
    public List<String> getItems() { return items; }
}

List<Order> orders = Arrays.asList(
    new Order("ORD1", Arrays.asList("Laptop", "Mouse")),
    new Order("ORD2", Arrays.asList("Keyboard")),
    new Order("ORD3", Arrays.asList("Monitor", "Cable", "Laptop"))
);

// Get all unique items ordered
Set<String> allItems = orders.stream()
    .flatMap(order -> order.getItems().stream())
    .collect(Collectors.toSet());

System.out.println("All items: " + allItems);
// Output: All items: [Laptop, Mouse, Keyboard, Monitor, Cable]

// Count how many times "Laptop" was ordered
long laptopCount = orders.stream()
    .flatMap(order -> order.getItems().stream())
    .filter(item -> item.equals("Laptop"))
    .count();

System.out.println("Laptops ordered: " + laptopCount);
// Output: Laptops ordered: 2
```

### Example 2: Social Network Analysis
```java
class Person {
    private String name;
    private List<String> friends;
    
    public Person(String name, List<String> friends) {
        this.name = name;
        this.friends = friends;
    }
    
    public String getName() { return name; }
    public List<String> getFriends() { return friends; }
}

List<Person> people = Arrays.asList(
    new Person("Alice", Arrays.asList("Bob", "Charlie")),
    new Person("Bob", Arrays.asList("Alice", "Diana")),
    new Person("Charlie", Arrays.asList("Eve"))
);

// Find all unique people in the network (including friends)
Set<String> allPeople = people.stream()
    .flatMap(person -> Stream.concat(
        Stream.of(person.getName()),
        person.getFriends().stream()
    ))
    .collect(Collectors.toSet());

System.out.println("Network size: " + allPeople);
// Output: Network size: [Alice, Bob, Charlie, Diana, Eve]
```

### Example 3: File Processing
```java
List<String> filePaths = Arrays.asList(
    "data1.txt:100,200,300",
    "data2.txt:400,500",
    "data3.txt:600,700,800,900"
);

// Extract all numbers from all files
List<Integer> allNumbers = filePaths.stream()
    .map(path -> path.split(":")[1])      // Get data part
    .flatMap(data -> Arrays.stream(data.split(",")))  // Split by comma
    .map(Integer::parseInt)
    .collect(Collectors.toList());

System.out.println("All numbers: " + allNumbers);
// Output: All numbers: [100, 200, 300, 400, 500, 600, 700, 800, 900]

int sum = allNumbers.stream().mapToInt(Integer::intValue).sum();
System.out.println("Sum: " + sum);
// Output: Sum: 4500
```

### Example 4: Course Enrollment System
```java
class Course {
    private String title;
    private List<String> topics;
    
    public Course(String title, List<String> topics) {
        this.title = title;
        this.topics = topics;
    }
    
    public String getTitle() { return title; }
    public List<String> getTopics() { return topics; }
}

class Student {
    private String name;
    private List<Course> enrolledCourses;
    
    public Student(String name, List<Course> courses) {
        this.name = name;
        this.enrolledCourses = courses;
    }
    
    public List<Course> getEnrolledCourses() { return enrolledCourses; }
}

List<Student> students = Arrays.asList(
    new Student("John", Arrays.asList(
        new Course("Java", Arrays.asList("OOP", "Streams", "Lambda")),
        new Course("Python", Arrays.asList("ML", "Data Science"))
    )),
    new Student("Emma", Arrays.asList(
        new Course("Java", Arrays.asList("OOP", "Streams", "Lambda"))
    ))
);

// Get all distinct topics across all students and courses
List<String> allTopics = students.stream()
    .flatMap(student -> student.getEnrolledCourses().stream())
    .flatMap(course -> course.getTopics().stream())
    .distinct()
    .sorted()
    .collect(Collectors.toList());

System.out.println("All topics: " + allTopics);
// Output: All topics: [Data Science, Lambda, ML, OOP, Streams]
```

---

## Summary

### Key Takeaways

1. **map()**: Use for simple one-to-one transformations
   - Transform objects
   - Extract properties
   - Convert types

2. **mapToInt/Long/Double()**: Use for primitive conversions
   - Better performance
   - Avoid boxing overhead
   - Access specialized operations (sum, average, etc.)

3. **flatMap()**: Use for flattening nested structures
   - Flatten collections
   - Process nested objects
   - Handle one-to-many relationships
   - Combine multiple streams

### The Golden Rule
- If your transformation produces **one result per element**, use `map()`
- If your transformation produces **zero or more results per element** (a collection/stream), use `flatMap()`

---

## Practice Exercise

Try this challenge to test your understanding:

```java
// Given a list of sentences, find all unique words longer than 3 characters
List<String> sentences = Arrays.asList(
    "Java streams are powerful",
    "FlatMap flattens nested structures",
    "Map transforms elements"
);

// Solution:
List<String> result = sentences.stream()
    .flatMap(sentence -> Arrays.stream(sentence.split(" ")))
    .filter(word -> word.length() > 3)
    .map(String::toLowerCase)
    .distinct()
    .sorted()
    .collect(Collectors.toList());

System.out.println(result);
// Output: [elements, flatmap, flattens, java, nested, powerful, streams, structures, transforms]
```

Happy streaming! 🚀