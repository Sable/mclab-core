package natlab.tame.builtin.shapeprop.ast;

import java.util.HashMap;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.*;
import natlab.tame.valueanalysis.components.constant.*;
import natlab.tame.valueanalysis.components.shape.*;

public class SPScalar<V extends Value<V>> extends SPAbstractVectorExpr<V> {
	
	static boolean Debug = false;
	String s;
	
	public SPScalar (String s) {
		this.s = s;
	}
	
	/**
	 * only used to match a scalar input argument, so cannot be indexed as uppercase.
	 */
	public ShapePropMatch<V> match(boolean isPatternSide, ShapePropMatch<V> previousMatchResult, Args<V> argValues, int Nargout) {
		if (isPatternSide) {
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
			// if the argument is not empty, and the current index pointing to an argument, try to match.
			else {
				Value<V> argument = argValues.get(previousMatchResult.getHowManyMatched());
				if (((HasConstant)argument).getConstant()==null && ((HasShape<V>)argument).getShape()==null) {
					HashMap<String, DimValue> lowercase = new HashMap<String, DimValue>();
					lowercase.put(s, new DimValue());
					HashMap<String, Shape<V>> uppercase = new HashMap<String, Shape<V>>();
					uppercase.put(s, (new ShapeFactory<V>()).newShapeFromIntegers((new DoubleConstant(1).getShape())));
					ShapePropMatch<V> match = new ShapePropMatch<V>(previousMatchResult, lowercase, uppercase);
					match.comsumeArg();
					match.saveLatestMatchedUppercase(s);
					if (Debug) System.out.println(match.getAllLowercase());
					if (Debug) System.out.println("inside empty constant value and shape value mathcing a Scalar!");
					return match;
				}
				// doesn't have constant info, but has shape info, so it may be a scalar if the shape is [1,1].
				else if (((HasConstant)argument).getConstant()==null && ((HasShape<V>)argument).getShape()!=null) {
					if (((HasShape<V>)argument).getShape().isScalar()) {
						// it's a scalar, but doesn't have a constant value, in this case, we can take advantage of its symbolic value.
						String symbolic = argument.getSymbolic();
						HashMap<String, DimValue> lowercase = new HashMap<String, DimValue>();
						lowercase.put(s, new DimValue(null, symbolic));
						HashMap<String, Shape<V>> uppercase = new HashMap<String, Shape<V>>();
						uppercase.put(s, ((HasShape<V>)argument).getShape());
						ShapePropMatch<V> match = new ShapePropMatch<V>(previousMatchResult, lowercase, uppercase);
						match.comsumeArg();
						match.saveLatestMatchedUppercase(s);
						return match;
					}
					else {
						if (Debug) System.out.println("it's not a scalar");
						previousMatchResult.setIsError(true);
						return previousMatchResult;						
					}
				}
				else if (((HasConstant)argument).getConstant() instanceof DoubleConstant) {
					double doubleValueArgument = ((DoubleConstant)((HasConstant)argument).getConstant()).getValue();
					int intValueArgument = (int) doubleValueArgument;
					if (Debug) System.out.println("argument has constant int value "+intValueArgument);
					String symbolic = argument.getSymbolic();
					HashMap<String, DimValue> lowercase = new HashMap<String, DimValue>();
					lowercase.put(s, new DimValue(intValueArgument, symbolic));
					HashMap<String, Shape<V>> uppercase = new HashMap<String, Shape<V>>();
					uppercase.put(s, ((HasShape<V>)argument).getShape());
					ShapePropMatch<V> match = new ShapePropMatch<V>(previousMatchResult, lowercase, uppercase);
					match.comsumeArg();
					match.saveLatestMatchedUppercase(s);
					return match;
				}
				else {
					previousMatchResult.setIsError(true);
					return previousMatchResult;
				}				
			}
		}
		else {
			previousMatchResult.addToOutput(new ShapeFactory<V>().getScalarShape());
			previousMatchResult.emitOneResult();
			return previousMatchResult;
		}
	}
	
	public String toString(){
		return s.toString();
	}
}
