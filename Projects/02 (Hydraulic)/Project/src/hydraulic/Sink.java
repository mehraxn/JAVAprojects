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
	 * The invocation of method connect() on a sink has no effect.
	 */
	@Override
	public void connect(Element elem) {
		// Do nothing
	}

	/**
	 * For a sink it returns null.
	 */
	@Override
	public Element getOutput() {
		return null;
	}

	/**
	 * For a sink it returns an empty array.
	 */
	@Override
	public Element[] getOutputs() {
		return new Element[0];
	}
	
	@Override
	public void simulate(double inputFlow, SimulationObserver observer, boolean enableMaxFlowCheck) {
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
