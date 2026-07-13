package bankaccountsimulator;

/**
 * Category of a {@link Transaction}.
 *
 * <ul>
 *   <li>{@link #DEPOSIT} — money paid into an account.</li>
 *   <li>{@link #WITHDRAWAL} — money taken out of an account.</li>
 *   <li>{@link #TRANSFER_OUT} — money sent to another account.</li>
 *   <li>{@link #TRANSFER_IN} — money received from another account.</li>
 * </ul>
 */
public enum TransactionType {
    DEPOSIT,
    WITHDRAWAL,
    TRANSFER_IN,
    TRANSFER_OUT
}
