import java.util.List;
import java.util.stream.Collectors;

public class StringFilterExample {
    public static void main(String[] args) {
        List<String> words = List.of(
            "Java", 
            "Programming", 
            "Microservices", 
            "Spring", 
            "Internationalization" // This is 20 chars
        );
        
        // Filter: Keep strings where length is NOT bigger than 10 (<= 10)
        List<String> shortWords = words.stream()
            .filter(s -> s.length() <= 10)
            .collect(Collectors.toList());
        
        System.out.println("Short words: " + shortWords);
    }
}