package cipipelinejavaapp;

public class GreetingService {
    public String createGreeting(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required.");
        }
        String cleanName = name.trim();
        if (cleanName.length() > 80) {
            throw new IllegalArgumentException("Name cannot exceed 80 characters.");
        }
        return "Hello, " + cleanName + "!";
    }
}
