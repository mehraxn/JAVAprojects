package custom;

import diet.Food;
import diet.NutritionalElement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomR2ProductsTest {

	@Test
	void productIsStoredAndReturnedPerUnit() {
		Food food = new Food();
		food.defineProduct("Snack", 150, 3, 20, 5);

		NutritionalElement snack = food.getProduct("Snack");

		assertNotNull(snack);
		assertEquals("Snack", snack.getName());
		assertEquals(150, snack.getCalories(), 0.001);
		assertFalse(snack.per100g());
	}

	@Test
	void productsAreSortedByName() {
		Food food = new Food();
		food.defineProduct("Water", 0, 0, 0, 0);
		food.defineProduct("Snack", 150, 3, 20, 5);
		food.defineProduct("Juice", 80, 1, 18, 0);

		List<String> names = food.products().stream()
				.map(NutritionalElement::getName)
				.toList();

		assertEquals(List.of("Juice", "Snack", "Water"), names);
	}

	@Test
	void invalidProductDataIsRejected() {
		Food food = new Food();

		assertThrows(IllegalArgumentException.class,
				() -> food.defineProduct(null, 1, 1, 1, 1));
		assertThrows(IllegalArgumentException.class,
				() -> food.defineProduct("Bad", 1, 1, -1, 1));
		assertThrows(IllegalArgumentException.class,
				() -> food.defineProduct("Bad", 1, 1, 1, -1));
	}
}
