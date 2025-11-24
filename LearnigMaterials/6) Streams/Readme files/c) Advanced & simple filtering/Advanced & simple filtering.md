# Java Stream filter() with Method References - Complete Guide

## Overview

The `filter()` method is an **intermediate operation** in Java Streams that allows you to select elements from a stream based on a given condition (predicate). It returns a new stream containing only the elements that match the predicate.

**Method Signature:**
```java
Stream<T> filter(Predicate<T> predicate)
```

---

## What is a Method Reference?

A **method reference** is a shorthand notation for calling an existing method. Instead of writing a lambda expression like `(x) -> x.someMethod()`, you can write `ClassName::someMethod`.

### Method Reference Syntax

There are **four types** of method references:

1. **Static Method Reference**: `ClassName::staticMethod`
2. **Instance Method Reference (Bound)**: `object::instanceMethod`
3. **Instance Method Reference (Unbound)**: `ClassName::instanceMethod`
4. **Constructor Reference**: `ClassName::new`

For `filter()`, we typically use types 2 and 3.

---

## Type 1: Instance Method Reference (Unbound)

### Syntax: `ClassName::methodName`

This is used when calling an instance method on each object in the stream. The method must:
- Return a boolean (or Boolean)
- Take no parameters (besides the implicit `this`)

### Example 1: Basic Boolean Method

```java
import java.util.List;

class Student {
    private String name;
    private boolean female;
    
    public Student(String name, boolean female) {
        this.name = name;
        this.female = female;
    }
    
    public boolean isFemale() {
        return female;
    }
    
    @Override
    public String toString() {
        return name + (female ? " (F)" : " (M)");
    }
}

public class UnboundMethodReferenceExample1 {
    public static void main(String[] args) {
        List<Student> students = List.of(
            new Student("Alice", true),
            new Student("Bob", false),
            new Student("Carol", true),
            new Student("Dave", false)
        );
        
        System.out.println("Female students:");
        students.stream()
                .filter(Student::isFemale)  // Unbound method reference
                .forEach(System.out::println);
    }
}
```

**Output:**
```
Female students:
Alice (F)
Carol (F)
```

**How it works:**
- `Student::isFemale` is equivalent to `student -> student.isFemale()`
- For each Student object in the stream, Java calls the `isFemale()` method
- If it returns `true`, the student passes through the filter

---

### Example 2: Multiple Boolean Methods

```java
import java.util.List;

class Employee {
    private String name;
    private boolean active;
    private boolean manager;
    private int yearsOfService;
    
    public Employee(String name, boolean active, boolean manager, int years) {
        this.name = name;
        this.active = active;
        this.manager = manager;
        this.yearsOfService = years;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public boolean isManager() {
        return manager;
    }
    
    public boolean isSenior() {
        return yearsOfService >= 5;
    }
    
    @Override
    public String toString() {
        return name + " (Active: " + active + ", Manager: " + manager + 
               ", Years: " + yearsOfService + ")";
    }
}

public class UnboundMethodReferenceExample2 {
    public static void main(String[] args) {
        List<Employee> employees = List.of(
            new Employee("John", true, false, 3),
            new Employee("Sarah", true, true, 7),
            new Employee("Mike", false, false, 2),
            new Employee("Lisa", true, true, 6),
            new Employee("Tom", true, false, 8)
        );
        
        System.out.println("Active employees:");
        employees.stream()
                 .filter(Employee::isActive)
                 .forEach(System.out::println);
        
        System.out.println("\nActive managers:");
        employees.stream()
                 .filter(Employee::isActive)
                 .filter(Employee::isManager)
                 .forEach(System.out::println);
        
        System.out.println("\nSenior employees:");
        employees.stream()
                 .filter(Employee::isSenior)
                 .forEach(System.out::println);
    }
}
```

**Output:**
```
Active employees:
John (Active: true, Manager: false, Years: 3)
Sarah (Active: true, Manager: true, Years: 7)
Lisa (Active: true, Manager: true, Years: 6)
Tom (Active: true, Manager: false, Years: 8)

Active managers:
Sarah (Active: true, Manager: true, Years: 7)
Lisa (Active: true, Manager: true, Years: 6)

Senior employees:
Sarah (Active: true, Manager: true, Years: 7)
Lisa (Active: true, Manager: true, Years: 6)
Tom (Active: true, Manager: false, Years: 8)
```

**Explanation:**
- Each `filter()` uses a method reference to a different boolean method
- You can chain multiple filters with different method references
- Each method reference (`Employee::isActive`, `Employee::isManager`, etc.) is called on each Employee object

---

### Example 3: String Methods

```java
import java.util.List;

public class StringMethodReferenceExample {
    public static void main(String[] args) {
        List<String> words = List.of("", "Hello", "", "World", "Java", "", "Stream");
        
        System.out.println("Non-empty strings:");
        words.stream()
             .filter(String::isEmpty)  // Keep empty strings
             .forEach(s -> System.out.println("(empty)"));
        
        System.out.println("\nNon-empty strings (negated):");
        words.stream()
             .filter(((java.util.function.Predicate<String>) String::isEmpty).negate())
             .forEach(System.out::println);
        
        // Better approach: create a helper method
        System.out.println("\nUsing custom isNotEmpty:");
        words.stream()
             .filter(StringMethodReferenceExample::isNotEmpty)
             .forEach(System.out::println);
    }
    
    private static boolean isNotEmpty(String s) {
        return !s.isEmpty();
    }
}
```

**Output:**
```
Non-empty strings:
(empty)
(empty)
(empty)

Non-empty strings (negated):
Hello
World
Java
Stream

Using custom isNotEmpty:
Hello
World
Java
Stream
```

---

## Type 2: Instance Method Reference (Bound)

### Syntax: `object::methodName`

This is used when you have a **specific object instance** and want to call one of its methods. The method is "bound" to that particular object.

### Example 4: Using a Specific Object's Method

```java
import java.util.List;

class StringValidator {
    private int minLength;
    
    public StringValidator(int minLength) {
        this.minLength = minLength;
    }
    
    public boolean isValid(String s) {
        return s != null && s.length() >= minLength;
    }
    
    public boolean hasVowel(String s) {
        return s.matches(".*[aeiouAEIOU].*");
    }
}

public class BoundMethodReferenceExample1 {
    public static void main(String[] args) {
        List<String> words = List.of("Hi", "Hello", "Sky", "World", "By", "Java");
        
        StringValidator validator = new StringValidator(4);
        
        System.out.println("Words with length >= 4:");
        words.stream()
             .filter(validator::isValid)  // Bound to the validator object
             .forEach(System.out::println);
        
        System.out.println("\nWords with vowels:");
        words.stream()
             .filter(validator::hasVowel)
             .forEach(System.out::println);
    }
}
```

**Output:**
```
Words with length >= 4:
Hello
World
Java

Words with vowels:
Hello
World
Java
```

**Explanation:**
- `validator::isValid` is bound to the specific `validator` object
- The `minLength` value (4) is captured from that object
- Each string is passed to `validator.isValid(string)`

---

### Example 5: Filtering with Configuration Object

```java
import java.util.List;

class Product {
    private String name;
    private double price;
    private String category;
    
    public Product(String name, double price, String category) {
        this.name = name;
        this.price = price;
        this.category = category;
    }
    
    public double getPrice() { return price; }
    public String getCategory() { return category; }
    
    @Override
    public String toString() {
        return name + " ($" + price + ", " + category + ")";
    }
}

class PriceFilter {
    private double maxPrice;
    
    public PriceFilter(double maxPrice) {
        this.maxPrice = maxPrice;
    }
    
    public boolean isAffordable(Product product) {
        return product.getPrice() <= maxPrice;
    }
}

class CategoryFilter {
    private String targetCategory;
    
    public CategoryFilter(String category) {
        this.targetCategory = category;
    }
    
    public boolean matches(Product product) {
        return product.getCategory().equals(targetCategory);
    }
}

public class BoundMethodReferenceExample2 {
    public static void main(String[] args) {
        List<Product> products = List.of(
            new Product("Laptop", 899.99, "Electronics"),
            new Product("Mouse", 19.99, "Electronics"),
            new Product("Desk", 299.99, "Furniture"),
            new Product("Chair", 149.99, "Furniture"),
            new Product("Keyboard", 49.99, "Electronics")
        );
        
        PriceFilter cheapFilter = new PriceFilter(100);
        CategoryFilter electronicsFilter = new CategoryFilter("Electronics");
        
        System.out.println("Affordable products (under $100):");
        products.stream()
                .filter(cheapFilter::isAffordable)
                .forEach(System.out::println);
        
        System.out.println("\nElectronics:");
        products.stream()
                .filter(electronicsFilter::matches)
                .forEach(System.out::println);
        
        System.out.println("\nAffordable Electronics:");
        products.stream()
                .filter(cheapFilter::isAffordable)
                .filter(electronicsFilter::matches)
                .forEach(System.out::println);
    }
}
```

**Output:**
```
Affordable products (under $100):
Mouse ($19.99, Electronics)
Keyboard ($49.99, Electronics)

Electronics:
Laptop ($899.99, Electronics)
Mouse ($19.99, Electronics)
Keyboard ($49.99, Electronics)

Affordable Electronics:
Mouse ($19.99, Electronics)
Keyboard ($49.99, Electronics)
```

**Explanation:**
- `cheapFilter::isAffordable` is bound to the `cheapFilter` object (maxPrice = 100)
- `electronicsFilter::matches` is bound to the `electronicsFilter` object
- Each filter object maintains its own state (maxPrice or targetCategory)
- You can create multiple filter objects with different configurations

---

### Example 6: Using Current User Context

```java
import java.util.List;

class Document {
    private String title;
    private String owner;
    private boolean isPublic;
    
    public Document(String title, String owner, boolean isPublic) {
        this.title = title;
        this.owner = owner;
        this.isPublic = isPublic;
    }
    
    public String getOwner() { return owner; }
    public boolean isPublic() { return isPublic; }
    
    @Override
    public String toString() {
        return title + " (Owner: " + owner + ", Public: " + isPublic + ")";
    }
}

class User {
    private String username;
    
    public User(String username) {
        this.username = username;
    }
    
    public boolean canAccess(Document doc) {
        return doc.isPublic() || doc.getOwner().equals(username);
    }
    
    public boolean owns(Document doc) {
        return doc.getOwner().equals(username);
    }
}

public class BoundMethodReferenceExample3 {
    public static void main(String[] args) {
        List<Document> documents = List.of(
            new Document("Report.pdf", "alice", false),
            new Document("Public_Info.pdf", "bob", true),
            new Document("Notes.txt", "alice", false),
            new Document("Guidelines.pdf", "admin", true),
            new Document("Private_Data.pdf", "bob", false)
        );
        
        User alice = new User("alice");
        User bob = new User("bob");
        
        System.out.println("Documents Alice can access:");
        documents.stream()
                 .filter(alice::canAccess)
                 .forEach(System.out::println);
        
        System.out.println("\nDocuments Alice owns:");
        documents.stream()
                 .filter(alice::owns)
                 .forEach(System.out::println);
        
        System.out.println("\nDocuments Bob can access:");
        documents.stream()
                 .filter(bob::canAccess)
                 .forEach(System.out::println);
    }
}
```

**Output:**
```
Documents Alice can access:
Report.pdf (Owner: alice, Public: false)
Public_Info.pdf (Owner: bob, Public: true)
Notes.txt (Owner: alice, Public: false)
Guidelines.pdf (Owner: admin, Public: true)

Documents Alice owns:
Report.pdf (Owner: alice, Public: false)
Notes.txt (Owner: alice, Public: false)

Documents Bob can access:
Public_Info.pdf (Owner: bob, Public: true)
Guidelines.pdf (Owner: admin, Public: true)
Private_Data.pdf (Owner: bob, Public: false)
```

**Explanation:**
- `alice::canAccess` is bound to the `alice` User object
- The method uses Alice's username to determine document access
- Different users can use the same method reference pattern with different results

---

## Method Reference vs Lambda: When to Use Each

### Use Method Reference When:

✅ A method already exists that does exactly what you need
✅ The method is clear and well-named
✅ You want cleaner, more readable code
✅ The method is reusable in other contexts

### Use Lambda When:

✅ You need custom logic that doesn't exist as a method
✅ The logic is simple and doesn't warrant a separate method
✅ You need to combine multiple conditions
✅ You're performing a one-off operation

---

## Comparison Table

| Aspect | Method Reference | Lambda Equivalent |
|--------|------------------|-------------------|
| Unbound | `Student::isFemale` | `student -> student.isFemale()` |
| Bound | `validator::isValid` | `str -> validator.isValid(str)` |
| Readability | Very concise | More explicit |
| Debugging | Can be harder to trace | Easier to add breakpoints |

---

## Key Points to Remember

1. **Method references are syntactic sugar** - They're converted to lambda expressions by the compiler

2. **Unbound references** (`ClassName::method`) call the method on each stream element

3. **Bound references** (`object::method`) call the method on a specific object, passing stream elements as arguments

4. **The method must return boolean** - For `filter()`, the referenced method must return `boolean` or `Boolean`

5. **No parameters** - For unbound references, the method takes no parameters (besides implicit `this`)

6. **One parameter** - For bound references, the method takes one parameter (the stream element)

7. **State in bound references** - Bound references can access the state of the object they're bound to

---

## Common Patterns

### Pattern 1: Chaining Multiple Predicate Methods
```java
list.stream()
    .filter(Item::isValid)
    .filter(Item::isAvailable)
    .filter(Item::isAffordable)
    .collect(Collectors.toList());
```

### Pattern 2: Using Configuration Objects
```java
Config config = new Config(minValue, maxValue);
list.stream()
    .filter(config::meetsRequirements)
    .collect(Collectors.toList());
```

### Pattern 3: Using Context Objects
```java
User currentUser = getCurrentUser();
documents.stream()
         .filter(currentUser::hasPermission)
         .collect(Collectors.toList());
```

---

## Best Practices

1. ✅ **Name methods descriptively** - `isValid()`, `isActive()`, `canAccess()`
2. ✅ **Keep methods focused** - Each method should check one condition
3. ✅ **Use bound references for stateful filtering** - When filter depends on configuration
4. ✅ **Chain multiple method references** - For multiple conditions
5. ❌ **Don't force method references** - Use lambdas if they're clearer