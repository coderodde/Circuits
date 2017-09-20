package net.coderodde.circuits;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import net.coderodde.circuits.components.AbstractCircuitComponent;
import net.coderodde.circuits.components.support.AndGate;
import net.coderodde.circuits.components.support.InputGate;
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
    
    private int modificationCount = 0;
    
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
        checkNewGateName(notGateName);
        NotGate notGate = new NotGate();
        componentMap.put(notGateName, notGate);
    }
    
    public void addAndGate(String andGateName) {
        checkNewGateName(andGateName);
        AndGate andGate = new AndGate();
        componentMap.put(andGateName, andGate);
    }
    
    public void addOrGate(String orGateName) {
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
                    "The circuit name (" + name + ")has illegal prefix \"" +
                    INPUT_PIN_NAME_PREFIX + "\".");
        }
        
        if (name.startsWith(OUTPUT_PIN_NAME_PREFIX)) {
            throw new IllegalArgumentException(
                    "The circuit name (" + name + ")has illegal prefix \"" + 
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
            }
        }
    }
}
