# Java Comparable Interface - Complete Guide

## Table of Contents
- [What is Comparable?](#what-is-comparable)
- [Is This Standard?](#is-this-standard)
- [Interface Definition](#interface-definition)
- [How compareTo() Works](#how-compareto-works)
- [Basic Examples](#basic-examples)
- [Advanced Examples](#advanced-examples)
- [Real-World Examples](#real-world-examples)
- [Edge Cases and Special Scenarios](#edge-cases-and-special-scenarios)
- [Best Practices](#best-practices)
- [Common Mistakes](#common-mistakes)
- [Comparable vs Comparator](#comparable-vs-comparator)

---

## What is Comparable?

The `Comparable` interface is a standard Java interface from the `java.lang` package that allows objects to be compared for ordering. When a class implements `Comparable`, its objects can be:
- Sorted automatically in collections
- Used in sorted data structures like TreeSet and TreeMap
- Searched using binary search algorithms
- Compared using natural ordering

---

## Is This Standard?

**YES, ABSOLUTELY!** The `Comparable` interface is 100% standard Java.

### Why it's standard:
- Part of the Java Standard Library since Java 1.2 (1998)
- Located in the `java.lang` package (automatically imported)
- Used throughout the entire Java ecosystem
- Foundation of the Collections Framework
- Implemented by many built-in Java classes

### Classes that already implement Comparable:
- `String`
- `Integer`, `Long`, `Double`, `Float`, `Short`, `Byte`
- `Character`
- `Boolean`
- `BigInteger`, `BigDecimal`
- `Date`, `LocalDate`, `LocalDateTime`
- `File`
- `Enum` types

---

## Interface Definition

```java
package java.lang;

public interface Comparable<T> {
    /**
     * Compares this object with the specified object for order.
     * 
     * @param obj the object to be compared
     * @return a negative integer, zero, or a positive integer as this object
     *         is less than, equal to, or greater than the specified object
     */
    int compareTo(T obj);
}
```

**Note:** The actual Java implementation uses generics, making it type-safe.

---

## How compareTo() Works

The `compareTo()` method establishes a total ordering on objects. It returns:

| Return Value | Meaning | Mathematical Representation |
|--------------|---------|----------------------------|
| **Negative** (< 0) | `this` precedes `obj` | this < obj |
| **Zero** (= 0) | `this` equals `obj` | this == obj |
| **Positive** (> 0) | `this` follows `obj` | this > obj |

### Visual Example:
```
Timeline: ----[obj]----[this]----[future]---->

if this.compareTo(obj) < 0  →  this comes BEFORE obj  (this is earlier/smaller)
if this.compareTo(obj) = 0  →  this is EQUAL to obj
if this.compareTo(obj) > 0  →  this comes AFTER obj   (this is later/larger)
```

---

## Basic Examples

### Example 1: Student Class - Compare by ID (Ascending)

```java
public class Student implements Comparable<Student> {
    int id;
    String name;
    
    public Student(int id, String name) {
        this.id = id;
        this.name = name;
    }
    
    @Override
    public int compareTo(Student obj) {
        // Compare by id - ascending order
        return this.id - obj.id;
    }
    
    @Override
    public String toString() {
        return "Student{id=" + id + ", name='" + name + "'}";
    }
}
```

**Usage:**
```java
public class TestStudent {
    public static void main(String[] args) {
        List<Student> students = new ArrayList<>();
        students.add(new Student(103, "Alice"));
        students.add(new Student(101, "Bob"));
        students.add(new Student(102, "Charlie"));
        students.add(new Student(105, "Diana"));
        students.add(new Student(104, "Eve"));
        
        System.out.println("Before sorting:");
        for (Student s : students) {
            System.out.println(s);
        }
        
        Collections.sort(students); // Uses compareTo() automatically
        
        System.out.println("\nAfter sorting:");
        for (Student s : students) {
            System.out.println(s);
        }
    }
}
```

**Output:**
```
Before sorting:
Student{id=103, name='Alice'}
Student{id=101, name='Bob'}
Student{id=102, name='Charlie'}
Student{id=105, name='Diana'}
Student{id=104, name='Eve'}

After sorting:
Student{id=101, name='Bob'}
Student{id=102, name='Charlie'}
Student{id=103, name='Alice'}
Student{id=104, name='Eve'}
Student{id=105, name='Diana'}
```

---

### Example 2: Person Class - Compare by Age

```java
public class Person implements Comparable<Person> {
    String name;
    int age;
    
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    @Override
    public int compareTo(Person obj) {
        // Compare by age - ascending order
        return this.age - obj.age;
    }
    
    @Override
    public String toString() {
        return name + " (age " + age + ")";
    }
}
```

**Usage:**
```java
public class TestPerson {
    public static void main(String[] args) {
        Person[] people = {
            new Person("John", 45),
            new Person("Alice", 23),
            new Person("Bob", 67),
            new Person("Charlie", 34),
            new Person("Diana", 29)
        };
        
        System.out.println("Before sorting:");
        for (Person p : people) {
            System.out.println(p);
        }
        
        Arrays.sort(people);
        
        System.out.println("\nAfter sorting:");
        for (Person p : people) {
            System.out.println(p);
        }
    }
}
```

**Output:**
```
Before sorting:
John (age 45)
Alice (age 23)
Bob (age 67)
Charlie (age 34)
Diana (age 29)

After sorting:
Alice (age 23)
Diana (age 29)
Charlie (age 34)
John (age 45)
Bob (age 67)
```

---

### Example 3: Product Class - Compare by Price

```java
public class Product implements Comparable<Product> {
    String name;
    double price;
    int stock;
    
    public Product(String name, double price, int stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
    }
    
    @Override
    public int compareTo(Product obj) {
        // Compare by price - using Double.compare for accuracy
        return Double.compare(this.price, obj.price);
    }
    
    @Override
    public String toString() {
        return name + ": $" + price + " (Stock: " + stock + ")";
    }
}
```

**Usage:**
```java
public class TestProduct {
    public static void main(String[] args) {
        List<Product> products = new ArrayList<>();
        products.add(new Product("Laptop", 1299.99, 5));
        products.add(new Product("Mouse", 29.99, 50));
        products.add(new Product("Keyboard", 89.99, 30));
        products.add(new Product("Monitor", 399.99, 15));
        products.add(new Product("Webcam", 79.99, 25));
        
        Collections.sort(products);
        
        System.out.println("Products sorted by price:");
        for (Product p : products) {
            System.out.println(p);
        }
    }
}
```

**Output:**
```
Products sorted by price:
Mouse: $29.99 (Stock: 50)
Webcam: $79.99 (Stock: 25)
Keyboard: $89.99 (Stock: 30)
Monitor: $399.99 (Stock: 15)
Laptop: $1299.99 (Stock: 5)
```

---

### Example 4: Book Class - Compare by Title (Alphabetically)

```java
public class Book implements Comparable<Book> {
    String title;
    String author;
    int year;
    
    public Book(String title, String author, int year) {
        this.title = title;
        this.author = author;
        this.year = year;
    }
    
    @Override
    public int compareTo(Book obj) {
        // Compare by title alphabetically
        return this.title.compareTo(obj.title);
    }
    
    @Override
    public String toString() {
        return "\"" + title + "\" by " + author + " (" + year + ")";
    }
}
```

**Usage:**
```java
public class TestBook {
    public static void main(String[] args) {
        List<Book> library = Arrays.asList(
            new Book("Moby Dick", "Herman Melville", 1851),
            new Book("1984", "George Orwell", 1949),
            new Book("To Kill a Mockingbird", "Harper Lee", 1960),
            new Book("Pride and Prejudice", "Jane Austen", 1813),
            new Book("The Great Gatsby", "F. Scott Fitzgerald", 1925)
        );
        
        Collections.sort(library);
        
        System.out.println("Books sorted alphabetically:");
        for (Book b : library) {
            System.out.println(b);
        }
    }
}
```

**Output:**
```
Books sorted alphabetically:
"1984" by George Orwell (1949)
"Moby Dick" by Herman Melville (1851)
"Pride and Prejudice" by Jane Austen (1813)
"The Great Gatsby" by F. Scott Fitzgerald (1925)
"To Kill a Mockingbird" by Harper Lee (1960)
```

---

## Advanced Examples

### Example 5: Employee - Multiple Field Comparison (Composite Ordering)

```java
public class Employee implements Comparable<Employee> {
    String department;
    String name;
    int salary;
    
    public Employee(String department, String name, int salary) {
        this.department = department;
        this.name = name;
        this.salary = salary;
    }
    
    @Override
    public int compareTo(Employee obj) {
        // First: compare by department
        int deptComparison = this.department.compareTo(obj.department);
        if (deptComparison != 0) {
            return deptComparison; // Departments are different
        }
        
        // Second: if same department, compare by salary (descending)
        int salaryComparison = Integer.compare(obj.salary, this.salary); // Note: reversed
        if (salaryComparison != 0) {
            return salaryComparison; // Salaries are different
        }
        
        // Third: if salary is also same, compare by name
        return this.name.compareTo(obj.name);
    }
    
    @Override
    public String toString() {
        return String.format("%-15s %-15s $%,d", department, name, salary);
    }
}
```

**Usage:**
```java
public class TestEmployee {
    public static void main(String[] args) {
        List<Employee> employees = Arrays.asList(
            new Employee("IT", "Alice", 85000),
            new Employee("HR", "Bob", 60000),
            new Employee("IT", "Charlie", 95000),
            new Employee("HR", "Diana", 65000),
            new Employee("IT", "Eve", 85000),
            new Employee("Sales", "Frank", 70000),
            new Employee("HR", "Grace", 65000),
            new Employee("Sales", "Henry", 75000)
        );
        
        Collections.sort(employees);
        
        System.out.println("Employees sorted by Department > Salary (desc) > Name:");
        System.out.println(String.format("%-15s %-15s %s", "Department", "Name", "Salary"));
        System.out.println("=".repeat(50));
        for (Employee e : employees) {
            System.out.println(e);
        }
    }
}
```

**Output:**
```
Employees sorted by Department > Salary (desc) > Name:
Department      Name            Salary
==================================================
HR              Diana           $65,000
HR              Grace           $65,000
HR              Bob             $60,000
IT              Charlie         $95,000
IT              Alice           $85,000
IT              Eve             $85,000
Sales           Henry           $75,000
Sales           Frank           $70,000
```

---

### Example 6: Score Class - Descending Order (High to Low)

```java
public class Score implements Comparable<Score> {
    String playerName;
    int points;
    LocalDateTime achievedDate;
    
    public Score(String playerName, int points, LocalDateTime achievedDate) {
        this.playerName = playerName;
        this.points = points;
        this.achievedDate = achievedDate;
    }
    
    @Override
    public int compareTo(Score obj) {
        // Descending order - reverse the comparison
        // Higher score comes first
        return Integer.compare(obj.points, this.points);
    }
    
    @Override
    public String toString() {
        return String.format("%s: %d points (achieved: %s)", 
            playerName, points, achievedDate.toLocalDate());
    }
}
```

**Usage:**
```java
public class TestScore {
    public static void main(String[] args) {
        List<Score> leaderboard = Arrays.asList(
            new Score("Alice", 1500, LocalDateTime.of(2025, 11, 10, 14, 30)),
            new Score("Bob", 2300, LocalDateTime.of(2025, 11, 12, 9, 15)),
            new Score("Charlie", 1800, LocalDateTime.of(2025, 11, 11, 16, 45)),
            new Score("Diana", 2100, LocalDateTime.of(2025, 11, 13, 11, 20)),
            new Score("Eve", 1900, LocalDateTime.of(2025, 11, 14, 13, 10))
        );
        
        Collections.sort(leaderboard);
        
        System.out.println("Leaderboard (Highest to Lowest):");
        System.out.println("=".repeat(60));
        int rank = 1;
        for (Score s : leaderboard) {
            System.out.println(rank++ + ". " + s);
        }
    }
}
```

**Output:**
```
Leaderboard (Highest to Lowest):
============================================================
1. Bob: 2300 points (achieved: 2025-11-12)
2. Diana: 2100 points (achieved: 2025-11-13)
3. Eve: 1900 points (achieved: 2025-11-14)
4. Charlie: 1800 points (achieved: 2025-11-11)
5. Alice: 1500 points (achieved: 2025-11-10)
```

---

### Example 7: Event Class - Compare by Date and Time

```java
public class Event implements Comparable<Event> {
    String title;
    LocalDateTime dateTime;
    String location;
    int attendees;
    
    public Event(String title, LocalDateTime dateTime, String location, int attendees) {
        this.title = title;
        this.dateTime = dateTime;
        this.location = location;
        this.attendees = attendees;
    }
    
    @Override
    public int compareTo(Event obj) {
        // Compare by date and time
        // LocalDateTime already implements Comparable
        return this.dateTime.compareTo(obj.dateTime);
    }
    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        return String.format("%-25s | %s | %s (%d attendees)", 
            title, dateTime.format(formatter), location, attendees);
    }
}
```

**Usage:**
```java
public class TestEvent {
    public static void main(String[] args) {
        List<Event> events = Arrays.asList(
            new Event("Team Meeting", 
                LocalDateTime.of(2025, 11, 20, 10, 0), "Room A", 12),
            new Event("Client Presentation", 
                LocalDateTime.of(2025, 11, 18, 14, 30), "Conference Hall", 25),
            new Event("Product Launch", 
                LocalDateTime.of(2025, 11, 25, 9, 0), "Auditorium", 200),
            new Event("Training Session", 
                LocalDateTime.of(2025, 11, 18, 9, 0), "Room B", 15),
            new Event("Year-End Party", 
                LocalDateTime.of(2025, 12, 15, 18, 0), "Rooftop", 100)
        );
        
        Collections.sort(events);
        
        System.out.println("Events Schedule (Chronological Order):");
        System.out.println("=".repeat(90));
        for (Event e : events) {
            System.out.println(e);
        }
    }
}
```

**Output:**
```
Events Schedule (Chronological Order):
==========================================================================================
Training Session          | Nov 18, 2025 09:00 | Room B (15 attendees)
Client Presentation       | Nov 18, 2025 14:30 | Conference Hall (25 attendees)
Team Meeting              | Nov 20, 2025 10:00 | Room A (12 attendees)
Product Launch            | Nov 25, 2025 09:00 | Auditorium (200 attendees)
Year-End Party            | Dec 15, 2025 18:00 | Rooftop (100 attendees)
```

---

### Example 8: Version Class - Compare Version Numbers

```java
public class Version implements Comparable<Version> {
    int major;
    int minor;
    int patch;
    
    public Version(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }
    
    public Version(String version) {
        String[] parts = version.split("\\.");
        this.major = Integer.parseInt(parts[0]);
        this.minor = Integer.parseInt(parts[1]);
        this.patch = Integer.parseInt(parts[2]);
    }
    
    @Override
    public int compareTo(Version obj) {
        // Compare major version first
        if (this.major != obj.major) {
            return Integer.compare(this.major, obj.major);
        }
        // If major is same, compare minor
        if (this.minor != obj.minor) {
            return Integer.compare(this.minor, obj.minor);
        }
        // If minor is same, compare patch
        return Integer.compare(this.patch, obj.patch);
    }
    
    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Version version = (Version) obj;
        return major == version.major && 
               minor == version.minor && 
               patch == version.patch;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(major, minor, patch);
    }
}
```

**Usage:**
```java
public class TestVersion {
    public static void main(String[] args) {
        List<Version> versions = Arrays.asList(
            new Version("2.1.5"),
            new Version("1.0.0"),
            new Version("2.0.1"),
            new Version("1.9.3"),
            new Version("2.1.0"),
            new Version("3.0.0"),
            new Version("1.9.10"),
            new Version("2.1.5") // duplicate
        );
        
        Collections.sort(versions);
        
        System.out.println("Versions sorted:");
        for (Version v : versions) {
            System.out.println("v" + v);
        }
        
        // Test equality
        System.out.println("\nTesting equality:");
        Version v1 = new Version("2.1.5");
        Version v2 = new Version("2.1.5");
        System.out.println("v1.equals(v2): " + v1.equals(v2));
        System.out.println("v1.compareTo(v2): " + v1.compareTo(v2));
    }
}
```

**Output:**
```
Versions sorted:
v1.0.0
v1.9.3
v1.9.10
v2.0.1
v2.1.0
v2.1.5
v2.1.5
v3.0.0

Testing equality:
v1.equals(v2): true
v1.compareTo(v2): 0
```

---

## Real-World Examples

### Example 9: Task Class - Priority-Based Sorting

```java
public class Task implements Comparable<Task> {
    enum Priority { LOW, MEDIUM, HIGH, URGENT }
    
    String description;
    Priority priority;
    LocalDate dueDate;
    boolean completed;
    
    public Task(String description, Priority priority, LocalDate dueDate) {
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.completed = false;
    }
    
    @Override
    public int compareTo(Task obj) {
        // Don't sort completed tasks the same as incomplete ones
        if (this.completed != obj.completed) {
            return this.completed ? 1 : -1; // Incomplete tasks first
        }
        
        // Compare by priority (URGENT first)
        if (this.priority != obj.priority) {
            return obj.priority.ordinal() - this.priority.ordinal();
        }
        
        // If same priority, compare by due date
        return this.dueDate.compareTo(obj.dueDate);
    }
    
    @Override
    public String toString() {
        String status = completed ? "[✓]" : "[ ]";
        return String.format("%s %-12s | %-40s | Due: %s", 
            status, priority, description, dueDate);
    }
    
    public void markComplete() {
        this.completed = true;
    }
}
```

**Usage:**
```java
public class TestTask {
    public static void main(String[] args) {
        List<Task> tasks = Arrays.asList(
            new Task("Fix critical bug in production", 
                Task.Priority.URGENT, LocalDate.of(2025, 11, 17)),
            new Task("Update documentation", 
                Task.Priority.LOW, LocalDate.of(2025, 11, 30)),
            new Task("Code review for new feature", 
                Task.Priority.HIGH, LocalDate.of(2025, 11, 20)),
            new Task("Prepare presentation for client", 
                Task.Priority.HIGH, LocalDate.of(2025, 11, 18)),
            new Task("Refactor database queries", 
                Task.Priority.MEDIUM, LocalDate.of(2025, 11, 25)),
            new Task("Team standup meeting", 
                Task.Priority.MEDIUM, LocalDate.of(2025, 11, 17))
        );
        
        // Mark one task as complete
        tasks.get(5).markComplete();
        
        Collections.sort(tasks);
        
        System.out.println("Task List (Priority Order):");
        System.out.println("=".repeat(80));
        for (Task t : tasks) {
            System.out.println(t);
        }
    }
}
```

**Output:**
```
Task List (Priority Order):
================================================================================
[ ] URGENT       | Fix critical bug in production         | Due: 2025-11-17
[ ] HIGH         | Prepare presentation for client        | Due: 2025-11-18
[ ] HIGH         | Code review for new feature            | Due: 2025-11-20
[ ] MEDIUM       | Refactor database queries              | Due: 2025-11-25
[ ] LOW          | Update documentation                   | Due: 2025-11-30
[✓] MEDIUM       | Team standup meeting                   | Due: 2025-11-17
```

---

### Example 10: File Metadata - Compare by Size

```java
public class FileMetadata implements Comparable<FileMetadata> {
    String name;
    long sizeInBytes;
    LocalDateTime lastModified;
    String extension;
    
    public FileMetadata(String name, long sizeInBytes, LocalDateTime lastModified) {
        this.name = name;
        this.sizeInBytes = sizeInBytes;
        this.lastModified = lastModified;
        this.extension = getFileExtension(name);
    }
    
    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return lastDot > 0 ? fileName.substring(lastDot + 1) : "";
    }
    
    @Override
    public int compareTo(FileMetadata obj) {
        // Compare by size (largest first)
        return Long.compare(obj.sizeInBytes, this.sizeInBytes);
    }
    
    private String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.2f MB", bytes / (1024.0 * 1024));
        return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
    }
    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return String.format("%-30s | %12s | %s", 
            name, formatSize(sizeInBytes), lastModified.format(formatter));
    }
}
```

**Usage:**
```java
public class TestFileMetadata {
    public static void main(String[] args) {
        List<FileMetadata> files = Arrays.asList(
            new FileMetadata("document.pdf", 2_457_600, 
                LocalDateTime.of(2025, 11, 10, 14, 30)),
            new FileMetadata("video.mp4", 157_286_400, 
                LocalDateTime.of(2025, 11, 12, 9, 15)),
            new FileMetadata("image.jpg", 3_145_728, 
                LocalDateTime.of(2025, 11, 11, 16, 45)),
            new FileMetadata("presentation.pptx", 8_388_608, 
                LocalDateTime.of(2025, 11, 13, 11, 20)),
            new FileMetadata("spreadsheet.xlsx", 524_288, 
                LocalDateTime.of(2025, 11, 14, 13, 10)),
            new FileMetadata("audio.mp3", 5_242_880, 
                LocalDateTime.of(2025, 11, 15, 10, 5)),
            new FileMetadata("archive.zip", 45_678_901, 
                LocalDateTime.of(2025, 11, 16, 8, 45))
        );
        
        Collections.sort(files);
        
        System.out.println("Files sorted by size (largest first):");
        System.out.println("=".repeat(70));
        for (FileMetadata f : files) {
            System.out.println(f);
        }
        
        // Calculate total size
        long totalSize = files.stream()
            .mapToLong(f -> f.sizeInBytes)
            .sum();
        System.out.println("=".repeat(70));
        System.out.println("Total size: " + files.get(0).formatSize(totalSize));
    }
}
```

**Output:**
```
Files sorted by size (largest first):
======================================================================
video.mp4                      |   150.00 MB | 2025-11-12 09:15
archive.zip                    |    43.56 MB | 2025-11-16 08:45
presentation.pptx              |     8.00 MB | 2025-11-13 11:20
audio.mp3                      |     5.00 MB | 2025-11-15 10:05
image.jpg                      |     3.00 MB | 2025-11-11 16:45
document.pdf                   |     2.34 MB | 2025-11-10 14:30
spreadsheet.xlsx               |   512.00 KB | 2025-11-14 13:10
======================================================================
Total size: 208.46 MB
```

---

### Example 11: BankAccount - Compare by Balance

```java
public class BankAccount implements Comparable<BankAccount> {
    String accountNumber;
    String accountHolder;
    double balance;
    String accountType; // "SAVINGS", "CHECKING", "BUSINESS"
    
    public BankAccount(String accountNumber, String accountHolder, 
                       double balance, String accountType) {
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.balance = balance;
        this.accountType = accountType;
    }
    
    @Override
    public int compareTo(BankAccount obj) {
        // Compare by balance (highest first)
        return Double.compare(obj.balance, this.balance);
    }
    
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }
    
    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            return true;
        }
        return false;
    }
    
    @Override
    public String toString() {
        return String.format("%-15s | %-25s | %-10s | $%,12.2f", 
            accountNumber, accountHolder, accountType, balance);
    }
}
```

**Usage:**
```java
public class TestBankAccount {
    public static void main(String[] args) {
        List<BankAccount> accounts = Arrays.asList(
            new BankAccount("ACC001", "John Doe", 15000.50, "CHECKING"),
            new BankAccount("ACC002", "Jane Smith", 45230.75, "SAVINGS"),
            new BankAccount("ACC003", "Bob Johnson", 5680.25, "CHECKING"),
            new BankAccount("ACC004", "Alice Brown", 125000.00, "BUSINESS"),
            new BankAccount("ACC005", "Charlie Wilson", 8920.80, "SAVINGS"),
            new BankAccount("ACC006", "Diana Davis", 67500.40, "BUSINESS")
        );
        
        Collections.sort(accounts);
        
        System.out.println("Bank Accounts sorted by balance (highest first):");
        System.out.println("=".repeat(85));
        System.out.printf("%-15s | %-25s | %-10s | %s%n", 
            "Account No.", "Account Holder", "Type", "Balance");
        System.out.println("=".repeat(85));
        for (Bank