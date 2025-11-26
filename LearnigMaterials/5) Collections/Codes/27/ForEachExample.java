import java.util.*;

public class ForEachExample {

    // 1. Simple Person class to make the code work
    static class Person {
        String name;
        Person(String name) { this.name = name; }
        
        // This makes sure it prints the name, not the memory address
        public String toString() { return "Person: " + name; }
    }

    public static void main(String[] args) {
        // 2. Create the data (Using ArrayList, which is an Iterable)
        List<Person> list = new ArrayList<>();
        list.add(new Person("Alice"));
        list.add(new Person("Bob"));
        list.add(new Person("Charlie"));

        // 3. Assign to Iterable (matching the image exactly)
        Iterable<Person> persons = list;

        // 4. The code from your image
        persons.forEach(p -> {
            System.out.println(p);
        });
    }
}