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
            // INTERCEPTION HAPPENS HERE
            System.out.println("✗ Error: " + e.getMessage());
        }
        
        // Test 2: Invalid age
        try {
            validateAge(15);
        } catch (IllegalArgumentException e) {
            // INTERCEPTION HAPPENS HERE
            System.out.println("✗ Error: " + e.getMessage());
        }
        
        // Test 3: Valid email
        try {
            validateEmail("user@example.com");
        } catch (IllegalArgumentException e) {
            // INTERCEPTION HAPPENS HERE
            System.out.println("✗ Error: " + e.getMessage());
        }
        
        // Test 4: Invalid email
        try {
            validateEmail("invalid-email");
        } catch (IllegalArgumentException e) {
            // INTERCEPTION HAPPENS HERE
            System.out.println("✗ Error: " + e.getMessage());
        }
        
        System.out.println("\nProgram completed successfully!");
    }
}