package diet;

/**
 * Represents a recipe of the diet.
 * * A recipe consists of a a set of ingredients that are given amounts of raw materials.
 * The overall nutritional values of a recipe can be computed
 * on the basis of the ingredients' values and are expressed per 100g
 * *
 */
public class Recipe implements NutritionalElement {
	
	private String name; // ADDED FOR R3
	private Food food; // ADDED FOR R3
	private double calories = 0.0; // ADDED FOR R3
	private double proteins = 0.0; // ADDED FOR R3
	private double carbs = 0.0; // ADDED FOR R3
	private double fat = 0.0; // ADDED FOR R3
	private double weight = 0.0; // ADDED FOR R3

	// ADDED FOR R3
	public Recipe(String name, Food food) {
		this.name = name;
		this.food = food;
	}

	/**
	 * Adds the given quantity of an ingredient to the recipe.
	 * The ingredient is a raw material.
	 * * @param material the name of the raw material to be used as ingredient
	 * @param quantity the amount in grams of the raw material to be used
	 * @return the same Recipe object, it allows method chaining.
	 */
	public Recipe addIngredient(String material, double quantity) {
		NutritionalElement raw = food.getRawMaterial(material); // ADDED FOR R3
		if (raw != null) { // ADDED FOR R3
			weight += quantity; // ADDED FOR R3
			
			// Simple Math: Add the values for this ingredient to the total // ADDED FOR R3
			calories += (raw.getCalories() * quantity) / 100.0; // ADDED FOR R3
			proteins += (raw.getProteins() * quantity) / 100.0; // ADDED FOR R3
			carbs += (raw.getCarbs() * quantity) / 100.0; // ADDED FOR R3
			fat += (raw.getFat() * quantity) / 100.0; // ADDED FOR R3
		}
		return this; // ADDED FOR R3
	}

	@Override
	public String getName() {
		return name; // ADDED FOR R3
	}

	@Override
	public double getCalories() {
		if (weight == 0) return 0.0; // ADDED FOR R3
		return (calories / weight) * 100.0; // ADDED FOR R3
	}

	@Override
	public double getProteins() {
		if (weight == 0) return 0.0; // ADDED FOR R3
		return (proteins / weight) * 100.0; // ADDED FOR R3
	}

	@Override
	public double getCarbs() {
		if (weight == 0) return 0.0; // ADDED FOR R3
		return (carbs / weight) * 100.0; // ADDED FOR R3
	}

	@Override
	public double getFat() {
		if (weight == 0) return 0.0; // ADDED FOR R3
		return (fat / weight) * 100.0; // ADDED FOR R3
	}

	/**
	 * Indicates whether the nutritional values returned by the other methods
	 * refer to a conventional 100g quantity of nutritional element,
	 * or to a unit of element.
	 * * For the {@link Recipe} class it must always return {@code true}:
	 * a recipe expresses nutritional values per 100g
	 * * @return boolean indicator
	 */
	@Override
	public boolean per100g() {
		return true;
	}
}