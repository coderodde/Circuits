package net.coderodde.circuits.components.support;

import java.util.List;
import net.coderodde.circuits.components.AbstractCircuitComponent;
import net.coderodde.circuits.components.AbstractSingleInputPinCircuitComponent;

/**
 * This gate always outputs zero.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Oct 3, 2017)
 */
public final class OffStubGate extends AbstractSingleInputPinCircuitComponent {

    public OffStubGate() {
        super(null);
    }
    
    @Override
    public boolean doCycle() {
        return false;
    }

    @Override
    public List<AbstractCircuitComponent> getInputComponents() {
        throw new UnsupportedOperationException(); 
    }

    @Override
    public List<AbstractCircuitComponent> getOutputComponents() {
        throw new UnsupportedOperationException();
    }
}
