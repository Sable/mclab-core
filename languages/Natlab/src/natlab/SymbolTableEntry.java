package natlab;

import natlab.ast.*;

public class SymbolTableEntry
{
    private String symbol;
    private String original;
    private ASTNode declLocation;

    private SymbolCount symbolCount;

	public SymbolTableEntry(String s)
	//SymbolTableEntry(String s)
    {
        symbol = s;
        original = s;

        symbolCount = new SymbolCount();
    }
    
    public SymbolTableEntry(String s, String o)
    //SymbolTableEntry(String s, String o)
    {
        symbol = s;
        original = o;

        symbolCount = new SymbolCount(); //TODO-JD: perhaps change this to search for other symbols with same original
    }

    public SymbolTableEntry split()
    {
        String newName = original + symbolCount.newSymbol();
        SymbolTableEntry se = new SymbolTableEntry( newName, original );
        se.setDeclLocation( getDeclLocation() );

        return se;
    }


    public void setSymbol(String s){symbol=s;}
    public void setOriginal(String o){original=o;}
    public void setDeclLocation(ASTNode n){declLocation=n;}

    public String getSymbol(){return symbol;}
    public String getOriginal(){return original;}
    public ASTNode getDeclLocation(){return declLocation;}
}