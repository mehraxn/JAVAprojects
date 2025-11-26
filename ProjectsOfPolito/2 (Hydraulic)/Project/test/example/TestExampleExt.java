package example;
import hydraulic.*;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestExampleExt {
	private HSystem s;
	private Source src;
	private Element t;
	private Element sinkA;
	private Element sinkB;
	private Element sinkC;
	private Element sinkD;
	private Tap r;
	private Multisplit ms;

	@BeforeEach
	void setUp(){
		s = new HSystem();

		src = new Source("Src");
		s.addElement(src);
		r = new Tap("R");
		s.addElement(r);
		t = new Split("T");
		s.addElement(t);
		sinkA = new Sink("sink A");
		s.addElement(sinkA);
		sinkB = new Sink("sink B");
		s.addElement(sinkB);

		// Create a multi-split and some additional sink
		ms = new Multisplit("MS",3);
		s.addElement(ms);
		sinkC = new Sink("sink C");
		s.addElement(sinkC);
		sinkD = new Sink("sink D");
		s.addElement(sinkD);

		// Change the system including the multi-split
		src.connect(r);
		r.connect(ms);
		ms.connect(t,0);
		ms.connect(sinkC,1);
		ms.connect(sinkD,2);
		t.connect(sinkA,0);
		t.connect(sinkB,1);
	}

	@Test
	void testR5(){
		// simulation parameters are then defined
		src.setFlow(20);
		r.setOpen(true);
		ms.setProportions(.25,.35,.40);

		// simulation starts
		PrintingObserver obs = new PrintingObserver();
		s.simulate(obs);
		assertTrue(obs.getCount() >= 8, 
				"Expected at least 8 notifications but received just " + obs.getCount());
	}

	@Test
	void testR6(){
		// delete the tap
		s.deleteElement("R");
		assertSame(ms, src.getOutput(), "Output of src should be t.");
	}

	@Test
	void testR7(){
		src.setFlow(20);
		r.setOpen(true);
		ms.setProportions(.25,.35,.40);

		ms.setMaxFlow(20);
		r.setMaxFlow(25);
		t.setMaxFlow(10);
		sinkA.setMaxFlow(10);
		sinkB.setMaxFlow(15);
		sinkC.setMaxFlow(3); // should raise error message, inFlow 8.0 but maxFlow 3.0
		sinkD.setMaxFlow(8);
		PrintingObserver obs = new PrintingObserver();
		s.simulate(obs,true);
		assertEquals(8, obs.getCount(), "Missing some simulation notification.");
		assertEquals(1, obs.getErrorCount(), "Error notification not received");
	}

	@Test
	void testR8(){
		double flow = 100.0;
		double[] props = {0.25,0.35,0.40};

		HSystem hs = HSystem.build().
		addSource("Src").withFlow(flow).
		linkToMultisplit("MS",3).withPropotions(props).withOutputs().
			linkToSplit("T").withOutputs().
				linkToSink("S1").
				then().linkToSink("S2").
				done().
			then().linkToSink("S3").
			then().linkToSink("S4").
		complete();

		assertNotNull(hs, "Builder did not return any system.");

		assertEquals(7, hs.size(), "Wrong number of elements in system " + hs);

		for(Element e : hs.getElements()){
			if("MS".equals(e.getName())){
				assertEquals(3, e.getOutputs().length, 
					"Wrong number of outputs in multi-split.");
			}
		}

		PrintingObserver obs = new PrintingObserver();
		hs.simulate(obs);
		assertTrue(obs.getCount() >= 7, 
			"Expected at least 7 notifications but received just " + obs.getCount());
	}
}
