package natlab.toolkits.rewrite;
import java.util.*;

import javax.management.RuntimeErrorException;

import ast.*;
import ast.List;



/**
 * Inlines every call to functions of given names.
 * The names of the function and the corresponding functions.
 * Every call to the name replaced with a copy of the corresponding function.
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
 * 
 * 
 * @author ant6n
 *
 */

public class Inliner extends AbstractLocalRewrite {
	private Map<String, Function> map;
	Inliner.Query query;
	Function functionTree; //the function which is being inlined
	/**
	 * a trivial query object that always allows all inlining actions.
	 */
	public static final Inliner.Query TRIVIAL_QUERY_OBJECT = new Inliner.Query() {
		public boolean doInline(InlineInfo inlineInfo) { return true; }
	};
	
	
	public Inliner(Function tree,Map<String, Function> map) {
		this(tree,map,TRIVIAL_QUERY_OBJECT);
	}
	public Inliner(Function tree,Map<String, Function> map,Inliner.Query query) {
		super(tree);
		this.map = map;
		this.query = query;
		this.functionTree = tree;
	}
	
	
	@Override
	public void caseAssignStmt(AssignStmt node) {
		//the info object used to query whether to inline, and as argument to acutally inline
		InlineInfo info; 
		//node is of the case [x,..] = f
		if (node.getRHS() instanceof NameExpr){
			String name = ((NameExpr)node.getRHS()).getName().getID();
			if (map.containsKey(name)){
				System.err.println("inlining found unparametric/assign call to "+name);
				//build info object
				info = new InlineInfo(map.get(name).copy(), functionTree, node,
						new List<Expr>(), getLHSList(node.getLHS()), false);
				if (query.doInline(info)) newNode = new TransformedNode<ASTNode>(inline(info));
			}
		}
		//node is of the case [x,..] = f(..)
		if (node.getRHS() instanceof ParameterizedExpr){ //RHS is parametrized expr..
			ParameterizedExpr pExpr = (ParameterizedExpr)node.getRHS();
			if (pExpr.getTarget() instanceof NameExpr){ //..where the target is a name..
				NameExpr nExpr = (NameExpr)pExpr.getTarget();
				String name = nExpr.getName().getID();
				if (map.containsKey(name)){ //..and the name is in the map
					System.err.println("inlining found parametric/assign call to "+name);
					//build info object
					info = new InlineInfo(map.get(name).copy(), functionTree, node,
							pExpr.getArgList(), getLHSList(node.getLHS()), true);
					if (query.doInline(info)) newNode = new TransformedNode<ASTNode>(inline(info));
				}
			}
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
				System.err.println("inlining found unparametric/assign call to "+name);
				//build info object
				InlineInfo info = new InlineInfo(this.functionTree, map.get(name).copy(), node,
						new List<Expr>(), new List<LValueExpr>(), false);
				if (query.doInline(info)) newNode = new TransformedNode<ASTNode>(inline(info));
			}
		}
		//node is of the case f(..)
		if (node.getExpr() instanceof ParameterizedExpr){ //RHS is parametrized expr..
			ParameterizedExpr pExpr = (ParameterizedExpr)node.getExpr();
			if (pExpr.getTarget() instanceof NameExpr){ //..where the target is a name..
				NameExpr nExpr = (NameExpr)pExpr.getTarget();
				String name = nExpr.getName().getID();
				if (map.containsKey(name)){ //..and the name is in the map
					System.err.println("inlining found parametric/assign call to "+name);
					//build info object
					InlineInfo info = new InlineInfo(this.functionTree, map.get(name).copy(), node,
							pExpr.getArgs(), new List<LValueExpr>(), true);
					if (query.doInline(info)) newNode = new TransformedNode<ASTNode>(inline(info));
				}
			}
		}
	}
	
	
	
	
	/**
	 * performs an actual inlining.
	 * Does not deal with varargout, varargin
	 * @param info an Object with all the information regarding the inlining
	 * @return a list of Statements inside a transformed node
	 */
	protected java.util.List<Stmt> inline(InlineInfo info){
		java.util.List<Stmt> stmts = new java.util.LinkedList<Stmt>();
		//assign the parameters
		//error: too many parameters when calling
		if (info.getParameters().getNumChild() > info.getInlinedFunction().getNumInputParam()){
			throw new UnsupportedOperationException("cannot inline -- function called with too many parameters.");
		}
		for (int i = 0; i < info.getParameters().getNumChild();i++){
			AssignStmt ass = new AssignStmt(
					new NameExpr(info.getInlinedFunction().getInputParam(i)),
					info.getParameters().getChild(i));
			ass.setOutputSuppressed(true);
			stmts.add(ass);
		}
		
		//put the body
		for (Stmt stmt : info.getInlinedFunction().getStmtList()){
			stmts.add(stmt);
		}
		
		//assign the return values
		//possible error - too many return values?
		if (info.getTargets().getNumChild() > info.getInlinedFunction().getNumOutputParam()){
			throw new UnsupportedOperationException("cannot inline -- function called with too many output parameters.");
		}
		//get the output suppression flag
		boolean outputSuppressed = info.getCallStatement().isOutputSuppressed();
		for (int i = 0; i < info.getTargets().getNumChild();i++){
			AssignStmt assign = new AssignStmt(
					info.getTargets().getChild(i),
					new NameExpr(info.getInlinedFunction().getOutputParam(i)));
			assign.setOutputSuppressed(outputSuppressed);
			stmts.add(assign);
		}
		return stmts;
	}
	
	
	/**
	 * when inlining a function, there might be some cleanup that needs to be done.
	 * This interface allows to either perform cleanup (like dealing with nargin,
	 * vargin,vargout etc.), or forbidding the inline action altogether.
	 */
	public interface Query{
		/**
		 * Determines whether the current inline action should be performed, and
		 * allows updating the function to be inlined via the given QueryObject.
		 * @param inlineInfo an Object containing information about the inline action
		 * @return true if the inline action should be performed, false otherwise.
		 */
		public boolean doInline(InlineInfo inlineInfo);
	}
	
	/**
	 * The Inliner.QueryObject describes how one function
	 * is about to be inlined within another, at one specific
	 * point.
	 * 
	 * This can be used by the Inline Query Object 
	 */
	public class InlineInfo{
		private Function inlinedFunction;
		private Function target;
		private Stmt callStatement;
		private List<Expr> parameters;
		private List<LValueExpr> targets;
		boolean isParametric;
		
		//note - the inlinedFuctnion should be a copy
		private InlineInfo(Function inlinedFunction,Function target,
				Stmt callStatement, List<Expr> parameters, List<LValueExpr> targets,
				boolean isParametric){
			this.inlinedFunction = inlinedFunction;
			this.target = target;
			this.callStatement = callStatement;
			this.parameters = parameters;
			this.targets = targets;
		}
		
		/**
		 * returns the function that is being inlined
		 * This should be a unique copy
		 */
		public Function getInlinedFunction(){
			return inlinedFunction;
		}
		/**
		 * replaces the function that is being inlined
		 * @param function
		 */
		public void setInlinedFunction(Function function){
			this.inlinedFunction = function;
		}
		/**
		 * returns the function in which the function is inliend
		 */
		public Function getTarget(){ return target; }
		/**
		 * returns the statement of the call where the function is
		 * about to get inlined
		 */
		public Stmt getCallStatement(){ return callStatement; }
		/**
		 * returns the list of expressions that the function is being called with
		 */
		public List<Expr> getParameters(){ return parameters; }
		/**
		 * returns the lvalues that the result of the function is being assigned to
		 */
		public List<LValueExpr> getTargets(){ return targets; }
		/**
		 * returns whether there are parenthesis around the call
		 * all calls that have one or more arguments are parametric,
		 * but if the call has zero arguments it may or may not be parametric,
		 * i.e. foo vs foo()
		 */
		public boolean isParametric(){ return isParametric; }
	}
}
