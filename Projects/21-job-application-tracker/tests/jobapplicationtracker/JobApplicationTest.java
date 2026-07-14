package jobapplicationtracker;

import static jobapplicationtracker.TestSupport.assertEquals;
import static jobapplicationtracker.TestSupport.assertThrows;
import static jobapplicationtracker.TestSupport.assertTrue;
import static jobapplicationtracker.TestSupport.test;

import java.time.LocalDate;

final class JobApplicationTest {
    private static final LocalDate DATE = LocalDate.now().minusDays(7);

    private JobApplicationTest() {
    }

    private static JobApplication sample() {
        return new JobApplication(1, "Northwind", "Java Developer", DATE,
                JobApplication.Status.APPLIED, "Sent online");
    }

    static void run() {
        test("valid application exposes all values through getters", () -> {
            JobApplication application = sample();
            assertEquals(1L, application.getId(), "ID");
            assertEquals("Northwind", application.getCompany(), "Company");
            assertEquals("Java Developer", application.getRole(), "Role");
            assertEquals(DATE, application.getApplicationDate(), "Date");
            assertEquals(JobApplication.Status.APPLIED, application.getStatus(), "Status");
            assertEquals("Sent online", application.getNotes(), "Notes");
        });

        test("company and role are trimmed", () -> {
            JobApplication application = new JobApplication(2, "  Contoso  ", "  Tester  ",
                    DATE, JobApplication.Status.SCREENING, null);
            assertEquals("Contoso", application.getCompany(), "Trimmed company");
            assertEquals("Tester", application.getRole(), "Trimmed role");
        });

        test("blank company is rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new JobApplication(1, null, "Role", DATE,
                            JobApplication.Status.APPLIED, ""),
                    "null company");
            assertThrows(IllegalArgumentException.class,
                    () -> new JobApplication(1, "   ", "Role", DATE,
                            JobApplication.Status.APPLIED, ""),
                    "whitespace company");
        });

        test("blank role is rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new JobApplication(1, "Company", null, DATE,
                            JobApplication.Status.APPLIED, ""),
                    "null role");
            assertThrows(IllegalArgumentException.class,
                    () -> new JobApplication(1, "Company", "", DATE,
                            JobApplication.Status.APPLIED, ""),
                    "empty role");
        });

        test("null status is rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new JobApplication(1, "Company", "Role", DATE, null, ""),
                    "null status");
        });

        test("null and future application dates are rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new JobApplication(1, "Company", "Role", null,
                            JobApplication.Status.APPLIED, ""),
                    "null date");
            assertThrows(IllegalArgumentException.class,
                    () -> new JobApplication(1, "Company", "Role", LocalDate.now().plusDays(1),
                            JobApplication.Status.APPLIED, ""),
                    "future date");
        });

        test("today is a valid application date", () -> {
            JobApplication application = new JobApplication(1, "Company", "Role",
                    LocalDate.now(), JobApplication.Status.APPLIED, "");
            assertEquals(LocalDate.now(), application.getApplicationDate(), "Today accepted");
        });

        test("non-positive IDs are rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new JobApplication(0, "Company", "Role", DATE,
                            JobApplication.Status.APPLIED, ""),
                    "zero ID");
            assertThrows(IllegalArgumentException.class,
                    () -> new JobApplication(-5, "Company", "Role", DATE,
                            JobApplication.Status.APPLIED, ""),
                    "negative ID");
        });

        test("null notes become empty and notes are trimmed", () -> {
            assertEquals("", new JobApplication(1, "Company", "Role", DATE,
                    JobApplication.Status.APPLIED, null).getNotes(), "null notes");
            assertEquals("Trimmed", new JobApplication(1, "Company", "Role", DATE,
                    JobApplication.Status.APPLIED, "  Trimmed  ").getNotes(), "trimmed notes");
        });

        test("line breaks in text fields are rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new JobApplication(1, "Compa\nny", "Role", DATE,
                            JobApplication.Status.APPLIED, ""),
                    "newline in company");
            assertThrows(IllegalArgumentException.class,
                    () -> sample().setNotes("line one\nline two"),
                    "newline in notes");
        });

        test("status update changes state and rejects null", () -> {
            JobApplication application = sample();
            application.setStatus(JobApplication.Status.INTERVIEW);
            assertEquals(JobApplication.Status.INTERVIEW, application.getStatus(), "Updated status");
            assertThrows(IllegalArgumentException.class,
                    () -> application.setStatus(null), "null status update");
            assertEquals(JobApplication.Status.INTERVIEW, application.getStatus(),
                    "Status unchanged after rejected update");
        });

        test("notes update changes state and keeps object valid", () -> {
            JobApplication application = sample();
            application.setNotes("Recruiter follow-up on Friday");
            assertEquals("Recruiter follow-up on Friday", application.getNotes(), "Updated notes");
            assertEquals("Northwind", application.getCompany(), "Company untouched");
        });

        test("every status enum value can be used", () -> {
            for (JobApplication.Status status : JobApplication.Status.values()) {
                JobApplication application = new JobApplication(1, "Company", "Role", DATE,
                        status, "");
                assertEquals(status, application.getStatus(), "Status " + status);
            }
        });

        test("copy is an independent object with the same values", () -> {
            JobApplication original = sample();
            JobApplication copy = original.copy();
            assertEquals(original.toString(), copy.toString(), "Copy values");
            copy.setStatus(JobApplication.Status.REJECTED);
            assertEquals(JobApplication.Status.APPLIED, original.getStatus(),
                    "Original unchanged after copy mutation");
        });

        test("toString contains the key fields", () -> {
            String text = sample().toString();
            assertTrue(text.contains("Northwind"), "toString company");
            assertTrue(text.contains("Java Developer"), "toString role");
            assertTrue(text.contains("APPLIED"), "toString status");
        });
    }
}
