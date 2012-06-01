package natlab.tame.builtin.shapeprop.ast;

import java.util.List;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Value;

public class SPCase extends SPNode{
	static boolean Debug = false;
	SPAbstractPattern first;
	SPOutput next;
	
	public SPCase(SPAbstractPattern p, SPOutput o){
		this.first = p;
		//System.out.println("->");
		this.next = o;
	}
	
	public ShapePropMatch match(boolean isPatternSide, ShapePropMatch previousMatchResult, List<? extends Value<?>> argValues, int num){
		ShapePropMatch match = first.match(isPatternSide, previousMatchResult, argValues, num);
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
			ShapePropMatch outputMatch = next.match(isPatternSide, match, argValues, num);
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
			ShapePropMatch outputMatch = next.match(isPatternSide, match, argValues, num); //I sense that maybe we don't need argValues in output
			return outputMatch;
		}
		else
			return null;
	}
	
	public String toString(){
		return first.toString()+"->"+next.toString();
	}
}