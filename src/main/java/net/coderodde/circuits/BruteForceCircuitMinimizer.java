package net.coderodde.circuits;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.coderodde.circuits.components.AbstractCircuitComponent;

public final class BruteForceCircuitMinimizer implements CircuitMinimizer {

    @Override
    public Circuit minimize(Circuit circuit) {
        Objects.requireNonNull(circuit, "The input circuit is null.");
        Map<String, AbstractCircuitComponent> componentMap = 
                circuit.getComponentMap();
        
        Set<AbstractCircuitComponent> reachableComponents = 
                findReachableComponents(circuit);
        return null;
    }
    
    private Set<AbstractCircuitComponent> 
        findReachableComponents(Circuit circuit) {
        return null;
    }
}
