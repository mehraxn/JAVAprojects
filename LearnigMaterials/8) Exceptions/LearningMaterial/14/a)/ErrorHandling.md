# Java Exception Handling - Complete Guide

## Overview of Exception Handling Keywords

### 1. `throw` - Throws an Exception
The `throw` keyword is used to explicitly throw an exception from a method or block of code. You create an exception object and throw it.

**Example:**
```java
public class ThrowExample {
    public static void validateAge(int age) {
        if (age < 18) {
            throw new IllegalArgumentException("Age must be 18 or older");
        }
        System.out.println("Access granted!");
    }
    
    public static void main(String[] args) {
        validateAge(15); // This will throw an exception
    }
}
```

---

### 2. `throws` - Declare a Potential Exception
The `throws` keyword is used in method signatures to declare that a method might throw certain exceptions. It's a warning to callers that they need to handle these exceptions.

**Example:**
```java
import java.io.*;

public class ThrowsExample {
    public static void readFile(String filename) throws IOException {
        FileReader file = new FileReader(filename);
        BufferedReader reader = new BufferedReader(file);
        System.out.println(reader.readLine());
        reader.close();
    }
    
    public static void main(String[] args) {
        try {
            readFile("data.txt");
        } catch (IOException e) {
            System.out.println("File error: " + e.getMessage());
        }
    }
}
```

---

### 3. `try` - Introduces Code to Watch for Exceptions
The `try` block contains code that might throw an exception. It must be followed by either `catch` or `finally` (or both).

**Example:**
```java
public class TryExample {
    public static void main(String[] args) {
        try {
            int result = 10 / 0; // This will cause ArithmeticException
            System.out.println(result);
        } catch (ArithmeticException e) {
            System.out.println("Cannot divide by zero!");
        }
    }
}
```

---

### 4. `catch` - Defines the Exception Handling Code
The `catch` block handles exceptions that occur in the corresponding `try` block. You can have multiple catch blocks for different exception types.

**Example:**
```java
public class CatchExample {
    public static void main(String[] args) {
        try {
            String text = null;
            System.out.println(text.length()); // NullPointerException
        } catch (NullPointerException e) {
            System.out.println("Caught exception: " + e.getMessage());
            System.out.println("The string was null!");
        }
    }
}
```

---

## The `Throwable` Class

`Throwable` is the parent class of all errors and exceptions in Java. All exception classes inherit from it.

**Hierarchy:**
```
Throwable
├── Error (System errors - usually not handled)
└── Exception
    ├── RuntimeException (Unchecked exceptions)
    └── Other Exceptions (Checked exceptions)
```

**Example:**
```java
public class ThrowableExample {
    public static void main(String[] args) {
        try {
            causeException();
        } catch (Throwable t) {
            System.out.println("Caught: " + t.getClass().getName());
            System.out.println("Message: " + t.getMessage());
            t.printStackTrace();
        }
    }
    
    public static void causeException() {
        throw new RuntimeException("Something went wrong!");
    }
}
```

---

## `throw` vs `throws` - Complete Comparison

| Aspect | `throw` | `throws` |
|--------|---------|----------|
| **Purpose** | Actually throws an exception | Declares potential exceptions |
| **Location** | Inside method body | In method signature |
| **Syntax** | `throw new Exception()` | `throws Exception` |
| **Quantity** | One exception at a time | Multiple exceptions allowed |
| **Followed by** | Exception object | Exception class name(s) |
| **Used for** | Explicit exception throwing | Exception declaration |

### Key Differences Examples

#### Example 1: Basic Difference
```java
// Using throw - actually throwing an exception
public void method1() {
    throw new RuntimeException("Error occurred");
}

// Using throws - declaring possible exception
public void method2() throws IOException {
    // Might throw IOException, but doesn't have to
}
```

#### Example 2: Checked Exception Handling
```java
public class ThrowVsThrows {
    // Method using throws - declares exception
    public static void openFile() throws FileNotFoundException {
        FileReader file = new FileReader("nonexistent.txt");
    }
    
    // Method using throw - actually throws exception
    public static void validateInput(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
    }
    
    public static void main(String[] args) {
        // Must handle throws exception
        try {
            openFile();
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
        }
        
        // Can handle throw exception or let it propagate
        try {
            validateInput(null);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid input: " + e.getMessage());
        }
    }
}
```

#### Example 3: Multiple Exceptions
```java
public class MultipleExceptionsExample {
    // throws can declare multiple exceptions
    public static void processData() throws IOException, SQLException, ParseException {
        // Method that might throw any of these exceptions
    }
    
    // throw can only throw one at a time
    public static void validateData(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Negative value");
        }
        if (value > 100) {
            throw new IllegalArgumentException("Value too large");
        }
    }
}
```

#### Example 4: Real-World Scenario
```java
import java.io.*;

public class RealWorldExample {
    // Declaration with throws
    public static void readAndValidate(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line = reader.readLine();
        
        // Actual throwing with throw
        if (line == null || line.isEmpty()) {
            throw new IllegalArgumentException("File is empty!");
        }
        
        System.out.println("Read: " + line);
        reader.close();
    }
    
    public static void main(String[] args) {
        try {
            readAndValidate("data.txt");
        } catch (IOException e) {
            System.out.println("IO Error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Validation Error: " + e.getMessage());
        }
    }
}
```

---

## Quick Summary

- **`throw`**: Action word - actively throws an exception object
- **`throws`**: Declaration word - warns that exceptions might occur
- **`try`**: Protection block - wraps risky code
- **`catch`**: Handler block - deals with exceptions
- **`Throwable`**: The root class of all exceptions and errors

**Remember:** Use `throws` in method signatures to declare, use `throw` in method bodies to actually throw!