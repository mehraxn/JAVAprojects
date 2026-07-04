package urlshortenerbackend;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ShortenerService {
    private final Map<String, UrlEntry> entries = new LinkedHashMap<>();
    private final CodeGenerator codeGenerator;

    public ShortenerService(CodeGenerator codeGenerator) {
        this.codeGenerator = codeGenerator;
    }

    public UrlEntry shorten(String originalUrl) {
        // TODO: Validate the URL, generate a unique code, and store the entry.
        throw new UnsupportedOperationException("TODO: shorten a URL");
    }

    public String resolve(String shortCode) {
        // TODO: Resolve the code and increment its hit counter.
        throw new UnsupportedOperationException("TODO: resolve a short code");
    }

    public List<UrlEntry> listEntries() {
        // TODO: Return a stable, read-only snapshot.
        throw new UnsupportedOperationException("TODO: list URL entries");
    }
}
