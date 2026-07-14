package diet;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a complete menu made of recipe portions and packaged products.
 */
public class Menu implements NutritionalElement {

	private static class RecipeItem {
		private final Recipe recipe;
		private final double quantity;

		RecipeItem(Recipe recipe, double quantity) {
			this.recipe = recipe;
			this.quantity = quantity;
		}
	}

	private final String name;
	private final Food food;
	private final List<RecipeItem> recipes = new ArrayList<>();
	private final List<NutritionalElement> products = new ArrayList<>();

	Menu(String name, Food food) {
		this.name = ValidationUtils.requireNotBlank(name, "menu name");
		if (food == null) {
			throw new IllegalArgumentException("food cannot be null");
		}
		this.food = food;
	}

	/**
	 * Adds a given serving size of a recipe.
	 *
	 * @param recipe the recipe name
	 * @param quantity the amount in grams
	 * @return this menu for method chaining
	 */
	public Menu addRecipe(String recipe, double quantity) {
		Recipe knownRecipe = food.requireRecipe(recipe);
		double validQuantity = ValidationUtils.requirePositive(quantity, "recipe quantity");
		recipes.add(new RecipeItem(knownRecipe, validQuantity));
		return this;
	}

	/**
	 * Adds one unit of a packaged product.
	 *
	 * @param product the product name
	 * @return this menu for method chaining
	 */
	public Menu addProduct(String product) {
		products.add(food.requireProduct(product));
		return this;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public double getCalories() {
		return totalValue(NutritionalElement::getCalories);
	}

	@Override
	public double getProteins() {
		return totalValue(NutritionalElement::getProteins);
	}

	@Override
	public double getCarbs() {
		return totalValue(NutritionalElement::getCarbs);
	}

	@Override
	public double getFat() {
		return totalValue(NutritionalElement::getFat);
	}

	@Override
	public boolean per100g() {
		return false;
	}

	private double totalValue(NutrientExtractor extractor) {
		double total = 0.0;
		for (RecipeItem item : recipes) {
			total += extractor.valueOf(item.recipe) * item.quantity / 100.0;
		}
		for (NutritionalElement product : products) {
			total += extractor.valueOf(product);
		}
		return total;
	}

	@FunctionalInterface
	private interface NutrientExtractor {
		double valueOf(NutritionalElement element);
	}
}
