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
		else if (((HasRangeValue<V>)arg.get(0)).getRangeValue().getIsTop() 
				|| ((HasRangeValue<V>)arg.get(1)).getRangeValue().getIsTop()) {
			RangeValue<V> topRange = new RangeValue<V>();
			topRange.flagIsTop();
			return topRange;				
		}
		else if (arg.hasDependency()) {
			if (arg.getDependentVars().contains(arg.get(0).getSymbolic()) 
					|| arg.getDependentVars().contains(arg.get(1).getSymbolic())) {
				RangeValue<V> topRange = new RangeValue<V>();
				topRange.flagIsTop();
				return topRange;					
			}
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
				|| ((HasRangeValue<V>)arg.get(1)).getRangeValue()==null) {
			RangeValue<V> topRange = new RangeValue<V>();
			topRange.flagIsTop();
			return topRange;
		}
		else if (((HasRangeValue<V>)arg.get(0)).getRangeValue().getIsTop() 
				|| ((HasRangeValue<V>)arg.get(1)).getRangeValue().getIsTop()) {
			RangeValue<V> topRange = new RangeValue<V>();
			topRange.flagIsTop();
			return topRange;				
		}
		else if (arg.hasDependency()) {
			if (arg.getDependentVars().contains(arg.get(0).getSymbolic()) 
					|| arg.getDependentVars().contains(arg.get(1).getSymbolic())) {
				RangeValue<V> topRange = new RangeValue<V>();
				topRange.flagIsTop();
				return topRange;					
			}
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
		else if (((HasRangeValue<V>)arg.get(0)).getRangeValue().getIsTop() 
				|| ((HasRangeValue<V>)arg.get(1)).getRangeValue().getIsTop()) {
			RangeValue<V> topRange = new RangeValue<V>();
			topRange.flagIsTop();
			return topRange;				
		}
		else if (arg.hasDependency()) {
			if (arg.getDependentVars().contains(arg.get(0).getSymbolic()) 
					|| arg.getDependentVars().contains(arg.get(1).getSymbolic())) {
				RangeValue<V> topRange = new RangeValue<V>();
				topRange.flagIsTop();
				return topRange;
			}
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
	 * matrix multiply
	 */
	public RangeValue<V> caseMtimes(Builtin builtin, Args<V> arg) {
		if (arg.size()!=2) return null;
		if (((HasShape<V>)arg.get(0)).getShape().isScalar() 
				&& ((HasShape<V>)arg.get(1)).getShape().isScalar()) {
			if (((HasRangeValue<V>)arg.get(0)).getRangeValue()==null 
					|| ((HasRangeValue<V>)arg.get(1)).getRangeValue()==null) 
				return null;
			else if (((HasRangeValue<V>)arg.get(0)).getRangeValue().getIsTop() 
					|| ((HasRangeValue<V>)arg.get(1)).getRangeValue().getIsTop()) {
				RangeValue<V> topRange = new RangeValue<V>();
				topRange.flagIsTop();
				return topRange;				
			}
			else if (arg.hasDependency()) {
				if (arg.getDependentVars().contains(arg.get(0).getSymbolic()) 
						|| arg.getDependentVars().contains(arg.get(1).getSymbolic())) {
					RangeValue<V> topRange = new RangeValue<V>();
					topRange.flagIsTop();
					return topRange;					
				}
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
		else return null;
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
		else if (((HasRangeValue<V>)arg.get(0)).getRangeValue().getIsTop() 
				|| ((HasRangeValue<V>)arg.get(1)).getRangeValue().getIsTop()) {
			RangeValue<V> topRange = new RangeValue<V>();
			topRange.flagIsTop();
			return topRange;				
		}
		else if (arg.hasDependency()) {
			if (arg.getDependentVars().contains(arg.get(0).getSymbolic()) 
					|| arg.getDependentVars().contains(arg.get(1).getSymbolic())) {
				RangeValue<V> topRange = new RangeValue<V>();
				topRange.flagIsTop();
				return topRange;					
			}
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
	 * matrix rdivision, when the arguments are scalar, 
	 * it works the same as element-by-element rdivision.
	 */
	public RangeValue<V> caseMrdivide(Builtin builtin, Args<V> arg) {
		if (arg.size()!=2) return null;
		if (((HasShape<V>)arg.get(0)).getShape().isScalar() 
				&& ((HasShape<V>)arg.get(1)).getShape().isScalar()) {
			if (((HasRangeValue<V>)arg.get(0)).getRangeValue()==null 
					|| ((HasRangeValue<V>)arg.get(1)).getRangeValue()==null) 
				return null;
			else if (((HasRangeValue<V>)arg.get(0)).getRangeValue().getIsTop() 
					|| ((HasRangeValue<V>)arg.get(1)).getRangeValue().getIsTop()) {
				RangeValue<V> topRange = new RangeValue<V>();
				topRange.flagIsTop();
				return topRange;				
			}
			else if (arg.hasDependency()) {
				if (arg.getDependentVars().contains(arg.get(0).getSymbolic()) 
						|| arg.getDependentVars().contains(arg.get(1).getSymbolic())) {
					RangeValue<V> topRange = new RangeValue<V>();
					topRange.flagIsTop();
					return topRange;					
				}
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
		else return null;
	}
	
	@Override
	/**
	 * array power, which is element-by-element power.
	 */
	public RangeValue<V> casePower(Builtin builtin, Args<V> arg) {
		if (arg.size()!=2) return null;
		if (((HasRangeValue<V>)arg.get(0)).getRangeValue()==null 
				|| ((HasRangeValue<V>)arg.get(1)).getRangeValue()==null) 
			return null;
		else if (((HasRangeValue<V>)arg.get(0)).getRangeValue().getIsTop() 
				|| ((HasRangeValue<V>)arg.get(1)).getRangeValue().getIsTop()) {
			RangeValue<V> topRange = new RangeValue<V>();
			topRange.flagIsTop();
			return topRange;				
		}
		else if (arg.hasDependency()) {
			if (arg.getDependentVars().contains(arg.get(0).getSymbolic()) 
					|| arg.getDependentVars().contains(arg.get(1).getSymbolic())) {
				RangeValue<V> topRange = new RangeValue<V>();
				topRange.flagIsTop();
				return topRange;					
			}
		}
		else if (((HasRangeValue<V>)arg.get(0)).getRangeValue().isConstant() 
				&& ((HasRangeValue<V>)arg.get(1)).getRangeValue().isConstant()) {
			Double result = Math.pow(((HasRangeValue<V>)arg.get(0)).getRangeValue().getLowerBound()
					, ((HasRangeValue<V>)arg.get(1)).getRangeValue().getLowerBound());
			return new RangeValue<V>(result);
		}
		return null; // TODO extend to interval value power analysis
	}
	
	@Override
	/**
	 * exponential
	 */
	public RangeValue<V> caseExp(Builtin builtin, Args<V> arg) {
		if (arg.size()!=1) return null;
		if (((HasRangeValue<V>)arg.get(0)).getRangeValue()==null) 
			return null;
		else if (((HasRangeValue<V>)arg.get(0)).getRangeValue().getIsTop()) {
			RangeValue<V> topRange = new RangeValue<V>();
			topRange.flagIsTop();
			return topRange;				
		}
		else if (arg.hasDependency()) {
			if (arg.getDependentVars().contains(arg.get(0).getSymbolic())) {
				RangeValue<V> topRange = new RangeValue<V>();
				topRange.flagIsTop();
				return topRange;
			}
		}
		Double result1 = Math.exp(((HasRangeValue<V>)arg.get(0)).getRangeValue().getLowerBound());
		Double result2 = Math.exp(((HasRangeValue<V>)arg.get(0)).getRangeValue().getUpperBound());
		return new RangeValue<V>(result1, result2);
	}
	
	
	@Override
	/**
	 * colon case:
	 * In MATLAB, if the first argument bigger than the second argument in colon built-in,
	 * MATLAB will return a 1-by-0 empty matrix.
	 */
	public RangeValue<V> caseColon(Builtin builtin, Args<V> arg) {
		if (arg.size()!=2 || arg.size()!=3) return null;
		else if (arg.size()==2) {
			if (((HasRangeValue<V>)arg.get(0)).getRangeValue()==null 
					|| ((HasRangeValue<V>)arg.get(1)).getRangeValue()==null) {
				return null;
			}
			else if (((HasRangeValue<V>)arg.get(0)).getRangeValue().getIsTop() 
					|| ((HasRangeValue<V>)arg.get(1)).getRangeValue().getIsTop()) {
				RangeValue<V> topRange = new RangeValue<V>();
				topRange.flagIsTop();
				return topRange;				
			}
			else if (arg.hasDependency()) {
				if (arg.getDependentVars().contains(arg.get(0).getSymbolic()) 
						|| arg.getDependentVars().contains(arg.get(1).getSymbolic())) {
					RangeValue<V> topRange = new RangeValue<V>();
					topRange.flagIsTop();
					return topRange;					
				}
			}
			Double lowerBound = ((HasRangeValue<V>)arg.get(0)).getRangeValue().getLowerBound();
			Double upperBound = ((HasRangeValue<V>)arg.get(1)).getRangeValue().getUpperBound();
			if (lowerBound > upperBound) return null;
			else return new RangeValue<V>(lowerBound, upperBound);
		}
		else {
			//TODO with increment
			return null;
		}
	}
	
	/**
	 * for loop variable.
	 * @param lower
	 * @param upper
	 * @param inc
	 * @return
	 */
	public RangeValue<V> forRange(V lower, V upper, V inc) {
		if (((DoubleConstant)((HasConstant)lower).getConstant())==null 
				|| ((DoubleConstant)((HasConstant)upper).getConstant())==null)
			return null;
		Double lowerBound = ((DoubleConstant)((HasConstant)lower).getConstant()).getValue();
		Double upperBound = ((DoubleConstant)((HasConstant)upper).getConstant()).getValue();
		return new RangeValue<V>(lowerBound, upperBound);
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
