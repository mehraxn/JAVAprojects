package it.polito.oop.test;

import static org.junit.jupiter.api.Assertions.*;
import static it.polito.oop.test.OOPAssertions.*;
import org.junit.jupiter.api.Test;

import hydraulic.*;

class TestR1_Elements {

	@Test
	void testEmptySystem(){
		HSystem s = new HSystem();

		Element[] elements = s.getElements();

		assertEquals(0, s.size(), "There should be no element");

		assertNotNull(elements, "Missing elements");

		assertEquals(0, elements.length, "There should be any elements at first");
	}

	@Test
	void testGetElements(){
		HSystem s = new HSystem();

		Element el1 = new Source("Test");		
		Element el2 = new Source("Test 1");
		Element el3 = new Source("Test 2");	
		s.addElement(el1);
		s.addElement(el2);
		s.addElement(el3);

		Element[] elements = s.getElements();

		assertArrayContainsAll(elements, all(el1, el2), "Missing elements");
	}
}
