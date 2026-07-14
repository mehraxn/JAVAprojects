package custom;

import diet.Customer;
import diet.Food;
import diet.Menu;
import diet.Order;
import diet.Restaurant;
import diet.Takeaway;
import org.junit.jupiter.api.Test;

import static diet.Order.OrderStatus.READY;
import static diet.Order.PaymentMethod.CARD;
import static org.junit.jupiter.api.Assertions.*;

class CustomR7OrdersTest {

	@Test
	void orderHasDefaultsAndCanChangeStatusAndPayment() {
		Fixture fixture = fixture();

		Order order = fixture.takeaway.createOrder(fixture.customer, "Napoli", "11:30");

		assertEquals(Order.PaymentMethod.CASH, order.getPaymentMethod());
		assertEquals(Order.OrderStatus.ORDERED, order.getStatus());

		order.setPaymentMethod(CARD);
		order.setStatus(READY);

		assertEquals(CARD, order.getPaymentMethod());
		assertEquals(READY, order.getStatus());
	}

	@Test
	void orderAddsMenusSortedByName() {
		Fixture fixture = fixture();
		Order order = fixture.takeaway.createOrder(fixture.customer, "Napoli", "11:30");

		order.addMenus("Dinner", 1).addMenus("Lunch", 2);

		assertEquals("Napoli, Ralph Fiennes : (11:30):\n\tDinner->1\n\tLunch->2",
				order.toString());
	}

	@Test
	void duplicateMenuLineReplacesQuantity() {
		Fixture fixture = fixture();
		Order order = fixture.takeaway.createOrder(fixture.customer, "Napoli", "11:30");

		order.addMenus("Lunch", 1).addMenus("Lunch", 3);

		assertEquals("Napoli, Ralph Fiennes : (11:30):\n\tLunch->3",
				order.toString());
	}

	@Test
	void orderDeliveryTimeAdjustsToNextOpening() {
		Fixture fixture = fixture();

		Order order = fixture.takeaway.createOrder(fixture.customer, "Napoli", "17:00");

		assertEquals("19:00", order.getDeliveryTime());
	}

	@Test
	void invalidOrderMenuLineIsRejected() {
		Fixture fixture = fixture();
		Order order = fixture.takeaway.createOrder(fixture.customer, "Napoli", "11:30");

		assertThrows(IllegalArgumentException.class,
				() -> order.addMenus("Missing", 1));
		assertThrows(IllegalArgumentException.class,
				() -> order.addMenus("Lunch", 0));
	}

	private Fixture fixture() {
		Takeaway takeaway = new Takeaway();
		Restaurant restaurant = takeaway.addRestaurant("Napoli");
		restaurant.setHours("08:00", "14:00", "19:00", "23:00");

		Food food = new Food();
		Menu lunch = food.createMenu("Lunch");
		Menu dinner = food.createMenu("Dinner");
		restaurant.addMenu(lunch);
		restaurant.addMenu(dinner);

		Customer customer = takeaway.registerCustomer("Ralph", "Fiennes", "r@example.com", "123");
		return new Fixture(takeaway, customer);
	}

	private record Fixture(Takeaway takeaway, Customer customer) {
	}
}
