package natlab;

import java.util.*;

public class SymbolTableFunctionEntry
{
    private String name;
    private SymbolTableScope localSymbols;
    private HashMap< String, SymbolTableFunctionEntry > nestedFunctions;
    private HashMap< String, SymbolTableFunctionEntry > localFunctions;

    //TODO-JD: Add some input output type info

    SymbolTableFunctionEntry( String n )
    {
        name = n;
        localSymbols = new SymbolTableScope();
        nestedFunctions = new HashMap< String, SymbolTableFunctionEntry >();
        localFunctions = new HashMap< String, SymbolTableFunctionEntry >();
    }
    SymbolTableFunctionEntry( String n, HashMap< String, SymbolTableFunctionEntry > lf )
    {
        name = n;
        localSymbols = new SymbolTableScope();
        nestedFunctions = new HashMap< String, SymbolTableFunctionEntry >();
        localFunctions = lf;
    }
    SymbolTableFunctionEntry( String n, SymbolTableScope st )
    {
        name = n;
        localSymbols = st;
        nestedFunctions = new HashMap< String, SymbolTableFunctionEntry >();
        localFunctions = new HashMap< String, SymbolTableFunctionEntry >();
    }
    SymbolTableFunctionEntry( String n, SymbolTableScope st, HashMap< String, SymbolTableFunctionEntry > lf )
    {
        name = n;
        localSymbols = st;
        nestedFunctions = new HashMap< String, SymbolTableFunctionEntry >();
        localFunctions = lf;
    }

}