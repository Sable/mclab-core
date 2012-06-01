package natlab.tame.builtin.shapeprop.ast;

import java.util.List;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Value;

public class SPRParen extends SPAbstractMatchExpr{
	SPAbstractPattern p;
	public SPRParen (SPAbstractPattern p){
		this.p = p;
		//System.out.println("()");
	}
	
	public ShapePropMatch match(boolean isPatternSide, ShapePropMatch previousMatchResult, List<? extends Value<?>> argValues, int num){
		previousMatchResult = p.match(isPatternSide, previousMatchResult, argValues, num);
		return previousMatchResult;
	}
	
	public String toString(){
		return "("+p.toString()+")";
	}
}
