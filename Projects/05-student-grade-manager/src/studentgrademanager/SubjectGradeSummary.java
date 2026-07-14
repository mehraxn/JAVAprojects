package studentgrademanager;

public final class SubjectGradeSummary {
    private final String subject;
    private final int gradeCount;
    private final double average;
    private final double highestGrade;
    private final double lowestGrade;

    public SubjectGradeSummary(
            String subject,
            int gradeCount,
            double average,
            double highestGrade,
            double lowestGrade) {
        this.subject = requireText(subject, "Subject");
        this.gradeCount = gradeCount;
        this.average = average;
        this.highestGrade = highestGrade;
        this.lowestGrade = lowestGrade;
    }

    public String getSubject() {
        return subject;
    }

    public int getGradeCount() {
        return gradeCount;
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

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
