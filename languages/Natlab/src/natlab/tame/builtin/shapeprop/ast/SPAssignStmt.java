package natlab.tame.builtin.shapeprop.ast;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.*;

public class SPAssignStmt<V extends Value<V>> extends SPAbstractPattern<V> {
	
	SPAbstractMatchElement<V> lhs;
	SPAbstractMatchElement<V> rhs;
	
	public SPAssignStmt(SPAbstractMatchElement<V> lhs, SPAbstractMatchElement<V> rhs) {
		this.lhs = lhs;
		this.rhs = rhs;
	}
	
	public ShapePropMatch<V> match(boolean isPatternSide, ShapePropMatch<V> previousMatchResult, Args<V> argValues, int Nargout) {
		previousMatchResult.setIsInsideAssign(true);
		/*
		 * since some symbol, like number or lowercase, may appear in multiple places, 
		 * we need some flag to inform these to the matching algorithm.
		 */
		ShapePropMatch<V> lhsMatch = lhs.match(isPatternSide, previousMatchResult, argValues, Nargout);
		/*
		 * above matching is not an argument-consuming matching, so it won't return matching error, 
		 * we don't need to check that, just go to the RHS.
		 */
		ShapePropMatch<V> rhsMatch = rhs.match(isPatternSide, lhsMatch, argValues, Nargout);
		rhsMatch.setIsInsideAssign(false);
		return rhsMatch;
	}
	
	public String toString(){
		return lhs.toString()+"="+rhs.toString();
	}
}
