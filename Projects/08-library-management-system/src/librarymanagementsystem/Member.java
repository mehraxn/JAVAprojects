package librarymanagementsystem;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A library member and the set of books they currently have on loan.
 *
 * <p>Identity ({@code memberId}), name, and email are fixed at construction; the
 * borrowed-ISBN set changes only through the package-private mutators driven by
 * {@link Library}. Outside callers receive an immutable {@link MemberSnapshot},
 * so a live {@code Member} is never leaked and cannot be mutated to bypass
 * {@link Library}.
 *
 * <p>Email is optional. When present it is checked with a deliberately simple,
 * educational rule (one {@code @}, non-blank local and domain parts, and a dot in
 * the domain) — not production-grade validation.
 */
public final class Member {
    private final String memberId;
    private final String name;
    private final String email;
    private final Set<String> borrowedIsbns = new LinkedHashSet<>();

    /** Creates a member with an email address (simple validation applied). */
    public Member(String memberId, String name, String email) {
        this.memberId = requireText(memberId, "Member ID");
        this.name = requireText(name, "Member name");
        this.email = validateEmail(email);
    }

    /** Creates a member without an email address (contact info not modeled). */
    public Member(String memberId, String name) {
        this.memberId = requireText(memberId, "Member ID");
        this.name = requireText(name, "Member name");
        this.email = null;
    }

    public String getMemberId() { return memberId; }
    public String getName() { return name; }

    /** Email address, or {@code null} if none was provided. */
    public String getEmail() { return email; }

    public int getActiveLoanCount() { return borrowedIsbns.size(); }

    public boolean hasBorrowed(String isbn) {
        return isbn != null && borrowedIsbns.contains(isbn.trim());
    }

    /** Records a borrowed ISBN; rejects a duplicate. */
    void addBorrowed(String isbn) {
        if (!borrowedIsbns.add(requireText(isbn, "ISBN"))) {
            throw new IllegalStateException("Member already has this book: " + isbn.trim());
        }
    }

    /** Removes a borrowed ISBN; rejects one the member does not hold. */
    void removeBorrowed(String isbn) {
        if (!borrowedIsbns.remove(requireText(isbn, "ISBN"))) {
            throw new IllegalStateException("Member did not borrow this book: " + isbn.trim());
        }
    }

    /** Unmodifiable snapshot copy of the currently borrowed ISBNs. */
    public Set<String> getBorrowedIsbns() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(borrowedIsbns));
    }

    /** Immutable, read-only view of this member and its borrowed set. */
    public MemberSnapshot toSnapshot() {
        return new MemberSnapshot(memberId, name, email,
                new LinkedHashSet<>(borrowedIsbns), borrowedIsbns.size());
    }

    @Override
    public String toString() {
        return memberId + " (" + name + ") holding " + borrowedIsbns.size() + " book(s)";
    }

    /** Simple, educational email check — not production-grade. */
    private static String validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email must not be blank");
        }
        String trimmed = email.trim();
        int at = trimmed.indexOf('@');
        if (at <= 0 || at != trimmed.lastIndexOf('@') || at == trimmed.length() - 1) {
            throw new IllegalArgumentException("Email must contain exactly one @ with local and domain parts");
        }
        String domain = trimmed.substring(at + 1);
        if (!domain.contains(".") || domain.startsWith(".") || domain.endsWith(".")) {
            throw new IllegalArgumentException("Email domain must contain a dot");
        }
        return trimmed;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
