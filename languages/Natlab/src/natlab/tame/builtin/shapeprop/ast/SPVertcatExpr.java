package natlab.tame.builtin.shapeprop.ast;

import java.util.ArrayList;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.components.shape.*;
import natlab.tame.valueanalysis.value.*;

public class SPVertcatExpr<V extends Value<V>> extends SPAbstractVectorExpr<V> {
	
	static boolean Debug = false;
	SPArglist<V> vl;
	
	/**
	 * the constructor for an empty shape, i.e. [].
	 */
	public SPVertcatExpr() {}
	
	/**
	 * the constructor for an non-empty shape.
	 * @param vl
	 */
	public SPVertcatExpr(SPArglist<V> vl) {
		this.vl = vl;
	}
	
	public ShapePropMatch<V> match(boolean isPatternSide, ShapePropMatch<V> previousMatchResult, Args<V> argValues, int Nargout) {
		if (isPatternSide) {
			if (Debug) System.out.println("just get into SPVertcatExpr, setIsInsideVertcat is true!");
			previousMatchResult.setIsInsideVertcat(true);
			previousMatchResult.setNumInVertcat(0);//reset it
			/*
			 * test whether it is an empty shape.
			 */
			if (!previousMatchResult.getIsInsideAssign() && vl==null) {
				if (argValues.size()==0) {
					// matching empty shape successfully.
					previousMatchResult.setIsMatchDone();
					return previousMatchResult;
				}
				previousMatchResult.setIsError(true);
				return previousMatchResult;
			}
			else if (previousMatchResult.getIsInsideAssign() && vl==null) {
				// TODO this is used for generating an empty shape, and later to store shape info propagation result.
				return previousMatchResult;
			}
			else {
				ShapePropMatch<V> match = vl.match(isPatternSide, previousMatchResult, argValues, Nargout);
				match.setIsInsideVertcat(false);
				match.comsumeArg();
				return match;				
			}
		}
		else {
			// a vertcat in output side, it should return a shape.
			if (Debug) System.out.println("inside output vertcat expression!");
			if (vl==null) {
				return previousMatchResult;
			}
			String[] arg = vl.toString().split(",");
			if (arg[0].equals("1")) {
				ArrayList<DimValue> al = new ArrayList<DimValue>(2);
				al.add(new DimValue(1, null));
				if (previousMatchResult.hasValue(arg[1])) {
					al.add(previousMatchResult.getValueOfVariable(arg[1]));
				}
				else {
					al.add(new DimValue());
				}
				Shape<V> shape = new ShapeFactory<V>().newShapeFromDimValues(al);
				previousMatchResult.addToOutput(shape);
				previousMatchResult.emitOneResult();
				return previousMatchResult;
			}
			else if (arg[1].equals("1")) {
				ArrayList<DimValue> al = new ArrayList<DimValue>(2);
				if (previousMatchResult.hasValue(arg[0])) {
					al.add(previousMatchResult.getValueOfVariable(arg[0]));
				}
				else {
					al.add(new DimValue());
				}
				al.add(new DimValue(1, null));
				Shape<V> shape = new ShapeFactory<V>().newShapeFromDimValues(al);
				previousMatchResult.addToOutput(shape);
				previousMatchResult.emitOneResult();
				return previousMatchResult;
			}
			else {
				//FIXME deal with the [m,k] or [m,k,j,..] kinds of output
				ArrayList<DimValue> al = new ArrayList<DimValue>(arg.length);
				for (String i : arg) {
					if (previousMatchResult.hasValue(i)) {
						al.add(previousMatchResult.getValueOfVariable(i));
					}
					else {
						al.add(new DimValue());
					}
				}
				if (Debug) System.out.println("the vertcat result is " + al);
				Shape<V> shape = new ShapeFactory<V>().newShapeFromDimValues(al);
				previousMatchResult.addToOutput(shape);
				previousMatchResult.emitOneResult();
				return previousMatchResult;	
			}
		}
	}
	
	public String toString() {
		if (vl==null) return "[]";
		return "[" + vl.toString() + "]";
	}
}
