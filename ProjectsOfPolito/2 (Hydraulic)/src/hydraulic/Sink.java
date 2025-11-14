package hydraulic;

/**
 * Represents the sink, i.e. the terminal element of a system
 *
 */
public class Sink extends Element {

	/**
	 * Constructor
	 * @param name name of the sink element
	 */
	public Sink(String name) {
		super(name);
	}

	/**
	 * (R2) The invocation of method connect() on a Sink object has no effect.
	 */
	@Override
	public void connect(Element elem) {
		// Do nothing
	}

	/**
	 * (R2) For a Sink it returns null.
	 */
	@Override
	public Element getOutput() {
		return null;
	}

	/**
	 * (R2) For a Sink it returns null or empty.
	 */
	@Override
	public Element[] getOutputs() {
		return new Element[0]; // Sink has no outputs
	}
	
	@Override
	public void simulate(double inputFlow, SimulationObserver observer, boolean enableMaxFlowCheck) {
		// (R7) Check max flow if enabled
		if (enableMaxFlowCheck && inputFlow > maxFlow) {
			observer.notifyFlowError("Sink", getName(), inputFlow, maxFlow);
		}
		
		// Sink is a terminal element
		observer.notifyFlow("Sink", getName(), inputFlow, SimulationObserver.NO_FLOW);
		// No downstream elements to simulate
	}

	@Override
	public void layout(SimulationObserver observer, boolean enableMaxFlowCheck) {
		// Terminal element, layout stops here.
		// Handled by the simulate() call from the upstream element.
	}
}