package it.polito.oop.test;

import hydraulic.*;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;


class TestR5_Multisplit {

	@Test
	void testCreationElements(){
		HSystem s = new HSystem();
		Source src = new Source("Src");
		Multisplit ms = new Multisplit("MS",3);
		Sink s1 = new Sink("S1");
		Sink s2 = new Sink("S2");
		Sink s3 = new Sink("S3");
		s.addElement(src);
		s.addElement(ms);
		s.addElement(s1);
		s.addElement(s2);
		s.addElement(s3);

		src.connect(ms);
		ms.connect(s1,0);
		ms.connect(s3,1);
		ms.connect(s2,2);

		Element[] outs = ms.getOutputs();

		assertNotNull(outs, "Missing outputs");
		assertEquals(3, outs.length, "Wrong number of outputs");
		assertArrayEquals(new Element[]{s1, s3, s2}, outs, "Wrong outputs");
	}

	@Test
	void testSimulation(){
		HSystem s = new HSystem();
		Source src = new Source("Src");
		Multisplit ms = new Multisplit("MS",3);
		Sink s1 = new Sink("S1");
		Sink s2 = new Sink("S2");
		Sink s3 = new Sink("S3");
		s.addElement(src);
		s.addElement(ms);
		s.addElement(s1);
		s.addElement(s2);
		s.addElement(s3);

		src.connect(ms);
		ms.connect(s1,0);
		ms.connect(s2,1);
		ms.connect(s3,2);


		double flow = 100.0;
		src.setFlow(flow);
		double[] props = {0.25,0.35,0.40};
		ms.setProportions(props);

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
	
}
