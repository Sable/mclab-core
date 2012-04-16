package natlab.tame.builtin.shapeprop.ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Value;

public class SPCase extends SPNode
{
	SPAbstractPattern first;
	SPOutput next;
	
	public SPCase(SPAbstractPattern p, SPOutput o)
	{
		this.first = p;
		//System.out.println("->");
		this.next = o;
	}
	
	public ShapePropMatch match(boolean isPatternSide, ShapePropMatch previousMatchResult, ArrayList<Integer> argValues)
	{
		//System.out.println("inside SPCase");
		ShapePropMatch match = first.match(isPatternSide, previousMatchResult, argValues);
		//System.out.println(match.getLatestMatchedLowerCase());
		//System.out.println(match.getNumMatched());
		if (match.getNumMatched()==argValues.size()){  //if pattern part is done with successful matching
			isPatternSide = false;
			System.out.println("matching part is done!");
			ShapePropMatch outputMatch = next.match(isPatternSide, match, argValues); //I sense that maybe we don't need argValues in output
			return outputMatch;
		}
		else
			return null;
	}
	
	public String toString()
	{
		return first.toString()+"->"+next.toString();
	}
}