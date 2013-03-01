package natlab.tame.builtin.shapeprop.ast;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.*;

public class SPCaselist<V extends Value<V>> extends SPNode<V>{
	static boolean Debug = false;
	SPCase<V> first;
	SPCaselist<V> next;
	
	public SPCaselist(SPCase<V> first, SPCaselist<V> next){
		this.first = first;
		this.next = next;
		/*if (next!=null)
		{
			System.out.println("||");
		}*/
	}
	
	public ShapePropMatch<V> match(boolean isPatternSide, ShapePropMatch<V> previousMatchResult, Args<V> argValues, int num){
		ShapePropMatch<V> match = first.match(isPatternSide, previousMatchResult, argValues, num);
		//first case match successfully!
		if((match.outputIsDone()==true)&&(match.getIsError()==false)) {
			if (Debug) System.out.println("matching and results emmitting successfully!\n");
			return match;
		}
		//first case matching part doesn't match, new a new one, start matching again.
		else if((match.getIsError()==true)&&(next!=null)){
			ShapePropMatch<V> newMatch = new ShapePropMatch<V>();
			newMatch = next.match(isPatternSide, newMatch, argValues, num);
			return newMatch;
		}
		//first case output parting doesn't match, new a new one, start matching again.
		else if((match.outputIsDone()==false)&&(next!=null)){
			return null;//FIXME
		}
		//only one case and doesn't match successfully.
		else
			return match;		
	}
	
	public String toString(){
		return first.toString()+(next==null?"":"||"+next);
	}
}
