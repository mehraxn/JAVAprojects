package hydraulic;

/**
 * Main class that acts as a container of the elements for
 * the simulation of a hydraulics system
 * 
 */
public class HSystem {

// R1
	/**
	 * Adds a new element to the system
	 * 
	 * @param elem the new element to be added to the system
	 */
	public void addElement(Element elem){
		//TODO: to be implemented
	}

	/**
	 * returns the number of elements currently present in the system
	 * 
	 * @return count of elements
	 */
	public int size() {
        // TODO: to be implemented
		return -1;
    }

	/**
	 * returns the element added so far to the system
	 * 
	 * @return an array of elements whose length is equal to 
	 * 							the number of added elements
	 */
	public Element[] getElements(){
		//TODO: to be implemented
		return null;
	}

// R4
	/**
	 * Starts the simulation of the system.
	 * The notifications about the simulations are sent
	 * to an observer object
	 * Before starting simulation, the parameters of the
	 * elements in the system must be defined
	 * 
	 * @param observer the observer receiving notifications
	 */
	public void simulate(SimulationObserver observer){
		//TODO: to be implemented
	}


// R6
	/**
	 * Deletes a previously added element 
	 * with the given name from the system
	 */
	public boolean deleteElement(String name) {
		//TODO: to be implemented
		return false;
	}

// R7
	/**
	 * Starts the simulation of the system; if {@code enableMaxFlowCheck} is {@code true},
	 * checks also the elements maximum flows against the input flow.
	 * If {@code enableMaxFlowCheck} is {@code false}  a normal simulation as
	 * the method {@link #simulate(SimulationObserver)} is performed.
	 * Before performing a checked simulation, the max flows of the elements in the
	 * system must be defined.
	 */
	public void simulate(SimulationObserver observer, boolean enableMaxFlowCheck) {
		//TODO: to be implemented
	}

// R8
	/**
	 * creates a new builder that can be used to create a 
	 * hydraulic system through a fluent API 
	 * 
	 * @return the builder object
	 */
    public static HBuilder build() {
		return new HBuilder();
    }
}
