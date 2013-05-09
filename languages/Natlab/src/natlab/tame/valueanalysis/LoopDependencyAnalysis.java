package natlab.tame.valueanalysis;

import java.util.*;

import ast.*;
import natlab.tame.tir.analysis.*;
import natlab.tame.tir.*;

public class LoopDependencyAnalysis 
extends TIRAbstractSimpleStructuralForwardAnalysis<Map<String, Set<String>>> {
	static boolean Debug = false;
    Map<String, Set<String>> dependenceMap = new HashMap<String, Set<String>>();
    Set<String> dependentVars = new HashSet<String>();
	
	public LoopDependencyAnalysis(ASTNode tree) {
		super(tree);
		analyze(tree);
	}

	@Override
	public Map<String, Set<String>> newInitialFlow() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Set<String>> merge(Map<String, Set<String>> in1,
			Map<String, Set<String>> in2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Set<String>> copy(Map<String, Set<String>> source) {
		// TODO Auto-generated method stub
		return null;
	}
    
    /**
     * helper method to analyze dependence in loop.
     */
    private void dependenceAnalyze() {
    	for (String lhs : dependenceMap.keySet()) {
    		for (String rhsArg : dependenceMap.get(lhs)) {
    			if (dependenceMap.keySet().contains(rhsArg) 
    					&& dependenceMap.get(rhsArg).contains(lhs)) {
    				dependentVars.add(rhsArg);
    			}
    		}
    	}
    }
	
	public Set<String> getResult() {
		return dependentVars;
	}
	
	/*********** statement cases for dependency analysis**********************/
	@Override
	public void caseTIRWhileStmt(TIRWhileStmt node) {
		if (Debug) System.out.println("hola, inside dependency while stmt.");
		ASTNode body = node.getStmts();
		analyze(body);
		dependenceAnalyze();
	}

	@Override
	public void caseTIRForStmt(TIRForStmt node) {
		if (Debug) System.out.println("hola, inside dependency for stmt.");
		ASTNode body = node.getStmts();
        analyze(body);
        dependenceAnalyze();
	}
	
	@Override
	public void caseTIRCallStmt(TIRCallStmt node) {
		if (Debug) System.out.println("hola, inside dependency call stmt.");
		String targetName = node.getTargetName().getID();
		if (Debug) System.out.println(node.getTargetName().getID()+" : " + node.getArguments());
		Set<String> value = new HashSet<String>();
		for (NameExpr arg : node.getArguments().getNameExpressions()) {
			value.add(arg.getName().getID());
		}
		dependenceMap.put(targetName, value);
		if (Debug) System.out.println(dependenceMap);
	}

	@Override
	public void caseTIRCopyStmt(TIRCopyStmt node) {
		if (Debug) System.out.println("hola, inside dependency copy stmt.");
		String targetName = node.getTargetName().getID();
		String sourceName = node.getSourceName().getID();
		if (Debug) System.out.println(targetName+" : " + sourceName);
		Set<String> value = new HashSet<String>();
		value.add(sourceName);
		dependenceMap.put(targetName, value);
		if (Debug) System.out.println(dependenceMap);
	}
}
