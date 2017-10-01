package net.coderodde.circuits;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class CircuitTest {
    
    @Test(expected = CycleException.class)
    public void testFindsCycle() {
        Circuit circuit = new Circuit("myCircuit", 1, 1);
        circuit.addAndGate("and1");
        circuit.connect("inputPin0").toFirstPinOf("and1");
        circuit.connect("and1").to("outputPin0");
        circuit.connect("and1").toSecondPinOf("and1");
        circuit.minimize();
    }
    
    @Test(expected = InputPinOccupiedException.class)
    public void testCannotConnectToOccupiedInput() {
        Circuit circuit = new Circuit("myCircuit", 1, 1);
        circuit.addNotGate("not");
        circuit.connect("inputPin0").to("outputPin0");
        circuit.connect("inputPin0").to("not");
        circuit.connect("not").to("outputPin0");
        circuit.minimize();
    }
    
    @Test
    public void test1() {
        Circuit circuit = new Circuit("myCircuit", 4, 1);
        
        circuit.addAndGate("and1");
        circuit.addAndGate("and2");
        circuit.addOrGate("or");
        
        circuit.connect("inputPin0").toFirstPinOf("and1");
        circuit.connect("inputPin1").toSecondPinOf("and1");
        circuit.connect("inputPin2").toFirstPinOf("and2");
        circuit.connect("inputPin3").toSecondPinOf("and2");
        circuit.connect("and1").toFirstPinOf("or");
        circuit.connect("and2").toSecondPinOf("or");
        circuit.connect("or").to("outputPin0");
        
        for (boolean bit0 : new boolean[]{ false, true }) {
            for (boolean bit1 : new boolean[]{ false, true }) {
                for (boolean bit2 : new boolean[]{ false, true }) {
                    for (boolean bit3 : new boolean[]{ false, true }) {
                        circuit.setInputBits(bit0, bit1, bit2, bit3);
                        boolean expected = (bit0 && bit1) || (bit2 && bit3);
                        circuit.doCycle();
                        boolean[] result = circuit.getOutputBits();
                        assertEquals(1, result.length);
                        assertEquals(expected, result[0]);
                    }
                }
            }
        }
    }
}
