package natlab.mc4;

import java.io.File;
import java.util.*;

import natlab.mc4.symbolTable.*;
import natlab.toolkits.analysis.varorfun.*;
import natlab.toolkits.rewrite.*;
import natlab.toolkits.rewrite.threeaddress.*;

import ast.*;

/**
 * 
 * This object represents a function with mc4.
 * It stores the AST of a function, as well as a symbol table.
 * 
 */
public class Mc4Function {
    Function function;
    SymbolTable symbolTable;
    File source;
    String name;
    HashSet<String> siblings;
    
    /**
     * Creates a Mc4 Function, given an ast and the source file.
     * This transforms the function to 3 address code and builds the symbol table.
     * 
     * @param function the AST of the function
     * @param source the file where it is from
     */
    public Mc4Function(Function function, File source) {
        this.function = function.fullCopy();
        this.source = source;
        this.symbolTable = new SymbolTable();
        this.name = function.getName();
        
        //set siblings
        siblings = new HashSet<String>(function.getSiblings().keySet());
        
        //transform to 3 address code
        transformTo3Addr();
        
        //create first symbol table
        createSymbolTable();
    }
    
    //creates and puts first order approximation of the symbol table - variable or function
    private void createSymbolTable(){
        //perform variable or function analysis on function and get result
        VFPreorderAnalysis functionAnalysis = new VFPreorderAnalysis(this.function);
        functionAnalysis.analyze();        
        VFFlowset<String, ? extends VFDatum> flowset; 
        flowset = functionAnalysis.getFlowSets().get(function);
        Mc4.debug("function "+name+" variable or function analysis result:\n"+flowset);
        
        //go through all symbols, and put them in the symbol table
        //TODO the var analysis only captures variables - get those first...
        for (ValueDatumPair<String, ? extends VFDatum> pair : flowset.toList()){
            VFDatum vf = pair.getDatum();
            String name = pair.getValue();
                        
            if (vf.isExactlyFunction()){
                symbolTable.put(name, new UnknownFunction());
            } if (vf.isExactlyAssignedVariable() || vf.isExactlyVariable()){
                symbolTable.put(name, new Variable());
            } else {
                System.err.println("symbol "+name+" in "+name+" cannot be resolved as a function or variable");
            }
        }        
        //...the rest are assumed to be functions for now
        for (String name : function.getSymbols()){
            if (!symbolTable.containsKey(name)){
                symbolTable.put(name, new UnknownFunction());
            }
        }
        
        Mc4.debug(this.toString());
        Mc4.debug("first symbol table for "+name+":"+symbolTable);
        //TODO deal with errors
    }
    
    
    //transforms the underlying AST to 3 address code
    private void transformTo3Addr(){
    	//so far 3 addr only works on Program nodes
    	FunctionList fList = new FunctionList();
    	fList.addFunction(function);
    	
    	//do transform - first left, then right
    	System.out.println("lr:");
    	LeftThreeAddressRewrite lr = new LeftThreeAddressRewrite(fList);
    	System.out.println("rr:");
    	RightThreeAddressRewrite rr = new RightThreeAddressRewrite(lr.transform());
    	this.function = ((FunctionList)rr.transform()).getFunction(0);
    	//System.out.println(rr.transform().getPrettyPrinted());
    }
    
    
    //getter methods
    public SymbolTable getSymbolTable(){ return symbolTable; }
    public String getName(){ return name; }
    public Function getAst(){ return function; }
    public File getFile(){ return source; }
    public Set<String> getSiblings(){ return siblings; }
    
    
    
    /**
     * Inlines a copy of the given function at the given node.
     * That is, replaces the given Assignment Statement with a copy of the AST of
     * the other node.
     * 
     * The Symbol table gets updated as well.
     * This will add a couple of temporaries.
     * The name expression has to have an assignment statement as a parent.
     * 
     * 
     * nargin, .. etc. replacer
     * need - ast inliner
     * need symbol table magic
     * need something that sets
     * 
     * @param otherFunction the function to be inlined
     * @param node the node which is the call to be replaced
     * 
     */
    public void inline(Mc4Function otherFunction,AssignStmt node){
    	
    	
    	//merge symbol table
    	Map renameMap = symbolTable.merge(
    			otherFunction.getSymbolTable(), otherFunction.name.substring(0,4)+"_");
    	
    	//rename
    	RenameSymbols rename = new RenameSymbols(otherFunction.getAst().copy(), renameMap);
    	
    	//acutally inline
    }
    
    
    @Override
    public String toString() {
        return 
            "Function: "+name+
            "\nfile: "+source+
            "\nSymbol table:\n"+symbolTable+
            "\ncode:\n"+function.getPrettyPrinted();
    }
}



