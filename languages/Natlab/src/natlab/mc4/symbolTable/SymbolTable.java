package natlab.mc4.symbolTable;

import java.util.*;

/**
 * A symbol table is a map from name (String) to a type (SymbolTableEntry).
 * The symbol table resolves all names, both variables and other functions called within
 * some function. Therefore an entry is either a function reference or a variable type.
 * Note that at this stage every variable has one distinct type, so it remains constant.
 * 
 * The symbol table can go through different levels of approximations, the first
 * is just the distinction between variables and functions.
 * 
 * @author ant6n
 *
 */

public class SymbolTable extends HashMap<String,SymbolType>{

    
    
    /**
     * returns the symbols whose entries pass the filter
     */
    public Set<String> getSymbols(SymbolTypeFilter filter){
        HashSet<String> set = new HashSet<String>();
        for (String name : keySet()){
            if (filter.accept(get(name))) set.add(name);
        }
        return set;
    }
    
    
    
    @Override
    public String toString() {
        //find maximum symbol length:
        int length = 0;
        for (String name: keySet()){
            if (name.length() > length) length = name.length();
        }
        
        String s = "";
        
        ArrayList<String> keys = new ArrayList<String>(keySet());
        Collections.sort(keys,String.CASE_INSENSITIVE_ORDER);
        for (String name: keys){
            s += name+":";
            for (int i = name.length(); i < length; i++) s+=' ';
            s += get(name)+"\n";
        }
        return s;
    }
    
}
