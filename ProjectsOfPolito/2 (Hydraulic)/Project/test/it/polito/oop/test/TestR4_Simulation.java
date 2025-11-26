package it.polito.oop.test;

import hydraulic.*;
import static hydraulic.SimulationObserver.NO_FLOW;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;


class TestR4_Simulation {

	@Test
	void testSimpleElements(){
		HSystem s = new HSystem();
		Source src = new Source("Src");		
		Tap tap = new Tap("Tap");		
		Sink sink = new Sink("Sink");		
		s.addElement(src);
		s.addElement(tap);
		s.addElement(sink);
		
		src.connect(tap);
		tap.connect(sink);
		
		double flow = 100.0;
		src.setFlow(flow);
		tap.setOpen(true);
		
		StoreObserver obs = new StoreObserver();

		s.simulate(obs);
		
		obs.assertHasType("Src","source");
		obs.assertHasType("Tap","tap");
		
		assertTrue(obs.contains("Tap"), "Missing simulation trace for element Tap");
		assertTrue(obs.contains("Sink"), "Missing simulation trace for element Sink");
		double inTap = obs.inFlowOf("Tap");
		double outTap = obs.outFlowOf("Tap");
		double inSink = obs.inFlowOf("Sink");

		assertEquals(flow, inTap, 0.01, "Wrong input flow of 'Tap'");
		assertEquals(flow, outTap, 0.01, "Wrong output flow of 'Tap'");
		assertEquals(flow, inSink, 0.01, "Wrong input flow of 'Sink'");
	}

	@Test
	void testSimpleElementsClosedTap(){
		HSystem s = new HSystem();
		Source src = new Source("Src");		
		Tap tap = new Tap("Tap");		
		Sink sink = new Sink("Sink");		
		s.addElement(src);
		s.addElement(tap);
		s.addElement(sink);
		
		src.connect(tap);
		tap.connect(sink);
		
		double flow = 100.0;
		src.setFlow(flow);
		tap.setOpen(false);
		
		StoreObserver obs = new StoreObserver();

		s.simulate(obs);
		
		obs.assertHasType("Src","Source");
		
		assertTrue(obs.contains("Tap"), "Missing simulation trace for element Tap");
		assertTrue(obs.contains("Sink"), "Missing simulation trace for element Sink");
		double inTap = obs.inFlowOf("Tap");
		double outTap = obs.outFlowOf("Tap");
		double inSink = obs.inFlowOf("Sink");

		assertEquals(flow, inTap, 0.01, "Wrong input flow of 'Tap'");
		assertEquals(0.0, outTap, 0.01, "When 'Tap' is closed output flow should be 0.0");
		assertEquals(0.0, inSink, 0.01, "Wrong input flow of 'Sink'");
	}

	@Test
	void testSplit(){
		HSystem s = new HSystem();
		Source src = new Source("Src");		
		Split t = new Split("T");	
		Element sink1 = new Sink("Sink 1");		
		Element sink2 = new Sink("Sink 2");		
		s.addElement(src);
		s.addElement(t);
		s.addElement(sink1);
		s.addElement(sink2);

		src.connect(t);
		t.connect(sink1,0);
		t.connect(sink2,1);

		double flow = 100.0;
		src.setFlow(flow);

		StoreObserver obs = new StoreObserver();
		s.simulate(obs);

		assertTrue(obs.contains("T"), "There was not simulation notification for element T");
		double[] splitOut = obs.outFlowsOf("T");

		assertEquals(2, splitOut.length, "There should be two outputs for the T split");
		assertEquals(50.0, splitOut[0], 0.01, "Wrong outputs for the T split");
		assertEquals(50.0, splitOut[1], 0.01, "Wrong outputs for the T split");
		assertEquals(50, obs.inFlowOf("Sink 1"), 0.01, "Wrong input flow of 'Sink 1'");
		assertEquals(50, obs.inFlowOf("Sink 2"), 0.01, "Wrong input flow of 'Sink 2'");
	}

	@Test
	void testMissingFlow(){
		HSystem s = new HSystem();
		Source src = new Source("Src");		
		Sink sink = new Sink("Sink");		
		s.addElement(src);
		s.addElement(sink);
		
		src.connect(sink);
		
		double flow = 10.0;
		src.setFlow(flow);
		
		StoreObserver obs = new StoreObserver();

		s.simulate(obs);
		
		assertTrue(obs.contains("Src"), "There was not simulation notification for element Src");
		assertTrue(obs.contains("Sink"), "There was not simulation notification for element Sink");
		double inSrc = obs.inFlowOf("Src");
		double outSrc = obs.outFlowOf("Src");
		double inSink = obs.inFlowOf("Sink");
		double outSink = obs.outFlowOf("Sink");

		assertEquals(flow, outSrc, 0.01, "Wrong output flow of 'Src'");
		assertEquals(flow, inSink, 0.01, "Wrong input flow of 'Sink'");
		assertEquals(NO_FLOW, inSrc, 0.0, "Input flow of source should be NO_FLOW");
		assertEquals(NO_FLOW, outSink, 0.0, "Output flow of sink should be NO_FLOW");
	}

}
