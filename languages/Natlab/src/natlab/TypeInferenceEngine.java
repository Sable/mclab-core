package natlab;

import java.io.*;

import natlab.ast.*;

import natlab.SymbolTableScope;
import natlab.SymbolTableEntry;
import annotations.ast.MatrixType;
import annotations.ast.Type;
import annotations.ast.PrimitiveType;
import annotations.ast.Size;

/**
 * A utility for TypeInferenceEngine
 * 
 * This tool prints out the parse tree of the program of the input file,
 * and currently, it also prints out the structured string of the program.
 * The output file is named as: basename + ".tree" 
 */
public class TypeInferenceEngine {
	public TypeInferenceEngine() {
	}
	
	public static Type inferType(SymbolTableScope stScope, NameExpr node, ASTNode varNode) 
	{
		String varName = node.getName().getID();
		Type varType = null;
		// Handle built-in values: pi, NaN, ...
		if(varName.equals("pi")) {
			// Should every program add a line of pi=3.
			varType = new PrimitiveType("double");
		} else {		
			// Handle variables
	        SymbolTableEntry stEntry = stScope.getSymbolById(node.getName().getID());
	        if(stEntry==null) {
	    		System.err.println("[inferType] SymbolTableEntry of ["+node.getName().getID()+"] should not be null!");
	        	return null;
	        }
	        if(stEntry.getDeclLocation()!=null) {
	        	VariableDecl varDecl = (VariableDecl) stEntry.getDeclLocation();
	        	varType = varDecl.getType();
	        } else {
	    		// assert(varDecl.getType()!=null) 
	    		// This case, the Variable doesn't initialize, therefore 
	    		// the parser should report error
	        	System.err.println("Null varDecl"+node);
	        	return null;
	        }
		}
        if (varNode instanceof ParameterizedExpr && 
        		!(varType instanceof MatrixType)) 
        	varType = adjustVariableType(varType, (ParameterizedExpr) varNode);
    	// System.out.println("inferType="+varType+ " ["+(varType==null?"":varType.getName())+"]1");
    	return varType;
	}
	public static Type inferType(SymbolTableScope stScope, 
			BinaryExpr expr, Type lhsType, Type rhsType, ASTNode varNode)
	{
		// should do some type comparision to determinate the type promotion...
		// if(expr instanceof PlusExpr)
		if(lhsType!=null && lhsType.getName().equals("double")) {
			return lhsType;
		} else if(rhsType!=null && rhsType.getName().equals("double")) {
			return rhsType;
		}
		return lhsType;
	}

	public static Type inferType(SymbolTableScope stScope, 
			RangeExpr expr, ASTNode varNode)
	{
		Type varType = null;
		int extern = -99;
		String strExtern;
		
		if(!(expr.getLower() instanceof IntLiteralExpr)
			|| 	!(expr.getUpper() instanceof IntLiteralExpr)) {
			System.err.println("inferType-BinaryExpr:not IntLiteralExpr:"+expr.getStructureString());
			strExtern = expr.getLower()+":"+expr.getUpper();
		} else {
			extern = 1 + ((IntLiteralExpr) expr.getUpper()).getValue().getValue().intValue()
				- ((IntLiteralExpr) expr.getLower()).getValue().getValue().intValue();			
		}
		java.util.List<Integer> dim = new java.util.ArrayList<Integer>();
		if(extern!=-99) {
			dim.add(extern);
		} else {
			dim.add(99);
		}
		MatrixType mType = new MatrixType(new PrimitiveType("double"));
		mType.setSize(new Size(dim));
		return mType;
	}

	public static Type inferType(SymbolTableScope stScope, 
			ParameterizedExpr expr, ASTNode varNode)
	{
		Type varType = null;
		if(!(expr.getTarget() instanceof NameExpr)) {
			System.err.println("inferType-ParameterizedExpr:"+expr.getStructureString());
			return null;
		}
        SymbolTableEntry stEntry = stScope.getSymbolById(
        		((NameExpr) expr.getTarget()).getName().getID());
        if(stEntry!=null) {
	        expr.isVariable = true;
	    	VariableDecl varDecl = (VariableDecl) stEntry.getDeclLocation();
	    	varType = varDecl.getType();
        } else {
        	// This is a function
    		expr.isVariable = false;

	        List<Expr> args = expr.getArgs();
            String fname = expr.getTarget().getFortran();

            // TODO: following may be need union-type, ...
            if(fname.equalsIgnoreCase("zeros") 
            		|| fname.equalsIgnoreCase("ones")) {
            	// type could be int/double, union?
    			varType = createMatrixType("double", expr);
            } else if(fname.equalsIgnoreCase("randn")) { 
    			varType = createMatrixType("double", expr);
            } else if(fname.equalsIgnoreCase("num2str")) {
            	varType =  new annotations.ast.PrimitiveType("character(50)");
            	//   +(getValue().length()+1) ?? 
            } 
        }
        // TODO: in case: x(1)=1; => x int[1] 
        //	x(10)=2;, should update the extent to x int[10] 
        if (varNode instanceof ParameterizedExpr && 
        		!(varType instanceof MatrixType)) 
        	varType = adjustVariableType(varType, (ParameterizedExpr) varNode);
    	// System.out.println("inferType="+varType+ " ["+(varType==null?"":varType.getName())+"]3");
		return varType;
	}
    // Set matrix type for variable like: X(10) = ..., X(m)=..
	private static Type adjustVariableType(Type varType, ParameterizedExpr expr) {
		Type mType = varType;
		String typeName = "double";
    	if(varType!=null) {
    		typeName = varType.getName();
    	}
		mType = createMatrixType(typeName, (ParameterizedExpr) expr);
        return mType;
	}
	
	// Create a matrix data type for an ParameterizedExpr 
	// Working for rand/rands/zeros/ones
	// Y = rand(n) returns an n-by-n matrix of values derived as described above.
	// Y = rand(m,n) or Y = rand([m n]) returns an m-by-n matrix of the same.
	public static MatrixType createMatrixType(String PrimitiveTypeName, ParameterizedExpr expr) {
        List<Expr> args = expr.getArgs();
		MatrixType mType = new MatrixType(
				new PrimitiveType(PrimitiveTypeName));
		java.util.List<Integer> dim = new java.util.ArrayList<Integer>();
		
        for(Expr arg : args) {
        	// TODO: Assumption: all args are integer, IntLiteralExpr
        	// should support String 
        	if(arg instanceof IntLiteralExpr) {
        		if(((IntLiteralExpr) arg).getValue().getValue().intValue()==1) {
        			// column matrix/row matrix
        			// currently treat as same one dimension array
        		} else {
	        		dim.add(((natlab.IntNumericLiteralValue) 
	        			((IntLiteralExpr) arg).getValue()).getValue().intValue());
	        		if(args.getNumChild()==1) {
		        		dim.add(((natlab.IntNumericLiteralValue) 
			        			((IntLiteralExpr) arg).getValue()).getValue().intValue());
	        		}
        		}
        	} else {
	        	// TODO: Dynamic type, current Type class not support
	        	// else if(arg instanceof NameExpr) 	 
	        	//     dim.add(((NameExpr) arg).getName().getID());
        		// here is temporary solution
        		dim.add(99);
        	}
        }
		mType.setSize(new Size(dim));
		return mType;		
	}
}
