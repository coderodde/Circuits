package net.coderodde.circuits.components.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.coderodde.circuits.components.AbstractCircuitComponent;
import net.coderodde.circuits.components.AbstractSingleInputPinCircuitComponent;

/**
 * This logical gate inverses the input signal, i.e., it outputs {@code true} 
 * when the input is {@code false}, and vice versa.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Sep 20, 2017)
 */
public final class NotGate extends AbstractSingleInputPinCircuitComponent {

    public NotGate(String name) {
        super(name);
    }
    
    @Override
    public boolean doCycle() {
        return !input.doCycle();
    }

    @Override
    public List<AbstractCircuitComponent> getInputComponents() {
        return Arrays.asList(input);
    }

    @Override
    public List<AbstractCircuitComponent> getOutputComponents() {
        if (output instanceof BranchWire) {
            return new ArrayList<>(((BranchWire) output).getOutputs());
        }
        
        return Arrays.asList(output);
    }
}
