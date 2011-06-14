// =========================================================================== //
//                                                                             //
// Copyright 2008-2011 Andrew Casey, Jun Li, Jesse Doherty,                    //
//   Maxime Chevalier-Boisvert, Toheed Aslam, Anton Dubrau, Nurudeen Lameed,   //
//   Amina Aslam, Rahul Garg, Soroush Radpour, Olivier Savary Belanger,        //
//   Laurie Hendren, Clark Verbrugge and McGill University.                    //
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
//   limitations under the License.                                            //
//                                                                             //
// =========================================================================== //

package natlab;

import java.util.*;

public class SymbolTableScope implements SymbolTableInterface{
    // hash map for lookup with symbol name
    public HashMap<String, SymbolTableEntry> symTable;
    // hash map for lookup with original name of symbol
    // contains hashmaps indexed by symbol names.
    // This is because each original name can have many symbol names associated
    // with it
    public HashMap<String, HashMap<String, SymbolTableEntry>> onameTable;

    // The set of variables (in upper case) current in the symbol table,
    // This is used by McFor for fast checking variable in case insensitive way. 
    public HashSet<String> varUpperCaseSet = new HashSet<String>();
    // This contains all variable names defined in the function.
    public HashSet<String> nameSet=null;

    public SymbolTableScope() {
    //SymbolTableScope() {
        symTable = new HashMap<String, SymbolTableEntry>();
        onameTable = new HashMap<String, HashMap<String, SymbolTableEntry>>();
    }

    public boolean addSymbol(SymbolTableEntry e) {
        String symbol = e.getSymbol();
        String oname = e.getOriginal();

        HashMap<String, SymbolTableEntry> onameEntries;

        // insert new entry to symTable
        // check if symbol already had an entry associated with it
        // if it did, revert back to old symbol and return false
        // TODO-JD Maybe throwing an error would be better?

        SymbolTableEntry old = symTable.put(symbol, e);
        if (old != null) {
        	symTable.put(symbol, old);	// put back the old one.	
            return false;
        }
        // Since we did not return, assume safe to insert into onameTable
        onameEntries = onameTable.get(oname);
        if (onameEntries == null) {
            // if oname had no associated entries, create new table for entries
            onameEntries = new HashMap<String, SymbolTableEntry>();
            onameTable.put(oname, onameEntries);
        }
        onameEntries.put(symbol, e);
        
        varUpperCaseSet.add(symbol.toUpperCase());
        return true;
    }

    public SymbolTableEntry getSymbolById(String s)
    {
        return symTable.get(s);
    }
    public HashMap<String, SymbolTableEntry> getSymbolsByOName(String on)
    {
        return onameTable.get(on);
    }

    /* 
       splitSymbol will take a given symbol, create a new symbol with 
       a related name, populate the symbol table entry with the correct 
       information. 
      
       returns new entry

       Idealy some housekeeping will be done. This would include creating a 
       new decleration node site, and perhaps updating some use and def chains
    */
    public SymbolTableEntry splitSymbol(SymbolTableEntry se)
    {
        SymbolTableEntry newSe = se.split();
        addSymbol( newSe );  //TODO-JD: would be good to throw error or have some recovery here if addSymbol fails
        return newSe;
    }
}