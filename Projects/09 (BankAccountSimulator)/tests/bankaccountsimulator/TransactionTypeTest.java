package bankaccountsimulator;

import static bankaccountsimulator.TestSupport.assertEquals;
import static bankaccountsimulator.TestSupport.assertNotNull;

final class TransactionTypeTest {

    private TransactionTypeTest() {
    }

    static void register(TestRunner runner) {
        runner.test("TransactionType: expected values exist", () -> {
            assertEquals(4, TransactionType.values().length, "four types");
            assertNotNull(TransactionType.valueOf("DEPOSIT"), "DEPOSIT");
            assertNotNull(TransactionType.valueOf("WITHDRAWAL"), "WITHDRAWAL");
            assertNotNull(TransactionType.valueOf("TRANSFER_IN"), "TRANSFER_IN");
            assertNotNull(TransactionType.valueOf("TRANSFER_OUT"), "TRANSFER_OUT");
        });
    }
}
