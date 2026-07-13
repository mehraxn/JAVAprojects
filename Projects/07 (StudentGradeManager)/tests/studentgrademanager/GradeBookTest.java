package studentgrademanager;

import java.util.List;
import java.util.Optional;

final class GradeBookTest {
    private GradeBookTest() {
    }

    static void run() {
        GradeBook gradeBook = new GradeBook();
        StudentSnapshot added = gradeBook.addStudent(" S001 ", " Amina ");
        TestSupport.assertEquals("S001", added.getStudentId(), "Added student ID should be normalized");
        TestSupport.assertEquals("Amina", added.getName(), "Added student name should be normalized");
        TestSupport.assertThrows(IllegalArgumentException.class, () -> gradeBook.addStudent("S001", "Duplicate"),
                "Duplicate student ID should be rejected");
        TestSupport.assertThrows(IllegalArgumentException.class, () -> gradeBook.addStudent(null),
                "Null Student object should be rejected");

        Optional<StudentSnapshot> existing = gradeBook.getStudent("S001");
        TestSupport.assertTrue(existing.isPresent(), "Existing student should be found");
        TestSupport.assertFalse(gradeBook.getStudent("S999").isPresent(), "Missing student should be Optional.empty");
        TestSupport.assertEquals(1, gradeBook.listStudents().size(), "List students should include added student");
        TestSupport.assertThrows(UnsupportedOperationException.class,
                () -> gradeBook.listStudents().add(added), "List students result should be unmodifiable");

        TestSupport.assertTrue(gradeBook.removeStudent("S001"), "Existing student should be removed");
        TestSupport.assertFalse(gradeBook.removeStudent("S001"), "Removing missing student should return false");
        gradeBook.addStudent("S001", "Amina");

        StudentSnapshot afterGrade = gradeBook.recordGrade("S001", "Mathematics", 85.0);
        TestSupport.assertEquals(1, afterGrade.getGradeCount(), "recordGrade should add a grade");
        TestSupport.assertDoubleEquals(85.0, afterGrade.getAverage(), 0.0001, "recordGrade should update average");
        TestSupport.assertThrows(IllegalArgumentException.class,
                () -> gradeBook.recordGrade("S999", "Mathematics", 85.0), "Unknown student should be rejected");
        TestSupport.assertThrows(IllegalArgumentException.class,
                () -> gradeBook.recordGrade("S001", null, 85.0), "Null subject should be rejected");
        TestSupport.assertThrows(IllegalArgumentException.class,
                () -> gradeBook.recordGrade("S001", " ", 85.0), "Blank subject should be rejected");
        TestSupport.assertThrows(IllegalArgumentException.class,
                () -> gradeBook.recordGrade("S001", "Mathematics", -1.0), "Grade below 0 should be rejected");
        TestSupport.assertThrows(IllegalArgumentException.class,
                () -> gradeBook.recordGrade("S001", "Mathematics", 101.0), "Grade above 100 should be rejected");
        TestSupport.assertEquals(1, gradeBook.getTranscript("S001").getGradeCount(),
                "Failed grade recording should leave state unchanged");
        gradeBook.recordGrade("S001", "Mathematics", 0.0);
        gradeBook.recordGrade("S001", "Programming", 100.0);
        TestSupport.assertEquals(3, gradeBook.getTranscript("S001").getGradeCount(),
                "Boundary grades 0 and 100 should be accepted");

        StudentSnapshot transcript = gradeBook.getTranscript("S001");
        TestSupport.assertDoubleEquals(61.6667, transcript.getAverage(), 0.001,
                "Student average should be calculated");
        TestSupport.assertDoubleEquals(100.0, transcript.getHighestGrade(), 0.0001,
                "Highest grade should be calculated");
        TestSupport.assertDoubleEquals(0.0, transcript.getLowestGrade(), 0.0001,
                "Lowest grade should be calculated");
        TestSupport.assertTrue(transcript.isPassing(), "Student should pass with average above threshold");
        TestSupport.assertEquals("D", transcript.getLetterGrade(), "Student should have D letter grade");

        gradeBook.addStudent("S002", "Luca");
        gradeBook.recordGrade("S002", "Mathematics", 50.0);
        gradeBook.addStudent("S003", "Annette");
        gradeBook.recordGrade("S003", "Mathematics", 90.0);
        gradeBook.addStudent("S004", "Anna");
        gradeBook.recordGrade("S004", "Mathematics", 90.0);
        gradeBook.addStudent("S005", "No Grade");

        TestSupport.assertDoubleEquals(65.0, gradeBook.getClassAverage(), 0.0001,
                "Class average should include all recorded grades");
        TestSupport.assertDoubleEquals(0.0, gradeBook.getTranscript("S005").getAverage(), 0.0001,
                "No-grade student average should be 0.0");
        TestSupport.assertFalse(gradeBook.getTranscript("S005").isPassing(),
                "No-grade student should not be passing");

        TestSupport.assertEquals(2, gradeBook.searchStudentsByName("ann").size(),
                "Partial name search should find matching students");
        TestSupport.assertEquals(1, gradeBook.searchStudentsByName("AMINA").size(),
                "Search should be case-insensitive");
        TestSupport.assertEquals(0, gradeBook.searchStudentsByName("missing").size(),
                "Missing search should return an empty list");

        List<StudentSnapshot> ranked = gradeBook.listStudentsByAverageDescending();
        TestSupport.assertEquals("Anna", ranked.get(0).getName(),
                "Ranking should sort by average then name for ties");
        TestSupport.assertEquals("Annette", ranked.get(1).getName(),
                "Ranking tie-break should be deterministic");
        TestSupport.assertEquals("S005", ranked.get(ranked.size() - 1).getStudentId(),
                "No-grade students should appear at the bottom");
        TestSupport.assertEquals("Anna", gradeBook.findTopStudent().get().getName(),
                "Top student should be highest ranked student");

        List<StudentSnapshot> failingStudents = gradeBook.findFailingStudents();
        TestSupport.assertTrue(containsStudent(failingStudents, "S002"), "Failing report should include low average");
        TestSupport.assertTrue(containsStudent(failingStudents, "S005"), "Failing report should include no-grade student");
        TestSupport.assertThrows(UnsupportedOperationException.class,
                () -> failingStudents.add(transcript), "Failing students list should be unmodifiable");

        TestSupport.assertNotEquals("S999", gradeBook.getTranscript("S001").getStudentId(),
                "Transcript should return the requested student");
        TestSupport.assertThrows(IllegalArgumentException.class, () -> gradeBook.getTranscript("S999"),
                "Unknown transcript should be rejected");
        TestSupport.assertThrows(UnsupportedOperationException.class,
                () -> gradeBook.searchStudentsByName("ann").add(transcript), "Search results should be unmodifiable");
        TestSupport.assertThrows(UnsupportedOperationException.class,
                () -> gradeBook.listStudentsByAverageDescending().add(transcript),
                "Ranked students result should be unmodifiable");
    }

    private static boolean containsStudent(List<StudentSnapshot> students, String studentId) {
        for (StudentSnapshot student : students) {
            if (student.getStudentId().equals(studentId)) {
                return true;
            }
        }
        return false;
    }
}
