package natlab.tame.builtin.shapeprop.ast;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.*;

public class SPCase<V extends Value<V>> extends SPNode<V> {
	
	static boolean Debug = false;
	SPPatternlist<V> first;
	SPOutputlist<V> next;
	
	public SPCase(SPPatternlist<V> p, SPOutputlist<V> o) {
		this.first = p;
		this.next = o;
	}
	
	public ShapePropMatch<V> match(boolean isPatternSide, ShapePropMatch<V> previousMatchResult, Args<V> argValues, int Nargout) {
		ShapePropMatch<V> matchResult = first.match(isPatternSide, previousMatchResult, argValues, Nargout);
		/*
		 * testing whether there is error when do shape matching.
		 */
		if (matchResult.getIsError()==true) {
			isPatternSide = false;
			return matchResult;
		}
		/*
		 * if there is no error when do shape matching.
		 */
		else {
			if (matchResult.getHowManyMatched()==argValues.size()) {
				isPatternSide = false;
				if (Debug) System.out.println("matching part is done successfully!");
				ShapePropMatch<V> matchOutput = next.match(isPatternSide, matchResult, argValues, Nargout);
				// we may don't need argValues in output side.
				return matchOutput;
			}
			else {
				isPatternSide = false;
				matchResult.setIsError(true);
				return matchResult;
			}			
		}
	}
	
	public String toString(){
		return first.toString() + "->" + next.toString();
	}
}