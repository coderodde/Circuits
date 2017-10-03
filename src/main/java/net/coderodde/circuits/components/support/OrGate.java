package net.coderodde.circuits.components.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.coderodde.circuits.components.AbstractCircuitComponent;
import net.coderodde.circuits.components.AbstractDoubleInputPinCircuitComponent;

/**
 * This logical gate implements the {@code or} operation, i.e., it outputs
 * {@code true} if either or both of the inputs are {@code true}.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Sep 20, 2017)
 */
public final class OrGate extends AbstractDoubleInputPinCircuitComponent {

    public OrGate(String name) {
        super(name);
    }
    
    @Override
    public boolean doCycle() {
        return input1.doCycle() || input2.doCycle();
    }

    @Override
    public List<AbstractCircuitComponent> getInputComponents() {
        return Arrays.asList(input1, input2);
    }

    @Override
    public List<AbstractCircuitComponent> getOutputComponents() {
        if (output instanceof BranchWire) {
            return new ArrayList<>(((BranchWire) output).getOutputs());
        }
        
        return Arrays.asList(output);
    }
}
