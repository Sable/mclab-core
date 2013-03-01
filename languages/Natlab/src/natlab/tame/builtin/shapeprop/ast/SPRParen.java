package natlab.tame.builtin.shapeprop.ast;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.*;

public class SPRParen<V extends Value<V>> extends SPAbstractMatchExpr<V>{
	SPAbstractPattern<V> p;
	public SPRParen (SPAbstractPattern<V> p){
		this.p = p;
		//System.out.println("()");
	}
	
	public ShapePropMatch<V> match(boolean isPatternSide, ShapePropMatch<V> previousMatchResult, Args<V> argValues, int num){
		previousMatchResult = p.match(isPatternSide, previousMatchResult, argValues, num);
		return previousMatchResult;
	}
	
	public String toString(){
		return "("+p.toString()+")";
	}
}
