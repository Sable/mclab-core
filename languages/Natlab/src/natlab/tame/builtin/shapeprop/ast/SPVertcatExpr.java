package natlab.tame.builtin.shapeprop.ast;

import java.util.ArrayList;
import java.util.List;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Value;

public class SPVertcatExpr extends SPAbstractVectorExpr
{
	SPVertExprArglist vl;
	public SPVertcatExpr(SPVertExprArglist vl)
	{
		this.vl = vl;
		//System.out.println("[]");
	}
	
	public ShapePropMatch match(boolean isPatternSide, ShapePropMatch previousMatchResult, ArrayList<Integer> argValues)
	{
		//System.out.println(isPatternSide);
		if(isPatternSide==true){
			return previousMatchResult; //TODO
		}
		else{
			ShapePropMatch match = vl.match(isPatternSide, previousMatchResult, argValues);
			System.err.println("the output shape is "+match.printVertcatExpr());
			return match;
		}
	}
	
	public String toString()
	{
		return "["+vl.toString()+"]";
	}
}
