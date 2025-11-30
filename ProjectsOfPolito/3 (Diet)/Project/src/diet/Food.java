package diet;

import java.util.Collection;
import java.util.ArrayList; // ADDED FOR R1

/**
 * Facade class for the diet management.
 * It allows defining and retrieving raw materials and products.
 *
 */
public class Food {
	
	// --- R1: RawMaterial Inner Class ---
	private class RawMaterial implements NutritionalElement { // ADDED FOR R1
		
		private String name; // ADDED FOR R1
		private double calories; // ADDED FOR R1
		private double proteins; // ADDED FOR R1
		private double carbs; // ADDED FOR R1
		private double fat; // ADDED FOR R1

		public RawMaterial(String name, double calories, double proteins, double carbs, double fat) { // ADDED FOR R1
			this.name = name; // ADDED FOR R1
			this.calories = calories; // ADDED FOR R1
			this.proteins = proteins; // ADDED FOR R1
			this.carbs = carbs; // ADDED FOR R1
			this.fat = fat; // ADDED FOR R1
		}

		@Override public String getName() { return name; } // ADDED FOR R1
		@Override public double getCalories() { return calories; } // ADDED FOR R1
		@Override public double getProteins() { return proteins; } // ADDED FOR R1
		@Override public double getCarbs() { return carbs; } // ADDED FOR R1
		@Override public double getFat() { return fat; } // ADDED FOR R1
		
		/**
		 * For RawMaterial, values are always per 100g.
		 */
		@Override 
		public boolean per100g() { // ADDED FOR R1
			return true; // ADDED FOR R1
		}
	} // ADDED FOR R1
	
	// --- R2: Product Inner Class ---
	private class Product implements NutritionalElement { // ADDED FOR R2
		private String name; // ADDED FOR R2
		private double calories; // ADDED FOR R2
		private double proteins; // ADDED FOR R2
		private double carbs; // ADDED FOR R2
		private double fat; // ADDED FOR R2

		public Product(String name, double calories, double proteins, double carbs, double fat) { // ADDED FOR R2
			this.name = name; // ADDED FOR R2
			this.calories = calories; // ADDED FOR R2
			this.proteins = proteins; // ADDED FOR R2
			this.carbs = carbs; // ADDED FOR R2
			this.fat = fat; // ADDED FOR R2
		}

		@Override public String getName() { return name; } // ADDED FOR R2
		@Override public double getCalories() { return calories; } // ADDED FOR R2
		@Override public double getProteins() { return proteins; } // ADDED FOR R2
		@Override public double getCarbs() { return carbs; } // ADDED FOR R2
		@Override public double getFat() { return fat; } // ADDED FOR R2
		
		@Override 
		public boolean per100g() { // ADDED FOR R2
			return false; // Values are for the whole product // ADDED FOR R2
		}
	} // ADDED FOR R2

	
	// --- R1: Storage for Raw Materials (Sorted by name) ---
	private java.util.Map<String, RawMaterial> rawMaterials = new java.util.TreeMap<>(); // ADDED FOR R1
	
	// --- R2: Storage for Products (Sorted by name) ---
	private java.util.Map<String, Product> products = new java.util.TreeMap<>(); // ADDED FOR R2

	// --- R3: Storage for Recipes (Sorted by name) ---
	private java.util.Map<String, Recipe> recipes = new java.util.TreeMap<>(); // ADDED FOR R3


	/**
	 * Define a new raw material.
	 * The nutritional values are specified for a conventional 100g quantity
	 * @param name unique name of the raw material
	 * @param calories calories per 100g
	 * @param proteins proteins per 100g
	 * @param carbs carbs per 100g
	 * @param fat fats per 100g
	 */
	public void defineRawMaterial(String name, double calories, double proteins, double carbs, double fat) {
		RawMaterial material = new RawMaterial(name, calories, proteins, carbs, fat); // ADDED FOR R1
		rawMaterials.put(name, material); // ADDED FOR R1
	}

	/**
	 * Retrieves the collection of all defined raw materials
	 * @return collection of raw materials though the {@link NutritionalElement} interface
	 */
	public Collection<NutritionalElement> rawMaterials() {
		// Simple loop instead of streams // ADDED FOR R3
		ArrayList<NutritionalElement> list = new ArrayList<>(); // ADDED FOR R3
		for (RawMaterial m : rawMaterials.values()) { // ADDED FOR R3
			list.add(m); // ADDED FOR R3
		}
		return list; // ADDED FOR R3
	}

	/**
	 * Retrieves a specific raw material, given its name
	 * @param name  name of the raw material
	 * @return  a raw material though the {@link NutritionalElement} interface
	 */
	public NutritionalElement getRawMaterial(String name) {
		return rawMaterials.get(name); // ADDED FOR R1
	}

	/**
	 * Define a new packaged product.
	 * The nutritional values are specified for a unit of the product
	 * @param name unique name of the product
	 * @param calories calories for a product unit
	 * @param proteins proteins for a product unit
	* @param carbs carbs for a product unit
	 * @param fat fats for a product unit
	 */
	public void defineProduct(String name, double calories, double proteins, double carbs, double fat) {
		Product product = new Product(name, calories, proteins, carbs, fat); // ADDED FOR R2
		products.put(name, product); // ADDED FOR R2
	}

	/**
	 * Retrieves the collection of all defined products
	 * @return collection of products though the {@link NutritionalElement} interface
	 */
	public Collection<NutritionalElement> products() {
		// Simple loop instead of streams // ADDED FOR R3
		ArrayList<NutritionalElement> list = new ArrayList<>(); // ADDED FOR R3
		for (Product p : products.values()) { // ADDED FOR R3
			list.add(p); // ADDED FOR R3
		}
		return list; // ADDED FOR R3
	}

	/**
	 * Retrieves a specific product, given its name
	 * @param name  name of the product
	 * @return  a product though the {@link NutritionalElement} interface
	 */
	public NutritionalElement getProduct(String name) {
		return products.get(name); // ADDED FOR R2
	}

	/**
	 * Creates a new recipe stored in this Food container.
	 * * @param name name of the recipe
	 * @return the newly created Recipe object
	 */
	public Recipe createRecipe(String name) {
		Recipe recipe = new Recipe(name, this); // ADDED FOR R3
		recipes.put(name, recipe); // ADDED FOR R3
		return recipe; // ADDED FOR R3
	}
	
	/**
	 * Retrieves the collection of all defined recipes
	 * @return collection of recipes though the {@link NutritionalElement} interface
	 */
	public Collection<NutritionalElement> recipes() {
		// Simple loop instead of streams // ADDED FOR R3
		ArrayList<NutritionalElement> list = new ArrayList<>(); // ADDED FOR R3
		for (Recipe r : recipes.values()) { // ADDED FOR R3
			list.add(r); // ADDED FOR R3
		}
		return list; // ADDED FOR R3
	}

	/**
	 * Retrieves a specific recipe, given its name
	 * @param name  name of the recipe
	 * @return  a recipe though the {@link NutritionalElement} interface
	 */
	public NutritionalElement getRecipe(String name) {
		return recipes.get(name); // ADDED FOR R3
	}

	/**
	 * Creates a new menu
	 * * @param name name of the menu
	 * @return the newly created menu
	 */
	public Menu createMenu(String name) {
		return null;
	}
}