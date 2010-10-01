package natlab.toolkits.analysis.isscalar;

public class IsScalarTypePool {
	private static Bottom bottom = new Bottom();
	private static Scalar scalar = new Scalar();
	private static Top top = new Top();
	
	public static Bottom bottom() {
		return bottom;
	}
	
	public static Scalar scalar() {
		return scalar;
	}
	
	public static Top top() {
		return top;
	}
}
