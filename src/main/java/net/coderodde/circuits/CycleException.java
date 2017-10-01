package net.coderodde.circuits;

public final class CycleException extends RuntimeException {

    public CycleException() {
        super("Found a cycle in the circuit.");
    }
}
