package natlab;

import java.util.ArrayList;
import java.util.HashSet;

import natlab.ast.*;

import natlab.SymbolTableScope;
import natlab.SymbolTableEntry;

import annotations.ast.ArgTupleType;
import annotations.ast.MatrixType;
import annotations.ast.Type;
import annotations.ast.PrimitiveType;
import annotations.ast.Size;

/**
 * A utility for TypeInferenceEngine
 * 
 * It's an Engine, handles request inferType(...) from different ASTNode,
 * which defined by 'TypeCollection.jrag'.
 *
 * TODO: 
 * 		1. Split transformations into another class
 */
public class TypeInferenceEngine {

	static public boolean DEBUG = false;
	static public int loopCounter = 1;

    // set inside the transposeType(), to indicate that the new inferred type 
    // (matrix type) is different to/bigger than pre-inferred, so need to adjust the previous 
    // define-node and use-node of same variable.
	static public boolean bAdjustArrayAccess = false;
	
	static final public String PHI_FUNC_NAME = "PHISSA";
	
	// TODO: replace all char by variable...
	static final public String TYPENAME_INTEGER = "int";
	static final public String TYPENAME_DOUBLE = "double";
	static final public String TYPENAME_FLOAT = "float";
	static final public String TYPENAME_LOGICAL = "logical";
	static final public String TYPENAME_COMPLEX = "complex";
	
	static final public String TYPENAME_CHARACTER = "character";

	// Constant for index
	static final int ERROR_EXTERN = -99;
	static final int UNKNOWN_EXTERN = 99;

	// A set of intrinsic function node, which is in command form
	static HashSet<NameExpr> cmdFormFuncList = new HashSet<NameExpr>();
	static SymbolTableScope gstScope;

	static public class Exception extends java.lang.Exception
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		Exception(String msg)
		{
			super(msg);
		}
	}
	public TypeInferenceEngine() {
	}
	
	public final static String TEMP_VAR_PREFIX = "tmpvar_";	
	// Purpose: create new name for renaming
	public static String getNewVarName(String orgName, ASTNode node) {
		return orgName+"_r"+node.getNodeID();
	}
	public static String getTempVarName(ASTNode node) {
		return TEMP_VAR_PREFIX+node.getNodeID();
	}

	public static Type inferType(SymbolTableScope stScope, NameExpr node, ASTNode varNode) 
	{
		String varName = node.getName().getID();
		Type varType = null;
		// Handle built-in values: pi, NaN, ...
		if(varName.equals("pi") || varName.equals("NaN")) {
			// Should every program add a line of pi=3.
			varType = new PrimitiveType(TYPENAME_DOUBLE);
		} else {		
			// Handle variables
	        SymbolTableEntry stEntry = stScope.getSymbolById(node.getName().getID());
	        if(stEntry==null) {
	    		if(DEBUG) System.err.println("[inferType] SymbolTableEntry of ["+node.getName().getID()+"] should not be null!");
	        	return inferTypeIntrinsicFunction(node);
	        }
	        if(stEntry.getDeclLocation()!=null) {
	        	VariableDecl varDecl = (VariableDecl) stEntry.getDeclLocation();
	        	varType = varDecl.getType();
	        } else {
	    		// assert(varDecl.getType()!=null) 
	    		// This case, the Variable doesn't initialize, therefore 
	    		// the parser should report error
	        	System.err.println("[inferType]-NameExpr: Null varDecl "+node.getStructureStringLessComments());
	        	return null;
	        }
		}
		// TODO LHS Range adjust, it will not happen here, !!! 
        if (varNode instanceof ParameterizedExpr && 
        		!(varType instanceof MatrixType)) {
        	System.err.println("[inferType] adjustVariableType ["+varName+"]="+varType+ " ["+(varType==null?"":varType.getName())+"]");        	
        	// varType = adjustMatrixType(varType, (ParameterizedExpr) varNode);
        }
        // DEBUG
        if(DEBUG) System.out.println("[inferType]1["+varName+"]<"+node.getNodeID()+"><"+ varNode.getNodeID()+">"+varNode.getStructureString());
        //if(varName.contains("tempvar_"))
    	if(DEBUG) System.out.println("[inferType]["+varName+"]="+varType+ " ["+(varType==null?"":varType.getName())+"]1");
    	return varType;
	}
	
	public static Type inferType(SymbolTableScope stScope, 
			UnaryExpr expr, Type opType, ASTNode varNode)
	{
		Type varType = opType;
		if(expr instanceof MTransposeExpr || expr instanceof ArrayTransposeExpr) {
			return transposeType(opType);
		}
		return varType;
	}
	// Flip a matrix about its main diagonal, turning row
	// vectors into column vectors and vice versa.
	private static Type transposeType(Type opType) {
		if(opType instanceof MatrixType) {
			Size size = ((MatrixType) opType).getSize();
			java.util.List<Integer> dim = new ArrayList<Integer>();
			MatrixType mType = new MatrixType(((MatrixType) opType).getElementType());
			if(size.getDims()!=null) {
				// Special case: [0, 1]'
				if(size.getDims().size()==1) {
					// if(DEBUG) System.out.println("[transposeType] size.getDims().size()==1");
					// Update the argument's type, and the expression will be adjusted
					// during 2nd time adjustment pass.
					// i.g.: tmp1=[1,2], tmp2=TRANSPOSE(tmp1); => tmp1={1,2}
					//		=> tmp1(1,:)=[1,2]
					dim.add(size.getDims().get(0));
					dim.add(1);
					java.util.List<Integer> newdim = new ArrayList<Integer>();
					newdim.add(1); newdim.add(size.getDims().get(0));
					((MatrixType) opType).setSize(new Size(newdim));
					// Set flag for further adjusting matrix access
					bAdjustArrayAccess = true;
					
				} else {
					dim.add(size.getDims().get(1));
					dim.add(size.getDims().get(0));
				}
				mType.setSize(new Size(dim));
				return mType;
			} else {
				java.util.List<String> vstrDims = size.getDynamicDims();
				java.util.List<String> mstrDims = new ArrayList<String>();
				for(int i=vstrDims.size()-1; i>=0; i--) {
					mstrDims.add(vstrDims.get(i));
				}
				Size msize = new Size();
				msize.setDynamicDims(mstrDims);
				mType.setSize(msize);
				return mType;
			}
		}
		return opType;
	}
	
	// Transform a command form function call into parameterized form
	public static void transformCmdFormFunction() {
		for(NameExpr node: cmdFormFuncList) {
			transform2ParameterizedExpr(node);
		}
		cmdFormFuncList.clear();	// otherwise will generate more useless expr.
	}

	private static void transform2ParameterizedExpr(Expr node, NameExpr newNode, Expr...exprs) {
    	ASTNode parent = node.getParent();
    	int loc = parent.getIndexOfChild(node);
		if(DEBUG) System.out.println("[transform2ParameterizedExpr]["+node.getNodeID()+"] "+newNode.getName().getID()+" "+loc
				+" parent=["+parent.getNodeID()+"]<"+parent+">="+exprs.length);
    	natlab.ast.List<Expr> list = new natlab.ast.List<Expr>();
		for(Expr expr: exprs) {
			list.add(expr);
		}
    	ParameterizedExpr funcExpr = new ParameterizedExpr(newNode, list);
    	parent.setChild(funcExpr, loc);
	}
	private static void transform2ParameterizedExpr(NameExpr node) {
		transform2ParameterizedExpr(node,node,new Expr[0]);
		/*
    	ASTNode parent = node.getParent();
    	int loc = parent.getIndexOfChild(node);
		if(DEBUG) System.out.println("[transform2ParameterizedExpr]["+node.getNodeID()+"] "+node.getName().getID()+" "+loc
				+" parent=["+parent.getNodeID()+"] "+parent );
    	natlab.ast.List<Expr> list = new natlab.ast.List<Expr>();
    	ParameterizedExpr funcExpr = new ParameterizedExpr(node, list);
    	parent.setChild(funcExpr, loc);
    	*/
	}
	// Transform a expression into logical expression, and update the AST
	public static Expr transform2LogicalOnly(Expr oprandExpr, Type typeExpr) {
		if(typeExpr instanceof PrimitiveType) {
			if(typeExpr.getName().equalsIgnoreCase(TYPENAME_INTEGER)
						|| typeExpr.getName().equalsIgnoreCase(TYPENAME_DOUBLE)) {
				NEExpr newExpr=new NEExpr();  
				if(typeExpr.getName().equalsIgnoreCase(TYPENAME_INTEGER)) {
					newExpr = new NEExpr(oprandExpr, new natlab.ast.IntLiteralExpr(new natlab.DecIntNumericLiteralValue("0")));
				} else {
					newExpr = new NEExpr(oprandExpr, new FPLiteralExpr(new natlab.FPNumericLiteralValue("0.0")));
				}
				return newExpr;
			}
		}
		return null;
		// Other case character(50). ..
	}
	private static void transform2Logical(BinaryExpr expr, Expr oprandExpr, Type typeExpr, boolean lhs) {
		if(typeExpr instanceof PrimitiveType) {
			if(typeExpr.getName().equalsIgnoreCase(TYPENAME_INTEGER)
						|| typeExpr.getName().equalsIgnoreCase(TYPENAME_DOUBLE)) {
				NEExpr newExpr=new NEExpr();  
				if(typeExpr.getName().equalsIgnoreCase(TYPENAME_INTEGER)) {
					newExpr = new NEExpr(oprandExpr, new natlab.ast.IntLiteralExpr(new natlab.DecIntNumericLiteralValue("0")));
				} else {
					newExpr = new NEExpr(oprandExpr, new FPLiteralExpr(new natlab.FPNumericLiteralValue("0.0")));
				}
				if(lhs) {	
					expr.setLHS(newExpr);
				} else {
					expr.setRHS(newExpr);
				}
			}
		}
		// Other case: character(50). ..
	}
	
	public static boolean isEqualType(Type type1, Type type2 ) {
		boolean bResult = false;
		if(type1!=null && type2!=null) {
			if(type1 instanceof PrimitiveType && type2 instanceof PrimitiveType) {
				bResult =(type1.getName().equals(type2.getName()));
			}
			// Otherwise, there is MatrixType ...
		}
		return bResult;
	}
	public static Type inferType(SymbolTableScope stScope, 
			BinaryExpr expr, Type lhsType, Type rhsType, ASTNode varNode)
	{
		Type varType = null;

		// (1) Logical binary operations, generate 'logical' type
		if(expr instanceof AndExpr || expr instanceof ShortCircuitAndExpr
				|| expr instanceof OrExpr || expr instanceof ShortCircuitOrExpr
				|| expr instanceof LTExpr || expr instanceof GTExpr
				|| expr instanceof LEExpr || expr instanceof GEExpr
				|| expr instanceof EQExpr || expr instanceof NEExpr)
		{
			varType = new PrimitiveType(TYPENAME_LOGICAL);
			
			if(expr instanceof AndExpr || expr instanceof ShortCircuitAndExpr
					|| expr instanceof OrExpr || expr instanceof ShortCircuitOrExpr)
			{
				// TODO: expr will be enforced as new Type
				annotations.ast.Type lType = expr.getLHS().collectType(stScope, varNode);
				if (!isEqualType(varType, lType)) {
					if(DEBUG) System.out.println("[enforceType]"+expr.getFortran()+" "+varType + " l="+lType.getName());
					transform2Logical(expr, expr.getLHS(),lType,true);
				}
				annotations.ast.Type rType = expr.getRHS().collectType(stScope, varNode);
				if (!isEqualType(varType, rType)) {
					if(DEBUG) System.out.println("[enforceType]"+expr.getFortran()+" "+varType + " r="+rType.getName());
					transform2Logical(expr, expr.getRHS(),rType,false);
				}
				if(DEBUG) System.out.println("[enforceType-OK]"+expr.getFortran()+" "+varType + " l="+lType+ " r="+rType);
			}
			
		} else {
			// (2) Mathematical binary-expression
			// Result type need be calculated based on the operand type and operator
			if(lhsType==null) {
				System.err.println("[inferType]2: LHS type is empty ["+expr.getNodeID()+"]"+expr.getFortran());
				return rhsType; 
			} else if(rhsType==null) {
				System.err.println("[inferType]2: RHS type is empty ["+expr.getNodeID()+"]"+expr.getFortran());
				return lhsType; 
			} else { // (lhsType!=null && rhsType!=null) 
				varType = lhsType;
				if(DEBUG) System.out.println("[inferType-BinaryExpr]3"+expr.getFortran()+lhsType+" : "+rhsType);
				if((lhsType instanceof MatrixType) && (rhsType instanceof MatrixType)){
					// Check the conform-able when either one is a matrix!					
					java.util.List<Integer> dim = new ArrayList<Integer>();
					java.util.List<Integer> lDims = ((MatrixType) lhsType).getSize().getDims();
					java.util.List<Integer> rDims = ((MatrixType) rhsType).getSize().getDims();
					if(expr instanceof MTimesExpr) {
						lDims = convertVectorDimension(lDims);
						rDims = convertVectorDimension(rDims);
						// At least one is 2 dimension
						if(lDims.size()==2 && rDims.size()==2) {
							if(lDims.get(1)==rDims.get(0)) {
								dim.add(lDims.get(0));
								dim.add(rDims.get(1));
								//Even if(dim.get(0)==1  && dim.get(1)==1), it's a 1x1, NOT a scalar 
								MatrixType mType = new MatrixType(new PrimitiveType(TYPENAME_DOUBLE));
								mType.setSize(new Size(dim));
								if(DEBUG) System.out.println("[inferType-BinaryExpr] Matrix "+dim.get(0)+"*"+dim.get(1));
								return mType;

							}
						}
						if(DEBUG) System.out.println("[inferType-BinaryExpr]"+" two operand types are not conformable! ");

					} else if((expr instanceof PlusExpr) || (expr instanceof ETimesExpr)
								|| (expr instanceof EDivExpr)) {
						// Check the conform-able, (two operands should have same size/dimension)
						boolean isConformable = false;
						if(lDims!=null && rDims!=null) {
							// Only static type can be compare 
							if(lDims.size()==rDims.size()) {
								isConformable = true;
								for(int i=0; i<lDims.size(); i++) {
									if(lDims.get(i)!=rDims.get(i)) {
										isConformable = false;
										break;
									}
								}
							}
						} else {
							isConformable = true;
						}
						if(isConformable) {
							// Decide the final type, equals to one operand's
							varType = lhsType;
						} else {
							System.err.println("[inferType-BinaryExpr]"+" two operand types are not conformable! ");								
						}
					} else {
						// TODO: Other operations ....
					}			
					
				// If one is matrix and another is scalar, return matrix, with larger primary type
				} else if(lhsType instanceof MatrixType) {
					varType = mergeMatrixType(rhsType, (MatrixType) lhsType);
				} else if(rhsType instanceof MatrixType) {
					varType = mergeMatrixType(lhsType, (MatrixType) rhsType);
				} else {
					varType =  mergePrimitiveType(rhsType, lhsType);;
				}
			} // if (lhsType!=null && rhsType!=null) 
			// TODO: Otherwise, we cannot decide what type it is, it's a DYNAMIC type
		}
		
        // DEBUG
    	if(DEBUG) System.out.println("[inferType]2["+expr.getStructureString()+"]=["+lhsType+ ","+rhsType+"]="
    			+varType+ " ["+(varType==null?"":varType.getName())+"]");
		return varType;
	}
	
	// If it's a row vector A(5), translate into A(1,5)
	public static java.util.List<Integer> convertVectorDimension(java.util.List<Integer> dims) {
		if (dims.size()==1) {
			dims.add(0,1);
		}
		return dims;
	}

	public static Type inferType(SymbolTableScope stScope, 
			Row expr, ASTNode varNode) 
	{
		annotations.ast.Type elementType=null;
		annotations.ast.Type tmpType=null;
		int totalNum = 0;
		// 1. Calling to next level 
        List<Expr> elements = expr.getElements();
        for(Expr element : elements) {
			tmpType = element.collectType(stScope, varNode);
			// Return biggest KNOWN element's type
			// currently just support matrix with primary type
			// 	and maximum type is DOUBLE! (which should be COMPLEX)
			if(tmpType!=null) {
				if(elementType == null)	{
					elementType = tmpType;
				} 
				if(elementType.getName().equals(TYPENAME_DOUBLE)) {
					break;
				} else if(tmpType.getName().equals(TYPENAME_DOUBLE)) {
					elementType = tmpType;
					break;
				} else if(isCharacterType(tmpType)){ 
					// Doing string concatenation ['ab','cdf'] ='abcdf'
					// And need to know length of each of them
					int loc1= tmpType.getName().indexOf("(");
					int loc2= tmpType.getName().indexOf(")");					
					totalNum += (new Integer(tmpType.getName().substring(loc1+1,loc2))).intValue();
				}
			} else {
				elementType = null;
			}
         }
        if(isCharacterType(tmpType)) {
			// Doing string concatenation ['ab','cdf'] ='abcdf'
			// And need to know length of each of them
			elementType = new annotations.ast.PrimitiveType("character("+(totalNum)+")");
		}
        
		// 2. Don't change the row itself 
		return elementType;		
	}
	
	public static Type inferType(SymbolTableScope stScope, 
			MatrixExpr expr, ASTNode varNode) 
	{
		annotations.ast.Type elementType=null;
		annotations.ast.Type tmpType=null;
		// 1. Calling to next level 
        List<Row> rows = expr.getRows();
		int maxColumn=0;
		int rowElem, totalNum=0;
        for(Row row : rows) {
        	// This one may NOT check all row, because the 'break' below
        	rowElem = row.getElements().getNumChild();
        	if(maxColumn < rowElem)
        		maxColumn = rowElem;
        		
			tmpType = row.collectType(stScope, varNode);

			// Return biggest KNOWN element's type
			// TODO: currently just support matrix with primary type
			// 	and maximum type is DOUBLE! (which should be COMPLEX)
			if(tmpType!=null) {
				if(elementType == null)	{
					elementType = tmpType;
				} 
				if(elementType.getName().equals(TYPENAME_DOUBLE)) {
					break;
				} else if(tmpType.getName().equals(TYPENAME_DOUBLE)) {
					elementType = tmpType;
					break;
				} else if(isCharacterType(tmpType)) {
					// Add each row's length 
					int loc1= tmpType.getName().indexOf("(");
					int loc2= tmpType.getName().indexOf(")");					
					totalNum += (new Integer(tmpType.getName().substring(loc1+1,loc2))).intValue();
				}
			} else {
				elementType = null;
			}
        }
        // The elementType should be a kind of PrimitiveType, here adding the dimensions
        if(elementType!=null) {
			java.util.List<Integer> dim = new java.util.ArrayList<Integer>();
			annotations.ast.Type varType; 
			
			// Doing string concatenation ['ab','cd'] ='abcd', generate Primitive-Type
	        // MATLAB doesn't allow vertical concatenation, ['ab';'cd'] is illegal!
	        if(isCharacterType(elementType)) {
				varType = new annotations.ast.PrimitiveType("character("+(totalNum)+")");
				// The whole string concatenation is happened in Row.getFortran() 
			} else {
				// Normal matrix type
				for(int i=0; i<rows.getNumChild(); i++) {
					dim.add(maxColumn);
				}
				varType = new annotations.ast.MatrixType(
						new annotations.ast.PrimitiveType(elementType.getName()));
				((annotations.ast.MatrixType) varType).setSize(new annotations.ast.Size(dim));
			}
			return varType;
        } else {
        	return null;
        }		
	}

	// # 2nd time calling RangeExpr.inferType() should not trigger following transformation
	// Because the transformation will 
	//	- Transform the floating point range expression to integer range expression
	//	- lower, upper, incr will all become Integer
	// For constant range:
	// So it relies on the constant propagation to convert variable range into constant range
	// For variable range:
	//	it relies on the transformation to create new variable for extent
	public static Type inferType(SymbolTableScope stScope, 
			RangeExpr expr, ASTNode varNode) 
	{
		boolean bTransform = false;
		String strExtent="";
		int extent = ERROR_EXTERN;
		boolean isInteger = false;
		boolean bForStmtIndex = false;
		
    	Stmt stmt = ASTToolBox.getParentStmtNode(expr);
    	if(stmt instanceof AssignStmt && stmt.getParent() instanceof ForStmt) {
    		bForStmtIndex = true;
    	}

    	//  If first element or inc element is Double, then Double
		MatrixType mType;		
		if(isFPLiteral(expr.getLower()) 
				|| isDoubleType(expr.getLower().collectType(stScope, expr))
				|| (expr.hasIncr() && (expr.getIncr()!=null && isFPLiteral(expr.getIncr())))
				|| (expr.hasIncr() && isDoubleType(expr.getIncr().collectType(stScope, expr)))
				) {
			mType = new MatrixType(new PrimitiveType(TYPENAME_DOUBLE));
			isInteger = false;
		} else if(isIntLiteral(expr.getLower()) 
				|| isIntegerType(expr.getLower().collectType(stScope, expr))) {
			mType = new MatrixType(new PrimitiveType(TYPENAME_INTEGER));
			isInteger = true;
		} else  {
			if(isIntegerType(expr.getLower().collectType(stScope, expr.getLower()))
					&& (expr.hasIncr() && isIntegerType(expr.getIncr().collectType(stScope, expr.getIncr())))) {
				isInteger = true;				
				mType = new MatrixType(new PrimitiveType(TYPENAME_DOUBLE));
			} else {
				isInteger = false;
				mType = new MatrixType(new PrimitiveType(TYPENAME_INTEGER));
			}
			// TODO: This case is quite series, need list all cases...
			// if(DEBUG) System.err.println("[inferType]-RangeExpr:CANNOT determin type of "+expr.getStructureString());
		}

		// [1] Literal:  IntLiteralExpr and FPLiteralExpr, never consider StringLiteralExpr
		if(isLiteral(expr.getLower())  && isLiteral(expr.getUpper()) 
				&& ( !expr.hasIncr() || expr.getIncr()==null || isLiteral(expr.getIncr()) ) )  { 
			if(isInteger) {
				int inc = 1;
				if(expr.hasIncr() && (expr.getIncr()!=null && isIntLiteral(expr.getIncr()))) {
					inc = getIntLiteralValue(expr.getIncr());
				}				 
				int first = getIntLiteralValue(expr.getLower());
				int last = 0;
				if(expr.getUpper() instanceof IntLiteralExpr) {
					last = getIntLiteralValue(expr.getUpper());
				} else {
					last = (int) getFPLiteralValue(expr.getUpper());
				}
				extent = 1 + Math.abs(last-first)/Math.abs(inc);
			} else {
				double inc = 1;
				if(expr.hasIncr() && (expr.getIncr()!=null)) { 
					if(isIntLiteral(expr.getIncr())) {
						inc = getIntLiteralValue(expr.getIncr());
					} else {
						inc = getFPLiteralValue(expr.getIncr());
					}
				}				 
				double first = 0;
				if(expr.getLower() instanceof IntLiteralExpr) {
					first = getIntLiteralValue(expr.getLower());
				} else {
					first = getFPLiteralValue(expr.getLower());
				}
				double last = 0;
				if(expr.getLower() instanceof IntLiteralExpr) {
					last = getIntLiteralValue(expr.getUpper());
				} else {
					last = getFPLiteralValue(expr.getUpper());
				}
				extent = (int) (1 + Math.abs(last-first)/Math.abs(inc));
			}
			java.util.List<Integer> dim = new ArrayList<Integer>();
			dim.add(extent);
			mType.setSize(new Size(dim));
			// Fortran doesn't support float/double in the range expression
			if(isInteger) {
				// Make super all lower:incr:upper are integer or not 
				if(isIntLiteral(expr.getUpper()) 
				  || isIntegerType(expr.getUpper().collectType(stScope, expr)) )
				{
					// All of three are integer, OK, no need to transform
				} else {
					// A3 = 1:6.3	==> A3 = 1:floor(6.3)
	            	natlab.ast.List<Expr> list = new natlab.ast.List<Expr>();
    				list.add(expr.getUpper());	                				
	            	ParameterizedExpr exprParam = new ParameterizedExpr(new NameExpr(new Name("floor")), list);
	            	expr.setUpper(exprParam);
				}
			} else {
				// transformDoubleRange
				// A2 = -2.5:2.5			==> A2 = (0:(6-1))+(-2.5)
				// A4 = 10:2.5:15			==> A4 = (0:(3-1))*(2.5)+10
				PlusExpr newPlus = new PlusExpr();
				newPlus.setRHS(expr.getLower());
				RangeExpr newRange = new RangeExpr();
				newRange.setLower(new IntLiteralExpr(new natlab.DecIntNumericLiteralValue("0")));
				newRange.setUpper(new IntLiteralExpr(new natlab.DecIntNumericLiteralValue(""+(extent-1))));
				if(expr.hasIncr()) {
					MTimesExpr newMTime = new MTimesExpr();
					newMTime.setLHS(newRange);
					newMTime.setRHS(expr.getIncr());
					newPlus.setLHS(newMTime);
				} else {
					newPlus.setLHS(newRange);
				}
				ASTNode parent = expr.getParent();
				int loc = parent.getIndexOfChild(expr);
				parent.setChild(newPlus, loc);
			}
		} else {
			// [2] extern are variables
			// get value for each one
			SymbolTableEntry varEntry = stScope.getSymbolById(varNode.getVarName());
			int first, last;
			// first = last = ERROR_EXTERN;
			String firstStr,lastStr,incStr;
			firstStr = lastStr = "";
			firstStr = getVariableValue(stScope, ((RangeExpr)expr).getLower());
			lastStr = getVariableValue(stScope, ((RangeExpr)expr).getUpper());
			java.util.List<String> dim = new ArrayList<String>();
			if(isInteger) {
				if(expr.hasIncr()) {
					incStr = getVariableValue(stScope, ((RangeExpr)expr).getIncr());
					strExtent = "("+lastStr+"-"+firstStr+")/"+incStr+"+1";
				} else if(firstStr!=null && lastStr!=null) {
					strExtent = lastStr+"+1"+"-"+firstStr;
				}
			} else {
				if(expr.hasIncr()) {
					incStr = getVariableValue(stScope, ((RangeExpr)expr).getIncr());
					strExtent = "floor(("+lastStr+"-"+firstStr+")/"+incStr+")+1";
				} else if(firstStr!=null && lastStr!=null) {
					strExtent = "floor("+lastStr+"-"+firstStr+")"+"+1";
				}
			}

			bTransform = false;
			// # 2nd time calling RangeExpr.inferType() should not trigger following transformation
			// Because the transformation will 
			//	- Transform the floating point range expression to integer range expression
			//	- lower, upper, incr will all become Integer
			
			// For integer, only adjust range.upper() when it is not integer expression, 
			if(isInteger && !(isIntegerType(expr.getUpper().collectType(stScope, expr.getUpper()))))  {
				natlab.ast.List<Expr> upperlist = new natlab.ast.List<Expr>();
				upperlist.add(expr.getUpper());	
            	ParameterizedExpr upperParam = new ParameterizedExpr(new NameExpr(new Name("floor")), upperlist);
				expr.setUpper(upperParam);
				bTransform = true;
			}
			if(DEBUG) System.out.println("[inferType]-2:"+strExtent + bTransform + " "+isInteger);
			// Transform the floating point range expression to integer range expression
			if(!isInteger || bTransform) {
				// Form the extent expression,
				// "floor((Upper-Lower)/Incr)+1";
				MinusExpr newMinus = new MinusExpr();
				newMinus.setLHS(expr.getUpper());
				newMinus.setRHS(expr.getLower());
				
				natlab.ast.List<Expr> list = new natlab.ast.List<Expr>();
				if(expr.hasIncr()) {
					MDivExpr newDiv = new MDivExpr();
					newDiv.setLHS(newMinus);
					newDiv.setRHS(expr.getIncr());
					list.add(newDiv);
				} else {
					list.add(newMinus);
				}
            	PlusExpr newPlus = new PlusExpr();
				if(!isInteger) {
					// Need another floor() function for the division
	            	ParameterizedExpr exprParam = new ParameterizedExpr(new NameExpr(new Name("floor")), list);
	            	newPlus.setLHS(exprParam);
				} else {
					// Integer don't need another floor() function, because it's integer division
					newPlus.setLHS(list.getChild(0));
				}
            	newPlus.setRHS(new IntLiteralExpr(new natlab.DecIntNumericLiteralValue("1")));
            	
            	// (1) Create new variable = this new parameterized-expression            	
            	NameExpr newVar = addNewAssignment(newPlus, expr, null, stScope);

            	// using that temporary assignment 
            	strExtent = newVar.getName().getID();
            	
            	// Re-build this range expression
				RangeExpr newRange = new RangeExpr();
				
				if(isInteger) {
					// A3 = 1:6.3	==> A3 = 1:floor(6.3)
					// already did above.
				} else {
					// A2 = -2.5:2.5			==> A2 = (0:(6-1))+(-2.5)
					// A4 = 10:2.5:15			==> A4 = (0:(3-1))*(2.5)+10
					newRange.setLower(new IntLiteralExpr(new natlab.DecIntNumericLiteralValue("0")));
			
					MinusExpr lowMinus = new MinusExpr();
					lowMinus.setLHS(newVar);
					lowMinus.setRHS(new IntLiteralExpr(new natlab.DecIntNumericLiteralValue("1")));				
	            	newRange.setUpper(lowMinus);
	
	            	PlusExpr lowerPlus = new PlusExpr();
					lowerPlus.setRHS(expr.getLower());				
					
					ASTNode parent = expr.getParent();
					int loc = parent.getIndexOfChild(expr);
					
					// ForStmt's index variable
	            	Stmt stmtNode = ASTToolBox.getParentStmtNode(expr);
	            	if(stmtNode.getParent() instanceof ForStmt) {
	            		List<Stmt> stmtList = ((ForStmt) stmtNode.getParent()).getStmtList();
	        			// (2) Replace by another temporary variable
	        			// Create new assignment and replace only one
	        			RangeExpr dupRange = new RangeExpr();
	        			dupRange.setUpper(newRange.getUpper());
	        			dupRange.setLower(newRange.getLower());
	        			if(newRange.hasIncr()) {
	        				dupRange.setIncr(newRange.getIncr());
	        			}	        			
	        			
	                	NameExpr newVar2 = addNewAssignment(dupRange, stmtNode, stmtNode.getParent(), stScope);
	                	// set flag: this range-expr is not used by ForStmt

						// Using new variable to create new assignment
						if(expr.hasIncr()) {
							MTimesExpr newMTime = new MTimesExpr();
							newMTime.setLHS(expr.getIncr());	// put scalar on LHS makes MTimesExpr infer faster
							newMTime.setRHS(newVar2);
							lowerPlus.setLHS(newMTime);
						} else {
							lowerPlus.setLHS(newVar2);
						}
						
	        			// (3) Need move to this assignment to inside ForStmt
	        			stmtList.insertChild(stmtNode, 0);
	        			stmtNode.setParent(stmtList);

	        			loc = stmtNode.getIndexOfChild(expr);
						stmtNode.setChild(lowerPlus, loc);
						stmtList.generateUseBoxesList();

			    		return new annotations.ast.PrimitiveType(TYPENAME_DOUBLE);
						
						
	            	} else {
						if(expr.hasIncr()) {
							MTimesExpr newMTime = new MTimesExpr();
							newMTime.setLHS(expr.getIncr());	// put scalar on LHS makes MTimesExpr infer faster
							newMTime.setRHS(newRange);
							lowerPlus.setLHS(newMTime);
						} else {
							lowerPlus.setLHS(newRange);
						}

						parent.setChild(lowerPlus, loc);	            		
	            	}
					System.err.println("[inferType]-Normal RangeExpr "+parent.getNodeID());
				}
			}
			// Using the new extent 
			dim.add(strExtent);
			Size s = new Size();
			s.setDynamicDims(dim);
			mType.setSize(s);					
			if(DEBUG) System.out.println("[inferType]-RangeExpr:NOT LiteralExpr:"+strExtent);
		}

    	if(DEBUG) System.out.println("[inferType]["+expr.getStructureString()+"]="
    			+mType+ " ["+(mType==null?"":mType.getName())+"]4");

    	if(bForStmtIndex) {
    		return new annotations.ast.PrimitiveType(TYPENAME_INTEGER);
    	} else { 
    		return mType;
    	}
	}
	
	// Two way to add the new assignment
	// 1. parentNode!=null: setChild(), replace old one
	// 2. parentNode==null: find the upper stmt-list, add to it
	public static NameExpr addNewAssignment(Expr RHS, ASTNode curStmt, ASTNode parentNode, SymbolTableScope stScope) {
		if(RHS==null || curStmt==null)
			return null;
		// Create new assignment and add to AST
		String tmpName = getTempVarName(curStmt);
		NameExpr lhs = new NameExpr(new Name(tmpName));
		AssignStmt newAssign = new AssignStmt();
		newAssign.setLHS(lhs);
		newAssign.setRHS(RHS);
		newAssign.generateUseBoxesList();

    	// Find the parent statement list node, and directly child node
		ASTNode parent = curStmt.getParent(); 
		ASTNode child = curStmt;
		if(DEBUG) System.out.println("[addNewAssignment]-1 "+parent + " " + child);
		if(parentNode!=null) {
			int loc = parentNode.getIndexOfChild(child);
			parentNode.setChild(newAssign, loc);
		} else {
			while ((parent!=null) && !(parent instanceof natlab.ast.List)) {
	    		child  = parent;
	    		parent = child.getParent(); 	   
	    	}
			if(parent==null)
				return null;

			int loc = parent.getIndexOfChild(child);
			parent.insertChild(newAssign, loc);
		}				
		
		// Add new declaration node
		Type pType = new PrimitiveType(TYPENAME_INTEGER);
    	VariableDecl tmpDecl = new VariableDecl(tmpName, pType, tmpName);
    	tmpDecl.setNodeID();

    	if(DEBUG) System.out.println("[addNewAssignment]-2 "+newAssign.getNodeID());
    	// adding them into tree;
    	while(!(parent.getChild(0) instanceof VariableDecl)) {
    		do {
    			parent=parent.getParent();
    		} while(parent!=null && !(parent instanceof List));
    		if(parent==null)
    			break;
    	}
		if(parent!=null && (parent.getChild(0) instanceof VariableDecl)) {
			parent.insertChild(tmpDecl, 0);
			((VariableDecl)parent.getChild(0)).setType(pType);
		} else {
	    	System.err.println("[addNewAssignment]"
	    			+" Cannot find the top level stmt-list to add this "+tmpDecl.getStructureString());
		}
		
		// Add new symbol table entry
		SymbolTableEntry e = new SymbolTableEntry(tmpName, tmpName, newAssign);
		e.setDeclLocation(tmpDecl);
		stScope.addSymbol(e);

		return lhs;
	}
	//-------------------------------------------------------------------------
	// Infer type for intrinsic function, in parameterized form
	// or array variable access
	public static Type inferType(SymbolTableScope stScope, 
			ParameterizedExpr expr, ASTNode varNode)
	{
		gstScope = stScope;		// used by createMatrixType()
		Type varType = null;
		if(!(expr.getTarget() instanceof NameExpr)) {
			System.err.println("[Error]inferType-ParameterizedExpr:"+expr.getStructureString());
			return null;
		}
        SymbolTableEntry stEntry = stScope.getSymbolById(
        		((NameExpr) expr.getTarget()).getName().getID());
    	// [1] This is an variable, this is a use-node, where symbol table should has its type 
        if(stEntry!=null) {
	        expr.isVariable = true;
	    	VariableDecl varDecl = (VariableDecl) stEntry.getDeclLocation();
	    	varType = varDecl.getType();
	    	// According to different case, return different type
	    	// Case 1: A = U(2,3)
	    	// 	Since this an array-access, so it's a single element type
	    	// 	 
	    	// Case 2: A = U(:,:), A = U(1,:), A = U(:) ; handle a sub-matrix
	    	if(varDecl.getType() instanceof MatrixType) {
		    	// (1) adjust RHS in case of: 
	        	// When using linear indexing, which means index misses some dimension
	        	// i.g.  A=Matrix(1*5),   expr:  A(2)  => A(1,2) 
	        	// i.g.  B=Matrix(5*1),   expr:  B(3)  => B(3,1)
	        	// i.g.  C=Matrix(2*10),  expr:  C(4)  => B(1,4)	// row major ??
	        	// 							expr:  C(15)  => B(2,5)	// row major
	    		adjustParameterizedExpr((MatrixType) varDecl.getType(), expr);
	    		
	    		// (2) Checking RHS expression type's format 
	    		PrimitiveType pType = (PrimitiveType) ((MatrixType) varDecl.getType()).getElementType();
	        	MatrixType lType = createMatrixType(pType.getName(), (ParameterizedExpr) expr);
	        	
	        	// Checking ':'; 
	        	// assumption: varType has same number of Dims as lType	        	
	        	if(lType.getSize().getDims()==null && 
	        			lType.getSize().getDynamicDims()!=null) {
	    			java.util.List<String> strDims = lType.getSize().getDynamicDims();

	    			java.util.List<Integer> viDims = ((MatrixType) varType).getSize().getDims();
	    			java.util.List<String>  vsDims = ((MatrixType) varType).getSize().getDynamicDims();
	    			java.util.List<Integer> miDims = new ArrayList<Integer>();
	    			java.util.List<String>  msDims = new ArrayList<String>();
	    				    			
    				// Type of Symbol table has explicit dimension  
	    			for(int i=0; i<strDims.size();i++) {
	    				if(strDims.get(i).equals(":")) {
	    					if(viDims!=null) {
	    						miDims.add(viDims.get(i));
	    					} else {
		    					msDims.add(vsDims.get(i));
	    					}
	    				}
	    				i++;
	    			}
	    			if(miDims.size()>0 || msDims.size()>0) {
	    				MatrixType mType = new MatrixType(pType);
	    				Size msize = new Size();
	    				if(miDims.size()>0) {
		    				msize.setDims(miDims);
	    				} else {
		    				msize.setDynamicDims(msDims);
	    				}
	    				mType.setSize(msize);
	    				varType = mType;
	    			} else {
	    				varType = pType;
	    			}	    			
	        	}
	    		
	    	} else if(isCharacterType(varType)) {
	    		int cnt = 0;
	    		for(Expr args:expr.getArgs()) {
	    			cnt ++;
	    		}
	    		if(cnt==1) {
	    			varType = new PrimitiveType("character(1)");
	    		}
		    	// TODO: ch=str(1:3), ch should according to the range of str(3), advance feature
	    	}
	    	
        } else {
        	// [2] This is a function
    		expr.isVariable = false;

	        // List<Expr> args = expr.getArgs();
            String fname = ((NameExpr)expr.getTarget()).getName().getID();

            // TODO: following may be need union-type, ...
            // For MATLAB built-in type conversion function
            // <<MATLAB-Programming 2-30>>
            if(fname.equalsIgnoreCase("int8")
            		|| fname.equalsIgnoreCase("uint8")
            		|| fname.equalsIgnoreCase("int16")
            		|| fname.equalsIgnoreCase("uint16")
            		|| fname.equalsIgnoreCase("int32")
            		|| fname.equalsIgnoreCase("uint32")
            		|| fname.equalsIgnoreCase("int64")
            		|| fname.equalsIgnoreCase("uint64")
            		|| fname.equalsIgnoreCase(TYPENAME_FLOAT)
            		|| fname.equalsIgnoreCase(TYPENAME_DOUBLE)
            		|| fname.equalsIgnoreCase(TYPENAME_LOGICAL)
            		|| fname.equalsIgnoreCase(TYPENAME_COMPLEX)
            		) { 
    			varType =  new PrimitiveType(fname);
            } else if(fname.equalsIgnoreCase("toc")) {
    			varType = new PrimitiveType(TYPENAME_DOUBLE);
            } else if(fname.equalsIgnoreCase("char")) {
    			// char() is quite complicated
    			
            } else if(fname.equalsIgnoreCase("randn") || fname.equalsIgnoreCase("rand")) { 
    			varType = createMatrixType(TYPENAME_DOUBLE, expr, true);
            } else if(fname.equalsIgnoreCase("zeros") 
            		|| fname.equalsIgnoreCase("ones")) {
            	// type could be int/double, let's set into integer, then upgrade later
    			varType = createMatrixType(TYPENAME_INTEGER, expr, true);
    			// varType = createMatrixType(TYPENAME_DOUBLE, expr, true);
            } else if(fname.equalsIgnoreCase("reshape")) {
            	// type could be int/double, union?
            	natlab.ast.List<Expr> list = new natlab.ast.List<Expr>();
            	list.add(expr.getArg(1));	list.add(expr.getArg(2));
            	// It should be a matrix type ...
            	String baseTypeName = TYPENAME_DOUBLE; 
            	Type orgType = (expr.getArg(0).collectType(stScope, varNode));
            	if(isValidType(orgType))
            		baseTypeName = orgType.getName();
            	ParameterizedExpr exprSize = new ParameterizedExpr(expr.getArg(0), list);
    			varType = createMatrixType(baseTypeName, exprSize);
    			
            } else if(fname.equalsIgnoreCase("transpose")) {	 
    			Type opType = expr.getArg(0).collectType(stScope, varNode);
    			varType = transposeType(opType);
    			
            } else if(fname.equalsIgnoreCase("str2num")) {
            	varType =  new PrimitiveType(TYPENAME_DOUBLE);
            	
            } else if(fname.equalsIgnoreCase("num2str")) {
            	varType =  new PrimitiveType("character(50)");
            	//  +(getValue().length()+1) ?? 
            } else if(fname.equalsIgnoreCase("sin") 
            		|| fname.equalsIgnoreCase("cos") 
            		|| fname.equalsIgnoreCase("abs") 
            		|| fname.equalsIgnoreCase("log") 
            		|| fname.equalsIgnoreCase("log2") 
            		|| fname.equalsIgnoreCase("log10") 
            		) { 
    			varType = expr.getArg(0).collectType(stScope, varNode);
            
            } else if(fname.equalsIgnoreCase(PHI_FUNC_NAME)) {	
    			// TODO: create union type for PHI function, abandoned now!
            	for(int i=0; i<expr.getNumArg(); i++) {
	    			Type argType = expr.getArg(i).collectType(stScope, varNode);
	            	if(argType instanceof MatrixType) {
	            		varType = argType;
	            	}
            	}
            	// some Fortran functions for retrieving command line arguments
            } else if(fname.equalsIgnoreCase("IARGC")	//GETARG
            		|| fname.equalsIgnoreCase("nargin") ) { 
    			varType = new PrimitiveType(TYPENAME_INTEGER);
           	
    			
            } else if(fname.equalsIgnoreCase("clock")) {
            	// Transformation happen in the AssignStmt.getFortran()
    			varType = createMatrixType(TYPENAME_DOUBLE, 1,6);
    			
            } else if(fname.equalsIgnoreCase("min")) {
            	// Only support, MIN(A): if A is a vector, then flat it. MIN(A(1), A(2),...)
            	varType = null;
            	Expr argExpr = expr.getArg(0);
            	if(argExpr instanceof NameExpr) {	// First argument is a variable, 
	            	String arg0 = ((NameExpr)expr.getArg(0)).getName().getID();
		    		SymbolTableEntry stEntry2 = stScope.getSymbolById(arg0);
		    		if(stEntry2!=null) {
			    		Type argType = ((VariableDecl) stEntry2.getDeclLocation()).getType();
			    		varType =  new PrimitiveType(argType.getName());
			    		
        	    		// When only one vector argument, then flat vector
			    		if(argType instanceof MatrixType && expr.getNumArg()==1) {
	    	            	natlab.ast.List<Expr> listMin = new natlab.ast.List<Expr>();
	            	    	java.util.List<Integer> argDims = ((MatrixType) argType).getSize().getDims();
	            	    	if(argDims.size()!=1) {
	        	            	System.err.println("[inferType]MIN(A)["+expr.getStructureString()+"] is not a vector!");
	            	    	} else {
	            	    		for(int i=0; i<argDims.get(0); ++i) {
	            	            	natlab.ast.List<Expr> list = new natlab.ast.List<Expr>();
	                				list.add(new natlab.ast.IntLiteralExpr(new natlab.DecIntNumericLiteralValue(""+(i+1))));	                				
	            	            	ParameterizedExpr exprSizeFunc = new ParameterizedExpr(argExpr , list);
	            	            	listMin.add(exprSizeFunc);
	            	    		}
	            	    		expr.setArgList(listMin);
	            	    	}
			    		}
		    		}
            	}
	    		if(varType == null) {
	    			varType = new PrimitiveType(TYPENAME_DOUBLE);
	    		}
	    		
            } else if(fname.equalsIgnoreCase("sqrt")) {
    			varType = new PrimitiveType(TYPENAME_DOUBLE);
    			// transform inner arguments to double
            	Expr arg0 = ((Expr)expr.getArg(0));
            	if(isIntLiteral(arg0)) {
            		double value = (double)getIntLiteralValue(arg0);
            		Expr argDobule = new FPLiteralExpr(new natlab.FPNumericLiteralValue(""+value));
            		expr.setArg(argDobule, 0);            		
            	}
    			
            } else if(fname.equalsIgnoreCase("mod")) {
            	boolean isDouble = false;
            	// find the double type
            	for(Expr arg: expr.getArgs()) {
            		if(isFPLiteral(arg)) {
            			isDouble = true;
            			break;
            		} else if(isIntLiteral(arg)) {
            		} else {
            			Type argType = arg.collectType(stScope, varNode);
            			if(isDoubleType(argType)) {
                			isDouble = true;
                			break;
            			}
            		}            		
            	}
            	if(isDouble) {
        			varType = new PrimitiveType(TYPENAME_DOUBLE);
        			// convert int to double
        			int i=0;
                	for(Expr arg: expr.getArgs()) {
                    	if(isIntLiteral(arg)) {
                    		double value = (double)getIntLiteralValue(arg);
                    		Expr argDobule = new FPLiteralExpr(new natlab.FPNumericLiteralValue(""+value));
                    		expr.setArg(argDobule, i);            		
                    	}
                    	i++;
                	}
            	} else {
            		varType = new PrimitiveType(TYPENAME_DOUBLE);
            	}

            } else if(fname.equalsIgnoreCase("round") || fname.equalsIgnoreCase("floor")
            		|| fname.equalsIgnoreCase("ceil") || fname.equalsIgnoreCase("fix")) {
    			varType = new PrimitiveType(TYPENAME_INTEGER);
    			
            } else if(fname.equalsIgnoreCase("mean")) {
    			varType = new PrimitiveType(TYPENAME_DOUBLE);
            } else if(fname.equalsIgnoreCase("exp")) {
    			varType = new PrimitiveType(TYPENAME_DOUBLE);
            } else if(fname.equalsIgnoreCase("sum")) {
    			varType = new PrimitiveType(TYPENAME_DOUBLE);
    				
            } else if(fname.equalsIgnoreCase("numel")
            		|| (fname.equalsIgnoreCase("size"))) {
            	// Transform the function into corresponding Fortran functions 
            	// for first element is character(), then transform into LEN()
            	// Otherwise, numel(A) will be transformed into SIZE(a,1)*SIZE(a,2)...
            	String arg0 = ((NameExpr)expr.getArg(0)).getName().getID();
	    		SymbolTableEntry stEntry2 = stScope.getSymbolById(arg0);
	    		if(stEntry2!=null) {
		    		Type argType = ((VariableDecl) stEntry2.getDeclLocation()).getType();
		    		if(DEBUG) System.out.println("[inferType-X]"+fname+"()["+expr.getStructureString()
	            			+"]="+expr.getArg(0).getStructureString()
	            			+"="+argType+ " ["+(argType==null?"":argType.getName())+"]");
	            	if(isCharacterType(argType)) {
	            		transform2ParameterizedExpr(expr, new NameExpr(new Name("LEN")), expr.getArg(0));
	            	} else if(argType instanceof MatrixType) {
	            		// MATLAB size(Array,Dim) is same as Fortran
	            		// Convert NUMEL(Array) to SIZE(Array)...
	            		if(fname.equalsIgnoreCase("numel")) {
	            	    	java.util.List argDims = null;
	            	    	java.util.List<Integer> argIntDims = ((MatrixType) argType).getSize().getDims();
	            	    	java.util.List<String> argStrDims = ((MatrixType) argType).getSize().getDynamicDims();
	            	    	if(argIntDims==null) {
	            	    		argDims = argStrDims;
	            	    	}
        	            	NameExpr funcNameExpr = new NameExpr(new Name("SIZE"));
            	    		MTimesExpr mainExpr = null; 
    	        			ASTNode parent = expr.getParent();
        	    			int loc = parent.getIndexOfChild(expr);
            	    		for(int i=0; i<argDims.size(); ++i) {
            	            	natlab.ast.List<Expr> list = new natlab.ast.List<Expr>();
            	            	list.add(expr.getArg(0));
                				list.add(new natlab.ast.IntLiteralExpr(new natlab.DecIntNumericLiteralValue(""+(i+1))));                				
            	            	ParameterizedExpr exprSizeFunc = new ParameterizedExpr(funcNameExpr , list);
            	    			if(i==0) {
            	    				mainExpr = new MTimesExpr();
            	    				mainExpr.setChild(exprSizeFunc, i);
            	    			} else if(i==1) {
            	    				mainExpr.setChild(exprSizeFunc, i);
            	    			} else {
            	    				MTimesExpr extraExpr = new MTimesExpr();
            	    				extraExpr.setChild(mainExpr, 0);
            	    				extraExpr.setChild(exprSizeFunc, 1);
            	    				mainExpr = extraExpr;
            	    			}
                	    		if(argDims.size()==1) {
                	    			parent.setChild(exprSizeFunc, loc);
                	    		}
            	    		}
            	    		if(argDims.size()>1 && mainExpr!=null) {
            	    			parent.setChild(mainExpr, loc);
            	    		}
	            		}
	            	}
	    		}
            	varType =  new PrimitiveType(TYPENAME_INTEGER);
            } else if(fname.equalsIgnoreCase("bitand")) {
            	varType = new PrimitiveType(TYPENAME_INTEGER);

            } else {	
            	// TODO: Handle functions 
            	// 1. the built-in/intrinsic functions
            	// 2. User-defined functions
            	varType = createFunctionSignature(stScope, expr, varNode);
            	// OR return first argument's type, abs(x), ...
    			// varType = expr.getArg(0).collectType(stScope, varNode);
            }
        }
    	if(DEBUG) System.out.println("[inferType]3["+expr.getStructureString()+"]="
    			+varType+ " ["+(varType==null?"":varType.getName())+"]");
		return varType;
	}
	
	//-------------------------------------------------------------------------
	// Infer type for intrinsic functions in command form
	public static Type inferTypeIntrinsicFunction(NameExpr node) {
		Type varType = null;
		String varName = node.getName().getID();
		if(varName.equalsIgnoreCase("toc")) {
			cmdFormFuncList.add(node);
			varType = new PrimitiveType(TYPENAME_DOUBLE);
		} else if(varName.equalsIgnoreCase("clock")) {
			cmdFormFuncList.add(node);
			varType = createMatrixType(TYPENAME_DOUBLE, 1,6);
		}
		return varType;
	}
	//-------------------------------------------------------------------------
	
	public static boolean isValidType(Type varType)
	{
		return (varType!=null && !(varType instanceof annotations.ast.UnknownType));
	}
	public static boolean isIntegerType(Type varType) {
        if(varType!=null && varType instanceof PrimitiveType
        		&& varType.getName().equalsIgnoreCase(TYPENAME_INTEGER)) {
        	return true;
        } else {
        	return false;
        }
	}
	 
	public static String getLiteralString(LiteralExpr expr) {
		if(expr==null) {
			return null;
		} else if(isIntLiteral(expr)) {
			return ""+getIntLiteralValue(expr);
		} else if(isFPLiteral(expr)) {
			return ""+getFPLiteralValue(expr);
		} else {
			return ((StringLiteralExpr) expr).getValue();
		}
	}
	public static int getIntLiteralValue(Expr expr) {
		if(isIntLiteral(expr)) {
	        if(expr instanceof IntLiteralExpr) {
	        	return ((IntLiteralExpr) expr).getValue().getValue().intValue();
	        } else if (expr instanceof UMinusExpr) {
	        	return -((IntLiteralExpr) ((UMinusExpr) expr).getOperand()).getValue().getValue().intValue();
	        } else if (expr instanceof UPlusExpr) {
	        	return ((IntLiteralExpr) ((UPlusExpr) expr).getOperand()).getValue().getValue().intValue();
	        }
		} 
		return 0;
	}
	public static double getFPLiteralValue(Expr expr) {
		if(isFPLiteral(expr)) {
	        if(expr instanceof FPLiteralExpr) {
	        	return ((FPLiteralExpr) expr).getValue().getValue().doubleValue();
	        } else if (expr instanceof UMinusExpr) {
	        	return -((FPLiteralExpr) ((UMinusExpr) expr).getOperand()).getValue().getValue().doubleValue();
	        } else if (expr instanceof UPlusExpr) {
	        	return ((FPLiteralExpr) ((UPlusExpr) expr).getOperand()).getValue().getValue().doubleValue();
	        }
		} 
		return 0.0;
	}
	public static boolean isLiteral(Expr expr) {
        if((expr instanceof LiteralExpr) 
	         || ((expr instanceof UMinusExpr) && ((UMinusExpr) expr).getOperand() instanceof LiteralExpr ) 
	         || ((expr instanceof UPlusExpr) && ((UPlusExpr) expr).getOperand() instanceof LiteralExpr ) 
			){ 
           	return true;
        } else {
        	return false;
        }
	}
	public static boolean isIntLiteral(Expr expr) {
        if((expr instanceof IntLiteralExpr) 
	         || ((expr instanceof UMinusExpr) && ((UMinusExpr) expr).getOperand() instanceof IntLiteralExpr ) 
	         || ((expr instanceof UPlusExpr) && ((UPlusExpr) expr).getOperand() instanceof IntLiteralExpr ) 
			){ 
           	return true;
        } else {
        	return false;
        }
	}
	public static boolean isFPLiteral(Expr expr) {
        if((expr instanceof FPLiteralExpr) 
	         || ((expr instanceof UMinusExpr) && ((UMinusExpr) expr).getOperand() instanceof FPLiteralExpr ) 
	         || ((expr instanceof UPlusExpr) && ((UPlusExpr) expr).getOperand() instanceof FPLiteralExpr ) 
			){ 
           	return true;
        } else {
        	return false;
        }
	}
	public static boolean isStringLiteral(Expr expr) {
		return (expr instanceof StringLiteralExpr);
	}

	public static boolean isDoubleType(Type varType) {
        if(varType!=null && varType instanceof PrimitiveType
        		&& varType.getName().equalsIgnoreCase(TYPENAME_DOUBLE)) {
        	return true;
        } else {
        	return false;
        }
	}
	public static boolean isCharacterType(Type varType) {
        if(varType!=null && varType.getName().length()>=TYPENAME_CHARACTER.length()
				&& varType.getName().substring(0, TYPENAME_CHARACTER.length()).equalsIgnoreCase(TYPENAME_CHARACTER)) {
        	return true;
        } else {
        	return false;
        }
	}
	public static boolean isLogicalType(Type varType) {
		if(varType!=null 
				&& varType instanceof PrimitiveType 
				&& varType.getName().equalsIgnoreCase(TYPENAME_LOGICAL)) {
			return true;
		} else {
			return false;
		}
	}

	private static Integer maxInteger(Integer i1, Integer i2) {
		return (i1>=i2?i1:i2);
	}

	//----------------------------------------------------------------------------------
    // Check and decide the final matrix type for variable like: X(10) = ..., X(1,1)=..
	// varType: the inferred type of the expression, based on RHS
	// expr : the LHS expression, which may indicate a use of the variable, 
	// There are many cases:
	//	- varType = PrimaryType			expr = x(1,2)		: enlarge
	//	- Matrix(2,3)					expr = x(4,5)		: enlarge
	//  - Matrix(3)						expr = x(1,2)		: enlarge ->1*3
	//  - Matrix(1,3)					expr = x(2)			: change expr=x(1,2)
	// ... ...
	// ... ...	
	// Adjust the inferred type (varType) with the matrix-access expression (expr)
	// Get the correct data type that based on those two.
	// Because they may different, i.g. A(1,2)=0.0
	public static MatrixType adjustMatrixType(Type varType, ParameterizedExpr expr) {
		String typeName =  TYPENAME_DOUBLE;
    	if(varType!=null) {
    		typeName = varType.getName();
    	}
    	MatrixType lType = createMatrixType(typeName, (ParameterizedExpr) expr);
    	MatrixType mType = mergeMatrixType(varType, lType);
    	
    	return mType;
	}

	// The result type will be a matrix type with largest dimension of those two types
	// And the primary type will be the smallest compatible type of those two.
	public static MatrixType mergeMatrixType(Type varType, MatrixType lType) {
		String typeName = TYPENAME_DOUBLE;
    	if(varType==null) {
    		return lType;
    	} else {
    		// Get the compatible primitive data type
    		Type pType = mergePrimitiveType(varType, new PrimitiveType(lType.getName()));
    		typeName = pType.getName();
    	}
    	
    	java.util.List<Integer> vDims;  
		if(varType instanceof MatrixType) {
			vDims = ((MatrixType) varType).getSize().getDims();
		} else {
			// use the dimension info of matrix type, use above merged primitive type
			// form a empty matrix type, let next part code handle it
			vDims = null;	
			varType = new MatrixType(new PrimitiveType(typeName));
			Size strSize = new Size();
			strSize.setDynamicDims(new ArrayList<String>());
			((MatrixType)varType).setSize(strSize);
		}
    	MatrixType mType = new MatrixType(new PrimitiveType(typeName));
    	
    	java.util.List<Integer> lDims = lType.getSize().getDims();
    	java.util.List<Integer> mDims = new ArrayList<Integer>();
		if(lDims==null || vDims==null) {
			// Dynamic indexes
			// Convert static dimension to string, so that they can compare each other
			if(lDims!=null) {
    			java.util.List<String> strDims = new ArrayList<String>();
    			for(Integer dim : lDims) {
    				strDims.add(dim.toString());
    			}
    			lType.getSize().setDynamicDims(strDims);
			}
			if(vDims!=null) {
    			java.util.List<String> strDims = new ArrayList<String>();
    			for(Integer dim : vDims) {
    				strDims.add(dim.toString());
    			}
    			((MatrixType)varType).getSize().setDynamicDims(strDims);
			}
			
			java.util.List<String> vstrDims = ((MatrixType)varType).getSize().getDynamicDims();
			java.util.List<String> lstrDims = lType.getSize().getDynamicDims();
			java.util.List<String> mstrDims = new ArrayList<String>();
			if(vstrDims.size()==lstrDims.size()) {
				// compare each dimension's string
				for(int i=0;i<vstrDims.size();++i) {
					String str = vstrDims.get(i); 
					String strl = lstrDims.get(i); 
					
					// TODO: Assume the length decide which is bigger
					// how to compare (i+1,i), (i-1,i), (i+1-1)
					if(str==null && strl==null) {
						str = "1";
					} else if(str==null) {
						str = strl;
					} else if(strl==null) {
						// lType is a unsolved dimension variable,  which has null
						// i.g. D(1,null), then ignore this type, 
						return (MatrixType)varType;			
					} else if (!(str.equals(strl))) {
						if (str.length() == strl.length()) {
							if (str.compareTo(strl) < 0)
								str = strl;
						} else if (str.length() < strl.length()) {
							str = strl;
						}
					}
					mstrDims.add(str);
				}
				Size strSize = new Size();
				strSize.setDynamicDims(mstrDims);
				mType.setSize(strSize);
				return mType;
			} else if(vstrDims.size()>lstrDims.size()) {
				Size strSize = new Size();
				strSize.setDynamicDims(vstrDims);
				mType.setSize(strSize);
				return mType;
			} else { // if(vstrDims.size()<lstrDims.size()) 
				Size strSize = new Size();
				strSize.setDynamicDims(lstrDims);
				mType.setSize(strSize);
				return mType;
			}		        

		} else {
			int vsize = vDims.size();
			int lsize = lDims.size();
			if(DEBUG) System.out.println("[mergeMatrixType]- "+vsize+" - "+lsize);
	    	
	    	if(vDims.size()==lDims.size()) {
	    		for(int i=0; i<vDims.size(); ++i) {
	    			Integer max = maxInteger(vDims.get(i),lDims.get(i));
	    			mDims.add(max);
	    		}
	    		
    		// Otherwise Using the type with lager dimension 
	    	} else if(vDims.size()<lDims.size()) {
	    		mDims.addAll(lDims);
	    	} else {	// vDims.size()>lDims.size()
	    		mDims.addAll(vDims);
	    	}
    		// Need to update all the previous define-node and use-node, 
	    	
			mType.setSize(new Size(mDims));
	        return mType;
		}
	}

	// Get the smallest compatible Primitive-Type of them two
	public static Type mergePrimitiveType(Type varType, Type orgType) {
		if(varType==null) {
			return orgType;
		} else if(orgType==null) { 
			return varType;
		}
		// Now they are not null
	    PrimitiveType pType = null; 
	    if(varType.getName().equalsIgnoreCase(orgType.getName())) {
	    	pType = new PrimitiveType(varType.getName());
	    } else {
	    	if(orgType.getName().equalsIgnoreCase(TYPENAME_COMPLEX)
	    			|| varType.getName().equalsIgnoreCase(TYPENAME_COMPLEX)) {
	    		pType = new  PrimitiveType(TYPENAME_COMPLEX);
	    	} else if(orgType.getName().equalsIgnoreCase(TYPENAME_DOUBLE)
	    			|| varType.getName().equalsIgnoreCase(TYPENAME_DOUBLE)) {
	    		pType = new  PrimitiveType(TYPENAME_DOUBLE);
	    	} else if(varType.getName().equalsIgnoreCase(TYPENAME_INTEGER)
	    			|| orgType.getName().equalsIgnoreCase(TYPENAME_INTEGER)) {
	    		pType = new  PrimitiveType(TYPENAME_INTEGER);
	    	} else if(varType.getName().equalsIgnoreCase(TYPENAME_LOGICAL)
	    			|| orgType.getName().equalsIgnoreCase(TYPENAME_LOGICAL)) {
	    		pType = new  PrimitiveType(TYPENAME_LOGICAL);
	    	} else {
	            if( isCharacterType(varType) && isCharacterType(orgType)) {
					int loc1= varType.getName().indexOf("(");
					int loc2= varType.getName().indexOf(")");					
					int varLen = (new Integer(varType.getName().substring(loc1+1,loc2))).intValue();
					loc1= orgType.getName().indexOf("(");
					loc2= orgType.getName().indexOf(")");					
					int orgLen = (new Integer(orgType.getName().substring(loc1+1,loc2))).intValue();
					int maxLen = (varLen>=orgLen ? varLen:orgLen);
					pType = new annotations.ast.PrimitiveType("character("+(maxLen)+")");
	            	
	            } else {
		    		// TODO: special int8 ...
		    		// character type , TYPENAME_CHARACTER
		    		pType = new PrimitiveType("character(50)");
	            }
	    	}
	    }
	    return pType;
	}
	// Merge between two types
	public static Type mergeType(Type varType, Type orgType) {
		if(!isValidType(orgType)) {
			return varType;
		} else if(!isValidType(varType)) {
			return orgType;
		}
		
	    // Rule-1: all PrimitiveType, choose Complex--Double--Int...
	    if(varType instanceof PrimitiveType && (orgType instanceof PrimitiveType)) { 
	    	return mergePrimitiveType(varType, orgType);
	    } else {
			// Rule-2: if one of them is MatrixType, another is PrimitiveType,
			//		  then merge them .
	    	MatrixType mType;
		    if(varType instanceof MatrixType) {
		    	mType = mergeMatrixType(orgType, (MatrixType)varType);
		    } else if(orgType instanceof MatrixType) {
		    	mType = mergeMatrixType(varType, (MatrixType)orgType);
		    } else {
		    	// There maybe (varType instanceof ArgTupleType)
		    	if(DEBUG) System.err.println("[mergeType] failed "+varType+" -org="+orgType);
		    	return varType; 		    	
		    }
	    	return mType;
	    }
	}
	//public static PrimitiveType getCompatibleType(PrimitiveType t1, PrimitiveType t2) {	}
	
	// Create a matrix data type for an ParameterizedExpr 
	// Working for rand/rands/zeros/ones
	// Y = rand(n) returns an n-by-n matrix of values derived as described above.
	// Y = rand(m,n) or Y = rand([m n]) returns an m-by-n matrix of the same.
	public static MatrixType createMatrixType(String PrimitiveTypeName, Integer...dimlist) {
		MatrixType mType = new MatrixType(
				new PrimitiveType(PrimitiveTypeName));
		java.util.List<Integer> dim = new ArrayList<Integer>();
		for(Integer value:dimlist) {
			dim.add(value);
		}
		mType.setSize(new Size(dim));
		return mType;		
	}
		
	public static MatrixType createMatrixType(String PrimitiveTypeName, ParameterizedExpr expr) {
		return createMatrixType(PrimitiveTypeName, expr, false);
	}
	// mxm : flag for special case: randn(m) => m*m
	public static MatrixType createMatrixType(String PrimitiveTypeName, ParameterizedExpr expr, boolean mxm) {
        List<Expr> args = (List<Expr>) expr.getArgs();
		MatrixType mType = new MatrixType(
				new PrimitiveType(PrimitiveTypeName));
		java.util.List<Integer> dim = new ArrayList<Integer>();
		Size mSize = new Size();
		java.util.List<String> strDim = new ArrayList<String>();
		boolean bDynamic = false;
        for(Expr arg : args) {
        	// TODO: Assumption: all args are integer constant, IntLiteralExpr
        	// should support String in dynamic case
        	if(arg instanceof IntLiteralExpr) {
        		// if(((IntLiteralExpr) arg).getValue().getValue().intValue()==1) 
        			// column matrix/row matrix,
        			// currently treat as two dimension array
        			// i.g.: A=1:10, B=rand(1,10); C=rand(10,1)
        		// 
        		int extent =((natlab.IntNumericLiteralValue) 
	        			((IntLiteralExpr) arg).getValue()).getValue().intValue(); 
	        	dim.add(extent);
    			strDim.add(""+extent);

    			// Special case: randn(m) => m*m
        		if((args.getNumChild()==1) && mxm) {
	        		extent = ((natlab.IntNumericLiteralValue) 
		        			((IntLiteralExpr) arg).getValue()).getValue().intValue();
		        	dim.add(extent);
	    			strDim.add(""+extent);
        		}
        	} else {
	        	// Using variable in the extern of each dimension 
        		String strValue = getVariableValue(gstScope, arg);
        		if(strValue == null)
        			strValue = arg.getStructureString();
    			strDim.add(strValue);
        		bDynamic = true;
            	if(DEBUG) System.out.println("[createMatrixType]-dynamic ["+expr.getNodeID()+"]"+strValue);
        	}
        }
		if(bDynamic) {
			mSize.setDynamicDims(strDim);
		} else {
			mSize.setDims(dim);
		}
		mType.setSize(mSize);
		return mType;		
	}
	
	// When using linear indexing, which means index misses some dimension
	// i.g.  A=Matrix(1*5),   expr:  A(2)=0.0  => A(1,2) 
	// i.g.  B=Matrix(5*1),   expr:  B(3)=0.0  => B(3,1)
	// i.g.  C=Matrix(2*10),  expr:  C(4)=0.0  => C(1,4)	// row major ??
	// 						  expr:  C(15)=0.0 => C(2,5)	// row major
	// TODO: Currently assume all index are integer literal
	public static void adjustParameterizedExpr(MatrixType mType, ParameterizedExpr expr) {
		// LSH expression type 
    	MatrixType lType = createMatrixType(mType.getName(), (ParameterizedExpr) expr);

    	java.util.List<Integer> lDims = lType.getSize().getDims();
    	java.util.List<Integer> mDims = mType.getSize().getDims();

    	natlab.ast.List<Expr> list = new natlab.ast.List<Expr>();

    	// Partial solution 
    	// If both are dynamic, we assume one of them should be '1',
    	// and assume the difference is 1
    	// Linear indexing A(i, j), A(k)=A(k/j,k%j)
    	// 	dd=zeros(n,1); dd(k)=5,   ff=zeros(1,n); ff(k)=5
    	if(lDims==null && mDims==null) {
        	java.util.List<String> lstrDims = lType.getSize().getDynamicDims();
        	java.util.List<String> mstrDims = mType.getSize().getDynamicDims();
        	if(mstrDims.size()>lstrDims.size()) {
        		int index = -1;
        		for(int i=0; i<mstrDims.size(); i++) {
        			if(mstrDims.get(i).equals("1")) {
        				index=i; break;
        			}
        		}
        		if(index>=0) {
        			int i=0;
        			for(Expr arg: expr.getArgList()) {
	        			if(index == i) {
	        				list.add(new natlab.ast.IntLiteralExpr(new natlab.DecIntNumericLiteralValue("1")));
	        			}
        				list.add(arg);
        				// list.add(new NameExpr(new Name(lstrDims.get(i)))); // this is the value
        				++i;
        			}
        			if(index == i) {
        				list.add(new natlab.ast.IntLiteralExpr(new natlab.DecIntNumericLiteralValue("1")));
        			}
	    			// Change the LHS node
	    			ASTNode parent = expr.getParent();
	    			int loc = parent.getIndexOfChild(expr);
	    	    	ParameterizedExpr funcExpr = new ParameterizedExpr(expr.getTarget(), list);
	    	    	parent.setChild(funcExpr, loc);
        		}
        	}
    		return;
    		
    	// If it's dynamic dimensions, mType=Matrix(1,1), lType=(n), 
    	// Then change lType as (1), the adjust 
    	} else if((lDims==null)  && (mDims!=null)) {
        	java.util.List<String> lstrDims = lType.getSize().getDynamicDims();
        	lDims = new ArrayList<Integer>();
        	for(int i=0; i<lstrDims.size(); i++) {
        		lDims.add(new Integer(1));
        	}
    	} 
    	if((lDims==null)  || (mDims==null)) {
        	if(DEBUG) System.err.println("[inferType]-adjustParameterizedExpr-NULL["+expr.getNodeID()+"]"+lDims+":"+mDims);
    		return ;
    	}

    	if(DEBUG) System.out.println("[inferType]-adjustParameterizedExpr["+expr.getNodeID()+"]"+lDims.size()+":"+mDims.size());
    	if(lDims.size()<mDims.size()) {
    		// Major cases are 2-dimension
    		if(mDims.size()==2) { 	// implies lDims.size()==1
    			if(mDims.get(0)==1) {
    				list.add(new natlab.ast.IntLiteralExpr(new natlab.DecIntNumericLiteralValue("1")));
    				list.add(expr.getArg(0));
    			} else if(mDims.get(1)==1) {
    				list.add(expr.getArg(0));
    				list.add(new natlab.ast.IntLiteralExpr(new natlab.DecIntNumericLiteralValue("1")));
    			} else {
    				// TODO: C=Matrix(2*10),  expr:  C(4)=0.0  => C(1,4) ???
    				// 						  expr:  C(15)=0.0 => C(2,5)	// row major
    				int row = lDims.get(0)/mDims.get(1);
    				int col = lDims.get(0)%mDims.get(1);
    				list.add(new natlab.ast.IntLiteralExpr(new natlab.DecIntNumericLiteralValue(""+row)));
    				list.add(new natlab.ast.IntLiteralExpr(new natlab.DecIntNumericLiteralValue(""+col)));
    			}
    			// Change the LHS node
    			ASTNode parent = expr.getParent();
    			int loc = parent.getIndexOfChild(expr);
    	    	ParameterizedExpr funcExpr = new ParameterizedExpr(expr.getTarget(), list);
    	    	parent.setChild(funcExpr, loc);
    		}
    	} else if(lDims.size()>mDims.size()) {
    		// According the logic before calling adjustParameterizedExpr(), this should not happen!
    		System.err.println("[Error]adjustParameterizedExpr: lDims.size()>mDims.size()");
    	}
    	// Ignor the equal case
	}
	
	// TODO: Adjust LHS, 
	// When LHS's dimension doesn't match the RHS's, needs adjust the LHS's indexing 
	// It's usually the row/column vector case. 
	// i.g. A=Matrix(1*5), then A=1:5  should be A(1,:)=1:5
	// Currently only handle the row/column vector case. 
	// rType: inferred type for RHS 
	// lType: LHS variable type, same as it in symbol table
	// expr: it's a NameExpr, NOT a ParameterizedExpr!
	public static void adjustArrayIndex(MatrixType rType, MatrixType lType, Expr expr) {
		// LSH expression type 
    	java.util.List<Integer> lDims = lType.getSize().getDims();
    	java.util.List<Integer> rDims = rType.getSize().getDims();	

    	// If it's dynamic dimensions,stop 
    	if(lDims==null || rDims==null)
    		return;
    	
    	if(DEBUG) System.out.println("[inferType]-adjustArrayIndex["+expr.getNodeID()+"]"+lDims.size()+":"+rDims.size());
    	// No need to handle expression A=1, where assigning 1 to whole array A; 
    	if(rDims==null || rDims.size()==0)	
    		return;
    	
    	natlab.ast.List<Expr> list = new natlab.ast.List<Expr>();

    	if(lDims.size()>rDims.size()) {
    		// Major cases are 2-dimension, i.g. A=1:5; A=[1,2,3,4,5]
    		// Since LHS is just a NameExpr, not a parameterized-expression, 
    		// If LHS/RHS dimension difference is 1, then add ':' colon-expression to LHS
    		if(lDims.size()==2 && rDims.size()==1) { 
    			if(lDims.get(0)==1) {
    				list.add(new natlab.ast.IntLiteralExpr(new natlab.DecIntNumericLiteralValue("1")));
    				list.add(new ColonExpr());
    			} else if(lDims.get(1)==1) {
    				list.add(new ColonExpr());
    				list.add(new natlab.ast.IntLiteralExpr(new natlab.DecIntNumericLiteralValue("1")));
    			} else {
    				// In case: C=Matrix(2*10),  expr:  C=[1,2] ??? Grammar Error
    	    		System.err.println("[Error]adjustArrayIndex: lhs=["+lDims.get(0)+","+lDims.get(1)+"]");
    			}
    			// Change the LHS node
    			ASTNode parent = expr.getParent();
    			int loc = parent.getIndexOfChild(expr);
    	    	ParameterizedExpr funcExpr = new ParameterizedExpr(expr, list);
    	    	parent.setChild(funcExpr, loc);
    		} else {
        		System.err.println("[Other Cases]adjustArrayIndex: lhs=["+lDims.get(0)+","+lDims.get(1)+"]"
        				+ " rhs=["+rDims.get(0)+","+rDims.get(1)+"]"); 
    		}
    	} else if(lDims.size()<rDims.size()) {
    		// According the logic before calling adjustParameterizedExpr(), this should not happen!
    		System.err.println("[Error]adjustArrayIndex: lDims.size()<rDims.size()");
    	} 
    	// Ignor the equal case     	
	}
	// Create a function signature from a function call
	// Assumption: all parameters are either variables (have been added into symbol table)
	//				or constants
	// TODO: Testing createFunctionSignature()
	public static Type createFunctionSignature(SymbolTableScope stScope, 
			ParameterizedExpr expr, ASTNode varNode)
	{	
		ArgTupleType funcSignature = new ArgTupleType(); 
		funcSignature.setName(expr.getVarName());
        List<Expr> args = expr.getArgs();
        for(Expr arg : args) {
        	Type argType; String argName;
        	if(arg instanceof LiteralExpr) {
	        	if(arg instanceof IntLiteralExpr) {
	        		argType = new PrimitiveType(TYPENAME_INTEGER);
	        		argName = ((IntLiteralExpr) arg).getValue().getText();
	        	} else if(arg instanceof FPLiteralExpr) {
	        		argType = new PrimitiveType(TYPENAME_DOUBLE);
	        		argName = ((FPLiteralExpr) arg).getValue().getText();
	        	} else { // StringLiteralExpr
	        		argType = new PrimitiveType("character(50)");
	        		argName = ((StringLiteralExpr) arg).getValue();
	        	}
        	} else {
            	argType = arg.collectType(stScope, arg);
            	argName = arg.getVarName();
        	}
        	funcSignature.addStaticArgType(argType, argName);
        }
		return funcSignature;
	}

	public static String getVariableValue(SymbolTableScope stScope, Expr expr) {
		String strValue=null;
		String varName = expr.getVarName();
		// If it an variable  
		if((expr instanceof LiteralExpr)) {
			strValue = TypeInferenceEngine.getLiteralString((LiteralExpr)expr);
		} else if((expr instanceof NameExpr)) {
			SymbolTableEntry rhsEntry = stScope.getSymbolById(varName);
			if(rhsEntry!=null) {
				strValue = TypeInferenceEngine.getLiteralString(rhsEntry.getValue());
			} else { 
    			// this should not happen
				System.err.println("[getVarValue] "+varName+ " doesn't have entry");
				strValue = ((NameExpr) expr).getName().getID();
			}
		} else if ((expr instanceof ParameterizedExpr)) {
			// Array variable access can be checked by 			        			
			// 		SymbolTableEntry rhsEntry = stScope.getSymbolById(rhsName);
			// Otherwise it's a function call
			// But since here don't support array variable, so treat them same
			// strValue = expr.getFortran();
			strValue = null;
			
		} else if (expr instanceof BinaryExpr){
			String opStr = null;
			if(expr instanceof PlusExpr) {
				opStr = "+";
			} else if(expr instanceof MinusExpr) {
				opStr = "-";
			} else if(expr instanceof MTimesExpr) {
				opStr = "*";
			} else if(expr instanceof MDivExpr) {
				opStr = "/";
			} else {
				
			}
			String lhsStr = getVariableValue(stScope, ((BinaryExpr) expr).getLHS());
			String rhsStr = getVariableValue(stScope, ((BinaryExpr) expr).getRHS());
			if(opStr!=null && lhsStr!=null && rhsStr!=null) {
				strValue ="("+lhsStr+opStr+rhsStr+")";
			} else {
				strValue = null;
			}

		} else if (expr instanceof UnaryExpr){
			String opStr = "+";
			if(expr instanceof UPlusExpr) {
				opStr = "+";
			} else if(expr instanceof UMinusExpr) {
				opStr = "-";
			}
			strValue = "("+opStr+getVariableValue(stScope, ((UnaryExpr) expr).getOperand())+")";
			
// TODO
		} else if ((expr instanceof RangeExpr)) {
			// This is handled by inferSymbolEntry(), because it needs 2 values
			// following is dummy code
			strValue = getVariableValue(stScope, ((RangeExpr)expr).getUpper());
		} else {	
			// unsure situation
			strValue = null;	// expr.getFortran();
		}
		return strValue;
	}
}
