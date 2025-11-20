import java.util.Collection;
import java.util.LinkedList;
import java.util.TreeSet;

// 1. The Person class definition
// IMPORTANT: It must implement Comparable so TreeSet knows how to sort it.
class Person implements Comparable<Person> {
    private String name;

    public Person(String name) {
        this.name = name;
    }

    // We override toString so 'System.out.println' prints the name, 
    // not the memory address (e.g., Person@15db9742)
    @Override
    public String toString() {
        return this.name;
    }

    // This method is REQUIRED for the TreeSet to work.
    // It tells Java how to order People (e.g., alphabetically).
    @Override
    public int compareTo(Person other) {
        return this.name.compareTo(other.name);
    }
}

// 2. The Main Class
public class CollectionExample {
    public static void main(String[] args) {
        
        // --- Start of code from the image ---

        // Create a LinkedList to hold Person objects
        Collection<Person> persons = new LinkedList<Person>();

        // Add a new Person named "Alice"
        persons.add(new Person("Alice"));

        // Print the size of the list (Output: 1)
        System.out.println(persons.size());

        // Create a TreeSet (a sorted collection)
        // This will crash if Person does not implement Comparable!
        Collection<Person> copy = new TreeSet<Person>();

        // Add all elements from the first list to the sorted set
        copy.addAll(persons); 

        // Convert the collection to a generic Object array
        Object[] array = copy.toArray();

        // Print the first element of the array (Output: Alice)
        System.out.println(array[0]);

        // --- End of code from the image ---
    }
}