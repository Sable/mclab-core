package natlab;

import java.util.*;

public interface SymbolTableInterface
{
    public boolean                           addSymbol( SymbolTableEntry e );
    public SymbolTableEntry                  getSymbolById( String s );
    public HashMap<String, SymbolTableEntry> getSymbolsByOName( String on );
}