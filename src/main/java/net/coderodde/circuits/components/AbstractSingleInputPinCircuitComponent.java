package net.coderodde.circuits.components;

public abstract class AbstractSingleInputPinCircuitComponent
extends AbstractCircuitComponent{

    protected AbstractCircuitComponent input;
    
    public AbstractCircuitComponent getInputComponent() {
        return input;
    }
    
    public void setInputComponent(AbstractCircuitComponent input) {
        this.input = input;
    }
}
