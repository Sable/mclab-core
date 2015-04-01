package natlab.tame.builtin.shapeprop.ast;

import java.util.HashMap;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.components.shape.HasShape;
import natlab.tame.valueanalysis.components.shape.Shape;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Value;

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
			if (previousMatchResult.getIsInsideAssign()) {
				previousMatchResult.saveLatestMatchedUppercase(s);
				return previousMatchResult;
			}
			else {
				/*
				 * don't need to check whether this # matched shape has the same shape as 
				 * the previous matched shape of #, because this # is used to match any.
				 */
				// if the argument is empty, not matched.
				if (argValues.isEmpty()) {
					previousMatchResult.setIsError(true);
					return previousMatchResult;
				}
				// if the argument is not empty, but the current index pointing to no argument, not matched.
				else if (argValues.size() <= previousMatchResult.getHowManyMatched()) {
					previousMatchResult.setIsError(true);
					return previousMatchResult;
				}
				else {
					Shape<V> argumentShape = ((HasShape<V>)argValues.get(previousMatchResult.getHowManyMatched())).getShape();
					HashMap<String, Shape<V>> uppercase = new HashMap<String, Shape<V>>();
					uppercase.put(s, argumentShape);
					// do we need to new a new ShapePropMatch?
					ShapePropMatch<V> matchResult = new ShapePropMatch<V>(previousMatchResult, null, uppercase);
					matchResult.comsumeArg();
					matchResult.saveLatestMatchedUppercase(s);
					return matchResult;
				}
			}
		}
		else {
			// # not matched in the pattern matching side, try to find vertcat output or scalar output.
			if (previousMatchResult.getShapeOfVariable(s)==null) {
				if (previousMatchResult.getOutputVertcatExpr().size()==0) {
					if (previousMatchResult.getLatestMatchedUppercase().equals("$")) {
						previousMatchResult.addToOutput(previousMatchResult.getShapeOfVariable("$"));
						previousMatchResult.emitOneResult();
						return previousMatchResult;
					}
					previousMatchResult.addToOutput(null);
					previousMatchResult.emitOneResult();
					return previousMatchResult;
				}
				else if (previousMatchResult.getOutputVertcatExpr().size()==1) {
					previousMatchResult.addToVertcatExpr(previousMatchResult.getOutputVertcatExpr().get(0));
					previousMatchResult.copyVertcatToOutput();
					previousMatchResult.emitOneResult();
					return previousMatchResult;
				}
				else {
					previousMatchResult.copyVertcatToOutput();
					previousMatchResult.emitOneResult();
					return previousMatchResult;
				}
			}
			/* 
			 * upeprcase matched in pattern matching side, 
			 * add the corresponding shape of this matched uppercase to output list.
			 */
			else {
				previousMatchResult.addToOutput(previousMatchResult.getShapeOfVariable(s));
				previousMatchResult.emitOneResult();
				return previousMatchResult;
			}
		}
	}
	
	public String toString() {
		return s.toString();
	}
}
