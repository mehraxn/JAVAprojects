package hydraulic;

/**
 * Represents a split element, a.k.a. T element
 * * During the simulation each downstream element will
 * receive a stream that is half the input stream of the split.
 */

public class Split extends Element {

	protected Element[] outputs = new Element[2];
	
	/**
	 * Constructor
	 * @param name name of the split element
	 */
	public Split(String name) {
		super(name);
	}
	
	/**
	 * (R2) In case of element with multiple outputs this method operates on the first one,
	 * it is equivalent to calling {@code connect(elem,0)}.
	 */
	@Override
	public void connect(Element elem) {
		this.connect(elem, 0);
	}
	
	/**
	 * (R3) connect() method accepts an additional argument specifying which output
	 */
	@Override
	public void connect(Element elem, int index) {
		if (index == 0 || index == 1) {
			this.outputs[index] = elem;
		}
		// Optional: else throw an exception
	}

	/**
	 * (R3) returns an array with the two connected elements.
	 */
	@Override
	public Element[] getOutputs() {
		return this.outputs;
	}
	
	@Override
	public void simulate(double inputFlow, SimulationObserver observer, boolean enableMaxFlowCheck) {
		// (R7) Check max flow if enabled
		if (enableMaxFlowCheck && inputFlow > maxFlow) {
			observer.notifyFlowError("Split", getName(), inputFlow, maxFlow);
		}
		
		// (R4) For the T split the input flow is divided into two equal output flows
		double outputFlow = inputFlow / 2.0;
		
		observer.notifyFlow("Split", getName(), inputFlow, outputFlow, outputFlow);
		
		// Continue simulation downstream for both outputs
		if (outputs[0] != null) {
			outputs[0].simulate(outputFlow, observer, enableMaxFlowCheck);
		}
		if (outputs[1] != null) {
			outputs[1].simulate(outputFlow, observer, enableMaxFlowCheck);
		}
	}

	@Override
	public void layout(SimulationObserver observer, boolean enableMaxFlowCheck) {
		// Handled by the simulate() call from the upstream element.
	}
}