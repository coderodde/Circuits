package net.coderodde.circuits.components;

import java.util.List;

public abstract class AbstractCircuitComponent {

    private final String name;
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
