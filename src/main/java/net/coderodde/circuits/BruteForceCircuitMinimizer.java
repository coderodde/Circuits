package net.coderodde.circuits;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.coderodde.circuits.components.AbstractCircuitComponent;
import net.coderodde.circuits.components.AbstractDoubleInputPinCircuitComponent;
import net.coderodde.circuits.components.AbstractSingleInputPinCircuitComponent;
import net.coderodde.circuits.components.support.AndGate;
import net.coderodde.circuits.components.support.BranchWire;
import net.coderodde.circuits.components.support.NotGate;
import net.coderodde.circuits.components.support.OffStubGate;

public final class BruteForceCircuitMinimizer implements CircuitMinimizer {

    @Override
    public void minimize(Circuit circuit) {
        Objects.requireNonNull(circuit, "The input circuit is null.");
        Map<String, AbstractCircuitComponent> componentMap = 
                circuit.getComponentMap();
        
        removeUnreachableComponents(circuit);
        minimizeAllSubcircuits(componentMap);
        doMinimize(circuit);
    }
    
    private void doMinimize(Circuit circuit) {
        Map<String, AbstractCircuitComponent> componentMap = 
                circuit.getComponentMap();
        
        while (true) {
            if (tryRemoveSingleOutputBranchWire(circuit)) {
                continue;
            }
            
            if (tryRemoveOneNotToAnd1(circuit)) {
                continue;
            }
            
            break;
        }
    }
    
    private boolean tryRemoveOneNotToAnd1Matches(
            AbstractCircuitComponent candidateComponent) {
        if (!(candidateComponent instanceof AndGate)) {
            return false;
        }
        
        AndGate andGate = (AndGate) candidateComponent;
        
        if (!(andGate.getInputComponent1() instanceof NotGate)) {
            return false;
        }
        
        if (andGate.getInputComponent2() instanceof NotGate) {
            // Can be minimized but in the another place.
            return false;
        }
        
        NotGate notGate = (NotGate) andGate.getInputComponent1();
        
        return notGate.getInputComponent() == andGate.getInputComponent2();
    }
    
    private boolean tryRemoveOneNotToAnd1(Circuit circuit) {
        AndGate targetAndGate = null;
        
        for (AbstractCircuitComponent component :
                circuit.getComponentMap().values()) {
            if (tryRemoveOneNotToAnd1Matches(component)) {
                targetAndGate = (AndGate) component;
                break;
            }
        }
        
        if (targetAndGate == null) {
            return false;
        }
        
        BranchWire wire = (BranchWire) targetAndGate.getInputComponent2();
        wire.removeFrom(targetAndGate);
        wire.removeFrom(targetAndGate.getInputComponent1());
        OffStubGate offStubGate = new OffStubGate();
        wire.connectTo(offStubGate);
        circuit.addComponent(offStubGate);
        offStubGate.setOutputComponent(targetAndGate.getOutputComponent());
        
        if (targetAndGate.getOutputComponent() 
                instanceof AbstractSingleInputPinCircuitComponent) {
            AbstractSingleInputPinCircuitComponent afterAndGate = 
                    (AbstractSingleInputPinCircuitComponent) 
                    targetAndGate.getOutputComponent();
            
            afterAndGate.setInputComponent(offStubGate);
        } else {
            AbstractDoubleInputPinCircuitComponent afterAndGate =
                    (AbstractDoubleInputPinCircuitComponent) 
                    targetAndGate.getOutputComponent();
            
            if (targetAndGate == afterAndGate.getInputComponent1()) {
                afterAndGate.setInputComponent1(offStubGate);
            } else {
                afterAndGate.setInputComponent2(offStubGate);
            }
        }
        
        return true;
    }
    
    private boolean tryRemoveSingleOutputBranchWire(Circuit circuit) {
        BranchWire targetBranchWire = null;
        
        for (AbstractCircuitComponent component : circuit.getComponentSet()) {
            if (component instanceof BranchWire) {
                if (((BranchWire) component).getOutputs().size() == 1) {
                    targetBranchWire = (BranchWire) component;
                    break;
                }
            }
        }
        
        if (targetBranchWire == null) {
            return false;
        }
        
        AbstractCircuitComponent inputComponent = 
                targetBranchWire.getInputComponent();
        
        AbstractCircuitComponent outputComponent = 
                targetBranchWire.getOutputs().iterator().next();
        
        inputComponent.setOutputComponent(outputComponent);
        
        if (outputComponent instanceof AbstractSingleInputPinCircuitComponent) {
            ((AbstractSingleInputPinCircuitComponent) outputComponent)
                    .setInputComponent(inputComponent);
        } else {
            AbstractDoubleInputPinCircuitComponent c = 
                    (AbstractDoubleInputPinCircuitComponent) outputComponent;
            
            if (c.getInputComponent1() == targetBranchWire) {
                c.setInputComponent1(inputComponent);
            } else {
                c.setInputComponent2(inputComponent);
            }
        }
        
        circuit.removeComponent(targetBranchWire);
        return true;
    }
    
    private void removeUnreachableComponents(Circuit circuit) {
        Set<AbstractCircuitComponent> reachableComponents = 
                findReachableComponents(circuit);
        Map<String, AbstractCircuitComponent> componentMap =
                circuit.getComponentMap();
        
        Set<AbstractCircuitComponent> allComponents = 
                new HashSet<>(componentMap.values()); 
        
        for (AbstractCircuitComponent component : allComponents) {
            if (!reachableComponents.contains(component)) {
                componentMap.remove(component.getName());
                circuit.removeComponent(component);
            }
        }
    }
    
    private void minimizeAllSubcircuits(
            Map<String, AbstractCircuitComponent> componentMap) {
        for (AbstractCircuitComponent component : componentMap.values()) {
            if (component instanceof Circuit) {
                ((Circuit) component).minimize(this);
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
