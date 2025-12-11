# Exception Handling in Java Loops

This guide explains two different patterns for handling exceptions within loops, their use cases, and key differences.

---

## Pattern 1: Try-Catch Inside the Loop

### Concept
When errors might affect **a single iteration** of the loop, the try-catch block is nested **inside** the loop. If an exception occurs, execution moves to the catch block, handles the error, and then **continues with the next iteration** of the loop.

### Structure
```java
while (true) {
    try {
        // potential exceptions
    } catch (AnException e) {
        // handle the anomaly
    } // and continue with next iteration
}
```

### When to Use
- When an error in one iteration should NOT stop the entire loop
- When you want to process as many items as possible, even if some fail
- When errors are expected and recoverable for individual items

### Example: Processing Multiple Files
```java
import java.io.*;
import java.util.*;

public class FileProcessor {
    public static void main(String[] args) {
        List<String> filenames = Arrays.asList("file1.txt", "file2.txt", "file3.txt", "file4.txt");
        
        for (String filename : filenames) {
            try {
                // Attempt to read each file
                BufferedReader reader = new BufferedReader(new FileReader(filename));
                String content = reader.readLine();
                System.out.println("Successfully read " + filename + ": " + content);
                reader.close();
            } catch (IOException e) {
                // Handle error for this specific file
                System.err.println("Error reading " + filename + ": " + e.getMessage());
                // Loop continues with the next file
            }
        }
        
        System.out.println("Finished processing all files.");
    }
}
```

**Output (if file2.txt is missing):**
```
Successfully read file1.txt: [content]
Error reading file2.txt: file2.txt (No such file or directory)
Successfully read file3.txt: [content]
Successfully read file4.txt: [content]
Finished processing all files.
```

---

## Pattern 2: Try-Catch Outside the Loop

### Concept
When errors are **serious and compromise the entire loop**, the loop is nested **within** the try block. If an exception occurs, execution moves to the catch block, thus **exiting the loop entirely**.

### Structure
```java
try {
    while (true) {
        // potential exceptions
    }
} catch (AnException e) {
    // exit the loop and handle the anomaly
}
```

### When to Use
- When an error means the loop cannot meaningfully continue
- When encountering a fatal error that invalidates all subsequent iterations
- When you need to clean up resources and stop processing immediately

### Example: Database Transaction Processing
```java
import java.sql.*;

public class DatabaseProcessor {
    public static void main(String[] args) {
        Connection conn = null;
        
        try {
            // Establish database connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", "user", "pass");
            conn.setAutoCommit(false); // Start transaction
            
            int recordsToProcess = 100;
            int currentRecord = 0;
            
            while (currentRecord < recordsToProcess) {
                // Process each record as part of a transaction
                Statement stmt = conn.createStatement();
                stmt.executeUpdate("UPDATE accounts SET balance = balance + 100 WHERE id = " + currentRecord);
                System.out.println("Processed record " + currentRecord);
                currentRecord++;
            }
            
            conn.commit(); // Commit if all succeed
            System.out.println("All records processed successfully!");
            
        } catch (SQLException e) {
            // Critical error - exit loop and rollback entire transaction
            System.err.println("Database error occurred: " + e.getMessage());
            System.err.println("Rolling back all changes...");
            
            try {
                if (conn != null) {
                    conn.rollback();
                    System.err.println("Transaction rolled back.");
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Error during rollback: " + rollbackEx.getMessage());
            }
            
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}
```

**Output (if database connection fails at record 50):**
```
Processed record 0
Processed record 1
...
Processed record 49
Database error occurred: Connection lost
Rolling back all changes...
Transaction rolled back.
```

---

## Key Differences

| Aspect | Try-Catch Inside Loop | Try-Catch Outside Loop |
|--------|----------------------|------------------------|
| **Exception Scope** | Affects only current iteration | Affects entire loop |
| **Loop Behavior** | Continues to next iteration | Exits the loop completely |
| **Use Case** | Independent items that can fail individually | Dependent operations where one failure invalidates all |
| **Error Recovery** | Per-iteration recovery | Global error handling |
| **Example Scenarios** | Processing files, validating user inputs, API calls | Database transactions, resource initialization, critical sequences |

---

## Decision Guide

**Choose Try-Catch INSIDE the loop when:**
- ✅ Each iteration is independent
- ✅ You want to process as many items as possible
- ✅ Some failures are acceptable
- ✅ Example: Sending emails to multiple recipients

**Choose Try-Catch OUTSIDE the loop when:**
- ✅ All iterations must succeed or fail together
- ✅ An error makes continuing pointless or dangerous
- ✅ You need transactional behavior (all-or-nothing)
- ✅ Example: Multi-step initialization sequence

---

## Best Practices

1. **Be Specific**: Catch specific exception types rather than generic `Exception`
2. **Log Appropriately**: Different patterns may require different logging strategies
3. **Resource Management**: Always use try-with-resources or finally blocks for cleanup
4. **Don't Swallow Exceptions**: Always log or handle exceptions meaningfully
5. **Consider Business Logic**: Let your business requirements guide the pattern choice

---

## Summary

Both patterns are valuable tools in exception handling. The key is understanding whether exceptions should be contained to individual iterations or should halt the entire loop. This decision should be based on the relationship between iterations and the criticality of errors in your specific use case.