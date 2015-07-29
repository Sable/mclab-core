package natlab.tame.valueanalysis.components.rangeValue;

import natlab.tame.valueanalysis.value.Value;

/**
 * denotes whether a value has range value information associated with it.
 * provides an access method to retrieve the shape.
 */
public interface HasRangeValue  {

	/**
	 * returns the range value associated with this value
	 */
	public RangeValue getRangeValue();
}
