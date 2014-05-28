package natlab.tame.builtin.shapeprop.ast;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Value;

public class SPPlus<V extends Value<V>> extends SPAbstractMatchExpr<V> {
	
	static boolean Debug = false;
	SPAbstractMatchExpr<V> sp;
	
	public SPPlus (SPAbstractMatchExpr<V> sp) {
		this.sp = sp;
	}
	
	public ShapePropMatch<V> match(boolean isPatternSide, ShapePropMatch<V> previousMatchResult, Args<V> argValues, int Nargout) {
		ShapePropMatch<V> matchResult = sp.match(isPatternSide, previousMatchResult, argValues, Nargout);
		while (argValues.size()>matchResult.getHowManyMatched() && !matchResult.getIsError()) {
			if (Debug) System.out.println("inside multiple shape pattern matching.");
			matchResult = sp.match(isPatternSide, matchResult, argValues, Nargout);
		}
		return matchResult;
	}
	
	public String toString() {
		return sp.toString() + "+";
	}
}
