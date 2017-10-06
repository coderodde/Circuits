package net.coderodde.circuits;

import static net.coderodde.circuits.Utils.toBinaryString;

public final class Demo {

    public static void main(String[] args) {
        System.out.println("xor:");
        
        Circuit xor = new Circuit("xor", 2, 1);
        
        xor.addAndGate("and1");
        xor.addAndGate("and2");
        xor.addNotGate("not1");
        xor.addNotGate("not2");
        xor.addOrGate("or");
        
        xor.connect("inputPin0").to("not1");
        xor.connect("not1").toFirstPinOf("and1");
        xor.connect("inputPin1").toSecondPinOf("and1");
        
        xor.connect("inputPin1").to("not2");
        xor.connect("not2").toSecondPinOf("and2");
        xor.connect("inputPin0").toFirstPinOf("and2");
        
        xor.connect("and1").toFirstPinOf("or");
        xor.connect("and2").toSecondPinOf("or");
        xor.connect("or").to("outputPin0");
        
        for (boolean bit1 : new boolean[]{ false, true }) {
            for (boolean bit2 : new boolean[]{ false, true }) {
                System.out.println(toBinaryString(bit1, bit2) + " " + 
                        xor.doCycle(bit1, bit2)[0]);
            }
        }
        
        //// Constructing the addition circuit.
        System.out.println("\n2-bit by 2-bit addition:");
        Circuit additionCircuit = new Circuit("additionCircuit", 4, 3);
        
        Circuit xor1 = new Circuit(xor, "xor1");
        Circuit xor2 = new Circuit(xor, "xor2");
        Circuit xor3 = new Circuit(xor, "xor3");
        
        additionCircuit.addCircuit(xor1);
        additionCircuit.addCircuit(xor2);
        additionCircuit.addCircuit(xor3);
        
        additionCircuit.addAndGate("and1");
        additionCircuit.addAndGate("and2");
        additionCircuit.addAndGate("and3");
        additionCircuit.addAndGate("and4");
        
        additionCircuit.addOrGate("or1");
        additionCircuit.addOrGate("or2");
        
        // Output bit 1:
        additionCircuit.connect("inputPin0").to("xor1.inputPin0");
        additionCircuit.connect("inputPin2").to("xor1.inputPin1");
        additionCircuit.connect("xor1.outputPin0").to("outputPin2");
        
        // Carry bit:
        additionCircuit.connect("inputPin0").toFirstPinOf("and1");
        additionCircuit.connect("inputPin2").toSecondPinOf("and1");
        
        // Output bit 2:
        additionCircuit.connect("and1").to("xor2.inputPin0");
        additionCircuit.connect("inputPin1").to("xor2.inputPin1");
        additionCircuit.connect("inputPin3").to("xor3.inputPin1");
        additionCircuit.connect("xor2.outputPin0").to("xor3.inputPin0");
        additionCircuit.connect("xor3.outputPin0").to("outputPin1");
        
        // Output bit 3:
        additionCircuit.connect("inputPin1").toFirstPinOf("and2");
        additionCircuit.connect("inputPin3").toSecondPinOf("and2");
        additionCircuit.connect("and1").toFirstPinOf("and3");
        additionCircuit.connect("inputPin1").toSecondPinOf("and3");
        additionCircuit.connect("and1").toFirstPinOf("and4");
        additionCircuit.connect("inputPin3").toSecondPinOf("and4");
        
        additionCircuit.connect("and2").toFirstPinOf("or1");
        additionCircuit.connect("and3").toSecondPinOf("or1");
        additionCircuit.connect("or1").toFirstPinOf("or2");
        additionCircuit.connect("and4").toSecondPinOf("or2");
        additionCircuit.connect("or2").to("outputPin0");
        
        for (boolean bit1 : new boolean[] { false, true }) {
            for (boolean bit0 : new boolean[] { false, true }) {
                for (boolean bit3 : new boolean[] { false, true }) {
                    for (boolean bit2 : new boolean[] { false, true }) {
                        System.out.print(Utils.toBinaryString(bit1, bit0));
                        System.out.print(" + ");
                        System.out.print(Utils.toBinaryString(bit3, bit2));
                        System.out.print(" = ");
                        System.out.println(
                                Utils.toBinaryString(
                                        additionCircuit.doCycle(bit0, 
                                                                bit1, 
                                                                bit2, 
                                                                bit3)));
                    }
                }
            }
        }
    }
}
