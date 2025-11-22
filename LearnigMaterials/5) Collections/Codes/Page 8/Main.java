import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        System.out.println("--- 1. Create and Add ---");
        SimpleArrayCollection<String> myCollection = new SimpleArrayCollection<>();
        myCollection.add("Apple");
        myCollection.add("Banana");
        myCollection.add("Cherry");
        
        System.out.println("Current size: " + myCollection.size()); // Output: 3
        System.out.println("Is Empty? " + myCollection.isEmpty());    // Output: false

        System.out.println("\n--- 2. Add All ---");
        ArrayList<String> moreFruits = new ArrayList<>();
        moreFruits.add("Date");
        moreFruits.add("Elderberry");
        
        myCollection.addAll(moreFruits);
        System.out.println("Size after addAll: " + myCollection.size()); // Output: 5

        System.out.println("\n--- 3. Contains Checks ---");
        System.out.println("Contains 'Banana'? " + myCollection.contains("Banana")); // true
        System.out.println("Contains 'Pizza'? " + myCollection.contains("Pizza"));   // false
        System.out.println("Contains All (Apple, Date)? " + 
            myCollection.containsAll(Arrays.asList("Apple", "Date"))); // true

        System.out.println("\n--- 4. Iteration ---");
        // This uses the iterator() method implicitly
        for(String fruit : myCollection) {
            System.out.print(fruit + " ");
        }
        // Output: Apple Banana Cherry Date Elderberry
        System.out.println();

        System.out.println("\n--- 5. Removal ---");
        myCollection.remove("Banana"); // Removes single item
        System.out.println("Removed Banana. Contains it? " + myCollection.contains("Banana"));

        myCollection.removeAll(moreFruits); // Removes Date and Elderberry
        System.out.println("Size after removeAll: " + myCollection.size()); // Should be 2 (Apple, Cherry)

        System.out.println("\n--- 6. To Array ---");
        Object[] arr = myCollection.toArray();
        System.out.println("Array contents: " + Arrays.toString(arr));

        System.out.println("\n--- 7. Clear ---");
        myCollection.clear();
        System.out.println("Size after clear: " + myCollection.size()); // 0
        System.out.println("Is Empty? " + myCollection.isEmpty());      // true
    }
}