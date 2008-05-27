package natlab;

/**
 * The value of a numeric literal that is definitely not a FP number.
 */
public abstract class IntNumericLiteralValue extends NumericLiteralValue {
	private final Integer value;

	public IntNumericLiteralValue(String text, Integer value, boolean isImaginary) {
		super(text, isImaginary);
		this.value = value;
	}

	@Override
	public Integer getValue() {
		return value;
	}
}
