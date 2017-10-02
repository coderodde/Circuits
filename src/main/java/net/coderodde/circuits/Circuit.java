package net.coderodde.circuits;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import net.coderodde.circuits.components.AbstractCircuitComponent;
import net.coderodde.circuits.components.AbstractDoubleInputPinCircuitComponent;
import net.coderodde.circuits.components.AbstractSingleInputPinCircuitComponent;
import net.coderodde.circuits.components.support.AndGate;
import net.coderodde.circuits.components.support.InputGate;
import net.coderodde.circuits.components.support.BranchWire;
import net.coderodde.circuits.components.support.NotGate;
import net.coderodde.circuits.components.support.OrGate;
import net.coderodde.circuits.components.support.OutputGate;

/**
 * This class serves as a circuit component containers implementing a logical
 * board.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Sep 20, 2017)
 */
public final class Circuit extends AbstractCircuitComponent {

    /**
     * The minimum allowed number of input pins per circuit.
     */
    private static final int MINIMUM_INPUT_PINS = 1;
    
    /**
     * The minimum allowed number of output pins per circuit.
     */
    private static final int MINIMUM_OUTPUT_PINS = 1;
    
    /**
     * The input pin name prefix.
     */
    private static final String INPUT_PIN_NAME_PREFIX = "inputPin";
    
    /**
     * The output pin name prefix.
     */
    private static final String OUTPUT_PIN_NAME_PREFIX = "outputPin";
    
    /**
     * The map mapping the name of a component to the actual component.
     */
    private final Map<String, AbstractCircuitComponent> componentMap = 
            new TreeMap<>();
    
    /**
     * The number of input pins in this circuit.
     */
    private final int numberOfInputPins;
    
    /**
     * The number of output pins in this circuit.
     */
    private final int numberOfOutputPins;
    
    /**
     * The list of input pins.
     */
    private final List<InputGate> inputGates;
    
    /**
     * The list of output pins.
     */
    private final List<OutputGate> outputGates;
    
    /**
     * Set to {@code true} if this circuit is minimized.
     */
    private boolean minimized = false;
    
    /**
     * Creates a new circuit.
     * 
     * @param name       the name of this circuit.
     * @param inputPins  the number of input pins.
     * @param outputPins the number of output pins.
     */
    public Circuit(String name, int inputPins, int outputPins) {
        super(checkName(name));
        this.numberOfInputPins  = checkInputPinCount(inputPins);
        this.numberOfOutputPins = checkOutputPinCount(outputPins);
        
        this.inputGates  = new ArrayList<>(this.numberOfInputPins);
        this.outputGates = new ArrayList<>(this.numberOfOutputPins);
        
        for (int inputPin = 0; inputPin < inputPins; ++inputPin) {
            String inputComponentName = INPUT_PIN_NAME_PREFIX + inputPin;
            InputGate inputComponent = new InputGate(inputComponentName);
            componentMap.put(inputComponentName, inputComponent);
            inputGates.add(inputComponent);
        }
        
        for (int outputPin = 0; outputPin < outputPins; ++outputPin) {
            String outputComponentName = OUTPUT_PIN_NAME_PREFIX + outputPin;
            OutputGate outputComponent = new OutputGate(outputComponentName);
            componentMap.put(outputComponentName, outputComponent);
            outputGates.add(outputComponent);
        }
    }
    
    /**
     * Adds a new <code>NOT</code>-gate to this circuit.
     * 
     * @param notGateName the name of the gate.
     */
    public void addNotGate(String notGateName) {
        checkEditable();
        checkNewGateName(notGateName);
        NotGate notGate = new NotGate(notGateName);
        componentMap.put(notGateName, notGate);
    }
    
    /**
     * Adds a new <code>AND</code>-gate to this circuit.
     * 
     * @param andGateName the name of the gate.
     */
    public void addAndGate(String andGateName) {
        checkEditable();
        checkNewGateName(andGateName);
        AndGate andGate = new AndGate(andGateName);
        componentMap.put(andGateName, andGate);
    }
    
    /**
     * Adds a new <code>OR</code>-gate to this circuit.
     * 
     * @param orGateName the name of the gate.
     */
    public void addOrGate(String orGateName) {
        checkEditable();
        checkNewGateName(orGateName);
        OrGate orGate = new OrGate(orGateName);
        componentMap.put(orGateName, orGate);
    }
    
    /**
     * Adds a subcircuit to this circuit.
     * 
     * @param circuit the subcircuit to add.
     */
    public void addCircuit(Circuit circuit) {
        checkEditable();
        checkNewGateName(circuit.getName());
        componentMap.put(circuit.getName(), circuit);
    }
    
    public int getNumberOfInputPins() {
        return numberOfInputPins;
    }
    
    public int getNumberOfOutputPins() {
        return numberOfOutputPins;
    }
    
    /**
     * Performs a single cycle of this circuit.
     * 
     * @return dummy value.
     */
    @Override
    public boolean doCycle() {
        for (OutputGate outputGate : outputGates) {
            outputGate.doCycle();
        }
        
        return false;
    }
    
    /**
     * Sets the states of all the input pins. If the length of {@code bits} is 
     * smaller than the number of input pins in this circuit, the rest of input
     * pins are set to zero. If the length of {@code bits} is greater than the 
     * number of input pins, the overflowing values of {@code bits} are ignored.
     * 
     * @param bits the bit vector.
     */
    public void setInputBits(boolean... bits) {
        Objects.requireNonNull(bits, "The input bit array is null.");
        unsetAllInputPins();
        
        for (int i = 0; i < Math.min(bits.length, inputGates.size()); ++i) {
            inputGates.get(i).setBit(bits[i]);
        }
    }
    
    /**
     * Returns a bit vector representing a result of a circuit cycle.
     * 
     * @return a bit vector.
     */
    public boolean[] getOutputBits() {
        boolean[] bits = new boolean[numberOfOutputPins];
        
        for (int i = 0; i < bits.length; ++i) {
            bits[i] = outputGates.get(i).doCycle();
        }
        
        return bits;
    }
    
    /**
     * Attempts to produce a logical circuit with minimal possible number of 
     * gates that is equivalent to this circuit.
     */
    public void minimize() {
        checkEditable();
        minimized = true;
        checkAllPinsAreConnected();
        checkIsDagInForwardDirection();
        checkIsDagInBackwardDirection();
    }
    
    /**
     * Initiates a call for connecting some gates.
     * 
     * @param sourceComponentName the source component name.
     * @return a target component selector.
     */
    public TargetComponentSelector connect(String sourceComponentName) {
        checkEditable();
        return new TargetComponentSelector(sourceComponentName);
    }
    
    public final class TargetComponentSelector {
        
        private final AbstractCircuitComponent sourceComponent;
        
        TargetComponentSelector(String sourceComponentName) {
            Objects.requireNonNull(sourceComponentName,
                                   "The source component name is null.");
            
            AbstractCircuitComponent sourceComponent;
            
            if (sourceComponentName.contains(".")) {
                String[] nameComponents = sourceComponentName.split("\\.");
                
                if (nameComponents.length != 2) {
                    throw new IllegalArgumentException(
                            "More than one dot operators: " + 
                                    sourceComponentName);
                }
                
                Circuit subcircuit = 
                        (Circuit) componentMap.get(nameComponents[0]);
                
                if (subcircuit == null) {
                    throw new IllegalArgumentException(
                            "Subcircuit \"" + nameComponents[0] + "\" is " +
                            "not present in this circuit (" + getName() +
                            ").");
                }
                
                sourceComponent = 
                        subcircuit.componentMap.get(nameComponents[1]);
            } else {
                sourceComponent = componentMap.get(sourceComponentName);
            }
            
            if (sourceComponent == null) {
                throwComponentNotPresent(sourceComponentName);
            }
            
            this.sourceComponent = sourceComponent;
        }
        
        public void toFirstPinOf(String targetComponentName) {
            Objects.requireNonNull(targetComponentName, 
                                   "The target component name is null.");
            
            AbstractCircuitComponent targetComponent;
            
            if (targetComponentName.contains(".")) {
                String[] nameComponents = targetComponentName.split("\\.");
                
                if (nameComponents.length != 2) {
                    throw new IllegalArgumentException(
                            "More than one dot operators: " + 
                                    targetComponentName);
                }
                
                Circuit subcircuit = 
                        (Circuit) componentMap.get(nameComponents[0]);
                
                if (subcircuit == null) {
                    throw new IllegalArgumentException(
                            "Subcircuit \"" + nameComponents[0] + "\" is " +
                            "not present in this circuit (" + getName() +
                            ").");
                }
                
                targetComponent = 
                        subcircuit.componentMap.get(nameComponents[1]);
            } else {
                targetComponent = componentMap.get(targetComponentName);
            }
            
            if (targetComponent == null) {
                throwComponentNotPresent(targetComponentName);
            }
            
            checkIsDoubleInputGate(targetComponent); ////
            
            if (((AbstractDoubleInputPinCircuitComponent) targetComponent)
                    .getInputComponent1() != null) {
                throw new InputPinOccupiedException(
                        "The 1st input pin of \"" + targetComponentName + "\"" + 
                        " is occupied.");
            }
            
            if (sourceComponent.getOutputComponent() == null) {
                ((AbstractDoubleInputPinCircuitComponent) targetComponent)
                        .setInputComponent1(sourceComponent);
                
                sourceComponent.setOutputComponent(targetComponent);
            } else if (
                   sourceComponent.getOutputComponent() instanceof BranchWire) {
                ((AbstractDoubleInputPinCircuitComponent) targetComponent)
                        .setInputComponent1(
                                sourceComponent.getOutputComponent());
                ((BranchWire) sourceComponent.getOutputComponent())
                        .connectTo(targetComponent);
            } else {
                // Change an existing wire with JointWire.
                BranchWire jointWire = new BranchWire();
                jointWire.connectTo(sourceComponent.getOutputComponent());
                sourceComponent.setOutputComponent(jointWire);
                jointWire.setInputComponent(sourceComponent);
                ((AbstractDoubleInputPinCircuitComponent) targetComponent)
                        .setInputComponent1(jointWire);
                jointWire.connectTo(targetComponent);
            }
        }
        
        public void toSecondPinOf(String targetComponentName) {
            Objects.requireNonNull(targetComponentName,
                                   "The target component name is null.");
            
            AbstractCircuitComponent targetComponent;
            
            if (targetComponentName.contains(".")) {
                String[] nameComponents = targetComponentName.split("\\.");
                
                if (nameComponents.length != 2) {
                    throw new IllegalArgumentException(
                            "More than one dot operators: " +
                                    targetComponentName);
                }
                
                Circuit subcircuit = 
                        (Circuit) componentMap.get(nameComponents[0]);
                
                if (subcircuit == null) {
                    throw new IllegalArgumentException(
                            "Subcircuit \"" + nameComponents[0] + "\" is " +
                            "not present in this circuit (" + getName() +
                            ").");
                }
                
                targetComponent = 
                        subcircuit.componentMap.get(nameComponents[1]);
            } else {
                targetComponent = componentMap.get(targetComponentName);
            }
            
            if (targetComponent == null) {
                throwComponentNotPresent(targetComponentName);
            }
            
            checkIsDoubleInputGate(targetComponent);
            
            if (((AbstractDoubleInputPinCircuitComponent) targetComponent)
                    .getInputComponent2() != null) {
                throw new InputPinOccupiedException(
                        "The 2nd input pin of \"" + targetComponentName + "\"" + 
                        " is occupied.");
            }
            
            if (sourceComponent.getOutputComponent() == null) {
                ((AbstractDoubleInputPinCircuitComponent) targetComponent)
                        .setInputComponent2(sourceComponent);
                
                sourceComponent.setOutputComponent(targetComponent);
            } else if (
                   sourceComponent.getOutputComponent() instanceof BranchWire) {
                ((AbstractDoubleInputPinCircuitComponent) targetComponent)
                        .setInputComponent2(
                                sourceComponent.getOutputComponent());
                ((BranchWire) sourceComponent.getOutputComponent())
                        .connectTo(targetComponent);
            } else {
                // Change an existing wire with JointWire.
                BranchWire jointWire = new BranchWire();
                jointWire.connectTo(sourceComponent.getOutputComponent());
                sourceComponent.setOutputComponent(jointWire);
                jointWire.setInputComponent(sourceComponent);
                ((AbstractDoubleInputPinCircuitComponent) targetComponent)
                        .setInputComponent2(jointWire);
                jointWire.connectTo(targetComponent);
            }
        }
        
        public void to(String targetComponentName) {
            Objects.requireNonNull(targetComponentName,
                                   "The target component name is null.");
            
            AbstractCircuitComponent targetComponent;
            
            if (targetComponentName.contains(".")) {
                String[] nameComponents = targetComponentName.split("\\.");
                
                if (nameComponents.length != 2) {
                    throw new IllegalArgumentException(
                            "More than one dot operators: " + 
                                    targetComponentName);
                }
                
                Circuit subcircuit = 
                        (Circuit) componentMap.get(nameComponents[0]);
                
                if (subcircuit == null) {
                    throw new IllegalArgumentException(
                            "Subcircuit \"" + nameComponents[0] + "\" is " +
                            "not present in this circuit (" + getName() + 
                            ").");
                }
                
                targetComponent = 
                        subcircuit.componentMap.get(nameComponents[1]);
            } else {
                targetComponent = componentMap.get(targetComponentName);
            }
            
            if (targetComponent == null) {
                throwComponentNotPresent(targetComponentName);
            }
            
            checkIsSingleInputGate(targetComponent);
            
            if (((AbstractSingleInputPinCircuitComponent) targetComponent)
                    .getInputComponent() != null) {
                throw new InputPinOccupiedException(
                        "The only input pin of \"" + targetComponentName + 
                        "\" is occupied.");
            }
            
            if (sourceComponent.getOutputComponent() == null) {
                ((AbstractSingleInputPinCircuitComponent) targetComponent)
                        .setInputComponent(sourceComponent);
                
                sourceComponent.setOutputComponent(targetComponent);
            } else if (
                   sourceComponent.getOutputComponent() instanceof BranchWire) {
                ((AbstractSingleInputPinCircuitComponent) targetComponent)
                        .setInputComponent(
                                sourceComponent.getOutputComponent());
                ((BranchWire) sourceComponent.getOutputComponent())
                        .connectTo(targetComponent);
            } else {
                // Change an existing wire with JointWire.
                BranchWire jointWire = new BranchWire();
                jointWire.connectTo(sourceComponent.getOutputComponent());
                sourceComponent.setOutputComponent(jointWire);
                jointWire.setInputComponent(sourceComponent);
                ((AbstractSingleInputPinCircuitComponent) targetComponent)
                        .setInputComponent(jointWire);
                jointWire.connectTo(targetComponent);
            }
        }
        
        private void throwComponentNotPresent(String componentName) {
            throw new IllegalStateException(
                    "The component \"" + componentName + "\" is " +
                    "not present in the circuit \"" + getName() + "\".");
        }
        
        private void checkIsSingleInputGate(AbstractCircuitComponent gate) {
            if (!(gate instanceof AbstractSingleInputPinCircuitComponent)) {
                throw new IllegalArgumentException(
                        "A single input pin component is expected here.");
            }
        }
        
        private void checkIsDoubleInputGate(AbstractCircuitComponent gate) {
            if (!(gate instanceof AbstractDoubleInputPinCircuitComponent)) {
                throw new IllegalArgumentException(
                        "A double input pin component is expected here.");
            }
        }
    }
    
    private void checkEditable() {
        if (minimized) {
            throw new IllegalStateException(
                    "The circuit \"" + getName() + "\" is not editable.");
        }
    }
    
    private void unsetAllInputPins() {
        for (InputGate inputGate : inputGates) {
            inputGate.setBit(false);
        }
    }
    
    private static String checkName(String name) {
        Objects.requireNonNull(name, "The circuit name is null.");
        
        if (name.isEmpty()) {
            throw new IllegalArgumentException("The circuit name is empty.");
        }
        
        if (name.startsWith(INPUT_PIN_NAME_PREFIX)) {
            throw new IllegalArgumentException(
                    "The circuit name (" + name + ") has illegal prefix \"" +
                    INPUT_PIN_NAME_PREFIX + "\".");
        }
        
        if (name.startsWith(OUTPUT_PIN_NAME_PREFIX)) {
            throw new IllegalArgumentException(
                    "The circuit name (" + name + ") has illegal prefix \"" + 
                    OUTPUT_PIN_NAME_PREFIX + "\".");
        }
        
        return name;
    }
    
    private static int checkInputPinCount(int inputPins) {
        if (inputPins < MINIMUM_INPUT_PINS) {
            throw new IllegalArgumentException(
            "Too few input pins (" + inputPins + "). At least " + 
                    MINIMUM_INPUT_PINS + " expected.");
        }
        
        return inputPins;
    }
    
    private static int checkOutputPinCount(int outputPins) {
        if (outputPins < MINIMUM_INPUT_PINS) {
            throw new IllegalArgumentException(
            "Too few output pins (" + outputPins + "). At least " + 
                    MINIMUM_OUTPUT_PINS + " expected.");
        }
        
        return outputPins;
    }
    
    private String checkNewGateName(String gateName) {
        Objects.requireNonNull(gateName, "The new gate name is null.");
        
        if (gateName.isEmpty()) {
            throw new IllegalArgumentException("The new gate name is empty.");
        }
        
        
        
        if (gateName.startsWith(INPUT_PIN_NAME_PREFIX)) {
            throw new IllegalArgumentException(
                    "The new gate name (" + gateName + ") has illegal prefix " +
                    "\"" + INPUT_PIN_NAME_PREFIX + "\".");
        }
        
        if (gateName.startsWith(OUTPUT_PIN_NAME_PREFIX)) {
            throw new IllegalArgumentException(
                    "The new gate name (" + gateName + ") has illegal prefix " +
                    "\"" + OUTPUT_PIN_NAME_PREFIX + "\".");
        }
        
        if (componentMap.containsKey(gateName)) {
            throw new IllegalArgumentException(
                    "The new gate name (" + gateName + ") is already "+
                    "occupied.");
        }
        
        return gateName;
    }
    
    private void checkInputGateComplete(InputGate inputGate, String name) {
        if (inputGate.getOutputComponent() == null) {
            throw new IncompleteCircuitException(
                    "The input gate \"" + name + "\" has no " + 
                    "output gate.");
        }    
    }
    
    private void checkOutputGateComplete(OutputGate outputGate, String name) {
        if (outputGate.getInputComponent() == null) {
            throw new IncompleteCircuitException(
                    "The output gate \"" + name + "\" has no input gate.");
        }
    }
    
    private void checkNotGateComplete(NotGate notGate, String name) {
        if (notGate.getInputComponent() == null) {
            throw new IncompleteCircuitException(
                    "The not gate \"" + name + "\" has no input gate.");
        }

        if (notGate.getOutputComponent() == null) {
            throw new IncompleteCircuitException(
                    "The not gate \"" + name + "\" has no output gate.");
        }
    }
    
    private void checkOrGateComplete(OrGate gate, String name) {
        if (gate.getInputComponent1() == null) {
            throw new IncompleteCircuitException(
                    "The OrGate \"" + name + "\" has no 1st input gate.");
        }
        
        if (gate.getInputComponent2() == null) {
            throw new IncompleteCircuitException(
                    "The OrGate \"" + name + "\" has no 2nd input gate.");
        }
        
        if (gate.getOutputComponent() == null) {
            throw new IncompleteCircuitException(
                    "The OrGate \"" + name + "\" has no output gate.");
        }
    }
    
    private void checkAndGateComplete(AndGate gate, String name) {
        if (gate.getInputComponent1() == null) {
            throw new IncompleteCircuitException(
                    "The AndGate \"" + name + "\" has no 1st input gate.");
        }
        
        if (gate.getInputComponent2() == null) {
            throw new IncompleteCircuitException(
                    "The AndGate \"" + name + "\" has no 2nd input gate.");
        }
        
        if (gate.getOutputComponent() == null) {
            throw new IncompleteCircuitException(
                    "The AndGate \"" + name + "\" has no output gate.");
        }
    }
    
    private void checkAllPinsAreConnected() {
        for (Map.Entry<String, AbstractCircuitComponent> e : 
                componentMap.entrySet()) {
            if (e.getValue() instanceof InputGate) {
                checkInputGateComplete((InputGate) e.getValue(), e.getKey());
            } else if (e.getValue() instanceof OutputGate) {
                checkOutputGateComplete((OutputGate) e.getValue(), e.getKey());
            } else if (e.getValue() instanceof NotGate) {
                checkNotGateComplete((NotGate) e.getValue(), e.getKey());
            } else if (e.getValue() instanceof OrGate) {
                checkOrGateComplete((OrGate) e.getValue(), e.getKey());
            } else if (e.getValue() instanceof AndGate) {
                checkAndGateComplete((AndGate) e.getValue(), e.getKey());
            } else {
                throw new IllegalStateException(
                        "Unknown component type: " + e.getValue());
            }
        }
    }
    
    private void checkIsDagInForwardDirection() {
        Set<AbstractCircuitComponent> closed = new HashSet<>();
        
        for (AbstractCircuitComponent inputComponent : inputGates) {
            if (hasCycleForward(inputComponent, closed)) {
                throw new ForwardCycleException(
                        "Forward cycle detected in circuit \"" + getName() + 
                        "\".");
            }
        }
    }
    
    private void checkIsDagInBackwardDirection() {
        Set<AbstractCircuitComponent> closed = new HashSet<>();
        
        for (AbstractCircuitComponent outputComponent : outputGates) {
            if (hasCycleBackwards(outputComponent, closed)) {
                throw new BackwardCycleException(
                        "Backward cycle detected in circuit \"" + getName() + 
                        "\".");
            }
        }
    }
    
    private static List<AbstractCircuitComponent> 
        forwardExpand(AbstractCircuitComponent component) {
        if (component instanceof BranchWire) {
            return new ArrayList<>(((BranchWire) component).getOutputs());
        } 
        
        if (component instanceof AbstractSingleInputPinCircuitComponent) {
            if (component.getOutputComponent() == null) {
                return Collections.emptyList();
            }
            
            return Arrays.asList(component.getOutputComponent());
        } 
        
        if (component instanceof AbstractDoubleInputPinCircuitComponent) {
            if (component.getOutputComponent() == null) {
                return Collections.emptyList();
            }
            
            return Arrays.asList(component.getOutputComponent());
        }
        
        throw new IllegalStateException("Unknown gate type.");
    }
        
    private static List<AbstractCircuitComponent>
        backwardExpand(AbstractCircuitComponent component) {
        if (component instanceof AbstractSingleInputPinCircuitComponent) {
            if (((AbstractSingleInputPinCircuitComponent) component)
                    .getInputComponent() == null) {
                return Collections.emptyList();
            }
            
            return Arrays.asList(
                    ((AbstractSingleInputPinCircuitComponent) component)
                            .getInputComponent());
        }       
        
        if (component instanceof AbstractDoubleInputPinCircuitComponent) {
            AbstractDoubleInputPinCircuitComponent c = 
                    (AbstractDoubleInputPinCircuitComponent) component;
            
            if (c.getInputComponent1() == null 
                    || c.getInputComponent2() == null) {
                return Collections.emptyList();
            }
            
            return Arrays.asList(c.getInputComponent1(),
                                 c.getInputComponent2());
        }
        
        throw new IllegalStateException("Unknown gate type.");
    }
    
    private boolean hasCycleForward(AbstractCircuitComponent component, 
                             Set<AbstractCircuitComponent> closed) {
        if (closed.contains(component)) {
            return true;
        }
        
        closed.add(component);
        
        for (AbstractCircuitComponent child : forwardExpand(component)) {
            if (hasCycleForward(child, closed)) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean hasCycleBackwards(AbstractCircuitComponent component,
                                     Set<AbstractCircuitComponent> closed) {
        if (closed.contains(component)) {
            return true;
        }
        
        closed.add(component);
        
        for (AbstractCircuitComponent parent : backwardExpand(component)) {
            if (hasCycleBackwards(parent, closed)) {
                return true;
            }
        }
        
        return false;
    }
    
}
