package net.coderodde.circuits.components.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.coderodde.circuits.components.AbstractCircuitComponent;
import net.coderodde.circuits.components.AbstractSingleInputPinCircuitComponent;

/**
 * This class implements the output pins that remember one bit each.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Oct 6, 2017)
 */
public final class OutputGate extends AbstractSingleInputPinCircuitComponent {
    
    public OutputGate(String name) {
        super(name);
    }
    
    @Override
    public boolean doCycle() {
        return input.doCycle();
    }

    @Override
    public List<AbstractCircuitComponent> getInputComponents() {
        return Arrays.asList(input);
    }

    @Override
    public List<AbstractCircuitComponent> getOutputComponents() {
        if (output == null) {
            return Collections.<AbstractCircuitComponent>emptyList();
        }
        
        if (output instanceof BranchWire) {
            return new ArrayList<>(((BranchWire) output).getOutputs());
        }
        
        return Arrays.asList(output);
    }
}
