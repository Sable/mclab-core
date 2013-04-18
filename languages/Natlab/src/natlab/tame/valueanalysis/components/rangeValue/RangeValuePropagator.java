package natlab.tame.valueanalysis.components.rangeValue;

import java.util.ArrayList;
import java.lang.Math;

import natlab.tame.builtin.Builtin;
import natlab.tame.builtin.BuiltinVisitor;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Value;
import natlab.tame.valueanalysis.components.constant.*;
import natlab.tame.valueanalysis.components.shape.*;

public class RangeValuePropagator<V extends Value<V>> 
extends BuiltinVisitor<Args<V>, RangeValue<V>> {
	
	static boolean Debug = false;
	@SuppressWarnings("rawtypes")
	static RangeValuePropagator instance = null;
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	static public <V extends Value<V>> RangeValuePropagator<V> getInstance() {
		if (instance == null) instance = new RangeValuePropagator();
		return instance;
	}
	
	//hidden private constructor
	private RangeValuePropagator() {}
	
	@Override
	/**
	 * if the range value analysis cannot infer from some built-ins, 
	 * return null, not top. currently, top is returned when merging 
	 * range value of the variables which have dependency in loops, 
	 * any other cases? one more thing, always remember to take care 
	 * of null value!
	 */
	public RangeValue<V> caseBuiltin(Builtin builtin, Args<V> arg) {
		return null;
	}
	
	@Override
	/**
	 * unary plus
	 */
	public RangeValue<V> caseUplus(Builtin builtin, Args<V> arg) {
		if (arg.size()!=1) return null;
		if (((HasRangeValue<V>)arg.get(0)).getRangeValue()==null) 
			return null;
		Double lowerBound = ((HasRangeValue<V>)arg.get(0)).getRangeValue().getLowerBound();
		Double upperBound = ((HasRangeValue<V>)arg.get(0)).getRangeValue().getUpperBound();
		return new RangeValue<V>(lowerBound, upperBound);
	}
	
	@Override
	/**
	 * binary plus.
	 */
	public RangeValue<V> casePlus(Builtin builtin, Args<V> arg) {
		if (arg.size()!=2) return null;
		if (((HasRangeValue<V>)arg.get(0)).getRangeValue()==null 
				|| ((HasRangeValue<V>)arg.get(1)).getRangeValue()==null) 
			return null;
		if (arg.hasDependency()) {
			/*
			 * if two args are both positive, the lowerbound can be determined, 
			 * but the upperbound will be +inf;
			 * it's not that easy to determine the lower and upper bound if one 
			 * of them is negative, this depends on which one varies in each 
			 * iteration. so, currently, we push the result to two side top, 
			 * <-inf,+inf>.
			 * TODO, make it more precise.
			 */
			if (arg.getDependentVars().contains(arg.get(0).getSymbolic()) 
					|| arg.getDependentVars().contains(arg.get(1).getSymbolic())) {
				RangeValue<V> topRange = new RangeValue<V>();
				if (((HasRangeValue<V>)arg.get(0)).getRangeValue().isRangeValuePositive() 
						&& ((HasRangeValue<V>)arg.get(1)).getRangeValue().isRangeValuePositive()) {
					topRange.flagUpperBoundIsTop();
					Double lowerBound = ((HasRangeValue<V>)arg.get(0)).getRangeValue().getLowerBound()
							+((HasRangeValue<V>)arg.get(1)).getRangeValue().getLowerBound();
					topRange.setLowerBound(lowerBound);
					//topRange.flagLowerBoundIsTop();
					return topRange;
				}
				topRange.flagLowerBoundIsTop();
				topRange.flagUpperBoundIsTop();
				return topRange;
			}
		}
		if (((HasRangeValue<V>)arg.get(0)).getRangeValue().hasTop()
				|| ((HasRangeValue<V>)arg.get(1)).getRangeValue().hasTop()) {
			RangeValue<V> topRange = new RangeValue<V>().mergeTop(
					((HasRangeValue<V>)arg.get(0)).getRangeValue()
					, ((HasRangeValue<V>)arg.get(1)).getRangeValue());
			return topRange;
		}
		Double lowerBound = ((HasRangeValue<V>)arg.get(0)).getRangeValue().getLowerBound()
				+((HasRangeValue<V>)arg.get(1)).getRangeValue().getLowerBound();
		Double upperBound = ((HasRangeValue<V>)arg.get(0)).getRangeValue().getUpperBound()
				+((HasRangeValue<V>)arg.get(1)).getRangeValue().getUpperBound();
		return new RangeValue<V>(lowerBound, upperBound);
	}
	
	@Override
	/**
	 * unary minus.
	 */
	public RangeValue<V> caseUminus(Builtin builtin, Args<V> arg) {
		if (arg.size()!=1) return null;
		if (((HasRangeValue<V>)arg.get(0)).getRangeValue()==null) 
			return null;
		Double lowerBound = -((HasRangeValue<V>)arg.get(0)).getRangeValue().getUpperBound();
		Double upperBound = -((HasRangeValue<V>)arg.get(0)).getRangeValue().getLowerBound();
		return new RangeValue<V>(lowerBound, upperBound);
	}
	
	@Override
	/**
	 * binary minus.
	 */
	public RangeValue<V> caseMinus(Builtin builtin, Args<V> arg) {
		if (Debug) System.out.println("inside rangeValue analysis for binary " +
				"minus with dependent vars, "+arg.getDependentVars());
		if (arg.size()!=2) return null;
		if (((HasRangeValue<V>)arg.get(0)).getRangeValue()==null 
				|| ((HasRangeValue<V>)arg.get(1)).getRangeValue()==null) 
			return null;
		if (arg.hasDependency()) {
			/*
			 * binary minus is also not that easy, since it also need the info 
			 * which operand or arg varies on each iteration, so, currently, 
			 * push the result to two side top, <-inf,+inf>.
			 * TODO, make it more precise.
			 */
			if (arg.getDependentVars().contains(arg.get(0).getSymbolic()) 
					|| arg.getDependentVars().contains(arg.get(1).getSymbolic())) {
				RangeValue<V> topRange = new RangeValue<V>();
				topRange.flagLowerBoundIsTop();
				topRange.flagUpperBoundIsTop();
				return topRange;					
			}
		}
		if (((HasRangeValue<V>)arg.get(0)).getRangeValue().hasTop() 
				|| ((HasRangeValue<V>)arg.get(1)).getRangeValue().hasTop()) {
			RangeValue<V> topRange = new RangeValue<V>().mergeTop(
					((HasRangeValue<V>)arg.get(0)).getRangeValue()
					, ((HasRangeValue<V>)arg.get(1)).getRangeValue());
			return topRange;
		}
		Double lowerBound = ((HasRangeValue<V>)arg.get(0)).getRangeValue().getLowerBound()
				-((HasRangeValue<V>)arg.get(1)).getRangeValue().getUpperBound();
		Double upperBound = ((HasRangeValue<V>)arg.get(0)).getRangeValue().getUpperBound()
				-((HasRangeValue<V>)arg.get(1)).getRangeValue().getLowerBound();
		return new RangeValue<V>(lowerBound, upperBound);
	}
	
	@Override
	/**
	 * element-by-element multiplication.
	 */
	public RangeValue<V> caseTimes(Builtin builtin, Args<V> arg) {
		if (arg.size()!=2) return null;
		if (((HasRangeValue<V>)arg.get(0)).getRangeValue()==null 
				|| ((HasRangeValue<V>)arg.get(1)).getRangeValue()==null) 
			return null;
		if (arg.hasDependency()) {
			if (arg.getDependentVars().contains(arg.get(0).getSymbolic()) 
					|| arg.getDependentVars().contains(arg.get(1).getSymbolic())) {
				RangeValue<V> topRange = new RangeValue<V>();
				topRange.flagLowerBoundIsTop();
				topRange.flagUpperBoundIsTop();
				return topRange;
			}
		}
		if (((HasRangeValue<V>)arg.get(0)).getRangeValue().hasTop() 
				|| ((HasRangeValue<V>)arg.get(1)).getRangeValue().hasTop()) {
			RangeValue<V> topRange = new RangeValue<V>().mergeTop(
					((HasRangeValue<V>)arg.get(0)).getRangeValue()
					, ((HasRangeValue<V>)arg.get(1)).getRangeValue());
			return topRange;
		}
		Double result1 = ((HasRangeValue<V>)arg.get(0)).getRangeValue().getLowerBound()
				*((HasRangeValue<V>)arg.get(1)).getRangeValue().getLowerBound();
		Double result2 = ((HasRangeValue<V>)arg.get(0)).getRangeValue().getLowerBound()
				*((HasRangeValue<V>)arg.get(1)).getRangeValue().getUpperBound();
		Double result3 = ((HasRangeValue<V>)arg.get(0)).getRangeValue().getUpperBound()
				*((HasRangeValue<V>)arg.get(1)).getRangeValue().getLowerBound();
		Double result4 = ((HasRangeValue<V>)arg.get(0)).getRangeValue().getUpperBound()
				*((HasRangeValue<V>)arg.get(1)).getRangeValue().getUpperBound();
		return new RangeValue<V>(getMinimum(result1, result2, result3, result4)
				, getMaximum(result1, result2, result3, result4));
	}
	
	@Override
	/**
	 * matrix multiplication, when the arguments are both scalars, 
	 * it works the same as element-by-element multiplication.
	 */
	public RangeValue<V> caseMtimes(Builtin builtin, Args<V> arg) {
		if (arg.size()!=2) return null;
		if (((HasShape<V>)arg.get(0)).getShape()==null 
				|| ((HasShape<V>)arg.get(1)).getShape()==null) return null;
		if (((HasShape<V>)arg.get(0)).getShape().isScalar() 
				&& ((HasShape<V>)arg.get(1)).getShape().isScalar()) {
			return caseTimes(builtin, arg);
		}
		return null;
	}
	
	@Override
	/**
	 * element-by-element rdivision.
	 */
	public RangeValue<V> caseRdivide(Builtin builtin, Args<V> arg) {
		if (arg.size()!=2) return null;
		if (((HasRangeValue<V>)arg.get(0)).getRangeValue()==null 
				|| ((HasRangeValue<V>)arg.get(1)).getRangeValue()==null) 
			return null;
		if (arg.hasDependency()) {
			if (arg.getDependentVars().contains(arg.get(0).getSymbolic()) 
					|| arg.getDependentVars().contains(arg.get(1).getSymbolic())) {
				RangeValue<V> topRange = new RangeValue<V>();
				topRange.flagLowerBoundIsTop();
				topRange.flagUpperBoundIsTop();
				return topRange;					
			}
		}
		if (((HasRangeValue<V>)arg.get(0)).getRangeValue().hasTop() 
				|| ((HasRangeValue<V>)arg.get(1)).getRangeValue().hasTop()) {
			RangeValue<V> topRange = new RangeValue<V>().mergeTop(
					((HasRangeValue<V>)arg.get(0)).getRangeValue()
					, ((HasRangeValue<V>)arg.get(1)).getRangeValue());
			return topRange;
		}
		Double result1 = ((HasRangeValue<V>)arg.get(0)).getRangeValue().getLowerBound()
				/((HasRangeValue<V>)arg.get(1)).getRangeValue().getLowerBound();
		Double result2 = ((HasRangeValue<V>)arg.get(0)).getRangeValue().getLowerBound()
				/((HasRangeValue<V>)arg.get(1)).getRangeValue().getUpperBound();
		Double result3 = ((HasRangeValue<V>)arg.get(0)).getRangeValue().getUpperBound()
				/((HasRangeValue<V>)arg.get(1)).getRangeValue().getLowerBound();
		Double result4 = ((HasRangeValue<V>)arg.get(0)).getRangeValue().getUpperBound()
				/((HasRangeValue<V>)arg.get(1)).getRangeValue().getUpperBound();
		return new RangeValue<V>(getMinimum(result1, result2, result3, result4)
				, getMaximum(result1, result2, result3, result4));
	}
	
	@Override
	/**
	 * matrix rdivision, when the arguments are both scalars, 
	 * it works the same as element-by-element rdivision.
	 */
	public RangeValue<V> caseMrdivide(Builtin builtin, Args<V> arg) {
		if (arg.size()!=2) return null;
		if (((HasShape<V>)arg.get(0)).getShape().isScalar() 
				&& ((HasShape<V>)arg.get(1)).getShape().isScalar()) {
			return caseRdivide(builtin, arg);
		}
		return null;
	}
	
	@Override
	/**
	 * element-by-element power.
	 */
	public RangeValue<V> casePower(Builtin builtin, Args<V> arg) {
		if (arg.size()!=2) return null;
		if (((HasRangeValue<V>)arg.get(0)).getRangeValue()==null 
				|| ((HasRangeValue<V>)arg.get(1)).getRangeValue()==null) 
			return null;
		if (arg.hasDependency()) {
			if (arg.getDependentVars().contains(arg.get(0).getSymbolic()) 
					|| arg.getDependentVars().contains(arg.get(1).getSymbolic())) {
				RangeValue<V> topRange = new RangeValue<V>();
				topRange.flagLowerBoundIsTop();
				topRange.flagUpperBoundIsTop();
				return topRange;					
			}
		}
		if (((HasRangeValue<V>)arg.get(0)).getRangeValue().hasTop() 
				|| ((HasRangeValue<V>)arg.get(1)).getRangeValue().hasTop()) {
			RangeValue<V> topRange = new RangeValue<V>().mergeTop(
					((HasRangeValue<V>)arg.get(0)).getRangeValue()
					, ((HasRangeValue<V>)arg.get(1)).getRangeValue());
			return topRange;
		}
		if (((HasRangeValue<V>)arg.get(0)).getRangeValue().isConstant() 
				&& ((HasRangeValue<V>)arg.get(1)).getRangeValue().isConstant()) {
			Double result = Math.pow(((HasRangeValue<V>)arg.get(0)).getRangeValue().getLowerBound()
					, ((HasRangeValue<V>)arg.get(1)).getRangeValue().getLowerBound());
			return new RangeValue<V>(result);
		}
		return null; // TODO extend to interval value power analysis
	}
	
	@Override
	/**
	 * array power, when the arguments are both scalars, 
	 * it works the same as element-by-element power.
	 */
	public RangeValue<V> caseMpower(Builtin builtin, Args<V> arg) {
		if (arg.size()!=2) return null;
		if (((HasShape<V>)arg.get(0)).getShape().isScalar() 
				&& ((HasShape<V>)arg.get(1)).getShape().isScalar()) {
			return casePower(builtin, arg);
		}
		return null;
	}
	
	@Override
	/**
	 * exponential
	 */
	public RangeValue<V> caseExp(Builtin builtin, Args<V> arg) {
		if (arg.size()!=1) return null;
		if (((HasRangeValue<V>)arg.get(0)).getRangeValue()==null) 
			return null;
		if (arg.hasDependency()) {
			if (arg.getDependentVars().contains(arg.get(0).getSymbolic())) {
				RangeValue<V> topRange = new RangeValue<V>();
				topRange.flagLowerBoundIsTop();
				topRange.flagUpperBoundIsTop();
				return topRange;
			}
		}
		if (((HasRangeValue<V>)arg.get(0)).getRangeValue().hasTop()) {
			RangeValue<V> topRange = new RangeValue<V>();
			topRange.flagLowerBoundIsTop();
			topRange.flagUpperBoundIsTop();
			return topRange;
		}
		Double result = Math.exp(((HasRangeValue<V>)arg.get(0)).getRangeValue().getLowerBound());
		return new RangeValue<V>(result);
	}
	
	
	@Override
	/**
	 * colon case:
	 * In MATLAB, if the lower bound bigger than the upper bound, 
	 * MATLAB will return a 1-by-0 empty matrix.
	 */
	public RangeValue<V> caseColon(Builtin builtin, Args<V> arg) {
		if (arg.size()!=2) return null;
		if (((HasRangeValue<V>)arg.get(0)).getRangeValue()==null 
				|| ((HasRangeValue<V>)arg.get(1)).getRangeValue()==null) 
			return null;
		if (arg.hasDependency()) {
			if (arg.getDependentVars().contains(arg.get(0).getSymbolic()) 
					|| arg.getDependentVars().contains(arg.get(1).getSymbolic())) {
				RangeValue<V> topRange = new RangeValue<V>();
				topRange.flagLowerBoundIsTop();
				topRange.flagUpperBoundIsTop();
				return topRange;					
			}
		}
		if (((HasRangeValue<V>)arg.get(0)).getRangeValue().hasTop() 
				|| ((HasRangeValue<V>)arg.get(1)).getRangeValue().hasTop()) {
			RangeValue<V> topRange = new RangeValue<V>().mergeTop(
					((HasRangeValue<V>)arg.get(0)).getRangeValue()
					, ((HasRangeValue<V>)arg.get(1)).getRangeValue());
			return topRange;
		}
		Double lowerBound = ((HasRangeValue<V>)arg.get(0)).getRangeValue().getLowerBound();
		Double upperBound = ((HasRangeValue<V>)arg.get(1)).getRangeValue().getUpperBound();
		if (lowerBound!=null && upperBound!=null) {
			if (lowerBound > upperBound) return null; // TODO return a 1-by-0 matrix
			return new RangeValue<V>(lowerBound, upperBound);
		}
		return new RangeValue<V>(lowerBound, upperBound);
	}
	
	/**
	 * for loop variable.
	 * In MATLAB, the default increment is 1, and without increment input, 
	 * if the lower bound bigger than the upper bound, MATLAB will return a 
	 * 1-by-0 empty matrix. but if the increment is negative, the lower bound 
	 * should be bigger than the upper bound.
	 */
	public RangeValue<V> forRange(V lower, V upper, V inc) {
		if (inc==null) {
			if (((DoubleConstant)((HasConstant)lower).getConstant())==null 
					|| ((DoubleConstant)((HasConstant)upper).getConstant())==null)
				return null;
			Double lowerBound = ((DoubleConstant)((HasConstant)lower).getConstant()).getValue();
			Double upperBound = ((DoubleConstant)((HasConstant)upper).getConstant()).getValue();
			return new RangeValue<V>(lowerBound, upperBound);			
		}
		else {
			// System.err.println(lower.toString()+upper+inc); TODO
			return null;
		}
	}
	
	//helper method
	public Double getMinimum(Double d1, Double d2, Double d3, Double d4) {
		ArrayList<Double> arr = new ArrayList<Double>(4);
		arr.add(d1);
		arr.add(d2);
		arr.add(d3);
		arr.add(d4);
		Double tmp;
		for (int i=0; i<4; i++) {
			for (int j=i; j<3; j++) {
				if (arr.get(j)>arr.get(j+1)) {
					tmp = arr.get(j+1);
					arr.set(j+1, arr.get(j));
					arr.set(j, tmp);
				}
			}
		}
		return arr.get(0);
	}
	
	public Double getMaximum(Double d1, Double d2, Double d3, Double d4) {
		ArrayList<Double> arr = new ArrayList<Double>(4);
		arr.add(d1);
		arr.add(d2);
		arr.add(d3);
		arr.add(d4);
		Double tmp;
		for (int i=0; i<4; i++) {
			for (int j=i; j<3; j++) {
				if (arr.get(j)>arr.get(j+1)) {
					tmp = arr.get(j+1);
					arr.set(j+1, arr.get(j));
					arr.set(j, tmp);
				}
			}
		}
		return arr.get(3);
	}
}
