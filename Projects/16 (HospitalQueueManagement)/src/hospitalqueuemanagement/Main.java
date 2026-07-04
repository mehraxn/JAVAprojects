package hospitalqueuemanagement;

import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        TriageQueue triageQueue = new TriageQueue();
        LocalDateTime baseTime = LocalDateTime.of(2026, 7, 4, 9, 0);

        triageQueue.addPatient(new Patient(
                "P001", "Mila", TriageLevel.STANDARD, baseTime));
        triageQueue.addPatient(new Patient(
                "P002", "Leon", TriageLevel.URGENT, baseTime.plusMinutes(5)));
        triageQueue.addPatient(new Patient(
                "P003", "Sara", TriageLevel.NON_URGENT, baseTime.plusMinutes(10)));

        triageQueue.markAsEmergency("P003");
        System.out.println("Waiting order:");
        for (Patient patient : triageQueue.viewQueue()) {
            System.out.println("- " + patient.getName() + " (" + patient.getTriageLevel() + ")");
        }

        Patient nextPatient = triageQueue.serveNextPatient();
        System.out.println("Serving: " + nextPatient.getName()
                + ", status " + nextPatient.getStatus());
        triageQueue.updatePatientStatus(nextPatient.getId(), PatientStatus.DISCHARGED);
        System.out.println("Recorded status: "
                + triageQueue.getPatient(nextPatient.getId()).getStatus());
    }
}
