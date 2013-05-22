package natlab.tame.builtin.shapeprop.ast;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.components.constant.*;
import natlab.tame.valueanalysis.components.shape.*;
import natlab.tame.valueanalysis.value.*;

public class SPStringLiteral<V extends Value<V>> extends SPAbstractMatchElement<V> {
	
	static boolean Debug = false;
	String s;
	
	public SPStringLiteral(String s) {
		this.s = s;
	}
	
	/**
	 * a string literal is used to match a constant string argument in a MATLAB built-in function.
	 */
	public ShapePropMatch<V> match(boolean isPatternSide, ShapePropMatch<V> previousMatchResult, Args<V> argValues, int Nargout) {
		// if the argument is empty, not matched.
		if (argValues.isEmpty()) {
			System.err.println("string literal argument matching fails.");
			previousMatchResult.setIsError(true);
			return previousMatchResult;
		}
		// if the argument is not empty, but the current index pointing to no argument, not matched.
		else if (argValues.size() <= previousMatchResult.getHowManyMatched()) {
			System.err.println("string literal argument matching fails.");
			previousMatchResult.setIsError(true);
			return previousMatchResult;
		}
		else {
			// we don't care about the shape of this argument, since this is mostly a CharConstant checking.
			Value<V> argument = argValues.get(previousMatchResult.getHowManyMatched());
			if (((HasConstant)argument).getConstant() instanceof CharConstant) {
				CharConstant charArg = (CharConstant) ((HasConstant)argument).getConstant();
				if (charArg.getValue().equals(s)) {
					if (Debug) System.out.println();
					previousMatchResult.comsumeArg();
					return previousMatchResult;
				}
			}
			System.err.println("string literal argument matching fails.");
			previousMatchResult.setIsError(true);
			return previousMatchResult;
		}
	}
	
	public String toString() {
		return "'" + s.toString() + "'";
	}
}
