package natlab.tame.builtin.shapeprop.ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Value;

public class SPPatternList extends SPAbstractPattern
{
	SPAbstractPattern first;
	SPAbstractPattern next;
	
	public SPPatternList (SPAbstractPattern first, SPAbstractPattern next)
	{
		this.first = first;
		this.next = next;
		/*if (next!=null)
		{
			System.out.println(",");
		}*/
	}
	
	public ShapePropMatch match(boolean isPatternSide, ShapePropMatch previousMatchResult, ArrayList<Integer> argValues)
	{
		ShapePropMatch match = first.match(isPatternSide, previousMatchResult, argValues);
		//System.out.println("matching part of pl successfully");
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
