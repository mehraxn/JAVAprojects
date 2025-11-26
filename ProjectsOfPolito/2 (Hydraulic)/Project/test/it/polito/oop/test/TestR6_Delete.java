package it.polito.oop.test;

import hydraulic.*;

import static org.junit.jupiter.api.Assertions.*;
import static it.polito.oop.test.OOPAssertions.*;

import org.junit.jupiter.api.Test;


class TestR6_Delete {

	@Test
	void testSimpleElementRemove(){
		HSystem s = new HSystem();
		Source src = new Source("Src");		
		Tap tap = new Tap("Tap");		
		Sink sink = new Sink("Sink");		
		s.addElement(src);
		s.addElement(tap);
		s.addElement(sink);
		
		src.connect(tap);
		tap.connect(sink);
		
		boolean done = s.deleteElement("Tap");		

		assertEquals(2, s.size(), "Wrong number of elements after delete");
		assertTrue(done, "Operation not performed");
	}

	@Test
	void testSimpleElementsRelink(){
		HSystem s = new HSystem();
		Source src = new Source("Src");		
		Tap tap = new Tap("Tap");		
		Sink sink = new Sink("Sink");		
		s.addElement(src);
		s.addElement(tap);
		s.addElement(sink);
		
		src.connect(tap);
		tap.connect(sink);
		
		System.out.println(s);
		boolean done = s.deleteElement("Tap");
		System.out.println(s);

		assertTrue(done, "Operation not performed");
		assertSameElement(sink, src.getOutput(), "Output not fixed after delete");
	}

	@Test
	void testSinkRelink(){
		HSystem s = new HSystem();
		Source src = new Source("Src");		
		Tap tap = new Tap("Tap");		
		Sink sink = new Sink("Sink");		
		s.addElement(src);
		s.addElement(tap);
		s.addElement(sink);

		src.connect(tap);
		tap.connect(sink);

		boolean done = s.deleteElement("Sink");

		assertTrue(done, "Operation not performed");
		assertSameElement(null, tap.getOutput(), "Output not fixed after delete");
	}

	@Test
	void testSinkAfterSplit(){
		HSystem s = new HSystem();
		Source src = new Source("Src");
		Tap tap = new Tap("Tap");
		Split t = new Split("T");
		Element sink1 = new Sink("Sink 1");		
		Element sink2 = new Sink("Sink 2");		
		s.addElement(src);
		s.addElement(tap);
		s.addElement(t);
		s.addElement(sink1);
		s.addElement(sink2);
		
		src.connect(tap);
		tap.connect(t);
		t.connect(sink1,0);
		t.connect(sink2,1);
		
		System.out.println(s);
		boolean done = s.deleteElement("Sink 2");
		System.out.println(s);

		
		assertTrue(done, "Operation should be performed");
		assertEquals(4, s.size(), "Wrong number of elements after delete");
		assertSameElement(sink1, t.getOutputs()[0], "Output not fixed after delete");
		assertSameElement(null, t.getOutputs()[1], "Output not fixed after delete");
	}

	@Test
	void testWithSplit(){
		HSystem s = new HSystem();
		Source src = new Source("Src");
		Tap tap = new Tap("Tap");
		Split t = new Split("T");
		Element sink1 = new Sink("Sink 1");		
		Element sink2 = new Sink("Sink 2");		
		s.addElement(src);
		s.addElement(tap);
		s.addElement(t);
		s.addElement(sink1);
		s.addElement(sink2);
		
		src.connect(tap);
		tap.connect(t);
		t.connect(sink1,0);
		t.connect(sink2,1);
		
		boolean done = s.deleteElement("T");

		assertFalse(done, "Operation should not be performed on a connected Split");
		assertEquals(5, s.size(), "Wrong number of elements after denied delete");
	}

	@Test
	void testWithSplitUnconnected(){
		HSystem s = new HSystem();
		Source src = new Source("Src");
		Tap tap = new Tap("Tap");
		Split t = new Split("T");
		Element sink1 = new Sink("Sink 1");		
		s.addElement(src);
		s.addElement(tap);
		s.addElement(t);
		s.addElement(sink1);

		src.connect(tap);
		tap.connect(t);
		t.connect(sink1,0); // only one output connected!

		boolean done = s.deleteElement("T");

		assertTrue(done, "Operation should be permitted on a single-connected Split");
		assertEquals(3, s.size(), "Wrong number of elements after delete");
		assertSameElement(sink1, tap.getOutput(), "Output not fixed after delete");
	}

	@Test
	void testMultipleDeletes(){
		HSystem s = new HSystem();
		Source src = new Source("Src");
		Tap tap = new Tap("Tap");
		Split t = new Split("T");
		Element sink1 = new Sink("Sink 1");		
		Element sink2 = new Sink("Sink 2");		
		s.addElement(src);
		s.addElement(tap);
		s.addElement(t);
		s.addElement(sink1);
		s.addElement(sink2);
		
		src.connect(tap);
		tap.connect(t);
		t.connect(sink1,0);
		t.connect(sink2,1);
		
		boolean done = s.deleteElement("Sink 1");
		assertTrue(done, "Operation should be permitted!");


		done = s.deleteElement("Sink 2");
		assertTrue(done, "Operation should be permitted!");


		done = s.deleteElement("T");

		assertTrue(done, "Operation should be permitted!");

		assertEquals(2, s.size(), "Wrong number of elements after multiple deletes");
	}
}
