package custom;

import diet.Food;
import diet.Recipe;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomR3RecipesTest {

	@Test
	void recipeNormalizesNutritionPer100g() {
		Food food = new Food();
		food.defineRawMaterial("Flour", 100, 10, 20, 1);
		food.defineRawMaterial("Cheese", 300, 20, 2, 25);

		Recipe recipe = food.createRecipe("Toast")
				.addIngredient("Flour", 100)
				.addIngredient("Cheese", 100);

		assertEquals(200, recipe.getCalories(), 0.001);
		assertEquals(15, recipe.getProteins(), 0.001);
		assertEquals(11, recipe.getCarbs(), 0.001);
		assertEquals(13, recipe.getFat(), 0.001);
		assertTrue(recipe.per100g());
	}

	@Test
	void recipeSupportsMethodChaining() {
		Food food = new Food();
		food.defineRawMaterial("Flour", 100, 10, 20, 1);

		Recipe recipe = food.createRecipe("Base");

		assertSame(recipe, recipe.addIngredient("Flour", 50));
	}

	@Test
	void emptyRecipeReturnsZeroNutrition() {
		Food food = new Food();
		Recipe recipe = food.createRecipe("Empty");

		assertEquals(0, recipe.getCalories(), 0.001);
		assertEquals(0, recipe.getProteins(), 0.001);
		assertEquals(0, recipe.getCarbs(), 0.001);
		assertEquals(0, recipe.getFat(), 0.001);
	}

	@Test
	void unknownRawMaterialIsRejected() {
		Food food = new Food();
		Recipe recipe = food.createRecipe("Toast");

		assertThrows(IllegalArgumentException.class,
				() -> recipe.addIngredient("Missing", 10));
	}

	@Test
	void invalidRecipeQuantityIsRejected() {
		Food food = new Food();
		food.defineRawMaterial("Flour", 100, 10, 20, 1);
		Recipe recipe = food.createRecipe("Toast");

		assertThrows(IllegalArgumentException.class,
				() -> recipe.addIngredient("Flour", 0));
		assertThrows(IllegalArgumentException.class,
				() -> recipe.addIngredient("Flour", -1));
	}
}
