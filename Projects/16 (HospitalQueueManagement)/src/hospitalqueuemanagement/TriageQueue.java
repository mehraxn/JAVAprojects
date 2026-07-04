package hospitalqueuemanagement;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class TriageQueue {
    private final PriorityQueue<Patient> patients = new PriorityQueue<>(
            Comparator.comparing(Patient::getTriageLevel)
                    .thenComparing(Patient::getArrivalTime));

    public void addPatient(Patient patient) {
        // TODO: Validate and enqueue the patient.
        throw new UnsupportedOperationException("TODO: add a patient");
    }

    public Patient serveNextPatient() {
        // TODO: Remove and return the highest-priority waiting patient.
        throw new UnsupportedOperationException("TODO: serve the next patient");
    }

    public void markAsEmergency(String patientId) {
        // TODO: Requeue the patient with emergency priority.
        throw new UnsupportedOperationException("TODO: apply emergency override");
    }

    public List<Patient> viewQueue() {
        // TODO: Return patients in service order without changing the queue.
        throw new UnsupportedOperationException("TODO: view the queue");
    }

    public double calculateAverageWaitMinutes() {
        // TODO: Calculate a simple wait-time statistic.
        throw new UnsupportedOperationException("TODO: calculate wait statistics");
    }
}
