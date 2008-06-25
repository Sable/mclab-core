package natlab;

import natlab.ast.*;

public class SymbolTableEntry
{
    private String symbol;
    private String original;
    private ASTNode declLocation;

    SymbolTableEntry(String s)
    {
        symbol = s;
    }
    SymbolTableEntry(String s, String o)
    {
        symbol = s;
        original = o;
    }

    public void setSymbol(String s){symbol=s;}
    public void setOriginal(String o){original=o;}
    public void setDeclLocation(ASTNode n){declLocation=n;}

    public String getSymbol(){return symbol;}
    public String getOriginal(){return original;}
    public ASTNode getDeclLocation(){return declLocation;}
}