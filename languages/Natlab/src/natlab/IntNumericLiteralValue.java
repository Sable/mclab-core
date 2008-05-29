package natlab;

import java.math.BigInteger;

/**
 * The value of a numeric literal that is definitely not a FP number.
 */
public abstract class IntNumericLiteralValue extends NumericLiteralValue {
	private final BigInteger value;

	public IntNumericLiteralValue(String text, BigInteger value, boolean isImaginary) {
		super(text, isImaginary);
		this.value = value;
	}

	@Override
	public BigInteger getValue() {
		return value;
	}
}
