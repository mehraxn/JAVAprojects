package hospitalqueuemanagement;

import java.time.LocalDateTime;

public class Patient {
    private final String id;
    private final String name;
    private final TriageLevel triageLevel;
    private final LocalDateTime arrivalTime;

    public Patient(String id, String name, TriageLevel triageLevel, LocalDateTime arrivalTime) {
        this.id = id;
        this.name = name;
        this.triageLevel = triageLevel;
        this.arrivalTime = arrivalTime;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public TriageLevel getTriageLevel() { return triageLevel; }
    public LocalDateTime getArrivalTime() { return arrivalTime; }
}
