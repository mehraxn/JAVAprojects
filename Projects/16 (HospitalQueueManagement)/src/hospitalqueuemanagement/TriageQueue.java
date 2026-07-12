package hospitalqueuemanagement;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.PriorityQueue;

public final class TriageQueue {
    private static final Comparator<Patient> WAITING_ORDER =
            Comparator.comparingInt((Patient p) -> p.getTriageLevel().getPriority())
                    .thenComparing(Patient::getArrivalTime).thenComparing(Patient::getId);
    private final PriorityQueue<Patient> waitingPatients = new PriorityQueue<>(WAITING_ORDER);
    private final Map<String, Patient> patientRecords = new LinkedHashMap<>();

    public void addPatient(Patient supplied) {
        if (supplied == null) { throw new IllegalArgumentException("Patient must not be null"); }
        Patient patient = supplied.copy();
        if (patientRecords.containsKey(patient.getId())) {
            throw new IllegalArgumentException("Patient ID already exists: " + patient.getId());
        }
        if (patient.getStatus() != PatientStatus.WAITING) {
            throw new IllegalArgumentException("New patient must have WAITING status");
        }
        patientRecords.put(patient.getId(), patient);
        waitingPatients.add(patient);
    }

    public Patient getPatient(String patientId) { return internalPatient(patientId).copy(); }

    public Optional<Patient> findPatient(String patientId) {
        Patient patient = patientRecords.get(requireText(patientId, "Patient ID"));
        return patient == null ? Optional.empty() : Optional.of(patient.copy());
    }

    public Patient serveNextPatient() { return serveNextPatient(LocalDateTime.now()); }

    public Patient serveNextPatient(LocalDateTime time) {
        if (time == null) { throw new IllegalArgumentException("Treatment start time must not be null"); }
        Patient patient = waitingPatients.peek();
        if (patient == null) { throw new IllegalStateException("No patients are waiting"); }
        patient.startTreatment(time);
        waitingPatients.remove();
        return patient.copy();
    }

    public void updatePriority(String patientId, TriageLevel level) {
        if (level == null) { throw new IllegalArgumentException("Triage level must not be null"); }
        Patient patient = internalPatient(patientId);
        if (patient.getStatus() != PatientStatus.WAITING) {
            throw new IllegalStateException("Only waiting patients can change priority");
        }
        waitingPatients.remove(patient);
        patient.setTriageLevel(level);
        waitingPatients.add(patient);
    }

    public void markAsEmergency(String patientId) { updatePriority(patientId, TriageLevel.EMERGENCY); }

    public void dischargePatient(String patientId) { dischargePatient(patientId, LocalDateTime.now()); }

    public void dischargePatient(String patientId, LocalDateTime time) {
        Patient patient = internalPatient(patientId);
        if (patient.getStatus() == PatientStatus.DISCHARGED) {
            throw new IllegalStateException("A discharged patient is terminal");
        }
        patient.discharge(time);
        waitingPatients.remove(patient);
    }

    public void requeuePatient(String patientId) {
        Patient patient = internalPatient(patientId);
        if (patient.getStatus() != PatientStatus.IN_TREATMENT) {
            throw new IllegalStateException("Only an in-treatment patient can be requeued");
        }
        patient.returnToWaiting();
        waitingPatients.add(patient);
    }

    public void updatePatientStatus(String patientId, PatientStatus status) {
        if (status == null) { throw new IllegalArgumentException("Patient status must not be null"); }
        Patient patient = internalPatient(patientId);
        if (patient.getStatus() == status) { return; }
        if (status == PatientStatus.WAITING) { requeuePatient(patientId); }
        else if (status == PatientStatus.DISCHARGED) { dischargePatient(patientId); }
        else { throw new IllegalStateException("IN_TREATMENT is entered only by serving a patient"); }
    }

    public List<Patient> viewQueue() {
        PriorityQueue<Patient> snapshot = new PriorityQueue<>(WAITING_ORDER);
        snapshot.addAll(waitingPatients);
        List<Patient> result = new ArrayList<>();
        while (!snapshot.isEmpty()) { result.add(snapshot.remove().copy()); }
        return Collections.unmodifiableList(result);
    }

    public List<Patient> listPatientRecords() {
        List<Patient> result = new ArrayList<>();
        for (Patient patient : patientRecords.values()) { result.add(patient.copy()); }
        return Collections.unmodifiableList(result);
    }

    public int waitingCount() { return waitingPatients.size(); }
    public int totalRecordCount() { return patientRecords.size(); }

    public Map<TriageLevel, Long> countWaitingByTriageLevel() {
        Map<TriageLevel, Long> result = new EnumMap<>(TriageLevel.class);
        for (TriageLevel level : TriageLevel.values()) { result.put(level, 0L); }
        for (Patient patient : waitingPatients) {
            TriageLevel level = patient.getTriageLevel();
            result.put(level, result.get(level) + 1L);
        }
        return Collections.unmodifiableMap(result);
    }

    public Optional<Patient> longestWaitingPatient(LocalDateTime now) {
        validateNow(now);
        return waitingPatients.stream().min(Comparator.comparing(Patient::getArrivalTime)
                .thenComparing(Patient::getId)).map(Patient::copy);
    }

    public double calculateAverageCurrentWaitMinutes(LocalDateTime now) {
        validateNow(now);
        if (waitingPatients.isEmpty()) { return 0.0; }
        long total = 0;
        for (Patient patient : waitingPatients) {
            total += Duration.between(patient.getArrivalTime(), now).toMinutes();
        }
        return (double) total / waitingPatients.size();
    }

    public OptionalDouble calculateAverageServedWaitMinutes() {
        long total = 0;
        int count = 0;
        for (Patient patient : patientRecords.values()) {
            if (patient.getTreatmentStartTime() != null) {
                total += Duration.between(patient.getArrivalTime(), patient.getTreatmentStartTime()).toMinutes();
                count++;
            }
        }
        return count == 0 ? OptionalDouble.empty() : OptionalDouble.of((double) total / count);
    }

    private void validateNow(LocalDateTime now) {
        if (now == null) { throw new IllegalArgumentException("Current time must not be null"); }
        for (Patient patient : waitingPatients) {
            if (now.isBefore(patient.getArrivalTime())) {
                throw new IllegalArgumentException("Current time is before a waiting patient's arrival");
            }
        }
    }

    private Patient internalPatient(String patientId) {
        String id = requireText(patientId, "Patient ID");
        Patient patient = patientRecords.get(id);
        if (patient == null) { throw new IllegalArgumentException("Unknown patient ID: " + id); }
        return patient;
    }

    private static String requireText(String value, String label) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(label + " must not be blank");
        }
        return value.trim();
    }
}
