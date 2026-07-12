package hospitalqueuemanagement;

import java.time.LocalDateTime;
import java.util.Map;

final class QueueStatisticsTest {
    private QueueStatisticsTest() { }
    static void run() {
        LocalDateTime t = LocalDateTime.of(2026, 1, 1, 8, 0);
        TriageQueue queue = new TriageQueue();
        TestSupport.assertDoubleEquals(0.0, queue.calculateAverageCurrentWaitMinutes(t), 0.0, "empty average is zero");
        TestSupport.assertTrue(queue.longestWaitingPatient(t).isEmpty(), "empty longest wait");
        TestSupport.assertTrue(queue.calculateAverageServedWaitMinutes().isEmpty(), "empty served average");
        queue.addPatient(new Patient("A", "A", TriageLevel.URGENT, t));
        queue.addPatient(new Patient("B", "B", TriageLevel.STANDARD, t.plusMinutes(20)));
        TestSupport.assertEquals(2, queue.waitingCount(), "waiting count");
        TestSupport.assertEquals(2, queue.totalRecordCount(), "record count");
        Map<TriageLevel, Long> counts = queue.countWaitingByTriageLevel();
        TestSupport.assertEquals(1L, counts.get(TriageLevel.URGENT), "urgent count");
        TestSupport.assertEquals(0L, counts.get(TriageLevel.EMERGENCY), "zero level included");
        TestSupport.assertThrows(UnsupportedOperationException.class, () -> counts.put(TriageLevel.URGENT, 9L), "counts unmodifiable");
        TestSupport.assertEquals("A", queue.longestWaitingPatient(t.plusHours(1)).orElseThrow().getId(), "longest waiting");
        TestSupport.assertDoubleEquals(50.0, queue.calculateAverageCurrentWaitMinutes(t.plusHours(1)), 0.001, "average current wait");
        TestSupport.assertThrows(IllegalArgumentException.class, () -> queue.calculateAverageCurrentWaitMinutes(t.minusMinutes(1)), "past now rejected");
        Patient longest = queue.longestWaitingPatient(t.plusHours(1)).orElseThrow();
        longest.setTriageLevel(TriageLevel.EMERGENCY);
        TestSupport.assertEquals(TriageLevel.URGENT, queue.getPatient("A").getTriageLevel(), "longest is defensive");
        queue.serveNextPatient(t.plusMinutes(30));
        TestSupport.assertDoubleEquals(30.0, queue.calculateAverageServedWaitMinutes().orElseThrow(), 0.001, "served average");
        TestSupport.assertEquals(1, queue.waitingCount(), "stats update after serving");
    }
}
