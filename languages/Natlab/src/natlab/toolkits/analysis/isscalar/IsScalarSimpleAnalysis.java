package natlab.toolkits.analysis.isscalar;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import ast.ASTNode;
import ast.AssignStmt;
import ast.BinaryExpr;
import ast.ColonExpr;
import ast.Expr;
import ast.ForStmt;
import ast.Function;
import ast.IntLiteralExpr;
import ast.LiteralExpr;
import ast.MatrixExpr;
import ast.Name;
import ast.NameExpr;
import ast.ParameterizedExpr;
import ast.RangeExpr;
import ast.Row;
import ast.Stmt;
import ast.StringLiteralExpr;
import ast.UnaryExpr;
import natlab.toolkits.analysis.AbstractSimpleStructuralForwardAnalysis;
import natlab.toolkits.analysis.varorfun.DataCollectFlowSet;
import natlab.toolkits.analysis.varorfun.DataPair;

public class IsScalarSimpleAnalysis extends AbstractSimpleStructuralForwardAnalysis<DataCollectFlowSet<String, IsScalarType>> {
	private Map<String, List<String>> globalInParamLists;
	private Map<String, List<String>> globalOutParamLists;
	private Map<String, Function> functionMap;
	private Map<String, List<DataCollectFlowSet<String, IsScalarType>>> functionCallsInSetsMap;
	private Map<DataCollectFlowSet<String, IsScalarType>, DataCollectFlowSet<String, IsScalarType>> functionCallsReturnedSetMap;
	private Map<String, NonScalar> nonScalarMap;
	
	public IsScalarSimpleAnalysis(ASTNode tree) {
		super(tree);
		DEBUG = false;
        currentOutSet = newInitialFlow();
		globalInParamLists = new Hashtable<String, List<String>>();
		globalOutParamLists = new Hashtable<String, List<String>>();
		functionMap = new Hashtable<String, Function>();
    	functionCallsInSetsMap = new Hashtable<String, List<DataCollectFlowSet<String, IsScalarType>>>();
    	functionCallsReturnedSetMap = new Hashtable<DataCollectFlowSet<String, IsScalarType>, DataCollectFlowSet<String, IsScalarType>>();
    	nonScalarMap = new Hashtable<String, NonScalar>();
	}
	
	@Override
	public void analyze() {
		buildParamListsAndFunctionMapForNode(tree);
		super.analyze();
	}

	@Override
	public void copy(
			DataCollectFlowSet<String, IsScalarType> source,
			DataCollectFlowSet<String, IsScalarType> dest) {
		source.copy(dest);
	}

	@Override
	public void merge(
			DataCollectFlowSet<String, IsScalarType> in1,
			DataCollectFlowSet<String, IsScalarType> in2,
			DataCollectFlowSet<String, IsScalarType> out) {
		out.emptySet();
		Collection<String> remainingKeysOfIn2 = IsScalarHelper.getKeysForFlowSet(in2);
		
		// merge elements of in1 with corresponding elements of in2 and remove them from the remaining keys of in2 list
		for (DataPair<String, IsScalarType> firstPair : in1.toList()) {
                    String id = firstPair.getKey();
                    IsScalarType secondType = in2.containsKey(id);
                    IsScalarType resultType = IsScalarHelper.butterfly(firstPair.getValue(), secondType);    	
                    out.add(new DataPair<String, IsScalarType>(id, resultType));
                    remainingKeysOfIn2.remove(id);
		}
		
		// add the IsScalarTypes for the remaining variables in in2
		for (String key : remainingKeysOfIn2) {
			IsScalarType type = in2.containsKey(key);
			out.add(new DataPair<String, IsScalarType>(key, type));
		}
	}

	@Override
	public DataCollectFlowSet<String, IsScalarType> newInitialFlow() {
		return new DataCollectFlowSet<String, IsScalarType>();
	}
	
	@Override
	public void caseAssignStmt(AssignStmt node) {	
		System.out.println("caseAssignStmt" + node.getPrettyPrinted());
        Expr lhs = node.getLHS();
        Expr rhs = node.getRHS();
        addScalarTypeForLeftAndRightExpr(lhs, rhs);
    	System.out.println("outset after " + node.getPrettyPrinted() + " " + currentOutSet);
	}
	
	@Override
	public void caseForStmt(ForStmt node) {
		if (node.getAssignStmt().getRHS() instanceof RangeExpr) {
			String id = IsScalarHelper.getIdForExpr(node.getAssignStmt().getLHS());
	    	currentOutSet.add(new DataPair<String, IsScalarType>(id, IsScalarTypePool.scalar()));
		}
		// TODO support all kinds of for loops
		else {
    		System.out.println("unhandled expr case " + node.getAssignStmt().getRHS().getClass().getSimpleName());
		}
		for (Stmt stmt : node.getStmtList()) {
			stmt.analyze(this);
		}
	}
	
	/** 
	 * visits every node and adds <br>
	 *  - the mapping from the function name to the function node <br>
	 *  - the list of input parameters and <br>
	 *  - the list of output parameters <br>
	 *  to the corresponding global maps if the nodes is a function declaration
	 */
	private void buildParamListsAndFunctionMapForNode(ASTNode node) {
		if (node instanceof Function) {
			Function function = (Function) node;
			String functionName = function.getName();
			getAndAddParameterListToGlobalMap(function.getInputParams(), function.getNumInputParam(), functionName, globalInParamLists);
			getAndAddParameterListToGlobalMap(function.getOutputParams(), function.getNumOutputParam(), functionName, globalOutParamLists);
			functionMap.put(functionName, function);
		}
		for (int i = 0; i < node.getNumChild(); i++) {
			ASTNode child = node.getChild(i);
			buildParamListsAndFunctionMapForNode(child);
		}
	}
	
	/**
	 * computes a list of parameters and adds it to the given mapping
	 */
	private void getAndAddParameterListToGlobalMap(ast.List<Name> parameters, int numParam,
			String functionName, Map<String, List<String>> paramLists) {
		List<String> localParamList = new ArrayList<String>(numParam);
		for (Name n : parameters) {
			localParamList.add(n.getID());
		}
		paramLists.put(functionName, localParamList);
	}

	/**
	 * adds the IsScalarTypes for the variables used in the given left and right expression to the current outset
	 */
	private void addScalarTypeForLeftAndRightExpr(Expr left, Expr right) {
        if (left instanceof NameExpr) {
        	String id = IsScalarHelper.getIdForExpr(left);
            addScalarTypeForIdAndExpr(id, right);
        }
        else if (left instanceof MatrixExpr) {
        	if (right instanceof MatrixExpr) {
        		addScalarTypeForMatrixExpressions((MatrixExpr) left, (MatrixExpr) right);
        	}
        	else if (right instanceof ParameterizedExpr) {
        		addScalarTypeForMatrixAndParamExpr((MatrixExpr) left, (ParameterizedExpr) right);
        	}
        }
        else {
    		System.out.println("unhandled case " + left.getClass().getSimpleName());
        }
	}

	/**
	 * adds the IsScalarTypes for the variables used in the given left and right MatrixExpr to the current outset
	 */
	private void addScalarTypeForMatrixExpressions(MatrixExpr left, MatrixExpr right) {
		int rightRowIndex = 0;
    	for (Row leftRow : left.getRows()) {
    		Row rightRow = right.getRow(rightRowIndex);
    		int rightElementIndex = 0;
    		for (Expr leftElement : leftRow.getElements()) {
    			Expr rightElement = rightRow.getElement(rightElementIndex);
    			addScalarTypeForLeftAndRightExpr(leftElement, rightElement);
    			rightElementIndex++;
    		}
    		rightRowIndex++;
    	}
	}
	
	/**
	 * adds the IsScalarTypes for the variables used in the given left MatrixExpr and right ParameterizedExpr to the current outset
	 */
	private void addScalarTypeForMatrixAndParamExpr(MatrixExpr left,
			ParameterizedExpr right) {
    	Row leftRow = left.getRow(0);
    	int numOfArgs = leftRow.getNumElement();
    	List<String> leftIds = new ArrayList<String>(numOfArgs);
    	for (Expr leftElement : leftRow.getElements()) {
			leftIds.add(IsScalarHelper.getIdForExpr(leftElement));
		}
    	
		DataCollectFlowSet<String, IsScalarType> returnedTypesWithWrongNames = getReturnedScalarTypesWithWrongNamesForParamExpr(right);
		
		DataCollectFlowSet<String, IsScalarType> returnedTypes = new DataCollectFlowSet<String, IsScalarType>();

		Expr targetExpr = right.getTarget();
		if (targetExpr instanceof NameExpr) {
			String calledName = ((NameExpr) targetExpr).getName().getID();
			// check if it's a call to an external function
			if (functionMap.get(calledName) == null) {
				int i = 0;
				for (String leftId : leftIds) {
					returnedTypes.add(new DataPair<String, IsScalarType>(leftId, IsScalarTypePool.bottom()));
					i++;
	        	}
			}
			// call to internally defined function
			else {
				List<String> params = globalOutParamLists.get(calledName);
				int i = 0;
				for (String leftId : leftIds) {
					String paramId = params.get(i);
					IsScalarType returnType = returnedTypesWithWrongNames.containsKey(paramId);
					returnedTypes.add(new DataPair<String, IsScalarType>(leftId, returnType));
					i++;
	        	}
			}
			// System.out.println("call returned: " + returnedTypes + "\n");
			currentOutSet = IsScalarHelper.getSumOfTypes(currentInSet, returnedTypes);
		}
		else {
    		System.out.println("unhandled expr case " + targetExpr.getClass().getSimpleName());
		}
	}

	/**
	 * returns a flowset containing the IsScalarTypes (mapped from wrong (internal) names) that are returned by a function call represented by the given parameterized expression 
	*/
	private DataCollectFlowSet<String, IsScalarType> getReturnedScalarTypesWithWrongNamesForParamExpr(ParameterizedExpr paramExpr) {
		DataCollectFlowSet<String, IsScalarType> returnedTypes = new DataCollectFlowSet<String, IsScalarType>();
		
		Expr targetExpr = paramExpr.getTarget();
		if (targetExpr instanceof NameExpr) {
			String calledName = IsScalarHelper.getIdForExpr(targetExpr);
			// TODO fix mapping for multiple functions with same name but different signature
			Function function = functionMap.get(calledName);
			DataCollectFlowSet<String, IsScalarType> functionCallInSet = getFunctionCallInSetFromParamExprAndCalledName(paramExpr, calledName);
			System.out.println("functionCallInSet " + functionCallInSet);	
			
			// check whether the same function has already been called with the same inset
			List<DataCollectFlowSet<String, IsScalarType>> functionCallInSets = functionCallsInSetsMap.get(calledName);
			if (functionCallInSets != null) {
				for (DataCollectFlowSet<String, IsScalarType> oldFunctionCallInSet : functionCallInSets) {
					if (IsScalarHelper.equals(functionCallInSet, oldFunctionCallInSet)) {
						DataCollectFlowSet<String, IsScalarType> oldReturnedSet = functionCallsReturnedSetMap.get(oldFunctionCallInSet);
						// check if the function already returned and thus is mapped to an outset
						if (oldReturnedSet != null) {
							// easy case: leap over function call
							return oldReturnedSet;
						}
						else {
							// function call with same inset did not return yet
							// safe and conservative assumption: TOP
							for (String id : globalOutParamLists.get(calledName)) {
								// use the wrong name in order to fulfil DataPair contract
								returnedTypes.add(new DataPair<String, IsScalarType>(id, IsScalarTypePool.top()));
							}
							// TODO to put or not to put? functionCallsReturnedSetMap.put(functionCallInSet, returnedTypes);
							return returnedTypes;
						}
					}
				}
			}
			
			// function has never been called with the same inset
			if (functionCallInSets == null) {
				functionCallInSets = new LinkedList<DataCollectFlowSet<String, IsScalarType>>();
				functionCallsInSetsMap.put(calledName, functionCallInSets);
			}
			functionCallInSets.add(functionCallInSet);
			
			DataCollectFlowSet<String, IsScalarType> callerInSet = new DataCollectFlowSet<String, IsScalarType>();
			copy(currentInSet, callerInSet);
			DataCollectFlowSet<String, IsScalarType> callerOutSet = new DataCollectFlowSet<String, IsScalarType>();
			copy(currentOutSet, callerOutSet);
			
	        copy(functionCallInSet, currentInSet);
	        inFlowSets.put(function, functionCallInSet);
	        copy(currentInSet, currentOutSet);
	        function.analyze(this);
			
			for (String id : globalOutParamLists.get(calledName)) {
                            IsScalarType returnType = currentOutSet.containsKey(id);
                            // use the wrong name in order to fulfil DataPair contract
                            returnedTypes.add(new DataPair<String, IsScalarType>(id, returnType));
			}
			
			copy(callerInSet, currentInSet);
			copy(callerOutSet, currentOutSet);

			functionCallsReturnedSetMap.put(functionCallInSet, returnedTypes);
		}
		
		return returnedTypes;
	}

	/** 
	 * computes and adds the IsScalarType for the given id and expression to the current outset
	 */
	private void addScalarTypeForIdAndExpr(String id, Expr expr) {
            IsScalarType oldType = currentOutSet.containsKey(id);
            IsScalarType newType = getScalarTypeForExpr(expr);
            IsScalarType resultType = IsScalarHelper.butterfly(oldType, newType);
            currentOutSet.add(new DataPair<String, IsScalarType>(id, resultType));
            if (resultType.isNonScalar()) {
    		nonScalarMap.put(id, (NonScalar) resultType);
            }
	}
	
	/** 
	 * returns the IsScalarType for the given expression
	 */
	private IsScalarType getScalarTypeForExpr(Expr expr) {
		if (expr instanceof BinaryExpr) {
			IsScalarType leftType = getScalarTypeForExpr(((BinaryExpr) expr).getLHS());
			IsScalarType rightType = getScalarTypeForExpr(((BinaryExpr) expr).getRHS());
			if (leftType.isScalar() && rightType.isScalar()) {
				return IsScalarTypePool.scalar();
			}
			else {
				return IsScalarTypePool.top();
			}
		}
		if (expr instanceof UnaryExpr) {
			return getScalarTypeForExpr(((UnaryExpr) expr).getOperand());
		}
		if (expr instanceof IntLiteralExpr) {
			return IsScalarTypePool.scalar();
		}
		if (expr instanceof RangeExpr) {
			RangeExpr rangeExpr = ((RangeExpr) expr);
			Expr lowerExpr = rangeExpr.getLower();
			Expr upperExpr = rangeExpr.getUpper();
			if (lowerExpr instanceof IntLiteralExpr && upperExpr instanceof IntLiteralExpr) {
				BigInteger lowerBigInt = ((IntLiteralExpr) lowerExpr).getValue().getValue();
				BigInteger upperBigInt = ((IntLiteralExpr) upperExpr).getValue().getValue();
				if (lowerBigInt.equals(upperBigInt)) {
					return IsScalarTypePool.scalar();
				}
				else {
					int length = upperBigInt.subtract(lowerBigInt).intValue() + 1;
					return new NonScalar(1,length);
				}
			}
			else {
	    		System.out.println("unhandled expr case " + lowerExpr.getClass().getSimpleName());
				return new NonScalar(0,0);
			}
		}
		if (expr instanceof MatrixExpr) {
			MatrixExpr matrixExpr = (MatrixExpr) expr;
			if (matrixExpr.getNumRow() == 1  && matrixExpr.getRow(0).getNumElement() == 1) {
				return IsScalarTypePool.scalar();
			}
			else {
				int noOfRows = matrixExpr.getNumRow();
				int noOfCols = 0;
				for (int i = 0; i < noOfRows; i++) {
					int actualNoOfCols = matrixExpr.getRow(i).getNumElement();
					if (actualNoOfCols > noOfCols) {
						noOfCols = actualNoOfCols;
					}
				}
				NonScalar nonScalar = new NonScalar(noOfRows, noOfCols);
				for (int i = 0; i < noOfRows; i++) {
					Row row = matrixExpr.getRow(i);
					for (int j = 0; j < row.getNumElement(); j++) {
						IsScalarType t = getScalarTypeForExpr(row.getElement(j));
						nonScalar.array[i][j] = t;
					}
				}
				nonScalar.flatten();
				return nonScalar;
			}
		}
		if (expr instanceof NameExpr) {
                    IsScalarType type = currentInSet.containsKey(IsScalarHelper.getIdForExpr(expr));
                    if (type == null) {
                        type = IsScalarTypePool.bottom();
                    }
                    return type;
		}
		if (expr instanceof ParameterizedExpr) {
			ParameterizedExpr paramExpr = ((ParameterizedExpr) expr);
			String calledName = IsScalarHelper.getIdForExpr(paramExpr.getTarget());
			IsScalarType returnType = null;
			// call to an external function?
			if (functionMap.get(calledName) == null) {
				// call to a nonScalar?
				NonScalar calledNonScalar = nonScalarMap.get(calledName);
				if (calledNonScalar == null) {
					// call to external function!
					returnType = IsScalarTypePool.top();
				}
				else {
					returnType = getIsScalarTypeForCalledNonScalarAndParamExpr(calledNonScalar, paramExpr);
				}
			}
			// call to internal function!
			else {
				DataCollectFlowSet<String, IsScalarType> returnedTypes = getReturnedScalarTypesWithWrongNamesForParamExpr(paramExpr);
				returnType = returnedTypes.toList().get(0).getValue();
			}
			//System.out.println("call returned " + returnType + "\n");
			return returnType;
		}
		else {
    		System.out.println("unhandled expr case " + expr.getClass().getSimpleName());
		}
		return IsScalarTypePool.top();
	}

	private IsScalarType getIsScalarTypeForCalledNonScalarAndParamExpr(
			NonScalar calledNonScalar, ParameterizedExpr paramExpr) {
		if (paramExpr.getNumArg() == 1) {
			Expr arg = paramExpr.getArg(0);
			if (arg instanceof IntLiteralExpr) {
				int rowPlusOne = ((IntLiteralExpr) arg).getValue().getValue().intValue();
				return calledNonScalar.array[0][rowPlusOne - 1];
			}
			if (arg instanceof StringLiteralExpr) {
				return IsScalarTypePool.top();
			}
			else {
	    		System.out.println("unhandled expr case " + arg.getClass().getSimpleName());
				return IsScalarTypePool.top();
			}
		}
		else if (paramExpr.getNumArg() == 2) {
			Expr arg0 = paramExpr.getArg(0);
			Expr arg1 = paramExpr.getArg(1);
			//System.out.println("arg0 " + arg0 + "arg1 " + arg1);
			if (arg1 instanceof StringLiteralExpr) {
				return IsScalarTypePool.top();
			}
			else if (arg0 instanceof IntLiteralExpr) {
				if (arg1 instanceof IntLiteralExpr) {
					int row = ((IntLiteralExpr) arg0).getValue().getValue().intValue();
					int column = ((IntLiteralExpr) arg1).getValue().getValue().intValue();
					return calledNonScalar.array[row][column];
				}
				else if (arg1 instanceof ColonExpr) {
					Expr rvalue = ((ColonExpr) arg1).getRValue();
					//System.out.println("rvalue " + rvalue);
					if (rvalue instanceof StringLiteralExpr) {
						return IsScalarTypePool.top();
					}
					else if (rvalue instanceof IntLiteralExpr){
						((IntLiteralExpr) arg0).getValue().getValue().intValue();
						//System.out.println("rvalue " + rvalue);
					}
				}
			}
		}
		return IsScalarTypePool.top();
		
/*		if (expr instanceof LiteralExpr) {
			return IsScalarTypePool.scalar();
		}
		if (expr instanceof RangeExpr) {
			RangeExpr rangeExpr = ((RangeExpr) expr);
			Expr lowerExpr = rangeExpr.getLower();
			Expr upperExpr = rangeExpr.getUpper();
			if (lowerExpr instanceof IntLiteralExpr && upperExpr instanceof IntLiteralExpr) {
				BigInteger lowerBigInt = ((IntLiteralExpr) lowerExpr).getValue().getValue();
				BigInteger upperBigInt = ((IntLiteralExpr) upperExpr).getValue().getValue();
				if (lowerBigInt.equals(upperBigInt)) {
					return IsScalarTypePool.scalar();
				}
				else {
					int length = upperBigInt.subtract(lowerBigInt).intValue() + 1;
					return new NonScalar(1,length);
				}
			}
			else {
	    		System.out.println("unhandled expr case " + lowerExpr.getClass().getSimpleName());
				return new NonScalar(0,0);
			}
		}*/
	}

	/**
	 * returns a flow set that contains the IsScalarTypes induced by the passed ParameterizedExpr (a call to a function with name calledName)
	 */
	private DataCollectFlowSet<String, IsScalarType> getFunctionCallInSetFromParamExprAndCalledName(
			ParameterizedExpr paramExpr, String calledName) {
		DataCollectFlowSet<String, IsScalarType> functionCallInSet = newInitialFlow();
		List<String> paramList = globalInParamLists.get(calledName);
		if (paramList != null) {
			for (int i = 0; i < paramExpr.getNumArg(); i++) {
				IsScalarType paramType = getScalarTypeForExpr(paramExpr.getArg(i));
				String paramName = paramList.get(i);
				functionCallInSet.add(new DataPair<String, IsScalarType>(paramName, paramType));
			}
		}
		else {
			for (int i = 0; i < paramExpr.getNumArg(); i++) {
				IsScalarType paramType = getScalarTypeForExpr(paramExpr.getArg(i));
				String paramName = paramList.get(i);
				functionCallInSet.add(new DataPair<String, IsScalarType>(paramName, paramType));
			}
		}

		return functionCallInSet;
	}
}
