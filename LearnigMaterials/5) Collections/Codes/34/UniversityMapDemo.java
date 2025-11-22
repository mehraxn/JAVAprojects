import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

// ---------------------------------------------------------
// 1. THE MAIN CLASS (Save file as UniversityMapDemo.java)
// ---------------------------------------------------------
public class UniversityMapDemo {
    public static void main(String[] args) {

        // A. Create the Map
        // Key: Student Object | Value: ReportCard Object
        Map<Student, ReportCard> semesterGrades = new HashMap<>();

        // B. Create our Data Objects
        Student s1 = new Student(101, "Alice");
        Student s2 = new Student(102, "Bob");
        Student s3 = new Student(103, "Charlie");

        ReportCard card1 = new ReportCard(4.0, "Excellent work");
        ReportCard card2 = new ReportCard(2.5, "Attendance issues");
        ReportCard card3 = new ReportCard(3.8, "Great improvement");

        // C. PUT: specific objects into the Map
        semesterGrades.put(s1, card1);
        semesterGrades.put(s2, card2);
        semesterGrades.put(s3, card3);

        System.out.println("--- Direct Lookup ---");
        // D. GET: Retrieve data using the original object
        System.out.println("Bob's Grade: " + semesterGrades.get(s2));

        System.out.println("\n--- Lookup by Logical Equality ---");
        // E. THE TRICK: We create a NEW object that happens to have the same ID as Alice
        // Because we implemented hashCode/equals below, the Map finds the original data!
        Student lookupSearch = new Student(101, "Alice");
        
        if (semesterGrades.containsKey(lookupSearch)) {
            System.out.println("Found Alice using a new key object!");
            System.out.println("Result: " + semesterGrades.get(lookupSearch));
        } else {
            System.out.println("Could not find student.");
        }

        System.out.println("\n--- Iterating (The 'entrySet' way) ---");
        // F. LOOP: efficient way to print both Key and Value
        for (Map.Entry<Student, ReportCard> entry : semesterGrades.entrySet()) {
            Student key = entry.getKey();
            ReportCard value = entry.getValue();
            
            System.out.println(key + " -> " + value);
        }
    }
}

// ---------------------------------------------------------
// 2. THE KEY OBJECT (Student)
// ---------------------------------------------------------
class Student {
    private int id;
    private String name;

    public Student(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // toString allows us to print the key nicely in the console
    @Override
    public String toString() {
        return "Student[ID:" + id + ", Name:" + name + "]";
    }

    // *** CRITICAL FOR MAP KEYS ***
    // 1. hashCode: Generates the "bucket" index. Must be based on the unique ID.
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // 2. equals: Confirms two objects are logically the same.
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Student other = (Student) obj;
        return id == other.id; // If IDs match, they are the same Key
    }
}

// ---------------------------------------------------------
// 3. THE VALUE OBJECT (ReportCard)
// ---------------------------------------------------------
class ReportCard {
    private double gpa;
    private String teacherComment;

    public ReportCard(double gpa, String teacherComment) {
        this.gpa = gpa;
        this.teacherComment = teacherComment;
    }

    @Override
    public String toString() {
        return "{ GPA: " + gpa + " | Note: " + teacherComment + " }";
    }
}