package natlab.tame.builtin.shapeprop.ast;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.*;

public class SPUpperIndex<V extends Value<V>> extends SPAbstractVertcatExprArg<V>{
	String s;
	SPAbstractScalarExpr<V> n;
	public SPUpperIndex(String s, SPAbstractScalarExpr<V> n){
		this.s = s;
		this.n = n;
		//System.out.println(s+"()");
	}
	
	public ShapePropMatch<V> match(boolean isPatternSide, ShapePropMatch<V> previousMatchResult, Args<V> argValues, int num){
		//for M(2), this node just want to get the value of M's second dimension size, if in an assignment, just modify the number of second dimension.
		//just save string s and mostly number n as the latest matched thing, and set inArrayIndex true
		previousMatchResult.saveLatestMatchedUppercase(s);
		previousMatchResult.setArrayIndexAssignLeft(true);
		previousMatchResult = n.match(isPatternSide, previousMatchResult, argValues, num);
		previousMatchResult.setArrayIndexAssignLeft(false);
		previousMatchResult.setArrayIndexAssignRight(true);
		return previousMatchResult;
	}
	
	public String toString(){
		return s.toString()+"("+n.toString()+")";
	}
}
