package natlab.tame.valueanalysis.components.rangeValue;

import java.util.ArrayList;

import natlab.tame.valueanalysis.value.*;
import natlab.toolkits.analysis.Mergable;

public class RangeValue<V extends Value<V>> implements Mergable<RangeValue<V>> {
	private ArrayList<Double> rangeValue = new ArrayList<Double>(2);
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
	
	@Override
	public RangeValue<V> merge(RangeValue<V> o) {
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
	
	public boolean equals(RangeValue<V> o) {
		/**
		 * When both lower and upper bound are equal,
		 * return true.
		 * TODO handle more situations!
		 */
		if (this.getLowerBound().equals(o.getLowerBound())
				&&this.getUpperBound().equals(o.getUpperBound())) {
			return true;
		}
		else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return "<"+this.rangeValue.get(0)+","+this.rangeValue.get(1)+">";
	}
}
