package net.coderodde.circuits.components.support;

import net.coderodde.circuits.components.AbstractDoubleInputPinCircuitComponent;

/**
 * This logical gate implements the {@code or} operation, i.e., it outputs
 * {@code true} if either or both of the inputs are {@code true}.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Sep 20, 2017)
 */
public final class OrGate extends AbstractDoubleInputPinCircuitComponent {

    @Override
    public boolean doCycle() {
        return input1.doCycle() || input2.doCycle();
    }
}
