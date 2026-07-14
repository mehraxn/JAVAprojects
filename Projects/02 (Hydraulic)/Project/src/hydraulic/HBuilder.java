package hydraulic;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Hydraulics system builder providing a fluent API
 */
public class HBuilder {
	
	private final HSystem system = new HSystem();
	private Element lastElement;
	private final Deque<BuilderFrame> splitStack = new ArrayDeque<>();

	private static final class BuilderFrame {
		private final Element branchingElement;
		private final int outputCount;
		private int outputIndex;
		private boolean branchStarted;

		private BuilderFrame(Element branchingElement) {
			this.branchingElement = branchingElement;
			this.outputCount = branchingElement.internalOutputs().length;
		}
	}

    /**
     * Add a source element with the given name
     * * @param name name of the source element to be added
     * @return the builder itself for chaining 
     */
    public HBuilder addSource(String name) {
		if (lastElement != null || system.size() != 0) {
			throw new IllegalStateException("a source can only be added at the start of a builder");
		}
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
		if (system.size() == 0) {
			throw new IllegalStateException("cannot complete an empty hydraulic system");
		}
		for (BuilderFrame frame : splitStack) {
			if (!frame.branchStarted || frame.outputIndex != frame.outputCount - 1) {
				throw new IllegalStateException("all branching outputs must be defined before complete()");
			}
		}
        return this.system;
    }
    
	private HBuilder link(Element newElement) {
		if (lastElement == null) {
			throw new IllegalStateException("addSource() must be called before linking elements");
		}
		system.addElement(newElement);

		boolean connectedToSplit = false;
		if (!splitStack.isEmpty()) {
			BuilderFrame frame = splitStack.peek();
			if (lastElement == frame.branchingElement) {
				frame.branchingElement.connect(newElement, frame.outputIndex);
				frame.branchStarted = true;
				connectedToSplit = true;
			}
		}

		if (!connectedToSplit && lastElement != null) {
			lastElement.connect(newElement);
		}

		lastElement = newElement;
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
		if (!(lastElement instanceof Split)) {
			throw new IllegalStateException("withOutputs() requires a split or multisplit");
		}
		splitStack.push(new BuilderFrame(lastElement));
        return this;     
    }

    /**
     * inform the builder that the next element will be
     * linked to the successive output of the previous split or multisplit.
     */
    public HBuilder then() {
		BuilderFrame frame = requireFrame("then");
		if (!frame.branchStarted) {
			throw new IllegalStateException("then() requires an element on the current output");
		}
		if (frame.outputIndex + 1 >= frame.outputCount) {
			throw new IllegalStateException("all outputs of the current split are already defined");
		}
		frame.outputIndex++;
		frame.branchStarted = false;
		lastElement = frame.branchingElement;
        return this;
    }

    /**
     * completes the definition of elements connected
     * to outputs of a split/multisplit. 
     */
    public HBuilder done() {
		BuilderFrame frame = requireFrame("done");
		if (!frame.branchStarted) {
			throw new IllegalStateException("done() requires an element on the current output");
		}
		splitStack.pop();
		lastElement = frame.branchingElement;
        return this;
    }

	private BuilderFrame requireFrame(String operation) {
		if (splitStack.isEmpty()) {
			throw new IllegalStateException(operation + "() requires withOutputs()");
		}
		return splitStack.peek();
	}

    /**
     * define the flow of the previous source
     * * @param flow flow used in the simulation
     * @return the builder itself for chaining 
     */
    public HBuilder withFlow(double flow) {
		if (!(lastElement instanceof Source)) {
			throw new IllegalStateException("withFlow() requires the preceding element to be a source");
		}
		((Source) lastElement).setFlow(flow);
        return this;
    }

    /**
     * define the status of a tap as open,
     * it will be used in the simulation
     * * @return the builder itself for chaining 
     */
    public HBuilder open() {
		if (!(lastElement instanceof Tap)) {
			throw new IllegalStateException("open() requires the preceding element to be a tap");
		}
		((Tap) lastElement).setOpen(true);
        return this;
    }

    /**
     * define the status of a tap as closed,
     * it will be used in the simulation
     * * @return the builder itself for chaining 
     */
    public HBuilder closed() {
		if (!(lastElement instanceof Tap)) {
			throw new IllegalStateException("closed() requires the preceding element to be a tap");
		}
		((Tap) lastElement).setOpen(false);
        return this;
    }

    /**
     * define the proportions of input flow distributed
     * to each output of the preceding a multisplit
     * * @param props the proportions
     * @return the builder itself for chaining 
     */
    public HBuilder withPropotions(double... props) {
		if (!(lastElement instanceof Multisplit)) {
			throw new IllegalStateException(
					"withPropotions() requires the preceding element to be a multisplit");
		}
		((Multisplit) lastElement).setProportions(props);
        return this;
    }

	/**
	 * Correctly spelled alias for {@link #withPropotions(double...)}.
	 */
	public HBuilder withProportions(double... props) {
		return withPropotions(props);
	}

    /**
     * define the maximum flow theshold for the previous element
     * * @param max flow threshold
     * @return the builder itself for chaining 
     */
    public HBuilder maxFlow(double max) {
		if (lastElement == null) {
			throw new IllegalStateException("maxFlow() requires a preceding element");
		}
		lastElement.setMaxFlow(max);
        return this;
    }
}
