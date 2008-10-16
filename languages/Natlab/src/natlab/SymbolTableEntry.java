package natlab;

import natlab.ast.*;
//import java.io.StringBuffer;

public class SymbolTableEntry
{
    private String symbol;
    private String original;
    private ASTNode declLocation;
    private ASTNode nodeLocation;
    private LiteralExpr value=null;
    
    private SymbolCount symbolCount;

    public SymbolTableEntry(String s)
    {
        this(s, s);
    }
    public SymbolTableEntry(String s, String o)
    {
        symbol = s;
        original = o;

        symbolCount = new SymbolCount(); 
        //TODO-JD: perhaps change this to search for other symbols with same original
    }

    public SymbolTableEntry(String s, ASTNode n)
    {
        this(s, s, n);
    }
    public SymbolTableEntry(String s, String o, ASTNode n)
    {
        symbol = s;
        original = o;

        symbolCount = new SymbolCount(); //TODO-JD: perhaps change this to search for other symbols with same original
        nodeLocation=n;
    }

    public SymbolTableEntry split()
    {
        String newName = original + symbolCount.newSymbol();
        SymbolTableEntry se = new SymbolTableEntry( newName, original, nodeLocation);
        se.setDeclLocation( getDeclLocation() );

        return se;
    }


    public void setSymbol(String s){symbol=s;}
    public void setOriginal(String o){original=o;}
    public void setDeclLocation(ASTNode n){declLocation=n;}
    public void setNodeLocation(ASTNode n){nodeLocation=n;}

    public String getSymbol(){return symbol;}
    public String getOriginal(){return original;}
    public ASTNode getDeclLocation(){return declLocation;}
    public ASTNode getNodeLocation(){return nodeLocation;}

    // Save constant value in the Symbol table	-JL 10.12
    public void setValue(LiteralExpr v) {
    	value = v;
    }
    public LiteralExpr getValue() {
    	return value;
    }
    public boolean isConstant() {
    	return value!=null;
    }
   
    public String getXML(){
        StringBuffer buf = new StringBuffer();
        long uid = -1;
        if( declLocation != null )
            uid = declLocation.getuID();

        buf.append("<Entry symbol=\""+symbol+"\" original=\""+original+"\" decl=\""+ uid + "\" />\n");
        return buf.toString();
    }
}