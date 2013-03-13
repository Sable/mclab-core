package natlab.tame.builtin.shapeprop.ast;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.*;

public class SPPatternlist<V extends Value<V>> extends SPNode<V> {
	
	SPAbstractPattern<V> first;
	SPPatternlist<V> next;
	
	public SPPatternlist (SPAbstractPattern<V> first, SPPatternlist<V> next) {
		this.first = first;
		this.next = next;
	}
	
	public ShapePropMatch<V> match(boolean isPatternSide, ShapePropMatch<V> previousMatchResult, Args<V> argValues, int Nargout) {
		ShapePropMatch<V> match = first.match(isPatternSide, previousMatchResult, argValues, Nargout);
		if (match.getIsError()) return match;
		if (next!=null) {
			ShapePropMatch<V> continueMatch = next.match(isPatternSide, match, argValues, Nargout);
			return continueMatch;
		}
		else return match;
	}
	
	public String toString() {
		return first.toString() + (next==null? "" : ","+next);
	}
}
