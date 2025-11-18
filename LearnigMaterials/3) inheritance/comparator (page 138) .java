import java.util.Arrays;
import java.util.Comparator;

public class Main {
    public static void main(String[] args) {
        
        // 1. Create the Student Array (Bottom box of your image)
        Student[] sv = { 
            new Student(11), 
            new Student(3), 
            new Student(7) 
        };

        // 2. Sort using an instance of the "StudentCmp" class
        Arrays.sort(sv, new StudentCmp());

        // 3. Print the results to check
        for (Student s : sv) {
            System.out.println("Student ID: " + s.id);
        }
    }
}

// ---------------------------------------------------------
// 4. The Comparator Class (Top box of your image)
// This is a separate class dedicated to comparing students
class StudentCmp implements Comparator {
    
    public int compare(Object a, Object b) {
        // Cast Objects to Students
        Student sa = (Student) a;
        Student sb = (Student) b;

        // FIXED: The image says 'a.id', but 'a' is an Object. 
        // We must use 'sa.id' and 'sb.id'.
        return sa.id - sb.id;
    }
}

// ---------------------------------------------------------
// 5. The Student Class (Required context)
class Student {
    int id;
    
    public Student(int id) {
        this.id = id;
    }
}