package custom;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import university.University;

class CustomUniversityBasicTest {

    @Test
    void universityNameAndRectorAreReturned() {
        University university = new University("PoliTo");
        university.setRector("Guido", "Saracco");
        assertAll(
                () -> assertEquals("PoliTo", university.getName()),
                () -> assertEquals("Guido Saracco", university.getRector()));
    }

    @Test
    void universityNameIsTrimmed() {
        assertEquals("PoliTo", new University("  PoliTo  ").getName());
    }

    @Test
    void invalidUniversityNamesAreRejected() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new University(null)),
                () -> assertThrows(IllegalArgumentException.class, () -> new University("  ")));
    }

    @Test
    void invalidRectorNamesAreRejected() {
        University university = new University("PoliTo");
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> university.setRector(null, "Valid")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> university.setRector("Valid", " ")));
    }

    @Test
    void studentIdsStartAtExpectedValue() {
        University university = new University("PoliTo");
        assertEquals(10000, university.enroll("Mario", "Rossi"));
        assertEquals(10001, university.enroll("Ada", "Lovelace"));
    }

    @Test
    void studentDescriptionPreservesFormat() {
        University university = new University("PoliTo");
        int id = university.enroll("Mario", "Rossi");
        assertEquals("10000 Mario Rossi", university.student(id));
    }

    @Test
    void invalidStudentNamesAreRejected() {
        University university = new University("PoliTo");
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> university.enroll("", "Rossi")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> university.enroll("Mario", null)));
    }

    @Test
    void missingStudentReturnsNull() {
        assertNull(new University("PoliTo").student(10000));
    }

    @Test
    void courseCodesStartAtExpectedValue() {
        University university = new University("PoliTo");
        assertEquals(10, university.activate("OOP", "James Gosling"));
        assertEquals(11, university.activate("Algorithms", "Edsger Dijkstra"));
    }

    @Test
    void courseDescriptionPreservesFormat() {
        University university = new University("PoliTo");
        int code = university.activate("OOP", "James Gosling");
        assertEquals("10,OOP,James Gosling", university.course(code));
    }

    @Test
    void invalidCourseValuesAreRejected() {
        University university = new University("PoliTo");
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> university.activate(" ", "Teacher")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> university.activate("Course", null)));
    }

    @Test
    void missingCourseReturnsNull() {
        assertNull(new University("PoliTo").course(10));
    }
}
