package net.coderodde.circuits;

public final class Utils {

    private Utils() {}
    
    public static String toBinaryString(boolean... bits) {
        StringBuilder sb = new StringBuilder(bits.length);
        
        for (int i = 0; i < bits.length; ++i) {
            sb.append(bits[i] ? '1' : '0');
        }
        
        return sb.toString();
    }
}
