package hospitalqueuemanagement;

import java.time.LocalDateTime;

public class Patient {
    private final String id;
    private final String name;
    private TriageLevel triageLevel;
    private final LocalDateTime arrivalTime;
    private PatientStatus status = PatientStatus.WAITING;

    public Patient(String id, String name, TriageLevel triageLevel, LocalDateTime arrivalTime) {
        this.id = requireText(id, "Patient ID");
        this.name = requireText(name, "Patient name");
        if (triageLevel == null || arrivalTime == null) {
            throw new IllegalArgumentException("Triage level and arrival time must not be null");
        }
        this.triageLevel = triageLevel;
        this.arrivalTime = arrivalTime;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public TriageLevel getTriageLevel() { return triageLevel; }
    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public PatientStatus getStatus() { return status; }

    void setTriageLevel(TriageLevel triageLevel) {
        if (triageLevel == null) {
            throw new IllegalArgumentException("Triage level must not be null");
        }
        this.triageLevel = triageLevel;
    }

    void setStatus(PatientStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Patient status must not be null");
        }
        this.status = status;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
