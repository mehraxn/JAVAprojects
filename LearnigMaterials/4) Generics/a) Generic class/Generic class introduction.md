# Complete Guide to Java Generic Classes

## Table of Contents
1. [Introduction](#introduction)
2. [What Are Generic Classes?](#what-are-generic-classes)
3. [Why Use Generics?](#why-use-generics)
4. [Basic Syntax](#basic-syntax)
5. [Detailed Examples](#detailed-examples)
6. [Type Parameters](#type-parameters)
7. [Generic Methods](#generic-methods)
8. [Bounded Type Parameters](#bounded-type-parameters)
9. [Multiple Type Parameters](#multiple-type-parameters)
10. [Type Erasure](#type-erasure)
11. [Wildcards](#wildcards)
12. [Best Practices](#best-practices)
13. [Common Pitfalls](#common-pitfalls)

---

## Introduction

Generics were introduced in Java 5 (2004) to provide stronger type checks at compile time and to eliminate the need for casting. They allow you to write more flexible, reusable, and type-safe code.

---

## What Are Generic Classes?

A **generic class** is a class that can operate on objects of various types while providing compile-time type safety. The type(s) are specified using **type parameters** (also called type variables) enclosed in angle brackets `<>`.

### Simple Analogy
Think of a generic class as a **blueprint with placeholders**. Instead of creating separate classes for storing integers, strings, or custom objects, you create one generic class where the type is specified when you use it.

---

## Why Use Generics?

### 1. **Type Safety**
Generics provide compile-time type checking, catching errors early rather than at runtime.

```java
// Without generics - runtime error possible
List list = new ArrayList();
list.add("Hello");
list.add(123);
String s = (String) list.get(1); // Runtime ClassCastException!

// With generics - compile-time error
List<String> list = new ArrayList<>();
list.add("Hello");
// list.add(123); // Compile error - type safety!
String s = list.get(0); // No casting needed
```

### 2. **Elimination of Casts**
No need for explicit type casting when retrieving elements.

### 3. **Code Reusability**
Write once, use with any type.

### 4. **Better Abstraction**
Enable programmers to implement generic algorithms that work on collections of different types.

---

## Basic Syntax

### Generic Class Declaration

```java
public class ClassName<T> {
    private T variable;
    
    public ClassName(T variable) {
        this.variable = variable;
    }
    
    public T getVariable() {
        return variable;
    }
    
    public void setVariable(T variable) {
        this.variable = variable;
    }
}
```

### Generic Class Usage

```java
// Declare with specific type
ClassName<String> stringInstance = new ClassName<>("Hello");
ClassName<Integer> intInstance = new ClassName<>(42);

// Java 7+ diamond operator - type inference
ClassName<String> instance = new ClassName<>("Hello");
```

---

## Detailed Examples

### Example 1: Simple Box Class

```java
// Generic Box that can hold any type
public class Box<T> {
    private T content;
    
    public Box(T content) {
        this.content = content;
    }
    
    public T getContent() {
        return content;
    }
    
    public void setContent(T content) {
        this.content = content;
    }
    
    public void displayContent() {
        System.out.println("Box contains: " + content);
    }
}

// Usage
public class BoxDemo {
    public static void main(String[] args) {
        // Box for Integer
        Box<Integer> intBox = new Box<>(123);
        System.out.println("Integer: " + intBox.getContent());
        
        // Box for String
        Box<String> strBox = new Box<>("Hello Generics");
        System.out.println("String: " + strBox.getContent());
        
        // Box for custom object
        Box<Person> personBox = new Box<>(new Person("Alice", 30));
        System.out.println("Person: " + personBox.getContent());
        
        // Type safety in action
        Integer num = intBox.getContent(); // No casting!
        // String str = intBox.getContent(); // Compile error!
    }
}
```

### Example 2: Pair Class (from your image)

```java
public class Pair<T> {
    private T a, b;
    
    public Pair(T a, T b) {
        this.a = a;
        this.b = b;
    }
    
    public T first() {
        return a;
    }
    
    public T second() {
        return b;
    }
    
    public void set1st(T x) {
        a = x;
    }
    
    public void set2nd(T x) {
        b = x;
    }
}

// Usage
public class PairDemo {
    public static void main(String[] args) {
        // Pair of Strings
        Pair<String> stringPair = new Pair<>("One", "Two");
        String first = stringPair.first(); // No cast needed!
        
        // Pair of Integers
        Pair<Integer> intPair = new Pair<>(1, 2);
        int firstNum = intPair.first(); // Auto-unboxing works!
        
        // Mixed type would cause compile error
        // Pair<String> mixPair = new Pair<>(1, "Two"); // Error!
    }
}
```

### Example 3: Generic Stack Implementation

```java
public class Stack<T> {
    private ArrayList<T> elements;
    
    public Stack() {
        elements = new ArrayList<>();
    }
    
    public void push(T item) {
        elements.add(item);
    }
    
    public T pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return elements.remove(elements.size() - 1);
    }
    
    public T peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return elements.get(elements.size() - 1);
    }
    
    public boolean isEmpty() {
        return elements.isEmpty();
    }
    
    public int size() {
        return elements.size();
    }
}

// Usage
public class StackDemo {
    public static void main(String[] args) {
        Stack<Integer> intStack = new Stack<>();
        intStack.push(10);
        intStack.push(20);
        intStack.push(30);
        
        System.out.println("Top: " + intStack.peek()); // 30
        System.out.println("Popped: " + intStack.pop()); // 30
        System.out.println("Size: " + intStack.size()); // 2
        
        Stack<String> strStack = new Stack<>();
        strStack.push("First");
        strStack.push("Second");
        System.out.println(strStack.pop()); // "Second"
    }
}
```

---

## Type Parameters

### Naming Conventions

By convention, type parameter names are single, uppercase letters:

- **T** - Type
- **E** - Element (used extensively by Java Collections)
- **K** - Key
- **V** - Value
- **N** - Number
- **S, U, V** - 2nd, 3rd, 4th types

```java
public class KeyValuePair<K, V> {
    private K key;
    private V value;
    
    public KeyValuePair(K key, V value) {
        this.key = key;
        this.value = value;
    }
    
    public K getKey() { return key; }
    public V getValue() { return value; }
}

// Usage
KeyValuePair<String, Integer> pair = new KeyValuePair<>("Age", 25);
```

---

## Generic Methods

You can define generic methods within both generic and non-generic classes.

```java
public class Utilities {
    // Generic method - note <T> before return type
    public static <T> void printArray(T[] array) {
        for (T element : array) {
            System.out.print(element + " ");
        }
        System.out.println();
    }
    
    // Generic method with return type
    public static <T> T getMiddleElement(T[] array) {
        return array[array.length / 2];
    }
    
    // Generic method with bounded type
    public static <T extends Comparable<T>> T findMax(T[] array) {
        T max = array[0];
        for (T element : array) {
            if (element.compareTo(max) > 0) {
                max = element;
            }
        }
        return max;
    }
}

// Usage
public class GenericMethodDemo {
    public static void main(String[] args) {
        Integer[] intArray = {1, 2, 3, 4, 5};
        String[] strArray = {"Hello", "World", "Generics"};
        
        Utilities.printArray(intArray);  // 1 2 3 4 5
        Utilities.printArray(strArray);  // Hello World Generics
        
        Integer middle = Utilities.getMiddleElement(intArray); // 3
        String middleStr = Utilities.getMiddleElement(strArray); // "World"
        
        Integer max = Utilities.findMax(intArray); // 5
        String maxStr = Utilities.findMax(strArray); // "World"
    }
}
```

---

## Bounded Type Parameters

Sometimes you want to restrict the types that can be used as type arguments. This is done using **bounds**.

### Upper Bounded (extends)

```java
// T must be Number or its subclass
public class NumberBox<T extends Number> {
    private T number;
    
    public NumberBox(T number) {
        this.number = number;
    }
    
    public double getDoubleValue() {
        return number.doubleValue(); // Can call Number methods!
    }
    
    public T getNumber() {
        return number;
    }
}

// Usage
public class BoundedDemo {
    public static void main(String[] args) {
        NumberBox<Integer> intBox = new NumberBox<>(123);
        NumberBox<Double> doubleBox = new NumberBox<>(45.67);
        
        // NumberBox<String> strBox = new NumberBox<>("text"); // Compile error!
        
        System.out.println(intBox.getDoubleValue());    // 123.0
        System.out.println(doubleBox.getDoubleValue()); // 45.67
    }
}
```

### Multiple Bounds

```java
// T must implement both Comparable and Serializable
public class AdvancedBox<T extends Comparable<T> & Serializable> {
    private T value;
    
    public AdvancedBox(T value) {
        this.value = value;
    }
    
    public boolean isGreaterThan(T other) {
        return value.compareTo(other) > 0;
    }
}

// Usage: String implements both interfaces
AdvancedBox<String> box = new AdvancedBox<>("Hello");
System.out.println(box.isGreaterThan("Hi")); // false
```

---

## Multiple Type Parameters

Classes can have multiple type parameters.

```java
public class Pair<K, V> {
    private K key;
    private V value;
    
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }
    
    public K getKey() { return key; }
    public V getValue() { return value; }
    
    public void setKey(K key) { this.key = key; }
    public void setValue(V value) { this.value = value; }
    
    @Override
    public String toString() {
        return "(" + key + ", " + value + ")";
    }
}

// Usage
public class MultiTypeDemo {
    public static void main(String[] args) {
        Pair<String, Integer> agePair = new Pair<>("Alice", 30);
        Pair<Integer, String> idPair = new Pair<>(101, "Employee");
        Pair<String, Double> pricePair = new Pair<>("Apple", 1.99);
        
        System.out.println(agePair);   // (Alice, 30)
        System.out.println(idPair);    // (101, Employee)
        System.out.println(pricePair); // (Apple, 1.99)
    }
}
```

### Triple Type Parameters

```java
public class Triple<A, B, C> {
    private A first;
    private B second;
    private C third;
    
    public Triple(A first, B second, C third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }
    
    // Getters and setters...
    
    public static <X, Y, Z> Triple<X, Y, Z> of(X x, Y y, Z z) {
        return new Triple<>(x, y, z);
    }
}

// Usage
Triple<String, Integer, Boolean> record = Triple.of("John", 25, true);
```

---

## Type Erasure

Java generics use **type erasure** - the generic type information is removed during compilation and replaced with casts.

### What Happens During Compilation

```java
// Source code
public class Box<T> {
    private T content;
    public T getContent() { return content; }
}

Box<String> box = new Box<>();

// After type erasure (roughly)
public class Box {
    private Object content;
    public Object getContent() { return content; }
}

Box box = new Box();
String s = (String) box.getContent(); // Cast added by compiler
```

### Implications

```java
// Cannot do this - type info not available at runtime
public class Container<T> {
    public void doSomething() {
        // if (T instanceof String) { } // Compile error!
        // T obj = new T(); // Compile error!
        // T[] array = new T[10]; // Compile error!
    }
}

// Cannot distinguish between generic types at runtime
List<String> stringList = new ArrayList<>();
List<Integer> intList = new ArrayList<>();
System.out.println(stringList.getClass() == intList.getClass()); // true!
```

---

## Wildcards

Wildcards provide flexibility when working with generic types.

### Unbounded Wildcard (?)

```java
public static void printList(List<?> list) {
    for (Object obj : list) {
        System.out.println(obj);
    }
}

// Works with any List
printList(List.of(1, 2, 3));
printList(List.of("a", "b", "c"));
```

### Upper Bounded Wildcard (? extends Type)

```java
// Can read as Number or its subclasses
public static double sumOfList(List<? extends Number> list) {
    double sum = 0.0;
    for (Number num : list) {
        sum += num.doubleValue();
    }
    return sum;
}

// Usage
List<Integer> ints = List.of(1, 2, 3);
List<Double> doubles = List.of(1.1, 2.2, 3.3);
System.out.println(sumOfList(ints));    // 6.0
System.out.println(sumOfList(doubles)); // 6.6
```

### Lower Bounded Wildcard (? super Type)

```java
// Can write Integer or its superclasses
public static void addIntegers(List<? super Integer> list) {
    for (int i = 1; i <= 5; i++) {
        list.add(i);
    }
}

// Usage
List<Integer> ints = new ArrayList<>();
List<Number> numbers = new ArrayList<>();
List<Object> objects = new ArrayList<>();

addIntegers(ints);     // OK
addIntegers(numbers);  // OK
addIntegers(objects);  // OK
```

### PECS Principle
**Producer Extends, Consumer Super**

- Use `extends` when you only **read** from the structure
- Use `super` when you only **write** to the structure

---

## Best Practices

### 1. Use Generics Over Raw Types

```java
// Bad - raw type
List list = new ArrayList();
list.add("String");
list.add(123);

// Good - generic type
List<String> list = new ArrayList<>();
list.add("String");
// list.add(123); // Compile error
```

### 2. Prefer Lists Over Arrays with Generics

```java
// Problematic - arrays are covariant
Object[] arr = new String[10];
arr[0] = 123; // Runtime error!

// Safe - generics provide compile-time safety
List<Object> list = new ArrayList<String>(); // Compile error!
```

### 3. Use Bounded Wildcards for API Flexibility

```java
// Less flexible
public void process(List<MyClass> list) { }

// More flexible
public void process(List<? extends MyClass> list) { }
```

### 4. Document Type Parameter Constraints

```java
/**
 * A container for elements that can be compared.
 * @param <T> the type of elements, must implement Comparable
 */
public class SortedContainer<T extends Comparable<T>> {
    // Implementation
}
```

---

## Common Pitfalls

### 1. Cannot Create Generic Array

```java
// Won't compile
T[] array = new T[10];

// Workaround
@SuppressWarnings("unchecked")
T[] array = (T[]) new Object[10];
```

### 2. Cannot Use Primitives as Type Arguments

```java
// Wrong
List<int> list = new ArrayList<>();

// Correct - use wrapper classes
List<Integer> list = new ArrayList<>();
```

### 3. Type Erasure Limitations

```java
public class Container<T> {
    // Cannot overload based on generic type
    public void process(List<String> list) { }
    // public void process(List<Integer> list) { } // Error!
    
    // After erasure, both methods have same signature
}
```

### 4. Static Context Cannot Use Type Parameters

```java
public class MyClass<T> {
    // private static T instance; // Compile error!
    
    // public static T getInstance() { } // Compile error!
    
    // Static context doesn't know about instance's type parameter
}
```

---

## Real-World Example: Repository Pattern

```java
// Generic repository interface
public interface Repository<T, ID> {
    T findById(ID id);
    List<T> findAll();
    T save(T entity);
    void delete(ID id);
    boolean exists(ID id);
}

// Implementation for User entity
public class UserRepository implements Repository<User, Long> {
    private Map<Long, User> database = new HashMap<>();
    
    @Override
    public User findById(Long id) {
        return database.get(id);
    }
    
    @Override
    public List<User> findAll() {
        return new ArrayList<>(database.values());
    }
    
    @Override
    public User save(User user) {
        database.put(user.getId(), user);
        return user;
    }
    
    @Override
    public void delete(Long id) {
        database.remove(id);
    }
    
    @Override
    public boolean exists(Long id) {
        return database.containsKey(id);
    }
}

// Implementation for Product entity
public class ProductRepository implements Repository<Product, String> {
    // Similar implementation with String IDs
}

// Usage
public class RepositoryDemo {
    public static void main(String[] args) {
        UserRepository userRepo = new UserRepository();
        userRepo.save(new User(1L, "Alice"));
        User user = userRepo.findById(1L);
        
        ProductRepository productRepo = new ProductRepository();
        productRepo.save(new Product("P001", "Laptop"));
        Product product = productRepo.findById("P001");
    }
}
```

---

## Summary

Java generics provide:
- **Type Safety**: Catch errors at compile time
- **Code Reusability**: Write once, use with multiple types
- **Elimination of Casts**: Cleaner, more readable code
- **Better Abstraction**: Create flexible, generic algorithms

Key concepts to remember:
- Type parameters (`<T>`)
- Bounded types (`<T extends SomeClass>`)
- Wildcards (`?`, `? extends`, `? super`)
- Type erasure (runtime behavior)
- PECS principle (Producer Extends, Consumer Super)

Generics are fundamental to modern Java development and are extensively used in Java Collections Framework, Stream API, and enterprise applications.