import java.util.*;

public class SymbolTable {
    // fields
    private HashMap<String, Integer> hashmap;

    // constructor
    public SymbolTable () {
        hashmap = new HashMap<String, Integer>();
        hashmap.put("R0", Integer.valueOf(0));
        hashmap.put("R1", Integer.valueOf(1));
        hashmap.put("R2", Integer.valueOf(2));
        hashmap.put("R3", Integer.valueOf(3));
        hashmap.put("R4", Integer.valueOf(4));
        hashmap.put("R5", Integer.valueOf(5));
        hashmap.put("R6", Integer.valueOf(6));
        hashmap.put("R7", Integer.valueOf(7));
        hashmap.put("R8", Integer.valueOf(8));
        hashmap.put("R9", Integer.valueOf(9));
        hashmap.put("R10", Integer.valueOf(10));
        hashmap.put("R11", Integer.valueOf(11));
        hashmap.put("R12", Integer.valueOf(12));
        hashmap.put("R13", Integer.valueOf(13));
        hashmap.put("R14", Integer.valueOf(14));
        hashmap.put("R15", Integer.valueOf(15));
        hashmap.put("SP", Integer.valueOf(0));
        hashmap.put("LCL", Integer.valueOf(1));
        hashmap.put("ARG", Integer.valueOf(2));
        hashmap.put("THIS", Integer.valueOf(3));
        hashmap.put("THAT", Integer.valueOf(4));
        hashmap.put("SCREEN", Integer.valueOf(16384));
        hashmap.put("KBD", Integer.valueOf(24576));
    }
    
    // methods
    public void addEntry(String symbol, int address) {
        hashmap.put(symbol, Integer.valueOf(address));
    }

    public boolean contains(String symbol) {
        return hashmap.containsKey(symbol);
    }

    public int getAddress (String symbol) {
        return hashmap.get(symbol);
    }
}