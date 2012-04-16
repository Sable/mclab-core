package natlab.tame.builtin.shapeprop.ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Value;

public class SPVertExprArglist extends SPNode
{
	SPAbstractVertcatExprArg first;
	SPVertExprArglist next;
	public SPVertExprArglist (SPAbstractVertcatExprArg first, SPVertExprArglist next)
	{
		this.first = first;
		this.next = next;
		//System.out.println(",");
	}
	
	public ShapePropMatch match(boolean isPatternSide, ShapePropMatch previousMatchResult, ArrayList<Integer> argValues)
	{
		ShapePropMatch match = first.match(isPatternSide, previousMatchResult, argValues);
		if(next!=null){
			ShapePropMatch continueMatch = next.match(isPatternSide, match, argValues);
			//System.out.println(continueMatch.getLatestMatchedLowerCase());
			return continueMatch;
		}
		else
			return match;
	}
	
	public String toString()
	{
		return first.toString()+(next==null?"":","+next);
	}
}
