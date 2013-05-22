package natlab.tame.builtin.shapeprop.ast;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.*;

public class SPRParen<V extends Value<V>> extends SPAbstractMatchExpr<V> {
	
	SPPatternlist<V> p;
	public SPRParen (SPPatternlist<V> p) {
		this.p = p;
	}
	
	public ShapePropMatch<V> match(boolean isPatternSide, ShapePropMatch<V> previousMatchResult, Args<V> argValues, int Nargout) {
		ShapePropMatch<V> matchResult = p.match(isPatternSide, previousMatchResult, argValues, Nargout);
		return matchResult;
	}
	
	public String toString() {
		return "(" + p.toString() + ")";
	}
}
