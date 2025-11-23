import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StreamFilteringOnly {

    public static void main(String[] args) {
        // Initial list with duplicates
        List<Integer> data = Arrays.asList(5, 1, 3, 5, 2, 1, 4, 6, 7);
        System.out.println("Original Data: " + data);
        
        // Apply the three methods
        List<Integer> result = data.stream()
            
            // 1. distinct()
            // Stream becomes: [5, 1, 3, 2, 4, 6, 7] (Duplicates 5 and 1 removed)
            .distinct() 
            
            // 2. skip(int n)
            // Skip the first 3 elements (5, 1, 3)
            // Stream becomes: [2, 4, 6, 7]
            .skip(3)     
            
            // 3. limit(int n)
            // Retain only the next 2 elements (2, 4)
            // Stream becomes: [2, 4]
            .limit(2)    
            
            // Terminal Operation: Collect the final result into a List
            .collect(Collectors.toList()); 

        System.out.println("Final Result: " + result);
        // Output: Final Result: [2, 4]
    }
}