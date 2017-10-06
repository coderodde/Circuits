package net.coderodde.circuits;

/**
 * Defines an exception thrown whenever there is a directed cycle in the circuit
 * whenever starting from input gates and moving towards output components.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Oct 6, 2017)
 */
public final class ForwardCycleException extends RuntimeException {

    public ForwardCycleException(String message) {
        super(message);
    }
}
