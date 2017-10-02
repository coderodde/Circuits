package net.coderodde.circuits;

public final class ForwardCycleException extends RuntimeException {

    public ForwardCycleException(String message) {
        super(message);
    }
}
