package eventregistrationsystem;

import java.lang.reflect.Modifier;

final class AttendeeTest {
    private AttendeeTest() { }
    static void run() {
        Attendee attendee = new Attendee(" A1 ", " Lea ", "lea@example.com");
        Assertions.assertEquals("A1", attendee.getId(), "ID stored");
        Assertions.assertEquals("Lea", attendee.getName(), "name stored");
        Assertions.assertEquals("lea@example.com", attendee.getEmail(), "email stored");
        Assertions.assertTrue(Modifier.isFinal(Attendee.class.getModifiers()), "Attendee final");
        String[][] invalid = {{null,"n","a@b.com"},{" ","n","a@b.com"},{"a",null,"a@b.com"},{"a"," ","a@b.com"},{"a","n",null},{"a","n"," "},{"a","n","ab.com"},{"a","n","a@@b.com"},{"a","n","@b.com"},{"a","n","a@"},{"a","n","a@domain"},{"a","n","a@.com"},{"a","n","a@com."}};
        for (String[] values : invalid) Assertions.assertThrows(IllegalArgumentException.class, () -> new Attendee(values[0], values[1], values[2]), "invalid attendee rejected");
    }
}
