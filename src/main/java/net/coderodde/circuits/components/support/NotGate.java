package net.coderodde.circuits.components.support;

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
}
