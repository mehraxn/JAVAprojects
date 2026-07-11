package authenticationsystem;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

/**
 * Test clock that starts at a fixed instant and only moves when the test
 * calls {@link #advance(Duration)}. Makes session expiry deterministic -
 * no Thread.sleep anywhere in the test suite.
 */
final class MutableClock extends Clock {
    private Instant current;

    MutableClock(Instant start) {
        if (start == null) {
            throw new IllegalArgumentException("Start instant cannot be null.");
        }
        this.current = start;
    }

    void advance(Duration duration) {
        if (duration == null || duration.isNegative()) {
            throw new IllegalArgumentException("Advance duration must be non-negative.");
        }
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
