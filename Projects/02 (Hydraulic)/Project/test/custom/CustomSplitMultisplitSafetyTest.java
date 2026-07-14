package custom;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import hydraulic.*;

class CustomSplitMultisplitSafetyTest {

    @Test
    void splitDividesFlowEqually() {
        RecordingObserver observer = simulateSplit(true, true);
        assertArrayEquals(new double[] { 50.0, 50.0 }, observer.statuses.get("split").outputs);
    }

    @Test
    void splitAllowsDisconnectedOutput() {
        assertDoesNotThrow(() -> simulateSplit(false, true));
    }

    @Test
    void splitRejectsInvalidIndex() {
        assertThrows(IllegalArgumentException.class,
                () -> new Split("split").connect(new Sink("sink"), 2));
    }

    @Test
    void splitOutputsAreDefensive() {
        Split split = new Split("split");
        Sink sink = new Sink("sink");
        split.connect(sink, 0);
        split.getOutputs()[0] = null;
        assertSame(sink, split.getOutputs()[0]);
    }

    @Test
    void multisplitDistributesUsingProportions() {
        RecordingObserver observer = simulateMultisplit();
        assertArrayEquals(new double[] { 20.0, 30.0, 50.0 },
                observer.statuses.get("multi").outputs, 1.0e-9);
    }

    @Test
    void callerMutationCannotChangeProportions() {
        Multisplit multi = new Multisplit("multi", 2);
        double[] proportions = { 0.25, 0.75 };
        multi.setProportions(proportions);
        proportions[0] = 1.0;
        assertArrayEquals(new double[] { 0.25, 0.75 }, multi.getProportions());
    }

    @Test
    void returnedProportionsAreDefensive() {
        Multisplit multi = new Multisplit("multi", 2);
        multi.setProportions(0.4, 0.6);
        multi.getProportions()[0] = 0.9;
        assertArrayEquals(new double[] { 0.4, 0.6 }, multi.getProportions());
    }

    @Test
    void multisplitOutputsAreDefensive() {
        Multisplit multi = new Multisplit("multi", 2);
        Sink sink = new Sink("sink");
        multi.connect(sink, 1);
        multi.getOutputs()[1] = null;
        assertSame(sink, multi.getOutputs()[1]);
    }

    @Test
    void invalidProportionLengthIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Multisplit("multi", 3).setProportions(0.5, 0.5));
    }

    @Test
    void negativeProportionIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Multisplit("multi", 2).setProportions(-0.1, 1.1));
    }

    @Test
    void invalidProportionSumIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Multisplit("multi", 2).setProportions(0.2, 0.2));
    }

    @Test
    void nullProportionsAreRejected() {
        Multisplit multi = new Multisplit("multi", 2);
        assertThrows(IllegalArgumentException.class, () -> multi.setProportions((double[]) null));
    }

    private RecordingObserver simulateSplit(boolean firstConnected, boolean secondConnected) {
        HSystem system = new HSystem();
        Source source = new Source("source");
        Split split = new Split("split");
        source.setFlow(100.0);
        source.connect(split);
        system.addElement(source);
        system.addElement(split);
        if (firstConnected) {
            Sink first = new Sink("first");
            split.connect(first, 0);
            system.addElement(first);
        }
        if (secondConnected) {
            Sink second = new Sink("second");
            split.connect(second, 1);
            system.addElement(second);
        }
        RecordingObserver observer = new RecordingObserver();
        system.simulate(observer);
        return observer;
    }

    private RecordingObserver simulateMultisplit() {
        HSystem system = new HSystem();
        Source source = new Source("source");
        Multisplit multi = new Multisplit("multi", 3);
        source.setFlow(100.0);
        source.connect(multi);
        multi.setProportions(0.2, 0.3, 0.5);
        system.addElement(source);
        system.addElement(multi);
        RecordingObserver observer = new RecordingObserver();
        system.simulate(observer);
        return observer;
    }
}
