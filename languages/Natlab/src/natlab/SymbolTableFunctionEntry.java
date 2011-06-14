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

public class SymbolTableFunctionEntry implements SymbolTableInterface
{
    private String name;
    private SymbolTableScope localSymbols;
    private SymbolTableFunctionEntry parent;
    private HashMap< String, SymbolTableFunctionEntry > nestedFunctions;

    //TODO-JD: Add some input output type info

    //locals=siblings + parent's locals         siblings = parent's nested
    //nested = children

    //Do constructors need to be changed to reflect that the locals will always be a parent's nested?

    SymbolTableFunctionEntry( String n )
    {
        name = n;
        localSymbols = new SymbolTableScope();
        nestedFunctions = new HashMap< String, SymbolTableFunctionEntry >();
        parent = null;
    }
    SymbolTableFunctionEntry( String n, SymbolTableScope st )
    {
        name = n;
        localSymbols = st;
        nestedFunctions = new HashMap< String, SymbolTableFunctionEntry >();
        parent = null;
    }
    SymbolTableFunctionEntry( String n, SymbolTableFunctionEntry p )
    {
        name = n;
        localSymbols = new SymbolTableScope();
        nestedFunctions = new HashMap< String, SymbolTableFunctionEntry >();
        parent = p;
    }
    SymbolTableFunctionEntry( String n, SymbolTableFunctionEntry p, SymbolTableScope st )
    {
        name = n;
        localSymbols = st;
        nestedFunctions = new HashMap< String, SymbolTableFunctionEntry >();
        parent = p;
    }
    


    public String getName(){ return name; }
    public SymbolTableScope getLocalsymbols(){ return localSymbols; }
    public HashMap< String, SymbolTableFunctionEntry > getNestedfunctions(){ return nestedFunctions; }
    public SymbolTableFunctionEntry getParent(){ return parent; }

    public void setName( String n ){ name = n; }
    public void setLocalSymbols( SymbolTableScope ls ){ localSymbols = ls; }
    public void setNestedFunctions( HashMap< String, SymbolTableFunctionEntry >  nf){ nestedFunctions = nf; }
    public void setParent( SymbolTableFunctionEntry p ){ parent = p; }

    /* Implementation of SymbolTableInterface */
    public boolean addSymbol( SymbolTableEntry e )
    {
        return localSymbols.addSymbol( e );
    }
    public SymbolTableEntry getSymbolById( String s )
    {
        SymbolTableEntry se = localSymbols.getSymbolById( s );
        if( se == null )
            return parent.getSymbolById( s );
        else 
            return se;
    }
    //Currently getSymbolsByOname for a function entry is just a wrapper for the localSymbols 
    //version.
    public HashMap<String, SymbolTableEntry> getSymbolsByOName( String on )
    {
        return localSymbols.getSymbolsByOName( on );
    }
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
            // n.localFunctions = nestedFunctions;
            n.parent = this;
            return true;
        }
        else{
            nestedFunctions.put(oldN.name, oldN);
            return false;
        }
    }
    /*public boolean addLocalFunction( SymbolTableFunctionEntry l )
    {
        SymbolTableFunctionEntry oldL = localFunctions.put(l.name, l);
        if( oldL == null )
            return true;
        else{
            localFunctions.put(oldL.name, oldL);
            return false;
        }
        }*/


}