package net.coderodde.circuits.components;

public abstract class AbstractCircuitComponent {

    protected AbstractCircuitComponent output;
    
    /**
     * This method simulates a cycle over this component.
     * 
     * @return the output from this component.
     */
    public abstract boolean doCycle();
    
    public AbstractCircuitComponent getOutputComponent() {
        return output;
    }
    
    public void setOutputComponent(AbstractCircuitComponent output) {
        this.output = output;
    }
}
