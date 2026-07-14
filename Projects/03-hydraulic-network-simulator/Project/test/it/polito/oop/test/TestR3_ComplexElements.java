package it.polito.oop.test;

import hydraulic.*;
import static org.junit.jupiter.api.Assertions.*;
import static it.polito.oop.test.OOPAssertions.*;

import org.junit.jupiter.api.Test;


class TestR3_ComplexElements {


	@Test
	void testSplitName(){
		Split t = new Split("Split name");	

		assertNotNull(t.getName(), "Missing split name");
		assertEquals("Split name", t.getName(), "Wrong name for split element");
	}


	@Test
	void testSplit(){

		HSystem s = new HSystem();

		Element src = new Source("Src");		
		Split t = new Split("T");	
		Element sink1 = new Sink("Sink 1");		
		Element sink2 = new Sink("Sink 2");		

		s.addElement(src);
		s.addElement(t);
		s.addElement(sink1);
		s.addElement(sink2);

		src.connect(t);
		t.connect(sink1, 0);
		t.connect(sink2, 1);

		Element[] out = t.getOutputs();

		assertNotNull(out, "Missing output for the split");
		assertSameElement(sink1, out[0], "Wrong output 0 for Split");
		assertSameElement(sink2, out[1], "Wrong output 1 for Split");
	}

}
