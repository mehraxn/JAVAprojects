import java.util.stream.Stream;

public class SimpleSum {

    // 1. This is the helper class "Acc" (Accumulator) from your image
    static class Acc {
        int n = 0; 
    }

    public static void main(String[] args) {
        
        // 2. We need some numbers to add up
        Integer[] numbers = { 10, 20, 30 };

        // 3. This is the code from your image
        int s = Stream.of(numbers).collect(
            Acc::new,                  // Create a new "Piggy Bank"
            (a, i) -> a.n += i,        // Add a number into the Piggy Bank
            (a1, a2) -> a1.n += a2.n   // Merge two Piggy Banks together
        ).n;                           // Get the final amount inside

        // 4. Print the result
        System.out.println("The sum is: " + s); 
    }
}