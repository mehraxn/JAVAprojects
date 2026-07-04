package studentgrademanager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GradeBook {
    private final Map<String, Student> students = new HashMap<>();

    public void addStudent(Student student) {
        // TODO: Validate the student and reject duplicate identifiers.
        throw new UnsupportedOperationException("TODO: add a student");
    }

    public boolean removeStudent(String studentId) {
        // TODO: Remove the matching student when present.
        throw new UnsupportedOperationException("TODO: remove a student");
    }

    public void recordGrade(String studentId, String subject, double grade) {
        // TODO: Find the student and delegate grade validation and storage.
        throw new UnsupportedOperationException("TODO: record a grade");
    }

    public List<Student> listStudentsByAverage() {
        // TODO: Return students ordered from highest to lowest average.
        throw new UnsupportedOperationException("TODO: sort students");
    }
}
