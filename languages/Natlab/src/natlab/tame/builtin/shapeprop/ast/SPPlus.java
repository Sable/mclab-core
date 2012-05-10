package natlab.tame.builtin.shapeprop.ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Value;

public class SPPlus extends SPAbstractMatchExpr
{
	SPAbstractMatchExpr sp;
	public SPPlus (SPAbstractMatchExpr sp)
	{
		this.sp = sp;
		//System.out.println("+");
	}
	
	public ShapePropMatch match(boolean isPatternSide, ShapePropMatch previousMatchResult, List<? extends Value<?>> argValues)
	{
		ShapePropMatch keepMatch = sp.match(isPatternSide, previousMatchResult, argValues);
		while(argValues.get(keepMatch.getNumMatched())!=null){
			keepMatch = sp.match(isPatternSide, keepMatch, argValues);
		}
		return keepMatch;
	}
	
	public String toString()
	{
		return sp.toString()+"+";
	}
}
