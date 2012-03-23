// =========================================================================== //
//                                                                             //
// Copyright 2011 Anton Dubrau and McGill University.                          //
//                                                                             //
//   Licensed under the Apache License, Version 2.0 (the "License");           //
//   you may not use this file except in compliance with the License.          //
//   You may obtain a copy of the License at                                   //
//                                                                             //
//       http://www.apache.org/licenses/LICENSE-2.0                            //
//                                                                             //
//   Unless required by applicable law or agreed to in writing, software       //
//   distributed under the License is distributed on an "AS IS" BASIS,         //
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  //
//   See the License for the specific language governing permissions and       //
//  limitations under the License.                                             //
//                                                                             //
// =========================================================================== //

package natlab.tame.mc4.symbolTable;

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
	 * a SymbolFilfer that filters all references to other functions
	 */
	public static final SymbolFilter NON_BUILTIN_FUNCTIONS = new SymbolFilter() {
		public boolean accept(Symbol symbolType) {
			if (!(symbolType instanceof FunctionReferenceType)) return false;
			return !((FunctionReferenceType)symbolType).getFunctionReference().isBuiltin();
		}
	};
    
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
     * already present in this symbol table - unless they refer to the same function.
     * They get renamed to <prefix><original name><postfix>,
     * where the prefix is a given argument, and the postfix are
     * some set of characters that ensure uniqueness.
     * 
     * The other symbol table does not get altered
     */
    public Map<String,String> merge(SymbolTable otherTable,String prefix){
    	HashMap<String,String> renameMap = new HashMap<String,String>();
    	for (String name : otherTable.keySet()){
    		if (this.containsKey(name)){ //name conflict
    			try{
        		System.out.println(name+" "+
        				(get(name) instanceof FunctionReferenceType)
        				+" "+ (otherTable.get(name) instanceof FunctionReferenceType)
        				+" "+ ((FunctionReferenceType)get(name)).equals(otherTable.get(name))
        		);}catch(ClassCastException e){}
    			
    			if ((get(name) instanceof FunctionReferenceType)
    				&& (otherTable.get(name) instanceof FunctionReferenceType)
    				&& ((FunctionReferenceType)get(name)).equals(otherTable.get(name))){
    				//both names refer to the same function - no need to put it there
    			} else {    			
    				//rename symbol and put
    				String newName = getNewName(prefix + name);
    				renameMap.put(name, newName);
    				put(newName,otherTable.get(name).copy());
    			}
    		} else { //no name conflict
    			put(name,otherTable.get(name).copy());
    		}
    	}
    	return renameMap;
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
