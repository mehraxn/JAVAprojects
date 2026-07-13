package studentgrademanager;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

final class SnapshotTest {
    private SnapshotTest() {
    }

    static void run() {
        TestSupport.assertTrue(Modifier.isFinal(StudentSnapshot.class.getModifiers()),
                "StudentSnapshot should be final");
        TestSupport.assertTrue(Modifier.isFinal(SubjectGradeSummary.class.getModifiers()),
                "SubjectGradeSummary should be final");
        TestSupport.assertTrue(Modifier.isFinal(GradeBookReport.class.getModifiers()),
                "GradeBookReport should be final");
        TestSupport.assertTrue(Modifier.isFinal(GradeBook.class.getModifiers()),
                "GradeBook should be final");

        GradeBook gradeBook = new GradeBook();
        Student externalStudent = new Student("S001", "Amina");
        externalStudent.addGrade("Mathematics", 80.0);
        gradeBook.addStudent(externalStudent);
        externalStudent.addGrade("Mathematics", 100.0);

        TestSupport.assertEquals(1, gradeBook.getTranscript("S001").getGradeCount(),
                "GradeBook should copy Student input instead of storing external object");
        TestSupport.assertDoubleEquals(80.0, gradeBook.getTranscript("S001").getAverage(), 0.0001,
                "Mutating external Student should not mutate GradeBook state");

        gradeBook.addStudent("S002", "Luca");
        gradeBook.recordGrade("S002", "Mathematics", 40.0);
        gradeBook.addStudent("S003", "Mei");
        gradeBook.recordGrade("S003", "Programming", 90.0);

        StudentSnapshot listed = gradeBook.listStudents().get(0);
        StudentSnapshot ranked = gradeBook.listStudentsByAverageDescending().get(0);
        StudentSnapshot searched = gradeBook.searchStudentsByName("ami").get(0);
        StudentSnapshot failing = gradeBook.findFailingStudents().get(0);
        StudentSnapshot recorded = gradeBook.recordGrade("S001", "Programming", 90.0);
        StudentSnapshot transcript = gradeBook.getTranscript("S001");
        GradeBookReport report = gradeBook.generateClassReport();
        SubjectGradeSummary summary = gradeBook.getSubjectSummaries().get(0);

        TestSupport.assertThrows(UnsupportedOperationException.class,
                () -> gradeBook.listStudents().add(listed), "listStudents result should be unmodifiable");
        TestSupport.assertThrows(UnsupportedOperationException.class,
                () -> gradeBook.listStudentsByAverageDescending().add(ranked),
                "listStudentsByAverage result should be unmodifiable");
        TestSupport.assertThrows(UnsupportedOperationException.class,
                () -> gradeBook.searchStudentsByName("ami").add(searched), "Search result should be unmodifiable");
        TestSupport.assertThrows(UnsupportedOperationException.class,
                () -> gradeBook.findFailingStudents().add(failing), "Failing result should be unmodifiable");
        TestSupport.assertThrows(UnsupportedOperationException.class,
                () -> gradeBook.getSubjectSummaries().add(summary), "Subject summaries result should be unmodifiable");
        TestSupport.assertThrows(UnsupportedOperationException.class,
                () -> listed.getGradesBySubject().put("Injected Subject", new ArrayList<Double>()),
                "StudentSnapshot grade map should be unmodifiable");
        TestSupport.assertThrows(UnsupportedOperationException.class,
                () -> listed.getGradesBySubject().get("Mathematics").add(100.0),
                "StudentSnapshot grade lists should be unmodifiable");
        TestSupport.assertThrows(UnsupportedOperationException.class,
                () -> report.getRankedStudents().add(ranked), "GradeBookReport ranked list should be unmodifiable");
        TestSupport.assertThrows(UnsupportedOperationException.class,
                () -> report.getFailingStudents().add(failing), "GradeBookReport failing list should be unmodifiable");
        TestSupport.assertThrows(UnsupportedOperationException.class,
                () -> report.getSubjectSummaries().add(summary),
                "GradeBookReport subject summary list should be unmodifiable");

        TestSupport.assertEquals(2, recorded.getGradeCount(), "recordGrade should return a snapshot");
        TestSupport.assertEquals(2, transcript.getGradeCount(), "Transcript should return a snapshot");
        TestSupport.assertThrows(UnsupportedOperationException.class,
                () -> recorded.getGradesBySubject().get("Programming").add(100.0),
                "recordGrade snapshot should not mutate internal student");
        TestSupport.assertThrows(UnsupportedOperationException.class,
                () -> transcript.getGradesBySubject().get("Mathematics").add(100.0),
                "Transcript snapshot should not mutate internal student");

        double averageBeforeSnapshotAccess = gradeBook.getTranscript("S001").getAverage();
        List<Double> copiedGrades = new ArrayList<>(listed.getGradesBySubject().get("Mathematics"));
        copiedGrades.add(100.0);
        TestSupport.assertDoubleEquals(averageBeforeSnapshotAccess, gradeBook.getTranscript("S001").getAverage(),
                0.0001, "Copied public data should not affect internal averages");
        TestSupport.assertDoubleEquals(85.0, gradeBook.getTranscript("S001").getAverage(), 0.0001,
                "Internal averages should remain correct after accessing snapshots");
    }
}
