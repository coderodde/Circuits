package net.coderodde.circuits;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.coderodde.circuits.components.AbstractCircuitComponent;

public final class BruteForceCircuitMinimizer implements CircuitMinimizer {

    @Override
    public void minimize(Circuit circuit) {
        Objects.requireNonNull(circuit, "The input circuit is null.");
        Map<String, AbstractCircuitComponent> componentMap = 
                circuit.getComponentMap();
        
        Set<AbstractCircuitComponent> reachableComponents = 
                findReachableComponents(circuit);
        
        Set<AbstractCircuitComponent> allComponents = 
                new HashSet<>(componentMap.values()); 
        
        for (AbstractCircuitComponent component : allComponents) {
            if (!reachableComponents.contains(component)) {
                componentMap.remove(component.getName());
            }
        }
    }
    
    private Set<AbstractCircuitComponent> 
        findReachableComponents(Circuit circuit) {
        Set<AbstractCircuitComponent> reachedComponents = 
                new HashSet<>(circuit.getComponentMap().size());
        
        Deque<AbstractCircuitComponent> queue = new ArrayDeque<>();
        queue.addAll(circuit.getOutputComponents());
        reachedComponents.addAll(queue);
        
        while (!queue.isEmpty()) {
            AbstractCircuitComponent component = queue.removeFirst();
            
            for (AbstractCircuitComponent inputComponent : 
                    component.getInputComponents()) {
                if (!reachedComponents.contains(inputComponent)) {
                    reachedComponents.add(inputComponent);
                    queue.addLast(inputComponent);
                }
            }
        }
        
        return reachedComponents;
    }
}
