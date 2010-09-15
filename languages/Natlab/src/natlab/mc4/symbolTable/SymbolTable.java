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

public class SymbolTable extends HashMap<String,Symbol>{

    
    
    /**
     * returns the symbols whose entries pass the filter
     */
    public Set<String> getSymbols(SymbolFilter filter){
        HashSet<String> set = new HashSet<String>();
        for (String name : keySet()){
            if (filter.accept(get(name))) set.add(name);
        }
        return set;
    }
    
    
    /**
     * merges this symbol table with the given other symbol table
     * This will rename all symbols of the other symbol table that are
     * already present in this symbol table. They get renamed with
     * <prefix><original name><postfix>
     * where the prefix is a given argument, and the postfix are
     * some set of characters that ensure uniqueness.
     */
    public Map<String,String> merge(SymbolTable otherTable,String prefix){
    	for (String oldName : otherTable.keySet()){
    		String newName = oldName;
    		if (this.containsKey(oldName)){
    			//rename symbol
    			newName = getNewName(prefix + oldName);
    		}
    		put(newName,otherTable.get(oldName));
    	}
    	return null;
    }
    
    /**
     * given a name, returns the same name with some numbers added in the 
     * and such that the returned name does not exist within the symbol table.
     * If the name does not yet exist in the symbol table, it just gets returned.
     * (This may be slow - tries to find the numerical postfix with smallest positive value)
     */
    public String getNewName(String name){
    	if (containsKey(name)){
    		int i = 1;
    		while(containsKey(name+i)) i++; //keep adding number until it is unique
    		return name+i;
    	} else {
    		return name;
    	}
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
