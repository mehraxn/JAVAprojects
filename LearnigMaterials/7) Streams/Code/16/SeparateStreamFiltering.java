import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SeparateStreamFiltering {

    public static void main(String[] args) {
        // Initial Data for all demonstrations
        List<String> planets = Arrays.asList(
            "Mercury", "Venus", "Earth", "Mars", "Jupiter", "Venus", "Earth", "Saturn");
        System.out.println("Original List: " + planets);
        
        // --- 1. Distinct Operation ---
        // Creates a new stream from 'planets' and applies only distinct()
        List<String> resultDistinct = planets.stream()
            .distinct() // Removes duplicate "Venus" and "Earth"
            .collect(Collectors.toList()); 

        System.out.println("\n1. Result of distinct(): " + resultDistinct);
        // Output: [Mercury, Venus, Earth, Mars, Jupiter, Saturn]

        // --- 2. Limit Operation ---
        // Creates a new stream from 'planets' and applies only limit(n)
        int limitN = 4;
        List<String> resultLimit = planets.stream()
            .limit(limitN) // Keeps only the first 4 elements
            .collect(Collectors.toList()); 

        System.out.println("\n2. Result of limit(" + limitN + "): " + resultLimit);
        // Output: [Mercury, Venus, Earth, Mars]

        // --- 3. Skip Operation ---
        // Creates a new stream from 'planets' and applies only skip(n)
        int skipN = 5;
        List<String> resultSkip = planets.stream()
            .skip(skipN) // Discards the first 5 elements
            .collect(Collectors.toList());

        System.out.println("\n3. Result of skip(" + skipN + "): " + resultSkip);
        // Output: [Venus, Earth, Saturn]
    }
}