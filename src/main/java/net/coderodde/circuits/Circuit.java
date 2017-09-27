package net.coderodde.circuits;

import java.util.ArrayList;
import java.util.Arrays;
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
import net.coderodde.circuits.components.support.JointWire;
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
public final class Circuit {

    private static final int MINIMUM_INPUT_PINS = 1;
    private static final int MINIMUM_OUTPUT_PINS = 1;
    private static final String INPUT_PIN_NAME_PREFIX = "inputPin";
    private static final String OUTPUT_PIN_NAME_PREFIX = "outputPin";
    
    private final String name;
    private final Map<String, AbstractCircuitComponent> componentMap = 
            new TreeMap<>();
    
    private final int inputPins;
    private final int outputPins;
    
    private final List<InputGate> inputGates;
    private final List<OutputGate> outputGates;
    
    private boolean minimized = false;
    
    public Circuit(String name, int inputPins, int outputPins) {
        this.name       = checkName(name);
        this.inputPins  = checkInputPinCount(inputPins);
        this.outputPins = checkOutputPinCount(outputPins);
        
        this.inputGates  = new ArrayList<>(this.inputPins);
        this.outputGates = new ArrayList<>(this.outputPins);
        
        for (int inputPin = 0; inputPin < inputPins; ++inputPin) {
            String inputComponentName = INPUT_PIN_NAME_PREFIX + inputPin;
            InputGate inputComponent = new InputGate();
            componentMap.put(inputComponentName, inputComponent);
            inputGates.add(inputComponent);
        }
        
        for (int outputPin = 0; outputPin < outputPins; ++outputPin) {
            String outputComponentName = OUTPUT_PIN_NAME_PREFIX + outputPin;
            OutputGate outputComponent = new OutputGate();
            componentMap.put(outputComponentName, outputComponent);
            outputGates.add(outputComponent);
        }
    }
    
    public String getCircuitName() {
        return name;
    }
    
    public void addNotGate(String notGateName) {
        checkEditable();
        checkNewGateName(notGateName);
        NotGate notGate = new NotGate();
        componentMap.put(notGateName, notGate);
    }
    
    public void addAndGate(String andGateName) {
        checkEditable();
        checkNewGateName(andGateName);
        AndGate andGate = new AndGate();
        componentMap.put(andGateName, andGate);
    }
    
    public void addOrGate(String orGateName) {
        checkEditable();
        checkNewGateName(orGateName);
        OrGate orGate = new OrGate();
        componentMap.put(orGateName, orGate);
    }
    
    public int getNumberOfInputPins() {
        return inputPins;
    }
    
    public int getNumberOfOutputPins() {
        return outputPins;
    }
    
    public void setInputPins(boolean... bits) {
        Objects.requireNonNull(bits, "The input bit array is null.");
        unsetAllInputPins();
        
        for (int i = 0; i < Math.min(bits.length, inputGates.size()); ++i) {
            inputGates.get(i).setBit(bits[i]);
        }
    }
    
    public void minimize() {
        checkEditable();
        minimized = true;
        checkAllPinsAreConnected();
        checkIsDagInForwardDirection();
        checkIsDagInBackwardDirection();
    }
    
    public TargetComponentSelector connect(String sourceComponentName) {
        return new TargetComponentSelector(sourceComponentName);
    }
    
    public final class TargetComponentSelector {
        
        private final AbstractCircuitComponent sourceComponent;
        
        TargetComponentSelector(String sourceComponentName) {
            Objects.requireNonNull(sourceComponentName,
                                   "The source component name is null.");
            
            AbstractCircuitComponent sourceComponent = 
                    componentMap.get(sourceComponentName);
            
            if (sourceComponent == null) {
                throwComponentNotPresent(sourceComponentName);
            }
            
            this.sourceComponent = sourceComponent;
        }
        
        public void toFirstPinOf(String targetComponentName) {
            Objects.requireNonNull(targetComponentName, 
                                   "The target component name is null.");
            
            AbstractCircuitComponent targetComponent =
                    componentMap.get(targetComponentName);
            
            if (targetComponent == null) {
                throwComponentNotPresent(targetComponentName);
            }
            
            checkIsDoubleInputGate(targetComponent);
            
            if (sourceComponent.getOutputComponent() == null) {
                ((AbstractDoubleInputPinCircuitComponent) targetComponent)
                        .setInputComponent1(sourceComponent);
                
                sourceComponent.setOutputComponent(targetComponent);
            } else if (
                   sourceComponent.getOutputComponent() instanceof JointWire) {
                ((AbstractDoubleInputPinCircuitComponent) targetComponent)
                        .setInputComponent1(
                                sourceComponent.getOutputComponent());
                ((JointWire) sourceComponent.getOutputComponent())
                        .connectTo(targetComponent);
            } else {
                // Change an existing wire with JointWire.
                JointWire jointWire = new JointWire();
                sourceComponent.setOutputComponent(jointWire);
                ((AbstractDoubleInputPinCircuitComponent) targetComponent)
                        .setInputComponent1(jointWire);
                jointWire.connectTo(targetComponent);
            }
        }
        
        public void toSecondPinOf(String targetComponentName) {
            Objects.requireNonNull(targetComponentName,
                                   "The target component name is null.");
            
            AbstractCircuitComponent targetComponent =
                    componentMap.get(targetComponentName);
            
            if (targetComponent == null) {
                throwComponentNotPresent(targetComponentName);
            }
            
            checkIsDoubleInputGate(targetComponent);
            
            if (sourceComponent.getOutputComponent() == null) {
                ((AbstractDoubleInputPinCircuitComponent) targetComponent)
                        .setInputComponent2(sourceComponent);
                
                sourceComponent.setOutputComponent(targetComponent);
            } else if (
                   sourceComponent.getOutputComponent() instanceof JointWire) {
                ((AbstractDoubleInputPinCircuitComponent) targetComponent)
                        .setInputComponent2(
                                sourceComponent.getOutputComponent());
                ((JointWire) sourceComponent.getOutputComponent())
                        .connectTo(targetComponent);
            } else {
                // Change an existing wire with JointWire.
                JointWire jointWire = new JointWire();
                sourceComponent.setOutputComponent(jointWire);
                ((AbstractDoubleInputPinCircuitComponent) targetComponent)
                        .setInputComponent2(jointWire);
                jointWire.connectTo(targetComponent);
            }
        }
        
        public void to(String targetComponentName) {
            Objects.requireNonNull(targetComponentName,
                                   "The target component name is null.");
            
            AbstractCircuitComponent targetComponent = 
                    componentMap.get(targetComponentName);
            
            if (targetComponent == null) {
                throwComponentNotPresent(targetComponentName);
            }
            
            checkIsSingleInputGate(targetComponent);
            
            if (sourceComponent.getOutputComponent() == null) {
                ((AbstractSingleInputPinCircuitComponent) targetComponent)
                        .setInputComponent(sourceComponent);
                
                sourceComponent.setOutputComponent(targetComponent);
            } else if (
                   sourceComponent.getOutputComponent() instanceof JointWire) {
                ((AbstractSingleInputPinCircuitComponent) targetComponent)
                        .setInputComponent(
                                sourceComponent.getOutputComponent());
                ((JointWire) sourceComponent.getOutputComponent())
                        .connectTo(targetComponent);
            } else {
                // Change an existing wire with JointWire.
                JointWire jointWire = new JointWire();
                sourceComponent.setOutputComponent(jointWire);
                ((AbstractSingleInputPinCircuitComponent) targetComponent)
                        .setInputComponent(jointWire);
                jointWire.connectTo(targetComponent);
            }
        }
        
        private void throwComponentNotPresent(String componentName) {
            throw new IllegalStateException(
                    "The component \"" + componentName + "\" is " +
                    "not present in the circuit \"" + name + "\".");
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
                    "The circuit \"" + name + "\" is not editable.");
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
            if (hasCycle(inputComponent, closed)) {
                throw new IllegalStateException("A forward cycle found.");
            }
        }
    }
    
    private static List<AbstractCircuitComponent> 
        expand(AbstractCircuitComponent component) {
        if (component instanceof JointWire) {
            return new ArrayList<>(((JointWire) component).getOutputs());
        } 
        
        if (component instanceof AbstractSingleInputPinCircuitComponent) {
            return Arrays.asList(component.getOutputComponent());
        } else if (
                component instanceof AbstractDoubleInputPinCircuitComponent) {
            return Arrays.asList(component.getOutputComponent());
        } else {
            throw new IllegalStateException("Unknown gate type.");
        }
    }
    
    private boolean hasCycle(AbstractCircuitComponent component, 
                             Set<AbstractCircuitComponent> closed) {
        if (closed.contains(component)) {
            return true;
        }
        
        closed.add(component);
        
        for (AbstractCircuitComponent child : expand(component)) {
            if (hasCycle(child, closed)) {
                return true;
            }
        }
        
        return false;
    }
    
    private void checkIsDagInBackwardDirection() {
        
    }
}
