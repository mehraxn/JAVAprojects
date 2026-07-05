package securecicdpipeline;

/**
 * Tiny CLI entry point. The real value of this project is the pipeline and the
 * security docs; this app just gives the compile/test/build stages a concrete,
 * deterministic thing to package.
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        String version = envOr("APP_VERSION", "1.0.0");
        String gitSha = envOr("GIT_SHA", "0000000");
        BuildInfo info = new BuildInfo(version, gitSha);

        System.out.println("Secure CI/CD pipeline application");
        System.out.println("version: " + info.version());
        System.out.println("commit:  " + info.gitSha());
        System.out.println("would-build image: "
                + info.imageReference("ghcr.io", "example/secure-cicd-app"));
    }

    private static String envOr(String name, String fallback) {
        String value = System.getenv(name);
        return (value == null || value.isBlank()) ? fallback : value.trim();
    }
}
