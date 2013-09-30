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
		if (this.lowerBound != null)
			this.lowerBound.negativeInf = true;
		else {
			System.out.println("push lower to top!");
			this.lowerBound = new DomainValue();
			this.lowerBound.negativeInf = true;
		}
	}
	
	public void pushUpperBoundtoTop() {
		if (this.upperBound != null) 
			this.upperBound.positiveInf = true;
		else {
			System.out.println("push upper to top!");
			this.upperBound = new DomainValue();
			this.upperBound.positiveInf = true;
		}
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
		if (this.lowerBound.isLessThanEq(other.lowerBound)) {
			res.lowerBound = this.lowerBound.cloneThisValue();
		}
		else {
			res.lowerBound = other.lowerBound.cloneThisValue();
		}
		if (!res.lowerBound.equals(this.lowerBound)) {
			this.lowerCounter++;
		}
		// separately merge lower and upper bound.
		if (this.upperBound.isGreaterThanEq(other.upperBound)) {
			res.upperBound = this.upperBound;
		}
		else {
			res.upperBound = other.upperBound;
		}
		if (!res.upperBound.equals(this.upperBound)) {
			this.upperCounter++;
		}
		if (Debug) System.err.println("merging "+this.toString()+" with "+other+" -> "+res);
		if (Debug) System.err.println("lower bound merging counter: "+this.lowerCounter);
		if (Debug) System.err.println("upper bound merging counter: "+this.upperCounter);
		if (this.lowerCounter > 5) {
			res.pushLowerBoundtoTop();
			other.pushLowerBoundtoTop();
			// this.lowerCounter = 0;
		}
		if (this.upperCounter > 5) {
			res.pushUpperBoundtoTop();
			other.pushUpperBoundtoTop();
			// this.upperCounter = 0;
		}
		if (Debug) System.err.println("new result: "+res);
		res.lowerCounter = this.lowerCounter;
		res.upperCounter = this.upperCounter;
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
