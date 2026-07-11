package jobapplicationtracker;

import static jobapplicationtracker.TestSupport.assertEquals;
import static jobapplicationtracker.TestSupport.assertThrows;
import static jobapplicationtracker.TestSupport.assertTrue;
import static jobapplicationtracker.TestSupport.test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class CsvApplicationRepositoryTest {
    private static final String HEADER = "id,company,role,applicationDate,status,notes";
    private static final LocalDate DATE = LocalDate.now().minusDays(5);

    private CsvApplicationRepositoryTest() {
    }

    interface TempFileTest {
        void run(Path file) throws Exception;
    }

    /** Runs a test body with a temp CSV file that is always deleted afterwards. */
    private static void withTempFile(TempFileTest body) throws Exception {
        Path file = Files.createTempFile("tracker-test", ".csv");
        try {
            body.run(file);
        } finally {
            Files.deleteIfExists(file);
        }
    }

    private static JobApplication application(long id, String company, String role,
            String notes) {
        return new JobApplication(id, company, role, DATE,
                JobApplication.Status.APPLIED, notes);
    }

    private static void writeLines(Path file, String... lines) throws IOException {
        Files.write(file, Arrays.asList(lines), StandardCharsets.UTF_8);
    }

    static void run() {
        CsvApplicationRepository repository = new CsvApplicationRepository();

        test("save then load preserves all records and fields", () -> withTempFile(file -> {
            List<JobApplication> original = new ArrayList<>();
            original.add(application(1, "Northwind", "Java Developer", "Sent online"));
            original.add(new JobApplication(2, "Contoso", "Tester", DATE,
                    JobApplication.Status.OFFER, ""));
            repository.save(file, original);
            List<JobApplication> loaded = repository.load(file);
            assertEquals(2, loaded.size(), "loaded count");
            assertEquals("Northwind", loaded.get(0).getCompany(), "company");
            assertEquals("Java Developer", loaded.get(0).getRole(), "role");
            assertEquals(DATE, loaded.get(0).getApplicationDate(), "date");
            assertEquals(JobApplication.Status.OFFER, loaded.get(1).getStatus(), "status");
            assertEquals("Sent online", loaded.get(0).getNotes(), "notes");
        }));

        test("commas inside fields are quoted and round-trip", () -> withTempFile(file -> {
            repository.save(file, List.of(
                    application(1, "Acme, Inc.", "Developer, Backend", "Call Monday, then email")));
            JobApplication loaded = repository.load(file).get(0);
            assertEquals("Acme, Inc.", loaded.getCompany(), "company with comma");
            assertEquals("Developer, Backend", loaded.getRole(), "role with comma");
            assertEquals("Call Monday, then email", loaded.getNotes(), "notes with comma");
        }));

        test("double quotes inside fields are escaped and round-trip", () -> withTempFile(file -> {
            repository.save(file, List.of(
                    application(1, "The \"Best\" Company", "Dev", "Said \"call us\"")));
            JobApplication loaded = repository.load(file).get(0);
            assertEquals("The \"Best\" Company", loaded.getCompany(), "company with quotes");
            assertEquals("Said \"call us\"", loaded.getNotes(), "notes with quotes");
        }));

        test("saving an empty list then loading returns an empty list", () -> withTempFile(file -> {
            repository.save(file, new ArrayList<>());
            assertEquals(0, repository.load(file).size(), "empty repository round-trip");
        }));

        test("missing file loads as an empty list", () -> {
            Path missing = Files.createTempFile("tracker-missing", ".csv");
            Files.delete(missing);
            assertEquals(0, repository.load(missing).size(), "missing file");
        });

        test("empty, whitespace-only, and header-only files load as empty", () -> withTempFile(file -> {
            assertEquals(0, repository.load(file).size(), "zero-byte file");
            writeLines(file, "   ", "");
            assertEquals(0, repository.load(file).size(), "whitespace-only file");
            writeLines(file, HEADER);
            assertEquals(0, repository.load(file).size(), "header-only file");
        }));

        test("blank lines between records are ignored", () -> withTempFile(file -> {
            writeLines(file, HEADER,
                    "1,CompanyA,Dev," + DATE + ",APPLIED,",
                    "",
                    "2,CompanyB,Dev," + DATE + ",OFFER,notes");
            assertEquals(2, repository.load(file).size(), "records around blank line");
        }));

        test("unexpected header is rejected", () -> withTempFile(file -> {
            writeLines(file, "id,name,role,date,status,notes",
                    "1,Company,Dev," + DATE + ",APPLIED,");
            assertThrows(IOException.class, () -> repository.load(file), "wrong header");
        }));

        test("wrong field count fails cleanly with the line number", () -> withTempFile(file -> {
            writeLines(file, HEADER, "1,Company,Dev," + DATE + ",APPLIED");
            try {
                repository.load(file);
                throw new AssertionError("expected IOException for 5 fields");
            } catch (IOException expected) {
                assertTrue(expected.getMessage().contains("line 2")
                        || expected.getMessage().contains("Line 2"),
                        "message names the line: " + expected.getMessage());
            }
        }));

        test("invalid ID, date, and status fail cleanly", () -> withTempFile(file -> {
            writeLines(file, HEADER, "abc,Company,Dev," + DATE + ",APPLIED,");
            assertThrows(IOException.class, () -> repository.load(file), "non-numeric ID");
            writeLines(file, HEADER, "1,Company,Dev,2026-02-30,APPLIED,");
            assertThrows(IOException.class, () -> repository.load(file), "impossible date");
            writeLines(file, HEADER, "1,Company,Dev," + DATE + ",HIRED,");
            assertThrows(IOException.class, () -> repository.load(file), "unknown status");
            writeLines(file, HEADER, "0,Company,Dev," + DATE + ",APPLIED,");
            assertThrows(IOException.class, () -> repository.load(file), "zero ID");
        }));

        test("duplicate IDs in the file are rejected on load", () -> withTempFile(file -> {
            writeLines(file, HEADER,
                    "1,CompanyA,Dev," + DATE + ",APPLIED,",
                    "1,CompanyB,Dev," + DATE + ",OFFER,");
            assertThrows(IOException.class, () -> repository.load(file), "duplicate ID");
        }));

        test("malformed quoting is rejected on load", () -> withTempFile(file -> {
            writeLines(file, HEADER, "1,\"Unclosed,Dev," + DATE + ",APPLIED,");
            assertThrows(IOException.class, () -> repository.load(file), "unclosed quote");
            writeLines(file, HEADER, "1,\"Company\"X,Dev," + DATE + ",APPLIED,");
            assertThrows(IOException.class, () -> repository.load(file), "text after quote");
        }));

        test("saving duplicate IDs is rejected before writing", () -> withTempFile(file -> {
            List<JobApplication> duplicates = List.of(
                    application(1, "A", "Dev", ""), application(1, "B", "Dev", ""));
            assertThrows(IllegalArgumentException.class,
                    () -> repository.save(file, duplicates), "duplicate save");
        }));

        test("null path and null list are rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> repository.load(null), "null load path");
            assertThrows(IllegalArgumentException.class,
                    () -> repository.save(null, new ArrayList<>()), "null save path");
            assertThrows(IllegalArgumentException.class, () -> withTempFile(
                    file -> repository.save(file, null)), "null list");
        });

        test("saved file uses the documented header", () -> withTempFile(file -> {
            repository.save(file, List.of(application(1, "Company", "Dev", "")));
            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            assertEquals(HEADER, lines.get(0), "header line");
            assertEquals(2, lines.size(), "one record line");
        }));
    }
}
