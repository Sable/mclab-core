package natlab.tame.interproceduralAnalysis;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ast.ASTNode;

/**
 * This object stores all the information about one call site. A call site
 * is a an AST node (usually an TIR statement) where for a particular analysis
 * one or more calls may occur. This object stores
 * - the AST node which is the call site
 * - all the possible call edges
 * - the interprocedural analysis node for which this call site is valid
 * 
 * Note that one function may have many interprocedural analysis nodes associated
 * with it, which means that an ast node in that function may have multiple
 * different callsite objects associated with it - i.e. up to one for each 
 * interprocedural analysis node.
 * 
 * To add call edges that are analyzed by an interprocedural analysis, an analysis
 * has to call the analyze method of an interprocedural analysis node. That's also
 * where analyses have to create callsites.
 * 
 * Analyses can directly add un-analyzed call edges for example for calls to builtin
 * nodes.
 * 
 * TODO - should there be a second list of un-analyzed calls, for example to model builtin calls?
 * @author adubra
 */
public class Callsite<F extends FunctionAnalysis<A,R>, A, R> {
	private InterproceduralAnalysisNode<F, A, R> interProceduralAnalysisNode;
	private ASTNode<?> astNode;
	private HashMap<Call<A>,InterproceduralAnalysisNode<F,A,R>> calls
			= new HashMap<Call<A>,InterproceduralAnalysisNode<F,A,R>>();
	private HashSet<Call<A>> builtinCalls = new HashSet<Call<A>>();
	
	/**
	 * creates a new callsite - should only be called from within the analysis framework
	 */
	protected Callsite(InterproceduralAnalysisNode<F,A,R> interProcAnalnode,ASTNode<?> astNode){
		this.astNode = astNode;
		this.interProceduralAnalysisNode = interProcAnalnode;
	}
	/**
	 * adds a new call edge to this call site object should only be used by the
	 * interprocedural analysis framework
	 */
	protected void addCall(Call<A> call,InterproceduralAnalysisNode<F,A,R> targetNode){
		this.calls.put(call, targetNode);
	}
	
	
	/**
	 * returns the ast node of this callsite
	 */
	public ASTNode<?> getASTNode(){
		return astNode;
	}
	
	/**
	 * returns the interprocedural analysis node of this callsite
	 */
	public InterproceduralAnalysisNode<F,A,R> getInterproceduralAnalysisNode(){
		return interProceduralAnalysisNode;
	}
	
	/**
	 * returns the set of callsites as an unmodifiable map, where
	 * the keys are the calls, and the values are the associated interprocedural
	 * analysis nodes.
	 */
	public Map<Call<A>,InterproceduralAnalysisNode<F,A,R>> getCalls(){
		return Collections.unmodifiableMap(this.calls);
	}
	
	/**
	 * adds an un-analyzed call edges, for example a call to a builtin function.
	 * This method should only be called  by an analysis.
	 */
	public void addBuiltinCall(Call<A> call){
		builtinCalls.add(call);
	}
	
	/**
	 * returns the set of un-analyzed call edges as an unmodifiable set
	 */
	public Set<Call<A>> getBuiltinCalls(){
		return Collections.unmodifiableSet(builtinCalls);
	}
}




