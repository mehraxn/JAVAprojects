package custom;

import diet.Food;
import diet.Menu;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomR4MenusTest {

	@Test
	void emptyMenuHasZeroNutritionAndIsPerUnit() {
		Food food = new Food();
		Menu menu = food.createMenu("Empty");

		assertEquals("Empty", menu.getName());
		assertEquals(0, menu.getCalories(), 0.001);
		assertFalse(menu.per100g());
	}

	@Test
	void menuCombinesRecipePortionsAndProducts() {
		Food food = sampleFood();

		Menu menu = food.createMenu("Lunch")
				.addRecipe("Toast", 200)
				.addProduct("Snack");

		assertEquals(500, menu.getCalories(), 0.001);
		assertEquals(32, menu.getProteins(), 0.001);
		assertEquals(42, menu.getCarbs(), 0.001);
		assertEquals(31, menu.getFat(), 0.001);
	}

	@Test
	void menuSupportsMethodChaining() {
		Food food = sampleFood();
		Menu menu = food.createMenu("Lunch");

		assertSame(menu, menu.addRecipe("Toast", 100));
		assertSame(menu, menu.addProduct("Snack"));
	}

	@Test
	void unknownRecipeIsRejected() {
		Food food = sampleFood();
		Menu menu = food.createMenu("Lunch");

		assertThrows(IllegalArgumentException.class,
				() -> menu.addRecipe("Missing", 100));
	}

	@Test
	void unknownProductIsRejected() {
		Food food = sampleFood();
		Menu menu = food.createMenu("Lunch");

		assertThrows(IllegalArgumentException.class,
				() -> menu.addProduct("Missing"));
	}

	@Test
	void invalidRecipePortionIsRejected() {
		Food food = sampleFood();
		Menu menu = food.createMenu("Lunch");

		assertThrows(IllegalArgumentException.class,
				() -> menu.addRecipe("Toast", 0));
	}

	private Food sampleFood() {
		Food food = new Food();
		food.defineRawMaterial("Flour", 100, 10, 20, 1);
		food.defineRawMaterial("Cheese", 300, 20, 2, 25);
		food.defineProduct("Snack", 100, 2, 20, 5);
		food.createRecipe("Toast")
				.addIngredient("Flour", 100)
				.addIngredient("Cheese", 100);
		return food;
	}
}
