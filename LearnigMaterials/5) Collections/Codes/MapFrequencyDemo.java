import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapFrequencyDemo {
    public static void main(String[] args) {
        List<String> words = List.of("java", "stream", "java", "map", "java");
        Map<String, Long> frequency = new HashMap<>();

        for (String word : words) {
            frequency.merge(word, 1L, Long::sum);
        }

        System.out.println(frequency);
    }
}
