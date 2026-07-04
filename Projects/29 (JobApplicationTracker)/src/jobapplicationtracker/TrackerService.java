package jobapplicationtracker;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TrackerService {
    private final ApplicationRepository repository;
    private final Map<Long, JobApplication> applications = new LinkedHashMap<Long, JobApplication>();
    private long nextId = 1;

    public TrackerService(ApplicationRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("Application repository cannot be null.");
        }
        this.repository = repository;
    }

    public JobApplication addApplication(String company, String role, LocalDate applicationDate,
            JobApplication.Status status, String notes) {
        JobApplication application = new JobApplication(
                nextId, company, role, applicationDate, status, notes);
        applications.put(nextId, application);
        nextId++;
        return application;
    }

    public boolean updateStatus(long applicationId, JobApplication.Status status) {
        requireId(applicationId);
        JobApplication application = applications.get(applicationId);
        if (application == null) {
            return false;
        }
        application.setStatus(status);
        return true;
    }

    public List<JobApplication> listApplications() {
        return Collections.unmodifiableList(new ArrayList<JobApplication>(applications.values()));
    }

    public List<JobApplication> searchByCompanyOrRole(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            throw new IllegalArgumentException("Search text cannot be empty.");
        }
        String query = searchText.trim().toLowerCase(Locale.ROOT);
        List<JobApplication> matches = new ArrayList<JobApplication>();
        for (JobApplication application : applications.values()) {
            if (application.getCompany().toLowerCase(Locale.ROOT).contains(query)
                    || application.getRole().toLowerCase(Locale.ROOT).contains(query)) {
                matches.add(application);
            }
        }
        return Collections.unmodifiableList(matches);
    }

    public List<JobApplication> filterByStatus(JobApplication.Status status) {
        if (status == null) {
            throw new IllegalArgumentException("Application status cannot be null.");
        }
        List<JobApplication> matches = new ArrayList<JobApplication>();
        for (JobApplication application : applications.values()) {
            if (application.getStatus() == status) {
                matches.add(application);
            }
        }
        return Collections.unmodifiableList(matches);
    }

    public int getTotalApplications() {
        return applications.size();
    }

    public Map<JobApplication.Status, Integer> getStatusSummary() {
        Map<JobApplication.Status, Integer> summary =
                new LinkedHashMap<JobApplication.Status, Integer>();
        for (JobApplication.Status status : JobApplication.Status.values()) {
            summary.put(status, 0);
        }
        for (JobApplication application : applications.values()) {
            JobApplication.Status status = application.getStatus();
            summary.put(status, summary.get(status) + 1);
        }
        return Collections.unmodifiableMap(summary);
    }

    public void save(Path path) throws IOException {
        repository.save(path, listApplications());
    }

    public void load(Path path) throws IOException {
        List<JobApplication> loaded = repository.load(path);
        Map<Long, JobApplication> checked = new LinkedHashMap<Long, JobApplication>();
        long newNextId = 1;
        for (JobApplication application : loaded) {
            if (checked.put(application.getId(), application) != null) {
                throw new IOException("Duplicate application ID: " + application.getId());
            }
            if (application.getId() >= newNextId) {
                newNextId = application.getId() + 1;
            }
        }
        applications.clear();
        applications.putAll(checked);
        nextId = newNextId;
    }

    private void requireId(long applicationId) {
        if (applicationId <= 0) {
            throw new IllegalArgumentException("Application ID must be positive.");
        }
    }
}
