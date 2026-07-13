package studentgrademanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class GradeBookReport {
    private final int studentCount;
    private final int totalGradeCount;
    private final double classAverage;
    private final List<StudentSnapshot> rankedStudents;
    private final List<StudentSnapshot> failingStudents;
    private final List<SubjectGradeSummary> subjectSummaries;

    public GradeBookReport(
            int studentCount,
            int totalGradeCount,
            double classAverage,
            List<StudentSnapshot> rankedStudents,
            List<StudentSnapshot> failingStudents,
            List<SubjectGradeSummary> subjectSummaries) {
        this.studentCount = studentCount;
        this.totalGradeCount = totalGradeCount;
        this.classAverage = classAverage;
        this.rankedStudents = Collections.unmodifiableList(new ArrayList<>(rankedStudents));
        this.failingStudents = Collections.unmodifiableList(new ArrayList<>(failingStudents));
        this.subjectSummaries = Collections.unmodifiableList(new ArrayList<>(subjectSummaries));
    }

    public int getStudentCount() {
        return studentCount;
    }

    public int getTotalGradeCount() {
        return totalGradeCount;
    }

    public double getClassAverage() {
        return classAverage;
    }

    public List<StudentSnapshot> getRankedStudents() {
        return rankedStudents;
    }

    public List<StudentSnapshot> getFailingStudents() {
        return failingStudents;
    }

    public List<SubjectGradeSummary> getSubjectSummaries() {
        return subjectSummaries;
    }
}
