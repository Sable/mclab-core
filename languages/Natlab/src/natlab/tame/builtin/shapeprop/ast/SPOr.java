package natlab.tame.builtin.shapeprop.ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Value;

public class SPOr extends SPAbstractMatchExpr
{
	SPAbstractMatchExpr first;
	SPAbstractMatchExpr next;
	
	public SPOr (SPAbstractMatchExpr first,SPAbstractMatchExpr next)
	{
		this.first = first;
		this.next = next;
		//System.out.println("|");
	}
	
	public ShapePropMatch match(boolean isPatternSide, ShapePropMatch previousMatchResult, List<? extends Value<?>> argValues)
	{
		int indexBeforeOr = previousMatchResult.getNumMatched();
		ShapePropMatch match = first.match(isPatternSide, previousMatchResult, argValues);
		int indexAfterOr = match.getNumMatched();
		if(indexBeforeOr==indexAfterOr){
			ShapePropMatch continueMatch = next.match(isPatternSide, match, argValues);//actually, here, match is the same to previousMatchResult
			return continueMatch;
		}
		return match;
	}
	
	public String toString()
	{
		return first.toString()+"|"+next.toString();
	}
}
