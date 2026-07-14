package hydraulic;

import java.util.Arrays;

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
		ValidationUtils.requirePositive(numOutput, "output count");
		this.outputs = new Element[numOutput];
		this.proportions = new double[numOutput];
		
		double p = 1.0 / numOutput;
		for (int i = 0; i < numOutput; i++) {
			this.proportions[i] = p;
		}
	}
	
	/**
	 * Connects a specific output.
	 */
	@Override
	public void connect(Element elem, int index) {
		ValidationUtils.requireOutputIndex(index, outputs.length);
		this.outputs[index] = elem;
	}
	
	/**
	 * Returns an array of the connected elements.
	 */
	@Override
	public Element[] getOutputs() {
		return this.outputs.clone();
	}
	
	/**
	 * Define the proportion of the output flows w.r.t. the input flow.
	 * * The sum of the proportions should be 1.0 and 
	 * the number of proportions should be equals to the number of outputs.
	 * Otherwise a check would detect an error.
	 * * @param proportions the proportions of flow for each output
	 */
	public void setProportions(double... proportions) {
		this.proportions = ValidationUtils.requireProportions(proportions, outputs.length);
	}
	
	public double[] getProportions() {
		return Arrays.copyOf(this.proportions, this.proportions.length);
	}
	
	@Override
	public void simulate(double inputFlow, SimulationObserver observer, boolean enableMaxFlowCheck) {
		if (enableMaxFlowCheck && inputFlow > maxFlow) {
			observer.notifyFlowError("Multisplit", getName(), inputFlow, maxFlow);
		}
		
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
