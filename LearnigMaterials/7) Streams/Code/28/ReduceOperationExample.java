import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ReduceOperationExample {

    public static void main(String[] args) {
        
        // Data for demonstration
        List<Integer> numbers = Arrays.asList(5, 1, 8, 2, 4);
        List<String> words = Arrays.asList("Hello", "World", "Stream");
        
        System.out.println("--- Input Data ---");
        System.out.println("Numbers: " + numbers);
        System.out.println("Words: " + words);
        
        // ----------------------------------------------------------------------
        // 1. Summation (Standard Use Case: Identity + Accumulator)
        // Signature: T reduce(T identity, BinaryOperator<T> accumulator)
        // Identity: 0 (since 0 + X = X)
        // Accumulator: Integer::sum (or (a, b) -> a + b)
        // ----------------------------------------------------------------------
        System.out.println("\n--- 1. Summation ---");
        
        int sum = numbers.stream()
            .reduce(0, Integer::sum); 
            
        System.out.println("Total Sum of Numbers (5+1+8+2+4): " + sum);
        // Expected Output: 20


        // ----------------------------------------------------------------------
        // 2. Concatenation (Using Strings: Identity + Accumulator)
        // Identity: "" (empty string, since "" + X = X)
        // Accumulator: (s1, s2) -> s1 + s2
        // ----------------------------------------------------------------------
        System.out.println("\n--- 2. Concatenation ---");
        
        String combinedString = words.stream()
            .reduce("", (s1, s2) -> s1 + " " + s2);
        
        System.out.println("Combined String: " + combinedString.trim());
        // Expected Output: Hello World Stream


        // ----------------------------------------------------------------------
        // 3. Finding the Maximum (Standard Use Case: Identity + Accumulator)
        // Identity: Integer.MIN_VALUE (or 0 if all numbers are known to be positive)
        // Accumulator: Math::max (or Integer::max)
        // ----------------------------------------------------------------------
        System.out.println("\n--- 3. Finding Maximum ---");
        
        // Using Math.max for the merge function
        int max = numbers.stream()
            .reduce(Integer.MIN_VALUE, Math::max);
            
        System.out.println("Maximum Number: " + max);
        // Expected Output: 8
        
        
        // Optional: reduce without identity (returns Optional<T>)
        Optional<Integer> maxOptional = numbers.stream()
            .reduce(Math::max);
            
        maxOptional.ifPresent(m -> System.out.println("Max (using Optional): " + m));

    }
}