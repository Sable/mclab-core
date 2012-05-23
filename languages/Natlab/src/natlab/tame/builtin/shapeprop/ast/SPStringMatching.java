package natlab.tame.builtin.shapeprop.ast;

import java.util.List;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Value;

public class SPStringMatching extends SPAbstractScalarExpr{

	String i;
	public SPStringMatching(String i){
		this.i = i;
	}
	
	public ShapePropMatch match(boolean isPatternSide, ShapePropMatch previousMatchResult, List<? extends Value<?>> argValues){
		return previousMatchResult;
	}
	
	public String toString(){
		return this.i;
	}
}
