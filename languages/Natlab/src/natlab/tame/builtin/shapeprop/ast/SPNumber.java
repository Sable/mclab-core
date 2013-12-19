package natlab.tame.builtin.shapeprop.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.components.shape.*;
import natlab.tame.valueanalysis.value.*;

public class SPNumber<V extends Value<V>> extends SPAbstractScalarExpr<V> {
	
	static boolean Debug = false;
	Number n;
	
	public SPNumber (Number n) {
		this.n = n;
	}
	
	/**
	 * number can appear in both pattern matching side and output emitting side:
	 * 1. in the pattern matching side, numbers can appear at the lhs of assignment, 
	 *    the array index number at the rhs of assignment or in a vertcat expression, 
	 *    like [1,k];
	 * 2. in the output emitting side, numbers can appear in a vertcat expression, 
	 *    like [1,k], too.
	 */
	public ShapePropMatch<V> match(boolean isPatternSide, ShapePropMatch<V> previousMatchResult, Args<V> argValues, int Nargout) {
		if (isPatternSide) {
			if (previousMatchResult.getIsAssignLiteralToLHS()) {
				if (Debug) System.out.println("trying to assign "+n.toString()+" to an indexed array!");
				// when modify shapes, make sure not modifying the original one.
				List<DimValue> dimensions = new ArrayList<DimValue>(
						previousMatchResult.getShapeOfVariable(previousMatchResult
								.getLatestMatchedUppercase()).getDimensions());
				if (previousMatchResult.getWhetherLatestMatchedIsNum()) {
					if (Debug) System.out.println("inside assigning a number to a number indexed array!"); // i.e. M(2)=2;
					/* 
					 * array bound check for lhs, actually, we can also do this in array indexing node, 
					 * but it's okay to put array bound check here. 
					 */
					if (dimensions.size() < previousMatchResult.getLatestMatchedNumber()) {
						System.err.println("index exceed the array bound, check your shape equation.");
						previousMatchResult.setIsError(true);
						previousMatchResult.setIsAssignLiteralToLHS(false);
						return previousMatchResult;
					}
					dimensions.remove(previousMatchResult.getLatestMatchedNumber()-1);
					dimensions.add(previousMatchResult.getLatestMatchedNumber()-1, new DimValue(n.intValue(), null));
				}
				else {
					if (Debug) System.out.println("inside assigning a number to a lowercase indexed array!");//i.e. M(n)=2;
					/* 
					 * array bound check for lhs, actually, we can also do this in array indexing node, 
					 * but it's okay to put array bound check here. 
					 */
					if (previousMatchResult.getValueOfVariable(previousMatchResult.getLatestMatchedLowercase()).hasIntValue()) {
						if (dimensions.size() < previousMatchResult.getValueOfVariable(previousMatchResult
								.getLatestMatchedLowercase()).getIntValue()) {
							if (Debug) System.out.println("index exceed the array bound, check you shape eqation.");
							previousMatchResult.setIsError(true);
							previousMatchResult.setIsAssignLiteralToLHS(false);
							return previousMatchResult;
						}
						dimensions.remove(previousMatchResult.getValueOfVariable(previousMatchResult
								.getLatestMatchedLowercase()).getIntValue()-1);
						dimensions.add(previousMatchResult.getValueOfVariable(previousMatchResult
								.getLatestMatchedLowercase()).getIntValue()-1, new DimValue(n.intValue(), null));
					}
					else {
						System.err.println("the index of uppercase shape dimension cannot be determined.");
						previousMatchResult.setIsError(true);
						return previousMatchResult;
					}
				}
				/*
				 * both if and else branch, if matching successfully, will generate a new dimensions.
				 */
				if (Debug) System.out.println("new shape of "+previousMatchResult.getLatestMatchedUppercase() 
						+ " is " + previousMatchResult.getShapeOfVariable(previousMatchResult.getLatestMatchedUppercase()));
				HashMap<String, Shape<V>> uppercase = new HashMap<String, Shape<V>>();
				uppercase.put(previousMatchResult.getLatestMatchedUppercase(),(new ShapeFactory<V>()).newShapeFromDimValues(dimensions));
				ShapePropMatch<V> matchResult = new ShapePropMatch<V>(previousMatchResult, null, uppercase);
				matchResult.setIsAssignLiteralToLHS(false);
				return matchResult;
			}
			/*
			 * when this number is in a vertcat pattern on the pattern matching side, like [1,k] or [j,2,...l], 
			 * we should check whether the pointed dimension of current pointed argument has the exact same size.
			 */
			else if (previousMatchResult.getIsInsideVertcat()) {
				if (Debug) System.out.println("inside matching a number in a vertcat pattern!");
				Shape<V> shapeOfCurrentArg = ((HasShape<V>)argValues.get(previousMatchResult.getHowManyMatched())).getShape();
				if (shapeOfCurrentArg!=null) {
					List<DimValue> dimensions = shapeOfCurrentArg.getDimensions();
					if (!dimensions.get(previousMatchResult.getNumInVertcat()).hasIntValue()) {
						System.err.println("cannot determine whether the size of " + previousMatchResult
								.getNumInVertcat() + " dimension equals to " + n.intValue());
						previousMatchResult.setIsError(true);
						return previousMatchResult;
					}
					if (dimensions.get(previousMatchResult.getNumInVertcat()).getIntValue() != n.intValue()) {
						System.err.println("the size of " + previousMatchResult.getNumInVertcat() 
								+ "dimension doesn't equal to " + n.intValue());
						previousMatchResult.setIsError(true);
						return previousMatchResult;
					}
					else {
						if (Debug) System.out.println("shape matching in vertcat for number " + n.intValue() + " is successful.");
						previousMatchResult.saveLatestMatchedNumber(n.intValue());
						previousMatchResult.setWhetherLatestMatchedIsNum(true);
						return previousMatchResult;
					}
				}
				System.err.println("shape info of " + argValues.get(previousMatchResult.getHowManyMatched()) 
						+ " is null, shape propagation fails.");
				previousMatchResult.setIsError(true);
				return previousMatchResult;
			}
			/*
			 * for the case, the number is as an index of a vector shape
			 * , just record it's value and mark it as latest matched number.
			 */
			else {
				previousMatchResult.saveLatestMatchedNumber(n.intValue());
				previousMatchResult.setWhetherLatestMatchedIsNum(true);
				return previousMatchResult;
			}
		}
		/*
		 * for the number in the output emitting side, it will only appear in a vertcat, like [1,k]. 
		 * but currently, I have already handled this in the SPVertcatExpr node, what should I do?
		 * btw, unless SPVertcatExpr won't call match method for its arglist, the program can never 
		 * come here, so it's safe currently, even we return null here.
		 */
		else {
			return null;
		}
		
	}
	
	public String toString() {
		return n.toString();
	}
}
