package natlab.tame.builtin.shapeprop.ast;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.*;

public class SPOutput<V extends Value<V>> extends SPNode<V>{
	static boolean Debug = false;
	SPAbstractVectorExpr<V> first;
	SPOutput<V> next;
	
	public SPOutput(SPAbstractVectorExpr<V> first, SPOutput<V> next){
		this.first = first;
		this.next = next;
	}
	
	public SPOutput() {}
	
	public ShapePropMatch<V> match(boolean isPatternSide, ShapePropMatch<V> previousMatchResult, Args<V> argValues, int num){
		ShapePropMatch<V> match = first.match(isPatternSide, previousMatchResult, argValues, num);
		if(next == null){
			if (Debug) System.out.println("output emitted done!");
			match.setOutputIsDone();
			return match;
		}
		return next.match(isPatternSide, match, argValues, num);
	}
	
	public String toString(){
		return first.toString()+(next==null?"":","+next);
	}
}
