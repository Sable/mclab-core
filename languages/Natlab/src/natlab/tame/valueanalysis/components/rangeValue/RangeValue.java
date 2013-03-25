package natlab.tame.valueanalysis.components.rangeValue;

import java.util.ArrayList;

import natlab.tame.valueanalysis.value.*;
import natlab.toolkits.analysis.Mergable;

public class RangeValue<V extends Value<V>> implements Mergable<RangeValue<V>> {
	
	static boolean Debug = false;
	private ArrayList<Double> rangeValue = new ArrayList<Double>(2);
	// TODO should be two tops for each of upper bound and lower bound.
	private boolean lowerBoundIsTop = false; 
	private boolean upperBoundIsTop = false;
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
		if (upperBoundIsTop) {
			if (rangeValue!=null && (!rangeValue.isEmpty())) {
				if (rangeValue.get(0)!=null) return true;
			}
			return false;
		}
		else if (rangeValue.size()==2) {
			if (rangeValue.get(0)!=null) return true;
		}
		return false;
	}
	
	public Double getLowerBound() {
		if (this.hasLowerBound()) return this.rangeValue.get(0);
		else {
			System.err.println("This value doesn't have lower bound.");
			return null;
		}
	}
	
	public boolean hasUpperBound() {
		if (lowerBoundIsTop) {
			if (rangeValue!=null && (!rangeValue.isEmpty())) {
				if (rangeValue.get(0)!=null) return true;
			}
			return false;
		}
		else if (rangeValue.size()==2) {
			if (rangeValue.get(1)!=null) return true;
		}
		return false;
	}
	
	public Double getUpperBound() {
		if (this.hasUpperBound()&&this.hasLowerBound()) return this.rangeValue.get(1);
		else if (this.hasUpperBound()) return this.rangeValue.get(0);
		else {
			System.err.println("This value doesn't have upper bound.");
			return null;
		}
	}
	
	public boolean isRangeValuePositive() {
		if (hasLowerBound()) {
			if (getLowerBound()>0) return true;
		}
		return false;
	}
	
	public boolean isRangeValueNegative() {
		if (hasUpperBound()) {
			if (getUpperBound()<0) return true;
		}
		return false;
	}
	
	public void flagLowerBoundIsTop() {
		lowerBoundIsTop = true;
	}
	
	public void flagUpperBoundIsTop() {
		upperBoundIsTop = true;
	}
	
	public boolean getLowerBoundIsTop() {
		return lowerBoundIsTop;
	}
	
	public boolean getUpperBoundIsTop() {
		return upperBoundIsTop;
	}
	
	public boolean hasTop() {
		if (lowerBoundIsTop || upperBoundIsTop) return true;
		return false;
	}
	
	public RangeValue<V> mergeTop(RangeValue<V> a, RangeValue<V> b) {
		RangeValue<V> mergedRange = new RangeValue<V>();
		if (a.getLowerBoundIsTop() || b.getLowerBoundIsTop()) {
			mergedRange.flagLowerBoundIsTop();
		}
		else {
			if (a.getLowerBound()<=b.getLowerBound()) 
				mergedRange.setLowerBound(a.getLowerBound());
			else mergedRange.setLowerBound(b.getLowerBound());
		}
		if (a.getUpperBoundIsTop() || b.getUpperBoundIsTop()) {
			mergedRange.flagUpperBoundIsTop();
		}
		else {
			if (a.getUpperBound()>=b.getUpperBound()) 
				mergedRange.setUpperBound(a.getUpperBound());
			else mergedRange.setUpperBound(b.getUpperBound());
		}
		return mergedRange;
	}
	
	public boolean isTopEquals(RangeValue<V> a, RangeValue<V> b) {
		if (a.getLowerBoundIsTop() && b.getLowerBoundIsTop()) {
			if (a.getUpperBoundIsTop() && b.getUpperBoundIsTop()) 
				return true;
			else if (a.getUpperBound()==b.getUpperBound())
				return true;
			else
				return false;
		}
		else if (a.getUpperBoundIsTop() && b.getUpperBoundIsTop()) {
			if (a.getLowerBound()==b.getLowerBound())
				return true;
			else
				return false;
		}
		return false;
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
		else if (hasTop() || o.hasTop()) {
			RangeValue<V> mergedRange = mergeTop(this, o);
			return mergedRange;
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
			RangeValue<V> o = (RangeValue<V>)obj;
			if (hasTop() && o.hasTop()) return isTopEquals(this, o);
			else if (hasTop()&&(!o.hasTop()) || (!hasTop())&&o.hasTop()) return false;
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
		return "<" 
	+ (hasLowerBound()? getLowerBound():"-inf") 
	+ "," 
	+ (hasUpperBound()? getUpperBound():"+inf") 
	+ ">";
	}
}
