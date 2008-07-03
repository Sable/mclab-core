package natlab;

import java.util.*;

public class SymbolTableFunctionEntry
{
    private String name;
    private SymbolTableScope localSymbols;
    private HashMap< String, SymbolTableFunctionEntry > nestedFunctions;
    private HashMap< String, SymbolTableFunctionEntry > localFunctions;
    private HashMap< String, SymbolTableFunctionEntry > parentLocals;

    //TODO-JD: Add some input output type info

    SymbolTableFunctionEntry( String n )
    {
        name = n;
        localSymbols = new SymbolTableScope();
        nestedFunctions = new HashMap< String, SymbolTableFunctionEntry >();
        localFunctions = new HashMap< String, SymbolTableFunctionEntry >();
        parentLocals = null;
    }
    SymbolTableFunctionEntry( String n, HashMap< String, SymbolTableFunctionEntry > lf )
    {
        name = n;
        localSymbols = new SymbolTableScope();
        nestedFunctions = new HashMap< String, SymbolTableFunctionEntry >();
        localFunctions = lf;
        parentLocals = null;
    }
    SymbolTableFunctionEntry( String n, SymbolTableScope st )
    {
        name = n;
        localSymbols = st;
        nestedFunctions = new HashMap< String, SymbolTableFunctionEntry >();
        localFunctions = new HashMap< String, SymbolTableFunctionEntry >();
        parentLocals = null;
    }
    SymbolTableFunctionEntry( String n, SymbolTableScope st, HashMap< String, SymbolTableFunctionEntry > lf )
    {
        name = n;
        localSymbols = st;
        nestedFunctions = new HashMap< String, SymbolTableFunctionEntry >();
        localFunctions = lf;
        parentLocals = null;
    }


    /*SymbolTableFunctionEntry( String n, HashMap< String, SymbolTableFunctionEntry > pl )
    {
        name = n;
        localSymbols = new SymbolTableScope();
        nestedFunctions = new HashMap< String, SymbolTableFunctionEntry >();
        localFunctions = new HashMap< String, SymbolTableFunctionEntry >();
        parentLocals = pl;
        }*/
    SymbolTableFunctionEntry( String n, HashMap< String, SymbolTableFunctionEntry > lf, HashMap< String, SymbolTableFunctionEntry > pl )
    {
        name = n;
        localSymbols = new SymbolTableScope();
        nestedFunctions = new HashMap< String, SymbolTableFunctionEntry >();
        localFunctions = lf;
        parentLocals = pl;
    }
    /*SymbolTableFunctionEntry( String n, SymbolTableScope st, HashMap< String, SymbolTableFunctionEntry > pl )
    {
        name = n;
        localSymbols = st;
        nestedFunctions = new HashMap< String, SymbolTableFunctionEntry >();
        localFunctions = new HashMap< String, SymbolTableFunctionEntry >();
        parentLocals = pl;
        }*/
    SymbolTableFunctionEntry( String n, SymbolTableScope st, 
                              HashMap< String, SymbolTableFunctionEntry > lf, 
                              HashMap< String, SymbolTableFunctionEntry > pl )
    {
        name = n;
        localSymbols = st;
        nestedFunctions = new HashMap< String, SymbolTableFunctionEntry >();
        localFunctions = lf;
        parentLocals = pl;
    }


    public String getName(){ return name; }
    public SymbolTableScope getLocalsymbols(){ return localSymbols; }
    public HashMap< String, SymbolTableFunctionEntry > getNestedfunctions(){ return nestedFunctions; }
    public HashMap< String, SymbolTableFunctionEntry > getLocalfunctions(){ return localFunctions; }
    public HashMap< String, SymbolTableFunctionEntry > getParentlocals(){ return parentLocals; }

    public void setName( String n ){ name = n; }
    public void setLocalSymbols( SymbolTableScope ls ){ localSymbols = ls; }
    public void setNestedFunctions( HashMap< String, SymbolTableFunctionEntry >  nf){ nestedFunctions = nf; }
    public void setLocalFunctions( HashMap< String, SymbolTableFunctionEntry > lf){ localFunctions = lf; }
    public void setParentLocals( HashMap< String, SymbolTableFunctionEntry > pl){ parentLocals = pl; }


}