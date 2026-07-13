# Java Standard Exception Classes - Comprehensive Guide

This document provides detailed explanations and practical examples for all standard Java exception classes that do not require extension for usage.

---

## Table of Contents
1. [Error Class Exceptions](#error-class-exceptions)
2. [Exception Class Exceptions](#exception-class-exceptions)
3. [RuntimeException Class Exceptions](#runtimeexception-class-exceptions)

---

## Error Class Exceptions

### OutOfMemoryError

**Explanation:**

`OutOfMemoryError` is thrown by the Java Virtual Machine (JVM) when it cannot allocate an object because it is out of memory, and no more memory could be made available by the garbage collector. This is a critical error that indicates the JVM has exhausted its heap space or other memory areas. Common causes include memory leaks, attempting to create extremely large arrays or objects, excessive recursion, or insufficient heap size configuration.

This error typically indicates a serious problem that requires investigation into memory usage patterns, potential memory leaks, or JVM configuration. Unlike exceptions, errors are generally not meant to be caught and handled in normal application code, though they can be caught for logging or cleanup purposes.

**Example:**

```java
import java.util.ArrayList;
import java.util.List;

public class OutOfMemoryErrorExample {
    public static void main(String[] args) {
        try {
            // Attempting to create an infinite list that will exhaust heap memory
            List<byte[]> memoryFiller = new ArrayList<>();
            
            System.out.println("Starting to fill memory...");
            
            // Continuously allocate 10MB chunks until memory runs out
            while (true) {
                // Allocate 10MB at a time
                byte[] chunk = new byte[10 * 1024 * 1024];
                memoryFiller.add(chunk);
                System.out.println("Allocated another 10MB. Total size: " + 
                                   memoryFiller.size() + " chunks");
            }
            
        } catch (OutOfMemoryError e) {
            System.err.println("OutOfMemoryError caught!");
            System.err.println("Error message: " + e.getMessage());
            System.err.println("The JVM has run out of heap memory.");
            
            // Print memory statistics
            Runtime runtime = Runtime.getRuntime();
            long maxMemory = runtime.maxMemory() / (1024 * 1024);
            long totalMemory = runtime.totalMemory() / (1024 * 1024);
            long freeMemory = runtime.freeMemory() / (1024 * 1024);
            
            System.err.println("Max memory (MB): " + maxMemory);
            System.err.println("Total memory (MB): " + totalMemory);
            System.err.println("Free memory (MB): " + freeMemory);
            System.err.println("Used memory (MB): " + (totalMemory - freeMemory));
        }
    }
}

/*
 * To run this example with limited heap:
 * java -Xmx50m OutOfMemoryErrorExample
 * 
 * Expected Output:
 * Starting to fill memory...
 * Allocated another 10MB. Total size: 1 chunks
 * Allocated another 10MB. Total size: 2 chunks
 * Allocated another 10MB. Total size: 3 chunks
 * Allocated another 10MB. Total size: 4 chunks
 * OutOfMemoryError caught!
 * Error message: Java heap space
 * The JVM has run out of heap memory.
 * Max memory (MB): 47
 * Total memory (MB): 47
 * Free memory (MB): 1
 * Used memory (MB): 46
 */
```

---

## Exception Class Exceptions

### ClassNotFoundException

**Explanation:**

`ClassNotFoundException` is a checked exception thrown when an application tries to load a class through its string name using methods like `Class.forName()`, `ClassLoader.loadClass()`, or `ClassLoader.findSystemClass()`, but the class with the specified name cannot be found in the classpath. This exception indicates that the requested class is not available at runtime, even though the code compiled successfully.

Common scenarios include attempting to load JDBC drivers dynamically, loading classes through reflection when the JAR file is missing, typos in class names, incorrect package names, or classpath configuration issues. This differs from `NoClassDefFoundError`, which occurs when a class was present at compile time but is missing at runtime.

**Example:**

```java
import java.sql.Connection;
import java.sql.Driver;

public class ClassNotFoundExceptionExample {
    public static void main(String[] args) {
        System.out.println("=== ClassNotFoundException Example ===\n");
        
        // Example 1: Loading a JDBC driver that doesn't exist
        loadJDBCDriver("com.nonexistent.jdbc.Driver");
        
        // Example 2: Loading a class with incorrect name
        loadCustomClass("com.myapp.NonExistentClass");
        
        // Example 3: Loading a class that actually exists (MySQL driver if available)
        loadJDBCDriver("com.mysql.cj.jdbc.Driver");
    }
    
    /**
     * Attempts to load a JDBC driver by name
     */
    private static void loadJDBCDriver(String driverClassName) {
        System.out.println("Attempting to load JDBC driver: " + driverClassName);
        
        try {
            // This is the traditional way to load JDBC drivers
            Class<?> driverClass = Class.forName(driverClassName);
            System.out.println("✓ Successfully loaded driver: " + driverClass.getName());
            
            // Verify it's actually a JDBC driver
            if (Driver.class.isAssignableFrom(driverClass)) {
                System.out.println("✓ Confirmed: This is a valid JDBC Driver");
            }
            
        } catch (ClassNotFoundException e) {
            System.err.println("✗ ClassNotFoundException caught!");
            System.err.println("  Class name: " + e.getMessage());
            System.err.println("  Reason: The specified class is not in the classpath");
            System.err.println("  Solution: Ensure the JAR file containing this class is included");
            
            // Get additional information about the exception
            if (e.getCause() != null) {
                System.err.println("  Underlying cause: " + e.getCause().getMessage());
            }
        }
        System.out.println();
    }
    
    /**
     * Attempts to load a custom class by name
     */
    private static void loadCustomClass(String className) {
        System.out.println("Attempting to load custom class: " + className);
        
        try {
            ClassLoader classLoader = ClassNotFoundExceptionExample.class.getClassLoader();
            Class<?> loadedClass = classLoader.loadClass(className);
            
            System.out.println("✓ Successfully loaded: " + loadedClass.getName());
            System.out.println("  Package: " + loadedClass.getPackage());
            System.out.println("  ClassLoader: " + loadedClass.getClassLoader());
            
        } catch (ClassNotFoundException e) {
            System.err.println("✗ ClassNotFoundException caught!");
            System.err.println("  Requested class: " + className);
            System.err.println("  Error: " + e.getMessage());
            System.err.println("  Common causes:");
            System.err.println("    - Class doesn't exist in any JAR on classpath");
            System.err.println("    - Typo in class or package name");
            System.err.println("    - Missing dependency");
        }
        System.out.println();
    }
}

/*
 * Expected Output:
 * === ClassNotFoundException Example ===
 * 
 * Attempting to load JDBC driver: com.nonexistent.jdbc.Driver
 * ✗ ClassNotFoundException caught!
 *   Class name: com.nonexistent.jdbc.Driver
 *   Reason: The specified class is not in the classpath
 *   Solution: Ensure the JAR file containing this class is included
 * 
 * Attempting to load custom class: com.myapp.NonExistentClass
 * ✗ ClassNotFoundException caught!
 *   Requested class: com.myapp.NonExistentClass
 *   Error: com.myapp.NonExistentClass
 *   Common causes:
 *     - Class doesn't exist in any JAR on classpath
 *     - Typo in class or package name
 *     - Missing dependency
 * 
 * Attempting to load JDBC driver: com.mysql.cj.jdbc.Driver
 * ✗ ClassNotFoundException caught!
 *   Class name: com.mysql.cj.jdbc.Driver
 *   Reason: The specified class is not in the classpath
 *   Solution: Ensure the JAR file containing this class is included
 */
```

---

### InstantiationException

**Explanation:**

`InstantiationException` is a checked exception thrown when an application attempts to create an instance of a class using `Class.newInstance()` method, but the instantiation fails. This occurs when trying to instantiate an abstract class, an interface, an array class, a primitive type, void, or a class that doesn't have a no-argument constructor accessible to the caller.

This exception is commonly encountered when using reflection to dynamically create objects. It indicates a fundamental problem with the class being instantiated - either the class is not designed to be instantiated (like abstract classes or interfaces), or it lacks the necessary constructor. Modern Java code often uses `Constructor.newInstance()` instead of `Class.newInstance()` for better exception handling and to invoke constructors with parameters.

**Example:**

```java
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

// Abstract class that cannot be instantiated
abstract class AbstractVehicle {
    protected String brand;
    
    public abstract void start();
}

// Interface that cannot be instantiated
interface Drivable {
    void drive();
}

// Concrete class without no-arg constructor
class Car {
    private String model;
    private int year;
    
    // Only parameterized constructor - no no-arg constructor
    public Car(String model, int year) {
        this.model = model;
        this.year = year;
    }
    
    @Override
    public String toString() {
        return year + " " + model;
    }
}

// Properly instantiable class
class Motorcycle {
    private String brand = "Generic";
    
    // Has a no-arg constructor (implicit)
    
    @Override
    public String toString() {
        return brand + " Motorcycle";
    }
}

public class InstantiationExceptionExample {
    public static void main(String[] args) {
        System.out.println("=== InstantiationException Examples ===\n");
        
        // Example 1: Try to instantiate an abstract class
        tryInstantiateClass(AbstractVehicle.class, "Abstract Class");
        
        // Example 2: Try to instantiate an interface
        tryInstantiateClass(Drivable.class, "Interface");
        
        // Example 3: Try to instantiate a class without no-arg constructor
        tryInstantiateClass(Car.class, "Class without no-arg constructor");
        
        // Example 4: Successfully instantiate a proper class
        tryInstantiateClass(Motorcycle.class, "Properly instantiable class");
        
        // Example 5: Proper way to handle this with Constructor
        System.out.println("\n=== Proper Alternative Using Constructor ===");
        instantiateCarProperly();
    }
    
    /**
     * Attempts to instantiate a class using reflection
     */
    private static void tryInstantiateClass(Class<?> clazz, String description) {
        System.out.println("Attempting to instantiate: " + description);
        System.out.println("Class: " + clazz.getName());
        
        try {
            // This is the old deprecated way that can throw InstantiationException
            Object instance = clazz.newInstance();
            System.out.println("✓ Successfully created instance: " + instance);
            System.out.println("  Instance class: " + instance.getClass().getSimpleName());
            System.out.println("  Instance toString: " + instance.toString());
            
        } catch (InstantiationException e) {
            System.err.println("✗ InstantiationException caught!");
            System.err.println("  Error: " + e.getMessage());
            System.err.println("  Class: " + clazz.getName());
            
            // Analyze why instantiation failed
            if (java.lang.reflect.Modifier.isAbstract(clazz.getModifiers())) {
                System.err.println("  Reason: Cannot instantiate abstract class");
            } else if (clazz.isInterface()) {
                System.err.println("  Reason: Cannot instantiate interface");
            } else {
                System.err.println("  Reason: Class may not have accessible no-arg constructor");
            }
            
            System.err.println("  Solution: ");
            System.err.println("    - For abstract classes: Create a concrete subclass");
            System.err.println("    - For interfaces: Create an implementing class");
            System.err.println("    - For missing constructors: Use Constructor.newInstance() with parameters");
            
        } catch (IllegalAccessException e) {
            System.err.println("✗ IllegalAccessException: Constructor not accessible");
            System.err.println("  Error: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    /**
     * Demonstrates the proper way to instantiate a class with parameters
     */
    private static void instantiateCarProperly() {
        System.out.println("Creating Car instance using Constructor with parameters:");
        
        try {
            // Get the constructor that takes String and int parameters
            Class<Car> carClass = Car.class;
            Constructor<Car> constructor = carClass.getConstructor(String.class, int.class);
            
            // Create instance using the parameterized constructor
            Car myCar = constructor.newInstance("Tesla Model 3", 2024);
            
            System.out.println("✓ Successfully created Car instance: " + myCar);
            System.out.println("  Method used: Constructor.newInstance() with parameters");
            System.out.println("  This is the preferred approach over Class.newInstance()");
            
        } catch (NoSuchMethodException e) {
            System.err.println("✗ Constructor not found: " + e.getMessage());
        } catch (InstantiationException e) {
            System.err.println("✗ Cannot instantiate: " + e.getMessage());
        } catch (IllegalAccessException e) {
            System.err.println("✗ Cannot access constructor: " + e.getMessage());
        } catch (InvocationTargetException e) {
            System.err.println("✗ Constructor threw exception: " + e.getCause().getMessage());
        }
    }
}

/*
 * Expected Output:
 * === InstantiationException Examples ===
 * 
 * Attempting to instantiate: Abstract Class
 * Class: AbstractVehicle
 * ✗ InstantiationException caught!
 *   Error: AbstractVehicle
 *   Class: AbstractVehicle
 *   Reason: Cannot instantiate abstract class
 *   Solution: 
 *     - For abstract classes: Create a concrete subclass
 *     - For interfaces: Create an implementing class
 *     - For missing constructors: Use Constructor.newInstance() with parameters
 * 
 * Attempting to instantiate: Interface
 * Class: Drivable
 * ✗ InstantiationException caught!
 *   Error: Drivable
 *   Class: Drivable
 *   Reason: Cannot instantiate interface
 *   Solution: 
 *     - For abstract classes: Create a concrete subclass
 *     - For interfaces: Create an implementing class
 *     - For missing constructors: Use Constructor.newInstance() with parameters
 * 
 * Attempting to instantiate: Class without no-arg constructor
 * Class: Car
 * ✗ InstantiationException caught!
 *   Error: Car
 *   Class: Car
 *   Reason: Class may not have accessible no-arg constructor
 *   Solution: 
 *     - For abstract classes: Create a concrete subclass
 *     - For interfaces: Create an implementing class
 *     - For missing constructors: Use Constructor.newInstance() with parameters
 * 
 * Attempting to instantiate: Properly instantiable class
 * Class: Motorcycle
 * ✓ Successfully created instance: Generic Motorcycle
 *   Instance class: Motorcycle
 *   Instance toString: Generic Motorcycle
 * 
 * === Proper Alternative Using Constructor ===
 * Creating Car instance using Constructor with parameters:
 * ✓ Successfully created Car instance: 2024 Tesla Model 3
 *   Method used: Constructor.newInstance() with parameters
 *   This is the preferred approach over Class.newInstance()
 */
```

---

### IOException

**Explanation:**

`IOException` is a checked exception that signals an input/output operation has failed or been interrupted. This is one of the most commonly encountered exceptions in Java, occurring during file operations, network communications, stream processing, or any interaction with external resources. It serves as a parent class for many specific I/O-related exceptions like `FileNotFoundException`, `EOFException`, and `SocketException`.

Common scenarios include reading from or writing to files that don't exist or are inaccessible, network connection failures, disk space issues, permission problems, corrupted data streams, or unexpected end of file. Proper handling of `IOException` is crucial for robust applications, typically involving resource cleanup using try-with-resources, providing meaningful error messages to users, and implementing appropriate recovery or retry mechanisms.

**Example:**

```java
import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ArrayList;

public class IOExceptionExample {
    public static void main(String[] args) {
        System.out.println("=== IOException Examples ===\n");
        
        // Example 1: Reading from non-existent file
        readNonExistentFile();
        
        // Example 2: Writing to read-only location
        writeToReadOnlyLocation();
        
        // Example 3: Proper file operations with exception handling
        properFileOperations();
        
        // Example 4: Network-like operation (simulated)
        simulateNetworkOperation();
    }
    
    /**
     * Example 1: Attempting to read from a file that doesn't exist
     */
    private static void readNonExistentFile() {
        System.out.println("Example 1: Reading non-existent file");
        String filename = "/tmp/nonexistent_file_12345.txt";
        
        try {
            System.out.println("Attempting to read: " + filename);
            
            // Using BufferedReader to read file
            BufferedReader reader = new BufferedReader(
                new FileReader(filename)
            );
            
            String line = reader.readLine();
            System.out.println("First line: " + line);
            reader.close();
            
        } catch (FileNotFoundException e) {
            // FileNotFoundException is a subclass of IOException
            System.err.println("✗ FileNotFoundException (subclass of IOException) caught!");
            System.err.println("  File: " + filename);
            System.err.println("  Error: " + e.getMessage());
            System.err.println("  Reason: The specified file does not exist");
            
        } catch (IOException e) {
            System.err.println("✗ IOException caught!");
            System.err.println("  Error: " + e.getMessage());
            System.err.println("  Type: " + e.getClass().getSimpleName());
        }
        System.out.println();
    }
    
    /**
     * Example 2: Attempting to write to a read-only or restricted location
     */
    private static void writeToReadOnlyLocation() {
        System.out.println("Example 2: Writing to restricted location");
        
        // Try to write to system directory (typically requires root/admin)
        String filename = "/sys/kernel/debug/restricted_file.txt";
        
        try {
            System.out.println("Attempting to write to: " + filename);
            
            FileWriter writer = new FileWriter(filename);
            writer.write("This will likely fail due to permissions");
            writer.close();
            
            System.out.println("✓ Successfully wrote to file");
            
        } catch (IOException e) {
            System.err.println("✗ IOException caught!");
            System.err.println("  File: " + filename);
            System.err.println("  Error type: " + e.getClass().getSimpleName());
            System.err.println("  Message: " + e.getMessage());
            System.err.println("  Reason: Insufficient permissions or invalid path");
            System.err.println("  Common causes:");
            System.err.println("    - No write permission for the directory");
            System.err.println("    - Directory doesn't exist");
            System.err.println("    - Disk is full");
            System.err.println("    - File is locked by another process");
        }
        System.out.println();
    }
    
    /**
     * Example 3: Proper file operations with comprehensive error handling
     */
    private static void properFileOperations() {
        System.out.println("Example 3: Proper file operations with error handling");
        
        String filename = "/tmp/test_file_" + System.currentTimeMillis() + ".txt";
        List<String> dataLines = new ArrayList<>();
        dataLines.add("Line 1: Hello from Java");
        dataLines.add("Line 2: IOException handling example");
        dataLines.add("Line 3: Proper resource management");
        
        try {
            // Writing to file using try-with-resources (automatic cleanup)
            System.out.println("Writing data to: " + filename);
            
            try (BufferedWriter writer = new BufferedWriter(
                    new FileWriter(filename))) {
                
                for (String line : dataLines) {
                    writer.write(line);
                    writer.newLine();
                }
                System.out.println("✓ Successfully wrote " + dataLines.size() + " lines");
            }
            
            // Reading back from file
            System.out.println("Reading data back from file:");
            
            try (BufferedReader reader = new BufferedReader(
                    new FileReader(filename))) {
                
                String line;
                int lineNumber = 1;
                while ((line = reader.readLine()) != null) {
                    System.out.println("  [" + lineNumber++ + "] " + line);
                }
                System.out.println("✓ Successfully read all lines");
            }
            
            // Clean up: delete the test file
            Files.deleteIfExists(Paths.get(filename));
            System.out.println("✓ Test file cleaned up");
            
        } catch (IOException e) {
            System.err.println("✗ IOException during file operations!");
            System.err.println("  Error: " + e.getMessage());
            System.err.println("  Stack trace:");
            e.printStackTrace(System.err);
            
            // Log additional details
            System.err.println("  Failed operation details:");
            System.err.println("    - File path: " + filename);
            System.err.println("    - Exception type: " + e.getClass().getName());
            
            if (e.getCause() != null) {
                System.err.println("    - Root cause: " + e.getCause().getMessage());
            }
        }
        System.out.println();
    }
    
    /**
     * Example 4: Simulated network operation that can fail
     */
    private static void simulateNetworkOperation() {
        System.out.println("Example 4: Simulated network operation");
        
        try {
            System.out.println("Simulating data transfer...");
            
            // Simulate reading from a stream that might fail
            ByteArrayInputStream inputStream = new ByteArrayInputStream(
                "Network data".getBytes(StandardCharsets.UTF_8)
            );
            
            // Simulate an interrupted stream
            inputStream.close(); // Close stream prematurely
            
            // Try to read from closed stream
            int data = inputStream.read();
            
            if (data != -1) {
                System.out.println("✓ Data received: " + (char) data);
            }
            
        } catch (IOException e) {
            System.err.println("✗ IOException during network operation!");
            System.err.println("  Error: " + e.getMessage());
            System.err.println("  Scenario: Stream was closed or interrupted");
            System.err.println("  In real applications, this could be:");
            System.err.println("    - Network connection lost");
            System.err.println("    - Server closed connection");
            System.err.println("    - Timeout occurred");
            System.err.println("    - Data corruption detected");
            System.err.println("  Recovery strategies:");
            System.err.println("    - Retry the operation");
            System.err.println("    - Use exponential backoff");
            System.err.println("    - Fall back to cached data");
            System.err.println("    - Notify user of connectivity issues");
        }
        System.out.println();
    }
}

/*
 * Expected Output:
 * === IOException Examples ===
 * 
 * Example 1: Reading non-existent file
 * Attempting to read: /tmp/nonexistent_file_12345.txt
 * ✗ FileNotFoundException (subclass of IOException) caught!
 *   File: /tmp/nonexistent_file_12345.txt
 *   Error: /tmp/nonexistent_file_12345.txt (No such file or directory)
 *   Reason: The specified file does not exist
 * 
 * Example 2: Writing to restricted location
 * Attempting to write to: /sys/kernel/debug/restricted_file.txt
 * ✗ IOException caught!
 *   File: /sys/kernel/debug/restricted_file.txt
 *   Error type: FileNotFoundException
 *   Message: /sys/kernel/debug/restricted_file.txt (Permission denied)
 *   Reason: Insufficient permissions or invalid path
 *   Common causes:
 *     - No write permission for the directory
 *     - Directory doesn't exist
 *     - Disk is full
 *     - File is locked by another process
 * 
 * Example 3: Proper file operations with error handling
 * Writing data to: /tmp/test_file_1234567890123.txt
 * ✓ Successfully wrote 3 lines
 * Reading data back from file:
 *   [1] Line 1: Hello from Java
 *   [2] Line 2: IOException handling example
 *   [3] Line 3: Proper resource management
 * ✓ Successfully read all lines
 * ✓ Test file cleaned up
 * 
 * Example 4: Simulated network operation
 * Simulating data transfer...
 * ✗ IOException during network operation!
 *   Error: Stream closed
 *   Scenario: Stream was closed or interrupted
 *   In real applications, this could be:
 *     - Network connection lost
 *     - Server closed connection
 *     - Timeout occurred
 *     - Data corruption detected
 *   Recovery strategies:
 *     - Retry the operation
 *     - Use exponential backoff
 *     - Fall back to cached data
 *     - Notify user of connectivity issues
 */
```

---

### InterruptedException

**Explanation:**

`InterruptedException` is a checked exception thrown when a thread is waiting, sleeping, or otherwise occupied, and another thread interrupts it using the `interrupt()` method. This exception is a fundamental part of Java's thread cooperation mechanism, allowing threads to be interrupted gracefully rather than being forcibly terminated. When a thread is interrupted, it can catch this exception and decide how to respond - typically by cleaning up resources and exiting, or by continuing execution after logging the interruption.

Common scenarios include interrupting threads that are sleeping via `Thread.sleep()`, waiting for locks with `wait()` or `join()`, or blocking on certain I/O operations. Proper handling is critical for responsive applications - ignoring interruptions can lead to applications that don't shut down cleanly, while improper handling can cause resource leaks or data corruption. Best practices include restoring the interrupt status if you can't handle it immediately, cleaning up resources properly, and allowing threads to terminate gracefully.

**Example:**

```java
import java.util.concurrent.*;
import java.util.ArrayList;
import java.util.List;

public class InterruptedExceptionExample {
    
    // Shared flag to coordinate output
    private static volatile boolean taskCompleted = false;
    
    public static void main(String[] args) {
        System.out.println("=== InterruptedException Examples ===\n");
        
        // Example 1: Basic Thread.sleep() interruption
        basicSleepInterruption();
        
        // Example 2: Worker thread with proper interruption handling
        workerThreadInterruption();
        
        // Example 3: ExecutorService with task cancellation
        executorServiceInterruption();
        
        // Example 4: Blocking queue with interruption
        blockingQueueInterruption();
    }
    
    /**
     * Example 1: Basic interruption of a sleeping thread
     */
    private static void basicSleepInterruption() {
        System.out.println("Example 1: Basic Thread.sleep() interruption");
        
        Thread sleepingThread = new Thread(() -> {
            try {
                System.out.println("[Sleeping Thread] Starting to sleep for 10 seconds...");
                System.out.println("[Sleeping Thread] Current time: " + 
                                   System.currentTimeMillis());
                
                // This sleep will be interrupted
                Thread.sleep(10000); // 10 seconds
                
                // This line will not execute if interrupted
                System.out.println("[Sleeping Thread] Woke up naturally after 10 seconds");
                
            } catch (InterruptedException e) {
                System.err.println("[Sleeping Thread] ✗ InterruptedException caught!");
                System.err.println("[Sleeping Thread]   Message: " + e.getMessage());
                System.err.println("[Sleeping Thread]   Thread was interrupted while sleeping");
                System.err.println("[Sleeping Thread]   Interrupted at: " + 
                                   System.currentTimeMillis());
                System.err.println("[Sleeping Thread]   Sleep duration: Less than intended");
                
                // Check interrupt status
                System.err.println("[Sleeping Thread]   Interrupt status (before clear): " + 
                                   Thread.currentThread().isInterrupted());
                
                // Restore interrupt status for proper propagation
                Thread.currentThread().interrupt();
                System.err.println("[Sleeping Thread]   Interrupt status (after restore): " + 
                                   Thread.currentThread().isInterrupted());
            }
            
            System.out.println("[Sleeping Thread] Thread finishing execution");
        });
        
        sleepingThread.start();
        
        try {
            // Let it sleep for a bit
            Thread.sleep(2000); // 2 seconds
            
            System.out.println("[Main Thread] Now interrupting the sleeping thread...");
            sleepingThread.interrupt(); // Interrupt the sleeping thread
            
            // Wait for thread to finish
            sleepingThread.join(3000);
            System.out.println("[Main Thread] Sleeping thread has finished");
            
        } catch (InterruptedException e) {
            System.err.println("[Main Thread] Main thread was interrupted!");
        }
        
        System.out.println();
    }
    
    /**
     * Example 2: Worker thread processing with proper interruption handling
     */
    private static void workerThreadInterruption() {
        System.out.println("Example 2: Worker thread with proper interruption handling");
        
        Thread workerThread = new Thread(new WorkerTask());
        workerThread.setName("Worker-1");
        workerThread.start();
        
        try {
            // Let worker run for 3 seconds
            Thread.sleep(3000);
            
            System.out.println("[Main Thread] Requesting worker to stop...");
            workerThread.interrupt();
            
            // Wait for worker to finish gracefully
            workerThread.join(5000);
            
            if (workerThread.isAlive()) {
                System.err.println("[Main Thread] Worker did not stop gracefully!");
            } else {
                System.out.println("[Main Thread] Worker stopped gracefully");
            }
            
        } catch (InterruptedException e) {
            System.err.println("[Main Thread] Main thread interrupted!");
        }
        
        System.out.println();
    }
    
    /**
     * Example 3: ExecutorService with task cancellation
     */
    private static void executorServiceInterruption() {
        System.out.println("Example 3: ExecutorService with task cancellation");
        
        ExecutorService executor = Executors.newSingleThreadExecutor();
        
        Future<?> future = executor.submit(() -> {
            try {
                System.out.println("[Task] Starting long-running computation...");
                
                for (int i = 1; i <= 10; i++) {
                    // Check for interruption
                    if (Thread.currentThread().isInterrupted()) {
                        System.out.println("[Task] Detected interrupt flag, stopping...");
                        return;
                    }
                    
                    System.out.println("[Task] Processing step " + i + " of 10...");
                    Thread.sleep(1000); // Simulate work
                }
                
                System.out.println("[Task] Computation completed successfully");
                
            } catch (InterruptedException e) {
                System.err.println("[Task] ✗ InterruptedException caught!");
                System.err.println("[Task]   The task was cancelled/interrupted");
                System.err.println("[Task]   Current step may be incomplete");
                System.err.println("[Task]   Performing cleanup...");
                
                // Cleanup code would go here
                System.err.println("[Task]   Cleanup completed");
                
                // Restore interrupt status
                Thread.currentThread().interrupt();
            }
        });
        
        try {
            // Let it run for 3 seconds, then cancel
            Thread.sleep(3000);
            
            System.out.println("[Main Thread] Cancelling the task...");
            boolean cancelled = future.cancel(true); // true = interrupt if running
            
            System.out.println("[Main Thread] Task cancelled: " + cancelled);
            System.out.println("[Main Thread] Task done: " + future.isDone());
            System.out.println("[Main Thread] Task cancelled: " + future.isCancelled());
            
        } catch (InterruptedException e) {
            System.err.println("[Main Thread] Main thread interrupted!");
        } finally {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        }
        
        System.out.println();
    }
    
    /**
     * Example 4: Blocking queue with interruption
     */
    private static void blockingQueueInterruption() {
        System.out.println("Example 4: Blocking queue with interruption");
        
        BlockingQueue<String> queue = new LinkedBlockingQueue<>(5);
        
        Thread consumer = new Thread(() -> {
            try {
                System.out.println("[Consumer] Waiting for items from queue...");
                
                while (!Thread.currentThread().isInterrupted()) {
                    // This will block until an item is available
                    String item = queue.take();
                    System.out.println("[Consumer] Received: " + item);
                    Thread.sleep(500); // Simulate processing
                }
                
            } catch (InterruptedException e) {
                System.err.println("[Consumer] ✗ InterruptedException caught!");
                System.err.println("[Consumer]   Interrupted while waiting on queue");
                System.err.println("[Consumer]   Current queue size: " + queue.size());
                System.err.println("[Consumer]   Stopping consumption gracefully");
                
                // Drain remaining items
                List<String> remaining = new ArrayList<>();
                queue.drainTo(remaining);
                if (!remaining.isEmpty()) {
                    System.err.println("[Consumer]   Unprocessed items: " + remaining);
                }
                
                Thread.currentThread().interrupt();
            }
            
            System.out.println("[Consumer] Consumer thread exiting");
        });
        
        consumer.start();
        
        try {
            // Add some items
            queue.put("Item-1");
            queue.put("Item-2");
            queue.put("Item-3");
            
            // Let consumer process a few items
            Thread.sleep(2000);
            
            // Add more items
            queue.put("Item-4");
            queue.put("Item-5");
            
            // Now interrupt the consumer
            Thread.sleep(1000);
            System.out.println("[Main Thread] Interrupting consumer...");
            consumer.interrupt();
            
            // Wait for consumer to finish
            consumer.join(3000);
            
        } catch (InterruptedException e) {
            System.err.println("[Main Thread] Main thread interrupted!");
        }
        
        System.out.println();
    }
    
    /**
     * Worker task that properly handles interruption
     */
    static class WorkerTask implements Runnable {
        @Override
        public void run() {
            System.out.println("[Worker] Starting work...");
            int workCount = 0;
            
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    // Simulate work
                    workCount++;
                    System.out.println("[Worker] Completed work unit #" + workCount);
                    
                    // Sleep between work units
                    Thread.sleep(800);
                    
                    // Check if we should continue
                    if (workCount >= 10) {
                        System.out.println("[Worker] Completed all planned work");
                        break;
                    }
                }
                
                // Check if we were interrupted
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("[Worker] Detected interruption flag");
                }
                
            } catch (InterruptedException e) {
                System.err.println("[Worker] ✗ InterruptedException caught!");
                System.err.println("[Worker]   Work interrupted at unit #" + workCount);
                System.err.println("[Worker]   Message: " + e.getMessage());
                System.err.println("[Worker]   Performing cleanup operations...");
                
                // Simulate cleanup
                try {
                    Thread.sleep(500);
                    System.err.println("[Worker]   Cleanup completed successfully");
                } catch (InterruptedException cleanupException) {
                    System.err.println("[Worker]   Cleanup also interrupted!");
                }
                
                // Restore interrupt status
                Thread.currentThread().interrupt();
            }
            
            System.out.println("[Worker] Total work units completed: " + workCount);
            System.out.println("[Worker] Worker thread exiting");
        }
    }
}

/*
 * Expected Output (may vary slightly due to timing):
 * === InterruptedException Examples ===
 * 
 * Example 1: Basic Thread.sleep() interruption
 * [Sleeping Thread] Starting to sleep for 10 seconds...
 * [Sleeping Thread] Current time: 1702345678901
 * [Main Thread] Now interrupting the sleeping thread...
 * [Sleeping Thread] ✗ InterruptedException caught!
 * [Sleeping Thread]   Message: sleep interrupted
 * [Sleeping Thread]   Thread was interrupted while sleeping
 * [Sleeping Thread]   Interrupted at: 1702345680905
 * [Sleeping Thread]   Sleep duration: Less than intended
 * [Sleeping Thread]   Interrupt status (before clear): false
 * [Sleeping Thread]   Interrupt status (after restore): true
 * [Sleeping Thread] Thread finishing execution
 * [Main Thread] Sleeping thread has finished
 * 
 * Example 2: Worker thread with proper interruption handling
 * [Worker] Starting work...
 * [Worker] Completed work unit #1
 * [Worker] Completed work unit #2
 * [Worker] Completed work unit #3
 * [Main Thread] Requesting worker to stop...
 * [Worker] ✗ InterruptedException caught!
 * [Worker]   Work interrupted at unit #3
 * [Worker]   Message: sleep interrupted
 * [Worker]   Performing cleanup operations...
 * [Worker]   Cleanup completed successfully
 * [Worker] Total work units completed: 3
 * [Worker] Worker thread exiting
 * [Main Thread] Worker stopped gracefully
 * 
 * Example 3: ExecutorService with task cancellation
 * [Task] Starting long-running computation...
 * [Task] Processing step 1 of 10...
 * [Task] Processing step 2 of 10...
 * [Task] Processing step 3 of 10...
 * [Main Thread] Cancelling the task...
 * [Main Thread] Task cancelled: true
 * [Main Thread] Task done: true
 * [Main Thread] Task cancelled: true
 * [Task] ✗ InterruptedException caught!
 * [Task]   The task was cancelled/interrupted
 * [Task]   Current step may be incomplete
 * [Task]   Performing cleanup...
 * [Task]   Cleanup completed
 * 
 * Example 4: Blocking queue with interruption
 * [Consumer] Waiting for items from queue...
 * [Consumer] Received: Item-1
 * [Consumer] Received: Item-2
 * [Consumer] Received: Item-3
 * [Consumer] Received: Item-4
 * [Main Thread] Interrupting consumer...
 * [Consumer] ✗ InterruptedException caught!
 * [Consumer]   Interrupted while waiting on queue
 * [Consumer]   Current queue size: 1
 * [Consumer]   Stopping consumption gracefully
 * [Consumer]   Unprocessed items: [Item-5]
 * [Consumer] Consumer thread exiting
 */
```

---

## RuntimeException Class Exceptions

### NullPointerException

**Explanation:**

`NullPointerException` is an unchecked runtime exception thrown when an application attempts to use `null` where an object is required. This includes invoking methods on a null object reference, accessing or modifying fields of a null object, taking the length of null as if it were an array, accessing or modifying slots of null as if it were an array, or throwing null as if it were a Throwable value. This is one of the most common exceptions in Java programming.

The exception occurs because Java doesn't allow dereferencing null pointers. Unlike some languages that might return default values or undefined behavior, Java explicitly throws this exception to prevent undefined behavior and potential security vulnerabilities. Common causes include uninitialized object references, methods returning null when objects are expected, incorrect API usage, logic errors in null checks, and improper handling of optional values. Modern Java versions provide helpful messages indicating which variable was null, making debugging easier.

**Example:**

```java
import java.util.*;

public class NullPointerExceptionExample {
    
    // Class variables that might be null
    private static String uninitializedString;
    private static List<String> uninitializedList;
    
    public static void main(String[] args) {
        System.out.println("=== NullPointerException Examples ===\n");
        
        // Example 1: Method invocation on null reference
        methodInvocationOnNull();
        
        // Example 2: Array access on null reference
        arrayAccessOnNull();
        
        // Example 3: Field access on null object
        fieldAccessOnNull();
        
        // Example 4: Unboxing null wrapper objects
        unboxingNullWrapper();
        
        // Example 5: Proper null handling strategies
        properNullHandling();
    }
    
    /**
     * Example 1: Attempting to invoke methods on null reference
     */
    private static void methodInvocationOnNull() {
        System.out.println("Example 1: Method invocation on null reference");
        
        String text = null;
        List<Integer> numbers = null;
        
        try {
            System.out.println("Attempting to call methods on null objects...");
            
            // This will throw NullPointerException
            int length = text.length();
            System.out.println("Text length: " + length);
            
        } catch (NullPointerException e) {
            System.err.println("✗ NullPointerException caught!");
            System.err.println("  Error message: " + e.getMessage());
            System.err.println("  Attempted operation: Calling length() on null String");
            System.err.println("  Variable name: text");
            System.err.println("  Variable value: null");
            
            // Stack trace shows exactly where the exception occurred
            System.err.println("  Location: " + e.getStackTrace()[0]);
        }
        
        try {
            // Another NullPointerException with different operation
            int size = numbers.size();
            System.out.println("List size: " + size);
            
        } catch (NullPointerException e) {
            System.err.println("✗ NullPointerException caught!");
            System.err.println("  Error message: " + e.getMessage());
            System.err.println("  Attempted operation: Calling size() on null List");
            System.err.println("  Common cause: Forgetting to initialize collection");
        }
        
        System.out.println();
    }
    
    /**
     * Example 2: Attempting to access array elements when array is null
     */
    private static void arrayAccessOnNull() {
        System.out.println("Example 2: Array access on null reference");
        
        int[] numbers = null;
        String[] names = null;
        
        try {
            System.out.println("Attempting to access array elements...");
            
            // This will throw NullPointerException
            int firstNumber = numbers[0];
            System.out.println("First number: " + firstNumber);
            
        } catch (NullPointerException e) {
            System.err.println("✗ NullPointerException caught!");
            System.err.println("  Error: Attempting to index into null array");
            System.err.println("  Array variable: numbers");
            System.err.println("  Array value: null");
            System.err.println("  Operation: numbers[0]");
        }
        
        try {
            // Checking array length on null also throws NullPointerException
            int length = names.length;
            System.out.println("Array length: " + length);
            
        } catch (NullPointerException e) {
            System.err.println("✗ NullPointerException caught!");
            System.err.println("  Error: Accessing .length on null array");
            System.err.println("  Note: Even .length property requires non-null array");
        }
        
        System.out.println();
    }
    
    /**
     * Example 3: Accessing fields of null objects
     */
    private static void fieldAccessOnNull() {
        System.out.println("Example 3: Field access on null object");
        
        Person person = null;
        Car car = null;
        
        try {
            System.out.println("Attempting to access fields of null objects...");
            
            // This will throw NullPointerException
            String name = person.name;
            System.out.println("Person name: " + name);
            
        } catch (NullPointerException e) {
            System.err.println("✗ NullPointerException caught!");
            System.err.println("  Error: Accessing field of null object");
            System.err.println("  Object type: Person");
            System.err.println("  Object value: null");
            System.err.println("  Attempted field access: person.name");
            System.err.println("  Common scenario: Method returned null when object expected");
        }
        
        try {
            // Chained field access - fails at first null
            String make = car.engine.manufacturer;
            System.out.println("Car manufacturer: " + make);
            
        } catch (NullPointerException e) {
            System.err.println("✗ NullPointerException caught!");
            System.err.println("  Error: Null in chained field access");
            System.err.println("  Expression: car.engine.manufacturer");
            System.err.println("  Failed at: car (null)");
            System.err.println("  Solution: Check each level before accessing next");
        }
        
        System.out.println();
    }
    
    /**
     * Example 4: Unboxing null wrapper objects
     */
    private static void unboxingNullWrapper() {
        System.out.println("Example 4: Unboxing null wrapper objects");
        
        Integer nullInteger = null;
        Boolean nullBoolean = null;
        Double nullDouble = null;
        
        try {
            System.out.println("Attempting to unbox null Integer...");
            
            // Auto-unboxing null throws NullPointerException
            int primitiveInt = nullInteger; // Unboxing happens here
            System.out.println("Value: " + primitiveInt);
            
        } catch (NullPointerException e) {
            System.err.println("✗ NullPointerException caught!");
            System.err.println("  Error: Auto-unboxing null wrapper object");
            System.err.println("  Wrapper type: Integer");
            System.err.println("  Primitive type: int");
            System.err.println("  Explanation: Cannot convert null to primitive value");
            System.err.println("  Common in: Map.get() returning null, then auto-unboxing");
        }
        
        try {
            // Conditional expression with null
            boolean result = nullBoolean ? true : false;
            System.out.println("Result: " + result);
            
        } catch (NullPointerException e) {
            System.err.println("✗ NullPointerException caught!");
            System.err.println("  Error: Unboxing null Boolean in conditional");
            System.err.println("  Expression requires boolean primitive");
        }
        
        try {
            // Arithmetic operation triggering unboxing
            double calculation = nullDouble * 2.0;
            System.out.println("Calculation result: " + calculation);
            
        } catch (NullPointerException e) {
            System.err.println("✗ NullPointerException caught!");
            System.err.println("  Error: Arithmetic operation on null wrapper");
            System.err.println("  Operation: nullDouble * 2.0");
            System.err.println("  Unboxing attempted for arithmetic");
        }
        
        System.out.println();
    }
    
    /**
     * Example 5: Proper null handling strategies
     */
    private static void properNullHandling() {
        System.out.println("Example 5: Proper null handling strategies");
        
        String possiblyNull = null;
        List<String> items = null;
        Map<String, Integer> scores = new HashMap<>();
        scores.put("Alice", 100);
        // Note: "Bob" key doesn't exist, will return null
        
        // Strategy 1: Explicit null checks
        System.out.println("Strategy 1: Explicit null checks");
        if (possiblyNull != null) {
            System.out.println("  Length: " + possiblyNull.length());
        } else {
            System.out.println("  ✓ String is null, avoiding NullPointerException");
        }
        
        // Strategy 2: Using Objects.requireNonNull()
        System.out.println("\nStrategy 2: Using Objects.requireNonNull()");
        try {
            String validated = Objects.requireNonNull(possiblyNull, 
                "String parameter cannot be null");
        } catch (NullPointerException e) {
            System.out.println("  ✓ Caught early with custom message: " + e.getMessage());
        }
        
        // Strategy 3: Using Optional
        System.out.println("\nStrategy 3: Using Optional");
        Optional<String> optional = Optional.ofNullable(possiblyNull);
        String result = optional.orElse("Default Value");
        System.out.println("  ✓ Result: " + result);
        
        optional.ifPresent(s -> System.out.println("  Length: " + s.length()));
        optional.ifPresentOrElse(
            s -> System.out.println("  Value exists: " + s),
            () -> System.out.println("  ✓ Value is absent, using alternative action")
        );
        
        // Strategy 4: Safe navigation with ternary
        System.out.println("\nStrategy 4: Safe navigation with ternary");
        int length = possiblyNull != null ? possiblyNull.length() : 0;
        System.out.println("  ✓ Safe length calculation: " + length);
        
        // Strategy 5: Defensive copying and initialization
        System.out.println("\nStrategy 5: Defensive initialization");
        List<String> safeList = items != null ? items : new ArrayList<>();
        System.out.println("  ✓ List size: " + safeList.size() + " (never null)");
        
        // Strategy 6: Using getOrDefault for maps
        System.out.println("\nStrategy 6: Using Map.getOrDefault()");
        int aliceScore = scores.getOrDefault("Alice", 0);
        int bobScore = scores.getOrDefault("Bob", 0);
        System.out.println("  ✓ Alice's score: " + aliceScore);
        System.out.println("  ✓ Bob's score (default): " + bobScore);
        
        // Strategy 7: Null-safe equals comparison
        System.out.println("\nStrategy 7: Null-safe equals comparison");
        String str1 = null;
        String str2 = "test";
        
        // Bad: str1.equals(str2) would throw NullPointerException
        // Good: Use Objects.equals()
        boolean areEqual = Objects.equals(str1, str2);
        System.out.println("  ✓ Null-safe comparison: " + areEqual);
        
        // Strategy 8: Stream null filtering
        System.out.println("\nStrategy 8: Stream null filtering");
        List<String> mixedList = Arrays.asList("A", null, "B", null, "C");
        long nonNullCount = mixedList.stream()
            .filter(Objects::nonNull)
            .count();
        System.out.println("  ✓ Non-null items: " + nonNullCount);
        
        System.out.println("\n✓ All null handling strategies demonstrated successfully!");
        System.out.println();
    }
    
    // Helper classes for examples
    static class Person {
        String name;
        int age;
        Address address;
    }
    
    static class Address {
        String street;
        String city;
    }
    
    static class Car {
        Engine engine;
        String model;
    }
    
    static class Engine {
        String manufacturer;
        int horsepower;
    }
}

/*
 * Expected Output:
 * === NullPointerException Examples ===
 * 
 * Example 1: Method invocation on null reference
 * Attempting to call methods on null objects...
 * ✗ NullPointerException caught!
 *   Error message: Cannot invoke "String.length()" because "text" is null
 *   Attempted operation: Calling length() on null String
 *   Variable name: text
 *   Variable value: null
 *   Location: NullPointerExceptionExample.methodInvocationOnNull(NullPointerExceptionExample.java:XX)
 * ✗ NullPointerException caught!
 *   Error message: Cannot invoke "java.util.List.size()" because "numbers" is null
 *   Attempted operation: Calling size() on null List
 *   Common cause: Forgetting to initialize collection
 * 
 * Example 2: Array access on null reference
 * Attempting to access array elements...
 * ✗ NullPointerException caught!
 *   Error: Attempting to index into null array
 *   Array variable: numbers
 *   Array value: null
 *   Operation: numbers[0]
 * ✗ NullPointerException caught!
 *   Error: Accessing .length on null array
 *   Note: Even .length property requires non-null array
 * 
 * Example 3: Field access on null object
 * Attempting to access fields of null objects...
 * ✗ NullPointerException caught!
 *   Error: Accessing field of null object
 *   Object type: Person
 *   Object value: null
 *   Attempted field access: person.name
 *   Common scenario: Method returned null when object expected
 * ✗ NullPointerException caught!
 *   Error: Null in chained field access
 *   Expression: car.engine.manufacturer
 *   Failed at: car (null)
 *   Solution: Check each level before accessing next
 * 
 * Example 4: Unboxing null wrapper objects
 * Attempting to unbox null Integer...
 * ✗ NullPointerException caught!
 *   Error: Auto-unboxing null wrapper object
 *   Wrapper type: Integer
 *   Primitive type: int
 *   Explanation: Cannot convert null to primitive value
 *   Common in: Map.get() returning null, then auto-unboxing
 * ✗ NullPointerException caught!
 *   Error: Unboxing null Boolean in conditional
 *   Expression requires boolean primitive
 * ✗ NullPointerException caught!
 *   Error: Arithmetic operation on null wrapper
 *   Operation: nullDouble * 2.0
 *   Unboxing attempted for arithmetic
 * 
 * Example 5: Proper null handling strategies
 * Strategy 1: Explicit null checks
 *   ✓ String is null, avoiding NullPointerException
 * 
 * Strategy 2: Using Objects.requireNonNull()
 *   ✓ Caught early with custom message: String parameter cannot be null
 * 
 * Strategy 3: Using Optional
 *   ✓ Result: Default Value
 *   ✓ Value is absent, using alternative action
 * 
 * Strategy 4: Safe navigation with ternary
 *   ✓ Safe length calculation: 0
 * 
 * Strategy 5: Defensive initialization
 *   ✓ List size: 0 (never null)
 * 
 * Strategy 6: Using Map.getOrDefault()
 *   ✓ Alice's score: 100
 *   ✓ Bob's score (default): 0
 * 
 * Strategy 7: Null-safe equals comparison
 *   ✓ Null-safe comparison: false
 * 
 * Strategy 8: Stream null filtering
 *   ✓ Non-null items: 3
 * 
 * ✓ All null handling strategies demonstrated successfully!
 */
```

---

### ClassCastException

**Explanation:**

`ClassCastException` is an unchecked runtime exception thrown when code attempts to cast an object to a class or interface of which it is not an instance. This exception indicates a type mismatch at runtime, even though the code compiled successfully. The cast operation fails because the actual runtime type of the object is incompatible with the target type specified in the cast.

Common scenarios include improper downcasting without instanceof checks, incorrect generic type assumptions when using raw types, casting objects retrieved from non-generic collections, misunderstanding inheritance hierarchies, and incorrect assumptions about object types in heterogeneous collections. The exception can be prevented by using the `instanceof` operator before casting, using proper generics to enforce compile-time type safety, or using pattern matching with instanceof in modern Java versions. This exception is particularly common when working with legacy code that uses raw types or when deserializing objects without proper type validation.

**Example:**

```java
import java.util.*;

public class ClassCastExceptionExample {
    public static void main(String[] args) {
        System.out.println("=== ClassCastException Examples ===\n");
        
        // Example 1: Basic downcasting error
        basicDowncastingError();
        
        // Example 2: Collection with mixed types (raw types)
        rawCollectionCasting();
        
        // Example 3: Incorrect sibling casting
        siblingClassCasting();
        
        // Example 4: Array type casting
        arrayTypeCasting();
        
        // Example 5: Proper type checking and casting
        properTypeCasting();
    }
    
    /**
     * Example 1: Incorrect downcasting in inheritance hierarchy
     */
    private static void basicDowncastingError() {
        System.out.println("Example 1: Basic downcasting error");
        
        // Upcasting is always safe
        Animal animal = new Animal("Generic Animal");
        System.out.println("Created Animal: " + animal.getName());
        
        // Now try to downcast Animal to Dog (incorrect)
        try {
            System.out.println("Attempting to cast Animal to Dog...");
            
            // This will throw ClassCastException
            Dog dog = (Dog) animal;
            dog.bark(); // This line won't execute
            
        } catch (ClassCastException e) {
            System.err.println("✗ ClassCastException caught!");
            System.err.println("  Error message: " + e.getMessage());
            System.err.println("  Attempted cast: Animal -> Dog");
            System.err.println("  Problem: animal is actually of type Animal, not Dog");
            System.err.println("  Rule: Can only downcast if object is actually of target type");
        }
        
        // Correct usage: Actual Dog object
        Animal actualDog = new Dog("Buddy");
        try {
            System.out.println("\nNow trying with actual Dog object...");
            Dog castedDog = (Dog) actualDog;
            castedDog.bark();
            System.out.println("✓ Successfully casted and called Dog-specific method");
            
        } catch (ClassCastException e) {
            System.err.println("✗ This shouldn't happen!");
        }
        
        System.out.println();
    }
    
    /**
     * Example 2: ClassCastException with raw types and collections
     */
    private static void rawCollectionCasting() {
        System.out.println("Example 2: Raw collection type confusion");
        
        // Using raw types (not recommended)
        List mixedList = new ArrayList();
        mixedList.add("String item");
        mixedList.add(Integer.valueOf(42));
        mixedList.add(new Dog("Rex"));
        mixedList.add(Double.valueOf(3.14));
        
        System.out.println("Created raw list with mixed types:");
        System.out.println("  Items: String, Integer, Dog, Double");
        
        // Try to process assuming all are Strings
        try {
            System.out.println("\nAttempting to cast all elements to String...");
            
            for (Object item : mixedList) {
                String str = (String) item; // Will fail on non-String items
                System.out.println("  Processed: " + str);
            }
            
        } catch (ClassCastException e) {
            System.err.println("✗ ClassCastException caught!");
            System.err.println("  Error: " + e.getMessage());
            System.err.println("  Problem: Assuming all list elements are Strings");
            System.err.println("  Reality: List contains multiple types");
            System.err.println("  Solution: Use generics or check types before casting");
        }
        
        // Safe iteration with type checking
        System.out.println("\n✓ Safe iteration with instanceof checks:");
        for (Object item : mixedList) {
            if (item instanceof String) {
                String str = (String) item;
                System.out.println("  String: " + str);
            } else if (item instanceof Integer) {
                Integer num = (Integer) item;
                System.out.println("  Integer: " + num);
            } else if (item instanceof Dog) {
                Dog dog = (Dog) item;
                System.out.println("  Dog: " + dog.getName());
            } else if (item instanceof Double) {
                Double dbl = (Double) item;
                System.out.println("  Double: " + dbl);
            }
        }
        
        System.out.println();
    }
    
    /**
     * Example 3: Attempting to cast between sibling classes
     */
    private static void siblingClassCasting() {
        System.out.println("Example 3: Sibling class casting error");
        
        Animal dog = new Dog("Max");
        Animal cat = new Cat("Whiskers");
        
        System.out.println("Created Dog: " + dog.getName());
        System.out.println("Created Cat: " + cat.getName());
        
        try {
            System.out.println("\nAttempting to cast Dog to Cat...");
            
            // Dog and Cat are siblings - cannot cast between them
            Cat impossibleCat = (Cat) dog;
            impossibleCat.meow(); // Won't execute
            
        } catch (ClassCastException e) {
            System.err.println("✗ ClassCastException caught!");
            System.err.println("  Error: " + e.getMessage());
            System.err.println("  Attempted cast: Dog -> Cat");
            System.err.println("  Problem: Dog and Cat are sibling classes");
            System.err.println("  Explanation: Siblings in class hierarchy cannot be cast");
            System.err.println("  Both are Animals, but Dog is not a Cat and vice versa");
        }
        
        // Demonstrate proper type checking
        System.out.println("\n✓ Proper type checking:");
        checkAndProcess(dog);
        checkAndProcess(cat);
        
        System.out.println();
    }
    
    /**
     * Example 4: Array type casting
     */
    private static void arrayTypeCasting() {
        System.out.println("Example 4: Array type casting");
        
        Object[] objectArray = new String[5];
        objectArray[0] = "Hello";
        objectArray[1] = "World";
        
        System.out.println("Created String array referenced as Object array");
        
        try {
            System.out.println("Attempting to cast to Integer array...");
            
            // Cannot cast String[] to Integer[]
            Integer[] intArray = (Integer[]) objectArray;
            System.out.println("First element: " + intArray[0]);
            
        } catch (ClassCastException e) {
            System.err.println("✗ ClassCastException caught!");
            System.err.println("  Error: " + e.getMessage());
            System.err.println("  Problem: String[] cannot be cast to Integer[]");
            System.err.println("  Reason: Array types are reified (type known at runtime)");
        }
        
        // Correct approach
        try {
            System.out.println("\nCorrect casting to actual type (String[])...");
            String[] stringArray = (String[]) objectArray;
            System.out.println("✓ Successfully cast to String array");
            System.out.println("  First element: " + stringArray[0]);
            System.out.println("  Second element: " + stringArray[1]);
            
        } catch (ClassCastException e) {
            System.err.println("✗ This shouldn't happen!");
        }
        
        System.out.println();
    }
    
    /**
     * Example 5: Proper type checking and safe casting
     */
    private static void properTypeCasting() {
        System.out.println("Example 5: Proper type checking and safe casting");
        
        List<Animal> animals = new ArrayList<>();
        animals.add(new Dog("Buddy"));
        animals.add(new Cat("Fluffy"));
        animals.add(new Dog("Charlie"));
        animals.add(new Animal("Generic"));
        animals.add(new Cat("Shadow"));
        
        System.out.println("Processing mixed animal list safely:");
        
        // Method 1: Traditional instanceof with casting
        System.out.println("\nMethod 1: Traditional instanceof");
        for (Animal animal : animals) {
            System.out.print("  " + animal.getName() + " - ");
            
            if (animal instanceof Dog) {
                Dog dog = (Dog) animal;
                dog.bark();
            } else if (animal instanceof Cat) {
                Cat cat = (Cat) animal;
                cat.meow();
            } else {
                System.out.println("Generic animal sound");
            }
        }
        
        // Method 2: Pattern matching (Java 16+)
        System.out.println("\nMethod 2: Pattern matching with instanceof");
        for (Animal animal : animals) {
            System.out.print("  " + animal.getName() + " - ");
            
            if (animal instanceof Dog dog) {
                // dog is automatically cast and available
                dog.bark();
            } else if (animal instanceof Cat cat) {
                // cat is automatically cast and available
                cat.meow();
            } else {
                System.out.println("Generic animal sound");
            }
        }
        
        // Method 3: Using getClass() for exact type checking
        System.out.println("\nMethod 3: Exact class checking with getClass()");
        long dogCount = animals.stream()
            .filter(a -> a.getClass() == Dog.class)
            .count();
        long catCount = animals.stream()
            .filter(a -> a.getClass() == Cat.class)
            .count();
        
        System.out.println("  Dogs: " + dogCount);
        System.out.println("  Cats: " + catCount);
        System.out.println("  Total animals: " + animals.size());
        
        System.out.println("\n✓ All type-safe operations completed successfully!");
        System.out.println();
    }
    
    /**
     * Helper method to demonstrate type checking
     */
    private static void checkAndProcess(Animal animal) {
        if (animal instanceof Dog) {
            Dog dog = (Dog) animal;
            System.out.println("  " + dog.getName() + " is a Dog");
            dog.bark();
        } else if (animal instanceof Cat) {
            Cat cat = (Cat) animal;
            System.out.println("  " + cat.getName() + " is a Cat");
            cat.meow();
        } else {
            System.out.println("  " + animal.getName() + " is a generic Animal");
        }
    }
    
    // Helper classes for examples
    static class Animal {
        private String name;
        
        public Animal(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
    }
    
    static class Dog extends Animal {
        public Dog(String name) {
            super(name);
        }
        
        public void bark() {
            System.out.println("Woof! Woof!");
        }
    }
    
    static class Cat extends Animal {
        public Cat(String name) {
            super(name);
        }
        
        public void meow() {
            System.out.println("Meow! Meow!");
        }
    }
}

/*
 * Expected Output:
 * === ClassCastException Examples ===
 * 
 * Example 1: Basic downcasting error
 * Created Animal: Generic Animal
 * Attempting to cast Animal to Dog...
 * ✗ ClassCastException caught!
 *   Error message: class Animal cannot be cast to class Dog
 *   Attempted cast: Animal -> Dog
 *   Problem: animal is actually of type Animal, not Dog
 *   Rule: Can only downcast if object is actually of target type
 * 
 * Now trying with actual Dog object...
 * Woof! Woof!
 * ✓ Successfully casted and called Dog-specific method
 * 
 * Example 2: Raw collection type confusion
 * Created raw list with mixed types:
 *   Items: String, Integer, Dog, Double
 * 
 * Attempting to cast all elements to String...
 *   Processed: String item
 * ✗ ClassCastException caught!
 *   Error: class java.lang.Integer cannot be cast to class java.lang.String
 *   Problem: Assuming all list elements are Strings
 *   Reality: List contains multiple types
 *   Solution: Use generics or check types before casting
 * 
 * ✓ Safe iteration with instanceof checks:
 *   String: String item
 *   Integer: 42
 *   Dog: Rex
 *   Double: 3.14
 * 
 * Example 3: Sibling class casting error
 * Created Dog: Max
 * Created Cat: Whiskers
 * 
 * Attempting to cast Dog to Cat...
 * ✗ ClassCastException caught!
 *   Error: class Dog cannot be cast to class Cat
 *   Attempted cast: Dog -> Cat
 *   Problem: Dog and Cat are sibling classes
 *   Explanation: Siblings in class hierarchy cannot be cast
 *   Both are Animals, but Dog is not a Cat and vice versa
 * 
 * ✓ Proper type checking:
 *   Max is a Dog
 * Woof! Woof!
 *   Whiskers is a Cat
 * Meow! Meow!
 * 
 * Example 4: Array type casting
 * Created String array referenced as Object array
 * Attempting to cast to Integer array...
 * ✗ ClassCastException caught!
 *   Error: [Ljava.lang.String; cannot be cast to [Ljava.lang.Integer;
 *   Problem: String[] cannot be cast to Integer[]
 *   Reason: Array types are reified (type known at runtime)
 * 
 * Correct casting to actual type (String[])...
 * ✓ Successfully cast to String array
 *   First element: Hello
 *   Second element: World
 * 
 * Example 5: Proper type checking and safe casting
 * Processing mixed animal list safely:
 * 
 * Method 1: Traditional instanceof
 *   Buddy - Woof! Woof!
 *   Fluffy - Meow! Meow!
 *   Charlie - Woof! Woof!
 *   Generic - Generic animal sound
 *   Shadow - Meow! Meow!
 * 
 * Method 2: Pattern matching with instanceof
 *   Buddy - Woof! Woof!
 *   Fluffy - Meow! Meow!
 *   Charlie - Woof! Woof!
 *   Generic - Generic animal sound
 *   Shadow - Meow! Meow!
 * 
 * Method 3: Exact class checking with getClass()
 *   Dogs: 2
 *   Cats: 2
 *   Total animals: 5
 * 
 * ✓ All type-safe operations completed successfully!
 */
```

---

## Summary

This guide covered all the standard Java exception classes shown in your presentation:

### Error Class
- **OutOfMemoryError**: JVM runs out of memory

### Exception Class
- **ClassNotFoundException**: Class not found when loading dynamically
- **InstantiationException**: Cannot instantiate a class (abstract, interface, or no no-arg constructor)
- **IOException**: Input/output operation failures
- **InterruptedException**: Thread interrupted while waiting/sleeping

### RuntimeException Class
- **NullPointerException**: Attempting to use null where an object is required
- **ClassCastException**: Invalid type casting at runtime

Each exception includes detailed explanations of when it occurs, common causes, and comprehensive code examples demonstrating both how the exception is triggered and proper handling techniques. These are all standard exceptions that can be used directly without creating custom subclasses.

---

**Best Practices:**
1. Use checked exceptions (IOException, ClassNotFoundException, etc.) for recoverable errors
2. Use unchecked exceptions (NullPointerException, ClassCastException) for programming errors
3. Always handle InterruptedException properly by either propagating or restoring interrupt status
4. Use try-with-resources for automatic resource management
5. Provide meaningful error messages and proper cleanup in catch blocks
6. Never ignore exceptions - at minimum, log them
7. Use modern Java features like Optional and pattern matching to prevent exceptions
8. Test exception handling paths as thoroughly as success paths