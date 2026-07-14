package studentgrademanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Student {
    public static final double MINIMUM_GRADE = 0.0;
    public static final double MAXIMUM_GRADE = 100.0;
    public static final double PASSING_GRADE = 60.0;

    private final String studentId;
    private String name;
    private final Map<String, List<Double>> gradesBySubject = new LinkedHashMap<>();

    public Student(String studentId, String name) {
        this.studentId = requireText(studentId, "Student ID");
        this.name = requireText(name, "Student name");
    }

    Student(Student source) {
        this(source.getStudentId(), source.getName());
        for (Map.Entry<String, List<Double>> entry : source.getAllGrades().entrySet()) {
            for (double grade : entry.getValue()) {
                addGrade(entry.getKey(), grade);
            }
        }
    }

    public String getStudentId() {
        return studentId;
    }

    public String getId() {
        return studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = requireText(name, "Student name");
    }

    public void addGrade(String subject, double grade) {
        String validSubject = requireText(subject, "Subject");
        if (!Double.isFinite(grade) || grade < MINIMUM_GRADE || grade > MAXIMUM_GRADE) {
            throw new IllegalArgumentException("Grade must be finite and between 0 and 100");
        }
        gradesBySubject.computeIfAbsent(validSubject, key -> new ArrayList<>()).add(grade);
    }

    public double getAverage() {
        return calculateAverage();
    }

    public double calculateAverage() {
        double total = 0.0;
        int gradeCount = 0;
        for (List<Double> subjectGrades : gradesBySubject.values()) {
            for (double grade : subjectGrades) {
                total += grade;
                gradeCount++;
            }
        }
        return gradeCount == 0 ? 0.0 : total / gradeCount;
    }

    public boolean isPassing() {
        return hasGrades() && calculateAverage() >= PASSING_GRADE;
    }

    public String getLetterGrade() {
        if (!hasGrades()) {
            return "N/A";
        }

        double average = calculateAverage();
        if (average >= 90.0) {
            return "A";
        }
        if (average >= 80.0) {
            return "B";
        }
        if (average >= 70.0) {
            return "C";
        }
        if (average >= 60.0) {
            return "D";
        }
        return "F";
    }

    public double getHighestGrade() {
        if (!hasGrades()) {
            return 0.0;
        }

        double highest = MINIMUM_GRADE;
        for (List<Double> subjectGrades : gradesBySubject.values()) {
            for (double grade : subjectGrades) {
                highest = Math.max(highest, grade);
            }
        }
        return highest;
    }

    public double getLowestGrade() {
        if (!hasGrades()) {
            return 0.0;
        }

        double lowest = MAXIMUM_GRADE;
        for (List<Double> subjectGrades : gradesBySubject.values()) {
            for (double grade : subjectGrades) {
                lowest = Math.min(lowest, grade);
            }
        }
        return lowest;
    }

    public boolean hasGrades() {
        for (List<Double> subjectGrades : gradesBySubject.values()) {
            if (!subjectGrades.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public int getGradeCount() {
        int gradeCount = 0;
        for (List<Double> subjectGrades : gradesBySubject.values()) {
            gradeCount += subjectGrades.size();
        }
        return gradeCount;
    }

    public Map<String, List<Double>> getAllGrades() {
        return copyGrades(gradesBySubject);
    }

    public Map<String, List<Double>> getGradesBySubject() {
        return getAllGrades();
    }

    public StudentSnapshot toSnapshot() {
        return new StudentSnapshot(
                studentId,
                name,
                getAllGrades(),
                calculateAverage(),
                getHighestGrade(),
                getLowestGrade(),
                isPassing(),
                getLetterGrade(),
                getGradeCount());
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }

    private static Map<String, List<Double>> copyGrades(Map<String, List<Double>> source) {
        Map<String, List<Double>> copy = new LinkedHashMap<>();
        for (Map.Entry<String, List<Double>> entry : source.entrySet()) {
            copy.put(entry.getKey(), Collections.unmodifiableList(new ArrayList<>(entry.getValue())));
        }
        return Collections.unmodifiableMap(copy);
    }
}
