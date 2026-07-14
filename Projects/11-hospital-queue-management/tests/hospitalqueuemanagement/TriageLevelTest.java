package hospitalqueuemanagement;

final class TriageLevelTest {
    private TriageLevelTest() { }
    static void run() {
        TestSupport.assertTrue(TriageLevel.EMERGENCY.getPriority() < TriageLevel.URGENT.getPriority(), "emergency above urgent");
        TestSupport.assertTrue(TriageLevel.URGENT.getPriority() < TriageLevel.STANDARD.getPriority(), "urgent above standard");
        TestSupport.assertEquals(1, TriageLevel.EMERGENCY.getPriority(), "deterministic priority");
        TestSupport.assertEquals("Non urgent", TriageLevel.NON_URGENT.getDisplayName(), "display name");
    }
}
