package natlab.tame.valueanalysis.components.rangeValue;

import natlab.tame.valueanalysis.value.Value;
import natlab.tame.valueanalysis.value.ValueFactory;

public class RangeValueFactory {

	
	public RangeValueFactory() {

	}
	
	public RangeValue newRangeValueFromDouble(Double value) {
		return new RangeValue(new DomainValue(value), new DomainValue(value));
	}
	
	public RangeValue newRangeValueFromBounds(DomainValue lower, DomainValue upper) {
		return new RangeValue(lower, upper);
	}
	
	public RangeValue newRangeValueFromObject(RangeValue value) {
		return new RangeValue(value);
	}	
}
