package natlab.tame.builtin.shapeprop.ast;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Value;

public class SPOr<V extends Value<V>> extends SPAbstractMatchExpr<V> {
	
	SPAbstractMatchExpr<V> first;
	SPAbstractMatchExpr<V> next;
	
	public SPOr (SPAbstractMatchExpr<V> first,SPAbstractMatchExpr<V> next) {
		this.first = first;
		this.next = next;
	}
	
	public ShapePropMatch<V> match(boolean isPatternSide, ShapePropMatch<V> previousMatchResult, Args<V> argValues, int Nargout) {
		int indexBeforeOr = previousMatchResult.getHowManyMatched();
		ShapePropMatch<V> firstMatchResult = first.match(isPatternSide, previousMatchResult, argValues, Nargout);
		int indexAfterOr = firstMatchResult.getHowManyMatched();
		if (indexBeforeOr == indexAfterOr) { 
			// which means first part matching fails, reset error flag, and goes to next part matching.
			if (firstMatchResult.getIsError()) firstMatchResult.setIsError(false);
			ShapePropMatch<V> continueMatch = next.match(isPatternSide, firstMatchResult, argValues, Nargout);
			return continueMatch;
		}
		// which means first part matching succeed.
		return firstMatchResult;
	}
	
	public String toString() {
		return first.toString() + "|" + next.toString();
	}
}
