package natlab.tame.valueanalysis.components.shape;
/**
 * denotes whether a value has shape information associated with it.
 * provides an accessor method to retrieve the shape.
 */

import natlab.tame.valueanalysis.value.Value;

public interface HasShape{
	
	/**
	 * returns the shape associated with this value
	 */
	public Shape getShape();
	
}
