package studentgrademanager;

import java.util.ArrayList;
import java.util.List;

final class ReportTest {
    private ReportTest() {
    }

    static void run() {
        GradeBook gradeBook = createReportGradeBook();

        StudentSnapshot transcript = gradeBook.getTranscript("S001");
        TestSupport.assertEquals("S001", transcript.getStudentId(), "Transcript should contain student ID");
        TestSupport.assertEquals("Amina", transcript.getName(), "Transcript should contain student name");
        TestSupport.assertTrue(transcript.getGradesBySubject().containsKey("Mathematics"),
                "Transcript should contain grades by subject");
        TestSupport.assertDoubleEquals(90.0, transcript.getAverage(), 0.0001,
                "Transcript should contain average");
        TestSupport.assertDoubleEquals(100.0, transcript.getHighestGrade(), 0.0001,
                "Transcript should contain highest grade");
        TestSupport.assertDoubleEquals(80.0, transcript.getLowestGrade(), 0.0001,
                "Transcript should contain lowest grade");
        TestSupport.assertTrue(transcript.isPassing(), "Transcript should contain pass/fail");
        TestSupport.assertEquals("A", transcript.getLetterGrade(), "Transcript should contain letter grade");

        List<SubjectGradeSummary> subjectSummaries = gradeBook.getSubjectSummaries();
        SubjectGradeSummary math = findSummary(subjectSummaries, "Mathematics");
        TestSupport.assertEquals(3, math.getGradeCount(), "Subject summary count should work");
        TestSupport.assertDoubleEquals(80.0, math.getAverage(), 0.0001, "Subject average should work");
        TestSupport.assertDoubleEquals(100.0, math.getHighestGrade(), 0.0001, "Subject highest should work");
        TestSupport.assertDoubleEquals(50.0, math.getLowestGrade(), 0.0001, "Subject lowest should work");

        GradeBookReport report = gradeBook.generateClassReport();
        TestSupport.assertEquals(3, report.getStudentCount(), "Class report student count should work");
        TestSupport.assertEquals(5, report.getTotalGradeCount(), "Class report grade count should work");
        TestSupport.assertDoubleEquals(80.0, report.getClassAverage(), 0.0001,
                "Class report class average should work");
        TestSupport.assertEquals("S001", report.getRankedStudents().get(0).getStudentId(),
                "Class report ranked students should work");
        TestSupport.assertEquals(1, report.getFailingStudents().size(),
                "Class report failing students should work");
        TestSupport.assertEquals(2, report.getSubjectSummaries().size(),
                "Class report subject summaries should work");
        TestSupport.assertEquals(report.getRankedStudents().get(0).getStudentId(),
                gradeBook.generateClassReport().getRankedStudents().get(0).getStudentId(),
                "Reports should be deterministic");
        TestSupport.assertEquals(report.getSubjectSummaries().get(0).getSubject(),
                gradeBook.generateClassReport().getSubjectSummaries().get(0).getSubject(),
                "Subject reports should be deterministic");

        TestSupport.assertThrows(UnsupportedOperationException.class,
                () -> report.getRankedStudents().add(transcript), "Ranked report list should be unmodifiable");
        TestSupport.assertThrows(UnsupportedOperationException.class,
                () -> report.getFailingStudents().add(transcript), "Failing report list should be unmodifiable");
        TestSupport.assertThrows(UnsupportedOperationException.class,
                () -> report.getSubjectSummaries().add(math), "Subject summary list should be unmodifiable");
        TestSupport.assertThrows(UnsupportedOperationException.class,
                () -> transcript.getGradesBySubject().put("Science", new ArrayList<Double>()),
                "Transcript grade map should be unmodifiable");
    }

    private static GradeBook createReportGradeBook() {
        GradeBook gradeBook = new GradeBook();
        gradeBook.addStudent("S001", "Amina");
        gradeBook.addStudent("S002", "Luca");
        gradeBook.addStudent("S003", "Mei");
        gradeBook.recordGrade("S001", "Mathematics", 80.0);
        gradeBook.recordGrade("S001", "Programming", 100.0);
        gradeBook.recordGrade("S002", "Mathematics", 50.0);
        gradeBook.recordGrade("S003", "Mathematics", 100.0);
        gradeBook.recordGrade("S003", "Programming", 70.0);
        return gradeBook;
    }

    private static SubjectGradeSummary findSummary(List<SubjectGradeSummary> summaries, String subject) {
        for (SubjectGradeSummary summary : summaries) {
            if (summary.getSubject().equals(subject)) {
                return summary;
            }
        }
        throw new AssertionError("Missing subject summary: " + subject);
    }
}
