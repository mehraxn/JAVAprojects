package jobapplicationtracker;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        TrackerService tracker = new TrackerService(new CsvApplicationRepository());
        JobApplication first = tracker.addApplication(
                "Northwind", "Java Developer", LocalDate.now().minusDays(10),
                JobApplication.Status.APPLIED, "Application sent through company website");
        tracker.addApplication(
                "Contoso", "Backend Engineer", LocalDate.now().minusDays(4),
                JobApplication.Status.SCREENING, "Recruiter call scheduled");
        tracker.updateStatus(first.getId(), JobApplication.Status.INTERVIEW);

        System.out.println("All applications:");
        printApplications(tracker.listApplications());
        System.out.println("\nSearch results for 'Java':");
        printApplications(tracker.searchByCompanyOrRole("Java"));
        System.out.println("\nStatus summary: " + tracker.getStatusSummary());

        if (args.length > 0) {
            demonstrateFileStorage(tracker, Paths.get(args[0]));
        } else {
            System.out.println("\nPass a CSV path to demonstrate saving and loading.");
        }
    }

    private static void demonstrateFileStorage(TrackerService tracker, Path path) {
        try {
            tracker.save(path);
            TrackerService reloaded = new TrackerService(new CsvApplicationRepository());
            reloaded.load(path);
            System.out.println("Saved and loaded " + reloaded.getTotalApplications()
                    + " applications from " + path + ".");
        } catch (IOException exception) {
            System.err.println("Could not use the CSV file: " + exception.getMessage());
        }
    }

    private static void printApplications(List<JobApplication> applications) {
        if (applications.isEmpty()) {
            System.out.println("No applications found.");
            return;
        }
        for (JobApplication application : applications) {
            System.out.println(application);
        }
    }
}
