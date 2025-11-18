package hydraulic;

/**
 * Represents a multisplit element, an extension of the Split that allows many outputs
 * * During the simulation each downstream element will
 * receive a stream that is determined by the proportions.
 */

public class Multisplit extends Split {

	private double[] proportions;
	
	/**
	 * Constructor
	 * @param name the name of the multi-split element
	 * @param numOutput the number of outputs
	 */
	public Multisplit(String name, int numOutput) {
		super(name);
		// (R5) Constructor accepts... the number of outputs.
		this.outputs = new Element[numOutput];
		this.proportions = new double[numOutput];
		
		// Default to equal proportions if not set
		if (numOutput > 0) {
			double p = 1.0 / numOutput;
			for (int i = 0; i < numOutput; i++) {
				this.proportions[i] = p;
			}
		}
	}
	
	/**
	 * (R5) Connects a specific output
	 */
	@Override
	public void connect(Element elem, int index) {
		if (index >= 0 && index < outputs.length) {
			this.outputs[index] = elem;
		}
		// Optional: else throw an exception
	}
	
	/**
	 * (R5) returns an array of the connected elements
	 */
	@Override
	public Element[] getOutputs() {
		return this.outputs;
	}
	
	/**
	 * Define the proportion of the output flows w.r.t. the input flow.
	 * * The sum of the proportions should be 1.0 and 
	 * the number of proportions should be equals to the number of outputs.
	 * Otherwise a check would detect an error.
	 * * @param proportions the proportions of flow for each output
	 */
	public void setProportions(double... proportions) {
		// (R5) Assume that the number of proportions... is equal to the number of outputs
		this.proportions = proportions;
	}
	
	public double[] getProportions() {
		return this.proportions;
	}
	
	@Override
	public void simulate(double inputFlow, SimulationObserver observer, boolean enableMaxFlowCheck) {
		// (R7) Check max flow if enabled
		if (enableMaxFlowCheck && inputFlow > maxFlow) {
			observer.notifyFlowError("Multisplit", getName(), inputFlow, maxFlow);
		}
		
		// (R5) ...divide the input flow among the outputs.
		double[] outputFlows = new double[outputs.length];
		for (int i = 0; i < outputs.length; i++) {
			outputFlows[i] = inputFlow * proportions[i];
		}
		
		observer.notifyFlow("Multisplit", getName(), inputFlow, outputFlows);
		
		// Continue simulation downstream for all outputs
		for (int i = 0; i < outputs.length; i++) {
			if (outputs[i] != null) {
				outputs[i].simulate(outputFlows[i], observer, enableMaxFlowCheck);
			}
		}
	}
}