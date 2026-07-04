package example;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import university.University;

class SimpleBaseTest {
	
	private static final int FIRST_ID = 10000;
	private static final int FIRST_CODE = 10;
	
	@Test
	void testR1() {
		String universityName = "Politecnico di Torino";
		
		University poli = new University(universityName);
		
		poli.setRector("Stefano", "Corgnati");
		
		System.out.println("Rector of " + poli.getName() + " : " + poli.getRector());

		assertEquals(universityName, poli.getName(), "Wrong university name");
		assertEquals("Stefano Corgnati", poli.getRector(), "Wrong rector");
	}
		
	@Test
	void testR2() {
		University poli = new University("PoliTo");
		int s1 = poli.enroll("Mario","Rossi");
		int s2 = poli.enroll("Giuseppe","Verdi");
		
		System.out.println("Enrolled students " + s1 + ", " + s2); // 10000, 10001
		System.out.println("s1 = " + poli.student(s1)); // 10000 Mario Rossi

		assertEquals(FIRST_ID, s1, "First student id should be "+ FIRST_ID);
		assertEquals((FIRST_ID+1), s2, "Second student id should be "+ (FIRST_ID+1));
	}
		
	@Test
	void testR3() {
		University poli = new University("PoliTo");
		int macro = poli.activate("Macro Economics", "Paul Krugman");
		int oop = poli.activate("Object Oriented Programming", "James Gosling");
		
		System.out.println("Activated courses " + macro + " and " + oop); // 10 and 11

		assertEquals(FIRST_CODE, macro, "First course id should be "+ FIRST_CODE);
		assertEquals((FIRST_CODE+1), oop, "Second course id should be "+ (FIRST_CODE+1));
	}
		
	@Test
	void testR4() {
		University poli = new University("PoliTo");
		int s1 = poli.enroll("Mario","Rossi");
		int s2 = poli.enroll("Giuseppe","Verdi");
		int macro = poli.activate("Macro Economics", "Paul Krugman");
		int oop = poli.activate("Object Oriented Programming", "James Gosling");

		poli.register(s1, macro);
		poli.register(s2, macro);
		poli.register(s2, oop);
		
		System.out.println(poli.listAttendees(macro));
		// 10000 Mario Rossi
		// 10001 Giuseppe Verdi
		String attendees = poli.listAttendees(macro);
		assertNotNull(attendees, "Missing attendees list");
		assertTrue(attendees.contains("Rossi"));
		assertTrue(attendees.contains("Verdi"), "Missing student Verdi");
		
		System.out.println(poli.studyPlan(s2));
		// 10,Macro Economics,Paul Krugman
		// 11,Object Oriented Programming,Marco Torchiano
		String plan = poli.studyPlan(s2);
		assertNotNull(plan, "Missing study plan");
		assertTrue(plan.contains("Object"), "Missing OOP course");
	}
}
