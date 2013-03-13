package natlab.tame.builtin.shapeprop.ast;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.*;

public class SPCaselist<V extends Value<V>> extends SPNode<V> {
	
	static boolean Debug = false;
	SPCase<V> first;
	SPCaselist<V> next;
	
	public SPCaselist(SPCase<V> first, SPCaselist<V> next) {
		this.first = first;
		this.next = next;
	}
	
	public ShapePropMatch<V> match(boolean isPatternSide, ShapePropMatch<V> previousMatchResult, Args<V> argValues, int Nargout) {
		ShapePropMatch<V> matchResult = first.match(isPatternSide, previousMatchResult, argValues, Nargout);
		// the first case's shape matching is successful.
		if (matchResult.getIsoutputDone()) {
			if (Debug) System.out.println("matching and results emmitting successfully!");
			return matchResult;
		}
		// the first case's shape matching doesn't succeed, go to next case if there is one.
		else if (!matchResult.getIsoutputDone() && next!=null) {
			isPatternSide = true;
			ShapePropMatch<V> newMatchResult = next.match(isPatternSide, new ShapePropMatch<V>(), argValues, Nargout);
			return newMatchResult;
		}
		// all the cases' shape matching don't succeed, return null; ShapePropTool will handle this null.
		else {
			return null;
		}
	}
	
	public String toString(){
		return first.toString() + (next==null? "" : "||"+next);
	}
}
