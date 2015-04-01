package natlab.tame.valueanalysis.components.isComplex;

/**
 * indicates whether a value has complex information associated with it.
 */

import natlab.tame.valueanalysis.value.Value;
public interface HasisComplexInfo<V extends Value<V>> extends Value<V> {
	
	/**
	 * returns the isComplexinfo associated with this value
	 */
	public isComplexInfo<V> getisComplexInfo();	
	
}