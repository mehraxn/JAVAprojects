package hydraulic;

/**
 * Represents a tap that can interrupt the flow.
 * * The status of the tap is defined by the method
 * {@link #setOpen(boolean) setOpen()}.
 */

public class Tap extends Element {

	private boolean open;

	/**
	 * Constructor
	 * @param name name of the tap element
	 */
	public Tap(String name) {
		super(name);
		this.open = false; // Default to closed
	}

	/**
	 * Set whether the tap is open or not. The status is used during the simulation.
	 *
	 * @param open opening status of the tap
	 */
	public void setOpen(boolean open){
		this.open = open;
	}
	
	public boolean isOpen() {
		return this.open;
	}
	
	@Override
	public void simulate(double inputFlow, SimulationObserver observer, boolean enableMaxFlowCheck) {
		// (R7) Check max flow if enabled
		if (enableMaxFlowCheck && inputFlow > maxFlow) {
			observer.notifyFlowError("Tap", getName(), inputFlow, maxFlow);
		}
		
		// (R4) When a tap is open the output flow is equal to the input flow,
		// otherwise the output is zero.
		double outputFlow = 0.0;
		if (this.open) {
			outputFlow = inputFlow;
		}
		
		observer.notifyFlow("Tap", getName(), inputFlow, outputFlow);
		
		// Continue simulation downstream
		if (output != null) {
			output.simulate(outputFlow, observer, enableMaxFlowCheck);
		}
	}

	@Override
	public void layout(SimulationObserver observer, boolean enableMaxFlowCheck) {
		// Handled by the simulate() call from the upstream element.
	}
}