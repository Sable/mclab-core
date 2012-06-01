package natlab.tame.builtin.shapeprop.ast;

import java.util.List;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Value;

public class SPStar extends SPAbstractMatchExpr{
	static boolean Debug = false;
	SPAbstractMatchExpr sp;
	public SPStar (SPAbstractMatchExpr sp){
		this.sp = sp;
		//System.out.println("*");
	}
	
	public ShapePropMatch match(boolean isPatternSide, ShapePropMatch previousMatchResult, List<? extends Value<?>> argValues, int num){
		while((argValues.size()>previousMatchResult.getNumMatched())&&(previousMatchResult.getIsError()==false))
		{
			if (Debug) System.out.println("inside star loop "+previousMatchResult.getNumMatched());
			if (Debug) System.out.println(previousMatchResult.getNumMatched());
			if (Debug) System.out.println("index doesn't point null, keep matching!");
			previousMatchResult = sp.match(isPatternSide, previousMatchResult, argValues, num);
		}
		
		return previousMatchResult;
	}
	
	public String toString(){
		return sp.toString()+"*";
	}
}
