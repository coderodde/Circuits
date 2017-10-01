package net.coderodde.circuits.components.support;

import net.coderodde.circuits.components.AbstractSingleInputPinCircuitComponent;

public final class InputGate extends AbstractSingleInputPinCircuitComponent {

    public static final boolean DEFAULT_BIT = false;
    
    private boolean bit;
    
    
    
    public InputGate(String name, boolean bit) {
        super(name);
        setBit(bit);
    }
    
    public InputGate(String name) {
        this(name, DEFAULT_BIT);
    }
    
    public boolean getBit() {
        return bit;
    }
    
    public void setBit(boolean bit) {
        this.bit = bit;
    }
    
    @Override
    public boolean doCycle() {
        if (getInputComponent() != null) {
            return getInputComponent().doCycle();
        }
        
        return bit;
    }
}
