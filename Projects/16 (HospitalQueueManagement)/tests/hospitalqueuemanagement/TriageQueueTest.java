package hospitalqueuemanagement;

import java.lang.reflect.Modifier;
import java.time.LocalDateTime;

final class TriageQueueTest {
    private TriageQueueTest() { }
    static void run() {
        LocalDateTime t = LocalDateTime.of(2026, 1, 1, 8, 0);
        TriageQueue queue = new TriageQueue();
        Patient external = new Patient("S", "Standard", TriageLevel.STANDARD, t.minusMinutes(5));
        queue.addPatient(external);
        queue.addPatient(new Patient("U2", "Urgent later ID", TriageLevel.URGENT, t));
        queue.addPatient(new Patient("U1", "Urgent first ID", TriageLevel.URGENT, t));
        queue.addPatient(new Patient("E", "Emergency", TriageLevel.EMERGENCY, t.plusMinutes(5)));
        TestSupport.assertEquals(4, queue.waitingCount(), "admit works");
        TestSupport.assertEquals("E", queue.viewQueue().get(0).getId(), "emergency first");
        TestSupport.assertEquals("U1", queue.viewQueue().get(1).getId(), "ID tie break");
        TestSupport.assertEquals("S", queue.viewQueue().get(3).getId(), "urgent before standard");
        TriageQueue arrivalQueue = new TriageQueue();
        arrivalQueue.addPatient(new Patient("LATE", "Later", TriageLevel.STANDARD, t.plusMinutes(1)));
        arrivalQueue.addPatient(new Patient("EARLY", "Earlier", TriageLevel.STANDARD, t));
        TestSupport.assertEquals("EARLY", arrivalQueue.viewQueue().get(0).getId(), "earlier arrival tie break");
        TestSupport.assertThrows(IllegalArgumentException.class, () -> queue.addPatient(null), "null admission rejected");
        TestSupport.assertThrows(IllegalArgumentException.class, () -> queue.addPatient(external), "duplicate rejected");
        TestSupport.assertThrows(UnsupportedOperationException.class, () -> queue.viewQueue().clear(), "queue view unmodifiable");
        TestSupport.assertThrows(UnsupportedOperationException.class, () -> queue.listPatientRecords().clear(), "records unmodifiable");
        Patient returned = queue.getPatient("S"); returned.setTriageLevel(TriageLevel.EMERGENCY);
        TestSupport.assertEquals(TriageLevel.STANDARD, queue.getPatient("S").getTriageLevel(), "lookup defensive");
        queue.updatePriority("S", TriageLevel.EMERGENCY);
        TestSupport.assertEquals("S", queue.viewQueue().get(0).getId(), "priority reorder");
        TestSupport.assertEquals(4, queue.waitingCount(), "priority update no duplicate");
        TestSupport.assertThrows(IllegalArgumentException.class, () -> queue.updatePriority("missing", TriageLevel.URGENT), "missing update rejected");
        TestSupport.assertThrows(IllegalArgumentException.class, () -> queue.updatePriority("S", null), "null update rejected");
        Patient served = queue.serveNextPatient(t.plusMinutes(10));
        TestSupport.assertEquals("S", served.getId(), "serve follows view order");
        TestSupport.assertEquals(PatientStatus.IN_TREATMENT, served.getStatus(), "serve status");
        TestSupport.assertNotNull(served.getTreatmentStartTime(), "treatment timestamp");
        served.returnToWaiting();
        TestSupport.assertEquals(PatientStatus.IN_TREATMENT, queue.getPatient("S").getStatus(), "served copy defensive");
        TestSupport.assertThrows(IllegalStateException.class, () -> queue.updatePriority("S", TriageLevel.URGENT), "treated priority rejected");
        queue.requeuePatient("S");
        TestSupport.assertEquals(PatientStatus.WAITING, queue.getPatient("S").getStatus(), "requeue works");
        TestSupport.assertNotNull(queue.getPatient("S").getTreatmentStartTime(), "requeue retains treatment time");
        queue.serveNextPatient(t.plusMinutes(12));
        queue.dischargePatient("S", t.plusMinutes(20));
        TestSupport.assertEquals(PatientStatus.DISCHARGED, queue.getPatient("S").getStatus(), "discharge status");
        TestSupport.assertNotNull(queue.getPatient("S").getDischargeTime(), "discharge timestamp");
        TestSupport.assertThrows(IllegalStateException.class, () -> queue.requeuePatient("S"), "discharged cannot requeue");
        TestSupport.assertThrows(IllegalStateException.class, () -> queue.updatePriority("S", TriageLevel.URGENT), "discharged priority rejected");
        TestSupport.assertThrows(IllegalStateException.class, () -> queue.dischargePatient("S", t.plusMinutes(30)), "discharged terminal");
        TestSupport.assertTrue(queue.findPatient("missing").isEmpty(), "missing lookup empty");
        TriageQueue leftQueue = new TriageQueue();
        leftQueue.addPatient(new Patient("LEFT", "Left", TriageLevel.STANDARD, t));
        leftQueue.dischargePatient("LEFT", t.plusMinutes(1));
        TestSupport.assertEquals(PatientStatus.DISCHARGED, leftQueue.getPatient("LEFT").getStatus(), "waiting discharge means left without treatment");
        TestSupport.assertEquals(0, leftQueue.waitingCount(), "waiting discharge removes patient");
        TestSupport.assertTrue(Modifier.isFinal(TriageQueue.class.getModifiers()), "queue final");
        TriageQueue empty = new TriageQueue();
        TestSupport.assertThrows(IllegalStateException.class, empty::serveNextPatient, "empty service rejected");
    }
}
