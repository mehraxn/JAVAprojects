package custom;

import diet.Customer;
import diet.Food;
import diet.Menu;
import diet.Order;
import diet.Restaurant;
import diet.Takeaway;
import org.junit.jupiter.api.Test;

import static diet.Order.OrderStatus.DELIVERED;
import static org.junit.jupiter.api.Assertions.*;

class CustomEndToEndWorkflowTest {

	@Test
	void completeDietAndTakeawayWorkflowWorks() {
		Food food = new Food();
		food.defineRawMaterial("Pasta", 350, 12, 72, 2);
		food.defineRawMaterial("Tomato", 50, 2, 8, 1);
		food.defineProduct("Water", 0, 0, 0, 0);
		food.createRecipe("Pasta Tomato")
				.addIngredient("Pasta", 80)
				.addIngredient("Tomato", 120);

		Menu menu = food.createMenu("Simple Lunch")
				.addRecipe("Pasta Tomato", 200)
				.addProduct("Water");

		Takeaway takeaway = new Takeaway();
		Restaurant restaurant = takeaway.addRestaurant("Napoli");
		restaurant.setHours("08:00", "14:00", "19:00", "23:00");
		restaurant.addMenu(menu);
		Customer customer = takeaway.registerCustomer("Ralph", "Fiennes", "r@example.com", "123");

		Order order = takeaway.createOrder(customer, "Napoli", "17:00")
				.addMenus("Simple Lunch", 2);
		order.setStatus(DELIVERED);

		assertEquals("19:00", order.getDeliveryTime());
		assertTrue(takeaway.openRestaurants("19:30").contains(restaurant));
		assertEquals("Napoli, Ralph Fiennes : (19:00):\n\tSimple Lunch->2",
				restaurant.ordersWithStatus(DELIVERED));
		assertTrue(menu.getCalories() > 0);
	}
}
