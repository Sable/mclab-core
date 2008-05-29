package natlab;

import java.math.BigDecimal;

/**
 * The value of a numeric literal that may be FP (includes e.g. 1.0).
 */
public class FPNumericLiteralValue extends NumericLiteralValue {
	private final BigDecimal value;

	public FPNumericLiteralValue(String text) {
		this(text, false);
	}

	public FPNumericLiteralValue(String text, boolean isImaginary) {
		super(text, isImaginary);
		value = new BigDecimal(stripImaginary(text, isImaginary));
	}

	@Override
	public BigDecimal getValue() {
		return value;
	}
}
