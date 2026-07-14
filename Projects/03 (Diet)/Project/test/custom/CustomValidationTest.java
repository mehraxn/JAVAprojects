package custom;

import diet.Customer;
import diet.Food;
import diet.Restaurant;
import diet.Takeaway;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class CustomValidationTest {

	@Test
	void blankNamesAreRejectedAcrossFacades() {
		Food food = new Food();
		Takeaway takeaway = new Takeaway();

		assertThrows(IllegalArgumentException.class, () -> food.createRecipe(" "));
		assertThrows(IllegalArgumentException.class, () -> food.createMenu(" "));
		assertThrows(IllegalArgumentException.class, () -> takeaway.addRestaurant(" "));
	}

	@Test
	void invalidCustomerDataIsRejected() {
		Takeaway takeaway = new Takeaway();

		assertThrows(IllegalArgumentException.class,
				() -> takeaway.registerCustomer("", "Smith", "s@example.com", "123"));
		assertThrows(IllegalArgumentException.class,
				() -> takeaway.registerCustomer("John", "", "s@example.com", "123"));

		Customer customer = takeaway.registerCustomer("John", "Smith", "s@example.com", "123");
		assertThrows(IllegalArgumentException.class, () -> customer.setEmail(" "));
		assertThrows(IllegalArgumentException.class, () -> customer.setPhone(" "));
	}

	@Test
	void invalidTimeValuesAreRejected() {
		Takeaway takeaway = new Takeaway();
		Restaurant restaurant = takeaway.addRestaurant("Napoli");

		assertThrows(IllegalArgumentException.class, () -> restaurant.setHours("aa:bb", "12:00"));
		assertThrows(IllegalArgumentException.class, () -> restaurant.isOpenAt("24:00"));
		assertThrows(IllegalArgumentException.class, () -> takeaway.openRestaurants("bad"));
	}

	@Test
	void unknownRestaurantOrderIsRejected() {
		Takeaway takeaway = new Takeaway();
		Customer customer = takeaway.registerCustomer("John", "Smith", "s@example.com", "123");

		assertThrows(IllegalArgumentException.class,
				() -> takeaway.createOrder(customer, "Missing", "12:00"));
	}
}
