package it.polito.oop.test;

import hydraulic.*;
import static org.junit.jupiter.api.Assertions.*;
import static it.polito.oop.test.OOPAssertions.*;

import org.junit.jupiter.api.Test;


class TestR2_SimpleElements {

	@Test
	void testNameSource(){
		String name="Test";
		Element el = new Source(name);

		assertEquals(name, el.getName(), "Wrong name for element");
	}

	@Test
	void testNameTap(){
		String name="Test";
		Element el = new Tap(name);

		assertEquals(name, el.getName(), "Wrong name for element");
	}

	@Test
	void testNameSink(){
		String name="Test";
		Element el = new Sink(name);

		assertEquals(name, el.getName(), "Wrong name for element");
	}

	@Test
	void testConnections(){
		HSystem s = new HSystem();
		Source src = new Source("Src");		
		Tap tap = new Tap("Tap");		
		Sink sink = new Sink("Sink");

		s.addElement(src);
		s.addElement(tap);
		s.addElement(sink);

		src.connect(tap);
		tap.connect(sink);

		assertSameElement(tap, src.getOutput(), "Wrong output for element src");
		assertSameElement(sink, tap.getOutput(), "Wrong output for element tap");
	}

	@Test
	void testSinkConnect(){
		Tap tap = new Tap("Tap");		
		Sink sink = new Sink("Sink");

		Element none = sink.getOutput();

		sink.connect(tap);

		Element out = sink.getOutput();

		assertSameElement(none, out, "Connect on a sink should have no effect");

		tap.connect(sink);

		assertSameElement(sink, tap.getOutput(), "Connect to a sink should work");
	}

}
