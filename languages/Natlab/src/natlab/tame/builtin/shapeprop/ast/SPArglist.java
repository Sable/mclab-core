package natlab.tame.builtin.shapeprop.ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Value;

public class SPArglist extends SPAbstractVertcatExprArg{
	SPAbstractVertcatExprArg first;
	SPAbstractVertcatExprArg next;
	
	public SPArglist(SPAbstractVertcatExprArg first, SPAbstractVertcatExprArg next){
		this.first = first;
		this.next = next;
		//System.out.println(",");
	}
	
	public ShapePropMatch match(boolean isPatternSide, ShapePropMatch previousMatchResult, List<? extends Value<?>> argValues){
		previousMatchResult = first.match(isPatternSide, previousMatchResult, argValues);
		if(next!=null){
			ShapePropMatch continueMatch = next.match(isPatternSide, previousMatchResult, argValues);
			return continueMatch;
		}
		return previousMatchResult;
	}
	
	public String toString(){
		return first.toString()+(next==null?"":","+next);
	}
}
