package example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static it.polito.oop.test.TestBranchUtils.*;

import diet.Food;
import diet.NutritionalElement;
import diet.Menu;
import diet.Recipe;


class TestExampleBase {

	private Food foods;
	
	@BeforeEach
    void setUp() {
		foods = new Food();
	}

	@Test
    void testR1()  {
        assumeRequirementAtLeast(1);
        foods.defineRawMaterial("Sugar", 400, 0, 100, 0);
        foods.defineRawMaterial("Mais", 70, 2.7, 12, 1.3);
        foods.defineRawMaterial("Pasta", 350, 12, 72.2, 1.5);
        foods.defineRawMaterial("Oil", 900, 0, 0, 100);
        foods.defineRawMaterial("Nutella", 530, 6.8, 56, 31);
    
        assertNotNull(foods.rawMaterials(), "Missing materials");
        assertEquals(5, foods.rawMaterials().size(), "There should be 5 materials");
    
        NutritionalElement oil = foods.getRawMaterial("Oil");
        assertNotNull(oil, "Missing material oil");
        assertEquals(900, oil.getCalories(), 0.1, "Wrong calories");
        assertEquals(100, oil.getFat(), 0.1, "Wrong calories");
	}

	@Test
    void testR2() {
        assumeRequirementAtLeast(2);
        foods.defineProduct("Crackers", 111, 2.6, 17.2, 3.5);
    
        NutritionalElement crackers = foods.getProduct("Crackers");
    
        assertNotNull(crackers, "Missing product");
        assertEquals(17.2, crackers.getCarbs(), 0.1, "Wrong carbs");
	}

	@Test
    void testR3()  {
        assumeRequirementAtLeast(3);
        foods.defineRawMaterial("Pasta", 350, 12, 72.2, 1.5);
        foods.defineRawMaterial("Nutella", 530, 6.8, 56, 31);

        Recipe r = foods.createRecipe("Pasta and Nutella");
    
        assertNotNull(r, "Missing recipe");

        r.addIngredient("Pasta", 70).
          addIngredient("Nutella", 30);
    
        assertEquals(404.0, r.getCalories(), 0.1, "Computation of calories for recipe is wrong");
        assertTrue(r.per100g(), "Recipe values should be per 100g");
	}

	@Test
    void testR4() {
        assumeRequirementAtLeast(4);
        foods.defineRawMaterial("Pasta", 350, 12, 72.2, 1.5);
        foods.defineRawMaterial("Nutella", 530, 6.8, 56, 31);
        foods.defineProduct("Crackers", 111, 2.6, 17.2, 3.5);

        Recipe r = foods.createRecipe("Pasta and Nutella");

        assertNotNull(r, "Missing recipe");

        r.addIngredient("Pasta", 70)
                .addIngredient("Nutella", 30);
    
        Menu menu1 = foods.createMenu("M1");
        assertNotNull(menu1, "Missing recipe");
    
        menu1.addRecipe("Pasta and Nutella", 50).
        	 addProduct("Crackers");
    
        assertFalse(menu1.per100g());
        assertEquals(313, menu1.getCalories(), 0.1);
        assertEquals(7.8, menu1.getProteins(), 0.1);
        assertEquals(8.7, menu1.getFat(), 0.1);
	}
}
