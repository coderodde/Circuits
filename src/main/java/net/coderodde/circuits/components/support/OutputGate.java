package net.coderodde.circuits.components.support;

import net.coderodde.circuits.components.AbstractSingleInputPinCircuitComponent;

public final class OutputGate extends AbstractSingleInputPinCircuitComponent {
    
    public OutputGate(String name) {
        super(name);
    }
    
    @Override
    public boolean doCycle() {
        return input.doCycle();
    }
}
