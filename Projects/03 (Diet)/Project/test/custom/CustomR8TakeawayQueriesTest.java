package custom;

import diet.Customer;
import diet.Food;
import diet.Order;
import diet.Restaurant;
import diet.Takeaway;
import org.junit.jupiter.api.Test;

import java.util.List;

import static diet.Order.OrderStatus.DELIVERED;
import static diet.Order.OrderStatus.READY;
import static org.junit.jupiter.api.Assertions.*;

class CustomR8TakeawayQueriesTest {

	@Test
	void openRestaurantsReturnsSortedMatches() {
		Takeaway takeaway = new Takeaway();
		Restaurant roma = takeaway.addRestaurant("Roma");
		roma.setHours("09:00", "12:00");
		Restaurant napoli = takeaway.addRestaurant("Napoli");
		napoli.setHours("08:00", "13:00");
		Restaurant milano = takeaway.addRestaurant("Milano");
		milano.setHours("18:00", "22:00");

		List<String> open = takeaway.openRestaurants("10:00").stream()
				.map(Restaurant::getName)
				.toList();

		assertEquals(List.of("Napoli", "Roma"), open);
	}

	@Test
	void openRestaurantsReturnsEmptyCollectionWhenNoneAreOpen() {
		Takeaway takeaway = new Takeaway();
		Restaurant restaurant = takeaway.addRestaurant("Roma");
		restaurant.setHours("09:00", "12:00");

		assertTrue(takeaway.openRestaurants("18:00").isEmpty());
	}

	@Test
	void ordersWithStatusReturnsMatchingOrdersOnly() {
		Fixture fixture = fixture();
		Order delivered = fixture.takeaway.createOrder(fixture.ralph, "Napoli", "19:30")
				.addMenus("Lunch", 1);
		delivered.setStatus(DELIVERED);
		fixture.takeaway.createOrder(fixture.judi, "Napoli", "19:45")
				.addMenus("Lunch", 2)
				.setStatus(READY);

		String result = fixture.restaurant.ordersWithStatus(DELIVERED);

		assertEquals("Napoli, Ralph Fiennes : (19:30):\n\tLunch->1", result);
	}

	@Test
	void ordersWithStatusIsDeterministic() {
		Fixture fixture = fixture();
		Order second = fixture.takeaway.createOrder(fixture.ralph, "Napoli", "19:30")
				.addMenus("Lunch", 1);
		Order first = fixture.takeaway.createOrder(fixture.judi, "Napoli", "19:00")
				.addMenus("Lunch", 1);
		second.setStatus(DELIVERED);
		first.setStatus(DELIVERED);

		assertEquals("Napoli, Judi Dench : (19:00):\n\tLunch->1\n"
						+ "Napoli, Ralph Fiennes : (19:30):\n\tLunch->1",
				fixture.restaurant.ordersWithStatus(DELIVERED));
	}

	private Fixture fixture() {
		Takeaway takeaway = new Takeaway();
		Restaurant restaurant = takeaway.addRestaurant("Napoli");
		restaurant.setHours("08:00", "14:00", "19:00", "23:00");
		Food food = new Food();
		restaurant.addMenu(food.createMenu("Lunch"));
		Customer ralph = takeaway.registerCustomer("Ralph", "Fiennes", "r@example.com", "123");
		Customer judi = takeaway.registerCustomer("Judi", "Dench", "j@example.com", "456");
		return new Fixture(takeaway, restaurant, ralph, judi);
	}

	private record Fixture(Takeaway takeaway, Restaurant restaurant, Customer ralph, Customer judi) {
	}
}
