package natlab.tame.builtin.shapeprop.ast;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.*;

public class SPOutputlist<V extends Value<V>> extends SPNode<V> {
	
	static boolean Debug = false;
	SPAbstractVectorExpr<V> first;
	SPOutputlist<V> next;
	
	public SPOutputlist(SPAbstractVectorExpr<V> first, SPOutputlist<V> next) {
		this.first = first;
		this.next = next;
	}
	
	public ShapePropMatch<V> match(boolean isPatternSide, ShapePropMatch<V> previousMatchResult, Args<V> argValues, int Nargout) {
		ShapePropMatch<V> matchResult = first.match(isPatternSide, previousMatchResult, argValues, Nargout);
		if (matchResult.gethowManyEmitted()==Nargout && next == null) {
			if (Debug) System.out.println("output emitting done!");
			matchResult.setIsOutputDone();
			return matchResult;
		}
		else if (matchResult.gethowManyEmitted()!=Nargout && next!=null) {
			return next.match(isPatternSide, matchResult, argValues, Nargout);
		}
		else {
			// shape matching fails.
			return matchResult;
		}
		
	}
	
	public String toString() {
		return first.toString() + (next==null? "" : ","+next);
	}
}
