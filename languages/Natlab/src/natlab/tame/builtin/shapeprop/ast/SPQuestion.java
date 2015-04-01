package natlab.tame.builtin.shapeprop.ast;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Value;

public class SPQuestion<V extends Value<V>> extends SPAbstractMatchExpr<V> {
	
	static boolean Debug = false;
	SPAbstractMatchExpr<V> spm;
	
	public SPQuestion (SPAbstractMatchExpr<V> spm) {
		this.spm = spm;
	}
	
	public ShapePropMatch<V> match(boolean isPatternSide, ShapePropMatch<V> previousMatchResult, Args<V> argValues, int Nargout) {
		if (argValues.size() > previousMatchResult.getHowManyMatched()) {
			System.out.println("inside may-have shape pattern matching.");
			ShapePropMatch<V> matchResult = spm.match(isPatternSide, previousMatchResult, argValues, Nargout);
			return matchResult;
		}
		return previousMatchResult;
	}
	
	public String toString() {
		return spm.toString() + "?";
	}
}
