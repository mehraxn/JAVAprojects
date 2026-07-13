package studentgrademanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public final class GradeBook {
    private final Map<String, Student> students = new LinkedHashMap<>();

    public StudentSnapshot addStudent(String studentId, String name) {
        return addStudent(new Student(studentId, name));
    }

    public StudentSnapshot addStudent(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student must not be null");
        }

        Student internalStudent = new Student(student);
        String studentId = internalStudent.getStudentId();
        if (students.containsKey(studentId)) {
            throw new IllegalArgumentException("Student ID already exists: " + studentId);
        }

        students.put(studentId, internalStudent);
        return internalStudent.toSnapshot();
    }

    public boolean removeStudent(String studentId) {
        return students.remove(requireId(studentId)) != null;
    }

    public Optional<StudentSnapshot> getStudent(String studentId) {
        String validId = requireId(studentId);
        Student student = students.get(validId);
        return student == null ? Optional.empty() : Optional.of(student.toSnapshot());
    }

    public StudentSnapshot recordGrade(String studentId, String subject, double grade) {
        Student student = requireStudent(studentId);
        student.addGrade(subject, grade);
        return student.toSnapshot();
    }

    public List<StudentSnapshot> listStudents() {
        List<StudentSnapshot> snapshots = new ArrayList<>();
        for (Student student : students.values()) {
            snapshots.add(student.toSnapshot());
        }
        return Collections.unmodifiableList(snapshots);
    }

    public List<StudentSnapshot> listStudentsByAverage() {
        return listStudentsByAverageDescending();
    }

    public List<StudentSnapshot> listStudentsByAverageDescending() {
        List<StudentSnapshot> sortedStudents = new ArrayList<>(listStudents());
        sortedStudents.sort(Comparator.comparingDouble(StudentSnapshot::getAverage)
                .reversed()
                .thenComparing(StudentSnapshot::getName, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(StudentSnapshot::getName)
                .thenComparing(StudentSnapshot::getStudentId));
        return Collections.unmodifiableList(sortedStudents);
    }

    public List<StudentSnapshot> searchStudentsByName(String query) {
        if (query == null) {
            throw new IllegalArgumentException("Search query must not be null");
        }

        String normalizedQuery = query.trim().toLowerCase();
        if (normalizedQuery.isEmpty()) {
            return Collections.emptyList();
        }

        List<StudentSnapshot> matches = new ArrayList<>();
        for (Student student : students.values()) {
            if (student.getName().toLowerCase().contains(normalizedQuery)) {
                matches.add(student.toSnapshot());
            }
        }
        matches.sort(Comparator.comparing(StudentSnapshot::getName, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(StudentSnapshot::getName)
                .thenComparing(StudentSnapshot::getStudentId));
        return Collections.unmodifiableList(matches);
    }

    public StudentSnapshot getTranscript(String studentId) {
        return requireStudent(studentId).toSnapshot();
    }

    public Optional<StudentSnapshot> findTopStudent() {
        List<StudentSnapshot> rankedStudents = listStudentsByAverageDescending();
        return rankedStudents.isEmpty() ? Optional.empty() : Optional.of(rankedStudents.get(0));
    }

    public List<StudentSnapshot> findFailingStudents() {
        List<StudentSnapshot> failingStudents = new ArrayList<>();
        for (Student student : students.values()) {
            StudentSnapshot snapshot = student.toSnapshot();
            if (!snapshot.isPassing()) {
                failingStudents.add(snapshot);
            }
        }
        failingStudents.sort(Comparator.comparing(StudentSnapshot::getName, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(StudentSnapshot::getName)
                .thenComparing(StudentSnapshot::getStudentId));
        return Collections.unmodifiableList(failingStudents);
    }

    public List<SubjectGradeSummary> getSubjectSummaries() {
        Map<String, SubjectStats> statsBySubject = new TreeMap<>();
        for (Student student : students.values()) {
            for (Map.Entry<String, List<Double>> entry : student.getAllGrades().entrySet()) {
                SubjectStats stats = statsBySubject.computeIfAbsent(entry.getKey(), SubjectStats::new);
                for (double grade : entry.getValue()) {
                    stats.addGrade(grade);
                }
            }
        }

        List<SubjectGradeSummary> summaries = new ArrayList<>();
        for (SubjectStats stats : statsBySubject.values()) {
            summaries.add(stats.toSummary());
        }
        return Collections.unmodifiableList(summaries);
    }

    public GradeBookReport generateClassReport() {
        return new GradeBookReport(
                students.size(),
                getTotalGradeCount(),
                getClassAverage(),
                listStudentsByAverageDescending(),
                findFailingStudents(),
                getSubjectSummaries());
    }

    public double getClassAverage() {
        double total = 0.0;
        int gradeCount = 0;
        for (Student student : students.values()) {
            for (List<Double> grades : student.getAllGrades().values()) {
                for (double grade : grades) {
                    total += grade;
                    gradeCount++;
                }
            }
        }
        return gradeCount == 0 ? 0.0 : total / gradeCount;
    }

    public int getTotalGradeCount() {
        int totalGradeCount = 0;
        for (Student student : students.values()) {
            totalGradeCount += student.getGradeCount();
        }
        return totalGradeCount;
    }

    private Student requireStudent(String studentId) {
        String validId = requireId(studentId);
        Student student = students.get(validId);
        if (student == null) {
            throw new IllegalArgumentException("Unknown student ID: " + validId);
        }
        return student;
    }

    private static String requireId(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID must not be blank");
        }
        return studentId.trim();
    }

    private static final class SubjectStats {
        private final String subject;
        private int gradeCount;
        private double total;
        private double highestGrade = Student.MINIMUM_GRADE;
        private double lowestGrade = Student.MAXIMUM_GRADE;

        private SubjectStats(String subject) {
            this.subject = subject;
        }

        private void addGrade(double grade) {
            gradeCount++;
            total += grade;
            highestGrade = Math.max(highestGrade, grade);
            lowestGrade = Math.min(lowestGrade, grade);
        }

        private SubjectGradeSummary toSummary() {
            double average = gradeCount == 0 ? 0.0 : total / gradeCount;
            double highest = gradeCount == 0 ? 0.0 : highestGrade;
            double lowest = gradeCount == 0 ? 0.0 : lowestGrade;
            return new SubjectGradeSummary(subject, gradeCount, average, highest, lowest);
        }
    }
}
