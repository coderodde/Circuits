package net.coderodde.circuits.components.support;

import net.coderodde.circuits.components.AbstractSingleInputPinCircuitComponent;

public final class OutputGate extends AbstractSingleInputPinCircuitComponent {
    
    @Override
    public boolean doCycle() {
        return input.doCycle();
    }
}
