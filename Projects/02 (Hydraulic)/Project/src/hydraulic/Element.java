package hydraulic;

/**
 * Represents the generic abstract element of an hydraulics system.
 * It is the base class for all elements.
 *
 * Any element can be connect to a downstream element
 * using the method {@link #connect(Element) connect()}.
 * * The class is abstract since it is not intended to be instantiated,
 * though all methods are defined to make subclass implementation easier.
 */
public abstract class Element {
	
	private String name;
	protected Element output;
	protected double maxFlow = Double.POSITIVE_INFINITY; // R7: Default is unlimited
	
	public Element(String name) {
		this.name = name;
	}

	/**
	 * getter method for the name of the element
	 * * @return the name of the element
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Connects this element to a given element.
	 * The given element will be connected downstream of this element
	 * * In case of element with multiple outputs this method operates on the first one,
	 * it is equivalent to calling {@code connect(elem,0)}. 
	 * * @param elem the element that will be placed downstream
	 */
	public void connect(Element elem) {
		this.output = elem;
	}
	
	/**
	 * Connects a specific output of this element to a given element.
	 * The given element will be connected downstream of this element
	 * * @param elem the element that will be placed downstream
	 * @param index the output index that will be used for the connection
	 */
	public void connect(Element elem, int index){
		// By default, this method does nothing
		// Only overridden by elements with multiple outputs
		if (index == 0) {
			this.connect(elem);
		}
	}
	
	/**
	 * Retrieves the single element connected downstream of this element
	 * * @return downstream element
	 */
	public Element getOutput(){
		return this.output;
	}

	/**
	 * Retrieves the elements connected downstream of this element
	 * * @return downstream element
	 */
	public Element[] getOutputs(){
		// For simple elements, return an array containing the single output
		if (this.output == null) {
			return new Element[0]; // No outputs
		}
		Element[] outputs = { this.output };
		return outputs;
	}
	
	/**
	 * Defines the maximum input flow acceptable for this element
	 * * @param maxFlow maximum allowed input flow
	 */
	public void setMaxFlow(double maxFlow) {
		this.maxFlow = maxFlow;
	}

	// --- Simulation methods (to be called by HSystem) ---

	/**
	 * (Used by HSystem)
	 * Computes the output flow(s) and notifies the observer.
	 * This method must be implemented by concrete subclasses.
	 * * @param inputFlow
	 * @param observer
	 * @param enableMaxFlowCheck
	 */
	public abstract void simulate(double inputFlow, SimulationObserver observer, boolean enableMaxFlowCheck);

	/**
	 * (Used by HSystem)
	 * Recursively simulates the downstream elements.
	 * * @param observer
	 * @param enableMaxFlowCheck
	 */
	public abstract void layout(SimulationObserver observer, boolean enableMaxFlowCheck);
	
	// ---
	
	protected static String pad(String current, String down){
		int n = current.length();
		final String fmt = "\n%"+n+"s";
		return current + down.replace("\n", fmt.formatted("") );
	}

	@Override
	public String toString(){
		String res = "[%s] ".formatted(getName());
		Element[] out = getOutputs();
		if( out != null && out.length > 0){
			StringBuilder buffer = new StringBuilder();
			for(int i=0; i<out.length; ++i) {
				if(i>0) buffer.append("\n");
				if (out[i] == null) buffer.append("+-> *");
				else buffer.append(pad("+-> ", out[i].toString()));
			}
			res = pad(res,buffer.toString());
		}
		return res;
	}

}