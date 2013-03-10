package natlab.tame.valueanalysis.components.rangeValue;

import java.util.ArrayList;

import natlab.tame.builtin.Builtin;
import natlab.tame.builtin.BuiltinVisitor;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Value;
import natlab.tame.valueanalysis.components.constant.*;

public class RangeValuePropagator<V extends Value<V>> 
extends BuiltinVisitor<Args<V>, RangeValue<V>> {
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
	public RangeValue<V> caseBuiltin(Builtin builtin, Args<V> arg) {
		return null;
	}
	
	@Override
	/**
	 * unary plus
	 */
	public RangeValue<V> caseUplus(Builtin builtin, Args<V> arg) {
		if (arg.size()!=1) {
			return null;
		}
		else {
			Double lowerBound = ((HasRangeValue<V>)arg.get(0)).getRangeValue().getLowerBound();
			Double upperBound = ((HasRangeValue<V>)arg.get(0)).getRangeValue().getUpperBound();
			return new RangeValue<V>(lowerBound, upperBound);
		}
	}
	
	@Override
	/**
	 * binary plus.
	 */
	public RangeValue<V> casePlus(Builtin builtin, Args<V> arg) {
		if (arg.size()!=2) {
			return null;
		}
		else {
			Double lowerBound = ((HasRangeValue<V>)arg.get(0)).getRangeValue().getLowerBound()
					+((HasRangeValue<V>)arg.get(1)).getRangeValue().getLowerBound();
			Double upperBound = ((HasRangeValue<V>)arg.get(0)).getRangeValue().getUpperBound()
					+((HasRangeValue<V>)arg.get(1)).getRangeValue().getUpperBound();
			return new RangeValue<V>(lowerBound, upperBound);
		}
	}
	
	@Override
	/**
	 * unary minus.
	 */
	public RangeValue<V> caseUminus(Builtin builtin, Args<V> arg) {
		if (arg.size()!=1) {
			return null;
		}
		else {
			Double lowerBound = -((HasRangeValue<V>)arg.get(0)).getRangeValue().getUpperBound();
			Double upperBound = -((HasRangeValue<V>)arg.get(0)).getRangeValue().getLowerBound();
			return new RangeValue<V>(lowerBound, upperBound);
		}
	}
	
	@Override
	/**
	 * binary minus.
	 */
	public RangeValue<V> caseMinus(Builtin builtin, Args<V> arg) {
		if (arg.size()!=2) {
			return null;
		}
		else {
			Double lowerBound = ((HasRangeValue<V>)arg.get(0)).getRangeValue().getLowerBound()
					-((HasRangeValue<V>)arg.get(1)).getRangeValue().getUpperBound();
			Double upperBound = ((HasRangeValue<V>)arg.get(0)).getRangeValue().getUpperBound()
					-((HasRangeValue<V>)arg.get(1)).getRangeValue().getLowerBound();
			return new RangeValue<V>(lowerBound, upperBound);
		}
	}
	
	@Override
	/**
	 * element-by-element multiplication.
	 */
	public RangeValue<V> caseTimes(Builtin builtin, Args<V> arg) {
		if (arg.size()!=2) {
			return null;
		}
		else {
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
	}
	
	@Override
	/**
	 * element-by-element division.
	 */
	public RangeValue<V> caseRdivide(Builtin builtin, Args<V> arg) {
		if (arg.size()!=2) {
			return null;
		}
		else {
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
	}
	
	@Override
	/**
	 * colon.
	 * In MATLAB, if the first argument bigger than the second argument in colon built-in,
	 * MATLAB will return a 1-by-0 empty matrix.
	 */
	public RangeValue<V> caseColon(Builtin builtin, Args<V> arg) {
		if (arg.size()!=2) {
			return null;
		}
		else {
			Double lowerBound = ((HasRangeValue<V>)arg.get(0)).getRangeValue().getLowerBound();
			Double upperBound = ((HasRangeValue<V>)arg.get(1)).getRangeValue().getUpperBound();
			if (lowerBound>upperBound) {
				return null;
			}
			else {
				return new RangeValue<V>(lowerBound, upperBound);
			}
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
