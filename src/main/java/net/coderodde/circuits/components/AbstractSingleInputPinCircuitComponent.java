package net.coderodde.circuits.components;

/**
 * Defines the API for all components having only one input.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Oct 6, 2017)
 */
public abstract class AbstractSingleInputPinCircuitComponent
extends AbstractCircuitComponent{

    protected AbstractCircuitComponent input;
    
    public AbstractSingleInputPinCircuitComponent(String name) {
        super(name);
    }
    
    public AbstractCircuitComponent getInputComponent() {
        return input;
    }
    
    public void setInputComponent(AbstractCircuitComponent input) {
        this.input = input;
    }
}
