package natlab;

import java.util.*;

public class SymbolTableFunctionEntry
{
    private String name;
    private SymbolTableScope localSymbols;
    private SymbolTableFunctionEntry parent;
    private HashMap< String, SymbolTableFunctionEntry > nestedFunctions;
    private HashMap< String, SymbolTableFunctionEntry > localFunctions;

    //TODO-JD: Add some input output type info

    //locals=siblings + parent's locals         siblings = parent's nested
    //nested = children

    //Do constructors need to be changed to reflect that the locals will always be a parent's nested?

    SymbolTableFunctionEntry( String n )
    {
        name = n;
        localSymbols = new SymbolTableScope();
        nestedFunctions = new HashMap< String, SymbolTableFunctionEntry >();
        localFunctions = new HashMap< String, SymbolTableFunctionEntry >();
        parent = null;
    }
    SymbolTableFunctionEntry( String n, HashMap< String, SymbolTableFunctionEntry > lf )
    {
        name = n;
        localSymbols = new SymbolTableScope();
        nestedFunctions = new HashMap< String, SymbolTableFunctionEntry >();
        localFunctions = lf;
        parent = null;
    }
    SymbolTableFunctionEntry( String n, SymbolTableScope st )
    {
        name = n;
        localSymbols = st;
        nestedFunctions = new HashMap< String, SymbolTableFunctionEntry >();
        localFunctions = new HashMap< String, SymbolTableFunctionEntry >();
        parent = null;
    }
    SymbolTableFunctionEntry( String n, SymbolTableScope st, HashMap< String, SymbolTableFunctionEntry > lf )
    {
        name = n;
        localSymbols = st;
        nestedFunctions = new HashMap< String, SymbolTableFunctionEntry >();
        localFunctions = lf;
        parent = null;
    }
    //For these, should the locals be set to parent's nested? Probably
    SymbolTableFunctionEntry( String n, SymbolTableFunctionEntry p )
    {
        name = n;
        localSymbols = new SymbolTableScope();
        nestedFunctions = new HashMap< String, SymbolTableFunctionEntry >();
        localFunctions = new HashMap< String, SymbolTableFunctionEntry >();
        parent = p;
    }
    SymbolTableFunctionEntry( String n, SymbolTableFunctionEntry p, 
                              HashMap< String, SymbolTableFunctionEntry > lf )
    {
        name = n;
        localSymbols = new SymbolTableScope();
        nestedFunctions = new HashMap< String, SymbolTableFunctionEntry >();
        localFunctions = lf;
        parent = p;
    }
    SymbolTableFunctionEntry( String n, SymbolTableFunctionEntry p, SymbolTableScope st )
    {
        name = n;
        localSymbols = st;
        nestedFunctions = new HashMap< String, SymbolTableFunctionEntry >();
        localFunctions = new HashMap< String, SymbolTableFunctionEntry >();
        parent = p;
    }
    /*SymbolTableFunctionEntry( String n, SymbolTableFunctionEntry p, 
                              SymbolTableScope st, HashMap< String, SymbolTableFunctionEntry > lf )
    {
        name = n;
        localSymbols = st;
        nestedFunctions = new HashMap< String, SymbolTableFunctionEntry >();
        localFunctions = lf;
        parent = p;
        }*/
    


    public String getName(){ return name; }
    public SymbolTableScope getLocalsymbols(){ return localSymbols; }
    public HashMap< String, SymbolTableFunctionEntry > getNestedfunctions(){ return nestedFunctions; }
    public HashMap< String, SymbolTableFunctionEntry > getLocalfunctions(){ return localFunctions; }
    public SymbolTableFunctionEntry getParent(){ return parent; }

    public void setName( String n ){ name = n; }
    public void setLocalSymbols( SymbolTableScope ls ){ localSymbols = ls; }
    public void setNestedFunctions( HashMap< String, SymbolTableFunctionEntry >  nf){ nestedFunctions = nf; }
    public void setLocalFunctions( HashMap< String, SymbolTableFunctionEntry > lf){ localFunctions = lf; }
    //Remove setParent?
    //public void setParent( SymbolTableFunctionEntry p ){ parent = p; }

    /* When a nested function is added it's locals is set to be equal to the nested 
       functions of the current function entry. 
       Parent's nested = Child's locals 
       Currently this does no checking to see if the child had a locals already set.
       Such a check should either be a check for null or a check for object equality. 
       One could also do a deeper check */
       
    public boolean addNestedFunction( SymbolTableFunctionEntry n )
    {
        SymbolTableFunctionEntry oldN = nestedFunctions.put(n.name, n);
        if( oldN == null ){
            n.localFunctions = nestedFunctions;
            n.parent = this;
            return true;
        }
        else{
            nestedFunctions.put(oldN.name, oldN);
            return false;
        }
    }
    public boolean addLocalFunction( SymbolTableFunctionEntry l )
    {
        SymbolTableFunctionEntry oldL = localFunctions.put(l.name, l);
        if( oldL == null )
            return true;
        else{
            localFunctions.put(oldL.name, oldL);
            return false;
        }
    }


}