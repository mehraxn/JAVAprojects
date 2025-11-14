package hydraulic;

/**
 * Represents a source of water, i.e. the initial element for the simulation.
 *
 * Lo status of the source is defined through the method
 * {@link #setFlow(double) setFlow()}.
 */
public class Source extends Element {

	private double flow;

	/**
	 * constructor
	 * @param name name of the source element
	 */
	public Source(String name) {
		super(name);
	}

	/**
	 * Define the flow of the source to be used during the simulation
	 *
	 * @param flow flow of the source (in cubic meters per hour)
	 */
	public void setFlow(double flow){
		this.flow = flow;
	}
	
	public double getFlow() {
		return this.flow;
	}
	
	/**
	 * (R7) For Source objects, calls to the setMaxFlow() method
	 * should not have any effect.
	 */
	@Override
	public void setMaxFlow(double maxFlow) {
		// Do nothing
	}

	@Override
	public void simulate(double inputFlow, SimulationObserver observer, boolean enableMaxFlowCheck) {
		// Source has no input flow, but its output flow is what was set
		double outputFlow = this.flow;
		observer.notifyFlow("Source", getName(), SimulationObserver.NO_FLOW, outputFlow);
		
		// Continue simulation downstream
		if (output != null) {
			output.simulate(outputFlow, observer, enableMaxFlowCheck);
		}
	}

	@Override
	public void layout(SimulationObserver observer, boolean enableMaxFlowCheck) {
		// This is the starting point, layout is called from HSystem
		// We call simulate on the element *it* is connected to.
		if (output != null) {
			output.simulate(this.flow, observer, enableMaxFlowCheck);
		}
	}
}