package studentgrademanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class StudentSnapshot {
    private final String studentId;
    private final String name;
    private final Map<String, List<Double>> gradesBySubject;
    private final double average;
    private final double highestGrade;
    private final double lowestGrade;
    private final boolean passing;
    private final String letterGrade;
    private final int gradeCount;

    public StudentSnapshot(
            String studentId,
            String name,
            Map<String, List<Double>> gradesBySubject,
            double average,
            double highestGrade,
            double lowestGrade,
            boolean passing,
            String letterGrade,
            int gradeCount) {
        this.studentId = requireText(studentId, "Student ID");
        this.name = requireText(name, "Student name");
        this.gradesBySubject = copyGrades(gradesBySubject);
        this.average = average;
        this.highestGrade = highestGrade;
        this.lowestGrade = lowestGrade;
        this.passing = passing;
        this.letterGrade = requireText(letterGrade, "Letter grade");
        this.gradeCount = gradeCount;
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

    public Map<String, List<Double>> getGradesBySubject() {
        return gradesBySubject;
    }

    public Map<String, List<Double>> getAllGrades() {
        return gradesBySubject;
    }

    public double getAverage() {
        return average;
    }

    public double getHighestGrade() {
        return highestGrade;
    }

    public double getLowestGrade() {
        return lowestGrade;
    }

    public boolean isPassing() {
        return passing;
    }

    public String getLetterGrade() {
        return letterGrade;
    }

    public int getGradeCount() {
        return gradeCount;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }

    private static Map<String, List<Double>> copyGrades(Map<String, List<Double>> source) {
        if (source == null) {
            throw new IllegalArgumentException("Grades map must not be null");
        }

        Map<String, List<Double>> copy = new LinkedHashMap<>();
        for (Map.Entry<String, List<Double>> entry : source.entrySet()) {
            copy.put(requireText(entry.getKey(), "Subject"),
                    Collections.unmodifiableList(new ArrayList<>(entry.getValue())));
        }
        return Collections.unmodifiableMap(copy);
    }
}
