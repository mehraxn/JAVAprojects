package studentgrademanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Student {
    public static final double MINIMUM_GRADE = 0.0;
    public static final double MAXIMUM_GRADE = 100.0;
    public static final double PASSING_GRADE = 60.0;

    private final String id;
    private String name;
    private final Map<String, List<Double>> gradesBySubject = new LinkedHashMap<>();

    public Student(String id, String name) {
        this.id = requireText(id, "Student ID");
        this.name = requireText(name, "Student name");
    }

    public String getId() {
        return id;
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
            throw new IllegalArgumentException("Grade must be between 0 and 100");
        }
        gradesBySubject.computeIfAbsent(validSubject, key -> new ArrayList<>()).add(grade);
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

    public double getHighestGrade() {
        ensureGradesExist();
        double highest = MINIMUM_GRADE;
        for (List<Double> subjectGrades : gradesBySubject.values()) {
            for (double grade : subjectGrades) {
                highest = Math.max(highest, grade);
            }
        }
        return highest;
    }

    public double getLowestGrade() {
        ensureGradesExist();
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

    public Map<String, List<Double>> getGradesBySubject() {
        Map<String, List<Double>> copy = new LinkedHashMap<>();
        for (Map.Entry<String, List<Double>> entry : gradesBySubject.entrySet()) {
            copy.put(entry.getKey(), Collections.unmodifiableList(new ArrayList<>(entry.getValue())));
        }
        return Collections.unmodifiableMap(copy);
    }

    private void ensureGradesExist() {
        if (!hasGrades()) {
            throw new IllegalStateException("Student has no grades");
        }
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
