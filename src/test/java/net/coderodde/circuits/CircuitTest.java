package net.coderodde.circuits;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class CircuitTest {
    
    @Test(expected = IllegalStateException.class)
    public void testFindsForwardCycle() {
        Circuit circuit = new Circuit("myCircuit", 1, 1);
        circuit.addAndGate("and1");
        circuit.connect("inputPin1").toFirstPinOf("and1");
        circuit.connect("and1").to("outputPin1");
        circuit.connect("and2").to("and1");
        circuit.minimize();
    }
}
