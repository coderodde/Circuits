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
import net.coderodde.circuits.components.support.OnStubGate;
import net.coderodde.circuits.components.support.OrGate;

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
        while (true) {
            if (tryRemoveSingleOutputBranchWire(circuit)) {
                continue;
            }
            
            if (tryRemoveOneNotToAnd1(circuit)) {
                continue;
            }
            
            if (tryRemoveOneNotToAnd2(circuit)) {
                continue;
            }
            
            if (tryRemoveOneNotToOr1(circuit)) {
                continue;
            }
            
            if (tryRemoveOneNotToOr2(circuit)) {
                continue;
            }
            
            if (tryRemoveTwoNots(circuit)) {
                continue;
            }
            
            break;
        }
    }
    
    private boolean tryRemoveTwoNotsMatches(
            AbstractCircuitComponent candidateComponent) {
        if (!(candidateComponent instanceof NotGate)) {
            return false;
        }
        
        NotGate notGate = (NotGate) candidateComponent;
        return notGate.getInputComponent() instanceof NotGate;
    }
    
    private boolean tryRemoveTwoNots(Circuit circuit) {
        NotGate targetNotGate = null;
        
        for (AbstractCircuitComponent component :
                circuit.getComponentMap().values()) {
            if (tryRemoveTwoNotsMatches(component)) {
                targetNotGate = (NotGate) component;
                break;
            }
        }
        
        if (targetNotGate == null) {
            return false;
        }
        
        NotGate beforeTargetNotGate = 
                (NotGate) targetNotGate.getInputComponent();
        
        beforeTargetNotGate.getInputComponent()
                     .setOutputComponent(targetNotGate.getOutputComponent());
        
        AbstractCircuitComponent afterTargetNotGate = 
                targetNotGate.getOutputComponent();
        
        if (afterTargetNotGate 
                instanceof AbstractSingleInputPinCircuitComponent) {
            AbstractSingleInputPinCircuitComponent tmpComponent = 
                    (AbstractSingleInputPinCircuitComponent)
                    afterTargetNotGate;
            
            tmpComponent.setInputComponent(beforeTargetNotGate.getInputComponent());
        } else {
            AbstractDoubleInputPinCircuitComponent tmpComponent =
                    (AbstractDoubleInputPinCircuitComponent) 
                    afterTargetNotGate;
            
            if (targetNotGate == tmpComponent.getInputComponent1()) {
                tmpComponent.setInputComponent1(
                        beforeTargetNotGate.getInputComponent());
            } else {
                tmpComponent.setInputComponent2(
                        beforeTargetNotGate.getInputComponent());
            }
        }
        
        // Remove from not gates from the circuit.
        circuit.removeComponent(targetNotGate);
        circuit.removeComponent(beforeTargetNotGate);
        
        circuit.getComponentMap().remove(targetNotGate.getName());
        circuit.getComponentMap().remove(beforeTargetNotGate.getName());
        return true;
    }
    
    private boolean tryRemoveOneNotToOr1Matches(
            AbstractCircuitComponent candidateComponent) {
        if (!(candidateComponent instanceof OrGate)) {
            return false;
        }
        
        OrGate orGate = (OrGate) candidateComponent;
        
        if (!(orGate.getInputComponent1() instanceof NotGate)) {
            return false;
        }
        
        if (orGate.getInputComponent2() instanceof NotGate) {
            // Can be minimized but in another place.
            return false;
        }
        
        NotGate notGate = (NotGate) orGate.getInputComponent1();
        
        return notGate.getInputComponent() == orGate.getInputComponent2();
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
    
    private boolean tryRemoveOneNotToOr2Matches(
            AbstractCircuitComponent candidateComponent) {
        if (!(candidateComponent instanceof OrGate)) {
            return false;
        }
        
        OrGate orGate = (OrGate) candidateComponent;
        
        if (!(orGate.getInputComponent2() instanceof NotGate)) {
            return false;
        }
        
        if (orGate.getInputComponent1() instanceof NotGate) {
            return false;
        }
        
        NotGate notGate = (NotGate) orGate.getInputComponent2();
        
        return notGate.getInputComponent() == orGate.getInputComponent1();
    }
    
    private boolean tryRemoveOneNotToAnd2Matches(
            AbstractCircuitComponent candidateComponent) {
        if (!(candidateComponent instanceof AndGate)) {
            return false;
        }
        
        AndGate andGate = (AndGate) candidateComponent;
        
        if (!(andGate.getInputComponent2() instanceof NotGate)) {
            return false;
        }
        
        if (andGate.getInputComponent1() instanceof NotGate) {
            return false;
        }
        
        NotGate notGate = (NotGate) andGate.getInputComponent2();
        
        return notGate.getInputComponent() == andGate.getInputComponent1();
    }
    
    private boolean tryRemoveOneNotToOr2(Circuit circuit) {
        OrGate targetOrGate = null;
        
        for (AbstractCircuitComponent component :
                circuit.getComponentMap().values()) {
            if (tryRemoveOneNotToOr2Matches(component)) {
                targetOrGate = (OrGate) component;
                break;
            }
        }
        
        if (targetOrGate == null) {
            return false;
        }
        
        BranchWire wire = (BranchWire) targetOrGate.getInputComponent1();
        wire.removeFrom(targetOrGate);
        wire.removeFrom(targetOrGate.getInputComponent2());
        
        OnStubGate onStubGate = new OnStubGate();
        wire.connectTo(onStubGate);
        onStubGate.setInputComponent(wire);
        onStubGate.setOutputComponent(targetOrGate.getOutputComponent());
        
        // Update the component set.
        circuit.addComponent(onStubGate);
        circuit.removeComponent(targetOrGate);
        circuit.removeComponent(targetOrGate.getInputComponent2());
        
        circuit.getComponentMap().remove(targetOrGate.getName());
        circuit.getComponentMap().remove(targetOrGate.getInputComponent2()
                                                     .getName());
        
        if (targetOrGate.getOutputComponent() 
                instanceof AbstractSingleInputPinCircuitComponent) {
            AbstractSingleInputPinCircuitComponent afterOrGate =
                    (AbstractSingleInputPinCircuitComponent)
                    targetOrGate.getOutputComponent();
            
            afterOrGate.setInputComponent(onStubGate);
        } else {
            AbstractDoubleInputPinCircuitComponent afterOrGate =
                    (AbstractDoubleInputPinCircuitComponent) 
                    targetOrGate.getOutputComponent();
            
            if (targetOrGate == afterOrGate.getInputComponent1()) {
                afterOrGate.setInputComponent1(onStubGate);
            } else {
                afterOrGate.setInputComponent2(onStubGate);
            }
        }
        
        return true;
    }
    
    private boolean tryRemoveOneNotToAnd2(Circuit circuit) {
        AndGate targetAndGate = null;
        
        for (AbstractCircuitComponent component :
                circuit.getComponentMap().values()) {
            if (tryRemoveOneNotToAnd2Matches(component)) {
                targetAndGate = (AndGate) component;
                break;
            }
        }
        
        if (targetAndGate == null) {
            return false;
        }
        
        BranchWire wire = (BranchWire) targetAndGate.getInputComponent1();
        wire.removeFrom(targetAndGate);
        wire.removeFrom(targetAndGate.getInputComponent2());
        
        OffStubGate offStubGate = new OffStubGate();
        wire.connectTo(offStubGate);
        offStubGate.setInputComponent(wire);
        offStubGate.setOutputComponent(targetAndGate.getOutputComponent());
        
        // Update the component set.
        circuit.addComponent(offStubGate);
        circuit.removeComponent(targetAndGate);
        circuit.removeComponent(targetAndGate.getInputComponent2());
        
        circuit.getComponentMap().remove(targetAndGate.getName());
        circuit.getComponentMap().remove(targetAndGate.getInputComponent2()
                                                      .getName());
        
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
    
    private boolean tryRemoveOneNotToOr1(Circuit circuit) {
        OrGate targetOrGate = null;
        
        for (AbstractCircuitComponent component :
                circuit.getComponentMap().values()) {
            if (tryRemoveOneNotToOr1Matches(component)) {
                targetOrGate = (OrGate) component;
                break;
            }
        }
        
        if (targetOrGate == null) {
            return false;
        }
        
        BranchWire wire = (BranchWire) targetOrGate.getInputComponent2();
        wire.removeFrom(targetOrGate);
        wire.removeFrom(targetOrGate.getInputComponent1());
        
        OnStubGate onStubGate = new OnStubGate();
        wire.connectTo(onStubGate);
        onStubGate.setInputComponent(wire);
        onStubGate.setOutputComponent(targetOrGate.getOutputComponent());
        
        // Update the component set.
        circuit.addComponent(onStubGate);
        circuit.removeComponent(targetOrGate);
        circuit.removeComponent(targetOrGate.getInputComponent1());
        
        circuit.getComponentMap().remove(targetOrGate.getName());
        circuit.getComponentMap().remove(targetOrGate.getInputComponent1()
                                                     .getName());
        
        if (targetOrGate.getOutputComponent()
                instanceof AbstractSingleInputPinCircuitComponent) {
            AbstractSingleInputPinCircuitComponent afterOrGate = 
                    (AbstractSingleInputPinCircuitComponent)
                    targetOrGate.getOutputComponent();
            
            afterOrGate.setInputComponent(onStubGate);
        } else {
            AbstractDoubleInputPinCircuitComponent afterOrGate = 
                    (AbstractDoubleInputPinCircuitComponent)
                    targetOrGate.getOutputComponent();
            
            if (targetOrGate == afterOrGate.getInputComponent1()) {
                afterOrGate.setInputComponent1(onStubGate);
            } else {
                afterOrGate.setInputComponent2(onStubGate);
            }
        }
        
        return true;
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
        offStubGate.setInputComponent(wire);
        offStubGate.setOutputComponent(targetAndGate.getOutputComponent());
        
        // Update the component set.
        circuit.addComponent(offStubGate);
        circuit.removeComponent(targetAndGate);
        circuit.removeComponent(targetAndGate.getInputComponent1());
        
        circuit.getComponentMap().remove(targetAndGate.getName());
        circuit.getComponentMap().remove(targetAndGate.getInputComponent1()
                                                      .getName());
        
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
