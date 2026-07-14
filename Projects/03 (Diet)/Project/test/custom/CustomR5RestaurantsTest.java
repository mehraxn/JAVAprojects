package custom;

import diet.Food;
import diet.Menu;
import diet.Restaurant;
import diet.Takeaway;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomR5RestaurantsTest {

	@Test
	void restaurantIsCreatedAndNamesAreSorted() {
		Takeaway takeaway = new Takeaway();
		Restaurant roma = takeaway.addRestaurant("Roma");
		takeaway.addRestaurant("Napoli");

		assertEquals("Roma", roma.getName());
		assertEquals(List.of("Napoli", "Roma"), takeaway.restaurants().stream().toList());
	}

	@Test
	void restaurantOpenInsideIntervalAndClosedOutside() {
		Restaurant restaurant = new Takeaway().addRestaurant("Napoli");
		restaurant.setHours("08:00", "14:00", "19:00", "23:59");

		assertTrue(restaurant.isOpenAt("08:00"));
		assertTrue(restaurant.isOpenAt("13:59"));
		assertFalse(restaurant.isOpenAt("14:00"));
		assertFalse(restaurant.isOpenAt("18:59"));
	}

	@Test
	void restaurantSupportsCrossMidnightInterval() {
		Restaurant restaurant = new Takeaway().addRestaurant("Late");
		restaurant.setHours("22:00", "02:00");

		assertTrue(restaurant.isOpenAt("23:30"));
		assertTrue(restaurant.isOpenAt("01:30"));
		assertFalse(restaurant.isOpenAt("03:00"));
	}

	@Test
	void invalidHoursAreRejected() {
		Restaurant restaurant = new Takeaway().addRestaurant("Napoli");

		assertThrows(IllegalArgumentException.class,
				() -> restaurant.setHours("08:00"));
		assertThrows(IllegalArgumentException.class,
				() -> restaurant.setHours("25:00", "26:00"));
	}

	@Test
	void restaurantStoresMenusByName() {
		Food food = new Food();
		Menu menu = food.createMenu("Lunch");
		Restaurant restaurant = new Takeaway().addRestaurant("Napoli");

		restaurant.addMenu(menu);

		assertSame(menu, restaurant.getMenu("Lunch"));
		assertNull(restaurant.getMenu("Missing"));
	}

	@Test
	void nullMenuIsRejected() {
		Restaurant restaurant = new Takeaway().addRestaurant("Napoli");

		assertThrows(IllegalArgumentException.class,
				() -> restaurant.addMenu(null));
	}
}
