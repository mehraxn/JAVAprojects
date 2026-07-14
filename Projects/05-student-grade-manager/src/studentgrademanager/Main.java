package studentgrademanager;

import java.io.PrintStream;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        int exitCode = run(args, System.out, System.err);
        System.exit(exitCode);
    }

    public static int run(String[] args, PrintStream out, PrintStream err) {
        String command = args.length == 0 ? "help" : args[0].trim().toLowerCase(Locale.ROOT);

        switch (command) {
            case "help":
                printHelp(out);
                return 0;
            case "demo":
                runDemo(out);
                return 0;
            case "grade-demo":
                runGradeDemo(out);
                return 0;
            case "report-demo":
                runReportDemo(out);
                return 0;
            case "ranking-demo":
                runRankingDemo(out);
                return 0;
            case "search-demo":
                runSearchDemo(out);
                return 0;
            case "validation-demo":
                runValidationDemo(out);
                return 0;
            default:
                err.println("Unknown command: " + command);
                err.println("Run 'java -cp out studentgrademanager.Main help' for available commands.");
                return 1;
        }
    }

    private static void printHelp(PrintStream out) {
        out.println("Student Grade Manager commands:");
        out.println("  help             Show this command list");
        out.println("  demo             Run the standard grade summary demo");
        out.println("  grade-demo       Show subject-based grade recording and validation");
        out.println("  report-demo      Show class, subject, top-student, and failing-student reports");
        out.println("  ranking-demo     Show deterministic ranking by average, name, then student ID");
        out.println("  search-demo      Show case-insensitive partial name search");
        out.println("  validation-demo  Show expected validation failures without a stack trace");
    }

    private static void runDemo(PrintStream out) {
        GradeBook gradeBook = createSampleGradeBook();
        out.println("Student grade summary");
        for (StudentSnapshot student : gradeBook.listStudentsByAverageDescending()) {
            printStudentLine(out, student);
        }
        out.printf(Locale.ROOT, "Class average: %.2f%n", gradeBook.getClassAverage());
    }

    private static void runGradeDemo(PrintStream out) {
        GradeBook gradeBook = new GradeBook();
        gradeBook.addStudent("S100", "Boundary Student");
        gradeBook.recordGrade("S100", "Mathematics", 0.0);
        gradeBook.recordGrade("S100", "Programming", 100.0);

        out.println("Subject-based grade demo");
        printTranscript(out, gradeBook.getTranscript("S100"));
        showExpectedFailure(out, "Negative grade", () -> gradeBook.recordGrade("S100", "Mathematics", -1.0));
        showExpectedFailure(out, "Grade above 100", () -> gradeBook.recordGrade("S100", "Programming", 101.0));
    }

    private static void runReportDemo(PrintStream out) {
        GradeBook gradeBook = createSampleGradeBook();
        GradeBookReport report = gradeBook.generateClassReport();

        out.println("Class report");
        out.printf(Locale.ROOT, "Students: %d%n", report.getStudentCount());
        out.printf(Locale.ROOT, "Recorded grades: %d%n", report.getTotalGradeCount());
        out.printf(Locale.ROOT, "Class average: %.2f%n", report.getClassAverage());

        out.println("Subject summaries");
        for (SubjectGradeSummary summary : report.getSubjectSummaries()) {
            out.printf(Locale.ROOT, "- %s: count %d, average %.2f, highest %.2f, lowest %.2f%n",
                    summary.getSubject(),
                    summary.getGradeCount(),
                    summary.getAverage(),
                    summary.getHighestGrade(),
                    summary.getLowestGrade());
        }

        out.println("Failing students");
        for (StudentSnapshot student : report.getFailingStudents()) {
            printStudentLine(out, student);
        }

        Optional<StudentSnapshot> topStudent = gradeBook.findTopStudent();
        if (topStudent.isPresent()) {
            out.println("Top student");
            printStudentLine(out, topStudent.get());
        }
    }

    private static void runRankingDemo(PrintStream out) {
        GradeBook gradeBook = createSampleGradeBook();
        out.println("Ranking by average, then name, then student ID");
        int rank = 1;
        for (StudentSnapshot student : gradeBook.listStudentsByAverageDescending()) {
            out.printf(Locale.ROOT, "%d. %s (%s) average %.2f%n",
                    rank,
                    student.getName(),
                    student.getStudentId(),
                    student.getAverage());
            rank++;
        }
    }

    private static void runSearchDemo(PrintStream out) {
        GradeBook gradeBook = createSampleGradeBook();
        out.println("Search for 'am'");
        printSearchResults(out, gradeBook.searchStudentsByName("am"));
        out.println("Search for 'LUCA'");
        printSearchResults(out, gradeBook.searchStudentsByName("LUCA"));
        out.println("Search for 'missing'");
        printSearchResults(out, gradeBook.searchStudentsByName("missing"));
    }

    private static void runValidationDemo(PrintStream out) {
        GradeBook gradeBook = new GradeBook();
        gradeBook.addStudent("S001", "Amina");

        out.println("Validation demo");
        showExpectedFailure(out, "Blank student ID", () -> gradeBook.addStudent(" ", "Blank Id"));
        showExpectedFailure(out, "Blank student name", () -> gradeBook.addStudent("S002", " "));
        showExpectedFailure(out, "Duplicate student ID", () -> gradeBook.addStudent("S001", "Duplicate"));
        showExpectedFailure(out, "Unknown student grade", () -> gradeBook.recordGrade("S999", "Math", 80.0));
        showExpectedFailure(out, "Blank subject", () -> gradeBook.recordGrade("S001", " ", 80.0));
        showExpectedFailure(out, "Negative grade", () -> gradeBook.recordGrade("S001", "Math", -0.1));
        showExpectedFailure(out, "Grade above 100", () -> gradeBook.recordGrade("S001", "Math", 100.1));
    }

    private static GradeBook createSampleGradeBook() {
        GradeBook gradeBook = new GradeBook();
        gradeBook.addStudent("S001", "Amina");
        gradeBook.addStudent("S002", "Luca");
        gradeBook.addStudent("S003", "Mei");
        gradeBook.addStudent("S004", "Noah");

        gradeBook.recordGrade("S001", "Mathematics", 88.0);
        gradeBook.recordGrade("S001", "Programming", 94.0);
        gradeBook.recordGrade("S002", "Mathematics", 52.0);
        gradeBook.recordGrade("S002", "Programming", 64.0);
        gradeBook.recordGrade("S003", "Mathematics", 100.0);
        gradeBook.recordGrade("S003", "Programming", 82.0);

        return gradeBook;
    }

    private static void printTranscript(PrintStream out, StudentSnapshot student) {
        out.printf(Locale.ROOT, "Transcript for %s (%s)%n", student.getName(), student.getStudentId());
        for (String subject : student.getGradesBySubject().keySet()) {
            out.printf(Locale.ROOT, "- %s: %s%n", subject, student.getGradesBySubject().get(subject));
        }
        printStudentLine(out, student);
    }

    private static void printStudentLine(PrintStream out, StudentSnapshot student) {
        out.printf(Locale.ROOT,
                "%s (%s): average %.2f, highest %.2f, lowest %.2f, %s, letter %s, grades %d%n",
                student.getName(),
                student.getStudentId(),
                student.getAverage(),
                student.getHighestGrade(),
                student.getLowestGrade(),
                student.isPassing() ? "PASS" : "FAIL",
                student.getLetterGrade(),
                student.getGradeCount());
    }

    private static void printSearchResults(PrintStream out, List<StudentSnapshot> students) {
        if (students.isEmpty()) {
            out.println("No matching students found.");
            return;
        }
        for (StudentSnapshot student : students) {
            printStudentLine(out, student);
        }
    }

    private static void showExpectedFailure(PrintStream out, String label, Runnable action) {
        try {
            action.run();
            out.println(label + ": expected rejection was not triggered");
        } catch (IllegalArgumentException ex) {
            out.println(label + ": rejected (" + ex.getMessage() + ")");
        }
    }
}
