package hydraulic;

/**
 * Main class that acts as a container of the elements for
 * the simulation of a hydraulics system
 * */
public class HSystem {
    
    // (R1) You can safely assume that the maximum number of elements is 100.
    private static final int MAX_ELEMENTS = 100;
    private Element[] elements = new Element[MAX_ELEMENTS];
    private int elementCount = 0;

    // R1
    /**
     * Adds a new element to the system
     * @param elem the new element to be added to the system
     */
    public void addElement(Element elem){
        if (elementCount < MAX_ELEMENTS) {
            elements[elementCount++] = elem;
        }
        // Optional: else throw an exception
    }

    /**
     * returns the number of elements currently present in the system
     * @return count of elements
     */
    public int size() {
        return elementCount;
    }

    /**
     * returns the element added so far to the system
     * @return an array of elements whose length is equal to 
     * the number of added elements
     */
    public Element[] getElements(){
        // (R1) an array containing all and only the elements
        Element[] currentElements = new Element[elementCount];
        for (int i = 0; i < elementCount; i++) {
            currentElements[i] = elements[i];
        }
        return currentElements;
    }

    // R4
    /**
     * Starts the simulation of the system.
     * The notifications about the simulations are sent
     * to an observer object
     * Before starting simulation, the parameters of the
     * elements in the system must be defined
     * @param observer the observer receiving notifications
     */
    public void simulate(SimulationObserver observer){
        // (R4) This method performs the flow computations for each element
        // (R7) a normal simulation
        simulate(observer, false);
    }

    // R6
    /**
     * Deletes a previously added element 
     * with the given name from the system
     */
    public boolean deleteElement(String name) {
        Element target = getElementByName(name);
        if (target == null) {
            return true; // Element not found
        }

        // (R6) If Split or Multisplit with > 1 output, no operation.
        if (target instanceof Split) { // Also catches Multisplit
            Element[] outputs = target.getOutputs();
            int connectedOutputs = 0;
            for (Element e : outputs) {
                if (e != null) connectedOutputs++;
            }
            if (connectedOutputs > 1) {
                return false;
            }
        }
        
        // Find upstream element
        Element upstream = findUpstreamElement(target);
        
        // Find downstream element (only for 0 or 1 output)
        Element downstream = null;
        if (target.getOutputs().length > 0) {
            downstream = target.getOutputs()[0];
        }
        
        // Rewire
        if (upstream != null) {
            Element[] upstreamOutputs = upstream.getOutputs();
            for (int i = 0; i < upstreamOutputs.length; i++) {
                if (upstreamOutputs[i] == target) {
                    upstream.connect(downstream, i);
                    break;
                }
            }
        }
        
        // Remove from system array
        int targetIndex = -1;
        for (int i = 0; i < elementCount; i++) {
            if (elements[i] == target) {
                targetIndex = i;
                break;
            }
        }
        
        if (targetIndex != -1) {
            for (int i = targetIndex; i < elementCount - 1; i++) {
                elements[i] = elements[i+1];
            }
            elements[elementCount - 1] = null;
            elementCount--;
        }

        return true;
    }

    // Helper for R6
    private Element getElementByName(String name) {
        for (int i = 0; i < elementCount; i++) {
            if (elements[i].getName().equals(name)) {
                return elements[i];
            }
        }
        return null;
    }
    
    // Helper for R6
    private Element findUpstreamElement(Element target) {
        for (int i = 0; i < elementCount; i++) {
            Element[] outputs = elements[i].getOutputs();
            for (Element output : outputs) {
                if (output == target) {
                    return elements[i];
                }
            }
        }
        return null;
    }

    // R7
    /**
     * Starts the simulation of the system; if {@code enableMaxFlowCheck} is {@code true},
     * checks also the elements maximum flows.
     */
    public void simulate(SimulationObserver observer, boolean enableMaxFlowCheck) {
        // (R4) Find the source
        Source source = null;
        for (int i = 0; i < elementCount; i++) {
            if (elements[i] instanceof Source) {
                source = (Source) elements[i];
                break;
            }
        }
        
        if (source != null) {
            // Notify source
            observer.notifyFlow("Source", source.getName(), SimulationObserver.NO_FLOW, source.getFlow());
            
            // Start recursive simulation
            Element firstElement = source.getOutput();
            if (firstElement != null) {
                firstElement.simulate(source.getFlow(), observer, enableMaxFlowCheck);
            }
        }
    }

    // R8
    /**
     * creates a new builder that can be used to create a 
     * hydraulic system through a fluent API 
     * @return the builder object
     */
    public static HBuilder build() {
        return new HBuilder();
    }
}
