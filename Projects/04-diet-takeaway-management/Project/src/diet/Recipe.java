package diet;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a recipe made of raw materials and quantities in grams.
 */
public class Recipe implements NutritionalElement {

	private static class Ingredient {
		private final NutritionalElement rawMaterial;
		private final double quantity;

		Ingredient(NutritionalElement rawMaterial, double quantity) {
			this.rawMaterial = rawMaterial;
			this.quantity = quantity;
		}
	}

	private final String name;
	private final Food food;
	private final List<Ingredient> ingredients = new ArrayList<>();

	Recipe(String name, Food food) {
		this.name = ValidationUtils.requireNotBlank(name, "recipe name");
		if (food == null) {
			throw new IllegalArgumentException("food cannot be null");
		}
		this.food = food;
	}

	/**
	 * Adds the given quantity of a raw material to the recipe.
	 *
	 * @param material the raw material name
	 * @param quantity the amount in grams
	 * @return this recipe for method chaining
	 */
	public Recipe addIngredient(String material, double quantity) {
		NutritionalElement raw = food.requireRawMaterial(material);
		double validQuantity = ValidationUtils.requirePositive(quantity, "ingredient quantity");
		ingredients.add(new Ingredient(raw, validQuantity));
		return this;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public double getCalories() {
		return per100gValue(NutritionalElement::getCalories);
	}

	@Override
	public double getProteins() {
		return per100gValue(NutritionalElement::getProteins);
	}

	@Override
	public double getCarbs() {
		return per100gValue(NutritionalElement::getCarbs);
	}

	@Override
	public double getFat() {
		return per100gValue(NutritionalElement::getFat);
	}

	@Override
	public boolean per100g() {
		return true;
	}

	private double per100gValue(NutrientExtractor extractor) {
		double totalWeight = totalWeight();
		if (totalWeight == 0.0) {
			return 0.0;
		}

		double total = 0.0;
		for (Ingredient ingredient : ingredients) {
			total += extractor.valueOf(ingredient.rawMaterial) * ingredient.quantity / 100.0;
		}
		return total / totalWeight * 100.0;
	}

	private double totalWeight() {
		double total = 0.0;
		for (Ingredient ingredient : ingredients) {
			total += ingredient.quantity;
		}
		return total;
	}

	@FunctionalInterface
	private interface NutrientExtractor {
		double valueOf(NutritionalElement element);
	}
}
