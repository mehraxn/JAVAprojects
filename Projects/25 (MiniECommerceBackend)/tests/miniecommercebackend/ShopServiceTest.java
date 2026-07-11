package miniecommercebackend;
import java.math.BigDecimal;
import java.util.List;
final class ShopServiceTest {
    private ShopServiceTest() { }
    static void run() {
        ShopService s = shop();
        Assertions.assertEquals(2, s.listProducts().size(), "add products");
        Assertions.assertThrows(IllegalArgumentException.class, () -> s.addProduct(new Product("P1", "Other", BigDecimal.ONE, 1)), "duplicate ID");
        Assertions.assertThrows(IllegalArgumentException.class, () -> s.addProduct(new Product("P3", "widget", BigDecimal.ONE, 1)), "duplicate name");
        Cart c = s.createCart(); Assertions.assertEquals("CART-1", c.getId(), "cart creation");
        s.addToCart(c.getId(), "P1", 2); Assertions.assertEquals(2, s.findCart(c.getId()).getItems().get("P1"), "add to cart");
        Assertions.assertThrows(IllegalArgumentException.class, () -> s.addToCart(c.getId(), "missing", 1), "missing product");
        Assertions.assertThrows(IllegalArgumentException.class, () -> s.addToCart("missing", "P1", 1), "missing cart");
        s.addToCart(c.getId(), "P2", 1);
        Assertions.assertBigDecimalEquals(new BigDecimal("25.50"), s.calculateCartTotal(c.getId()), "decimal cart total");
        Order o = s.placeOrder(c.getId());
        Assertions.assertEquals(Order.Status.CREATED, o.getStatus(), "checkout order");
        Assertions.assertBigDecimalEquals(new BigDecimal("25.5"), o.getTotal(), "decimal order total");
        Assertions.assertEquals(3, stock(s, "P1"), "first stock reduced");
        Assertions.assertEquals(1, stock(s, "P2"), "second stock reduced");
        Assertions.assertNull(s.findCart(c.getId()), "cart removed");
        Assertions.assertThrows(IllegalArgumentException.class, () -> s.placeOrder(c.getId()), "repeat checkout");

        ShopService f = shop(); Cart losing = f.createCart(); f.addToCart(losing.getId(), "P1", 5);
        Product external = f.listProducts().get(0); external.decreaseStock(5);
        Assertions.assertEquals(5, stock(f, "P1"), "catalog defensive copy");
        Cart winning = f.createCart(); f.addToCart(winning.getId(), "P1", 5); f.placeOrder(winning.getId());
        Assertions.assertThrows(IllegalStateException.class, () -> f.placeOrder(losing.getId()), "insufficient checkout");
        Assertions.assertEquals(0, stock(f, "P1"), "failed checkout stock unchanged");
        Assertions.assertEquals(5, f.findCart(losing.getId()).getItems().get("P1"), "failed checkout cart unchanged");

        ShopService x = shop(); Cart cc = x.createCart(); x.addToCart(cc.getId(), "P1", 2); Order co = x.placeOrder(cc.getId());
        Assertions.assertTrue(x.updateOrderStatus(co.getId(), Order.Status.CANCELLED), "cancel created");
        Assertions.assertEquals(5, stock(x, "P1"), "cancel restores stock");
        Assertions.assertEquals(Order.Status.CANCELLED, x.findOrder(co.getId()).getStatus(), "cancel status");
        Assertions.assertThrows(IllegalStateException.class, () -> x.updateOrderStatus(co.getId(), Order.Status.PAID), "cancel terminal");

        ShopService p = shop(); Cart pc = p.createCart(); p.addToCart(pc.getId(), "P2", 1); Order po = p.placeOrder(pc.getId());
        Assertions.assertTrue(p.updateOrderStatus(po.getId(), Order.Status.PAID), "pay order");
        Assertions.assertThrows(IllegalStateException.class, () -> p.updateOrderStatus(po.getId(), Order.Status.CANCELLED), "paid cannot cancel");
        Assertions.assertFalse(p.updateOrderStatus("missing", Order.Status.PAID), "missing order update");
        Assertions.assertNull(p.findOrder("missing"), "missing order lookup");
        Assertions.assertThrows(IllegalArgumentException.class, () -> p.findCart(" "), "invalid cart ID");
        Assertions.assertThrows(UnsupportedOperationException.class, () -> p.listProducts().clear(), "catalog unmodifiable");
        List<Order> orders = p.listOrders();
        Assertions.assertThrows(UnsupportedOperationException.class, () -> orders.clear(), "orders unmodifiable");
        Order exposed = p.findOrder(po.getId());
        Assertions.assertThrows(IllegalStateException.class, () -> exposed.setStatus(Order.Status.CANCELLED), "copy status rules");
        Assertions.assertEquals(Order.Status.PAID, p.findOrder(po.getId()).getStatus(), "order defensive copy");
    }
    private static ShopService shop() {
        ShopService s = new ShopService();
        s.addProduct(new Product("P1", "Widget", new BigDecimal("10.25"), 5));
        s.addProduct(new Product("P2", "Cable", new BigDecimal("5.00"), 2)); return s;
    }
    private static int stock(ShopService s, String id) {
        for (Product p : s.listProducts()) if (p.getId().equals(id)) return p.getStockQuantity();
        throw new AssertionError("missing test product");
    }
}
