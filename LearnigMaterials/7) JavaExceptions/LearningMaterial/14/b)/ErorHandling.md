# Java Error Handling - Complete Guide with Full Working Examples

## Overview
Java provides four keywords for exception handling: `throw`, `throws`, `try`, and `catch`. Additionally, `Throwable` is the parent class for all exceptions and errors.

---

## 1. `throw` Keyword

### Purpose
Used to **explicitly throw an exception** from your code.

### Syntax
```java
throw new ExceptionType("Error message");
```

### Complete Working Example
```java
public class ThrowExample {
    
    // Method that validates age and throws exception if invalid
    public static void validateAge(int age) {
        if (age < 18) {
            // Explicitly throwing an exception
            throw new IllegalArgumentException("Age must be 18 or older. Provided: " + age);
        }
        System.out.println("✓ Age is valid: " + age);
    }
    
    // Method that validates email
    public static void validateEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        System.out.println("✓ Email is valid: " + email);
    }
    
    public static void main(String[] args) {
        System.out.println("=== throw Keyword Demo ===\n");
        
        // Test 1: Valid age
        try {
            validateAge(25);
        } catch (IllegalArgumentException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
        
        // Test 2: Invalid age
        try {
            validateAge(15);
        } catch (IllegalArgumentException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
        
        // Test 3: Valid email
        try {
            validateEmail("user@example.com");
        } catch (IllegalArgumentException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
        
        // Test 4: Invalid email
        try {
            validateEmail("invalid-email");
        } catch (IllegalArgumentException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
        
        System.out.println("\nProgram completed successfully!");
    }
}
```

### Output
```
=== throw Keyword Demo ===

✓ Age is valid: 25
✗ Error: Age must be 18 or older. Provided: 15
✓ Email is valid: user@example.com
✗ Error: Invalid email format

Program completed successfully!
```

### Key Points
- Used inside method body to explicitly throw exceptions
- Can throw only one exception at a time
- Must be followed by an instance of `Throwable` or its subclass
- Commonly used for input validation and business logic enforcement

---

## 2. `throws` Keyword

### Purpose
Used in method signature to **declare that a method might throw exceptions**. This informs the caller that they need to handle these exceptions.

### Syntax
```java
returnType methodName() throws ExceptionType1, ExceptionType2 {
    // method body
}
```

### Complete Working Example
```java
import java.io.*;

public class ThrowsExample {
    
    // Method declares it might throw IOException
    public static String readFile(String filename) throws IOException {
        System.out.println("Attempting to read file: " + filename);
        
        FileReader file = new FileReader(filename);
        BufferedReader reader = new BufferedReader(file);
        
        StringBuilder content = new StringBuilder();
        String line;
        
        while ((line = reader.readLine()) != null) {
            content.append(line).append("\n");
        }
        
        reader.close();
        System.out.println("✓ File read successfully!");
        return content.toString();
    }
    
    // Method that declares multiple exceptions
    public static void processData(String filename) throws IOException, NumberFormatException {
        String content = readFile(filename);
        int number = Integer.parseInt(content.trim());
        System.out.println("Parsed number: " + number);
    }
    
    // Method that calls another method with throws
    public static void handleFile(String filename) {
        try {
            String content = readFile(filename);
            System.out.println("File content length: " + content.length() + " characters");
        } catch (IOException e) {
            System.out.println("✗ Error reading file: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        System.out.println("=== throws Keyword Demo ===\n");
        
        // Test 1: Try to read non-existent file
        handleFile("nonexistent.txt");
        
        System.out.println("\n--- Creating a test file ---");
        
        // Create a test file
        try {
            FileWriter writer = new FileWriter("test.txt");
            writer.write("Hello, Java Exception Handling!");
            writer.close();
            System.out.println("✓ Test file created");
        } catch (IOException e) {
            System.out.println("✗ Could not create test file");
        }
        
        // Test 2: Read the created file
        System.out.println("\n--- Reading test file ---");
        handleFile("test.txt");
        
        // Test 3: Multiple exceptions
        System.out.println("\n--- Testing multiple exceptions ---");
        try {
            processData("test.txt");
        } catch (IOException e) {
            System.out.println("✗ IO Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("✗ Number Format Error: " + e.getMessage());
        }
        
        System.out.println("\nProgram completed!");
    }
}
```

### Output
```
=== throws Keyword Demo ===

Attempting to read file: nonexistent.txt
✗ Error reading file: nonexistent.txt (No such file or directory)

--- Creating a test file ---
✓ Test file created

--- Reading test file ---
Attempting to read file: test.txt
✓ File read successfully!
File content length: 33 characters

--- Testing multiple exceptions ---
Attempting to read file: test.txt
✓ File read successfully!
✗ Number Format Error: For input string: "Hello, Java Exception Handling!"

Program completed!
```

### Key Points
- Used in method signature to declare potential exceptions
- Can declare multiple exceptions separated by commas
- Delegates exception handling responsibility to the caller
- Required for checked exceptions (IOException, SQLException, etc.)

---

## 3. `try` Keyword

### Purpose
Defines a **block of code to be tested for exceptions** while it's being executed.

### Syntax
```java
try {
    // Code that might throw an exception
}
```

### Complete Working Example
```java
public class TryExample {
    
    // Method with array operations
    public static void arrayDemo() {
        System.out.println("\n--- Array Operations Demo ---");
        try {
            int[] numbers = {10, 20, 30, 40, 50};
            
            System.out.println("Array length: " + numbers.length);
            System.out.println("Accessing index 0: " + numbers[0]);
            System.out.println("Accessing index 2: " + numbers[2]);
            System.out.println("Accessing index 4: " + numbers[4]);
            
            // This will throw ArrayIndexOutOfBoundsException
            System.out.println("Accessing index 10: " + numbers[10]);
            
            // This line won't execute
            System.out.println("This message will not be printed");
            
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("✗ Exception caught: " + e.getMessage());
        }
        
        System.out.println("Array demo completed");
    }
    
    // Method with arithmetic operations
    public static void arithmeticDemo() {
        System.out.println("\n--- Arithmetic Operations Demo ---");
        try {
            int a = 100;
            int b = 0;
            
            System.out.println("a = " + a);
            System.out.println("b = " + b);
            System.out.println("Calculating a / b...");
            
            // This will throw ArithmeticException
            int result = a / b;
            
            System.out.println("Result: " + result);  // Won't execute
            
        } catch (ArithmeticException e) {
            System.out.println("✗ Exception caught: " + e.getMessage());
        }
        
        System.out.println("Arithmetic demo completed");
    }
    
    // Method with string operations
    public static void stringDemo() {
        System.out.println("\n--- String Operations Demo ---");
        try {
            String text = null;
            
            System.out.println("Text value: " + text);
            System.out.println("Checking text length...");
            
            // This will throw NullPointerException
            int length = text.length();
            
            System.out.println("Length: " + length);  // Won't execute
            
        } catch (NullPointerException e) {
            System.out.println("✗ Exception caught: Cannot invoke method on null object");
        }
        
        System.out.println("String demo completed");
    }
    
    public static void main(String[] args) {
        System.out.println("=== try Keyword Demo ===");
        
        arrayDemo();
        arithmeticDemo();
        stringDemo();
        
        System.out.println("\n=== All demos completed successfully! ===");
    }
}
```

### Output
```
=== try Keyword Demo ===

--- Array Operations Demo ---
Array length: 5
Accessing index 0: 10
Accessing index 2: 30
Accessing index 4: 50
✗ Exception caught: Index 10 out of bounds for length 5
Array demo completed

--- Arithmetic Operations Demo ---
a = 100
b = 0
Calculating a / b...
✗ Exception caught: / by zero
Arithmetic demo completed

--- String Operations Demo ---
Text value: null
Checking text length...
✗ Exception caught: Cannot invoke method on null object
String demo completed

=== All demos completed successfully! ===
```

### Key Points
- Must be followed by either `catch` or `finally` block (or both)
- Can have multiple catch blocks for different exception types
- When exception occurs, remaining code in try block is skipped
- Program continues after the catch block

---

## 4. `catch` Keyword

### Purpose
Defines the **exception handling code** that executes when a specific exception occurs in the try block.

### Syntax
```java
catch (ExceptionType variable) {
    // Exception handling code
}
```

### Complete Working Example
```java
public class CatchExample {
    
    // Method demonstrating multiple catch blocks
    public static void multiCatchDemo(String scenario) {
        System.out.println("\n--- Testing scenario: " + scenario + " ---");
        
        try {
            if (scenario.equals("number")) {
                // NumberFormatException
                int number = Integer.parseInt("abc123");
                System.out.println("Number: " + number);
                
            } else if (scenario.equals("null")) {
                // NullPointerException
                String text = null;
                System.out.println("Length: " + text.length());
                
            } else if (scenario.equals("array")) {
                // ArrayIndexOutOfBoundsException
                int[] arr = {1, 2, 3};
                System.out.println("Value: " + arr[10]);
                
            } else if (scenario.equals("divide")) {
                // ArithmeticException
                int result = 10 / 0;
                System.out.println("Result: " + result);
                
            } else {
                System.out.println("✓ No exception in this scenario");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("✗ NumberFormatException caught!");
            System.out.println("   Message: " + e.getMessage());
            System.out.println("   Reason: Invalid number format");
            
        } catch (NullPointerException e) {
            System.out.println("✗ NullPointerException caught!");
            System.out.println("   Message: " + e.getMessage());
            System.out.println("   Reason: Trying to use null object");
            
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("✗ ArrayIndexOutOfBoundsException caught!");
            System.out.println("   Message: " + e.getMessage());
            System.out.println("   Reason: Array index out of range");
            
        } catch (ArithmeticException e) {
            System.out.println("✗ ArithmeticException caught!");
            System.out.println("   Message: " + e.getMessage());
            System.out.println("   Reason: Division by zero");
            
        } catch (Exception e) {
            System.out.println("✗ Generic Exception caught!");
            System.out.println("   Type: " + e.getClass().getName());
            System.out.println("   Message: " + e.getMessage());
        }
        
        System.out.println("Scenario completed\n");
    }
    
    // Method demonstrating catch with finally
    public static void catchWithFinallyDemo() {
        System.out.println("--- Catch with Finally Demo ---");
        
        try {
            System.out.println("Executing try block...");
            int result = 10 / 0;
            
        } catch (ArithmeticException e) {
            System.out.println("✗ Caught in catch block: " + e.getMessage());
            
        } finally {
            System.out.println("✓ Finally block always executes!");
        }
    }
    
    public static void main(String[] args) {
        System.out.println("=== catch Keyword Demo ===");
        
        // Test different exception scenarios
        multiCatchDemo("number");
        multiCatchDemo("null");
        multiCatchDemo("array");
        multiCatchDemo("divide");
        multiCatchDemo("valid");
        
        // Test catch with finally
        catchWithFinallyDemo();
        
        System.out.println("\n=== All catch demos completed! ===");
    }
}
```

### Output
```
=== catch Keyword Demo ===

--- Testing scenario: number ---
✗ NumberFormatException caught!
   Message: For input string: "abc123"
   Reason: Invalid number format
Scenario completed


--- Testing scenario: null ---
✗ NullPointerException caught!
   Message: Cannot invoke "String.length()" because "text" is null
   Reason: Trying to use null object
Scenario completed


--- Testing scenario: array ---
✗ ArrayIndexOutOfBoundsException caught!
   Message: Index 10 out of bounds for length 3
   Reason: Array index out of range
Scenario completed


--- Testing scenario: divide ---
✗ ArithmeticException caught!
   Message: / by zero
   Reason: Division by zero
Scenario completed


--- Testing scenario: valid ---
✓ No exception in this scenario
Scenario completed

--- Catch with Finally Demo ---
Executing try block...
✗ Caught in catch block: / by zero
✓ Finally block always executes!

=== All catch demos completed! ===
```

### Key Points
- Must follow a `try` block
- Can have multiple catch blocks for different exception types
- Order matters: specific exceptions must come before general ones
- Can use multi-catch: `catch (IOException | SQLException e)`
- Each catch block handles one type of exception

---

## 5. `Throwable` Class

### Purpose
The **superclass of all errors and exceptions** in Java. All exception types inherit from this class.

### Hierarchy
```
Throwable
├── Error (System-level problems, usually not caught)
│   ├── OutOfMemoryError
│   ├── StackOverflowError
│   └── VirtualMachineError
└── Exception (Application-level problems, should be caught)
    ├── IOException
    ├── SQLException
    └── RuntimeException (Unchecked exceptions)
        ├── NullPointerException
        ├── ArrayIndexOutOfBoundsException
        └── IllegalArgumentException
```

### Complete Working Example
```java
public class ThrowableExample {
    
    // Custom exception extending Exception
    static class CustomCheckedException extends Exception {
        public CustomCheckedException(String message) {
            super(message);
        }
        
        public CustomCheckedException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    // Custom exception extending RuntimeException
    static class CustomUncheckedException extends RuntimeException {
        public CustomUncheckedException(String message) {
            super(message);
        }
    }
    
    // Method demonstrating Throwable methods
    public static void demonstrateThrowableMethods() {
        System.out.println("\n--- Throwable Methods Demo ---");
        
        try {
            throw new Exception("This is a test exception");
            
        } catch (Throwable t) {
            System.out.println("✗ Exception caught as Throwable");
            System.out.println("   getMessage(): " + t.getMessage());
            System.out.println("   getClass(): " + t.getClass().getName());
            System.out.println("   toString(): " + t.toString());
            
            System.out.println("\n   Stack trace:");
            StackTraceElement[] stackTrace = t.getStackTrace();
            for (int i = 0; i < Math.min(3, stackTrace.length); i++) {
                System.out.println("      at " + stackTrace[i]);
            }
        }
    }
    
    // Method demonstrating exception chaining
    public static void demonstrateExceptionChaining() {
        System.out.println("\n--- Exception Chaining Demo ---");
        
        try {
            try {
                // Original exception
                int result = 10 / 0;
                
            } catch (ArithmeticException e) {
                // Wrap and re-throw with more context
                throw new CustomCheckedException("Error in calculation", e);
            }
            
        } catch (CustomCheckedException e) {
            System.out.println("✗ Caught custom exception:");
            System.out.println("   Message: " + e.getMessage());
            System.out.println("   Cause: " + e.getCause());
            System.out.println("   Root cause message: " + e.getCause().getMessage());
        }
    }
    
    // Method demonstrating custom exceptions
    public static void demonstrateCustomExceptions() {
        System.out.println("\n--- Custom Exceptions Demo ---");
        
        // Test custom checked exception
        try {
            System.out.println("Testing custom checked exception...");
            throw new CustomCheckedException("This is a custom checked exception");
            
        } catch (CustomCheckedException e) {
            System.out.println("✗ Caught: " + e.getMessage());
        }
        
        // Test custom unchecked exception
        try {
            System.out.println("\nTesting custom unchecked exception...");
            throw new CustomUncheckedException("This is a custom unchecked exception");
            
        } catch (CustomUncheckedException e) {
            System.out.println("✗ Caught: " + e.getMessage());
        }
    }
    
    // Method demonstrating throwable hierarchy
    public static void demonstrateThrowableHierarchy() {
        System.out.println("\n--- Throwable Hierarchy Demo ---");
        
        Exception ex = new Exception("Test exception");
        
        System.out.println("Exception instance checks:");
        System.out.println("   ex instanceof Throwable: " + (ex instanceof Throwable));
        System.out.println("   ex instanceof Exception: " + (ex instanceof Exception));
        System.out.println("   ex instanceof RuntimeException: " + (ex instanceof RuntimeException));
        
        RuntimeException rex = new RuntimeException("Runtime exception");
        System.out.println("\nRuntimeException instance checks:");
        System.out.println("   rex instanceof Throwable: " + (rex instanceof Throwable));
        System.out.println("   rex instanceof Exception: " + (rex instanceof Exception));
        System.out.println("   rex instanceof RuntimeException: " + (rex instanceof RuntimeException));
    }
    
    public static void main(String[] args) {
        System.out.println("=== Throwable Class Demo ===");
        
        demonstrateThrowableMethods();
        demonstrateExceptionChaining();
        demonstrateCustomExceptions();
        demonstrateThrowableHierarchy();
        
        System.out.println("\n=== All Throwable demos completed! ===");
    }
}
```

### Output
```
=== Throwable Class Demo ===

--- Throwable Methods Demo ---
✗ Exception caught as Throwable
   getMessage(): This is a test exception
   getClass(): java.lang.Exception
   toString(): java.lang.Exception: This is a test exception

   Stack trace:
      at ThrowableExample.demonstrateThrowableMethods(ThrowableExample.java:27)
      at ThrowableExample.main(ThrowableExample.java:108)

--- Exception Chaining Demo ---
✗ Caught custom exception:
   Message: Error in calculation
   Cause: java.lang.ArithmeticException: / by zero
   Root cause message: / by zero

--- Custom Exceptions Demo ---
Testing custom checked exception...
✗ Caught: This is a custom checked exception

Testing custom unchecked exception...
✗ Caught: This is a custom unchecked exception

--- Throwable Hierarchy Demo ---
Exception instance checks:
   ex instanceof Throwable: true
   ex instanceof Exception: true
   ex instanceof RuntimeException: false

RuntimeException instance checks:
   rex instanceof Throwable: true
   rex instanceof Exception: true
   rex instanceof RuntimeException: true

=== All Throwable demos completed! ===
```

### Key Points
- Root class for all exceptions and errors in Java
- Has two main subclasses: `Error` and `Exception`
- Provides important methods: `getMessage()`, `printStackTrace()`, `getCause()`
- Best practice: catch specific exception types rather than `Throwable`
- Supports exception chaining for better error context

---

## 6. Exception Generation (Custom Exceptions)

### Complete Working Example
```java
// Custom exception class
class EmptyStackException extends Exception {
    public EmptyStackException() {
        super("Stack is empty!");
    }
    
    public EmptyStackException(String message) {
        super(message);
    }
}

// Stack implementation
class Stack {
    private int[] data;
    private int size;
    private int capacity;
    
    public Stack(int capacity) {
        this.capacity = capacity;
        this.data = new int[capacity];
        this.size = 0;
    }
    
    // Method that throws custom exception
    public int pop() throws EmptyStackException {
        if (size == 0) {
            EmptyStackException e = new EmptyStackException("Cannot pop from empty stack");
            throw e;
        }
        
        int value = data[--size];
        System.out.println("✓ Popped: " + value);
        return value;
    }
    
    public void push(int value) {
        if (size < capacity) {
            data[size++] = value;
            System.out.println("✓ Pushed: " + value);
        } else {
            System.out.println("✗ Stack is full!");
        }
    }
    
    public int getSize() {
        return size;
    }
    
    public boolean isEmpty() {
        return size == 0;
    }
}

public class CustomExceptionExample {
    
    public static void main(String[] args) {
        System.out.println("=== Custom Exception Generation Demo ===\n");
        
        Stack stack = new Stack(5);
        
        // Test 1: Normal operations
        System.out.println("--- Test 1: Normal Push and Pop ---");
        stack.push(10);
        stack.push(20);
        stack.push(30);
        
        try {
            stack.pop();
            stack.pop();
        } catch (EmptyStackException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
        
        // Test 2: Pop from empty stack (will throw exception)
        System.out.println("\n--- Test 2: Pop Until Empty ---");
        try {
            stack.pop();  // Pop last element
            stack.pop();  // This will throw EmptyStackException
        } catch (EmptyStackException e) {
            System.out.println("✗ Caught EmptyStackException: " + e.getMessage());
        }
        
        // Test 3: Multiple operations
        System.out.println("\n--- Test 3: Refill and Empty ---");
        stack.push(100);
        stack.push(200);
        
        try {
            while (!stack.isEmpty()) {
                stack.pop();
            }
            // Try one more pop - should fail
            stack.pop();
        } catch (EmptyStackException e) {
            System.out.println("✗ Exception as expected: " + e.getMessage());
        }
        
        System.out.println("\n=== Demo completed successfully! ===");
    }
}
```

### Output
```
=== Custom Exception Generation Demo ===

--- Test 1: Normal Push and Pop ---
✓ Pushed: 10
✓ Pushed: 20
✓ Pushed: 30
✓ Popped: 30
✓ Popped: 20

--- Test 2: Pop Until Empty ---
✓ Popped: 10
✗ Caught EmptyStackException: Cannot pop from empty stack

--- Test 3: Refill and Empty ---
✓ Pushed: 100
✓ Pushed: 200
✓ Popped: 200
✓ Popped: 100
✗ Exception as expected: Cannot pop from empty stack

=== Demo completed successfully! ===
```

---

## 7. Exception Relay (Propagation)

### Complete Working Example
```java
class EmptyStackException extends Exception {
    public EmptyStackException(String message) {
        super(message);
    }
}

class Stack {
    private int[] data;
    private int size;
    
    public Stack(int capacity) {
        this.data = new int[capacity];
        this.size = 0;
    }
    
    public int pop() throws EmptyStackException {
        if (size == 0) {
            throw new EmptyStackException("Stack is empty");
        }
        return data[--size];
    }
    
    public void push(int value) {
        if (size < data.length) {
            data[size++] = value;
        }
    }
    
    public int getSize() {
        return size;
    }
}

class Dummy {
    Stack st;
    
    public Dummy() {
        st = new Stack(3);
    }
    
    // Method that relays (propagates) exception to caller
    // Note: method declares throws but doesn't catch
    public int foo() throws EmptyStackException {
        System.out.println("   foo() called");
        
        // This line might throw EmptyStackException
        // Exception is relayed to the caller
        int v = st.pop();
        
        System.out.println("   foo() returning: " + (v + 1));
        return v + 1;  // This line won't execute if exception occurs
    }
}

public class ExceptionRelayExample {
    
    public static void main(String[] args) throws EmptyStackException {
        System.out.println("=== Exception Relay Demo ===\n");
        
        Dummy d = new Dummy();
        
        // Test 1: Call with empty stack
        System.out.println("--- Test 1: Calling foo() on empty stack ---");
        try {
            System.out.println("Calling d.foo()...");
            int result = d.foo();  // Exception will be relayed here
            System.out.println("Result: " + result);  // Won't execute
        } catch (EmptyStackException e) {
            System.out.println("✗ Exception relayed to main: " + e.getMessage());
            System.out.println("   The 'return v + 1' line was not executed");
        }
        
        // Test 2: Call with data in stack
        System.out.println("\n--- Test 2: Calling foo() with data ---");
        d.st.push(5);
        d.st.push(10);
        
        try {
            System.out.println("Stack size: " + d.st.getSize());
            System.out.println("Calling d.foo()...");
            int result = d.foo();
            System.out.println("✓ Result: " + result);
            System.out.println("   The 'return v + 1' line was executed");
        } catch (EmptyStackException e) {
            System.out.println("✗ Exception: " + e.getMessage());
        }
        
        // Test 3: Multiple calls until exception
        System.out.println("\n--- Test 3: Multiple calls until exception ---");
        try {
            System.out.println("First call:");
            int result1 = d.foo();
            System.out.println("✓ Result: " + result1);
            
            System.out.println("\nSecond call (stack now empty):");
            int result2 = d.foo();  // Will throw exception
            System.out.println("Result: " + result2);  // Won't execute
            
        } catch (EmptyStackException e) {
            System.out.println("✗ Exception relayed: " + e.getMessage());
        }
        
        System.out.println("\n=== Relay demo completed! ===");
        System.out.println("\nKey Point: Exception was relayed (propagated) from");
        System.out.println("st.pop() → foo() → main() without being caught in foo()");
    }
}
```

### Output
```
=== Exception Relay Demo ===

--- Test 1: Calling foo() on empty stack ---
Calling d.foo()...
   foo() called
✗ Exception relayed to main: Stack is empty
   The 'return v + 1' line was not executed

--- Test 2: Calling foo() with data ---
Stack size: 2
Calling d.foo()...
   foo() called
   foo() returning: 11
✓ Result: 11
   The 'return v + 1' line was executed

--- Test 3: Multiple calls until exception ---
First call:
   foo() called
   foo() returning: 6
✓ Result: 6

Second call (stack now empty):
   foo() called
✗ Exception relayed: Stack is empty

=== Relay demo completed! ===

Key Point: Exception was relayed (propagated) from
st.pop() → foo() → main() without being caught in foo()
```

---

## 8. Catch and Handle

### Complete Working Example
```java
class EmptyStackException extends Exception {
    public EmptyStackException(String message) {
        super(message);
    }
}

class Stack {
    private int[] data;
    private int size;
    
    public Stack(int capacity) {
        this.data = new int[capacity];
        this.size = 0;
    }
    
    public int pop() throws EmptyStackException {
        if (size == 0) {
            throw new EmptyStackException("Stack is empty");
        }
        return data[--size];
    }
    
    public void push(int value) {
        if (size < data.length) {
            data[size++] = value;
        }
    }
}

class Dummy {
    Stack st;
    
    public Dummy() {
        st = new Stack(3);
    }
    
    // Method that catches and handles exception internally
    // Returns default value when exception occurs
    public int foo() {
        System.out.println("   foo() called");
        
        try {
            int v = st.pop();
            System.out.println("