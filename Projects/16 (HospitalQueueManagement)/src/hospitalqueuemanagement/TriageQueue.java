package hospitalqueuemanagement;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class TriageQueue {
    private static final Comparator<Patient> WAITING_ORDER =
            Comparator.comparingInt((Patient patient) -> patient.getTriageLevel().getPriority())
                    .thenComparing(Patient::getArrivalTime)
                    .thenComparing(Patient::getId);

    private final PriorityQueue<Patient> waitingPatients = new PriorityQueue<>(WAITING_ORDER);
    private final Map<String, Patient> patientRecords = new LinkedHashMap<>();

    public void addPatient(Patient patient) {
        if (patient == null) {
            throw new IllegalArgumentException("Patient must not be null");
        }
        if (patientRecords.containsKey(patient.getId())) {
            throw new IllegalArgumentException("Patient ID already exists: " + patient.getId());
        }
        if (patient.getStatus() != PatientStatus.WAITING) {
            throw new IllegalArgumentException("New patient must have WAITING status");
        }
        patientRecords.put(patient.getId(), patient);
        waitingPatients.add(patient);
    }

    public Patient getPatient(String patientId) {
        String validId = requireText(patientId, "Patient ID");
        Patient patient = patientRecords.get(validId);
        if (patient == null) {
            throw new IllegalArgumentException("Unknown patient ID: " + validId);
        }
        return patient;
    }

    public Patient serveNextPatient() {
        Patient patient = waitingPatients.poll();
        if (patient == null) {
            throw new IllegalStateException("No patients are waiting");
        }
        patient.setStatus(PatientStatus.IN_TREATMENT);
        return patient;
    }

    public void updatePriority(String patientId, TriageLevel newLevel) {
        Patient patient = getPatient(patientId);
        if (newLevel == null) {
            throw new IllegalArgumentException("Triage level must not be null");
        }
        if (patient.getStatus() != PatientStatus.WAITING) {
            throw new IllegalStateException("Only waiting patients can change priority");
        }
        waitingPatients.remove(patient);
        patient.setTriageLevel(newLevel);
        waitingPatients.add(patient);
    }

    public void markAsEmergency(String patientId) {
        updatePriority(patientId, TriageLevel.EMERGENCY);
    }

    public void updatePatientStatus(String patientId, PatientStatus newStatus) {
        Patient patient = getPatient(patientId);
        if (newStatus == null) {
            throw new IllegalArgumentException("Patient status must not be null");
        }
        if (patient.getStatus() == newStatus) {
            return;
        }
        if (!isValidStatusTransition(patient.getStatus(), newStatus)) {
            throw new IllegalStateException("Invalid patient status transition: "
                    + patient.getStatus() + " -> " + newStatus);
        }

        waitingPatients.remove(patient);
        patient.setStatus(newStatus);
        if (newStatus == PatientStatus.WAITING) {
            waitingPatients.add(patient);
        }
    }

    public List<Patient> viewQueue() {
        PriorityQueue<Patient> snapshot = new PriorityQueue<>(WAITING_ORDER);
        snapshot.addAll(waitingPatients);
        List<Patient> orderedPatients = new ArrayList<>();
        while (!snapshot.isEmpty()) {
            orderedPatients.add(snapshot.poll());
        }
        return Collections.unmodifiableList(orderedPatients);
    }

    public List<Patient> listPatientRecords() {
        return Collections.unmodifiableList(new ArrayList<>(patientRecords.values()));
    }

    public double calculateAverageWaitMinutes() {
        return calculateAverageWaitMinutes(LocalDateTime.now());
    }

    public double calculateAverageWaitMinutes(LocalDateTime currentTime) {
        if (currentTime == null) {
            throw new IllegalArgumentException("Current time must not be null");
        }
        if (waitingPatients.isEmpty()) {
            return 0.0;
        }
        long totalMinutes = 0;
        for (Patient patient : waitingPatients) {
            long waitMinutes = Duration.between(patient.getArrivalTime(), currentTime).toMinutes();
            if (waitMinutes < 0) {
                throw new IllegalArgumentException("Current time is before a patient's arrival");
            }
            totalMinutes += waitMinutes;
        }
        return (double) totalMinutes / waitingPatients.size();
    }

    private static boolean isValidStatusTransition(PatientStatus current, PatientStatus next) {
        switch (current) {
            case WAITING:
                return next == PatientStatus.IN_TREATMENT || next == PatientStatus.DISCHARGED;
            case IN_TREATMENT:
                return next == PatientStatus.WAITING || next == PatientStatus.DISCHARGED;
            case DISCHARGED:
                return false;
            default:
                return false;
        }
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
