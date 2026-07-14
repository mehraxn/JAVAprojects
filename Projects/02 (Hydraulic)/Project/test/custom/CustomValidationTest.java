package custom;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import hydraulic.*;

class CustomValidationTest {

    @Test
    void nullNameIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Source(null));
    }

    @Test
    void blankNameIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Tap("  "));
    }

    @Test
    void nullElementIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new HSystem().addElement(null));
    }

    @Test
    void duplicateNameIsRejected() {
        HSystem system = new HSystem();
        system.addElement(new Source("same"));
        assertThrows(IllegalArgumentException.class, () -> system.addElement(new Sink("same")));
    }

    @Test
    void elementsArrayIsDefensive() {
        HSystem system = new HSystem();
        Source source = new Source("source");
        system.addElement(source);
        Element[] exposed = system.getElements();
        exposed[0] = new Sink("replacement");
        assertSame(source, system.getElements()[0]);
    }

    @Test
    void negativeSourceFlowIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Source("source").setFlow(-0.01));
    }

    @Test
    void negativeMaximumFlowIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Tap("tap").setMaxFlow(-1.0));
    }

    @Test
    void zeroOutputMultisplitIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Multisplit("multi", 0));
    }

    @Test
    void nullObserverIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new HSystem().simulate(null));
    }

    @Test
    void emptySystemSimulationIsSafe() {
        assertDoesNotThrow(() -> new HSystem().simulate(new RecordingObserver()));
    }
}
