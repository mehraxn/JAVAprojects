package custom;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import university.University;

class CustomTopThreeTest {

    @Test
    void noEligibleStudentsProducesEmptyRanking() {
        assertEquals("", new University("PoliTo").topThreeStudents());
    }

    @Test
    void fewerThanThreeEligibleStudentsAreReturned() {
        University university = new University("PoliTo");
        int student = university.enroll("Mario", "Rossi");
        int course = university.activate("OOP", "Teacher");
        university.register(student, course);
        university.exam(student, course, 27);
        assertEquals("Mario Rossi : 37.0", university.topThreeStudents());
    }

    @Test
    void rankingIsSortedByComputedScoreAndLimitedToThree() {
        University university = new University("PoliTo");
        int course = university.activate("OOP", "Teacher");
        int[] grades = { 20, 30, 25, 28 };
        String[] names = { "A", "B", "C", "D" };
        for (int i = 0; i < grades.length; i++) {
            int student = university.enroll(names[i], "Student" + names[i]);
            university.register(student, course);
            university.exam(student, course, grades[i]);
        }
        String[] ranking = university.topThreeStudents().split("\n");
        assertAll(
                () -> assertEquals(3, ranking.length),
                () -> assertTrue(ranking[0].startsWith("B ")),
                () -> assertTrue(ranking[1].startsWith("D ")),
                () -> assertTrue(ranking[2].startsWith("C ")));
    }

    @Test
    void tiesUseDeterministicNameOrdering() {
        University university = new University("PoliTo");
        int course = university.activate("OOP", "Teacher");
        int zeta = university.enroll("Zoe", "Zulu");
        int alpha = university.enroll("Amy", "Alpha");
        university.register(zeta, course);
        university.register(alpha, course);
        university.exam(zeta, course, 25);
        university.exam(alpha, course, 25);
        assertTrue(university.topThreeStudents().startsWith("Amy Alpha"));
    }

    @Test
    void duplicateRegistrationDoesNotInflateBonus() {
        University university = new University("PoliTo");
        int student = university.enroll("Mario", "Rossi");
        int course = university.activate("OOP", "Teacher");
        university.register(student, course);
        university.register(student, course);
        university.exam(student, course, 25);
        assertEquals("Mario Rossi : 35.0", university.topThreeStudents());
    }
}
