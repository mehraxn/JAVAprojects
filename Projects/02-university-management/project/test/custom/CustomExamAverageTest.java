package custom;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import university.University;

class CustomExamAverageTest {

    @Test
    void boundaryGradesAreAccepted() {
        Fixture fixture = new Fixture();
        assertDoesNotThrow(() -> fixture.university.exam(fixture.studentId, fixture.courseCode, 0));
        assertDoesNotThrow(() -> fixture.university.exam(fixture.studentId, fixture.courseCode, 30));
    }

    @Test
    void gradeBelowZeroIsRejected() {
        Fixture fixture = new Fixture();
        assertThrows(IllegalArgumentException.class,
                () -> fixture.university.exam(fixture.studentId, fixture.courseCode, -1));
    }

    @Test
    void gradeAboveThirtyIsRejected() {
        Fixture fixture = new Fixture();
        assertThrows(IllegalArgumentException.class,
                () -> fixture.university.exam(fixture.studentId, fixture.courseCode, 31));
    }

    @Test
    void examRequiresRegistration() {
        University university = new University("PoliTo");
        int student = university.enroll("Mario", "Rossi");
        int course = university.activate("OOP", "Teacher");
        assertThrows(IllegalStateException.class, () -> university.exam(student, course, 25));
    }

    @Test
    void examRejectsUnknownStudentAndCourse() {
        Fixture fixture = new Fixture();
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> fixture.university.exam(99999, fixture.courseCode, 25)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> fixture.university.exam(fixture.studentId, 999, 25)));
    }

    @Test
    void studentAverageUsesAllDistinctCourses() {
        University university = new University("PoliTo");
        int student = university.enroll("Mario", "Rossi");
        int first = university.activate("OOP", "Teacher A");
        int second = university.activate("Algorithms", "Teacher B");
        university.register(student, first);
        university.register(student, second);
        university.exam(student, first, 24);
        university.exam(student, second, 30);
        assertEquals("Student 10000 : 27.0", university.studentAvg(student));
    }

    @Test
    void courseAverageUsesAllStudents() {
        University university = new University("PoliTo");
        int first = university.enroll("Mario", "Rossi");
        int second = university.enroll("Ada", "Lovelace");
        int course = university.activate("OOP", "Teacher");
        university.register(first, course);
        university.register(second, course);
        university.exam(first, course, 24);
        university.exam(second, course, 30);
        assertEquals("The average for the course OOP is: 27.0", university.courseAvg(course));
    }

    @Test
    void noExamMessagesArePreserved() {
        Fixture fixture = new Fixture();
        assertAll(
                () -> assertEquals("Student 10000 hasn't taken any exams",
                        fixture.university.studentAvg(fixture.studentId)),
                () -> assertEquals("No student has taken the exam in OOP",
                        fixture.university.courseAvg(fixture.courseCode)));
    }

    @Test
    void repeatedExamUpdatesExistingGrade() {
        Fixture fixture = new Fixture();
        fixture.university.exam(fixture.studentId, fixture.courseCode, 18);
        fixture.university.exam(fixture.studentId, fixture.courseCode, 30);
        assertAll(
                () -> assertEquals("Student 10000 : 30.0",
                        fixture.university.studentAvg(fixture.studentId)),
                () -> assertEquals("The average for the course OOP is: 30.0",
                        fixture.university.courseAvg(fixture.courseCode)));
    }

    @Test
    void missingAverageLookupBehaviorIsStable() {
        University university = new University("PoliTo");
        assertAll(
                () -> assertEquals("Student 99999 hasn't taken any exams", university.studentAvg(99999)),
                () -> assertNull(university.courseAvg(999)));
    }

    private static final class Fixture {
        final University university = new University("PoliTo");
        final int studentId = university.enroll("Mario", "Rossi");
        final int courseCode = university.activate("OOP", "Teacher");

        Fixture() {
            university.register(studentId, courseCode);
        }
    }
}
