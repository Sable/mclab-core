package natlab.tame.builtin.shapeprop.ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Value;

public class SPUpperIndex extends SPAbstractVertcatExprArg{
	String s;
	SPAbstractScalarExpr n;
	public SPUpperIndex(String s, SPAbstractScalarExpr n){
		this.s = s;
		this.n = n;
		//System.out.println(s+"()");
	}
	
	public ShapePropMatch match(boolean isPatternSide, ShapePropMatch previousMatchResult, List<? extends Value<?>> argValues, int num){
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
