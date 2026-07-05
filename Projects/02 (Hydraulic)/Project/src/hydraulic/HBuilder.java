package hydraulic;

// No imports - as requested.

/**
 * Hydraulics system builder providing a fluent API
 */
public class HBuilder {
	
	private HSystem system = new HSystem();
	private Element lastElement;
	
	// (R8) Custom stack implementation using an array
    // Replaces java.util.Stack to avoid imports.
    // Assumes a maximum nesting depth of 10 for splits, which is reasonable.
	private Element[] splitStack = new Element[10];
	private int splitStackTop = -1; // -1 means stack is empty
	
	private int outputIndex = 0;

    /**
     * Add a source element with the given name
     * * @param name name of the source element to be added
     * @return the builder itself for chaining 
     */
    public HBuilder addSource(String name) {
        Source source = new Source(name);
        system.addElement(source);
        lastElement = source;
        return this;
    }

    /**
     * returns the hydraulic system built with the previous operations
     * * @return the hydraulic system
     */
    public HSystem complete() {
        return this.system;
    }
    
	/**
	 * [FIXED]
	 * Helper method to link a new element.
	 * Now correctly handles chaining vs. branching.
	 */
	private HBuilder link(Element newElement) {
		system.addElement(newElement);

		boolean connectedToSplit = false;
        // Check if our custom stack is not empty
		if (splitStackTop != -1) { 
            // Peek at the top element
			Element currentSplit = splitStack[splitStackTop]; 
			
			// Only connect to split if lastElement IS the split
			// (i.e., we are at the start of a new branch)
			if (lastElement == currentSplit) { 
				currentSplit.connect(newElement, outputIndex);
				connectedToSplit = true;
			}
		}

		// If we're not starting a branch, continue the chain
		if (!connectedToSplit && lastElement != null) {
			lastElement.connect(newElement);
		}

		lastElement = newElement; // Advance the chain
		return this;
	}


    /**
     * creates a new tap and links it to the previous element
     * * @param name name of the tap
     * @return the builder itself for chaining 
     */
    public HBuilder linkToTap(String name) {
        return link(new Tap(name));
    }

    /**
     * creates a sink and link it to the previous element
     * @param name name of the sink
     * @return the builder itself for chaining 
     */
    public HBuilder linkToSink(String name) {
        return link(new Sink(name));
    }

    /**
     * creates a split and links it to the previous element
     * @param name of the split
     * @return the builder itself for chaining 
     */
    public HBuilder linkToSplit(String name) {
        return link(new Split(name));
    }

    /**
     * creates a multisplit and links it to the previous element
     * @param name name of the multisplit
     * @param numOutput the number of output of the multisplit
     * @return the builder itself for chaining 
     */
    public HBuilder linkToMultisplit(String name, int numOutput) {
        return link(new Multisplit(name, numOutput));
    }

    /**
     * introduces the elements connected to the first output 
     * of the latest split/multisplit.
     */
    public HBuilder withOutputs() {
        // "Push" onto our custom stack
        splitStackTop++;
        splitStack[splitStackTop] = lastElement;
        
        outputIndex = 0;
        return this;     
    }

    /**
	 * [FIXED]
     * inform the builder that the next element will be
     * linked to the successive output of the previous split or multisplit.
     */
    public HBuilder then() {
        outputIndex++;
		// This line is crucial: it resets the chain back to the split
        // "Peek" at the top of our custom stack
        lastElement = splitStack[splitStackTop]; 
        return this;
    }

    /**
     * completes the definition of elements connected
     * to outputs of a split/multisplit. 
     */
    public HBuilder done() {
    	// (R8) ...after it is called the last node is the one 
    	// upstream the (multi)split
        
        // "Pop" from our custom stack
        lastElement = splitStack[splitStackTop];
        splitStack[splitStackTop] = null; // Clean up
        splitStackTop--;
        
        outputIndex = 0;
        return this;
    }

    /**
     * define the flow of the previous source
     * * @param flow flow used in the simulation
     * @return the builder itself for chaining 
     */
    public HBuilder withFlow(double flow) {
        if (lastElement instanceof Source) {
			((Source) lastElement).setFlow(flow);
		}
        return this;
    }

    /**
     * define the status of a tap as open,
     * it will be used in the simulation
     * * @return the builder itself for chaining 
     */
    public HBuilder open() {
        if (lastElement instanceof Tap) {
			((Tap) lastElement).setOpen(true);
		}
        return this;
    }

    /**
     * define the status of a tap as closed,
     * it will be used in the simulation
     * * @return the builder itself for chaining 
     */
    public HBuilder closed() {
        if (lastElement instanceof Tap) {
			((Tap) lastElement).setOpen(false);
		}
        return this;
    }

    /**
     * define the proportions of input flow distributed
     * to each output of the preceding a multisplit
     * * @param props the proportions
     * @return the builder itself for chaining 
     */
    public HBuilder withPropotions(double... props) {
		if (lastElement instanceof Multisplit) {
			((Multisplit) lastElement).setProportions(props);
		}
        return this;
    }

    /**
     * define the maximum flow theshold for the previous element
     * * @param max flow threshold
     * @return the builder itself for chaining 
     */
    public HBuilder maxFlow(double max) {
        if (lastElement != null) {
			lastElement.setMaxFlow(max);
		}
        return this;
    }
}