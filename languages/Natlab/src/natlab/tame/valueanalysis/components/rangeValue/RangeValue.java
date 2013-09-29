package natlab.tame.valueanalysis.components.rangeValue;

import java.util.ArrayList;

import natlab.tame.valueanalysis.value.*;
import natlab.toolkits.analysis.Mergable;

/**
 * the representation of range value is <lower bound, upper bound>;
 * lower bound can be one of -inf, real numbers and real numbers with +;
 * upper bound can be one of real numbers with -, real numbers and +inf;
 */	
public class RangeValue<V extends Value<V>> implements Mergable<RangeValue<V>> {
	static boolean Debug = false;
	DomainValue lowerBound;
	DomainValue upperBound;
	// track how many times lowerBound been merged.
	private int lowerCounter = 0;
	// track how many times upperBound been merged.
	private int upperCounter= 0;
	
	public RangeValue(){}
	
	public RangeValue(DomainValue value) {
		this.lowerBound = value.cloneThisValue();
		this.upperBound = value.cloneThisValue();
	}
	
	public RangeValue(DomainValue lowerValue, DomainValue upperValue) {
		this.lowerBound = lowerValue.cloneThisValue();
		this.upperBound = upperValue.cloneThisValue();
	}
	
	public RangeValue(RangeValue<V> rangeValue) {
		this.lowerBound = rangeValue.lowerBound.cloneThisValue();
		this.upperBound = rangeValue.upperBound.cloneThisValue();
	}
	
	public boolean hasLowerBound() {
		if (this.lowerBound != null) 
			return true;
		else 
			return false;
	}
	
	public DomainValue getLowerBound() {
		if (this.hasLowerBound()) 
			return this.lowerBound;
		else {
			System.err.println("This value doesn't have a lower bound.");
			return null;
		}
	}
	
	public boolean hasUpperBound() {
		if (this.upperBound != null) 
			return true;
		else 
			return false;
	}
	
	public DomainValue getUpperBound() {
		if (this.hasUpperBound()) 
			return this.upperBound;
		else {
			System.err.println("This value doesn't have a upper bound.");
			return null;
		}
	}
	
	public boolean isRangeValuePositive() {
		if (this.hasLowerBound() 
				&& this.getLowerBound().greaterThanZero()) 
			return true;
		else 
			return false;
	}
	
	public boolean isRangeValueNegative() {
		if (this.hasUpperBound() 
				&& this.getUpperBound().lessThanZero()) 
			return true;
		else 
			return false;
	}
	
	public void pushLowerBoundtoTop() {
		this.lowerBound.negativeInf = true;
	}
	
	public void pushUpperBoundtoTop() {
		this.upperBound.positiveInf = true;
	}
	
	public boolean isLowerBoundTop() {
		return this.lowerBound.negativeInf;
	}
	
	public boolean isUpperBoundTop() {
		return this.upperBound.positiveInf;
	}
	
	public boolean hasTop() {
		if (this.isLowerBoundTop() 
				|| this.isUpperBoundTop()) 
			return true;
		else 
			return false;
	}
	
	public boolean isInBounds(int lower, int upper) {
		DomainValue low = new DomainValue();
		low.realNum = lower;
		DomainValue up = new DomainValue();
		up.realNum = upper;
		if (this.lowerBound.isGreaterThanEq(low) 
				&& this.upperBound.isLessThanEq(up)) 
			return true;
		else 
			return false;
	}
	
	/*public RangeValue<V> mergeTop(RangeValue<V> a, RangeValue<V> b) {
		RangeValue<V> mergedRange = new RangeValue<V>();
		if (a.isLowerBoundTop() || b.isLowerBoundTop()) {
			mergedRange.pushLowerBoundtoTop();
		}
		else {
			if (a.getLowerBound().isLessThanEq(b.getLowerBound())) 
				mergedRange.lowerBound = a.getLowerBound().cloneThisValue();
			else mergedRange.lowerBound = b.getLowerBound().cloneThisValue();
		}
		if (a.isUpperBoundTop() || b.isUpperBoundTop()) {
			mergedRange.pushUpperBoundtoTop();
		}
		else {
			if (a.getUpperBound().isGreaterThanEq(b.getUpperBound())) 
				mergedRange.upperBound = a.getUpperBound().cloneThisValue();
			else mergedRange.upperBound = b.getUpperBound().cloneThisValue();
		}
		return mergedRange;
	}*/
	
	/*public boolean isTopEquals(RangeValue<V> a, RangeValue<V> b) {
		if (a.isLowerBoundTop() && b.isLowerBoundTop()) {
			if (a.isUpperBoundTop() && b.isUpperBoundTop()) 
				return true;
			else if (a.getUpperBound()==b.getUpperBound())
				return true;
			else
				return false;
		}
		else if (a.isUpperBoundTop() && b.isUpperBoundTop()) {
			if (a.getLowerBound()==b.getLowerBound())
				return true;
			else
				return false;
		}
		return false;
	}*/
	
	/*public boolean isConstant() {
		if (hasLowerBound() && hasUpperBound()) {
			boolean result = getLowerBound().equals(getUpperBound());
			return result;
		}
		return false;
	}*/
	
	@Override
	/**
	 * involving a counter to track how many times each bound been 
	 * merged, if more than 5 (magic number), push the corresponding 
	 * bound to top, lower to -inf and upper to +inf.
	 */
	public RangeValue<V> merge(RangeValue<V> other) {
		if (Debug) System.out.println("inside range value merge!");
		RangeValue<V> res = new RangeValue<V>();
		// null means unknown, is the bottom of range value lattice.
		if (other == null) {
			other = new RangeValue<V>();
			other.lowerBound = this.lowerBound.cloneThisValue();
			other.upperBound = this.upperBound.cloneThisValue();
		}
		if (other.getLowerBound() == null) {
			other.lowerBound = this.lowerBound.cloneThisValue();
		}
		if (other.getUpperBound() == null) {
			other.upperBound = this.upperBound.cloneThisValue();
		}
		// separately merge lower and upper bound.
		if (!this.lowerBound.equals(other.getLowerBound())) {
			if (this.lowerCounter > 5 || other.lowerCounter > 5) {
				res.pushLowerBoundtoTop();
				this.pushLowerBoundtoTop();
			}
			else if (this.lowerBound.isLessThanEq(other.getUpperBound())) {
				res.lowerBound = this.lowerBound.cloneThisValue();
				this.lowerCounter++;
				other.lowerCounter++;	
			}
			else {
				res.lowerBound = other.getLowerBound().cloneThisValue();
				this.lowerCounter++;
				other.lowerCounter++;	
			}			
		}
		else {
			res.lowerBound = this.lowerBound.cloneThisValue();
		}
		if (!this.upperBound.equals(other.getUpperBound())) {
			if (this.upperCounter > 5 || other.upperCounter > 5) {
				res.pushUpperBoundtoTop();
				this.pushUpperBoundtoTop();
			}
			else if (this.upperBound.isLessThanEq(other.getUpperBound())) {
				res.upperBound = other.getUpperBound().cloneThisValue();
				this.upperCounter++;
				other.upperCounter++;
			}
			else {
				res.upperBound = this.upperBound.cloneThisValue();
				this.upperCounter++;
				other.upperCounter++;
			}	
		}
		else {
			res.upperBound = this.upperBound.cloneThisValue();
		}
		return res;
	}
	
	@Override
	/**
	 * When both lower and upper bound equals, return true.
	 * TODO handle more situations!
	 */
	public boolean equals(Object obj) {
		if (obj == null) return false;
		else if (obj instanceof RangeValue) {
			if (Debug) System.out.println("inside check whether range value equals!");
			RangeValue<V> other = (RangeValue<V>)obj;
			if (this.getLowerBound().equals(other.getLowerBound()) 
					&& this.getUpperBound().equals(other.getUpperBound())) {
				return true;
			}
			else return false;
		}
		else return false;
	}
	
	@Override
	public String toString() {
		return "<" 
	+ (hasLowerBound()? getLowerBound():"?") 
	+ ", " 
	+ (hasUpperBound()? getUpperBound():"?") 
	+ ">";
	}
}
