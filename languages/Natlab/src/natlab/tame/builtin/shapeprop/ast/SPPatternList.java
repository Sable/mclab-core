package natlab.tame.builtin.shapeprop.ast;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.*;

public class SPPatternList<V extends Value<V>> extends SPAbstractPattern<V>{
	SPAbstractPattern<V> first;
	SPAbstractPattern<V> next;
	
	public SPPatternList (SPAbstractPattern<V> first, SPAbstractPattern<V> next){
		this.first = first;
		this.next = next;
		/*if (next!=null)
		{
			System.out.println(",");
		}*/
	}
	
	public ShapePropMatch<V> match(boolean isPatternSide, ShapePropMatch<V> previousMatchResult, Args<V> argValues, int num){
		ShapePropMatch<V> match = first.match(isPatternSide, previousMatchResult, argValues, num);
		if(match.getIsError()==true){
			return match;
		}
		//System.out.println("matching part of pl successfully");
		if(next!=null){
			ShapePropMatch<V> continueMatch = next.match(isPatternSide, match, argValues, num);
			return continueMatch;
		}
		else
			return match;
	}
	
	public String toString(){
		return first.toString()+(next==null?"":","+next);
	}
}
