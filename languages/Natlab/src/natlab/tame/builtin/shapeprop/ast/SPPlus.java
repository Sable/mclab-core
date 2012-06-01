package natlab.tame.builtin.shapeprop.ast;

import java.util.List;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Value;

public class SPPlus extends SPAbstractMatchExpr{
	static boolean Debug = false;
	SPAbstractMatchExpr sp;
	public SPPlus (SPAbstractMatchExpr sp){
		this.sp = sp;
		//System.out.println("+");
	}
	
	public ShapePropMatch match(boolean isPatternSide, ShapePropMatch previousMatchResult, List<? extends Value<?>> argValues, int num){
		ShapePropMatch keepMatch = sp.match(isPatternSide, previousMatchResult, argValues, num);
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
