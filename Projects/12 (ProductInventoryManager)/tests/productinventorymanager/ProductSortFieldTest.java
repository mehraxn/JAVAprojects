package productinventorymanager;

import static productinventorymanager.TestSupport.assertEquals;
import static productinventorymanager.TestSupport.assertNotNull;

final class ProductSortFieldTest {

    private ProductSortFieldTest() {
    }

    static void register(TestRunner runner) {
        runner.test("ProductSortField: has NAME, PRICE, QUANTITY", () -> {
            assertEquals(3, ProductSortField.values().length, "three fields");
            assertNotNull(ProductSortField.valueOf("NAME"), "NAME present");
            assertNotNull(ProductSortField.valueOf("PRICE"), "PRICE present");
            assertNotNull(ProductSortField.valueOf("QUANTITY"), "QUANTITY present");
        });
    }
}
