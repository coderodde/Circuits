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
        
        
    }
}
