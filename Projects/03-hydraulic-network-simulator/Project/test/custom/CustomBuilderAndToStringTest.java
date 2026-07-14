package custom;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import hydraulic.*;

class CustomBuilderAndToStringTest {

    @Test
    void simpleBuilderCreatesConnectedSystem() {
        HSystem system = HSystem.build().addSource("source").linkToSink("sink").complete();
        assertEquals("sink", system.getElements()[0].getOutput().getName());
    }

    @Test
    void correctlySpelledProportionsAliasWorks() {
        HSystem system = HSystem.build().addSource("source")
                .linkToMultisplit("multi", 2).withProportions(0.3, 0.7).withOutputs()
                .linkToSink("one").then().linkToSink("two").done().complete();
        assertArrayEquals(new double[] { 0.3, 0.7 },
                ((Multisplit) system.getElements()[1]).getProportions());
    }

    @Test
    void legacyProportionsSpellingStillWorks() {
        HSystem system = HSystem.build().addSource("source")
                .linkToMultisplit("multi", 2).withPropotions(0.4, 0.6).withOutputs()
                .linkToSink("one").then().linkToSink("two").done().complete();
        assertArrayEquals(new double[] { 0.4, 0.6 },
                ((Multisplit) system.getElements()[1]).getProportions());
    }

    @Test
    void nestedBuilderCreatesAllBranches() {
        HSystem system = HSystem.build().addSource("source").linkToSplit("outer").withOutputs()
                .linkToSplit("inner").withOutputs()
                .linkToSink("a").then().linkToSink("b").done()
                .then().linkToSink("c").done().complete();
        assertEquals(6, system.size());
    }

    @Test
    void thenWithoutOutputsFailsClearly() {
        HBuilder builder = HSystem.build().addSource("source");
        assertThrows(IllegalStateException.class, builder::then);
    }

    @Test
    void doneWithoutOutputsFailsClearly() {
        HBuilder builder = HSystem.build().addSource("source");
        assertThrows(IllegalStateException.class, builder::done);
    }

    @Test
    void tooManyThenCallsFailClearly() {
        HBuilder builder = HSystem.build().addSource("source").linkToSplit("split").withOutputs()
                .linkToSink("one").then().linkToSink("two");
        assertThrows(IllegalStateException.class, builder::then);
    }

    @Test
    void withOutputsRequiresBranchingElement() {
        HBuilder builder = HSystem.build().addSource("source").linkToTap("tap");
        assertThrows(IllegalStateException.class, builder::withOutputs);
    }

    @Test
    void incompleteBranchCannotBeCompleted() {
        HBuilder builder = HSystem.build().addSource("source").linkToSplit("split").withOutputs()
                .linkToSink("one");
        assertThrows(IllegalStateException.class, builder::complete);
    }

    @Test
    void systemStringContainsConnectedNames() {
        HSystem system = HSystem.build().addSource("source").linkToTap("tap").linkToSink("sink").complete();
        String layout = system.toString();
        assertAll(
                () -> assertTrue(layout.contains("source")),
                () -> assertTrue(layout.contains("tap")),
                () -> assertTrue(layout.contains("sink")),
                () -> assertFalse(layout.matches(".*HSystem@[0-9a-fA-F]+.*")));
    }

    @Test
    void systemStringHandlesCycles() {
        HSystem system = new HSystem();
        Source source = new Source("source");
        Tap tap = new Tap("tap");
        source.connect(tap);
        tap.connect(source);
        system.addElement(source);
        system.addElement(tap);
        assertTrue(system.toString().contains("cycle"));
    }
}
