package natlab.tame.valueanalysis.components.rangeValue;

import natlab.tame.valueanalysis.value.*;

public class RangeValueFactory<V extends Value<V>> {
	ValueFactory<V> factory;
	
	public RangeValueFactory() {}
	
	public RangeValueFactory(ValueFactory<V> factory) {
		this.factory = factory;
	}
	
	public RangeValue<V> newRangeValueFromDouble(Double value) {
		return new RangeValue<V>(value);
	}
	
	public RangeValue<V> newRangeValueFromBounds(Double lower, Double upper) {
		return new RangeValue<V>(lower, upper);
	}
	
	public RangeValue<V> newRangeValueFromObject(RangeValue<V> value) {
		return new RangeValue<V>(value);
	}
	
	
}
