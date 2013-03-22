package natlab.tame.valueanalysis.components.rangeValue;

import java.util.ArrayList;

import natlab.tame.valueanalysis.value.*;
import natlab.toolkits.analysis.Mergable;

public class RangeValue<V extends Value<V>> implements Mergable<RangeValue<V>> {
	
	static boolean Debug = false;
	private ArrayList<Double> rangeValue = new ArrayList<Double>(2);
	private boolean isTop = false; // TODO should be two tops for each of upper bound and lower bound.
	static RangeValueFactory factory = new RangeValueFactory();
	
	public RangeValue() {
		
	}
	
	public RangeValue(Double value) {
		this.setLowerBound(value);
		this.setUpperBound(value);
	}
	
	public RangeValue(Double lowerValue, Double upperValue) {
		this.setLowerBound(lowerValue);
		this.setUpperBound(upperValue);
	}
	
	public RangeValue(RangeValue<V> rangeValue) {
		this.setLowerBound(rangeValue.getLowerBound());
		this.setUpperBound(rangeValue.getUpperBound());
	}
	
	public void setLowerBound(Double value) {
		this.rangeValue.add(0, value);
	}
	
	public void setUpperBound(Double value) {
		this.rangeValue.add(1, value);
	}
	
	public boolean hasLowerBound() {
		if (this.rangeValue.size()!=2) return false;
		if (this.rangeValue.get(0)!=null) return true;
		else return false;
	}
	
	public Double getLowerBound() {
		if (this.hasLowerBound()) return this.rangeValue.get(0);
		else {
			System.err.println("This value doesn't have lower bound.");
			return null;
		}
	}
	
	public boolean hasUpperBound() {
		if (this.rangeValue.get(1)!=null) return true;
		else return false;
	}
	
	public Double getUpperBound() {
		if (this.hasUpperBound()) return this.rangeValue.get(1);
		else {
			System.err.println("This value doesn't have upper bound.");
			return null;
		}
	}
	
	public void flagIsTop() {
		isTop = true;
	}
	
	public boolean getIsTop() {
		return isTop;
	}
	
	public boolean isConstant() {
		if (hasLowerBound() && hasUpperBound()) {
			boolean result = getLowerBound().equals(getUpperBound());
			return result;
		}
		return false;
	}
	
	@Override
	public RangeValue<V> merge(RangeValue<V> o) {
		if (Debug) System.out.println("inside range value merge!");
		if (this.equals(o)) return this;
		else if (o==null) return null;
		else if (this.isTop==true || o.isTop==true) {
			RangeValue<V> topRange = new RangeValue<V>();
			topRange.flagIsTop();
			return topRange;
		}
		Double newLowerBound;
		Double newUpperBound;
		int lowerResult = this.getLowerBound().compareTo(o.getLowerBound());
		int upperResult = this.getUpperBound().compareTo(o.getUpperBound());
		if (lowerResult>=0) {
			newLowerBound = new Double(o.getLowerBound());
		}
		else {
			newLowerBound = new Double(this.getLowerBound());
		}
		if (upperResult>=0) {
			newUpperBound = new Double(this.getUpperBound());
		}
		else {
			newUpperBound = new Double(o.getUpperBound());
		}
		return factory.newRangeValueFromBounds(newLowerBound, newUpperBound);
	}
	
	@Override
	public boolean equals(Object obj) {
		/**
		 * When both lower and upper bound are equal,
		 * return true.
		 * TODO handle more situations!
		 */
		if (obj == null) return false;
		if (obj instanceof RangeValue) {
			if (Debug) System.out.println("inside check whether range value equals!");
			RangeValue o = (RangeValue)obj;
			if (isTop==true && o.isTop==true) return true;
			else if (isTop==true || o.isTop==true) return false;
			if (this.getLowerBound().equals(o.getLowerBound())
					&&this.getUpperBound().equals(o.getUpperBound())) {
				return true;
			}
			else {
				return false;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		if (this.isTop) return "<-inf,+inf>";
		return "<"+this.rangeValue.get(0)+","+this.rangeValue.get(1)+">";
	}
}
