package librarymanagementsystem;

import static librarymanagementsystem.TestSupport.assertEquals;
import static librarymanagementsystem.TestSupport.assertNotNull;

final class LoanStatusTest {

    private LoanStatusTest() {
    }

    static void register(TestRunner runner) {
        runner.test("LoanStatus: has ACTIVE and RETURNED", () -> {
            assertEquals(2, LoanStatus.values().length, "two statuses");
            assertNotNull(LoanStatus.valueOf("ACTIVE"), "ACTIVE");
            assertNotNull(LoanStatus.valueOf("RETURNED"), "RETURNED");
        });
    }
}
