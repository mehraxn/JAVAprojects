# Testing Workflows with Fake Repositories

## Learning goals

- Test service logic without a database.
- Understand fake repositories vs mocks.
- Cover success and failure paths.

## Fake repository vs mock

A fake repository is a simple working implementation used for tests.

```java
public final class FakeOrderRepository implements OrderRepository {
    private final Map<String, Order> orders = new LinkedHashMap<>();

    public void save(Order order) {
        orders.put(order.id(), order);
    }

    public Optional<Order> findById(String id) {
        return Optional.ofNullable(orders.get(id));
    }
}
```

A mock is usually configured to expect calls. Fakes are often easier for beginner service tests because they behave like small in-memory storage.

## Success path test

```java
@Test
void cancelOrderMarksOrderCancelled() {
    FakeOrderRepository orders = new FakeOrderRepository();
    orders.save(new Order("O-1"));
    OrderService service = new OrderService(orders);

    OrderSnapshot result = service.cancelOrder("O-1");

    assertTrue(result.cancelled());
}
```

## Failure path tests

Test:

- invalid input;
- unauthorized user;
- not found;
- duplicate;
- state unchanged after failure.

## State unchanged example

```java
@Test
void failedTransferDoesNotChangeBalances() {
    FakeAccountRepository accounts = new FakeAccountRepository();
    accounts.save(new Account("A", 100));
    accounts.save(new Account("B", 200));

    TransferService service = new TransferService(accounts);

    assertThrows(BusinessRuleException.class, () -> service.transfer("A", "B", 500));
    assertEquals(100, accounts.findRequired("A").balance());
    assertEquals(200, accounts.findRequired("B").balance());
}
```

## Common mistakes

- Testing only the happy path.
- Using a database for every service test.
- Not asserting state after failure.
- Making fake repositories behave differently from real repository contracts.

## Mini exercises

1. Create a fake repository for `Book`.
2. Test a borrow workflow success path.
3. Test missing member and missing book paths.
4. Test unauthorized approval.

## Quick summary

Fake repositories make workflow tests fast, focused, and independent from persistence setup.
