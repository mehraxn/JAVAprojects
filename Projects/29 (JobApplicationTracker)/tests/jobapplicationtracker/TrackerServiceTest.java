package jobapplicationtracker;

import static jobapplicationtracker.TestSupport.assertEquals;
import static jobapplicationtracker.TestSupport.assertFalse;
import static jobapplicationtracker.TestSupport.assertThrows;
import static jobapplicationtracker.TestSupport.assertTrue;
import static jobapplicationtracker.TestSupport.test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

final class TrackerServiceTest {
    private static final LocalDate DATE = LocalDate.now().minusDays(3);
    private static final Path UNUSED = Paths.get("unused.csv");

    private TrackerServiceTest() {
    }

    /** In-memory repository fake so no real files are needed. */
    private static final class FakeRepository implements ApplicationRepository {
        List<JobApplication> toLoad = new ArrayList<>();
        List<JobApplication> lastSaved;
        IOException loadFailure;

        @Override
        public List<JobApplication> load(Path path) throws IOException {
            if (loadFailure != null) {
                throw loadFailure;
            }
            return new ArrayList<>(toLoad);
        }

        @Override
        public void save(Path path, List<JobApplication> applications) {
            lastSaved = new ArrayList<>(applications);
        }
    }

    private static TrackerService service() {
        return new TrackerService(new FakeRepository());
    }

    private static JobApplication application(long id, String company, String role,
            JobApplication.Status status) {
        return new JobApplication(id, company, role, DATE, status, "");
    }

    static void run() {
        test("null repository is rejected", () -> {
            assertThrows(IllegalArgumentException.class, () -> new TrackerService(null),
                    "null repository");
        });

        test("added applications receive sequential IDs starting at 1", () -> {
            TrackerService tracker = service();
            assertEquals(1L, tracker.addApplication("A", "Dev", DATE,
                    JobApplication.Status.APPLIED, "").getId(), "first ID");
            assertEquals(2L, tracker.addApplication("B", "Dev", DATE,
                    JobApplication.Status.APPLIED, "").getId(), "second ID");
            assertEquals(3L, tracker.addApplication("C", "Dev", DATE,
                    JobApplication.Status.APPLIED, "").getId(), "third ID");
        });

        test("list returns all applications in insertion order", () -> {
            TrackerService tracker = service();
            tracker.addApplication("First", "Dev", DATE, JobApplication.Status.APPLIED, "");
            tracker.addApplication("Second", "Dev", DATE, JobApplication.Status.OFFER, "");
            List<JobApplication> all = tracker.listApplications();
            assertEquals(2, all.size(), "list size");
            assertEquals("First", all.get(0).getCompany(), "first entry");
            assertEquals("Second", all.get(1).getCompany(), "second entry");
        });

        test("listed applications are defensive copies", () -> {
            TrackerService tracker = service();
            tracker.addApplication("Company", "Dev", DATE, JobApplication.Status.APPLIED, "");
            tracker.listApplications().get(0).setStatus(JobApplication.Status.REJECTED);
            assertEquals(JobApplication.Status.APPLIED,
                    tracker.listApplications().get(0).getStatus(),
                    "stored status unchanged after mutating a listed copy");
        });

        test("updateStatus changes an existing application", () -> {
            TrackerService tracker = service();
            long id = tracker.addApplication("Company", "Dev", DATE,
                    JobApplication.Status.APPLIED, "").getId();
            assertTrue(tracker.updateStatus(id, JobApplication.Status.INTERVIEW),
                    "update reported success");
            assertEquals(JobApplication.Status.INTERVIEW,
                    tracker.listApplications().get(0).getStatus(), "status stored");
        });

        test("updateStatus fails cleanly for unknown and invalid IDs", () -> {
            TrackerService tracker = service();
            assertFalse(tracker.updateStatus(42, JobApplication.Status.OFFER),
                    "unknown ID returns false");
            assertThrows(IllegalArgumentException.class,
                    () -> tracker.updateStatus(0, JobApplication.Status.OFFER),
                    "zero ID rejected");
            assertThrows(IllegalArgumentException.class,
                    () -> tracker.updateStatus(5, null), "null status rejected");
        });

        test("search matches company and role case-insensitively", () -> {
            TrackerService tracker = service();
            tracker.addApplication("Northwind", "Java Developer", DATE,
                    JobApplication.Status.APPLIED, "");
            tracker.addApplication("Contoso", "Backend Engineer", DATE,
                    JobApplication.Status.SCREENING, "");
            assertEquals(1, tracker.searchByCompanyOrRole("NORTHWIND").size(), "company match");
            assertEquals(1, tracker.searchByCompanyOrRole("java").size(), "role match");
            assertEquals(1, tracker.searchByCompanyOrRole("ENGINEER").size(),
                    "case-insensitive role match");
            assertEquals(0, tracker.searchByCompanyOrRole("nomatch").size(), "no match");
            assertThrows(IllegalArgumentException.class,
                    () -> tracker.searchByCompanyOrRole("   "), "blank search rejected");
        });

        test("filterByStatus returns only matching records", () -> {
            TrackerService tracker = service();
            tracker.addApplication("A", "Dev", DATE, JobApplication.Status.APPLIED, "");
            tracker.addApplication("B", "Dev", DATE, JobApplication.Status.APPLIED, "");
            tracker.addApplication("C", "Dev", DATE, JobApplication.Status.OFFER, "");
            assertEquals(2, tracker.filterByStatus(JobApplication.Status.APPLIED).size(),
                    "APPLIED count");
            assertEquals(0, tracker.filterByStatus(JobApplication.Status.REJECTED).size(),
                    "REJECTED count");
            assertThrows(IllegalArgumentException.class,
                    () -> tracker.filterByStatus(null), "null filter rejected");
        });

        test("status summary counts every status including zeroes", () -> {
            TrackerService tracker = service();
            tracker.addApplication("A", "Dev", DATE, JobApplication.Status.APPLIED, "");
            tracker.addApplication("B", "Dev", DATE, JobApplication.Status.APPLIED, "");
            tracker.addApplication("C", "Dev", DATE, JobApplication.Status.INTERVIEW, "");
            assertEquals(3, tracker.getTotalApplications(), "total");
            assertEquals(2, tracker.getStatusSummary().get(JobApplication.Status.APPLIED),
                    "APPLIED summary");
            assertEquals(1, tracker.getStatusSummary().get(JobApplication.Status.INTERVIEW),
                    "INTERVIEW summary");
            assertEquals(0, tracker.getStatusSummary().get(JobApplication.Status.OFFER),
                    "OFFER summary zero");
        });

        test("save passes the current records to the repository", () -> {
            FakeRepository repository = new FakeRepository();
            TrackerService tracker = new TrackerService(repository);
            tracker.addApplication("Company", "Dev", DATE, JobApplication.Status.APPLIED, "");
            tracker.save(UNUSED);
            assertEquals(1, repository.lastSaved.size(), "saved size");
            assertEquals("Company", repository.lastSaved.get(0).getCompany(), "saved company");
        });

        test("load replaces records and continues IDs after the highest loaded ID", () -> {
            FakeRepository repository = new FakeRepository();
            repository.toLoad.add(application(3, "Loaded A", "Dev",
                    JobApplication.Status.APPLIED));
            repository.toLoad.add(application(7, "Loaded B", "Dev",
                    JobApplication.Status.OFFER));
            TrackerService tracker = new TrackerService(repository);
            tracker.addApplication("Old", "Dev", DATE, JobApplication.Status.APPLIED, "");
            tracker.load(UNUSED);
            assertEquals(2, tracker.getTotalApplications(), "loaded count replaced old records");
            assertEquals(8L, tracker.addApplication("New", "Dev", DATE,
                    JobApplication.Status.APPLIED, "").getId(), "next ID after load");
        });

        test("failed load keeps the current in-memory records", () -> {
            FakeRepository repository = new FakeRepository();
            repository.loadFailure = new IOException("simulated read failure");
            TrackerService tracker = new TrackerService(repository);
            // Bypass the failure for setup by adding directly.
            repository.loadFailure = null;
            tracker.addApplication("Kept", "Dev", DATE, JobApplication.Status.APPLIED, "");
            repository.loadFailure = new IOException("simulated read failure");
            assertThrows(IOException.class, () -> tracker.load(UNUSED), "load fails");
            assertEquals(1, tracker.getTotalApplications(), "records kept after failed load");
            assertEquals("Kept", tracker.listApplications().get(0).getCompany(),
                    "kept record intact");
        });

        test("duplicate IDs from the repository are rejected without replacing records", () -> {
            FakeRepository repository = new FakeRepository();
            repository.toLoad.add(application(5, "First", "Dev", JobApplication.Status.APPLIED));
            repository.toLoad.add(application(5, "Second", "Dev", JobApplication.Status.OFFER));
            TrackerService tracker = new TrackerService(repository);
            tracker.addApplication("Kept", "Dev", DATE, JobApplication.Status.APPLIED, "");
            assertThrows(IOException.class, () -> tracker.load(UNUSED), "duplicate IDs rejected");
            assertEquals(1, tracker.getTotalApplications(), "records kept after duplicate IDs");
        });
    }
}
