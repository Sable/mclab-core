package natlab.tame.builtin.shapeprop.ast;

import java.util.ArrayList;
import java.util.List;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Value;

public class SPCaselist extends SPNode
{
	static boolean Debug = true;
	SPCase first;
	SPCaselist next;
	
	public SPCaselist(SPCase first, SPCaselist next)
	{
		this.first = first;
		this.next = next;
		/*if (next!=null)
		{
			System.out.println("||");
		}*/
	}
	
	public ShapePropMatch match(boolean isPatternSide, ShapePropMatch previousMatchResult, List<? extends Value<?>> argValues){
		ShapePropMatch match = first.match(isPatternSide, previousMatchResult, argValues);
		if (match.outputIsDone() == true) {
			if (Debug) System.out.println("matching and results emmitting successfully!\n");
			return match;
		}
		else
			return next.match(isPatternSide, previousMatchResult, argValues);		
	}
	
	public String toString()
	{
		return first.toString()+(next==null?"":"||"+next);
	}
}
