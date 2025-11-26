# Java Collection Interface - Complete Guide

## Overview

The `Collection` interface is the root interface in the Java Collections Framework hierarchy. It represents a group of objects known as elements and provides fundamental methods for manipulating collections of data.

---

## Core Methods

### 1. `int size()`

**Description:** Returns the number of elements in the collection.

**Returns:** The count of elements currently stored in the collection.

**Example:**
```java
Collection<String> fruits = new ArrayList<>();
fruits.add("Apple");
fruits.add("Banana");
fruits.add("Cherry");
System.out.println(fruits.size()); // Output: 3
```

---

### 2. `boolean isEmpty()`

**Description:** Checks whether the collection contains any elements.

**Returns:** `true` if the collection has no elements, `false` otherwise.

**Example:**
```java
Collection<Integer> numbers = new HashSet<>();
System.out.println(numbers.isEmpty()); // Output: true

numbers.add(42);
System.out.println(numbers.isEmpty()); // Output: false
```

---

### 3. `boolean contains(E element)`

**Description:** Checks if the collection contains the specified element. Uses the `equals()` method for comparison.

**Parameters:** `element` - the object to search for in the collection

**Returns:** `true` if the element exists in the collection, `false` otherwise.

**Example:**
```java
Collection<String> colors = new ArrayList<>();
colors.add("Red");
colors.add("Green");
colors.add("Blue");

System.out.println(colors.contains("Green")); // Output: true
System.out.println(colors.contains("Yellow")); // Output: false
```

---

### 4. `boolean containsAll(Collection<?> c)`

**Description:** Checks if the collection contains all elements from another specified collection.

**Parameters:** `c` - the collection to be checked for containment

**Returns:** `true` if all elements of the specified collection are present, `false` otherwise.

**Example:**
```java
Collection<String> allAnimals = new ArrayList<>();
allAnimals.add("Dog");
allAnimals.add("Cat");
allAnimals.add("Bird");
allAnimals.add("Fish");

Collection<String> pets = new ArrayList<>();
pets.add("Dog");
pets.add("Cat");

System.out.println(allAnimals.containsAll(pets)); // Output: true

pets.add("Hamster");
System.out.println(allAnimals.containsAll(pets)); // Output: false
```

---

### 5. `boolean add(E element)`

**Description:** Adds the specified element to the collection. The behavior varies by implementation (e.g., sets reject duplicates).

**Parameters:** `element` - the element to be added

**Returns:** `true` if the collection changed as a result of the call, `false` otherwise.

**Example:**
```java
Collection<Integer> numbers = new ArrayList<>();
System.out.println(numbers.add(10)); // Output: true
System.out.println(numbers.add(20)); // Output: true

// For Set - duplicates not allowed
Collection<Integer> uniqueNumbers = new HashSet<>();
System.out.println(uniqueNumbers.add(10)); // Output: true
System.out.println(uniqueNumbers.add(10)); // Output: false (duplicate)
```

---

### 6. `boolean addAll(Collection<? extends E> c)`

**Description:** Adds all elements from the specified collection to this collection.

**Parameters:** `c` - collection containing elements to be added

**Returns:** `true` if the collection changed as a result of the call.

**Example:**
```java
Collection<String> list1 = new ArrayList<>();
list1.add("Apple");
list1.add("Banana");

Collection<String> list2 = new ArrayList<>();
list2.add("Cherry");
list2.add("Date");

list1.addAll(list2);
System.out.println(list1); // Output: [Apple, Banana, Cherry, Date]
```

---

### 7. `boolean remove(E element)`

**Description:** Removes a single instance of the specified element from the collection, if present.

**Parameters:** `element` - the element to be removed

**Returns:** `true` if the element was found and removed, `false` otherwise.

**Example:**
```java
Collection<String> cities = new ArrayList<>();
cities.add("Paris");
cities.add("London");
cities.add("Tokyo");

System.out.println(cities.remove("London")); // Output: true
System.out.println(cities); // Output: [Paris, Tokyo]
System.out.println(cities.remove("Berlin")); // Output: false
```

---

### 8. `boolean removeAll(Collection<?> c)`

**Description:** Removes all elements from this collection that are also contained in the specified collection.

**Parameters:** `c` - collection containing elements to be removed

**Returns:** `true` if the collection changed as a result of the call.

**Example:**
```java
Collection<Integer> numbers = new ArrayList<>();
numbers.add(1);
numbers.add(2);
numbers.add(3);
numbers.add(4);
numbers.add(5);

Collection<Integer> toRemove = new ArrayList<>();
toRemove.add(2);
toRemove.add(4);

numbers.removeAll(toRemove);
System.out.println(numbers); // Output: [1, 3, 5]
```

---

### 9. `void clear()`

**Description:** Removes all elements from the collection, leaving it empty.

**Returns:** void (no return value)

**Example:**
```java
Collection<String> names = new ArrayList<>();
names.add("Alice");
names.add("Bob");
names.add("Charlie");

System.out.println(names.size()); // Output: 3
names.clear();
System.out.println(names.size()); // Output: 0
System.out.println(names.isEmpty()); // Output: true
```

---

### 10. `Object[] toArray()`

**Description:** Converts the collection to an array containing all of its elements.

**Returns:** An array containing all elements in the collection.

**Example:**
```java
Collection<String> languages = new ArrayList<>();
languages.add("Java");
languages.add("Python");
languages.add("JavaScript");

Object[] array = languages.toArray();
for (Object lang : array) {
    System.out.println(lang);
}
// Output:
// Java
// Python
// JavaScript
```

---

### 11. `Iterator<E> iterator()`

**Description:** Returns an iterator over the elements in the collection. The iterator allows traversal and removal of elements.

**Returns:** An `Iterator` object for the collection.

**Example:**
```java
Collection<String> books = new ArrayList<>();
books.add("1984");
books.add("Brave New World");
books.add("Fahrenheit 451");

Iterator<String> iterator = books.iterator();
while (iterator.hasNext()) {
    String book = iterator.next();
    System.out.println(book);
    if (book.equals("Brave New World")) {
        iterator.remove(); // Safe removal during iteration
    }
}
System.out.println(books); // Output: [1984, Fahrenheit 451]
```

---

## Complete Working Example

Here's a comprehensive example demonstrating all methods:

```java
import java.util.*;

public class CollectionInterfaceDemo {
    public static void main(String[] args) {
        // Creating a collection
        Collection<String> students = new ArrayList<>();
        
        // isEmpty() - checking if empty
        System.out.println("Is empty? " + students.isEmpty()); // true
        
        // add() - adding elements
        students.add("Alice");
        students.add("Bob");
        students.add("Charlie");
        System.out.println("Students: " + students);
        
        // size() - getting size
        System.out.println("Size: " + students.size()); // 3
        
        // contains() - checking for element
        System.out.println("Contains Bob? " + students.contains("Bob")); // true
        
        // addAll() - adding multiple elements
        Collection<String> newStudents = Arrays.asList("David", "Eve");
        students.addAll(newStudents);
        System.out.println("After addAll: " + students);
        
        // containsAll() - checking for multiple elements
        System.out.println("Contains all new students? " + 
            students.containsAll(newStudents)); // true
        
        // toArray() - converting to array
        Object[] array = students.toArray();
        System.out.println("Array length: " + array.length);
        
        // iterator() - iterating through elements
        System.out.println("Iterating:");
        Iterator<String> iterator = students.iterator();
        while (iterator.hasNext()) {
            System.out.println("- " + iterator.next());
        }
        
        // remove() - removing single element
        students.remove("Bob");
        System.out.println("After removing Bob: " + students);
        
        // removeAll() - removing multiple elements
        students.removeAll(Arrays.asList("David", "Eve"));
        System.out.println("After removeAll: " + students);
        
        // clear() - removing all elements
        students.clear();
        System.out.println("After clear, size: " + students.size()); // 0
    }
}
```

---

## Key Points to Remember

1. **Collection is an interface** - it cannot be instantiated directly. Use implementations like `ArrayList`, `HashSet`, or `LinkedList`.

2. **Generic type E** - represents the type of elements stored in the collection.

3. **Different implementations behave differently** - for example, `Set` doesn't allow duplicates while `List` does.

4. **Iterator for safe removal** - use `iterator.remove()` instead of `collection.remove()` when iterating.

5. **null elements** - some implementations allow null elements, others don't (e.g., `HashSet` allows one null, but `TreeSet` doesn't allow any).

6. **Performance varies** - different implementations have different time complexities for operations.

---

## Common Implementations

- **ArrayList** - resizable array, fast random access
- **LinkedList** - doubly-linked list, fast insertion/deletion
- **HashSet** - hash table, no duplicates, no order
- **TreeSet** - sorted set, no duplicates
- **LinkedHashSet** - maintains insertion order, no duplicates

---

*This guide covers all standard methods of the Java Collection interface with practical examples.*