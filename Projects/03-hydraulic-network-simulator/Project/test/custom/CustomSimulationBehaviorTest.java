package custom;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import hydraulic.*;

class CustomSimulationBehaviorTest {

    @Test
    void sourceFlowReachesSink() {
        HSystem system = new HSystem();
        Source source = new Source("source");
        Sink sink = new Sink("sink");
        system.addElement(source);
        system.addElement(sink);
        source.connect(sink);
        source.setFlow(42.0);
        RecordingObserver observer = new RecordingObserver();
        system.simulate(observer);
        assertEquals(42.0, observer.statuses.get("sink").input);
    }

    @Test
    void openTapPassesFlow() {
        Fixture fixture = new Fixture(true);
        fixture.system.simulate(fixture.observer);
        assertEquals(80.0, fixture.observer.statuses.get("tap").outputs[0]);
    }

    @Test
    void closedTapBlocksFlow() {
        Fixture fixture = new Fixture(false);
        fixture.system.simulate(fixture.observer);
        assertEquals(0.0, fixture.observer.statuses.get("sink").input);
    }

    @Test
    void disconnectedSourceDoesNotCrash() {
        HSystem system = new HSystem();
        Source source = new Source("source");
        source.setFlow(10.0);
        system.addElement(source);
        assertDoesNotThrow(() -> system.simulate(new RecordingObserver()));
    }

    @Test
    void maximumFlowAlarmIsReportedWhenEnabled() {
        Fixture fixture = new Fixture(true);
        fixture.tap.setMaxFlow(50.0);
        fixture.system.simulate(fixture.observer, true);
        assertTrue(fixture.observer.errors.containsKey("tap"));
    }

    @Test
    void maximumFlowAlarmIsSuppressedWhenDisabled() {
        Fixture fixture = new Fixture(true);
        fixture.tap.setMaxFlow(50.0);
        fixture.system.simulate(fixture.observer, false);
        assertTrue(fixture.observer.errors.isEmpty());
    }

    @Test
    void sinkHasNoOutput() {
        Sink sink = new Sink("sink");
        assertNull(sink.getOutput());
        assertEquals(0, sink.getOutputs().length);
    }

    private static final class Fixture {
        final HSystem system = new HSystem();
        final Tap tap = new Tap("tap");
        final RecordingObserver observer = new RecordingObserver();

        Fixture(boolean open) {
            Source source = new Source("source");
            Sink sink = new Sink("sink");
            source.setFlow(80.0);
            tap.setOpen(open);
            source.connect(tap);
            tap.connect(sink);
            system.addElement(source);
            system.addElement(tap);
            system.addElement(sink);
        }
    }
}
