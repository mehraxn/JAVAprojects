package diet;

/**
 * Represents a complete menu.
 * 
 * It can be made up of both packaged products and servings of given recipes.
 *
 */
public class Menu implements NutritionalElement {

	/**
	 * Adds a given serving size of a recipe.
	 * The recipe is a name of a recipe defined in the {@code food}
	 * argument of the constructor.
	 * 
	 * @param recipe the name of the recipe to be used as ingredient
	 * @param quantity the amount in grams of the recipe to be used
	 * @return the same Menu to allow method chaining
	 */
    public Menu addRecipe(String recipe, double quantity) {
		return null;
	}

	/**
	 * Adds a unit of a packaged product.
	 * The product is a name of a product defined in the {@code food}
	 * argument of the constructor.
	 * 
	 * @param product the name of the product to be used as ingredient
	 * @return the same Menu to allow method chaining
	 */
    public Menu addProduct(String product) {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}

	/**
	 * Total KCal in the menu
	 */
	@Override
	public double getCalories() {
		return -1.0;
	}

	/**
	 * Total proteins in the menu
	 */
	@Override
	public double getProteins() {
		return -1.0;
	}

	/**
	 * Total carbs in the menu
	 */
	@Override
	public double getCarbs() {
		return -1.0;
	}

	/**
	 * Total fats in the menu
	 */
	@Override
	public double getFat() {
		return -1.0;
	}

	/**
	 * Indicates whether the nutritional values returned by the other methods
	 * refer to a conventional 100g quantity of nutritional element,
	 * or to a unit of element.
	 * 
	 * For the {@link Menu} class it must always return {@code false}:
	 * nutritional values are provided for the whole menu.
	 * 
	 * @return boolean indicator
	 */
	@Override
	public boolean per100g() {
		return false;
	}
}