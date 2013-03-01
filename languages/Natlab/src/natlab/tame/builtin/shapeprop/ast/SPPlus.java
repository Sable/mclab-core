package natlab.tame.builtin.shapeprop.ast;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.*;

public class SPPlus<V extends Value<V>> extends SPAbstractMatchExpr<V>{
	static boolean Debug = false;
	SPAbstractMatchExpr<V> sp;
	public SPPlus (SPAbstractMatchExpr<V> sp){
		this.sp = sp;
		//System.out.println("+");
	}
	
	public ShapePropMatch<V> match(boolean isPatternSide, ShapePropMatch<V> previousMatchResult, Args<V> argValues, int num){
		ShapePropMatch<V> keepMatch = sp.match(isPatternSide, previousMatchResult, argValues, num);
		while((argValues.size()>keepMatch.getNumMatched())&&(keepMatch.getIsError()==false)){
			if (Debug) System.out.println("inside plus loop "+keepMatch.getNumMatched());
			if (Debug) System.out.println(keepMatch.getNumMatched());
			if (Debug) System.out.println("index doesn't point null, keep matching!");
			keepMatch = sp.match(isPatternSide, keepMatch, argValues, num);
		}
		return keepMatch;
	}
	
	public String toString(){
		return sp.toString()+"+";
	}
}
