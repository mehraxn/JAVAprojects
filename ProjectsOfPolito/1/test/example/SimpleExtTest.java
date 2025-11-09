package example;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.logging.Level;

import university.University;

/**
 * Test for extended requirements
 */
class SimpleExtTest {	
	@Test
	void testR5() {
		University poli = new University("PoliTo");
		int s1 = poli.enroll("Mario","Rossi");
		int s2 = poli.enroll("Giuseppe","Verdi");
		int macro = poli.activate("Macro Economics", "Paul Krugman");
		int oop = poli.activate("Object Oriented Programming", "James Gosling");
		poli.register(s1, macro);
		poli.register(s2, macro);
		poli.register(s2, oop);

		poli.exam(s1, macro, 27);
		poli.exam(s2, macro, 30);
		poli.exam(s2, oop, 28);
		
		System.out.println(poli.studentAvg(s2)); // 29.0
		
		System.out.println(poli.courseAvg(macro)); // 28.5
        
		assertEquals("Student 10001 : 29.0", poli.studentAvg(s2), "Wrong student average");		
	}

    @Test
    void testR6() {
        University poli = new University("PoliTo");
        int s1 = poli.enroll("Mario","Rossi");
        int s2 = poli.enroll("Giuseppe","Verdi");
        int macro = poli.activate("Macro Economics", "Paul Krugman");
        int oop = poli.activate("Object Oriented Programming", "James Gosling");
        poli.register(s1, macro);
        poli.register(s2, macro);
        poli.register(s2, oop);

        poli.exam(s1, macro, 27);
        poli.exam(s2, macro, 30);
        poli.exam(s2, oop, 28);

        System.out.println("Best students:\n" + poli.topThreeStudents());

        String best = poli.topThreeStudents();

        assertNotNull(best, "Missing top students");
        assertTrue(best.contains("Verdi : 39.0"), "Missing best student Verdi");
    }

    private String lastMessage;
    private int logCount;
    @Test
    void testR7() {
        University.logger.setFilter( r -> {
            lastMessage = r.getMessage();
            logCount++;
            return true;
        });
        lastMessage = "";
        logCount = 0;
        University.logger.setLevel(Level.ALL);

        University poli = new University("PoliTo");
        poli.enroll("Mario","Rossi");

        assertEquals(1, logCount, "No log message was generated for new enrollment");
        assertTrue(lastMessage.contains("Rossi"), "Message does not include student name");
        
        poli.activate("Macro Economics", "Paul Krugman");

        assertEquals(2, logCount, "No log message was generated for new course activation");
        assertTrue(lastMessage.contains("Economics"), "Message does not include course title");

        University.logger.setFilter(null);
    }
}

