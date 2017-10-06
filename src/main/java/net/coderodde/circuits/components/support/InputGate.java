package net.coderodde.circuits.components.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.coderodde.circuits.components.AbstractCircuitComponent;
import net.coderodde.circuits.components.AbstractSingleInputPinCircuitComponent;

/**
 * This class implements an input pin of a circuit.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Oct 6, 2017)
 */
public final class InputGate extends AbstractSingleInputPinCircuitComponent {

    public static final boolean DEFAULT_BIT = false;
    
    private boolean bit;
    
    public InputGate(String name, boolean bit) {
        super(name);
        setBit(bit);
    }
    
    public InputGate(String name) {
        this(name, DEFAULT_BIT);
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

    @Override
    public List<AbstractCircuitComponent> getInputComponents() {
        if (input == null) {
            return Collections.<AbstractCircuitComponent>emptyList();
        }
        
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
