package natlab.tame.builtin.shapeprop.ast;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.*;

public class SPOr<V extends Value<V>> extends SPAbstractMatchExpr<V>{
	SPAbstractMatchExpr<V> first;
	SPAbstractMatchExpr<V> next;
	
	public SPOr (SPAbstractMatchExpr<V> first,SPAbstractMatchExpr<V> next){
		this.first = first;
		this.next = next;
		//System.out.println("|");
	}
	
	public ShapePropMatch<V> match(boolean isPatternSide, ShapePropMatch<V> previousMatchResult, Args<V> argValues, int num){
		int indexBeforeOr = previousMatchResult.getHowManyMatched();
		ShapePropMatch<V> match = first.match(isPatternSide, previousMatchResult, argValues, num);
		int indexAfterOr = match.getHowManyMatched();
		if(indexBeforeOr==indexAfterOr){
			if(match.getIsError()){
				match.setIsError(false);
			}
			ShapePropMatch<V> continueMatch = next.match(isPatternSide, match, argValues, num);//actually, here, match is the same to previousMatchResult
			return continueMatch;
		}
		return match;
	}
	
	public String toString(){
		return first.toString()+"|"+next.toString();
	}
}
