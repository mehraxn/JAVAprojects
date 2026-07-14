package diet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/**
 * Facade class for raw materials, products, recipes, and menus.
 */
public class Food {

	private static class RawMaterial implements NutritionalElement {
		private final String name;
		private final double calories;
		private final double proteins;
		private final double carbs;
		private final double fat;

		RawMaterial(String name, double calories, double proteins, double carbs, double fat) {
			this.name = name;
			this.calories = calories;
			this.proteins = proteins;
			this.carbs = carbs;
			this.fat = fat;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public double getCalories() {
			return calories;
		}

		@Override
		public double getProteins() {
			return proteins;
		}

		@Override
		public double getCarbs() {
			return carbs;
		}

		@Override
		public double getFat() {
			return fat;
		}

		@Override
		public boolean per100g() {
			return true;
		}
	}

	private static class Product implements NutritionalElement {
		private final String name;
		private final double calories;
		private final double proteins;
		private final double carbs;
		private final double fat;

		Product(String name, double calories, double proteins, double carbs, double fat) {
			this.name = name;
			this.calories = calories;
			this.proteins = proteins;
			this.carbs = carbs;
			this.fat = fat;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public double getCalories() {
			return calories;
		}

		@Override
		public double getProteins() {
			return proteins;
		}

		@Override
		public double getCarbs() {
			return carbs;
		}

		@Override
		public double getFat() {
			return fat;
		}

		@Override
		public boolean per100g() {
			return false;
		}
	}

	private final Map<String, RawMaterial> rawMaterials = new TreeMap<>();
	private final Map<String, Product> products = new TreeMap<>();
	private final Map<String, Recipe> recipes = new TreeMap<>();
	private final Map<String, Menu> menus = new TreeMap<>();

	/**
	 * Define a new raw material.
	 * The nutritional values are specified for a conventional 100g quantity.
	 *
	 * @param name unique name of the raw material
	 * @param calories calories per 100g
	 * @param proteins proteins per 100g
	 * @param carbs carbs per 100g
	 * @param fat fats per 100g
	 */
	public void defineRawMaterial(String name, double calories, double proteins, double carbs, double fat) {
		String validName = ValidationUtils.requireNotBlank(name, "raw material name");
		ValidationUtils.requireNonNegative(calories, "calories");
		ValidationUtils.requireNonNegative(proteins, "proteins");
		ValidationUtils.requireNonNegative(carbs, "carbs");
		ValidationUtils.requireNonNegative(fat, "fat");
		rawMaterials.put(validName, new RawMaterial(validName, calories, proteins, carbs, fat));
	}

	/**
	 * Retrieves the collection of all defined raw materials.
	 *
	 * @return collection of raw materials through the {@link NutritionalElement} interface
	 */
	public Collection<NutritionalElement> rawMaterials() {
		return new ArrayList<>(rawMaterials.values());
	}

	/**
	 * Retrieves a specific raw material, given its name.
	 *
	 * @param name name of the raw material
	 * @return a raw material through the {@link NutritionalElement} interface, or {@code null}
	 */
	public NutritionalElement getRawMaterial(String name) {
		if (name == null) {
			return null;
		}
		return rawMaterials.get(name);
	}

	/**
	 * Define a new packaged product.
	 * The nutritional values are specified for a unit of the product.
	 *
	 * @param name unique name of the product
	 * @param calories calories for a product unit
	 * @param proteins proteins for a product unit
	 * @param carbs carbs for a product unit
	 * @param fat fats for a product unit
	 */
	public void defineProduct(String name, double calories, double proteins, double carbs, double fat) {
		String validName = ValidationUtils.requireNotBlank(name, "product name");
		ValidationUtils.requireNonNegative(calories, "calories");
		ValidationUtils.requireNonNegative(proteins, "proteins");
		ValidationUtils.requireNonNegative(carbs, "carbs");
		ValidationUtils.requireNonNegative(fat, "fat");
		products.put(validName, new Product(validName, calories, proteins, carbs, fat));
	}

	/**
	 * Retrieves the collection of all defined products.
	 *
	 * @return collection of products through the {@link NutritionalElement} interface
	 */
	public Collection<NutritionalElement> products() {
		return new ArrayList<>(products.values());
	}

	/**
	 * Retrieves a specific product, given its name.
	 *
	 * @param name name of the product
	 * @return a product through the {@link NutritionalElement} interface, or {@code null}
	 */
	public NutritionalElement getProduct(String name) {
		if (name == null) {
			return null;
		}
		return products.get(name);
	}

	/**
	 * Creates a new recipe stored in this Food container.
	 *
	 * @param name name of the recipe
	 * @return the recipe object
	 */
	public Recipe createRecipe(String name) {
		String validName = ValidationUtils.requireNotBlank(name, "recipe name");
		return recipes.computeIfAbsent(validName, key -> new Recipe(key, this));
	}

	/**
	 * Retrieves the collection of all defined recipes.
	 *
	 * @return collection of recipes through the {@link NutritionalElement} interface
	 */
	public Collection<NutritionalElement> recipes() {
		return new ArrayList<>(recipes.values());
	}

	/**
	 * Retrieves a specific recipe, given its name.
	 *
	 * @param name name of the recipe
	 * @return a recipe through the {@link NutritionalElement} interface, or {@code null}
	 */
	public NutritionalElement getRecipe(String name) {
		if (name == null) {
			return null;
		}
		return recipes.get(name);
	}

	/**
	 * Creates a new menu.
	 *
	 * @param name name of the menu
	 * @return the menu object
	 */
	public Menu createMenu(String name) {
		String validName = ValidationUtils.requireNotBlank(name, "menu name");
		return menus.computeIfAbsent(validName, key -> new Menu(key, this));
	}

	/**
	 * Retrieves a specific menu by name.
	 *
	 * @param name name of the menu
	 * @return the menu, or {@code null}
	 */
	public Menu getMenu(String name) {
		if (name == null) {
			return null;
		}
		return menus.get(name);
	}

	/**
	 * Retrieves all defined menus sorted by name.
	 *
	 * @return defensive copy of menus
	 */
	public Collection<NutritionalElement> menus() {
		return new ArrayList<>(menus.values());
	}

	NutritionalElement requireRawMaterial(String name) {
		String validName = ValidationUtils.requireNotBlank(name, "raw material name");
		NutritionalElement element = rawMaterials.get(validName);
		if (element == null) {
			throw new IllegalArgumentException("Unknown raw material: " + validName);
		}
		return element;
	}

	Recipe requireRecipe(String name) {
		String validName = ValidationUtils.requireNotBlank(name, "recipe name");
		Recipe recipe = recipes.get(validName);
		if (recipe == null) {
			throw new IllegalArgumentException("Unknown recipe: " + validName);
		}
		return recipe;
	}

	NutritionalElement requireProduct(String name) {
		String validName = ValidationUtils.requireNotBlank(name, "product name");
		NutritionalElement product = products.get(validName);
		if (product == null) {
			throw new IllegalArgumentException("Unknown product: " + validName);
		}
		return product;
	}
}
