package natlab.tame.builtin.shapeprop.ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Value;

public class SPOutput extends SPNode
{
	SPAbstractVectorExpr first;
	SPOutput next;
	
	public SPOutput(SPAbstractVectorExpr first, SPOutput next)
	{
		this.first = first;
		this.next = next;
		/*if (next!=null)
		{
			System.out.println(",");
		}*/
	}
	
	public SPOutput() {}
	
	public ShapePropMatch match(boolean isPatternSide, ShapePropMatch previousMatchResult, List<? extends Value<?>> argValues)
	{
		ShapePropMatch match = first.match(isPatternSide, previousMatchResult, argValues);//here, previousMatchResult would be the pattern part result, and that part match successfully.
		if (next == null) {
			System.out.println("output emitted done!");
			match.setOutputIsDone();
			return match;
		}
		return next.match(isPatternSide, previousMatchResult, argValues);
	}
	
	public String toString()
	{
		return first.toString()+(next==null?"":","+next);
	}
}
