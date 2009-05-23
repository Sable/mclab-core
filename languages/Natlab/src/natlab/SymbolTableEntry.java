package natlab;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import natlab.ast.*;
//import java.io.StringBuffer;

public class SymbolTableEntry
{
    private String symbol;
    private String original;
    private ASTNode declLocation;
    private ASTNode nodeLocation;
    private ASTNode orgNodeLocation;
    private SymbolTableEntry orgNodeEntry;
    private LiteralExpr value=null;		// max value
    private LiteralExpr valueMin=null;	// min value 
    private String valueStr="";			// min value 
    private boolean isConstant=false;
    public boolean isFirmType=false;		// if it's true, then its dimensions cannot be changed
    // The old way is 'value' just keep the constant value,
    // right now, value can store StringLiteralExpr
    
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
        // declLocation need to be set by calling setDeclLocation
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
    public void setOrgNodeLocation(ASTNode n){orgNodeLocation=n;}
    public void setOrgNodeEntry(SymbolTableEntry e){orgNodeEntry=e;}

    public String getSymbol(){return symbol;}
    public String getOriginal(){return original;}
    public ASTNode getDeclLocation(){return declLocation;}
    public ASTNode getNodeLocation(){return nodeLocation;}
    public ASTNode getOrgNodeLocation(){return orgNodeLocation;}
    public SymbolTableEntry getOrgNodeEntry(){return orgNodeEntry;}

    // Save value in the Symbol table, value can be Int/FP/String literal	-JL 12.21
    static int count = 0;
    public void setValue(LiteralExpr v) {
    	value = v;
    	valueMin = v;
    }
    // For loop index variable, i=1:n, it may need the range
    public void setValue(LiteralExpr v, LiteralExpr vmin) {	 
    	value = v;
    	valueMin = vmin;
    }
    public void setMinValue(LiteralExpr v) {	 
    	valueMin = v;
    }
    public void setMaxValue(LiteralExpr v) {	 
    	value = v;
    }
    public LiteralExpr getValue() {
    	return value;
    }
    public LiteralExpr getValueMin() {	
    	return valueMin;
    }
    public boolean isConstant() {
    	// return value!=null;	// The old way is 'value' just keep the constant value, 
    	return isConstant;
    }
    public void setConstant(boolean flag) {
    	isConstant = flag;
    }
   
   
    public boolean getXML(Document doc, Element parent)
    {   
        long uid = -1;
        if( declLocation != null ){
            uid = declLocation.getuID();
        }

    	Element e = doc.createElement("Entry");
    	e.setAttribute("symbol", symbol);
    	e.setAttribute("original", original);
    	e.setAttribute("decl", Long.toString(uid));
    	
		parent.appendChild(e);
        return true;
    }
}