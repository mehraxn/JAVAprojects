package hydraulic;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Main class that acts as a container of the elements for
 * the simulation of a hydraulics system
 * */
public class HSystem {
    
    private static final int MAX_ELEMENTS = 100;
    private final Element[] elements = new Element[MAX_ELEMENTS];
    private int elementCount = 0;

    /**
     * Adds a new element to the system
     * @param elem the new element to be added to the system
     */
    public void addElement(Element elem){
        if (elem == null) {
            throw new IllegalArgumentException("element cannot be null");
        }
        if (getElementByName(elem.getName()) != null) {
            throw new IllegalArgumentException("duplicate element name: " + elem.getName());
        }
        if (elementCount == MAX_ELEMENTS) {
            throw new IllegalStateException("system capacity of " + MAX_ELEMENTS + " elements reached");
        }
        elements[elementCount++] = elem;
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
        Element[] currentElements = new Element[elementCount];
        System.arraycopy(elements, 0, currentElements, 0, elementCount);
        return currentElements;
    }

    /**
     * Starts the simulation of the system.
     * The notifications about the simulations are sent
     * to an observer object
     * Before starting simulation, the parameters of the
     * elements in the system must be defined
     * @param observer the observer receiving notifications
     */
    public void simulate(SimulationObserver observer){
        simulate(observer, false);
    }

    /**
     * Deletes a previously added element 
     * with the given name from the system
     */
    public boolean deleteElement(String name) {
        Element target = getElementByName(name);
        if (target == null) {
            return false;
        }

        Element downstream = null;
        int connectedOutputs = 0;
        for (Element candidate : target.internalOutputs()) {
            if (candidate != null) {
                downstream = candidate;
                connectedOutputs++;
            }
        }
        if (connectedOutputs > 1) {
            return false;
        }

        for (int i = 0; i < elementCount; i++) {
            if (elements[i] != target) {
                elements[i].replaceOutput(target, downstream);
            }
        }

        int targetIndex = -1;
        for (int i = 0; i < elementCount; i++) {
            if (elements[i] == target) {
                targetIndex = i;
                break;
            }
        }
        
        for (int i = targetIndex; i < elementCount - 1; i++) {
            elements[i] = elements[i + 1];
        }
        elements[--elementCount] = null;

        return true;
    }

    private Element getElementByName(String name) {
        for (int i = 0; i < elementCount; i++) {
            if (elements[i].getName().equals(name)) {
                return elements[i];
            }
        }
        return null;
    }

    /**
     * Starts the simulation of the system; if {@code enableMaxFlowCheck} is {@code true},
     * checks also the elements maximum flows.
     */
    public void simulate(SimulationObserver observer, boolean enableMaxFlowCheck) {
        if (observer == null) {
            throw new IllegalArgumentException("observer cannot be null");
        }
        Source source = null;
        for (int i = 0; i < elementCount; i++) {
            if (elements[i] instanceof Source) {
                source = (Source) elements[i];
                break;
            }
        }
        
        if (source != null) {
            observer.notifyFlow("Source", source.getName(), SimulationObserver.NO_FLOW, source.getFlow());
            Element firstElement = source.getOutput();
            if (firstElement != null) {
                firstElement.simulate(source.getFlow(), observer, enableMaxFlowCheck);
            }
        }
    }

    /**
     * creates a new builder that can be used to create a 
     * hydraulic system through a fluent API 
     * @return the builder object
     */
    public static HBuilder build() {
        return new HBuilder();
    }

    @Override
    public String toString() {
        Source source = null;
        for (int i = 0; i < elementCount; i++) {
            if (elements[i] instanceof Source) {
                source = (Source) elements[i];
                break;
            }
        }
        if (source != null) {
            StringBuilder result = new StringBuilder();
            appendLayout(source, result, "", new IdentityHashMap<>());
            return result.toString();
        }

        StringJoiner names = new StringJoiner(", ", "[", "]");
        for (int i = 0; i < elementCount; i++) {
            names.add(elements[i].getName());
        }
        return names.toString();
    }

    private static void appendLayout(Element element, StringBuilder result, String indent,
            Map<Element, Boolean> visited) {
        result.append(indent).append('[').append(element.getName()).append(']');
        if (visited.put(element, Boolean.TRUE) != null) {
            result.append(" (cycle)");
            return;
        }

        Element[] outputs = element.internalOutputs();
        for (int i = 0; i < outputs.length; i++) {
            result.append(System.lineSeparator()).append(indent).append("  +-> ");
            if (outputs[i] == null) {
                result.append('*');
            } else {
                appendLayout(outputs[i], result, indent + "      ", visited);
            }
        }
        visited.remove(element);
    }
}
