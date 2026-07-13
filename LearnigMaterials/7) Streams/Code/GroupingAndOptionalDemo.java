import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class GroupingAndOptionalDemo {
    public static void main(String[] args) {
        List<String> words = List.of("apple", "banana", "cherry", "avocado", "blueberry");

        Map<Integer, List<String>> byLength = words.stream()
            .collect(Collectors.groupingBy(String::length));

        System.out.println(byLength.get(6));

        Optional<String> longest = words.stream()
            .max((a, b) -> Integer.compare(a.length(), b.length()));

        longest.ifPresent(System.out::println);

        DoubleSummaryStatistics stats = words.stream()
            .mapToDouble(String::length)
            .summaryStatistics();

        System.out.println("Average length: " + stats.getAverage());
    }
}
