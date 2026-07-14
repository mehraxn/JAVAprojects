package cipipelinejavaapp;

public class Main {
    public static void main(String[] args) {
        GreetingService service = new GreetingService();
        String name = args.length == 0 ? "CI learner" : args[0];
        try {
            System.out.println(service.createGreeting(name));
        } catch (IllegalArgumentException exception) {
            System.err.println("Invalid name: " + exception.getMessage());
        }
    }
}
