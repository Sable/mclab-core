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
	
	public SPOutputlist() {}
	
	public ShapePropMatch<V> match(boolean isPatternSide, ShapePropMatch<V> previousMatchResult, Args<V> argValues, int Nargout) {
		ShapePropMatch<V> match = first.match(isPatternSide, previousMatchResult, argValues, Nargout);
		if (match.gethowManyEmitted()==Nargout && next == null) {
			if (Debug) System.out.println("output emitted done!");
			match.setIsOutputDone();
			return match;
		}
		else if (match.gethowManyEmitted()!=Nargout && next!=null) {
			return next.match(isPatternSide, match, argValues, Nargout);
		}
		else {
			//shape matching fails.
			return match;
		}
		
	}
	
	public String toString(){
		return first.toString() + (next==null? "" : ","+next);
	}
}
