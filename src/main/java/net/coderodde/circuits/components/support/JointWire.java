package net.coderodde.circuits.components.support;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.coderodde.circuits.components.AbstractCircuitComponent;
import net.coderodde.circuits.components.AbstractSingleInputPinCircuitComponent;

public final class JointWire extends AbstractSingleInputPinCircuitComponent {

    private final Set<AbstractCircuitComponent> outputs = new HashSet<>();
    
    @Override
    public boolean doCycle() {
        return input.doCycle();
    }
    
    public void connectTo(AbstractCircuitComponent circuitComponent) {
        outputs.add(circuitComponent);
    }
    
    public Set<AbstractCircuitComponent> getOutputs() {
        return Collections.unmodifiableSet(outputs);
    }
}
