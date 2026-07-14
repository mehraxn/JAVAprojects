package custom;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import hydraulic.*;

class CustomDeleteElementEdgeCaseTest {

    @Test
    void deletingSimpleMiddleElementRewires() {
        HSystem system = new HSystem();
        Source source = add(system, new Source("source"));
        Tap tap = add(system, new Tap("tap"));
        Sink sink = add(system, new Sink("sink"));
        source.connect(tap);
        tap.connect(sink);
        assertTrue(system.deleteElement("tap"));
        assertSame(sink, source.getOutput());
    }

    @Test
    void deletingSplitWithOnlyOutputZeroRewires() {
        assertSplitRewire(0);
    }

    @Test
    void deletingSplitWithOnlyOutputOneRewires() {
        assertSplitRewire(1);
    }

    @Test
    void deletingMultisplitWithOnlyLastOutputRewires() {
        HSystem system = new HSystem();
        Source source = add(system, new Source("source"));
        Multisplit multi = add(system, new Multisplit("multi", 3));
        Sink sink = add(system, new Sink("sink"));
        source.connect(multi);
        multi.connect(sink, 2);
        assertTrue(system.deleteElement("multi"));
        assertSame(sink, source.getOutput());
    }

    @Test
    void deletingConnectedBranchingElementIsDenied() {
        HSystem system = new HSystem();
        Source source = add(system, new Source("source"));
        Split split = add(system, new Split("split"));
        source.connect(split);
        split.connect(add(system, new Sink("one")), 0);
        split.connect(add(system, new Sink("two")), 1);
        assertFalse(system.deleteElement("split"));
        assertEquals(4, system.size());
    }

    @Test
    void deletingMissingElementReturnsFalse() {
        assertFalse(new HSystem().deleteElement("missing"));
    }

    @Test
    void everyIncomingReferenceIsRewired() {
        HSystem system = new HSystem();
        Source first = add(system, new Source("first"));
        Source second = add(system, new Source("second"));
        Tap tap = add(system, new Tap("tap"));
        Sink sink = add(system, new Sink("sink"));
        first.connect(tap);
        second.connect(tap);
        tap.connect(sink);
        assertTrue(system.deleteElement("tap"));
        assertSame(sink, first.getOutput());
        assertSame(sink, second.getOutput());
    }

    @Test
    void deletionPreservesInsertionOrder() {
        HSystem system = new HSystem();
        add(system, new Source("first"));
        add(system, new Tap("middle"));
        add(system, new Sink("last"));
        system.deleteElement("middle");
        assertArrayEquals(new String[] { "first", "last" },
                java.util.Arrays.stream(system.getElements()).map(Element::getName).toArray(String[]::new));
    }

    private void assertSplitRewire(int connectedOutput) {
        HSystem system = new HSystem();
        Source source = add(system, new Source("source"));
        Split split = add(system, new Split("split"));
        Sink sink = add(system, new Sink("sink"));
        source.connect(split);
        split.connect(sink, connectedOutput);
        assertTrue(system.deleteElement("split"));
        assertSame(sink, source.getOutput());
    }

    private <T extends Element> T add(HSystem system, T element) {
        system.addElement(element);
        return element;
    }
}
