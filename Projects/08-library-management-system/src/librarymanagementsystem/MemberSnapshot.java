package librarymanagementsystem;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Immutable, read-only view of a {@link Member}. The borrowed-ISBN set is an
 * unmodifiable copy, so holding a snapshot cannot change library state.
 */
public final class MemberSnapshot {
    private final String memberId;
    private final String name;
    private final String email;
    private final Set<String> borrowedIsbns;
    private final int activeLoanCount;

    MemberSnapshot(String memberId, String name, String email,
                   Set<String> borrowedIsbns, int activeLoanCount) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
        this.borrowedIsbns = Collections.unmodifiableSet(new LinkedHashSet<>(borrowedIsbns));
        this.activeLoanCount = activeLoanCount;
    }

    public String getMemberId() { return memberId; }
    public String getName() { return name; }

    /** Email address, or {@code null} if none was provided. */
    public String getEmail() { return email; }
    public Set<String> getBorrowedIsbns() { return borrowedIsbns; }
    public int getActiveLoanCount() { return activeLoanCount; }

    @Override
    public String toString() {
        return memberId + " (" + name + ") holding " + activeLoanCount + " book(s)";
    }
}
