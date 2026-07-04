package studentgrademanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Student {
    private final String id;
    private String name;
    private final Map<String, List<Double>> gradesBySubject = new HashMap<>();

    public Student(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addGrade(String subject, double grade) {
        // TODO: Validate the subject and grade, then store the grade.
        throw new UnsupportedOperationException("TODO: add a grade");
    }

    public double calculateAverage() {
        // TODO: Calculate the average across all recorded grades.
        throw new UnsupportedOperationException("TODO: calculate the average");
    }
}
