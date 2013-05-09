package natlab.tame.builtin.shapeprop.ast;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.*;

public class SPArglist<V extends Value<V>> extends SPNode<V> {
	
	SPAbstractMatchElement<V> first;
	SPArglist<V> next;
	
	public SPArglist(SPAbstractMatchElement<V> first, SPArglist<V> next) {
		this.first = first;
		this.next = next;
	}
	
	public ShapePropMatch<V> match(boolean isPatternSide, ShapePropMatch<V> previousMatchResult, Args<V> argValues, int Nargout) {
		ShapePropMatch<V> matchResult = first.match(isPatternSide, previousMatchResult, argValues, Nargout);
		if (!matchResult.getIsError() && next!=null) {
			ShapePropMatch<V> continueMatch = next.match(isPatternSide, matchResult, argValues, Nargout);
			return continueMatch;
		}
		return matchResult;
	}
	
	public String toString() {
		return first.toString() + (next==null? "" : ","+next);
	}
}
