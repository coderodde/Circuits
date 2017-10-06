package net.coderodde.circuits.components.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.coderodde.circuits.components.AbstractCircuitComponent;
import net.coderodde.circuits.components.AbstractDoubleInputPinCircuitComponent;

/**
 * This logical gate implements the {@code and} operation, i.e., it outputs
 * {@code true} only if both of the inputs are {@code true}.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Sep 20, 2017)
 */
public final class AndGate extends AbstractDoubleInputPinCircuitComponent {

    public AndGate(String name) {
        super(name);
    }
    
    @Override
    public boolean doCycle() {
        return input1.doCycle() && input2.doCycle();
    }

    @Override
    public List<AbstractCircuitComponent> getInputComponents() {
        return new ArrayList<>(Arrays.asList(input1, input2));
    }

    @Override
    public List<AbstractCircuitComponent> getOutputComponents() {
        if (output instanceof BranchWire) {
            return new ArrayList<>(((BranchWire) output).getOutputs());
        } else {
            return Arrays.asList(output);
        }
    }
}
