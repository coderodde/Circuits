package net.coderodde.circuits;

public final class IncompleteCircuitException extends RuntimeException {

    public IncompleteCircuitException(String message) {
        super(message);
    }
}
