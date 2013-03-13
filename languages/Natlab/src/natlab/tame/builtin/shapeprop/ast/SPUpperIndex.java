package natlab.tame.builtin.shapeprop.ast;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.*;

public class SPUpperIndex<V extends Value<V>> extends SPAbstractMatchElement<V> {
	
	String s;
	SPAbstractScalarExpr<V> n;
	
	public SPUpperIndex(String s, SPAbstractScalarExpr<V> n) {
		this.s = s;
		this.n = n;
	}
	
	/**
	 * for M(2), this node just want to get the value of M's second dimension size, 
	 * if in an assignment, just modify the number of second dimension. just save 
	 * string s and mostly number n as the latest matched thing, and set inArrayIndex true
	 */
	public ShapePropMatch<V> match(boolean isPatternSide, ShapePropMatch<V> previousMatchResult, Args<V> argValues, int Nargout) {
		previousMatchResult.saveLatestMatchedUppercase(s);
		ShapePropMatch<V> matchResult = n.match(isPatternSide, previousMatchResult, argValues, Nargout);
		matchResult.setIsAssignLiteralToLHS(true);
		return matchResult;
	}
	
	public String toString() {
		return s.toString() + "(" + n.toString() + ")";
	}
}
