package natlab.tame.builtin.shapeprop.ast;

import java.util.HashMap;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.components.constant.CharConstant;
import natlab.tame.valueanalysis.components.constant.Constant;
import natlab.tame.valueanalysis.components.constant.HasConstant;
import natlab.tame.valueanalysis.components.shape.HasShape;
import natlab.tame.valueanalysis.components.shape.Shape;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Value;

public class SPUppercase<V extends Value<V>> extends SPAbstractVectorExpr<V> {
	
	static boolean Debug = false;
	String s;
	
	public SPUppercase (String s) {
		this.s = s;
	}
	
	/**
	 * uppercase can appear at both pattern matching side and output emitting side, 
	 * on the pattern matching side, it can appear to match an array shape pattern 
	 * or appear in an indexed array expression, like M(2);
	 * on the output emitting side, it is just to represent an array shape.
	 */
	public ShapePropMatch<V> match(boolean isPatternSide, ShapePropMatch<V> previousMatchResult, Args<V> argValues, int Nargout) {
		if (isPatternSide) {
			if (previousMatchResult.getIsInsideAssign()) {
				previousMatchResult.saveLatestMatchedUppercase(s);
				return previousMatchResult;
			}
			else {
				/*
				 * similar to lowercase matching in vertcat, if this uppercase has been matched before, 
				 * we should do shape checking to make sure these two shape are equal.
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
					Shape argumentShape = ((HasShape)argValues.get(previousMatchResult.getHowManyMatched())).getShape();
					Constant argumentConstant =((HasConstant)argValues.get(previousMatchResult.getHowManyMatched())).getConstant();
					if (argumentConstant!=null && !(argumentConstant instanceof CharConstant)) {
						if (Debug) System.out.println("it's a constant!");
						previousMatchResult.setIsError(true);
						return previousMatchResult;
					}
					/*
					 * check whether or not current uppercase already in the previousMatchResult, 
					 * cases like (M,M->M), those M on the pattern matching side should be the same. if not, return error.
					 * 
					 * updated by Xu at 8:36pm Nov. 25th, 2012, actually, it's also okay if the shapes are same without trailing 1s.
					 */
					if (previousMatchResult.getAllUppercase().containsKey(s)) {
						Shape previousShape = previousMatchResult.getShapeOfVariable(previousMatchResult.getLatestMatchedUppercase());
						if (argumentShape.isConstant() && previousShape.isConstant() 
								&& !argumentShape.equals(previousShape) 
								&& !argumentShape.eliminateTrailingOnes().equals(previousShape.eliminateLeadingOnes())) {
							if (Debug) System.err.println("arguments don't have the same shape, go to next case.");
							previousMatchResult.setIsError(true);
							return previousMatchResult;
						}
						else if (argumentShape.isRowVector() && previousShape.isColVector()) {
							if (Debug) System.err.println("arguments don't have the same shape, go to next case.");
							previousMatchResult.setIsError(true);
							return previousMatchResult;
						}
						else if (argumentShape.isColVector() && previousShape.isRowVector()) {
							if (Debug) System.err.println("arguments don't have the same shape, go to next case.");
							previousMatchResult.setIsError(true);
							return previousMatchResult;
						}
						else if (argumentShape.maybeScalarOrVector() && !previousShape.maybeScalarOrVector()) {
							if (Debug) System.err.println("arguments don't have the same shape, go to next case.");
							previousMatchResult.setIsError(true);
							return previousMatchResult;
						}
						else if (!argumentShape.maybeScalarOrVector() && previousShape.maybeScalarOrVector()) {
							if (Debug) System.err.println("arguments don't have the same shape, go to next case.");
							previousMatchResult.setIsError(true);
							return previousMatchResult;
						}
						else {
							/*
							 * if new shape and old shape are equals, just consume one argument, 
							 * and then keep on matching.
							 */
							previousMatchResult.comsumeArg();
							previousMatchResult.saveLatestMatchedUppercase(s);
							return previousMatchResult;
						}
					}
					/*
					 * first time encounter this uppercase.
					 */
					else {
						HashMap<String, Shape> uppercase = new HashMap<String, Shape>();
						uppercase.put(s, argumentShape);
						ShapePropMatch<V> matchResult = new ShapePropMatch<V>(previousMatchResult, null, uppercase);
						matchResult.comsumeArg();
						matchResult.saveLatestMatchedUppercase(s);
						return matchResult;						
					}						
				}
			}
		}
		else {
			// uppercase not matched in the pattern matching side, try to find vertcat output or scalar output.
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
