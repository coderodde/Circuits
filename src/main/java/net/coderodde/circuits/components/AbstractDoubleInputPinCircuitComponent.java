package net.coderodde.circuits.components;

public abstract class AbstractDoubleInputPinCircuitComponent 
extends AbstractCircuitComponent {

    protected AbstractCircuitComponent input1;
    protected AbstractCircuitComponent input2;
    
    public AbstractDoubleInputPinCircuitComponent(String name) {
        super(name);
    }
    
    public AbstractCircuitComponent getInputComponent1() {
        return input1;
    }
    
    public AbstractCircuitComponent getInputComponent2() {
        return input2;
    }
    
    public void setInputComponent1(AbstractCircuitComponent input1) {
        this.input1 = input1;
    }
    
    public void setInputComponent2(AbstractCircuitComponent input2) {
        this.input2 = input2;
    }
}
