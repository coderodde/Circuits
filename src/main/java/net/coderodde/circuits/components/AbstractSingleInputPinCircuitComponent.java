package net.coderodde.circuits.components;

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
