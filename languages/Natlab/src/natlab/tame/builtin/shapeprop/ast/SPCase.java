package natlab.tame.builtin.shapeprop.ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Value;

public class SPCase extends SPNode
{
	static boolean Debug = true;
	SPAbstractPattern first;
	SPOutput next;
	
	public SPCase(SPAbstractPattern p, SPOutput o)
	{
		this.first = p;
		//System.out.println("->");
		this.next = o;
	}
	
	public ShapePropMatch match(boolean isPatternSide, ShapePropMatch previousMatchResult, List<? extends Value<?>> argValues)
	{
		ShapePropMatch match = first.match(isPatternSide, previousMatchResult, argValues);
		if(match.getIsError()==true){//FIXME!!!
			isPatternSide = false;
			if (Debug) System.out.println("matching part is done!");
			ShapePropMatch outputMatch = next.match(isPatternSide, match, argValues); //I sense that maybe we don't need argValues in output
			return outputMatch;
		}
		if(match.getNumMatched()==argValues.size()){  //if pattern part is done with successful matching
			isPatternSide = false;
			if (Debug) System.out.println("matching part is done!");
			ShapePropMatch outputMatch = next.match(isPatternSide, match, argValues); //I sense that maybe we don't need argValues in output
			return outputMatch;
		}
		if((match.getNumMatched()==1)&&(argValues.isEmpty()==true)){  //for matching an empty argument list
			isPatternSide = false;
			if (Debug) System.out.println("matching an empty argument list is done!");
			ShapePropMatch outputMatch = next.match(isPatternSide, match, argValues);
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