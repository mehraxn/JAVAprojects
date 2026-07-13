package librarymanagementsystem;

import java.util.Set;

import static librarymanagementsystem.TestSupport.assertEquals;
import static librarymanagementsystem.TestSupport.assertNull;
import static librarymanagementsystem.TestSupport.assertThrows;
import static librarymanagementsystem.TestSupport.assertTrue;

final class MemberTest {

    private MemberTest() {
    }

    static void register(TestRunner runner) {
        runner.test("Member: valid creation stores id, name, and email", () -> {
            Member member = new Member("M001", "Elena", "elena@example.com");
            assertEquals("M001", member.getMemberId(), "id");
            assertEquals("Elena", member.getName(), "name");
            assertEquals("elena@example.com", member.getEmail(), "email");
        });

        runner.test("Member: simple constructor has null email", () ->
                assertNull(new Member("M002", "Noah").getEmail(), "no email"));

        runner.test("Member: null/blank member ID rejected", () -> {
            assertThrows(IllegalArgumentException.class, () -> new Member(null, "N"), "null id");
            assertThrows(IllegalArgumentException.class, () -> new Member("  ", "N"), "blank id");
        });

        runner.test("Member: null/blank name rejected", () -> {
            assertThrows(IllegalArgumentException.class, () -> new Member("M1", null), "null name");
            assertThrows(IllegalArgumentException.class, () -> new Member("M1", "  "), "blank name");
        });

        runner.test("Member: invalid email rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new Member("M1", "N", "no-at-sign"), "no @");
            assertThrows(IllegalArgumentException.class,
                    () -> new Member("M1", "N", "a@@b.com"), "two @");
            assertThrows(IllegalArgumentException.class,
                    () -> new Member("M1", "N", "@example.com"), "blank local");
            assertThrows(IllegalArgumentException.class,
                    () -> new Member("M1", "N", "a@examplecom"), "no dot in domain");
            assertThrows(IllegalArgumentException.class,
                    () -> new Member("M1", "N", "  "), "blank email");
        });

        runner.test("Member: borrowed set starts empty", () ->
                assertEquals(0, new Member("M1", "N").getActiveLoanCount(), "empty"));

        runner.test("Member: borrowed tracking works internally", () -> {
            Member member = new Member("M1", "N");
            member.addBorrowed("978-1");
            assertTrue(member.hasBorrowed("978-1"), "has book");
            assertEquals(1, member.getActiveLoanCount(), "count 1");
            member.removeBorrowed("978-1");
            assertEquals(0, member.getActiveLoanCount(), "count 0");
        });

        runner.test("Member: adding a duplicate / removing a missing ISBN rejected", () -> {
            Member member = new Member("M1", "N");
            member.addBorrowed("978-1");
            assertThrows(IllegalStateException.class, () -> member.addBorrowed("978-1"), "dup add");
            assertThrows(IllegalStateException.class, () -> member.removeBorrowed("000"), "missing remove");
        });

        runner.test("Member: borrowed ISBN set is unmodifiable", () -> {
            Member member = new Member("M1", "N");
            member.addBorrowed("978-1");
            Set<String> isbns = member.getBorrowedIsbns();
            assertThrows(UnsupportedOperationException.class, () -> isbns.add("X"), "unmodifiable");
        });

        runner.test("Member: snapshot contains expected data", () -> {
            Member member = new Member("M001", "Elena", "elena@example.com");
            member.addBorrowed("978-1");
            MemberSnapshot snapshot = member.toSnapshot();
            assertEquals("M001", snapshot.getMemberId(), "snapshot id");
            assertEquals("Elena", snapshot.getName(), "snapshot name");
            assertEquals("elena@example.com", snapshot.getEmail(), "snapshot email");
            assertEquals(1, snapshot.getActiveLoanCount(), "snapshot count");
            assertThrows(UnsupportedOperationException.class,
                    () -> snapshot.getBorrowedIsbns().clear(), "snapshot set unmodifiable");
        });
    }
}
