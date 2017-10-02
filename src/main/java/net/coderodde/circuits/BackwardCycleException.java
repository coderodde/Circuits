package net.coderodde.circuits;

public final class BackwardCycleException extends RuntimeException {

    public BackwardCycleException(String message) {
        super(message);
    }
}
