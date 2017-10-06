package net.coderodde.circuits.components;

import java.util.List;

/**
 * Defines the common API for all circuit components.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Oct 6, 2017)
 */
public abstract class AbstractCircuitComponent {

    private final String name;
    
    /**
     * Each component has a single output wire.
     */
    protected AbstractCircuitComponent output;
    
    public AbstractCircuitComponent(String name) {
        this.name = name;
    }
    
    /**
     * This method simulates a cycle over this component.
     * 
     * @return the output from this component.
     */
    public abstract boolean doCycle();
    
    public String getName() {
        return name;
    }
    
    public AbstractCircuitComponent getOutputComponent() {
        return output;
    }
    
    public void setOutputComponent(AbstractCircuitComponent output) {
        this.output = output;
    }
    
    public abstract List<AbstractCircuitComponent> getInputComponents();
    public abstract List<AbstractCircuitComponent> getOutputComponents();
}
