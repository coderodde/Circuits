package net.coderodde.circuits;

/**
 * Defines an exception thrown whenever there is a directed cycle in the circuit
 * whenever starting from output gates and moving towards input components.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Oct 6, 2017)
 */
public final class BackwardCycleException extends RuntimeException {

    public BackwardCycleException(String message) {
        super(message);
    }
}
