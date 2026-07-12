package hospitalqueuemanagement;

import java.lang.reflect.Modifier;
import java.time.LocalDateTime;

final class PatientTest {
    private PatientTest() { }
    static void run() {
        LocalDateTime time = LocalDateTime.of(2026, 1, 1, 8, 0);
        Patient patient = new Patient(" P1 ", " Ada ", TriageLevel.URGENT, time);
        TestSupport.assertEquals("P1", patient.getId(), "ID stored and trimmed");
        TestSupport.assertEquals("Ada", patient.getName(), "name stored and trimmed");
        TestSupport.assertEquals(TriageLevel.URGENT, patient.getTriageLevel(), "triage stored");
        TestSupport.assertEquals(time, patient.getArrivalTime(), "arrival stored");
        TestSupport.assertEquals(PatientStatus.WAITING, patient.getStatus(), "initial status");
        TestSupport.assertNull(patient.getTreatmentStartTime(), "initial treatment time");
        TestSupport.assertNull(patient.getDischargeTime(), "initial discharge time");
        TestSupport.assertThrows(IllegalArgumentException.class, () -> new Patient(null, "A", TriageLevel.URGENT, time), "null ID rejected");
        TestSupport.assertThrows(IllegalArgumentException.class, () -> new Patient(" ", "A", TriageLevel.URGENT, time), "blank ID rejected");
        TestSupport.assertThrows(IllegalArgumentException.class, () -> new Patient("P", null, TriageLevel.URGENT, time), "null name rejected");
        TestSupport.assertThrows(IllegalArgumentException.class, () -> new Patient("P", " ", TriageLevel.URGENT, time), "blank name rejected");
        TestSupport.assertThrows(IllegalArgumentException.class, () -> new Patient("P", "A", null, time), "null triage rejected");
        TestSupport.assertThrows(IllegalArgumentException.class, () -> new Patient("P", "A", TriageLevel.URGENT, null), "null arrival rejected");
        TestSupport.assertThrows(IllegalArgumentException.class, () -> new Patient("P", "A", TriageLevel.URGENT, time, null, null, null), "null status rejected");
        TestSupport.assertThrows(IllegalArgumentException.class, () -> new Patient("P", "A", TriageLevel.URGENT, time, PatientStatus.IN_TREATMENT, time.minusMinutes(1), null), "early treatment rejected");
        TestSupport.assertThrows(IllegalArgumentException.class, () -> new Patient("P", "A", TriageLevel.URGENT, time, PatientStatus.DISCHARGED, null, time.minusMinutes(1)), "early discharge rejected");
        TestSupport.assertThrows(IllegalArgumentException.class, () -> new Patient("P", "A", TriageLevel.URGENT, time, PatientStatus.DISCHARGED, time.plusMinutes(2), time.plusMinutes(1)), "discharge before treatment rejected");
        TestSupport.assertTrue(Modifier.isFinal(Patient.class.getModifiers()), "Patient final");
        Patient copy = patient.copy();
        TestSupport.assertNotEquals(patient, copy, "copy has separate identity");
        TestSupport.assertEquals(patient.getId(), copy.getId(), "copy preserves data");
    }
}
