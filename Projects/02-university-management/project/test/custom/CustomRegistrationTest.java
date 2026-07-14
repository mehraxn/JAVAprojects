package custom;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import university.University;

class CustomRegistrationTest {

    @Test
    void registrationUpdatesAttendeesAndStudyPlan() {
        Fixture fixture = new Fixture();
        fixture.university.register(fixture.studentId, fixture.courseCode);
        assertAll(
                () -> assertEquals("10000 Mario Rossi",
                        fixture.university.listAttendees(fixture.courseCode)),
                () -> assertEquals("10,OOP,James Gosling",
                        fixture.university.studyPlan(fixture.studentId)));
    }

    @Test
    void duplicateRegistrationIsIdempotent() {
        Fixture fixture = new Fixture();
        fixture.university.register(fixture.studentId, fixture.courseCode);
        fixture.university.register(fixture.studentId, fixture.courseCode);
        assertAll(
                () -> assertEquals(1, fixture.university.listAttendees(fixture.courseCode).lines().count()),
                () -> assertEquals(1, fixture.university.studyPlan(fixture.studentId).lines().count()));
    }

    @Test
    void unknownStudentRegistrationIsRejected() {
        Fixture fixture = new Fixture();
        assertThrows(IllegalArgumentException.class,
                () -> fixture.university.register(99999, fixture.courseCode));
    }

    @Test
    void unknownCourseRegistrationIsRejected() {
        Fixture fixture = new Fixture();
        assertThrows(IllegalArgumentException.class,
                () -> fixture.university.register(fixture.studentId, 999));
    }

    @Test
    void missingAttendeesAndStudyPlanAreEmpty() {
        University university = new University("PoliTo");
        assertAll(
                () -> assertEquals("", university.listAttendees(999)),
                () -> assertEquals("", university.studyPlan(99999)));
    }

    @Test
    void studentCourseCapacityHasClearFailure() {
        University university = new University("PoliTo");
        int studentId = university.enroll("Mario", "Rossi");
        for (int i = 0; i < 25; i++) {
            int code = university.activate("Course " + i, "Teacher " + i);
            university.register(studentId, code);
        }
        int extra = university.activate("Extra", "Teacher");
        assertThrows(IllegalStateException.class, () -> university.register(studentId, extra));
    }

    private static final class Fixture {
        final University university = new University("PoliTo");
        final int studentId = university.enroll("Mario", "Rossi");
        final int courseCode = university.activate("OOP", "James Gosling");
    }
}
