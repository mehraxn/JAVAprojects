package studentgrademanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GradeBook {
    private final Map<String, Student> students = new LinkedHashMap<>();

    public void addStudent(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student must not be null");
        }
        if (students.containsKey(student.getId())) {
            throw new IllegalArgumentException("Student ID already exists: " + student.getId());
        }
        students.put(student.getId(), student);
    }

    public boolean removeStudent(String studentId) {
        return students.remove(requireId(studentId)) != null;
    }

    public Student getStudent(String studentId) {
        String validId = requireId(studentId);
        Student student = students.get(validId);
        if (student == null) {
            throw new IllegalArgumentException("Unknown student ID: " + validId);
        }
        return student;
    }

    public void recordGrade(String studentId, String subject, double grade) {
        getStudent(studentId).addGrade(subject, grade);
    }

    public List<Student> listStudents() {
        return Collections.unmodifiableList(new ArrayList<>(students.values()));
    }

    public List<Student> listStudentsByAverage() {
        List<Student> sortedStudents = new ArrayList<>(students.values());
        sortedStudents.sort(Comparator.comparingDouble(Student::calculateAverage)
                .reversed()
                .thenComparing(Student::getName, String.CASE_INSENSITIVE_ORDER));
        return Collections.unmodifiableList(sortedStudents);
    }

    private static String requireId(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID must not be blank");
        }
        return studentId.trim();
    }
}
