package natlab.tame.builtin.shapeprop.ast;

import java.util.List;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Value;

public class SPArglist extends SPAbstractVertcatExprArg{
	SPAbstractVertcatExprArg first;
	SPAbstractVertcatExprArg next;
	
	public SPArglist(SPAbstractVertcatExprArg first, SPAbstractVertcatExprArg next){
		this.first = first;
		this.next = next;
		//System.out.println(",");
	}
	
	public ShapePropMatch match(boolean isPatternSide, ShapePropMatch previousMatchResult, List<? extends Value<?>> argValues, int num){
		previousMatchResult = first.match(isPatternSide, previousMatchResult, argValues, num);
		if(next!=null){
			ShapePropMatch continueMatch = next.match(isPatternSide, previousMatchResult, argValues, num);
			return continueMatch;
		}
		return previousMatchResult;
	}
	
	public String toString(){
		return first.toString()+(next==null?"":","+next);
	}
}
