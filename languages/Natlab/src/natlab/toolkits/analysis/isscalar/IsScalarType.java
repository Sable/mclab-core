package natlab.toolkits.analysis.isscalar;

public abstract class IsScalarType {
	public boolean isBottom() {
		return this instanceof Bottom;
	}
	
	public boolean isScalar() {
		return this instanceof Scalar;
	}
	
	public boolean isNonScalar() {
		return this instanceof NonScalar;
	}
	
	public boolean isTop() {
		return this instanceof Top;
	}
}
