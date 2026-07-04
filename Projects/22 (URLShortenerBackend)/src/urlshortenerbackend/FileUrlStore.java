package urlshortenerbackend;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class FileUrlStore {
    public Map<String, UrlEntry> load(Path path) throws IOException {
        // TODO: Load validated URL mappings from a standard-Java text format.
        throw new UnsupportedOperationException("TODO: load URL mappings");
    }

    public void save(Path path, Map<String, UrlEntry> entries) throws IOException {
        // TODO: Persist URL mappings and hit counts atomically where practical.
        throw new UnsupportedOperationException("TODO: save URL mappings");
    }
}
