package natlab.tame.builtin.shapeprop.ast;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.*;

public class SPVertExprArglist<V extends Value<V>> extends SPNode<V>{
	SPAbstractVertcatExprArg<V> first;
	SPVertExprArglist<V> next;
	public SPVertExprArglist (SPAbstractVertcatExprArg<V> first, SPVertExprArglist<V> next){
		this.first = first;
		this.next = next;
		//System.out.println(",");
	}
	
	public ShapePropMatch<V> match(boolean isPatternSide, ShapePropMatch<V> previousMatchResult, Args<V> argValues, int num){
		ShapePropMatch<V> match = first.match(isPatternSide, previousMatchResult, argValues, num);
		if(match.getIsError()==true){
			return match;			
		}
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
