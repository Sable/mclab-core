package natlab.tame.builtin.shapeprop.ast;

import java.util.HashMap;
import java.util.List;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.components.shape.*;
import natlab.tame.valueanalysis.value.*;

public class SPLowercase<V extends Value<V>> extends SPAbstractScalarExpr<V> {
	
	static boolean Debug = false;
	String s;
	
	public SPLowercase(String s) {
		this.s = s;
	}
	
	/**
	 * lowercase variables can appear in several cases: 
	 * 1. as a vertcat shape pattern, like [m,n];
	 * 2. on the lhs of an assignment statement by itself, like n=previousScalar();
	 * 3. on the lhs of an assignment statement as an index of an uppercase, like M(m)=n;
	 * 4. on the rhs of an assignment statement by itself, like M(2)=n.
	 * we have three boolean values or "flags" to help matching algorithm informed which 
	 * cases this lowercase is in, they are 
	 * 		boolean	isInsideVertcat, 
	 * 		boolean	isAssignLiteralToLHS and 
	 * 		boolean	whetherLatestMatchedIsNum. 
	 * isInsideVertcat can separate case 1 from other three cases, 
	 * isAssignLiteralToLHS can separate case 3,4 from case 2, and 
	 * whetherLatestMatchedIsNum can separate case 3 from case 4.
	 */
	public ShapePropMatch<V> match(boolean isPatternSide, ShapePropMatch<V> previousMatchResult, Args<V> argValues, int Nargout) {
		if (isPatternSide) {
			/* 
			 * for matching a vertcat expression, like [m,n] or [j,k,..l]. 
			 * if the first time encounter this lowercase, first, we try to match this lowercase with 
			 * corresponding argument's shape, and then record this lowercase;
			 * if not the first time encounter this lowercase, after matching this lowercase with 
			 * corresponding argument's shape, we have to also check whether the matching results 
			 * from two same lowercase matching are the same, like [l,k]*[k,n], k should match the same.
			 */
			if (previousMatchResult.getIsInsideVertcat()) {
				if (Debug) System.out.println("a lowercase inside vertcat!");
				Shape<V> argumentShape = ((HasShape<V>)argValues.get(previousMatchResult
						.getHowManyMatched())).getShape();
				int index = previousMatchResult.getNumInVertcat();
				if (argumentShape.getDimensions().size() <= index) {
					previousMatchResult.setIsError(true);
					System.err.println("shape propagation fails in vertcat matching.");
					return previousMatchResult;
				}
				/* 
				 * already encounter this lowercase before, checking whether the previous matched result 
				 * is equal to this time matched result.
				 */
				if (previousMatchResult.getAllLowercase().containsKey(s)) {
					if (previousMatchResult.getValueOfVariable(s).hasIntValue() 
							&& argumentShape.getDimensions().get(index).hasIntValue()) {
						if (!previousMatchResult.getValueOfVariable(s).getIntValue()
								.equals(argumentShape.getDimensions().get(index).getIntValue())) {
							System.err.println("two same lowercase's values are not equal" +
									", go to next pattern matching!");
							previousMatchResult.setIsError(true);
							return previousMatchResult;
						}
						previousMatchResult.setNumInVertcat(index+1);
						return previousMatchResult;						
					}
					System.err.println("shape propagation fails in vertcat matching.");
					previousMatchResult.setIsError(true);
					return previousMatchResult;
				}
				/*
				 * first time encounter this lowercase, matching shape then record the value of matching result.
				 */
				else {
					HashMap<String, DimValue> lowercase = new HashMap<String, DimValue>();
					if (argumentShape.getDimensions().get(index).hasIntValue()) {
						lowercase.put(s, new DimValue(argumentShape.getDimensions().get(index).getIntValue(), null));
						ShapePropMatch<V> matchResult = new ShapePropMatch<V>(previousMatchResult, lowercase, null);
						matchResult.setNumInVertcat(index+1);
						return matchResult;
					}
					else if (argumentShape.getDimensions().get(index).hasSymbolic()) {
						lowercase.put(s, new DimValue(null, argumentShape.getDimensions().get(index).getSymbolic()));
						ShapePropMatch<V> matchResult = new ShapePropMatch<V>(previousMatchResult, lowercase, null);
						matchResult.setNumInVertcat(index+1);
						return matchResult;
					}
					else {
						lowercase.put(s, new DimValue());
						ShapePropMatch<V> matchResult = new ShapePropMatch<V>(previousMatchResult, lowercase, null);
						matchResult.setNumInVertcat(index+1);
						return matchResult;
					}				
				}
			}
			/*
			 * for matching the pattern that assigning a lowercase to 
			 */
			else if (previousMatchResult.getIsAssignLiteralToLHS()) {
				if (Debug) System.out.println("trying to assign the value of "+s.toString()+" to an indexed array!");
				List<DimValue> dimensions = previousMatchResult.getShapeOfVariable(previousMatchResult
						.getLatestMatchedUppercase()).getDimensions();
				if (previousMatchResult.getWhetherLatestMatchedIsNum()) {
					if (Debug) System.out.println("inside assigning a lowercase's value to a num indexed array!"); // i.e. M(2)=n;
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
					dimensions.remove((previousMatchResult.getLatestMatchedNumber()-1));
					if (previousMatchResult.hasValue(s)) 
						dimensions.add((previousMatchResult.getLatestMatchedNumber()-1), previousMatchResult.getValueOfVariable(s));
					else
						dimensions.add((previousMatchResult.getLatestMatchedNumber()-1), new DimValue());
				}
				else {
					if (Debug) System.out.println("inside assigning a lowercase's value to array with lowercase index!");//i.e. M(n)=m;
					/* 
					 * array bound check for lhs, actually, we can also do this in array indexing node, 
					 * but it's okay to put array bound check here. 
					 */
					if (previousMatchResult.getValueOfVariable(previousMatchResult.getLatestMatchedLowercase()).hasIntValue()) {
						if (dimensions.size() < previousMatchResult.getValueOfVariable(previousMatchResult
								.getLatestMatchedLowercase()).getIntValue()) {
							System.err.println("index exceed the array bound, check your shape equation.");
							previousMatchResult.setIsError(true);
							previousMatchResult.setIsAssignLiteralToLHS(false);
							return previousMatchResult;
						}
						dimensions.remove(previousMatchResult.getValueOfVariable(previousMatchResult
								.getLatestMatchedLowercase()).getIntValue()-1);
						if (previousMatchResult.hasValue(s)) 
							dimensions.add(previousMatchResult.getValueOfVariable(previousMatchResult
									.getLatestMatchedLowercase()).getIntValue()-1, previousMatchResult.getValueOfVariable(s));
						else
							dimensions.add(previousMatchResult.getValueOfVariable(previousMatchResult
									.getLatestMatchedLowercase()).getIntValue()-1, new DimValue());
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
				uppercase.put(previousMatchResult.getLatestMatchedUppercase(), new ShapeFactory<V>().newShapeFromDimValues(dimensions));
				ShapePropMatch<V> matchResult = new ShapePropMatch<V>(previousMatchResult, null, uppercase);
				matchResult.setIsAssignLiteralToLHS(false);
				return matchResult;
			}
			/*
			 * the case that assigning anything to a lowercase, like n=previousScalar() or n=rhs.
			 */
			else {
				if (previousMatchResult.getAllLowercase().containsKey(s)) {
					if (Debug) System.out.println(s+" has already been stored in the hashmap!");
					previousMatchResult.saveLatestMatchedLowercase(s);
					return previousMatchResult;
				}
				else {
					HashMap<String, DimValue> lowercase = new HashMap<String, DimValue>();
					lowercase.put(s, new DimValue());
					ShapePropMatch<V> matchResult = new ShapePropMatch<V>(previousMatchResult, lowercase, null);
					matchResult.saveLatestMatchedLowercase(s);
					return matchResult;					
				}
			}
		}
		else {
			previousMatchResult.addToVertcatExpr(previousMatchResult.getValueOfVariable(s));
			return previousMatchResult;
		}
		
	}
	
	public String toString() {
		return s.toString();
	}
}
