package natlab.tame.builtin.shapeprop.ast;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.*;

public class SPCase<V extends Value<V>> extends SPNode<V>{
	static boolean Debug = false;
	SPPatternlist<V> first;
	SPOutputlist<V> next;
	
	public SPCase(SPPatternlist<V> p, SPOutputlist<V> o){
		this.first = p;
		//System.out.println("->");
		this.next = o;
	}
	
	public ShapePropMatch<V> match(boolean isPatternSide, ShapePropMatch<V> previousMatchResult, Args<V> argValues, int num){
		ShapePropMatch<V> match = first.match(isPatternSide, previousMatchResult, argValues, num);
		if(match.getIsError()==true){
			isPatternSide = false;
			if(match.getNumMatched()!=argValues.size()){
				if (Debug) System.out.println("matching part is over, break out!");
				return match;
			}
			//there is no possibility that, isError is true and getNumMatched equals to argVaules.size...funny...
		}
		if((match.getNumMatched()==1)&&(argValues.isEmpty()==true)){  //for matching an empty argument list
			isPatternSide = false;
			if (Debug) System.out.println("matching an empty argument list is done!");
			ShapePropMatch<V> outputMatch = next.match(isPatternSide, match, argValues, num);
			return outputMatch;
		}
		if(match.getNumMatched()!=argValues.size()){ //match unsuccessful
			isPatternSide = false;
			match.setIsError();
			return match;
		}
		if(match.getNumMatched()==argValues.size()){  //if pattern part is done with successful matching
			isPatternSide = false;
			if (Debug) System.out.println("matching part is done!");
			ShapePropMatch<V> outputMatch = next.match(isPatternSide, match, argValues, num); //I sense that maybe we don't need argValues in output
			return outputMatch;
		}
		else
			return null;
	}
	
	public String toString(){
		return first.toString()+"->"+next.toString();
	}
}