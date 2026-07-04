# Java Iterator and Iterable - Complete Guide

## Table of Contents
1. [Introduction](#introduction)
2. [Iterator Interface](#iterator-interface)
3. [Iterable Interface](#iterable-interface)
4. [Key Differences](#key-differences)
5. [Iterator Examples](#iterator-examples)
6. [Iterable Examples](#iterable-examples)
7. [Using Both Together](#using-both-together)

---

## Introduction

**Iterator** and **Iterable** are two fundamental interfaces in Java's Collections Framework that work together to provide a standardized way to traverse through collections of objects.

---

## Iterator Interface

### Definition
```java
public interface Iterator<E> {
    boolean hasNext();
    E next();
    void remove(); // optional operation
}
```

### Purpose
The **Iterator** interface provides methods to iterate over a collection one element at a time. It acts as a cursor that points to elements in a collection.

### Key Methods
- **`hasNext()`**: Returns `true` if there are more elements to iterate
- **`next()`**: Returns the next element in the iteration
- **`remove()`**: Removes the last element returned by `next()` (optional)

### Characteristics
- Represents the **state** of an iteration
- Can only move forward (unidirectional)
- Each iterator instance maintains its own position
- Multiple iterators can work on the same collection independently

---

## Iterable Interface

### Definition
```java
public interface Iterable<T> {
    Iterator<T> iterator();
    // default methods: forEach(), spliterator()
}
```

### Purpose
The **Iterable** interface represents a collection that can be iterated over. Any class implementing `Iterable` can be used in a **for-each loop**.

### Key Method
- **`iterator()`**: Returns an `Iterator` instance for traversing the collection

### Characteristics
- Represents a **collection** that can be iterated
- Enables the use of enhanced for-loop (for-each)
- A single `Iterable` can create multiple independent `Iterator` instances

---

## Key Differences

| Aspect | Iterator | Iterable |
|--------|----------|----------|
| **Purpose** | Performs the iteration | Provides the iterator |
| **Method** | `hasNext()`, `next()`, `remove()` | `iterator()` |
| **Represents** | Current position in iteration | The collection itself |
| **For-each loop** | Cannot be used directly | Can be used in for-each loop |
| **Multiple iterations** | Single iteration instance | Can create multiple iterators |
| **Implementation** | Usually implemented by internal classes | Implemented by collection classes |

---

## Iterator Examples

### Example 1: Basic Iterator Usage
```java
import java.util.*;

public class IteratorExample1 {
    public static void main(String[] args) {
        List<String> fruits = new ArrayList<>();
        fruits.add("Apple");
        fruits.add("Banana");
        fruits.add("Cherry");
        
        // Get an iterator
        Iterator<String> iterator = fruits.iterator();
        
        // Traverse using iterator
        while (iterator.hasNext()) {
            String fruit = iterator.next();
            System.out.println(fruit);
        }
    }
}
```

### Example 2: Iterator with Remove
```java
import java.util.*;

public class IteratorExample2 {
    public static void main(String[] args) {
        List<Integer> numbers = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6));
        
        Iterator<Integer> iterator = numbers.iterator();
        
        // Remove even numbers
        while (iterator.hasNext()) {
            Integer num = iterator.next();
            if (num % 2 == 0) {
                iterator.remove(); // Safe removal during iteration
            }
        }
        
        System.out.println(numbers); // Output: [1, 3, 5]
    }
}
```

### Example 3: Multiple Iterators
```java
import java.util.*;

public class IteratorExample3 {
    public static void main(String[] args) {
        Set<String> colors = new HashSet<>(Arrays.asList("Red", "Green", "Blue"));
        
        // Two independent iterators
        Iterator<String> iter1 = colors.iterator();
        Iterator<String> iter2 = colors.iterator();
        
        System.out.println("Iterator 1:");
        if (iter1.hasNext()) {
            System.out.println(iter1.next());
        }
        
        System.out.println("\nIterator 2:");
        while (iter2.hasNext()) {
            System.out.println(iter2.next());
        }
    }
}
```

---

## Iterable Examples

### Example 1: Using Iterable with For-Each Loop
```java
import java.util.*;

public class IterableExample1 {
    public static void main(String[] args) {
        List<String> languages = new ArrayList<>();
        languages.add("Java");
        languages.add("Python");
        languages.add("JavaScript");
        
        // For-each loop works because List implements Iterable
        for (String lang : languages) {
            System.out.println(lang);
        }
    }
}
```

### Example 2: Custom Iterable Class
```java
import java.util.*;

class NumberRange implements Iterable<Integer> {
    private int start;
    private int end;
    
    public NumberRange(int start, int end) {
        this.start = start;
        this.end = end;
    }
    
    @Override
    public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {
            private int current = start;
            
            @Override
            public boolean hasNext() {
                return current <= end;
            }
            
            @Override
            public Integer next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return current++;
            }
        };
    }
}

public class IterableExample2 {
    public static void main(String[] args) {
        NumberRange range = new NumberRange(1, 5);
        
        // Can use for-each because NumberRange implements Iterable
        for (int num : range) {
            System.out.println(num);
        }
    }
}
```

### Example 3: Iterable with forEach Method
```java
import java.util.*;

public class IterableExample3 {
    public static void main(String[] args) {
        List<Double> prices = Arrays.asList(19.99, 29.99, 9.99, 49.99);
        
        // Using forEach method from Iterable interface
        prices.forEach(price -> System.out.println("$" + price));
    }
}
```

### Example 4: Iterator with Map
```java
import java.util.*;

public class IteratorExample4 {
    public static void main(String[] args) {
        Map<String, Integer> studentGrades = new HashMap<>();
        studentGrades.put("Alice", 85);
        studentGrades.put("Bob", 92);
        studentGrades.put("Charlie", 78);
        studentGrades.put("Diana", 95);
        
        // Iterator over map entries
        Iterator<Map.Entry<String, Integer>> iterator = studentGrades.entrySet().iterator();
        
        System.out.println("Students with grades above 80:");
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();
            if (entry.getValue() > 80) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        }
    }
}
```

### Example 5: ListIterator (Bidirectional)
```java
import java.util.*;

public class IteratorExample5 {
    public static void main(String[] args) {
        List<String> cities = new ArrayList<>(Arrays.asList("Paris", "London", "Tokyo", "New York"));
        
        // ListIterator allows bidirectional traversal
        ListIterator<String> listIterator = cities.listIterator();
        
        System.out.println("Forward traversal:");
        while (listIterator.hasNext()) {
            System.out.println(listIterator.next());
        }
        
        System.out.println("\nBackward traversal:");
        while (listIterator.hasPrevious()) {
            System.out.println(listIterator.previous());
        }
        
        // Adding elements during iteration
        listIterator = cities.listIterator();
        while (listIterator.hasNext()) {
            String city = listIterator.next();
            if (city.equals("London")) {
                listIterator.add("Berlin"); // Add after London
            }
        }
        
        System.out.println("\nAfter adding Berlin: " + cities);
    }
}
```

### Example 6: Iterator with Custom Filter Logic
```java
import java.util.*;

public class IteratorExample6 {
    public static void main(String[] args) {
        List<String> emails = new ArrayList<>(Arrays.asList(
            "user@gmail.com",
            "admin@company.com",
            "spam@unknown.net",
            "contact@business.org",
            "test@gmail.com"
        ));
        
        Iterator<String> iterator = emails.iterator();
        
        // Remove all non-gmail addresses
        while (iterator.hasNext()) {
            String email = iterator.next();
            if (!email.endsWith("@gmail.com")) {
                iterator.remove();
                System.out.println("Removed: " + email);
            }
        }
        
        System.out.println("\nRemaining emails: " + emails);
    }
}
```

---

## Iterable Examples

### Example 1: Using Iterable with For-Each Loop
```java
import java.util.*;

public class IterableExample1 {
    public static void main(String[] args) {
        List<String> languages = new ArrayList<>();
        languages.add("Java");
        languages.add("Python");
        languages.add("JavaScript");
        
        // For-each loop works because List implements Iterable
        for (String lang : languages) {
            System.out.println(lang);
        }
    }
}
```

### Example 2: Custom Iterable Class
```java
import java.util.*;

class NumberRange implements Iterable<Integer> {
    private int start;
    private int end;
    
    public NumberRange(int start, int end) {
        this.start = start;
        this.end = end;
    }
    
    @Override
    public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {
            private int current = start;
            
            @Override
            public boolean hasNext() {
                return current <= end;
            }
            
            @Override
            public Integer next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return current++;
            }
        };
    }
}

public class IterableExample2 {
    public static void main(String[] args) {
        NumberRange range = new NumberRange(1, 5);
        
        // Can use for-each because NumberRange implements Iterable
        for (int num : range) {
            System.out.println(num);
        }
    }
}
```

### Example 3: Iterable with forEach Method
```java
import java.util.*;

public class IterableExample3 {
    public static void main(String[] args) {
        List<Double> prices = Arrays.asList(19.99, 29.99, 9.99, 49.99);
        
        // Using forEach method from Iterable interface
        prices.forEach(price -> System.out.println("$" + price));
    }
}
```

### Example 4: Custom Iterable Array Wrapper
```java
import java.util.*;

class ArrayWrapper<T> implements Iterable<T> {
    private T[] array;
    
    public ArrayWrapper(T[] array) {
        this.array = array;
    }
    
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int index = 0;
            
            @Override
            public boolean hasNext() {
                return index < array.length;
            }
            
            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return array[index++];
            }
        };
    }
}

public class IterableExample4 {
    public static void main(String[] args) {
        String[] fruits = {"Apple", "Banana", "Cherry", "Date"};
        ArrayWrapper<String> wrapper = new ArrayWrapper<>(fruits);
        
        // Now we can use for-each on a regular array wrapper
        System.out.println("Fruits:");
        for (String fruit : wrapper) {
            System.out.println("- " + fruit);
        }
    }
}
```

### Example 5: Fibonacci Sequence as Iterable
```java
import java.util.*;

class FibonacciSequence implements Iterable<Integer> {
    private int limit;
    
    public FibonacciSequence(int limit) {
        this.limit = limit;
    }
    
    @Override
    public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {
            private int count = 0;
            private int previous = 0;
            private int current = 1;
            
            @Override
            public boolean hasNext() {
                return count < limit;
            }
            
            @Override
            public Integer next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                
                count++;
                if (count == 1) return 0;
                if (count == 2) return 1;
                
                int next = previous + current;
                previous = current;
                current = next;
                return next;
            }
        };
    }
}

public class IterableExample5 {
    public static void main(String[] args) {
        FibonacciSequence fibonacci = new FibonacciSequence(10);
        
        System.out.println("First 10 Fibonacci numbers:");
        for (int num : fibonacci) {
            System.out.print(num + " ");
        }
    }
}
```

### Example 6: Custom String Tokenizer as Iterable
```java
import java.util.*;

class StringTokenizer implements Iterable<String> {
    private String text;
    private String delimiter;
    
    public StringTokenizer(String text, String delimiter) {
        this.text = text;
        this.delimiter = delimiter;
    }
    
    @Override
    public Iterator<String> iterator() {
        return new Iterator<String>() {
            private String[] tokens = text.split(delimiter);
            private int index = 0;
            
            @Override
            public boolean hasNext() {
                return index < tokens.length;
            }
            
            @Override
            public String next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return tokens[index++].trim();
            }
        };
    }
}

public class IterableExample6 {
    public static void main(String[] args) {
        StringTokenizer tokenizer = new StringTokenizer(
            "Java,Python,C++,JavaScript,Ruby", 
            ","
        );
        
        System.out.println("Programming languages:");
        for (String language : tokenizer) {
            System.out.println("- " + language);
        }
    }
}
```

---

## Using Both Together

### Example 1: Custom Book Library
```java
import java.util.*;

class Book {
    private String title;
    private String author;
    
    public Book(String title, String author) {
        this.title = title;
        this.author = author;
    }
    
    @Override
    public String toString() {
        return title + " by " + author;
    }
}

class Library implements Iterable<Book> {
    private List<Book> books = new ArrayList<>();
    
    public void addBook(Book book) {
        books.add(book);
    }
    
    @Override
    public Iterator<Book> iterator() {
        return books.iterator();
    }
    
    // Custom iterator method for filtering
    public Iterator<Book> authorIterator(String author) {
        return new Iterator<Book>() {
            private Iterator<Book> baseIterator = books.iterator();
            private Book nextBook = null;
            
            @Override
            public boolean hasNext() {
                while (baseIterator.hasNext()) {
                    Book book = baseIterator.next();
                    if (book.author.equals(author)) {
                        nextBook = book;
                        return true;
                    }
                }
                return false;
            }
            
            @Override
            public Book next() {
                if (nextBook == null && !hasNext()) {
                    throw new NoSuchElementException();
                }
                Book result = nextBook;
                nextBook = null;
                return result;
            }
        };
    }
}

public class BothExample1 {
    public static void main(String[] args) {
        Library library = new Library();
        library.addBook(new Book("1984", "George Orwell"));
        library.addBook(new Book("Animal Farm", "George Orwell"));
        library.addBook(new Book("Brave New World", "Aldous Huxley"));
        
        // Using Iterable (for-each loop)
        System.out.println("All books:");
        for (Book book : library) {
            System.out.println(book);
        }
        
        // Using custom Iterator
        System.out.println("\nBooks by George Orwell:");
        Iterator<Book> orwellBooks = library.authorIterator("George Orwell");
        while (orwellBooks.hasNext()) {
            System.out.println(orwellBooks.next());
        }
    }
}
```

### Example 2: Playlist with Multiple Iteration Modes
```java
import java.util.*;

class Song {
    private String title;
    private int duration; // in seconds
    
    public Song(String title, int duration) {
        this.title = title;
        this.duration = duration;
    }
    
    public int getDuration() {
        return duration;
    }
    
    @Override
    public String toString() {
        return title + " (" + duration + "s)";
    }
}

class Playlist implements Iterable<Song> {
    private List<Song> songs = new ArrayList<>();
    
    public void addSong(Song song) {
        songs.add(song);
    }
    
    @Override
    public Iterator<Song> iterator() {
        return songs.iterator(); // Normal order
    }
    
    // Reverse iterator
    public Iterator<Song> reverseIterator() {
        return new Iterator<Song>() {
            private int currentIndex = songs.size() - 1;
            
            @Override
            public boolean hasNext() {
                return currentIndex >= 0;
            }
            
            @Override
            public Song next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return songs.get(currentIndex--);
            }
        };
    }
    
    // Shuffle iterator
    public Iterator<Song> shuffleIterator() {
        List<Song> shuffled = new ArrayList<>(songs);
        Collections.shuffle(shuffled);
        return shuffled.iterator();
    }
}

public class BothExample2 {
    public static void main(String[] args) {
        Playlist playlist = new Playlist();
        playlist.addSong(new Song("Song A", 180));
        playlist.addSong(new Song("Song B", 210));
        playlist.addSong(new Song("Song C", 195));
        playlist.addSong(new Song("Song D", 240));
        
        // Using Iterable (normal order)
        System.out.println("Normal order:");
        for (Song song : playlist) {
            System.out.println(song);
        }
        
        // Using custom Iterator (reverse)
        System.out.println("\nReverse order:");
        Iterator<Song> reverseIter = playlist.reverseIterator();
        while (reverseIter.hasNext()) {
            System.out.println(reverseIter.next());
        }
        
        // Using custom Iterator (shuffle)
        System.out.println("\nShuffle order:");
        Iterator<Song> shuffleIter = playlist.shuffleIterator();
        while (shuffleIter.hasNext()) {
            System.out.println(shuffleIter.next());
        }
    }
}
```

### Example 3: Shopping Cart with Filtering
```java
import java.util.*;

class Product {
    private String name;
    private double price;
    private String category;
    
    public Product(String name, double price, String category) {
        this.name = name;
        this.price = price;
        this.category = category;
    }
    
    public double getPrice() {
        return price;
    }
    
    public String getCategory() {
        return category;
    }
    
    @Override
    public String toString() {
        return name + " - $" + price + " [" + category + "]";
    }
}

class ShoppingCart implements Iterable<Product> {
    private List<Product> products = new ArrayList<>();
    
    public void addProduct(Product product) {
        products.add(product);
    }
    
    @Override
    public Iterator<Product> iterator() {
        return products.iterator();
    }
    
    // Iterator for products in a specific price range
    public Iterator<Product> priceRangeIterator(double minPrice, double maxPrice) {
        return new Iterator<Product>() {
            private Iterator<Product> baseIterator = products.iterator();
            private Product nextProduct = null;
            
            @Override
            public boolean hasNext() {
                while (baseIterator.hasNext()) {
                    Product product = baseIterator.next();
                    if (product.getPrice() >= minPrice && product.getPrice() <= maxPrice) {
                        nextProduct = product;
                        return true;
                    }
                }
                return false;
            }
            
            @Override
            public Product next() {
                if (nextProduct == null && !hasNext()) {
                    throw new NoSuchElementException();
                }
                Product result = nextProduct;
                nextProduct = null;
                return result;
            }
        };
    }
    
    public double getTotalPrice() {
        double total = 0;
        for (Product product : this) { // Using Iterable
            total += product.getPrice();
        }
        return total;
    }
}

public class BothExample3 {
    public static void main(String[] args) {
        ShoppingCart cart = new ShoppingCart();
        cart.addProduct(new Product("Laptop", 999.99, "Electronics"));
        cart.addProduct(new Product("Mouse", 29.99, "Electronics"));
        cart.addProduct(new Product("Desk", 199.99, "Furniture"));
        cart.addProduct(new Product("Chair", 149.99, "Furniture"));
        cart.addProduct(new Product("Keyboard", 79.99, "Electronics"));
        
        // Using Iterable
        System.out.println("All products:");
        for (Product product : cart) {
            System.out.println(product);
        }
        
        System.out.println("\nTotal: $" + cart.getTotalPrice());
        
        // Using custom Iterator
        System.out.println("\nProducts between $50 and $200:");
        Iterator<Product> priceIter = cart.priceRangeIterator(50, 200);
        while (priceIter.hasNext()) {
            System.out.println(priceIter.next());
        }
    }
}
```

---

## Summary

- **Iterable** is implemented by classes that represent collections and allows them to be used in for-each loops
- **Iterator** is returned by `Iterable.iterator()` and provides the actual mechanism for traversing elements
- Together, they provide a flexible and standardized way to work with collections in Java
- Custom implementations allow for specialized iteration behavior while maintaining compatibility with Java's language features

By implementing `Iterable`, your class can work with for-each loops. By providing custom `Iterator` implementations, you can control exactly how elements are traversed.