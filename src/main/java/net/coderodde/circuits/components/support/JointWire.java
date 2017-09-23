package net.coderodde.circuits.components.support;

import java.util.ArrayList;
import java.util.List;
import net.coderodde.circuits.components.AbstractCircuitComponent;
import net.coderodde.circuits.components.AbstractSingleInputPinCircuitComponent;

public final class JointWire extends AbstractSingleInputPinCircuitComponent {

    private final List<AbstractCircuitComponent> outputs = new ArrayList<>();
    
    @Override
    public boolean doCycle() {
        return input.doCycle();
    }
}
