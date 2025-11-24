import java.util.List;
import java.util.stream.Collectors;

public class LimitDuplicateExample {
    public static void main(String[] args) {
        // A list with many duplicates at the start
        List<Integer> numbers = List.of(1, 1, 1, 2, 3, 4, 5);

        // We ask for the first 3 elements
        List<Integer> result = numbers.stream()
            .limit(3)
            .collect(Collectors.toList());

        // Output will be [1, 1, 1]
        // limit() did not try to find unique numbers.
        System.out.println("Result: " + result); 
    }
}