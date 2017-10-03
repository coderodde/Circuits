package net.coderodde.circuits.components.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.coderodde.circuits.components.AbstractCircuitComponent;
import net.coderodde.circuits.components.AbstractSingleInputPinCircuitComponent;

public final class BranchWire extends AbstractSingleInputPinCircuitComponent {

    private final Set<AbstractCircuitComponent> outputs = new HashSet<>();
    
    public BranchWire() {
        super(null);
    }
    
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

    @Override
    public List<AbstractCircuitComponent> getInputComponents() {
        return Arrays.asList(input);
    }

    @Override
    public List<AbstractCircuitComponent> getOutputComponents() {
        return new ArrayList<>(outputs);
    }
}
