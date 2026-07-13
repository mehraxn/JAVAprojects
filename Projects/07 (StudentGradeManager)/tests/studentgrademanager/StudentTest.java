package studentgrademanager;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

final class StudentTest {
    private StudentTest() {
    }

    static void run() {
        Student student = new Student(" S001 ", " Amina ");
        TestSupport.assertTrue(Modifier.isFinal(Student.class.getModifiers()), "Student should be final");
        TestSupport.assertEquals("S001", student.getStudentId(), "Student ID should be trimmed and stored");
        TestSupport.assertEquals("S001", student.getId(), "getId should return the student ID");
        TestSupport.assertEquals("Amina", student.getName(), "Student name should be trimmed and stored");
        TestSupport.assertThrows(IllegalArgumentException.class, () -> new Student(null, "Amina"),
                "Null student ID should be rejected");
        TestSupport.assertThrows(IllegalArgumentException.class, () -> new Student(" ", "Amina"),
                "Blank student ID should be rejected");
        TestSupport.assertThrows(IllegalArgumentException.class, () -> new Student("S001", null),
                "Null student name should be rejected");
        TestSupport.assertThrows(IllegalArgumentException.class, () -> new Student("S001", " "),
                "Blank student name should be rejected");

        student.addGrade(" Mathematics ", 90.0);
        student.addGrade("Mathematics", 80.0);
        student.addGrade("Programming", 100.0);
        student.addGrade("History", 0.0);

        Map<String, List<Double>> grades = student.getAllGrades();
        TestSupport.assertTrue(grades.containsKey("Mathematics"), "Subject should be stored");
        TestSupport.assertEquals(2, grades.get("Mathematics").size(), "Multiple grades should use the same subject");
        TestSupport.assertEquals(1, grades.get("Programming").size(), "Different subjects should be tracked");
        TestSupport.assertDoubleEquals(0.0, grades.get("History").get(0), 0.0001, "Grade 0 should be accepted");
        TestSupport.assertDoubleEquals(100.0, grades.get("Programming").get(0), 0.0001, "Grade 100 should be accepted");
        TestSupport.assertEquals(4, student.getGradeCount(), "Grade count should include all subjects");
        TestSupport.assertDoubleEquals(67.5, student.getAverage(), 0.0001, "Average should include all grades");
        TestSupport.assertDoubleEquals(100.0, student.getHighestGrade(), 0.0001, "Highest grade should be calculated");
        TestSupport.assertDoubleEquals(0.0, student.getLowestGrade(), 0.0001, "Lowest grade should be calculated");
        TestSupport.assertTrue(student.isPassing(), "Average at or above 60 should pass");
        TestSupport.assertEquals("D", student.getLetterGrade(), "67.5 average should be a D");

        TestSupport.assertThrows(IllegalArgumentException.class, () -> student.addGrade("Math", -0.1),
                "Negative grade should be rejected");
        TestSupport.assertThrows(IllegalArgumentException.class, () -> student.addGrade("Math", 100.1),
                "Grade above 100 should be rejected");
        TestSupport.assertThrows(IllegalArgumentException.class, () -> student.addGrade(null, 90.0),
                "Null subject should be rejected");
        TestSupport.assertThrows(IllegalArgumentException.class, () -> student.addGrade(" ", 90.0),
                "Blank subject should be rejected");
        TestSupport.assertThrows(IllegalArgumentException.class, () -> student.addGrade("Math", Double.NaN),
                "NaN grade should be rejected");

        Student noGrades = new Student("S002", "Luca");
        TestSupport.assertDoubleEquals(0.0, noGrades.getAverage(), 0.0001, "No-grade average should be 0.0");
        TestSupport.assertDoubleEquals(0.0, noGrades.getHighestGrade(), 0.0001, "No-grade highest should be 0.0");
        TestSupport.assertDoubleEquals(0.0, noGrades.getLowestGrade(), 0.0001, "No-grade lowest should be 0.0");
        TestSupport.assertFalse(noGrades.isPassing(), "No-grade student should not pass");
        TestSupport.assertEquals("N/A", noGrades.getLetterGrade(), "No-grade letter should be N/A");

        TestSupport.assertTrue(studentWithAverage(60.0).isPassing(), "60.0 average should pass");
        TestSupport.assertFalse(studentWithAverage(59.99).isPassing(), "Below 60.0 average should fail");
        TestSupport.assertEquals("A", studentWithAverage(90.0).getLetterGrade(), "90 average should be A");
        TestSupport.assertEquals("B", studentWithAverage(80.0).getLetterGrade(), "80 average should be B");
        TestSupport.assertEquals("C", studentWithAverage(70.0).getLetterGrade(), "70 average should be C");
        TestSupport.assertEquals("D", studentWithAverage(60.0).getLetterGrade(), "60 average should be D");
        TestSupport.assertEquals("F", studentWithAverage(59.0).getLetterGrade(), "Below 60 average should be F");

        TestSupport.assertThrows(UnsupportedOperationException.class,
                () -> grades.put("Science", new ArrayList<Double>()), "Grades map should be unmodifiable");
        TestSupport.assertThrows(UnsupportedOperationException.class,
                () -> grades.get("Mathematics").add(70.0), "Grade lists should be unmodifiable");

        StudentSnapshot snapshot = student.toSnapshot();
        TestSupport.assertEquals("S001", snapshot.getStudentId(), "Snapshot should contain student ID");
        TestSupport.assertEquals("Amina", snapshot.getName(), "Snapshot should contain name");
        TestSupport.assertEquals(4, snapshot.getGradeCount(), "Snapshot should contain grade count");
        TestSupport.assertDoubleEquals(67.5, snapshot.getAverage(), 0.0001, "Snapshot should contain average");
        TestSupport.assertDoubleEquals(100.0, snapshot.getHighestGrade(), 0.0001, "Snapshot should contain highest");
        TestSupport.assertDoubleEquals(0.0, snapshot.getLowestGrade(), 0.0001, "Snapshot should contain lowest");
        TestSupport.assertTrue(snapshot.isPassing(), "Snapshot should contain pass/fail");
        TestSupport.assertEquals("D", snapshot.getLetterGrade(), "Snapshot should contain letter grade");
    }

    private static Student studentWithAverage(double average) {
        Student student = new Student("S" + average, "Student " + average);
        student.addGrade("Subject", average);
        return student;
    }
}
