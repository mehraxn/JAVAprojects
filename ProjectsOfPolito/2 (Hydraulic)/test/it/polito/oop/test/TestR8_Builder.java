package it.polito.oop.test;

import hydraulic.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;


class TestR8_Builder {

	@Test
	void testBuildSource(){
		HSystem s = HSystem.build().addSource("Src 1").complete();
				
		Element[] elements = s.getElements();
		
		assertNotNull(elements, "Elements array should not be null");
		assertEquals(1, elements.length, "There should be one element");
		assertEquals("Src 1", elements[0].getName(), "Wrong element");
	}

	@Test
	void testConnections(){
		HSystem s = HSystem.build().
			addSource("Src").
			linkToTap("Tap").
			linkToSink("Sink").
			complete();

		Element[] elements = s.getElements();
	
		assertNotNull(elements, "Elements array should not be null");
		assertEquals(3, elements.length, "Wrong number of elements");
		Element src = elements[0];
		assertEquals("Src", src.getName(), "Wrong element");

		Element tap = src.getOutput();
		assertNotNull(tap, "Missing element");
		assertEquals("Tap", tap.getName(), "Wrong element");


		Element sink = tap.getOutput();
		assertNotNull(sink, "Missing element");
		assertEquals("Sink", sink.getName(), "Wrong element");
	}

	@Test
	void testSplit(){
		HSystem s = HSystem.build().
		addSource("Src").
		linkToSplit("T").withOutputs().
			linkToSink("Sink 1").
			then().linkToSink("Sink 2").
		complete();
		
		Element[] elements = s.getElements();
	
		assertNotNull(elements, "Elements array should not be null");
		assertEquals(4, elements.length, "Wrong number of elements");
		Element src = elements[0];
		assertEquals("Src", src.getName(), "Wrong element");

		Element split = src.getOutput();

		Element[] out = split.getOutputs();

		assertNotNull(out, "Missing outputs for the split");
		assertEquals(2, out.length, "Wrong number of outputs");
		assertNotNull(out[0], "Missing output 0 for Split");
		assertEquals("Sink 1", out[0].getName(), "Wrong output 0 for Split");
		assertNotNull(out[1], "Missing output 1 for Split");
		assertEquals("Sink 2", out[1].getName(), "Wrong output 1 for Split");
	}

	@Test
	void testSplitSplit(){
		HSystem s = HSystem.build().
		addSource("Src").
		linkToSplit("T").withOutputs().
			linkToSink("Sink 1").
			then().linkToSplit("T2").withOutputs().
				linkToSink("Sink 2").
				then().linkToSink("Sink 3").
				done().
			done().
		complete();
		
		Element[] elements = s.getElements();
	
		assertNotNull(elements, "Elements array should not be null");
		assertEquals(6, elements.length, "Wrong number of elements");
		Element src = elements[0];
		assertEquals("Src", src.getName(), "Wrong element");

		Element split = src.getOutput();

		Element[] out = split.getOutputs();

		assertNotNull(out, "Missing outputs for the split");
		assertEquals(2, out.length, "Wrong number of outputs");
		assertNotNull(out[0], "Missing output 0 for Split");
		assertEquals("Sink 1", out[0].getName(), "Wrong output 0 for Split");
		assertNotNull(out[1], "Missing output 1 for Split");
		assertEquals("T2", out[1].getName(), "Wrong output 1 for Split");

		out = out[1].getOutputs();
		assertNotNull(out, "Missing outputs for the nested split");
		assertNotNull(out[1], "Missing output 1 for Split");
		assertEquals("Sink 3", out[1].getName(), "Wrong output for nested Split");
	}

	@Test
	void testSplitTapTap(){
		HSystem s = HSystem.build().
		addSource("Src").
		linkToSplit("T").withOutputs().
			linkToTap("Tap 1").linkToTap("Tap 2").linkToSink("Sink 1").
			then().linkToTap("Tap 3").linkToTap("Tap 4").linkToSink("Sink 2").
			done().
		complete();
		
		Element[] elements = s.getElements();
	
		assertNotNull(elements, "Elements array should not be null");
		assertEquals(8, elements.length, "Wrong number of elements");
		Element src = elements[0];
		assertEquals("Src", src.getName(), "Wrong element");

		Element split = src.getOutput();

		Element[] out = split.getOutputs();

		assertNotNull(out, "Missing outputs for the split");
		assertEquals(2, out.length, "Wrong number of outputs");
		assertNotNull(out[0], "Missing output 0 for Split");
		assertEquals("Tap 1", out[0].getName(), "Wrong output 0 for Split");
		assertNotNull(out[1], "Missing output 1 for Split");
		assertEquals("Tap 3", out[1].getName(), "Wrong output 1 for Split");

		assertNotNull(out[0].getOutput(), "Missing outputs for the sequence of taps");
		assertNotNull(out[1].getOutput(), "Missing outputs for the sequence of taps");
		assertEquals("Tap 4", out[1].getOutput().getName(), "Wrong output for nested Split");
		assertNotNull(out[1].getOutput().getOutput(), "Missing outputs for the sequence of taps");
		assertEquals("Sink 2", out[1].getOutput().getOutput().getName(), "Wrong output for nested Split");
	}


	@Test
	void testSimulate(){
		double flow = 100.0;
		
		HSystem s = HSystem.build().
		addSource("Src").withFlow(flow).
		linkToSplit("T").withOutputs().
			linkToSink("Sink 1").
			then().linkToSink("Sink 2").
			done().
		complete();
		
		
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
	void testSimulateTap(){
		double flow = 100.0;
		
		HSystem s = HSystem.build().
		addSource("Src").withFlow(flow).
		linkToTap("Tap").open().
		linkToSink("Sink").
		complete();
		
		
		StoreObserver obs = new StoreObserver();
		s.simulate(obs);
		
		assertTrue(obs.contains("Tap"), "There was not simulation notification for element T");
		double[] splitOut = obs.outFlowsOf("Tap");

		assertEquals(1,splitOut.length, "There should be two outputs for the T split");
		assertEquals(flow,splitOut[0],0.01, "Wrong outputs for the T split");
		assertEquals( flow, obs.inFlowOf("Sink"), 0.01, "Wrong input flow of 'Sink 1'");
	}

	@Test
	void testSimulateTapClose(){
		double flow = 100.0;
		
		HSystem s = HSystem.build().
		addSource("Src").withFlow(flow).
		linkToTap("Tap").closed().
		linkToSink("Sink").
		complete();
		
		
		StoreObserver obs = new StoreObserver();
		s.simulate(obs);
		
		assertTrue(obs.contains("Tap"), "There was not simulation notification for element T");
		double[] splitOut = obs.outFlowsOf("Tap");

		assertEquals(1,splitOut.length, "There should be two outputs for the T split");
		assertEquals(0.0, splitOut[0],0.01, "Wrong outputs for the T split");
		assertEquals(0.0, obs.inFlowOf("Sink"), 0.01, "Wrong input flow of 'Sink 1'");
	}

	@Test
	void testSimulateMultisplit(){
		double flow = 100.0;
		double[] props = {0.25,0.35,0.40};

		HSystem s = HSystem.build().
		addSource("Src").withFlow(flow).
		linkToMultisplit("MS",3).withPropotions(props).withOutputs().
			linkToSink("S1").
			then().linkToSink("S2").
			then().linkToSink("S3").
			done().
		complete();
		
		
		StoreObserver obs = new StoreObserver();
		s.simulate(obs);
				
		assertTrue(obs.contains("MS"), "Missing simulation trace for element MS");

		double inTap = obs.inFlowOf("MS");
		double[] outTap = obs.outFlowsOf("MS");
		double inSink = obs.inFlowOf("S3");

		assertEquals(flow, inTap, 0.01, "Wrong input flow of 'MS'");
		for(int i=0; i<props.length; ++i)
			assertEquals(flow*props[i], outTap[i], 0.01, "Wrong output flow " + i + " of 'MS'");
		assertEquals(flow*props[2], inSink, 0.01, "Wrong input flow of 'S3'");
	}

	@Test
	void testSimulateMultisplitSplit(){
		double flow = 100.0;
		double[] props = {0.25,0.35,0.40};

		HSystem s = HSystem.build().
		addSource("Src").withFlow(flow).
		linkToMultisplit("MS",3).withPropotions(props).withOutputs().
			linkToSplit("T").withOutputs().
				linkToSink("S1").
				then().linkToSink("S2").
				done().
			then().linkToSink("S3").
			then().linkToSink("S4").
			done().
		complete();
				
		StoreObserver obs = new StoreObserver();
		s.simulate(obs);
				
		assertTrue(obs.contains("MS"), "Missing simulation trace for element MS");

		double inTap = obs.inFlowOf("MS");
		double[] outTap = obs.outFlowsOf("MS");
		double inSink = obs.inFlowOf("S2");

		assertEquals(flow, inTap, 0.01, "Wrong input flow of 'MS'");
		for(int i=0; i<props.length; ++i)
			assertEquals(flow*props[i], outTap[i], 0.01, "Wrong output flow " + i + " of 'MS'");
		assertEquals(flow*props[0]*0.5, inSink, 0.01, "Wrong input flow of 'S2'");
	}

	@Test
	void testMaxFlow(){
		double flow = 100.0;
		
		HSystem s = HSystem.build().
		addSource("Src").withFlow(flow).
		linkToTap("Tap").open().maxFlow(90.0).
		linkToSink("Sink").maxFlow(90.0).
		complete();
		
		
		StoreObserver obs = new StoreObserver();
		s.simulate(obs, true);
		
		assertEquals(2, obs.getErrorCount(), "Wrong number of notifications");
		assertTrue(obs.containsError("Tap"), "Missing simulation trace for element Tap");

		assertEquals(90.0, obs.maxFlowOf("Tap"), 0.01, "Wrong max flow of 'Tap'");
		assertEquals(90.0, obs.maxFlowOf("Sink"), 0.01, "Wrong max flow of 'Sink'");
	}
}
