package natlab.tame.valueanalysis.components.rangeValue;

import natlab.tame.valueanalysis.value.*;
import natlab.toolkits.analysis.Mergable;

/**
 * the representation of range value is <lower bound, upper bound>;
 * lower bound can be one of -inf, real numbers and real numbers with +;
 * upper bound can be one of real numbers with -, real numbers and +inf;
 * 
 * @author XU
 */	
public class RangeValue<V extends Value<V>> implements Mergable<RangeValue<V>> {
	static boolean Debug = false;
	private DomainValue lowerBound;
	private DomainValue upperBound;
	// track how many times lowerBound been merged.
	private int lowerCounter = 0;
	// track how many times upperBound been merged.
	private int upperCounter= 0;
	
	public RangeValue(){}
	
	public RangeValue(DomainValue value) {
		lowerBound = value.cloneThisValue();
		upperBound = value.cloneThisValue();
	}
	
	public RangeValue(DomainValue lowerValue, DomainValue upperValue) {
		if (lowerValue != null) {
			lowerBound = lowerValue.cloneThisValue();
		}
		if (upperValue != null) {
			upperBound = upperValue.cloneThisValue();
		}
	}
	
	public RangeValue(RangeValue<V> rangeValue) {
		if (rangeValue.lowerBound != null) {
			lowerBound = rangeValue.lowerBound.cloneThisValue();
		}
		if (rangeValue.upperBound != null) {
			upperBound = rangeValue.upperBound.cloneThisValue();
		}
	}
	
	public boolean hasLowerBound() {
		if (lowerBound != null) 
			return true;
		else 
			return false;
	}
	
	public DomainValue getLowerBound() {
		if (hasLowerBound()) 
			return lowerBound;
		else {
			System.err.println("This value doesn't have a lower bound.");
			return null;
		}
	}
	
	public void setLowerBound(DomainValue value) {
		lowerBound = value.cloneThisValue();
	}
	
	public boolean hasUpperBound() {
		if (upperBound != null) 
			return true;
		else 
			return false;
	}
	
	public DomainValue getUpperBound() {
		if (hasUpperBound()) 
			return upperBound;
		else {
			System.err.println("This value doesn't have a upper bound.");
			return null;
		}
	}
	
	public void setUpperBound(DomainValue value) {
		upperBound = value.cloneThisValue();
	}
	
	public boolean isRangeValuePositive() {
		if (hasLowerBound() 
				&& getLowerBound().greaterThanZero()) 
			return true;
		else 
			return false;
	}
	
	public boolean isRangeValueNegative() {
		if (hasUpperBound() 
				&& getUpperBound().lessThanZero()) 
			return true;
		else 
			return false;
	}
	
	public void pushLowerBoundtoTop() {
		if (lowerBound != null)
			lowerBound.negativeInf = true;
		else {
			System.out.println("push lower to top!");
			lowerBound = new DomainValue();
			lowerBound.negativeInf = true;
		}
	}
	
	public void pushUpperBoundtoTop() {
		if (upperBound != null) 
			upperBound.positiveInf = true;
		else {
			System.out.println("push upper to top!");
			upperBound = new DomainValue();
			upperBound.positiveInf = true;
		}
	}
	
	/**
	 * known means neither null nor -inf.
	 * @return
	 */
	public boolean isLowerBoundKnown() {
		if (lowerBound != null && !lowerBound.negativeInf) {
			return true;
		}
		else 
			return false;
	}
	
	/**
	 * known means neither null nor +inf.
	 * @return
	 */
	public boolean isUpperBoundKnown() {
		if (upperBound != null && !upperBound.positiveInf) {
			return true;
		}
		else 
			return false;
	}
	
	public boolean isBothBoundsKnown() {
		if (isLowerBoundKnown() 
				&& isUpperBoundKnown()) 
			return true;
		else 
			return false;
	}
	
	public boolean isInBounds(int lower, int upper) {
		DomainValue low = new DomainValue();
		low.realNum = lower;
		DomainValue up = new DomainValue();
		up.realNum = upper;
		if (lowerBound.isGreaterThanEq(low) 
				&& upperBound.isLessThanEq(up)) 
			return true;
		else 
			return false;
	}
	
	@Override
	/**
	 * involving a counter to track how many times each bound been 
	 * merged, if more than 5 (magic number), push the corresponding 
	 * bound to top, lower to -inf and upper to +inf.
	 */
	public RangeValue<V> merge(RangeValue<V> other) {
		if (Debug) System.out.println("inside range value merge!");
		RangeValue<V> res = new RangeValue<V>();
		// separately merge lower and upper bound.
		if (lowerBound != null) {
			if (lowerBound.isLessThanEq(other.lowerBound)) {
				res.lowerBound = lowerBound.cloneThisValue();
			}
			else {
				res.lowerBound = other.lowerBound.cloneThisValue();
			}
			if (!res.lowerBound.equals(lowerBound)) {
				lowerCounter++;
			}			
		}
		// separately merge lower and upper bound.
		if (upperBound != null) {
			if (upperBound.isGreaterThanEq(other.upperBound)) {
				res.upperBound = upperBound;
			}
			else {
				res.upperBound = other.upperBound;
			}
			if (!res.upperBound.equals(upperBound)) {
				upperCounter++;
			}			
		}
		if (Debug) System.err.println("merging "+toString()+" with "+other+" -> "+res);
		if (Debug) System.err.println("lower bound merging counter: "+lowerCounter);
		if (Debug) System.err.println("upper bound merging counter: "+upperCounter);
		if (lowerCounter > 5) {
			res.pushLowerBoundtoTop();
			other.pushLowerBoundtoTop();
			// lowerCounter = 0;
		}
		if (upperCounter > 5) {
			res.pushUpperBoundtoTop();
			other.pushUpperBoundtoTop();
			// upperCounter = 0;
		}
		if (Debug) System.err.println("new result: "+res);
		res.lowerCounter = lowerCounter;
		res.upperCounter = upperCounter;
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
			System.out.println("comparing "+this+" with "+other);
			if (getLowerBound().equals(other.getLowerBound()) 
					&& getUpperBound().equals(other.getUpperBound())) {
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
