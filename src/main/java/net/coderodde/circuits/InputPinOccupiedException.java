package net.coderodde.circuits;

/**
 * Defines an exception thrown whenever trying to connect a component to an
 * input of other component that is already occupied.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Oct 6, 2017)
 */
public final class InputPinOccupiedException extends RuntimeException {

    public InputPinOccupiedException(String message) {
        super(message);
    }
}
