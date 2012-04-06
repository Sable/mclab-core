package natlab.tame.valueanalysis.components.complex;

/**
 * indicates whether a value has complex information associated with it.
 */
public interface HasComplexInfo {

	/**
	 * return the complex info associated with this value
	 */
	public ComplexInfo getComplexInfo();
}
