package natlab;

/**
 * The value of a numeric literal that may be FP (includes e.g. 1.0).
 */
public class FPNumericLiteralValue extends NumericLiteralValue {
	private final Double value;

	public FPNumericLiteralValue(String text) {
		this(text, false);
	}

	public FPNumericLiteralValue(String text, boolean isImaginary) {
		super(text, isImaginary);
		value = Double.parseDouble(stripImaginary(text, isImaginary));
	}

	@Override
	public Double getValue() {
		return value;
	}
}
