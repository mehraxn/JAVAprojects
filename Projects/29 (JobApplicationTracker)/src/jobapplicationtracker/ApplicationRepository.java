package jobapplicationtracker;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface ApplicationRepository {
    List<JobApplication> load(Path path) throws IOException;

    void save(Path path, List<JobApplication> applications) throws IOException;
}
