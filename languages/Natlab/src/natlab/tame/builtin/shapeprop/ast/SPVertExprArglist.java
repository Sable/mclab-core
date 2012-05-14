package natlab.tame.builtin.shapeprop.ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.components.shape.*;
import natlab.tame.valueanalysis.value.*;

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
	
	public ShapePropMatch match(boolean isPatternSide, ShapePropMatch previousMatchResult, List<? extends Value<?>> argValues)
	{
		ShapePropMatch match = first.match(isPatternSide, previousMatchResult, argValues);
		if(match.getIsError()==true){
			return match;			
		}
		if(next!=null){
			ShapePropMatch continueMatch = next.match(isPatternSide, match, argValues);
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
