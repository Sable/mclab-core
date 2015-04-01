// =========================================================================== //
//                                                                             //
// Copyright 2011 Anton Dubrau and McGill University.                          //
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
//  limitations under the License.                                             //
//                                                                             //
// =========================================================================== //

package natlab.toolkits.rewrite.inline;
import java.util.Map;

import natlab.toolkits.rewrite.AbstractLocalRewrite;
import natlab.toolkits.rewrite.TransformedNode;
import ast.ASTNode;
import ast.AssignStmt;
import ast.Expr;
import ast.ExprStmt;
import ast.Function;
import ast.LValueExpr;
import ast.List;
import ast.MatrixExpr;
import ast.NameExpr;
import ast.ParameterizedExpr;
import ast.Row;
import ast.Script;
import ast.Stmt;

/**
 * Inlines every call to functions or scripts of given names into a function.
 * Every call to one of the given names is replaced with a copy of the corresponding 
 * function or script.
 * 
 * Note:
 * - The function call has to be the rhs of an assignmnet, or a 
 *   expression statement which is a paremetrized or name expression, i.e.
 *   it has to be of one of the following forms
 *     - [b1,b2...] = f(a1,a2,...)
 *     - b = f
 *     - f(a1,a2...)
 *     - f
 * - the first example gets transformed into
 *       x1 = a1
 *       x2 = a2
 *       ...
 *       body of the function
 *       b1 = y1
 *       b2 = y2
 *       ...
 *   
 * - the name of the function and the call do not have to be the same
 * - a call to a script should have no input and no output
 * - note that no name expressions will be renamed. This merely provides the copying of code,
 *   conflicts should be resolved by other transformations.
 * 
 * @author ant6n
 *
 */

public class Inliner<ScriptOrFunction extends ASTNode,TargetScriptOrFunction extends ASTNode> extends AbstractLocalRewrite {
    public boolean DEBUG = false;
	private Map<String, ScriptOrFunction> map;
	InlineQuery<ScriptOrFunction,TargetScriptOrFunction> query;
	TargetScriptOrFunction targetTree; //the program where functions/scripts are being inlined

	/**
	 * Constructs inliner from given Program (Script or Function) and Map from String. The
	 * Inline action is always performed. The map does not get modified.
	 */
	public Inliner(TargetScriptOrFunction tree,Map<String, ScriptOrFunction> map) {
		this(tree,map,new InlineQuery<ScriptOrFunction,TargetScriptOrFunction>(){
		    public boolean doInline(InlineInfo<ScriptOrFunction,TargetScriptOrFunction> inlineInfo) { 
		        return true; 
		    }
		});
	}
    /**
     * Constructs inliner from given Program (Script or Function) and Map from String. A
     * should also has to be supplied, allowing control of inlining actions.
     * @param
     * @param
     */
	public Inliner(TargetScriptOrFunction tree,Map<String, ScriptOrFunction> map,InlineQuery<ScriptOrFunction,TargetScriptOrFunction> query) {
		super(tree);
		this.map = map;
		this.query = query;
		this.targetTree = tree;
	}
	
	
	@Override
	public void caseAssignStmt(AssignStmt node) {
		//the info object used to query whether to inline, and as argument to acutally inline
		InlineInfo<ScriptOrFunction,TargetScriptOrFunction> info; 
		//node is of the case [x,..] = f
		if (node.getRHS() instanceof NameExpr){
			String name = ((NameExpr)node.getRHS()).getName().getID();
			if (map.containsKey(name)){
				if (DEBUG) System.out.println("inlining found unparametric/assign call to "+name);
				//build info object
				Script s;
				info = new InlineInfo<ScriptOrFunction,TargetScriptOrFunction>(copy(map.get(name)), 
				        targetTree, node,new List<Expr>(), getLHSList(node.getLHS()), false);
				if (query.doInline(info)) newNode = new TransformedNode(inline(info));
			}
		}
		//node is of the case [x,..] = f(..)
		if (node.getRHS() instanceof ParameterizedExpr){ //RHS is parametrized expr..
			ParameterizedExpr pExpr = (ParameterizedExpr)node.getRHS();
			if (pExpr.getTarget() instanceof NameExpr){ //..where the target is a name..
				NameExpr nExpr = (NameExpr)pExpr.getTarget();
				String name = nExpr.getName().getID();
				if (map.containsKey(name)){ //..and the name is in the map
					if (DEBUG) System.err.println("inlining found parametric/assign call to "+name);
					//build info object
					info = new InlineInfo<ScriptOrFunction,TargetScriptOrFunction>(copy(map.get(name)),
					        targetTree, node, pExpr.getArgList(), getLHSList(node.getLHS()), true);
					if (query.doInline(info)) newNode = new TransformedNode(inline(info));
				}
			}
		}		
	}
	
	@SuppressWarnings("unchecked")
    private ScriptOrFunction copy(ScriptOrFunction origin){
	    if (origin instanceof Function){
	        return ((ScriptOrFunction)((Function)origin).fullCopy());
	    } else if (origin instanceof Script){
	        return ((ScriptOrFunction)((Script)origin).fullCopy());
	    } else {
	        throw new UnsupportedOperationException();
	    }
	}
	
	//turns the RHS of an assignment statement into a list of LValue Expressions
	//The RHS is just an expression, but it has to be some sort of LValue
	//if it is not, then a unsupported operation runtime exception gets thrown
	//the [x1,x2,...] case gets resolved here
	private List<LValueExpr> getLHSList(Expr value){
		List<LValueExpr> list = new List<LValueExpr>();
		if (value instanceof LValueExpr) {
			LValueExpr lValue = (LValueExpr) value;
			if (lValue instanceof MatrixExpr){
				//there are multiple lvalues - they are in a matrix
				//put every element of the first row into the result
				Row row = ((MatrixExpr)lValue).getRow(0);
				for (Expr expr : row.getElementList()){
					list.add((LValueExpr)expr);
				}
			} else {
				list.add(lValue);				
			}
		} else {
			throw new UnsupportedOperationException("Inliner received non LValue as LHS of assignment");
		}
		return list;
	}
	
	
	@Override
	public void caseExprStmt(ExprStmt node) {
		//node is of the case f
		if (node.getExpr() instanceof NameExpr){
			String name = ((NameExpr)node.getExpr()).getName().getID();
			if (map.containsKey(name)){
				if (DEBUG) System.err.println("inlining found unparametric/assign call to "+name);
				//build info object
				InlineInfo info = new InlineInfo<ScriptOrFunction,TargetScriptOrFunction>(copy(map.get(name)),
				        targetTree, node, new List<Expr>(), new List<LValueExpr>(), false);
				if (query.doInline(info)) newNode = new TransformedNode(inline(info));
			}
		}
		//node is of the case f(..)
		if (node.getExpr() instanceof ParameterizedExpr){ //RHS is parametrized expr..
			ParameterizedExpr pExpr = (ParameterizedExpr)node.getExpr();
			if (pExpr.getTarget() instanceof NameExpr){ //..where the target is a name..
				NameExpr nExpr = (NameExpr)pExpr.getTarget();
				String name = nExpr.getName().getID();
				if (map.containsKey(name)){ //..and the name is in the map
					if (DEBUG) System.err.println("inlining found parametric/assign call to "+name);
					//build info object
					InlineInfo info = new InlineInfo<ScriptOrFunction,TargetScriptOrFunction>(copy(map.get(name)),
					        targetTree, node, pExpr.getArgs(), new List<LValueExpr>(), true);
					if (query.doInline(info)) newNode = new TransformedNode(inline(info));
				}
			}
		}
	}
	
	
	
	
	/**
	 * performs an actual inlining.
	 * Does not deal with varargout, varargin
	 * @param info an Object with all the information regarding the inlining
	 * @return a list of Statements
	 */
	protected java.util.List<Stmt> inline(InlineInfo<ScriptOrFunction,TargetScriptOrFunction> info){
        java.util.List<Stmt> stmts = new java.util.LinkedList<Stmt>();
	    if (info.getInlinedScriptOrFunction() instanceof Function){
	        Function inlinedFunction = (Function)info.getInlinedScriptOrFunction();
	        //assign the parameters
	        //error: too many parameters when calling
	        if (info.getParameters().getNumChild() > inlinedFunction.getNumInputParam()){
	            throw new UnsupportedOperationException("cannot inline -- function called with too many parameters.");
	        }
	        for (int i = 0; i < info.getParameters().getNumChild();i++){
	            AssignStmt ass = new AssignStmt(
	                    new NameExpr(inlinedFunction.getInputParam(i)),
	                    info.getParameters().getChild(i));
	            ass.setOutputSuppressed(true);
	            stmts.add(ass);
	        }
	        
	        //put the body
	        for (Stmt stmt : inlinedFunction.getStmtList()){
	            stmts.add(stmt);
	        }
	        
	        //assign the return values
	        //possible error - too many return values?
	        if (info.getTargets().getNumChild() > inlinedFunction.getNumOutputParam()){
	            throw new UnsupportedOperationException("cannot inline -- function called with too many output parameters.");
	        }
	        //get the output suppression flag
	        boolean outputSuppressed = info.getCallStatement().isOutputSuppressed();
	        for (int i = 0; i < info.getTargets().getNumChild();i++){
	            AssignStmt assign = new AssignStmt(
	                    info.getTargets().getChild(i),
	                    new NameExpr(inlinedFunction.getOutputParam(i)));
	            assign.setOutputSuppressed(outputSuppressed);
	            stmts.add(assign);
	        }
	    } else if (info.getInlinedScriptOrFunction() instanceof Script){
	        //there can't be args
	        if (info.getParameters().getNumChild() > 0 || info.getTargets().getNumChild() > 0){
	            throw new UnsupportedOperationException("cannot inline script with input or output arguments");
	        }
            for (Stmt stmt : ((Script)(info.getInlinedScriptOrFunction())).getStmtList()){
                stmts.add(stmt);
            }	        
	    } else {
	        throw new UnsupportedOperationException("trying to inline neither script nor function");
	    }
        return stmts;
	}
}

