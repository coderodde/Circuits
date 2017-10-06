package net.coderodde.circuits;

/**
 * Defines an exception thrown whenever there is a component missing input/
 * output components.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Oct 6, 2017)
 */
public final class IncompleteCircuitException extends RuntimeException {

    public IncompleteCircuitException(String message) {
        super(message);
    }
}
