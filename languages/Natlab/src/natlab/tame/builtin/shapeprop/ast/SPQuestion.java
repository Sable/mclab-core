package natlab.tame.builtin.shapeprop.ast;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.*;

public class SPQuestion<V extends Value<V>> extends SPAbstractMatchExpr<V>{
	SPAbstractMatchExpr<V> spm;
	public SPQuestion (SPAbstractMatchExpr<V> spm){
		this.spm = spm;
		//System.out.println("?");
	}
	
	public ShapePropMatch<V> match(boolean isPatternSide, ShapePropMatch<V> previousMatchResult, Args<V> argValues, int num){
		if(argValues.size()>previousMatchResult.getNumMatched()){
			//System.out.println("inside ? expression");
			ShapePropMatch<V> match = spm.match(isPatternSide, previousMatchResult, argValues, num);
			return match;
		}
		return previousMatchResult;
	}
	
	public String toString(){
		return spm.toString()+"?";
	}
}
