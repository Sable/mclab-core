package natlab;

import java.util.*;


public class SymbolTable
{
    //hash map for lookup with symbol name
    private HashMap<String,SymbolTableEntry> symTable;
    //hash map for lookup with original name of symbol
    //contains hashmaps indexed by symbol names.
    //This is because each original name can have many symbol names associated
    //with it
    private HashMap<String,HashMap<String, SymbolTableEntry> > onameTable ;

    SymbolTable(){
        symTable = new HashMap<String,SymbolTableEntry>();
        onameTable = new HashMap<<String,HashMap<String,SymbolTableEntry> >();
    }

    public boolean addSymbol(SymbolTableEntry e){
        String symbol = e.getSymbol();
        String oname = e.getOriginal();
        
        HashMap<String,SymbolTableEntry> onameEntries;

        //insert new entry to symTable
        //check if symbol already had an entry associated with it
        //if it did, revert back to old symbol and return false
        //TODO-JD Maybe throwing an error would be better?
        SymbolTableEntry old = symTable.put( symbol, e );
        if( old != null ){
            symTable.put( symbol, e );
            return false;
        }

        //Since we did not return, assume safe to insert into onameTable
        onameEntries = onameTable.get( oname );
        if( onameEntries == null ){
            //if oname had no associated entries, create new table for entries
            onameEntries = new HashMap<String, SymbolTableEntry>();
            onameTable.put( oname, onameEntries );
        }
        onameEntries.put( symbol, e );
}