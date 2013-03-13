package natlab.tame.builtin.shapeprop.ast;

import java.util.HashMap;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.components.shape.*;
import natlab.tame.valueanalysis.value.*;

/**
 * matching expression "ANY, #" is used to match a non-scalar, in another word, an array. 
 * it's different from the matching expression Uppercase, because the same Uppercase in 
 * shape equation should be equal, if not, there will mark as an error. while, "ANY, #" 
 * is just used for matching a shape and we don't care whether it's equal to the previous 
 * matched Uppercase.
 */
public class SPAny<V extends Value<V>> extends SPAbstractVectorExpr<V> {
	
	static boolean Debug = false;
	String s; // actually, this s is always "#".
	
	public SPAny(String a) {
		this.s = a;
	}
	
	public ShapePropMatch<V> match(boolean isPatternSide, ShapePropMatch<V> previousMatchResult, Args<V> argValues, int Nargout) {
		if (Debug) System.out.println("inside matching any shape variable!");
		if (isPatternSide) {
			if (argValues.get(previousMatchResult.getHowManyMatched())!=null) {
				Shape<V> argumentShape = ((HasShape<V>)argValues.get(previousMatchResult.getHowManyMatched())).getShape();
				HashMap<String, Shape<V>> uppercase = new HashMap<String, Shape<V>>();
				uppercase.put(s, argumentShape);
				// do we need to new a new ShapePropMatch?
				ShapePropMatch<V> match = new ShapePropMatch<V>(previousMatchResult, null, uppercase);
				match.comsumeArg();
				match.saveLatestMatchedUppercase(s);
				return match;
			}
			else {
				previousMatchResult.setIsError(true);
				return previousMatchResult;
			}
		}
		else {
			if (Debug) System.out.println("inside output uppercase " + s);
			if (previousMatchResult.getShapeOfVariable(s)==null) {
				// I forget why implement like this...TODO
				if (previousMatchResult.getOutputVertcatExpr().size()==1) {
					previousMatchResult.addToVertcatExpr(previousMatchResult.getOutputVertcatExpr().get(0));
				}
				previousMatchResult.copyVertcatToOutput();
				return previousMatchResult;
			}
			else {
				previousMatchResult.addToOutput(previousMatchResult.getShapeOfVariable(s));
				return previousMatchResult;
			}
		}
	}
	
	public String toString() {
		return s.toString();
	}
}
