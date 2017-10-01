package net.coderodde.circuits.components.support;

import net.coderodde.circuits.components.AbstractSingleInputPinCircuitComponent;

public final class InputGate extends AbstractSingleInputPinCircuitComponent {

    public static final boolean DEFAULT_BIT = false;
    
    private boolean bit;
    
    public InputGate(boolean bit) {
        setBit(bit);
    }
    
    public InputGate() {
        this(DEFAULT_BIT);
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
