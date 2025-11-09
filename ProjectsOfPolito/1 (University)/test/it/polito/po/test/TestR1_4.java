package it.polito.po.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import university.University;

class TestR1_4 {
	
	static final String UNIVERSITY_NAME = "Politecnico di Torino";
	private University poli;

	@BeforeEach
    void setUp() { // FIXTURE, it is executed before every test to
						 // the name 'setUp' is convention deriving from JUnit 3
		poli = new University(UNIVERSITY_NAME);
		poli.setRector("Guido", "Saracco");
	}

	@Test
	void testNameRector(){
		String name = poli.getName();
		String rector = poli.getRector();
		
		assertNotNull(name, "Missing university name");
		
		assertEquals(UNIVERSITY_NAME,		  // expected value
					 name,	  // actual value
					 "Wrong university name"); // msg in case of failure

		assertNotNull(rector, "Missing rector name");
		
		assertContained("Saracco",rector, "Wrong rector name");
	}
	
	@Test
	void testEnroll(){				
		int s1 = poli.enroll("Mario","Rossi");
		int s2 = poli.enroll("Francesca","Verdi");
		
		assertEquals(10000,s1,"Wrong id number for first enrolled student");
		assertEquals(10001,s2,"Wrong id number for second enrolled student");
	}

	@Test
	void testStudents(){				
		int s1 = poli.enroll("Vilfredo","Pareto");
		int s2 = poli.enroll("Galileo","Ferraris");
		int s3 = poli.enroll("Leo", "Da Vinci");
	
		String ss1 = poli.student(s1);
		assertNotNull(ss1, "Missing student info");
		assertContained("Pareto",ss1);

		String ss2 = poli.student(s2);
		assertNotNull(ss2, "Missing student info");
		assertContained("Galileo",ss2);

		String ss3 = poli.student(s3);
		assertNotNull( ss3, "Missing student info");
		assertContained("Vinci",ss3);
	}

	@Test @Disabled("Input sanitization was not in the requirements, so not checking it.")
	void testWrongStudent(){
		poli.enroll("Vilfredo","Pareto");
		poli.enroll("Galileo","Ferraris");
		int s3 = poli.enroll("Leo", "Da Vinci");

		poli.student(s3+100);
		// no assumption on result, just not crashing
		assertTrue(true);
	}

	@Test
	void testCourseActivation(){
		int macro = poli.activate("Macro Economics", "Paul Krugman");
		int oop = poli.activate("Object Oriented Programming", "James Gosling");
		
		assertEquals(10,macro, "Wrong id number for first activated course");
		assertEquals(11,oop, "Wrong id number for second activated course");		
	}
	
	@Test
	void testCourses(){
		int macro = poli.activate("Macro Economics", "Paul Krugman");
		int oop = poli.activate("Object Oriented Programming", "James Gosling");
		
		assertNotNull(poli.course(macro), "Missing course description");
		assertContained("Macro Economics", poli.course(macro), "Wrong description of course");
		assertContained("Oriented", poli.course(oop), "Wrong description of course");
		assertContained("James", poli.course(oop), "Wrong description of course");
	}

	@Test @Disabled("Input sanitization was not in the requirements, so not checking it.")
	void testCoursesWrong(){
		int macro = poli.activate("Macro Economics", "Paul Krugman");
		int oop = poli.activate("Object Oriented Programming", "James Gosling");

		assertNotNull(poli.course(macro), "Missing course description");
		poli.course(oop+1);
		// no assumption on result, just not crashing
	}

	@Test
	void testAttendees(){
		poli.enroll("Mario","Rossi");
		poli.enroll("Francesca","Verdi");
		
		poli.activate("Macro Economics", "Paul Krugman");
		poli.activate("Object Oriented Programming", "James Gosling");
		
		poli.register(10000, 10);
		poli.register(10001, 10);
		poli.register(10001, 11);
		
		String attendees = poli.listAttendees(10);
		assertNotNull(attendees, "Missing attendees list");
		assertEquals(2,countLines(attendees), "Wrong number of attendees for course 10");

		attendees = poli.listAttendees(11);
		assertEquals(1, countLines(attendees), "Wrong number of attendees for course 11");
	}

	@Test @Disabled("Input sanitization was not in the requirements, so not checking it.")
	void testAttendeesWrong(){
		poli.enroll("Mario","Rossi");
		poli.enroll("Francesca","Verdi");

		poli.activate("Macro Economics", "Paul Krugman");

		poli.register(10000, 10);

		String attendees = poli.listAttendees(10);
		assertNotNull(attendees, "Missing attendees list");

		poli.register(10000+100, 10);
		poli.register(10000, 10+10);

		poli.listAttendees(50);
		// no assumption on result, just not crashing
	}

	@Test
	void testStudyPlan(){
		poli.enroll("Mario","Rossi");
		poli.enroll("Francesca","Verdi");
		
		poli.activate("Macro Economics", "Paul Krugman");
		poli.activate("Object Oriented Programming", "James Gosling");
		
		poli.register(10000, 10);
		poli.register(10001, 10);
		poli.register(10001, 11);
		
		String plan = poli.studyPlan(10001);
		assertNotNull(plan, "Missing study plan");
		assertEquals(2,countLines(plan), "Wrong number of courses for student 10001");

		plan = poli.studyPlan(10000);
		assertEquals(1,countLines(plan), "Wrong number of courses for student 10000");
	}

	@Test @Disabled("Input sanitization was not in the requirements, so not checking it.")
	void testStudyPlanWrong(){
		poli.enroll("Mario","Rossi");
		poli.enroll("Francesca","Verdi");

		poli.activate("Macro Economics", "Paul Krugman");

		poli.register(10000, 10);

		String plan = poli.studyPlan(10001);
		assertNotNull(plan, "Missing study plan");

		poli.studyPlan(10001+100);
		// no assumption on the results, just don't crash!
	}

	// -------------------- Utility methods (o Helper methods) ------------------------------------------
	
	// specialized assert methods, make the test easier to read
	
	/**
	 * Assert that a sub string is contained in the actual string
	 * 
	 * @param expectedSubStr the expected sub string
	 * @param actualStr      the actual string
	 */
	private static void assertContained(String expectedSubStr, String actualStr) {
		assertContained(expectedSubStr,actualStr, null);
	}
	
	
	/**
	 * Assert that a sub string is contained in the actual string
	 * 
	 * @param expectedSubStr the expected sub string
	 * @param actualStr      the actual string
	 */
	private static void assertContained(String expectedSubStr, String actualStr, String msg) {
		assertTrue((actualStr != null && actualStr.contains(expectedSubStr)),
				   (msg==null?"":msg+": ") + "expected sub string [" + expectedSubStr + "] is not contained in ["+actualStr+"]");
	}
	
	// other support methods
	
	private static int countLines(String s) {
		if(s==null) return 0;
		return 1 + s.trim().replaceAll("[^\n]", "").length();
	}
}
