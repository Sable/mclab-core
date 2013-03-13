package natlab.tame.builtin.shapeprop.ast;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.*;

public class SPMult<V extends Value<V>> extends SPAbstractMatchExpr<V>{
	static boolean Debug = false;
	SPAbstractMatchExpr<V> sp;
	public SPMult (SPAbstractMatchExpr<V> sp){
		this.sp = sp;
		//System.out.println("*");
	}
	
	public ShapePropMatch<V> match(boolean isPatternSide, ShapePropMatch<V> previousMatchResult, Args<V> argValues, int num){
		while((argValues.size()>previousMatchResult.getHowManyMatched())&&(!previousMatchResult.getIsError()))
		{
			if (Debug) System.out.println("inside star loop "+previousMatchResult.getHowManyMatched());
			if (Debug) System.out.println(previousMatchResult.getHowManyMatched());
			if (Debug) System.out.println("index doesn't point null, keep matching!");
			previousMatchResult = sp.match(isPatternSide, previousMatchResult, argValues, num);
		}
		
		return previousMatchResult;
	}
	
	public String toString(){
		return sp.toString()+"*";
	}
}
