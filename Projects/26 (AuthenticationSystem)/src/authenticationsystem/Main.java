package authenticationsystem;

import java.security.GeneralSecurityException;

public class Main {
    public static void main(String[] args) {
        AuthService authService = new AuthService(new PasswordHasher());
        try {
            authService.register("learner", "Learning1!".toCharArray(), User.Role.USER);
            authService.register("administrator", "AdminStudy2!".toCharArray(), User.Role.ADMIN);

            Session userSession = authService.login("learner", "Learning1!".toCharArray());
            Session adminSession = authService.login("administrator", "AdminStudy2!".toCharArray());
            if (userSession != null) {
                System.out.println(authService.performUserAction(userSession.getToken()));
                System.out.println("User can perform admin action: "
                        + authService.canAccess(userSession.getToken(), User.Role.ADMIN));
                authService.logout(userSession.getToken());
                System.out.println("User session active after logout: "
                        + (authService.authenticate(userSession.getToken()) != null));
            }
            if (adminSession != null) {
                System.out.println(authService.performAdminAction(adminSession.getToken()));
            }
            System.out.println("No passwords, hashes, salts, or tokens were printed.");
        } catch (GeneralSecurityException exception) {
            System.err.println("Required password-hashing algorithm is unavailable: "
                    + exception.getMessage());
        }
    }
}
