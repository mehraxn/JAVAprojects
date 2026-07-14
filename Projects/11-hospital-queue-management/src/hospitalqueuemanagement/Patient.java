package hospitalqueuemanagement;

import java.time.LocalDateTime;

public final class Patient {
    private final String id;
    private final String name;
    private TriageLevel triageLevel;
    private final LocalDateTime arrivalTime;
    private PatientStatus status;
    private LocalDateTime treatmentStartTime;
    private LocalDateTime dischargeTime;

    public Patient(String id, String name, TriageLevel triageLevel, LocalDateTime arrivalTime) {
        this(id, name, triageLevel, arrivalTime, PatientStatus.WAITING, null, null);
    }

    public Patient(Patient other) {
        this(requirePatient(other).id, other.name, other.triageLevel, other.arrivalTime,
                other.status, other.treatmentStartTime, other.dischargeTime);
    }

    Patient(String id, String name, TriageLevel triageLevel, LocalDateTime arrivalTime,
            PatientStatus status, LocalDateTime treatmentStartTime, LocalDateTime dischargeTime) {
        this.id = requireText(id, "Patient ID");
        this.name = requireText(name, "Patient name");
        this.triageLevel = requireValue(triageLevel, "Triage level");
        this.arrivalTime = requireValue(arrivalTime, "Arrival time");
        this.status = requireValue(status, "Patient status");
        validateTimes(arrivalTime, treatmentStartTime, dischargeTime);
        this.treatmentStartTime = treatmentStartTime;
        this.dischargeTime = dischargeTime;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public TriageLevel getTriageLevel() { return triageLevel; }
    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public PatientStatus getStatus() { return status; }
    public LocalDateTime getTreatmentStartTime() { return treatmentStartTime; }
    public LocalDateTime getDischargeTime() { return dischargeTime; }
    public Patient copy() { return new Patient(this); }

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

    void startTreatment(LocalDateTime time) {
        requireNotBeforeArrival(time, "Treatment start time");
        status = PatientStatus.IN_TREATMENT;
        treatmentStartTime = time;
    }

    void returnToWaiting() { status = PatientStatus.WAITING; }

    void discharge(LocalDateTime time) {
        requireNotBeforeArrival(time, "Discharge time");
        if (treatmentStartTime != null && time.isBefore(treatmentStartTime)) {
            throw new IllegalArgumentException("Discharge time must not be before treatment start time");
        }
        status = PatientStatus.DISCHARGED;
        dischargeTime = time;
    }

    private void requireNotBeforeArrival(LocalDateTime time, String label) {
        requireValue(time, label);
        if (time.isBefore(arrivalTime)) {
            throw new IllegalArgumentException(label + " must not be before arrival time");
        }
    }

    private static void validateTimes(LocalDateTime arrival, LocalDateTime treatment,
            LocalDateTime discharge) {
        if (treatment != null && treatment.isBefore(arrival)) {
            throw new IllegalArgumentException("Treatment start time must not be before arrival time");
        }
        if (discharge != null && discharge.isBefore(arrival)) {
            throw new IllegalArgumentException("Discharge time must not be before arrival time");
        }
        if (treatment != null && discharge != null && discharge.isBefore(treatment)) {
            throw new IllegalArgumentException("Discharge time must not be before treatment start time");
        }
    }

    private static Patient requirePatient(Patient patient) {
        return requireValue(patient, "Patient to copy");
    }

    private static <T> T requireValue(T value, String label) {
        if (value == null) {
            throw new IllegalArgumentException(label + " must not be null");
        }
        return value;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
