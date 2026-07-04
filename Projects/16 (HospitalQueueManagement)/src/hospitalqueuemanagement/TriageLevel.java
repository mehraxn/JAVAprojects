package hospitalqueuemanagement;

public enum TriageLevel {
    EMERGENCY(1),
    URGENT(2),
    STANDARD(3),
    NON_URGENT(4);

    private final int priority;

    TriageLevel(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
