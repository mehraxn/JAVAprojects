package authenticationsystem;

import java.io.PrintStream;
import java.security.GeneralSecurityException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Locale;

/**
 * Command-line entry point. Main only parses arguments, drives AuthService,
 * and prints results - all hashing, session, and authorization logic lives in
 * the service classes. The {@link #run(String[], PrintStream, PrintStream)}
 * method contains the whole CLI so tests can call it directly; only
 * {@link #main(String[])} calls System.exit.
 *
 * All passwords used here are throwaway local demo values, printed in the
 * docs as examples. No real credential appears anywhere in this project.
 */
public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        System.exit(run(args, System.out, System.err));
    }

    /** Runs one CLI command; returns 0 on success and 1 on any user error. */
    public static int run(String[] args, PrintStream out, PrintStream err) {
        if (args.length == 0) {
            err.println("No command given.");
            printUsage(err);
            return 1;
        }
        String command = args[0].toLowerCase(Locale.ROOT);
        try {
            switch (command) {
                case "help":
                    printUsage(out);
                    return 0;
                case "demo":
                    return runDemo(out);
                case "register-demo":
                    return runRegisterDemo(out);
                case "login-demo":
                    return runLoginDemo(out);
                case "authorization-demo":
                    return runAuthorizationDemo(out);
                case "expiry-demo":
                    return runExpiryDemo(out);
                default:
                    err.println("Unknown command: " + args[0]);
                    printUsage(err);
                    return 1;
            }
        } catch (GeneralSecurityException exception) {
            err.println("Required password-hashing algorithm is unavailable: "
                    + exception.getMessage());
            return 1;
        }
    }

    private static int runDemo(PrintStream out) throws GeneralSecurityException {
        out.println("Full demo (in-memory, local only; passwords are demo values):");
        AuthService service = new AuthService(new PasswordHasher());

        PublicUser user = service.registerUser("learner", "LearnJava10!".toCharArray());
        PublicUser admin = service.seedAdminForDemo("demo-admin", "AdminStudy2!".toCharArray());
        out.println("  registered " + user + " (public registration is always USER)");
        out.println("  seeded     " + admin + " (demo-only admin seeding)");

        SessionInfo userSession = service.login("learner", "LearnJava10!".toCharArray());
        SessionInfo adminSession = service.login("demo-admin", "AdminStudy2!".toCharArray());
        out.println("  user logged in:  " + userSession);
        out.println("  admin logged in: " + adminSession);

        out.println();
        out.println("  USER action with user token:  "
                + service.performUserAction(userSession.getToken()));
        out.println("  ADMIN check with user token:  "
                + service.canAccess(userSession.getToken(), User.Role.ADMIN)
                + "  <- USER cannot access ADMIN actions");
        out.println("  ADMIN action with admin token: "
                + service.performAdminAction(adminSession.getToken()));

        out.println();
        service.logout(userSession.getToken());
        out.println("  user logged out; token still authenticates: "
                + (service.authenticate(userSession.getToken()) != null));
        out.println("  active sessions remaining: " + service.getActiveSessionCount());
        out.println();
        out.println("Demo completed successfully. Only masked tokens were printed;");
        out.println("no password, hash, or salt appears in any output.");
        return 0;
    }

    private static int runRegisterDemo(PrintStream out) throws GeneralSecurityException {
        out.println("Registration demo:");
        AuthService service = new AuthService(new PasswordHasher());

        out.println("  valid registration: "
                + service.registerUser("learner", "LearnJava10!".toCharArray()));
        try {
            service.registerUser("LEARNER", "OtherPass99?".toCharArray());
        } catch (IllegalArgumentException exception) {
            out.println("  duplicate username (case-insensitive) rejected: "
                    + exception.getMessage());
        }
        try {
            service.registerUser("second-user", "weak".toCharArray());
        } catch (IllegalArgumentException exception) {
            out.println("  weak password rejected: " + exception.getMessage());
        }
        out.println("Registration demo completed successfully.");
        return 0;
    }

    private static int runLoginDemo(PrintStream out) throws GeneralSecurityException {
        out.println("Login demo:");
        AuthService service = new AuthService(new PasswordHasher());
        service.registerUser("learner", "LearnJava10!".toCharArray());

        SessionInfo session = service.login("learner", "LearnJava10!".toCharArray());
        out.println("  correct password: " + session);
        out.println("  wrong password:   "
                + service.login("learner", "WrongGuess3?".toCharArray())
                + "  <- null means rejected");
        out.println("  unknown username: "
                + service.login("nobody", "WrongGuess3?".toCharArray())
                + "  <- still runs one dummy hash so timing stays similar");
        out.println("Login demo completed successfully.");
        return 0;
    }

    private static int runAuthorizationDemo(PrintStream out) throws GeneralSecurityException {
        out.println("Authorization demo:");
        AuthService service = new AuthService(new PasswordHasher());
        service.registerUser("learner", "LearnJava10!".toCharArray());
        service.seedAdminForDemo("demo-admin", "AdminStudy2!".toCharArray());
        SessionInfo userSession = service.login("learner", "LearnJava10!".toCharArray());
        SessionInfo adminSession = service.login("demo-admin", "AdminStudy2!".toCharArray());

        out.println("  USER token -> USER action:  "
                + service.canAccess(userSession.getToken(), User.Role.USER));
        out.println("  USER token -> ADMIN action: "
                + service.canAccess(userSession.getToken(), User.Role.ADMIN));
        out.println("  ADMIN token -> USER action:  "
                + service.canAccess(adminSession.getToken(), User.Role.USER));
        out.println("  ADMIN token -> ADMIN action: "
                + service.canAccess(adminSession.getToken(), User.Role.ADMIN));
        out.println("  made-up token -> USER action: "
                + service.canAccess("not-a-real-token", User.Role.USER));
        out.println("Authorization demo completed successfully.");
        return 0;
    }

    private static int runExpiryDemo(PrintStream out) throws GeneralSecurityException {
        out.println("Expiry demo (uses an injected Clock - no waiting, no sleeps):");
        SteppingClock clock = new SteppingClock(Instant.parse("2026-01-01T10:00:00Z"));
        AuthService service = new AuthService(new PasswordHasher(),
                Duration.ofMinutes(30), clock);
        service.registerUser("learner", "LearnJava10!".toCharArray());
        SessionInfo session = service.login("learner", "LearnJava10!".toCharArray());

        out.println("  session issued at 10:00, expires at " + session.getExpiresAt());
        out.println("  at 10:00 token authenticates: "
                + (service.authenticate(session.getToken()) != null));
        clock.advance(Duration.ofMinutes(29));
        out.println("  at 10:29 token authenticates: "
                + (service.authenticate(session.getToken()) != null));
        clock.advance(Duration.ofMinutes(2));
        out.println("  at 10:31 token authenticates: "
                + (service.authenticate(session.getToken()) != null)
                + "  <- expired sessions are rejected and removed");
        out.println("  active sessions now: " + service.getActiveSessionCount());
        out.println("Expiry demo completed successfully.");
        return 0;
    }

    private static void printUsage(PrintStream stream) {
        stream.println("Authentication System (educational, local only) - commands:");
        stream.println("  help                  Show this help");
        stream.println("  demo                  Register, seed demo admin, login, authorize, logout");
        stream.println("  register-demo         Valid, duplicate, and weak-password registration");
        stream.println("  login-demo            Correct password, wrong password, unknown username");
        stream.println("  authorization-demo    USER/ADMIN role checks and invalid tokens");
        stream.println("  expiry-demo           Session expiry via an injected Clock (no waiting)");
        stream.println();
        stream.println("All accounts, passwords, and sessions are in-memory demo values.");
    }

    /** Manually advanced clock so the expiry demo needs no real waiting. */
    private static final class SteppingClock extends Clock {
        private Instant current;

        SteppingClock(Instant start) {
            this.current = start;
        }

        void advance(Duration duration) {
            current = current.plus(duration);
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return current;
        }
    }
}
