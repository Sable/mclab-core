package natlab.tame.builtin.shapeprop.ast;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Value;

public class SPCase<V extends Value<V>> extends SPNode<V> {
	
	static boolean Debug = false;
	SPPatternlist<V> patternlist;
	SPOutputlist<V> outputlist;
	
	public SPCase(SPPatternlist<V> p, SPOutputlist<V> o) {
		this.patternlist = p;
		this.outputlist = o;
	}
	
	public ShapePropMatch<V> match(boolean isPatternSide, ShapePropMatch<V> previousMatchResult, Args<V> argValues, int Nargout) {
		ShapePropMatch<V> patternMatch = patternlist.match(isPatternSide, previousMatchResult, argValues, Nargout);
		/*
		 * test whether there is any error when do shape matching.
		 */
		if (patternMatch.getIsError()) {
			isPatternSide = false;
			return patternMatch;
		}
		/*
		 * if there is no error when do shape matching.
		 */
		else {
			if (patternMatch.getHowManyMatched()==argValues.size()) {
				isPatternSide = false;
				if (Debug) System.out.println("matching part is done successfully!");
				ShapePropMatch<V> outputMatch = outputlist.match(isPatternSide, patternMatch, argValues, Nargout);
				// we may don't need argValues in output side.
				return outputMatch;
			}
			else {
				isPatternSide = false;
				patternMatch.setIsError(true);
				return patternMatch;
			}			
		}
	}
	
	public String toString(){
		return patternlist.toString() + "->" + outputlist.toString();
	}
}