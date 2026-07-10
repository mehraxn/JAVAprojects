package securecicdpipeline;

/**
 * Small, pure, testable logic so the pipeline's compile and test stages have
 * something real to exercise. It validates a semantic version and derives the
 * traceable image reference the pipeline would build, scan, and sign.
 *
 * Nothing here talks to a registry or runs a build; it only computes strings.
 */
public final class BuildInfo {

    private final String version;
    private final String gitSha;

    public BuildInfo(String version, String gitSha) {
        this.version = requireSemver(version);
        this.gitSha = requireSha(gitSha);
    }

    /** e.g. "1.4.0" */
    public String version() {
        return version;
    }

    /** Short git sha, 7-40 hex chars. */
    public String gitSha() {
        return gitSha;
    }

    /**
     * The tag a CI build would push. Combining version and sha keeps the tag
     * traceable back to an exact commit — important for a signed, auditable
     * supply chain. NOTE: this only builds the string; it pushes nothing.
     */
    public String imageReference(String registry, String repository) {
        if (registry == null || registry.isBlank()) {
            throw new IllegalArgumentException("Registry is required.");
        }
        if (repository == null || repository.isBlank()) {
            throw new IllegalArgumentException("Repository is required.");
        }
        return registry + "/" + repository + ":" + version + "-" + gitSha;
    }

    private static String requireSemver(String value) {
        if (value == null || !value.matches("\\d+\\.\\d+\\.\\d+")) {
            throw new IllegalArgumentException(
                    "Version must be semantic (MAJOR.MINOR.PATCH), got: " + value);
        }
        return value;
    }

    private static String requireSha(String value) {
        if (value == null || !value.matches("[0-9a-f]{7,40}")) {
            throw new IllegalArgumentException(
                    "Git sha must be 7-40 lowercase hex chars, got: " + value);
        }
        return value;
    }
}
