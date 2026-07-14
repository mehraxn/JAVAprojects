package custom;

import diet.Food;
import diet.NutritionalElement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomR1RawMaterialsTest {

	@Test
	void rawMaterialIsStoredAndReturnedPer100g() {
		Food food = new Food();
		food.defineRawMaterial("Flour", 340, 10, 70, 1);

		NutritionalElement flour = food.getRawMaterial("Flour");

		assertNotNull(flour);
		assertEquals("Flour", flour.getName());
		assertEquals(340, flour.getCalories(), 0.001);
		assertEquals(10, flour.getProteins(), 0.001);
		assertEquals(70, flour.getCarbs(), 0.001);
		assertEquals(1, flour.getFat(), 0.001);
		assertTrue(flour.per100g());
	}

	@Test
	void rawMaterialsAreSortedByName() {
		Food food = new Food();
		food.defineRawMaterial("Tomato", 20, 1, 4, 0);
		food.defineRawMaterial("Flour", 340, 10, 70, 1);
		food.defineRawMaterial("Cheese", 300, 20, 2, 25);

		List<String> names = food.rawMaterials().stream()
				.map(NutritionalElement::getName)
				.toList();

		assertEquals(List.of("Cheese", "Flour", "Tomato"), names);
	}

	@Test
	void rawMaterialCollectionIsDefensiveCopy() {
		Food food = new Food();
		food.defineRawMaterial("Flour", 340, 10, 70, 1);

		food.rawMaterials().clear();

		assertEquals(1, food.rawMaterials().size());
	}

	@Test
	void invalidRawMaterialDataIsRejected() {
		Food food = new Food();

		assertThrows(IllegalArgumentException.class,
				() -> food.defineRawMaterial(" ", 100, 1, 1, 1));
		assertThrows(IllegalArgumentException.class,
				() -> food.defineRawMaterial("Bad", -1, 1, 1, 1));
		assertThrows(IllegalArgumentException.class,
				() -> food.defineRawMaterial("Bad", 1, -1, 1, 1));
	}
}
