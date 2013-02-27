package natlab.tame.valueanalysis.components.rangeValue;

import natlab.tame.valueanalysis.value.*;

/**
 * denotes whether a value has range value information associated with it.
 * provides an access method to retrieve the shape.
 */
public interface HasRangeValue<V extends Value<V>> extends Value<V> {

	/**
	 * returns the range value associated with this value
	 */
	public RangeValue<V> getRangeValue();
}
